/*
 * (C) Copyright 2006-2021 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Contributors:
 *     Andrei Nechaev
 */
package org.nuxeo.ai.sdk.objects.deduplication;

import java.util.Collections;
import java.util.List;

/**
 * Container Object to store scroll id and results for Similar tuples
 * <pre>
 *     {
 *     "scrollId": "DXF1ZXJ5QW5kRmV0Y2gB...==",
 *     "result": [
 *         {
 *             "documentId": "doc10",
 *             "xpath": "file:content",
 *             "similarDocumentIds": [
 *                 "doc12",
 *                 "doc13",
 *                 "doc14",
 *                 ...
 * </pre>
 */
public class ScrollableResult {

    protected String scrollId;

    protected List<SimilarTuple> result;

    public ScrollableResult() {
    }

    /**
     * @param id      {@link String} as a Scroll ID to use for getting next results
     * @param results {@link List} of {@link SimilarTuple} that represent similar documents
     */
    public ScrollableResult(String id, List<SimilarTuple> results) {
        this.scrollId = id;
        this.result = results;
    }

    /**
     * Factory method to return empty results
     *
     * @return {@link ScrollableResult}
     */
    public static ScrollableResult empty() {
        return new ScrollableResult(null, Collections.emptyList());
    }

    public String getScrollId() {
        return scrollId;
    }

    public List<SimilarTuple> getResult() {
        return result;
    }
}
