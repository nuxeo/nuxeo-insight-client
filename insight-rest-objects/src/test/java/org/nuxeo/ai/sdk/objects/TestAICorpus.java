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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestAICorpus {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(Collections.singletonMap("name", "dc:creator"));

        Date start = new Date();
        Date end = new Date(start.getTime() + 1000);
        AICorpus.Info info = new AICorpus.Info(dateFormat.format(start), dateFormat.format(end));

        AICorpus.Properties props = new AICorpus.Properties.Builder().setBatchId("test-batch-id")
                                                                     .setDocCount(100)
                                                                     .setSplit(80)
                                                                     .setEvaluationDocCount(100)
                                                                     .setFields(fields)
                                                                     .setInfo(info)
                                                                     .setTrainData(new AICorpus.Batch("0", "upload_01"))
                                                                     .setEvalData(new AICorpus.Batch("1", "upload_02"))
                                                                     .setStats(new AICorpus.Batch("2", "upload_03"))
                                                                     .build();
        AICorpus corpus = new AICorpus("test-corpus", props);
        String serialized = MAPPER.writeValueAsString(corpus);
        assertThat(serialized).isNotEmpty().contains("test-batch-id").contains("entity-type");
    }

    @Test
    public void shouldDeserialize() throws URISyntaxException, IOException {
        URL resource = TestAICorpus.class.getClassLoader().getResource("corpus.json");
        assert resource != null;
        File file = new File(resource.toURI());

        assertThat(file).isNotNull().exists();

        AICorpus aiCorpus = MAPPER.readValue(file, AICorpus.class);
        assertThat(aiCorpus).isNotNull();
        assertThat(aiCorpus.getName()).isEqualTo("test-corpus");
    }

}
