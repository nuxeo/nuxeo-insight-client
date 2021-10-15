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

package org.nuxeo.ai.sdk.rest.api;

import static java.util.Collections.emptyList;
import static org.nuxeo.ai.sdk.rest.Common.DISTANCE_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.UID;
import static org.nuxeo.ai.sdk.rest.Common.XPATH_PARAM;
import static org.nuxeo.ai.sdk.rest.client.API.HttpMethod.GET;
import static org.nuxeo.ai.sdk.rest.client.API.HttpMethod.POST;
import static org.nuxeo.ai.sdk.rest.client.InsightClient.MAPPER;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.sdk.objects.TensorInstances;
import org.nuxeo.ai.sdk.rest.ResponseHandler;
import org.nuxeo.ai.sdk.rest.client.API;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import org.nuxeo.ai.sdk.rest.exception.InvalidEndpointException;
import org.nuxeo.ai.sdk.rest.exception.InvalidParametersException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public class DedupCaller implements Resource {

    private static final TypeReference<List<String>> LIST_TYPE_REFERENCE = new TypeReference<List<String>>() {
    };

    private final Logger log = LogManager.getLogger(DedupCaller.class);

    private final InsightClient client;

    private final API.Dedup type;

    public DedupCaller(InsightClient client, API.Dedup type) {
        this.client = client;
        this.type = type;
    }

    @Nullable
    @Override
    public <T> T call(Map<String, Serializable> parameters) throws IOException {
        return call(parameters, null);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Map<String, Serializable> parameters, Serializable payload) throws IOException {
        switch (this.type) {
        case INDEX:
            return (T) handleIndex(parameters, (TensorInstances) payload);
        case FIND:
            return (T) handleFind(parameters, (TensorInstances) payload);
        default:
            throw new InvalidEndpointException("No such endpoint " + this.type.name());
        }
    }

    private Boolean handleIndex(Map<String, Serializable> parameters, TensorInstances payload)
            throws JsonProcessingException {
        String docId = (String) parameters.get(UID);
        String xpath = (String) parameters.get(XPATH_PARAM);
        int distance = (int) parameters.getOrDefault(DISTANCE_PARAM, 0);
        if (StringUtils.isAnyEmpty(docId, xpath) || payload == null) {
            throw new InvalidParametersException("Document UUID, XPath and Payload are required parameters");
        }

        String json = MAPPER.writeValueAsString(payload);
        return client.post(this.type.toPath(POST, client.getProjectId(), docId, xpath, distance), json, response -> {
            if (response.isSuccessful()) {
                log.debug("Successfully indexed document {} with xpath {}", docId, xpath);
                return true;
            }

            log.error(
                    "Failed to index document {} with xpath {} for project {};\nURL: {}\nresponse code: {}\nmessage: {}",
                    docId, xpath, client.getProjectId(), client.getUrl(), response.code(), response.message());
            return false;
        });
    }

    private List<String> handleFind(Map<String, Serializable> parameters, TensorInstances payload)
            throws JsonProcessingException {
        String docId = (String) parameters.get(UID);
        String xpath = (String) parameters.get(XPATH_PARAM);
        ResponseHandler<List<String>> handler = handleResponse(docId, xpath);

        if (payload != null) {
            if (StringUtils.isEmpty(docId)) {
                throw new InvalidParametersException("Document UUID and XPath are required parameters");
            }

            String json = MAPPER.writeValueAsString(payload);
            return client.post(this.type.toPath(POST, client.getProjectId(), null, xpath), json, handler);
        } else {
            if (StringUtils.isEmpty(xpath)) {
                throw new InvalidParametersException("Document UUID and XPath are required parameters");
            }

            return client.get(this.type.toPath(GET, client.getProjectId(), docId, xpath), handler);
        }
    }

    protected ResponseHandler<List<String>> handleResponse(String docId, String xpath) {
        return response -> {
            if (!response.isSuccessful()) {
                log.error(
                        "Failed to find any similar documents {} for xpath {} for project {};\nURL: {}\nresponse code: {}\nmessage: {}",
                        docId, xpath, client.getProjectId(), client.getUrl(), response.code(), response.message());
                return emptyList();
            }

            return response.body() != null ?
                    MAPPER.readValue(response.body().byteStream(), LIST_TYPE_REFERENCE) :
                    emptyList();
        };
    }
}
