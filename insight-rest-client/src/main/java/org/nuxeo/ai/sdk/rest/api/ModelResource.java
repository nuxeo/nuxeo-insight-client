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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.nuxeo.ai.sdk.rest.client.API;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

public interface ModelResource extends Resource {

    @Nullable
    <T> T call(API.Model endpoint, Map<String, Serializable> parameters)
            throws JsonProcessingException;

    @Nullable
    <T> T call(API.Model endpoint, Map<String, Serializable> parameters, Serializable payload)
            throws JsonProcessingException;
}
