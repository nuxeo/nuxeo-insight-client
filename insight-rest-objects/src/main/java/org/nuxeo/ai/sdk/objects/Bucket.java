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

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for defining statistics' bucket retrieved from ES
 * <code>
 * {
 * "key":"dc:title",
 * "docCount":100
 * }
 * </code>
 */
public class Bucket {

    public static final String UNDEFINED_KEY = "undefined";

    protected final String key;

    protected final long docCount;

    @JsonCreator
    public Bucket(@JsonProperty("key") String key, @JsonProperty("docCount") long docCount) {
        this.key = StringUtils.isEmpty(key) ? UNDEFINED_KEY : key;
        this.docCount = docCount;
    }

    public String getKey() {
        return key;
    }

    public long getDocCount() {
        return docCount;
    }
}
