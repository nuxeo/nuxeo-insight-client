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

import org.junit.Rule;
import org.nuxeo.ai.sdk.rest.client.Authentication;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import org.nuxeo.ai.sdk.rest.client.InsightConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class AbstractCallerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            options().extensions(new ResponseTemplateTransformer(true)).port(5089));

    protected InsightClient getInsightClient() {
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
