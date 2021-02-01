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

package org.nuxeo.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.callers.Caller;
import org.nuxeo.ai.callers.ExportCaller;
import org.nuxeo.ai.exception.ConfigurationException;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.spi.auth.BasicAuthInterceptor;
import org.nuxeo.client.spi.auth.TokenAuthInterceptor;

import java.io.IOException;
import java.util.function.Supplier;

import static org.nuxeo.client.ConstantsV1.API_PATH;

public class InsightClient {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger log = LogManager.getLogger(InsightClient.class);

    private final InsightConfiguration configuration;

    private NuxeoClient client;

    public InsightClient(InsightConfiguration configuration) {
        this.configuration = configuration;
    }

    public InsightConfiguration getConfiguration() {
        return configuration;
    }

    public void connect() throws ConfigurationException {
        Authentication auth = configuration.getAuthentication();
        okhttp3.Interceptor interceptor;
        switch (auth.getType()) {
        case BASIC:
            if (StringUtils.isEmpty(auth.getUsername())) {
                throw new IllegalArgumentException("Basic authentication requires Username");
            }
            interceptor = new BasicAuthInterceptor(auth.getUsername(), auth.getSecret());
            break;
        case TOKEN:
            interceptor = new TokenAuthInterceptor(auth.getSecret());
            break;
        default:
            throw new ConfigurationException("Wrong Configuration type " + auth.getType().name());
        }

        NuxeoClient.Builder builder = new NuxeoClient.Builder().url(configuration.getUrl())
                                                               .authentication(interceptor)
                                                               .readTimeout(configuration.getReadTimeout().getSeconds())
                                                               .writeTimeout(
                                                                       configuration.getWriteTimeout().getSeconds())
                                                               .schemas("dublincore", "common")
                                                               .header("Accept-Encoding", "identity")
                                                               .connectTimeout(configuration.getConnectionTimeout()
                                                                                            .getSeconds());

        if (log.isDebugEnabled()) {
            LogInterceptor logInterceptor = new LogInterceptor();
            builder.interceptor(logInterceptor);
        }

        client = builder.connect();
    }

    public boolean isConnected() {
        return client != null;
    }

    enum Resource {
        EXPORT, TRAIN, PREDICT
    }

    public <T> Caller<T> api(Resource resource) {
        switch (resource) {
        case EXPORT:
            return (Caller<T>) new ExportCaller(this);
        default:
            return null;
        }
    }

    protected NuxeoClient getClient() throws ConfigurationException {
        if (client == null) {
            connect();
        }
        return client;
    }

    protected String getApiUrl() {
        return configuration.getUrl() + API_PATH;
    }

    public <T> T post(String postUrl, String jsonBody, ResponseHandler<T> handler) {
        return callCloud(() -> getClient().post(getApiUrl() + postUrl, jsonBody), handler);
    }

    public <T> T put(String putUrl, String jsonBody, ResponseHandler<T> handler) {
        return callCloud(() -> getClient().put(getApiUrl() + putUrl, jsonBody), handler);
    }

    public <T> T get(String url, ResponseHandler<T> handler) {
        return callCloud(() -> getClient().get(getApiUrl() + url), handler);
    }

    public <T> T callCloud(Supplier<Response> caller, ResponseHandler<T> handler) {
        Response response = null;
        try {
            if (isConnected()) {
                response = caller.get();
                if (response != null && handler != null) {
                    return handler.handleResponse(response);
                }
            } else {
                log.warn("Nuxeo cloud client is not configured or unavailable.");
            }
        } catch (IllegalArgumentException iae) {
            log.warn("IllegalArgumentException exception: ", iae);
        } catch (IOException e) {
            log.warn("IOException exception: ", e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
        return null;
    }
}
