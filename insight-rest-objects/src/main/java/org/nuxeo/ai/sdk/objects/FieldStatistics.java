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

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POJO that represents overall statistic for a field
 * __________________________________________________
 * Example:
 * <pre>
 * {
 *   "type": "terms",
 *   "field": "total",
 *   "count": 100,
 *   "total": 100,
 *   "missing": 0,
 *   "terms": [
 *     {
 *       "key": "dc:title",
 *       "docCount": 10
 *     }
 *   ],
 *   "cardinality": 0,
 *   "multiClass": false
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldStatistics {

    public static final String AGG_CARDINALITY = "cardinality";

    public static final String AGG_MISSING = "missing";

    public static final String AGG_TYPE_TERMS = "terms";

    public static final String AGG_TYPE_COUNT = "count";

    protected String type;

    protected String field;

    protected long count;

    protected long total;

    protected long missing;

    protected List<Bucket> terms;

    protected long cardinality;

    protected boolean multiClass;

    public FieldStatistics() {
    }

    public FieldStatistics(long total, long count, long missing, String type, String field, List<Bucket> terms,
            long cardinality, boolean multiClass) {
        this.total = total;
        this.missing = missing;
        this.type = type;
        this.field = field;
        this.count = count;
        this.terms = terms;
        this.cardinality = cardinality;
        this.multiClass = multiClass;
    }

    public static FieldStatistics from(Statistic stat, long total) {
        long missing = 0L;
        long cardinality = 0L;
        List<Bucket> terms = null;
        if (AGG_MISSING.equals(stat.getAggType())) {
            missing = stat.getNumericValue() == null ? 0L : stat.getNumericValue().longValue();
        } else if (AGG_CARDINALITY.equals(stat.getAggType())) {
            cardinality = stat.getNumericValue() == null ? 0L : stat.getNumericValue().longValue();
        } else if (AGG_TYPE_TERMS.equals(stat.getAggType())) {
            terms = stat.getValue();
        }

        return new FieldStatistics(total, total - missing, missing, stat.getType(), stat.getField(), terms, cardinality,
                false);
    }

    /**
     * Merging with another statistics too obtain missing values
     *
     * @param that {@link FieldStatistics} to merge
     * @return original {@link FieldStatistics} with values from input if taken
     */
    public FieldStatistics merge(FieldStatistics that) {
        this.total = Math.max(this.total, that.total);
        this.missing = Math.max(this.missing, that.missing);
        this.cardinality = Math.max(this.cardinality, that.cardinality);
        this.count = this.total - this.missing;
        this.terms = this.terms == null ? that.terms : this.terms;

        return this;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getMissing() {
        return missing;
    }

    public void setMissing(long missing) {
        this.missing = missing;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Bucket> getTerms() {
        return terms;
    }

    public void setTerms(List<Bucket> terms) {
        this.terms = terms;
    }

    public long getCardinality() {
        return cardinality;
    }

    public void setCardinality(long cardinality) {
        this.cardinality = cardinality;
    }

    public boolean isMultiClass() {
        return multiClass;
    }

    public void setMultiClass(boolean multiClass) {
        this.multiClass = multiClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FieldStatistics that = (FieldStatistics) o;
        return type.equals(that.type) && field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field);
    }
}
