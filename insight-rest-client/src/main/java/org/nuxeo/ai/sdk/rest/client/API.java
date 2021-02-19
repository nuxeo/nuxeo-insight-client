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

import org.nuxeo.ai.sdk.rest.exception.UnsupportedPathException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class for enums containing public API to Insight Cloud
 */
public class API {

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }


    public enum Export implements Endpoint {

        INIT(HttpMethod.POST), ATTACH(HttpMethod.POST), BIND(HttpMethod.POST), DONE(HttpMethod.POST);

        public static final String API_EXPORT_AI = "ai_export/";

        public final HttpMethod method;

        Export(HttpMethod method) {
            this.method = method;
        }

        /**
         * Resolve path based on
         *
         * @param project Id of a client
         * @param id      of a document
         * @return {@link String} as uri
         */
        public String toPath(@Nonnull String project, String id) {
            switch (this) {
            case INIT:
                return API_EXPORT_AI + "init/" + project + "?corpora=" + id;
            case ATTACH:
                return API_EXPORT_AI + "attach/" + project + "/" + id;
            case DONE:
                return API_EXPORT_AI + "done/" + project + "/" + id;
            default:
                throw new UnsupportedPathException("Invalid API call for " + this.name());
            }
        }

        /**
         * Resolve path between
         *
         * @param project   Id of a client
         * @param modelId   of AI_Model
         * @param corporaId of AI_Corpora
         * @return {@link String} as uri
         */
        public String toPath(@Nonnull String project, @Nonnull String modelId, @Nonnull String corporaId) {
            if (this == BIND) {
                return API_EXPORT_AI + "bind/" + project + "?modelId=" + modelId + "&corporaId=" + corporaId;
            } else {
                throw new UnsupportedPathException("Invalid API call for " + this.name());
            }
        }
    }

    public enum Model implements Endpoint {
        ALL, BY_DATASOURCE, PUBLISHED, DELTA, PREDICT;

        public static final String API_AI = "ai/";

        public final HttpMethod method = HttpMethod.GET;

        /**
         * Resolve path between
         *
         * @param project Id of a client
         * @param id can be a datasource or a label
         * @return {@link String} as uri
         */
        public String toPath(@Nonnull String project, @Nullable String id, @Nullable String datasource) {
            switch (this) {
            case ALL:
                return API_AI + project + "/" + "models?properties=ai_model";
            case BY_DATASOURCE:
                return  API_AI + project + "/" + "models?properties=ai_model&datasource=" + datasource;
            case PUBLISHED:
                return  API_AI + project + "/" + "models?properties=ai_model&publishState=published&label=" + datasource;
            case DELTA:
                return API_AI + project + "/model/" + id + "/corpusdelta";
            case PREDICT:
                return API_AI + project + "/model/" + id + "/" + datasource + "/predict?datasource=" + datasource;
            default:
                throw new UnsupportedPathException("Invalid API call for " + this.name());
            }
        }
    }

    public interface Endpoint {

    }
}
