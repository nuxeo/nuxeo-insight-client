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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBucket {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        Bucket bucket = new Bucket("dc:title", 100L);
        String serialized = MAPPER.writeValueAsString(bucket);
        assertThat(serialized).isNotEmpty().contains("dc:title");
    }

    @Test
    public void shouldDeserialize() throws JsonProcessingException {
        Bucket bucket = MAPPER.readValue("{\"key\":\"dc:title\",\"docCount\":100}", Bucket.class);
        assertThat(bucket).isNotNull();
        assertThat(bucket.docCount).isEqualTo(100L);
        assertThat(bucket.key).isEqualTo("dc:title");
    }
}