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
import static org.nuxeo.ai.sdk.rest.Common.DISTANCE_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.THRESHOLD_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.UID;
import static org.nuxeo.ai.sdk.rest.Common.XPATH_PARAM;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ai.sdk.rest.exception.UnsupportedPathException;

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

    /**
     * Endpoints available for Model execution
     */
    public enum Model implements Endpoint {
        ALL, BY_DATASOURCE, PUBLISHED, DELTA, PREDICT;

        public static final String API_AI = "ai/";

        public final HttpMethod method = HttpMethod.GET;

        /**
         * Resolve path between
         *
         * @param project Id of a client
         * @param id      can be a datasource or a label
         * @return {@link String} as uri
         */
        public String toPath(@Nonnull String project, @Nullable String id, @Nullable String datasource) {
            switch (this) {
            case ALL:
                return API_AI + project + "/" + "models?properties=ai_model";
            case BY_DATASOURCE:
                return API_AI + project + "/" + "models?properties=ai_model&datasource=" + datasource;
            case PUBLISHED:
                return API_AI + project + "/" + "models?properties=ai_model&publishState=published&label=" + datasource;
            case DELTA:
                return API_AI + project + "/model/" + id + "/corpusdelta";
            case PREDICT:
                return API_AI + project + "/model/" + id + "/" + datasource + "/predict?datasource=" + datasource;
            default:
                throw new UnsupportedPathException("Invalid API call for " + this.name());
            }
        }
    }

    /**
     * Endpoints available for Deduplication
     */
    public enum Dedup implements Endpoint {
        INDEX, FIND, ALL, REINDEX;

        public static final String API_DEDUP = "ai/dedup/";

        /**
         * Resolve path for
         *
         * @param project {@link String} as project Id
         * @return {@link String} URL path
         */
        public String toPath(HttpMethod method, @Nonnull String project) {
            return toPath(method, project, emptyMap());
        }

        /**
         * Resolve path for
         *
         * @param project    {@link String} as project Id
         * @param parameters {@link Map} of query parameters
         * @return {@link String} URL path
         */
        public String toPath(HttpMethod method, @Nonnull String project, Map<String, Serializable> parameters) {
            Objects.requireNonNull(project);

            String docId = (String) parameters.get(UID);
            String xpath = (String) parameters.get(XPATH_PARAM);
            int distance = (int) parameters.getOrDefault(DISTANCE_PARAM, 0);
            switch (this) {
            case INDEX:
                Objects.requireNonNull(docId);
                return API_DEDUP + project + "/index/" + docId + "/" + xpath;
            case FIND: {
                if (method == HttpMethod.GET) {
                    Objects.requireNonNull(docId);
                    Objects.requireNonNull(xpath);
                    return API_DEDUP + project + "/find/" + docId + "/" + xpath + "?distance=" + distance;
                } else {
                    String segment = StringUtils.isEmpty(xpath) ? "" : "&xpath=" + xpath;
                    return API_DEDUP + project + "/find?distance=" + distance + segment;
                }
            }
            case ALL:
                return API_DEDUP + project + "/similars";
            case REINDEX:
                int threshold = (int) parameters.get(THRESHOLD_PARAM);
                return API_DEDUP + project + "/reindex?threshold=" + threshold;
            default:
                throw new UnsupportedPathException("Invalid API call for " + this.name());
            }
        }
    }

    public interface Endpoint {

    }
}
