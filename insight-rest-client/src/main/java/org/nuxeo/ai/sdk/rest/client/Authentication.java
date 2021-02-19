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
package org.nuxeo.ai.sdk.rest.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Generic authentication configuration that supports authentications:
 * - Basic
 * - Token based
 */
public class Authentication {

    private final TYPE type;

    private final String username;

    private final String secret;

    public Authentication(@Nonnull String username, @Nonnull String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        this.username = username;
        this.secret = password;
        this.type = TYPE.BASIC;
    }

    public Authentication(@Nonnull String token) {
        Objects.requireNonNull(token);

        this.username = null;
        this.secret = token;
        this.type = TYPE.TOKEN;
    }

    @Nonnull
    public TYPE getType() {
        return type;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nonnull
    public String getSecret() {
        return secret;
    }

    public enum TYPE {
        BASIC, TOKEN
    }
}
