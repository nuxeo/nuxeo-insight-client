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

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.nuxeo.ai.sdk.rest.Common.DEFAULT_XPATH;
import static org.nuxeo.ai.sdk.rest.Common.THRESHOLD_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.UID;
import static org.nuxeo.ai.sdk.rest.Common.XPATH_PARAM;
import static org.nuxeo.ai.sdk.rest.Common.Headers.SCROLL_ID_HEADER;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.nuxeo.ai.sdk.objects.TensorInstances;
import org.nuxeo.ai.sdk.objects.deduplication.ScrollableResult;
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

        List<String> result = client.api(Dedup.FIND).call(params, createTensor(null));
        assertThat(result).containsExactly("doc_001", "doc_003", "doc_004");

        params.put(XPATH_PARAM, DEFAULT_XPATH);
        result = client.api(Dedup.FIND).call(params, createTensor(null));
        assertThat(result).containsExactly("doc_001", "doc_003");

    }

    @Test
    public void shouldFindTuples() throws IOException {
        InsightClient client = getInsightClient();
        ScrollableResult result = client.api(Dedup.ALL).call(emptyMap());
        assertThat(result).isNotNull();
        String scrollId = result.getScrollId();
        assertThat(scrollId).isNotEmpty();
        assertThat(result.getResult()).isNotEmpty();
        assertThat(result.getResult().get(0).getDocumentId()).isNotEmpty();
        assertThat(result.getResult().get(0).getXpath()).isNotEmpty();
        assertThat(result.getResult().get(0).getSimilarDocumentIds()).isNotEmpty();
        assertThat(result.getResult().get(0).getSimilarDocumentIds()).containsExactlyInAnyOrder("doc12", "doc13",
                "doc14", "doc15", "doc16", "doc17", "doc18");

        result = client.api(Dedup.ALL).call(singletonMap(SCROLL_ID_HEADER, scrollId));
        assertThat(result).isNotNull();
        assertThat(scrollId).isNotEmpty();
        assertThat(result.getResult()).isNotEmpty();
        assertThat(result.getResult().get(0).getDocumentId()).isNotEmpty();
        assertThat(result.getResult().get(0).getXpath()).isNotEmpty();
        assertThat(result.getResult().get(0).getSimilarDocumentIds()).isNotEmpty();
        assertThat(result.getResult().get(0).getSimilarDocumentIds()).containsExactlyInAnyOrder("doc121", "doc123");
    }

    @Test
    public void iCanRecalculateTuples() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(THRESHOLD_PARAM, 0);
        Boolean response = client.api(Dedup.RECALCULATETUPLES).call(params);
        assertThat(response).isTrue();
    }

    @Test
    public void iCanDeleteFromIndex() throws IOException {
        InsightClient client = getInsightClient();
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(UID, "document_uuid_001");
        params.put(XPATH_PARAM, DEFAULT_XPATH);

        Boolean result = client.api(Dedup.INDEX).call(params, createTensor("document_uuid_001"));
        assertThat(result).isTrue();

        result = client.api(Dedup.DELETE).call(params, "{}");
        assertThat(result).isTrue();
    }

    private TensorInstances createTensor(String docId) {
        return new TensorInstances(docId, Collections.emptyList());
    }
}
