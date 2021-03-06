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
package org.nuxeo.ai.sdk.objects;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Represents parameters to build and collect corpora on Insight Cloud
 * ___________________________________________________________________
 * Example:
 * <pre>
 * {
 *   "fields": [
 *     {
 *       "name": "file:content",
 *       "type": "img"
 *     },
 *     {
 *       "name": "dc:title",
 *       "type": "txt"
 *     }
 *   ],
 *   "query": "SELECT * FROM Document WHERE dc:title = 'something'"
 * }
 * </pre>
 */
public class CorporaParameters implements Serializable {

    private static final long serialVersionUID = 603202103827L;

    protected Set<PropertyType> fields;

    protected String query;

    public CorporaParameters() {
    }

    public Set<PropertyType> getFields() {
        return fields;
    }

    public void setFields(Set<PropertyType> fields) {
        this.fields = fields;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CorporaParameters that = (CorporaParameters) o;
        return fields.equals(that.fields) && Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, query);
    }
}
