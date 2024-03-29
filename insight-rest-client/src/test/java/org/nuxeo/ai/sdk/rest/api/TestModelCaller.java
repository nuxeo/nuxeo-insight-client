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

package org.nuxeo.ai.sdk.rest.api;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ai.sdk.rest.Common.MODEL_ID_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.MODEL_NAME_PARAM;
import static org.nuxeo.ai.sdk.rest.api.ModelCaller.DATASOURCE_PARAM;
import static org.nuxeo.ai.sdk.rest.api.ModelCaller.LABEL_PARAM;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.nuxeo.ai.sdk.objects.TensorInstances;
import org.nuxeo.ai.sdk.rest.client.API;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import org.nuxeo.client.objects.Documents;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TestModelCaller extends AbstractCallerTest {

    @Test
    public void shouldGetALLModels() throws IOException {
        InsightClient client = getInsightClient();
        String response = client.api(API.Model.ALL).call(Collections.emptyMap());
        assertThat(response).isNotNull().isNotEqualTo("{}");
        Documents documents = client.getJSONFactory().readJSON(response, Documents.class);
        assertThat(documents).isNotNull();
    }

    @Test
    public void shouldGetModelByDatasource() throws IOException {
        InsightClient client = getInsightClient();
        String response = client.api(API.Model.BY_DATASOURCE).call(Collections.emptyMap());
        assertThat(response).isNotNull().isNotEqualTo("{}");

        Documents documents = client.getJSONFactory().readJSON(response, Documents.class);
        assertThat(documents).isNotNull();
    }

    @Test
    public void shouldGetModelsByLabel() throws IOException {
        InsightClient client = getInsightClient();
        Map<String, Serializable> params = Collections.singletonMap(LABEL_PARAM, "dev");
        String response = client.api(API.Model.PUBLISHED).call(params);
        assertThat(response).isNotNull().isNotEqualTo("{}");

        Documents documents = client.getJSONFactory().readJSON(response, Documents.class);
        assertThat(documents).isNotNull();
    }

    @Test
    public void shouldGetDelta() throws IOException {
        InsightClient client = getInsightClient();
        Map<String, Serializable> params = Collections.singletonMap(MODEL_ID_PARAM,
                "6b93bace-4ed3-408f-8efe-79a8dd287199");
        String response = client.api(API.Model.DELTA).call(params);
        assertThat(response).isNotNull().isNotEqualTo("{}");

        Documents documents = client.getJSONFactory().readJSON(response, Documents.class);
        assertThat(documents).isNotNull();
    }

    @Test
    public void shouldRunPredict() throws IOException {
        InsightClient client = getInsightClient();
        Map<String, Serializable> params = new HashMap<>();
        params.put(MODEL_NAME_PARAM, "testModel");
        params.put(DATASOURCE_PARAM, "dev");
        TensorInstances instances = new TensorInstances("a doc id", Collections.emptyList());
        String response = client.api(API.Model.PREDICT).call(params, instances);
        assertThat(response).isNotEmpty().isNotEqualTo("{}");
    }
}
