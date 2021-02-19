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
package org.nuxeo.ai.sdk.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.nuxeo.ai.sdk.objects.DataType.CATEGORY;
import static org.nuxeo.ai.sdk.objects.DataType.IMAGE;
import static org.nuxeo.ai.sdk.objects.DataType.TEXT;

/**
 * A JSON representation of Tensorflow instance parameters
 * _______________________________________________________
 * Example:
 * <pre>
 * {
 *   "docId": "aDocumentId",
 *   "instances": [
 *     {
 *       "dc:subjects": {
 *         "type": "cat",
 *         "categories": [
 *           "sciences",
 *           "art/cinema"
 *         ]
 *       },
 *       "dc:title": {
 *         "type": "txt",
 *         "text": "My document title"
 *       }
 *     }
 *   ]
 * }
 * </pre>
 */
public class TensorInstances implements Serializable {

    private static final long serialVersionUID = 202108021233428L;

    public final String docId;

    public final List<Map<String, Tensor>> instances;

    public TensorInstances(@JsonProperty("docId") String docId,
            @JsonProperty("instances") List<Map<String, Tensor>> inputs) {
        this.docId = docId;
        this.instances = inputs;
    }

    /**
     * A JSON representation of Tensorflow instance parameter
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tensor implements Serializable {

        private static final long serialVersionUID = 2603715122387085509L;

        public final String type;

        public final String b64;

        public final String text;

        public final String[] categories;

        protected Tensor(@JsonProperty("type") String type, @JsonProperty("b64") String b64,
                @JsonProperty("text") String text, @JsonProperty("categories") String[] categories) {
            this.type = type;
            this.b64 = b64;
            this.text = text;
            this.categories = categories;
        }

        protected Tensor(DataType type, String b64, String text, String[] categories) {
            this.type = type.shorten();
            this.b64 = b64;
            this.text = text;
            this.categories = categories;
        }

        public static Tensor image(String b64) {
            return new Tensor(IMAGE.shorten(), b64, null, null);
        }

        public static Tensor text(String text) {
            return new Tensor(TEXT.shorten(), null, text, null);
        }

        public static Tensor category(String[] categories) {
            return new Tensor(CATEGORY.shorten(), null, null, categories);
        }
    }
}
