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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.sdk.rest.client.API;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import org.nuxeo.ai.sdk.rest.exception.ConfigurationException;
import org.nuxeo.ai.sdk.rest.exception.InvalidEndpointException;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import static org.nuxeo.ai.sdk.rest.Common.MODEL_ID_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.MODEL_NAME_PARAM;
import static org.nuxeo.ai.sdk.rest.client.InsightClient.MAPPER;

public class ModelCaller implements Resource<API.Model> {

    public static final String LABEL_PARAM = "label";

    public static final String DATASOURCE_PARAM = "datasource";

    private final Logger log = LogManager.getLogger(ModelCaller.class);

    private final InsightClient client;

    private final API.Model type;

    public ModelCaller(InsightClient client, API.Model type) {
        this.client = client;
        this.type = type;
    }

    @Override
    public <T> T call(Map<String, Serializable> parameters) throws JsonProcessingException {
        return call(parameters, null);
    }

    @Override
    @SuppressWarnings("unchecked") // TODO: review casting
    public <T> T call(Map<String, Serializable> parameters, Serializable payload) throws JsonProcessingException {
        if (client == null || !client.isConnected()) {
            throw new ConfigurationException("No active client");
        }

        switch (this.type) {
        case ALL:
            return (T) client.get(API.Model.ALL.toPath(client.getProjectId(), null, null), response -> {
                if (response.body() == null) {
                    return null;
                }

                return response.body().string();
            });
        case PUBLISHED:
            String label = (String) parameters.get(LABEL_PARAM);
            return (T) client.get(API.Model.PUBLISHED.toPath(client.getProjectId(), null, label), response -> {
                if (response.body() == null) {
                    return null;
                }

                return response.body().string();
            });
        case BY_DATASOURCE: {
            String datasource = (String) parameters.getOrDefault(DATASOURCE_PARAM,
                    client.getConfiguration().getDatasource());
            return (T) client.get(API.Model.BY_DATASOURCE.toPath(client.getProjectId(), null, datasource), response -> {
                if (response.body() == null) {
                    return null;
                }

                return response.body().string();
            });
        }
        case DELTA:
            String modelId = (String) parameters.get(MODEL_ID_PARAM);
            return (T) client.get(API.Model.DELTA.toPath(client.getProjectId(), modelId, null), response -> {
                if (response.body() == null) {
                    return null;
                }

                return response.body().string();
            });
        case PREDICT:
            Objects.requireNonNull(payload);
            String modelName = (String) parameters.get(MODEL_NAME_PARAM);
            String datasource = (String) parameters.get(DATASOURCE_PARAM);
            String json = MAPPER.writeValueAsString(payload);
            return (T) client.post(API.Model.PREDICT.toPath(client.getProjectId(), modelName, datasource), json,
                    response -> {
                        if (!response.isSuccessful() || response.body() == null) {
                            log.error("Failed to predict for project {}, payload {}, url {}, code {} and reason {}",
                                    client.getProjectId(), payload, client.getUrl(), response.code(),
                                    response.message());
                            return null;
                        }

                        return response.body().string();
                    });
        default:
            throw new InvalidEndpointException("No such endpoint " + this.type.name());
        }
    }
}
