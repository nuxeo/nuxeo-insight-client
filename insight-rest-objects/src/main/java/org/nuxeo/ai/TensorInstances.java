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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.nuxeo.ai.DataType.CATEGORY;
import static org.nuxeo.ai.DataType.IMAGE;
import static org.nuxeo.ai.DataType.TEXT;

/**
 * A JSON representation of Tensorflow instance parameters
 */
public class TensorInstances {

    public final String docId;

    public final List<Map<String, Tensor>> instances;

    public TensorInstances(@JsonProperty("docId") String docId, @JsonProperty("instances") Map<String, Tensor> inputs) {
        this.docId = docId;
        this.instances = Collections.singletonList(inputs);
    }

    /**
     * A JSON representation of Tensorflow instance parameter
     */
    public static class Tensor implements Serializable {
        private static final long serialVersionUID = 2603715122387085509L;

        public final String type;

        public final String b64;

        public final String text;

        public final String[] categories;

        protected Tensor(DataType type, String b64, String text, String[] categories) {
            this.type = type.shorten();
            this.b64 = b64;
            this.text = text;
            this.categories = categories;
        }

        public static Tensor image(String b64) {
            return new Tensor(IMAGE, b64, null, null);
        }

        public static Tensor text(String text) {
            return new Tensor(TEXT, null, text, null);
        }

        public static Tensor category(String[] categories) {
            return new Tensor(CATEGORY, null, null, categories);
        }
    }
}
