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
import static org.nuxeo.ai.sdk.rest.Common.CORPORA_ID_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.EXPORT_ID_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.MODEL_ID_PARAM;
import static org.nuxeo.ai.sdk.rest.client.API.Export.BIND;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.nuxeo.ai.sdk.objects.AICorpus;
import org.nuxeo.ai.sdk.objects.CorporaParameters;
import org.nuxeo.ai.sdk.rest.client.API;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TestExportCaller extends AbstractCallerTest {

    @Test
    public void shouldCallInitExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        CorporaParameters corporaParameters = new CorporaParameters();
        Map<String, Serializable> params = new HashMap<>();
        String uuid = client.api(API.Export.INIT).call(params, corporaParameters);
        assertThat(uuid).isNotEmpty();
    }

    @Test
    public void shouldCallBindExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        Map<String, Serializable> params = new HashMap<>();
        params.put(MODEL_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d14081221");
        params.put(CORPORA_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d1408ce67a0");
        Boolean bound = client.api(BIND).call(params);
        assertThat(bound).isNotNull().isTrue();
    }

    @Test
    public void shouldCallAttachExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        AICorpus corpus = new AICorpus("test", new AICorpus.Properties());
        Map<String, Serializable> params = new HashMap<>();
        params.put(CORPORA_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d14081221");
        String uuid = client.api(API.Export.ATTACH).call(params, corpus);
        assertThat(uuid).isNotEmpty();
    }

    @Test
    public void shouldCallDoneExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        Map<String, Serializable> params = new HashMap<>();
        params.put(EXPORT_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d1408ce67a0");
        Boolean done = client.api(API.Export.DONE).call(params);
        assertThat(done).isNotNull().isTrue();
    }
}
