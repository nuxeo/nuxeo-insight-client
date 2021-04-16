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
package org.nuxeo.ai.sdk.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @since 0.1
 */
public class LogInterceptor implements Interceptor {

    private static final Logger log = LogManager.getLogger(LogInterceptor.class);

    public static final List<String> redactedHeaders = Arrays.asList("PROXY-AUTHORIZATION", "AUTHORIZATION",
            "X-AUTHENTICATION-TOKEN");

    public static final List<String> skippedHeaders = Arrays.asList("CONTENT-TYPE", "CONTENT-LENGTH");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (log.isDebugEnabled()) {
            RequestBody requestBody = request.body();
            Connection connection = chain.connection();
            boolean hasRequestBody = requestBody != null;
            log.debug("{} {}{}{}", request.method(), request.url(),
                    connection != null ? " " + connection.protocol() : "",
                    hasRequestBody ? " (" + requestBody.contentLength() + "-byte body)" : "");
            if (log.isTraceEnabled()) {
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        log.trace("  Content-Type: {}", requestBody.contentType());
                    }
                    if (requestBody.contentLength() != -1L) {
                        log.trace("  Content-Length: {}", requestBody.contentLength());
                    }
                }
                Headers headers = request.headers();
                headers.names()
                       .stream()
                       .filter(name -> !skippedHeaders.contains(name.toUpperCase()))
                       .forEach(name -> logHeader(headers, name));
            }
        }
        Response response = chain.proceed(request);
        log.debug("RESPONSE {}", response.toString());
        return response;
    }

    private void logHeader(Headers headers, String name) {
        String value;
        if (redactedHeaders.contains(name.toUpperCase())) {
            value = "******"; // do not log sensitive values
        } else {
            value = headers.get(name);
        }
        log.trace("  {}:{}", name, value);
    }
}
