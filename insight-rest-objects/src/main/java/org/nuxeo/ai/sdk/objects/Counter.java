/*
 * (C) Copyright 2019 Nuxeo (http://nuxeo.com/) and others.
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
 *     Pedro Cardoso
 */
package org.nuxeo.ai.sdk.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Class to count Strings, with the possibility of merging Counts
 */
public class Counter extends HashMap<String, Long> {

    public Counter() {
        super();
    }

    public Counter(Map<String, Long> counts) {
        super(counts);
    }

    public void update(Counter counts) {
        for (Entry<String, Long> count : counts.entrySet()) {
            compute(count.getKey(), (key, val) -> (val == null) ? count.getValue() : val + count.getValue());
        }
    }

    public List<String> getKeysAboveCount(long minCount) {
        return entrySet().stream()
                         .filter(entry -> entry.getValue() >= minCount)
                         .map(Entry::getKey)
                         .collect(Collectors.toList());
    }

    public List<String> getKeysBellowCount(long count) {
        return entrySet().stream()
                         .filter(entry -> entry.getValue() < count)
                         .map(Entry::getKey)
                         .collect(Collectors.toList());
    }
}
