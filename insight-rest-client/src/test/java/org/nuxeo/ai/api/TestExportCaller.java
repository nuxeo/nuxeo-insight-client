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

package org.nuxeo.ai.api;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.nuxeo.ai.CorporaParameters;
import org.nuxeo.ai.client.API;
import org.nuxeo.ai.client.Authentication;
import org.nuxeo.ai.client.InsightClient;
import org.nuxeo.ai.client.InsightConfiguration;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ai.api.ExportCaller.CORPORA_ID_PARAM;
import static org.nuxeo.ai.api.ExportCaller.EXPORT_ID_PARAM;
import static org.nuxeo.ai.api.ExportCaller.MODEL_ID_PARAM;

public class TestExportCaller {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            options().extensions(new ResponseTemplateTransformer(true)).port(5089));

    @Test
    public void shouldCallInitExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        CorporaParameters corporaParameters = new CorporaParameters();
        Map<String, Serializable> params = new HashMap<>();
        String uuid = client.api(ExportResource.class).call(API.Export.INIT, params, corporaParameters);
        assertThat(uuid).isNotEmpty();
    }

    @Test
    public void shouldCallBindExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        CorporaParameters corporaParameters = new CorporaParameters();
        Map<String, Serializable> params = new HashMap<>();
        params.put(MODEL_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d14081221");
        params.put(CORPORA_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d1408ce67a0");
        boolean bound = client.api(ExportResource.class).call(API.Export.BIND, params, corporaParameters);
        assertThat(bound).isTrue();
    }

    @Test
    public void shouldCallAttachExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        CorporaParameters corporaParameters = new CorporaParameters();
        Map<String, Serializable> params = new HashMap<>();
        params.put(CORPORA_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d14081221");
        String uuid = client.api(ExportResource.class).call(API.Export.ATTACH, params, corporaParameters);
        assertThat(uuid).isNotEmpty();
    }

    @Test
    public void shouldCallDoneExportAPI() throws IOException {
        InsightClient client = getInsightClient();
        CorporaParameters corporaParameters = new CorporaParameters();
        Map<String, Serializable> params = new HashMap<>();
        params.put(EXPORT_ID_PARAM, "e67ee0e8-1bef-4fb7-9966-1d1408ce67a0");
        boolean bound = client.api(ExportResource.class).call(API.Export.DONE, params, corporaParameters);
        assertThat(bound).isTrue();
    }

    private InsightClient getInsightClient() {
        Authentication auth = new Authentication("Administrator", "Administrator");
        InsightConfiguration config = new InsightConfiguration.Builder().setProjectId("test")
                                                                        .setAuthentication(auth)
                                                                        .setUrl("http://localhost:5089")
                                                                        .build();
        InsightClient client = new InsightClient(config);
        client.connect();
        return client;
    }
}
