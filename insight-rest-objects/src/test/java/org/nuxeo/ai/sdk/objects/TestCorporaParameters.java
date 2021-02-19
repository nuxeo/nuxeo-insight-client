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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCorporaParameters {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        CorporaParameters parameters = new CorporaParameters();
        parameters.setQuery("SELECT * FROM Document WHERE dc:title = 'something'");

        HashSet<PropertyType> propertyTypes = new HashSet<>();
        propertyTypes.add(PropertyType.of("dc:title", DataType.TEXT));
        propertyTypes.add(PropertyType.of("file:content", DataType.IMAGE));
        parameters.setFields(propertyTypes);

        String serialized = MAPPER.writeValueAsString(parameters);
        assertThat(serialized).isNotEmpty().contains("file:content").contains("img");
    }

    @Test
    public void shouldDeserialize() throws URISyntaxException, IOException {
        URL resource = TestCorporaParameters.class.getClassLoader().getResource("corpora_params.json");
        assert resource != null;
        File json = new File(resource.toURI());
        CorporaParameters corporaParameters = MAPPER.readValue(json, CorporaParameters.class);
        assertThat(corporaParameters).isNotNull();
        assertThat(corporaParameters.getQuery()).isEqualTo("SELECT * FROM Document WHERE dc:title = 'something'");

        Set<PropertyType> fields = corporaParameters.getFields();
        assertThat(fields).hasSize(2);
        List<String> names = fields.stream().map(PropertyType::getName).collect(Collectors.toList());
        assertThat(names).contains("dc:title", "file:content");
    }
}