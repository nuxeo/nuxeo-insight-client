/*
 *  (C) Copyright 2006-2021 Nuxeo (http://nuxeo.com/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  Contributors:
 *       Andrei Nechaev
 */

package org.nuxeo.ai.callers;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.AICorpus;
import org.nuxeo.ai.API;
import org.nuxeo.ai.InsightClient;
import org.nuxeo.ai.exception.ConfigurationException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.OK;
import static org.nuxeo.ai.InsightClient.MAPPER;

public class ExportCaller implements Caller<API.Export> {

    public final Logger log = LogManager.getLogger(ExportCaller.class);

    private InsightClient client;

    public ExportCaller(InsightClient client) {
        this.client = client;
    }

    @Override
    public Object call(API.Export endpoint, Map<String, Serializable> parameters, Serializable payload)
            throws IOException {
        if (client == null || !client.isConnected()) {
            throw new ConfigurationException("No active client");
        }

        String url = client.getConfiguration().getUrl();
        String projectId = client.getConfiguration().getProjectId();

        String modelId = (String) parameters.get("modelId");
        String corporaId = (String) parameters.get("corporaId");
        String exportId = (String) parameters.get("exportId");
        AICorpus corpus = (AICorpus) parameters.get("corpus");

        switch (endpoint) {
        case INIT: {
            String jsonString = MAPPER.writeValueAsString(payload);
            return client.post(endpoint.toPath(projectId, corporaId), jsonString, response -> {
                if (!response.isSuccessful()) {
                    log.error("Failed to initialize Export for project {}, payload {}, url {}, code {} and reason {}",
                            projectId, payload, url, response.code(), response.message());
                    return null;
                }
                JsonNode node = response.body() != null ? MAPPER.readTree(response.body().byteStream()) : null;
                if (node == null || !node.has("uid")) {
                    log.error("Corpora for project {} and id {} wasn't created; payload {}", projectId, corporaId,
                            payload);
                    return null;
                } else {
                    String corpusId = node.get("uid").asText();
                    log.info("Corpora {} created for project {}, payload {}", corpusId, projectId, payload);
                    return corpusId;
                }
            });
        }
        case BIND: {
            return client.post(endpoint.toPath(projectId, modelId, corporaId), "{}", (resp) -> {
                if (!resp.isSuccessful()) {
                    log.error("Failed to bind model {} with corpora {} for project {}, url {}, code {} and reason {}",
                            modelId, corporaId, projectId, url, resp.code(), resp.message());
                    return false;
                }

                return true;
            });
        }
        case ATTACH: {
            String jsonString;
            try (StringWriter writer = new StringWriter()) {
                MAPPER.writeValue(writer, corpus);
                jsonString = writer.toString();
            }

            log.info("Creating dataset document");

            JsonNode node = client.post(endpoint.toPath(projectId, corporaId), jsonString, (resp) -> {
                if (!resp.isSuccessful()) {
                    log.error(
                            "Failed to create/upload the corpus dataset to project {}, payload {}, url {}, code {} and reason {}",
                            projectId, payload, url, resp.code(), resp.message());
                    return null;
                }
                return resp.body() != null ? MAPPER.readTree(resp.body().byteStream()) : null;
            });

            if (node == null || !node.has("uid")) {
                log.error("Failed to create/upload the corpus dataset to project {}, payload {} and response {}",
                        projectId, payload, node);
                return null;
            } else {
                String corpusId = node.get("uid").toString();
                log.info("Corpus {} added to project {}, payload {}", corpusId, projectId, payload);
                return corpusId;
            }
        }
        case DONE:
            return client.post(endpoint.toPath(projectId, exportId), "{}", Response::code) == OK.getStatusCode();
        }

        return null;
    }
}
