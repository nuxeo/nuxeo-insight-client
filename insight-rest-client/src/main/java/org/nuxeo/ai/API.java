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

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ai.exception.ConfigurationException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.OK;
import static org.nuxeo.ai.InsightClient.MAPPER;

public class API {

    public static final String API_AI = "ai/";

    public static final String API_EXPORT_AI = "ai_export/";

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    public enum Export {
        INIT(HttpMethod.POST), ATTACH, BIND, DONE;

        public final HttpMethod method;

        Export() {
            this.method = HttpMethod.GET;
        }

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
                return null;
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
            return API_EXPORT_AI + "bind/" + project + "?modelId=" + modelId + "&corporaId=" + corporaId;
        }
    }
}
