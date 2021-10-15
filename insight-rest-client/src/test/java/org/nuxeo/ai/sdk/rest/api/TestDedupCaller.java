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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.nuxeo.ai.sdk.rest.Common.DEFAULT_XPATH;
import static org.nuxeo.ai.sdk.rest.Common.UID;
import static org.nuxeo.ai.sdk.rest.Common.XPATH_PARAM;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ai.sdk.objects.TensorInstances;
import org.nuxeo.ai.sdk.rest.client.API.Dedup;
import org.nuxeo.ai.sdk.rest.client.InsightClient;
import org.nuxeo.ai.sdk.rest.exception.InvalidParametersException;

public class TestDedupCaller extends AbstractCallerTest {

    @Test
    public void shouldExecuteIndexAPICall() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(UID, "document_uuid_001");
        params.put(XPATH_PARAM, DEFAULT_XPATH);

        Boolean result = client.api(Dedup.INDEX).call(params, createTensor("document_uuid_001"));
        assertThat(result).isTrue();
    }

    @Test(expected = InvalidParametersException.class)
    public void shouldFailOnEmptyPayload() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(UID, "document_uuid_001");
        params.put(XPATH_PARAM, DEFAULT_XPATH);

        Boolean result = client.api(Dedup.INDEX).call(params);
        fail("Should have failed on the step above due to empty payload");
    }

    @Test
    public void shouldFindBasedOnDocId() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(UID, "document_uuid_001");
        params.put(XPATH_PARAM, DEFAULT_XPATH);

        List<String> result = client.api(Dedup.FIND).call(params);
        assertThat(result).containsExactly("doc_001", "doc_002");
    }

    @Test
    public void shouldFindBasedOnTensor() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(UID, "document_uuid_001");

        List<String> result = client.api(Dedup.FIND).call(params, createTensor("document_uuid_001"));
        assertThat(result).containsExactly("doc_001", "doc_003", "doc_004");

        params.put(XPATH_PARAM, DEFAULT_XPATH);
        result = client.api(Dedup.FIND).call(params, createTensor("document_uuid_001"));
        assertThat(result).containsExactly("doc_001", "doc_003");

    }

    private TensorInstances createTensor(String docId) {
        return new TensorInstances(docId, Collections.emptyList());
    }
}
