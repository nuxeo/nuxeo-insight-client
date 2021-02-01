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

package org.nuxeo.ai.client;

import org.nuxeo.ai.exception.UnsupportedPathException;

import javax.annotation.Nonnull;

public class API {

    public static final String API_AI = "ai/";

    public static final String API_EXPORT_AI = "ai_export/";

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    public enum Export {
        INIT(HttpMethod.POST), ATTACH(HttpMethod.POST), BIND(HttpMethod.POST), DONE(HttpMethod.POST);

        public final HttpMethod method;

        Export(HttpMethod method) {
            this.method = method;
        }

        public String lowerName() {
            return this.name().toLowerCase();
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
}
