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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestInsightResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        List<InsightResponse.Label> labels = Collections.singletonList(new InsightResponse.Label("LOC", 0.01f));

        InsightResponse ir = new InsightResponse(Collections.singletonMap("dc:title", labels));
        String serialized = MAPPER.writeValueAsString(ir);
        assertThat(serialized).contains("LOC", "0.01");
    }

    @Test
    public void shouldDeserialize() throws URISyntaxException, IOException {
        URL resource = TestCorporaParameters.class.getClassLoader().getResource("insight_response.json");
        assert resource != null;

        File file = new File(resource.toURI());
        InsightResponse ir = MAPPER.readValue(file, InsightResponse.class);
        assertThat(ir).isNotNull();
        assertThat(ir.labels).hasSize(4);
    }
}