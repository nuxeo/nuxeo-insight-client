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

package org.nuxeo.ai.sdk.rest.client;

import static java.util.Collections.emptyMap;
import static org.nuxeo.client.ConstantsV1.API_PATH;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.sdk.rest.LogInterceptor;
import org.nuxeo.ai.sdk.rest.ResponseHandler;
import org.nuxeo.ai.sdk.rest.api.DedupCaller;
import org.nuxeo.ai.sdk.rest.api.ExportCaller;
import org.nuxeo.ai.sdk.rest.api.ModelCaller;
import org.nuxeo.ai.sdk.rest.api.Resource;
import org.nuxeo.ai.sdk.rest.exception.ConfigurationException;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.spi.auth.BasicAuthInterceptor;
import org.nuxeo.client.spi.auth.TokenAuthInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Response;

/**
 * REST Client to Insight Cloud
 * For endpoints look at derived from {@link Resource}
 * for example:
 * {@link ExportCaller} for Export API
 * {@link ModelCaller} for Model API
 */
public class InsightClient {

    private static final Logger log = LogManager.getLogger(InsightClient.class);

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final InsightConfiguration configuration;

    private NuxeoClient client;

    public InsightClient(InsightConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    @Nonnull
    public InsightConfiguration getConfiguration() {
        return configuration;
    }

    @Nonnull
    public NuxeoConverterFactory getJSONFactory() {
        return client.getConverterFactory();
    }

    public BatchUpload getBatchUpload(int chunkSize) {
        return client.batchUploadManager().createBatch().enableChunk().chunkSize(chunkSize);
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

    @Nonnull
    public String getUrl() {
        return configuration.getUrl();
    }

    @Nonnull
    public String getProjectId() {
        return configuration.getProjectId();
    }

    public <T extends API.Endpoint> Resource api(T type) {
        if (type instanceof API.Export) {
            return new ExportCaller(this, (API.Export) type);
        } else if (type instanceof API.Model) {
            return new ModelCaller(this, (API.Model) type);
        } else if (type instanceof API.Dedup) {
            return new DedupCaller(this, (API.Dedup) type);
        } else {
            return null;
        }
    }

    protected NuxeoClient getClient() throws ConfigurationException {
        if (client == null) {
            connect();
        }

        return client;
    }

    protected NuxeoClient getClient(Map<String, Serializable> headers) throws ConfigurationException {
        if (client == null) {
            connect();
        }

        headers.forEach((header, value) -> {
            client.header(header, value);
        });
        return client;
    }

    protected String getApiUrl() {
        return configuration.getUrl() + API_PATH;
    }

    public <T> T get(String url, ResponseHandler<T> handler) {
        return get(url, emptyMap(), handler);
    }

    public <T> T get(String url, Map<String, Serializable> headers, ResponseHandler<T> handler) {
        return callCloud(() -> getClient(headers).get(getApiUrl() + url), handler);
    }

    public <T> T post(String url, String json, ResponseHandler<T> handler) {
        return post(url, emptyMap(), json, handler);
    }

    public <T> T post(String url, Map<String, Serializable> headers, String json, ResponseHandler<T> handler) {
        return callCloud(() -> getClient(headers).post(getApiUrl() + url, json), handler);
    }

    public <T> T delete(String url, String json, ResponseHandler<T> handler) {
        return delete(url, emptyMap(), json, handler);
    }

    public <T> T delete(String url, Map<String, Serializable> headers, String json, ResponseHandler<T> handler) {
        return callCloud(() -> getClient(headers).delete(getApiUrl() + url, json), handler);
    }

    public <T> T put(String url, String json, ResponseHandler<T> handler) {
        return put(url, emptyMap(), json, handler);
    }

    public <T> T put(String url, Map<String, Serializable> headers, String json, ResponseHandler<T> handler) {
        return callCloud(() -> getClient(headers).put(getApiUrl() + url, json), handler);
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
