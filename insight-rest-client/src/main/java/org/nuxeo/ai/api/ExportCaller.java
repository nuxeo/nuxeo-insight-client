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

package org.nuxeo.ai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.Common;
import org.nuxeo.ai.CorporaParameters;
import org.nuxeo.ai.client.API;
import org.nuxeo.ai.client.InsightClient;
import org.nuxeo.ai.exception.ConfigurationException;
import org.nuxeo.ai.exception.InvalidEndpointException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

import static javax.ws.rs.core.Response.Status.OK;
import static org.nuxeo.ai.client.InsightClient.MAPPER;

public class ExportCaller implements ExportResource {

    public static final String MODEL_ID_PARAM = "modelId";

    public static final String CORPORA_ID_PARAM = "corporaId";

    public static final String CORPUS_PARAM = "corpus";

    public static final String EXPORT_ID_PARAM = "exportId";

    public final Logger log = LogManager.getLogger(ExportCaller.class);

    private final InsightClient client;

    public ExportCaller(InsightClient client) {
        this.client = client;
    }

    @Override
    @SuppressWarnings("unchecked") // TODO: review type casting
    public <T> T call(API.Export endpoint, Map<String, Serializable> parameters, Serializable payload)
            throws IOException {
        if (client == null || !client.isConnected()) {
            throw new ConfigurationException("No active client");
        }

        switch (endpoint) {
        case INIT:
            return (T) handleInit(parameters, (CorporaParameters) payload);
        case BIND:
            return (T) handleBind(parameters);
        case ATTACH:
            return (T) handleAttach(parameters, payload);
        case DONE:
            return (T) handleDone(parameters);
        default:
            throw new InvalidEndpointException("No such endpoint " + endpoint.name());
        }
    }

    private String handleInit(Map<String, Serializable> parameters, CorporaParameters corporaParameters)
            throws JsonProcessingException {
        String corporaId = (String) parameters.get(CORPORA_ID_PARAM);

        String payload = MAPPER.writeValueAsString(corporaParameters);
        Objects.requireNonNull(payload, "Init Export API requires non null Corpora Parameters");

        return client.post(API.Export.INIT.toPath(client.getProjectId(), corporaId), payload, response -> {
            if (!response.isSuccessful()) {
                log.error("Failed to initialize Export for project {}, payload {}, url {}, code {} and reason {}",
                        client.getProjectId(), payload, client.getUrl(), response.code(), response.message());
                return null;
            }

            JsonNode node = response.body() != null ? MAPPER.readTree(response.body().byteStream()) : null;
            if (node == null || !node.has(Common.UID)) {
                log.error("Corpora for project {} and id {} wasn't created; payload {}", client.getProjectId(),
                        corporaId, payload);
                return null;
            } else {
                String corpusId = node.get(Common.UID).asText();
                log.info("Corpora {} created for project {}, payload {}", corpusId, client.getProjectId(), payload);
                return corpusId;
            }
        });
    }

    private Boolean handleBind(Map<String, Serializable> parameters) {
        String modelId = (String) parameters.get(MODEL_ID_PARAM);
        Objects.requireNonNull(modelId, "Bind Export API requires model ID");

        String corporaId = (String) parameters.get(CORPORA_ID_PARAM);
        Objects.requireNonNull(corporaId, "Bind Export API requires corpora ID");

        return client.post(API.Export.BIND.toPath(client.getProjectId(), modelId, corporaId), "{}", (resp) -> {
            if (!resp.isSuccessful()) {
                log.error("Failed to bind model {} with corpora {} for project {}, url {}, code {} and reason {}",
                        modelId, corporaId, client.getProjectId(), client.getUrl(), resp.code(), resp.message());
                return false;
            }

            return true;
        });
    }

    private String handleAttach(Map<String, Serializable> parameters, Serializable payload) throws IOException {
        String jsonString;
        try (StringWriter writer = new StringWriter()) {
            MAPPER.writeValue(writer, parameters.get(CORPUS_PARAM));
            jsonString = writer.toString();
        }

        log.info("Creating dataset document");
        String corporaId = (String) parameters.get(CORPORA_ID_PARAM);
        Objects.requireNonNull(corporaId, "Attach API requires corpora ID");
        JsonNode node = client.post(API.Export.ATTACH.toPath(client.getProjectId(), corporaId), jsonString, (resp) -> {
            if (!resp.isSuccessful()) {
                log.error(
                        "Failed to create/upload the corpus dataset to project {}, payload {}, url {}, code {} and reason {}",
                        client.getProjectId(), payload, client.getUrl(), resp.code(), resp.message());
                return null;
            }

            return resp.body() != null ? MAPPER.readTree(resp.body().byteStream()) : null;
        });

        if (node == null || !node.has("uid")) {
            log.error("Failed to create/upload the corpus dataset to project {}, payload {} and response {}",
                    client.getProjectId(), payload, node);
            return null;
        } else {
            String corpusId = node.get("uid").toString();
            log.info("Corpus {} added to project {}, payload {}", corpusId, client.getProjectId(), payload);
            return corpusId;
        }
    }

    private Boolean handleDone(Map<String, Serializable> parameters) {
        String projectId = client.getProjectId();
        String exportId = (String) parameters.get(EXPORT_ID_PARAM);
        Objects.requireNonNull(exportId, "Done Export API requires export ID");
        return client.post(API.Export.DONE.toPath(projectId, exportId), "{}", Response::code) == OK.getStatusCode();
    }

}