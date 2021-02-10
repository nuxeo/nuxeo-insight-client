/*
 * (C) Copyright 2006-2020 Nuxeo (http://nuxeo.com/) and others.
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
 *     anechaev
 */
package org.nuxeo.ai.sdk.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a response from TFServing
 */
@JsonDeserialize(using = InsightResponse.ResultDeserializer.class)
public class InsightResponse implements Serializable {

    private static final Logger log = LogManager.getLogger(InsightResponse.class);

    public static final String JSON_OUTPUTS = "output_names";

    public static final String JSON_LABELS = "_labels";

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Map{ output_name: { category1: prob1 ..... } }
     */
    protected Map<String, List<Label>> labels;

    public InsightResponse(@JsonProperty("labels") Map<String, List<Label>> labels) {
        this.labels = labels;
    }

    public Map<String, List<Label>> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, List<Label>> labels) {
        this.labels = labels;
    }

    public static class Label {

        protected String name;

        protected float confidence;

        public Label() {
        }

        public Label(String name, float confidence) {
            this.name = name;
            this.confidence = confidence;
        }

        public String getName() {
            return name;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Label label = (Label) o;
            return Double.compare(label.confidence, confidence) == 0 && Objects.equals(this.name, label.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, confidence);
        }

        @Override
        public String toString() {
            return "Label{" + "name='" + name + '\'' + ", confidence=" + confidence + '}';
        }
    }

    public static class ResultDeserializer extends StdDeserializer<InsightResponse> {

        protected ResultDeserializer() {
            this(InsightResponse.class);
        }

        protected ResultDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public InsightResponse deserialize(JsonParser jp, DeserializationContext ctx)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);

            if (node.has("labels")) {
                // coming from internal usage
                TypeReference<Map<String, List<Label>>> ref = new TypeReference<Map<String, List<Label>>>() {
                };
                Map<String, List<Label>> probabilities = MAPPER.readValue(node.with("labels").toString(), ref);
                return new InsightResponse(probabilities);
            }

            Map<String, List<Label>> probabilities = new HashMap<>();
            // deserialize from external usage. Matter for change when a common SDK introduced
            node.withArray("results").elements().forEachRemaining(elem -> {
                JsonNode jsonNode = elem.withArray(JSON_OUTPUTS);

                jsonNode.elements().forEachRemaining(outputNode -> {
                    String outputName = outputNode.asText();
                    List<Label> labels = new ArrayList<>();
                    probabilities.put(outputName, labels);

                    if (!elem.hasNonNull(outputName) || !elem.hasNonNull(outputName + JSON_LABELS)) {
                        log.warn("Either {} or its label are null", outputName);
                        return;
                    }

                    ArrayNode outputProbabilities = (ArrayNode) elem.get(outputName);
                    ArrayNode outputLabels = (ArrayNode) elem.get(outputName + JSON_LABELS);

                    if (outputLabels.size() == outputProbabilities.size()) {
                        for (int i = 0; i < outputLabels.size(); i++) {
                            String label = outputLabels.get(i).asText();
                            float confidence = outputProbabilities.get(i).floatValue();

                            Label res = new Label(label, confidence);
                            labels.add(res);
                        }
                    } else {
                        log.warn("Mismatch of labels and probabilities cardinality");
                    }

                });
            });

            return new InsightResponse(probabilities);
        }
    }
}
