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

package org.nuxeo.ai;

import org.junit.Test;
import org.nuxeo.ai.client.Authentication;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAuthentication {

    @Test
    public void shouldCrateAuthentication() {
        Authentication basic = new Authentication("user", "password");
        assertThat(basic).isNotNull();
        assertThat(basic.getType()).isEqualTo(Authentication.TYPE.BASIC);

        Authentication token = new Authentication("token");
        assertThat(token).isNotNull();
        assertThat(token.getType()).isEqualTo(Authentication.TYPE.TOKEN);
    }
}
