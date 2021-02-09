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

import java.time.Duration;
import java.util.Objects;

public class InsightConfiguration {

    private final String url;

    private final String projectId;

    private final String datasource;

    private final Authentication authentication;

    private final Duration readTimeout;

    private final Duration writeTimeout;

    private final Duration connectionTimeout;

    protected InsightConfiguration(String url, String projectId, String datasource, Authentication authentication,
            Duration readTimeout, Duration writeTimeout, Duration connectionTimeout) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(projectId);
        Objects.requireNonNull(authentication);

        this.url = url;
        this.projectId = projectId;
        this.datasource = datasource;
        this.authentication = authentication;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    public String getUrl() {
        return url;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getDatasource() {
        return datasource;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public static class Builder {

        private String url;

        private String projectId;

        private Authentication authentication;

        private String datasource = "dev";

        private Duration readTimeout = Duration.ofMinutes(10);

        private Duration writeTimeout = Duration.ofMinutes(10);

        private Duration connectionTimeout = Duration.ofSeconds(30);

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder setDatasource(String datasource) {
            this.datasource = datasource;
            return this;
        }

        public Builder setAuthentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setWriteTimeout(Duration writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public InsightConfiguration build() {
            return new InsightConfiguration(url, projectId, datasource, authentication, readTimeout, writeTimeout,
                    connectionTimeout);
        }
    }
}
