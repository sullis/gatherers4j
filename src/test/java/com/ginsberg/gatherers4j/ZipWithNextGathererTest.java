/*
 * Copyright 2024 Todd Ginsberg
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
 */

package com.ginsberg.gatherers4j;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ZipWithNextGathererTest {

    @Test
    void zipWithNext() {
        // Arrange
        final Stream<String> input = Stream.of("A", "B", "C", "D", "E");

        // Act
        final List<List<String>> output = input
                .gather(Gatherers4j.zipWithNext())
                .toList();

        // Assert
        assertThat(output)
                .containsExactly(
                        List.of("A", "B"),
                        List.of("B", "C"),
                        List.of("C", "D"),
                        List.of("D", "E")
                );
    }


    @Test
    void zipWithNextIncludingNulls() {
        // Arrange
        final Stream<String> input = Stream.of("A", null, "C", null);

        // Act
        final List<List<String>> output = input
                .gather(Gatherers4j.zipWithNext())
                .toList();

        // Assert
        assertThat(output)
                .containsExactly(
                        Arrays.asList("A", null),
                        Arrays.asList(null, "C"),
                        Arrays.asList("C", null)
                );
    }

    @Test
    void zipWithNextSingleElementProducesNothing() {
        // Arrange
        final Stream<String> input = Stream.of("A");

        // Act
        final List<List<String>> output = input
                .gather(Gatherers4j.zipWithNext())
                .toList();

        // Assert
        assertThat(output).isEmpty();
    }

}