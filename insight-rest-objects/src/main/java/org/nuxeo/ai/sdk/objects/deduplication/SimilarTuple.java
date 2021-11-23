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
 *     Nuxeo
 */
package org.nuxeo.ai.sdk.objects.deduplication;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * POJO representing a similar document tuple with the source document id, the given xpath and the similar documents.
 */
@JsonDeserialize(using = SimilarTuple.SimilarTupleDeserializer.class)
public class SimilarTuple implements Serializable {

    private static final long serialVersionUID = -1000077427339197687L;

    protected String documentId;

    protected String xpath;

    protected Set<Pair<String, String>> similarDocuments;

    public SimilarTuple() {
    }

    public SimilarTuple(String documentId, String xpath, Set<Pair<String, String>> similarDocuments) {
        this.documentId = documentId;
        this.xpath = xpath;
        this.similarDocuments = similarDocuments;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Set<Pair<String, String>> getSimilarDocuments() {
        return similarDocuments;
    }

    public String getXpath() {
        return xpath;
    }

    public static class SimilarTupleDeserializer extends JsonDeserializer<SimilarTuple> {
        @Override
        public SimilarTuple deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            TypeReference<Map<String, Object>> ref = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> object = jsonParser.readValueAs(ref);
            String docId = (String) object.get("documentId");
            String xpath = (String) object.get("xpath");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> similarDocumentsList = (List<Map<String, String>>) object.get("similarDocuments");
            Set<Pair<String, String>> similarDocuments = similarDocumentsList.stream()
                                                                             .filter(elem -> !elem.isEmpty())
                                                                             .filter(elem -> elem.keySet()
                                                                                                 .stream()
                                                                                                 .findAny()
                                                                                                 .isPresent())
                                                                             .map(elem -> {
                                                                                 String key = elem.keySet()
                                                                                                  .stream()
                                                                                                  .findAny()
                                                                                                  .get();
                                                                                 return Pair.of(key, elem.get(key));
                                                                             })
                                                                             .collect(Collectors.toSet());
            return new SimilarTuple(docId, xpath, similarDocuments);
        }
    }
}
