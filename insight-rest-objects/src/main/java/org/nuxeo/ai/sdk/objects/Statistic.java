/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 * Contributors:
 *     Gethin James
 */
package org.nuxeo.ai.sdk.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * A POJO representation of a Dataset Statistic
 * ____________________________________________
 * Example:
 * <pre>
 * {
 *   "id": "terms",
 *   "field": "total",
 *   "type":"total",
 *   "aggType": "terms",
 *   "numericValue": 10,
 *   "value": [
 *     {
 *       "key": "dc:title",
 *       "docCount": 10
 *     }
 *   ]
 * }
 * </pre>
 */
public class Statistic {

    protected final String id;

    protected final String field;

    protected final String type;

    protected final Number numericValue;

    @JsonProperty("value")
    protected List<Bucket> value;

    protected String aggType;

    public Statistic(@JsonProperty("id") String id, @JsonProperty("field") String field,
            @JsonProperty("type") String type, @JsonProperty("aggType") String aggType,
            @JsonProperty("numericValue") Number numericValue) {
        this.id = id;
        this.field = field;
        this.type = type;
        this.numericValue = numericValue;
        this.aggType = aggType;
    }

    /**
     * Factory constructor
     *
     * @return {@link Statistic} new object
     */
    public static Statistic of(String id, String field, String type, String aggType, Number numericValue) {
        return new Statistic(id, field, type, aggType, numericValue);
    }

    /**
     * Factory constructor
     *
     * @param transformer abstraction to encapsulate construction
     * @return {@link Statistic}
     */
    public static Statistic from(Transformer<Statistic> transformer) {
        return transformer.transform();
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getType() {
        return type;
    }

    public Number getNumericValue() {
        return numericValue;
    }

    public List<Bucket> getValue() {
        return value;
    }

    public void setValue(List<Bucket> value) {
        this.value = value;
    }

    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Statistic statistic = (Statistic) o;
        return id.equals(statistic.id) && Objects.equals(field, statistic.field) && Objects.equals(type, statistic.type)
                && Objects.equals(value, statistic.value) && Objects.equals(numericValue, statistic.numericValue)
                && Objects.equals(aggType, statistic.aggType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, field, type, value, numericValue, aggType);
    }
}
