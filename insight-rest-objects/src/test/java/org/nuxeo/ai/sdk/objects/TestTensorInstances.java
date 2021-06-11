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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestTensorInstances {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        TensorInstances.Tensor tensor = new TensorInstances.Tensor(DataType.IMAGE, "b64G32A21", "text",
                new String[] { "milk", "sugar" });
        Map<String, TensorInstances.Tensor> tensorMap = Collections.singletonMap("file:content", tensor);
        TensorInstances instances = new TensorInstances("a doc id", Collections.singletonList(tensorMap));

        String serialized = MAPPER.writeValueAsString(instances);
        assertThat(serialized).isNotNull().contains("file:content", "milk");
    }

    @Test
    public void shouldDeserialize() throws URISyntaxException, IOException {
        URL resource = TestTensorInstances.class.getClassLoader().getResource("tensor_instances.json");
        assert resource != null;
        File file = new File(resource.toURI());
        TensorInstances ti = MAPPER.readValue(file, TensorInstances.class);
        assertThat(ti).isNotNull();
        assertThat(ti.docId).isEqualTo("aDocumentId");
        assertThat(ti.instances).hasSize(1);
        assertThat(ti.instances.get(0)).hasSize(2);
    }
}
