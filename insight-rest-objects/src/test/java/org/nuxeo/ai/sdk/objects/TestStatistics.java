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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ai.sdk.objects.FieldStatistics.AGG_TYPE_TERMS;

public class TestStatistics {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String STATS_TOTAL = "total";

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        Statistic statistic = new Statistic(AGG_TYPE_TERMS, STATS_TOTAL, STATS_TOTAL, AGG_TYPE_TERMS, 10);
        statistic.setValue(Collections.singletonList(new Bucket("dc:title", 10)));
        String serialized = MAPPER.writeValueAsString(statistic);
        assertThat(serialized).isNotEmpty().contains(STATS_TOTAL, AGG_TYPE_TERMS);

        FieldStatistics fs = FieldStatistics.from(statistic, 100L);
        serialized = MAPPER.writeValueAsString(fs);
        assertThat(serialized).isNotEmpty().contains(STATS_TOTAL);
        System.out.println(serialized);
    }

    @Test
    public void shouldDeserialize() throws JsonProcessingException {
        Statistic statistic = MAPPER.readValue(
                "{\"id\":\"terms\",\"field\":\"total\",\"type\":\"total\",\"aggType\":\"terms\",\"numericValue\":10,\"value\":[{\"key\":\"dc:title\",\"docCount\":10}]}",
                Statistic.class);
        assertThat(statistic).isNotNull();
        assertThat(statistic.numericValue).isEqualTo(10);
        assertThat(statistic.value).hasSize(1);

        FieldStatistics fs = MAPPER.readValue(
                "{\"type\":\"total\",\"field\":\"total\",\"count\":100,\"total\":100,\"missing\":0,\"terms\":[{\"key\":\"dc:title\",\"docCount\":10}],\"cardinality\":0,\"multiClass\":false}",
                FieldStatistics.class);

        assertThat(fs).isNotNull();
        assertThat(fs.total).isEqualTo(100L);
        assertThat(fs.terms).hasSize(1);
    }
}