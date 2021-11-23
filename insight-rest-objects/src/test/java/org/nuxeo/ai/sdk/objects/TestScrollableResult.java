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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.nuxeo.ai.sdk.objects.deduplication.ScrollableResult;
import org.nuxeo.ai.sdk.objects.deduplication.SimilarTuple;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestScrollableResult {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        ScrollableResult empty = ScrollableResult.empty();
        String json = MAPPER.writeValueAsString(empty);
        assertThat(json).isNotEmpty();

        ScrollableResult deserialized = MAPPER.readValue(json, ScrollableResult.class);
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getResult()).isEmpty();
        assertThat(deserialized.getScrollId()).isNull();

        SimilarTuple similarTuple = new SimilarTuple("doc1", "file:content",
                Sets.newLinkedHashSet(Pair.of("doc2", "file:content"), Pair.of("doc3", "file:content")));
        ScrollableResult result = new ScrollableResult("test", singletonList(similarTuple));
        json = MAPPER.writeValueAsString(result);
        assertThat(json).isNotEmpty();

        deserialized = MAPPER.readValue(json, ScrollableResult.class);
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getScrollId()).isEqualTo("test");
        assertThat(deserialized.getResult()).isNotEmpty();
        assertThat(deserialized.getResult().get(0)).isNotNull();
        assertThat(deserialized.getResult().get(0).getSimilarDocuments()).isNotEmpty();
        assertThat(deserialized.getResult().get(0).getSimilarDocuments()).containsExactlyInAnyOrder(
                Pair.of("doc2", "file:content"), Pair.of("doc3", "file:content"));
    }
}
