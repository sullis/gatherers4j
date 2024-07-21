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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AveragingBigDecimalGathererTest {

    @Test
    void averageOfBigDecimals() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0")
        );

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals())
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.3333333333333333")
                );
    }

    @Test
    void mathContextChange() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0")
        );

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().withMathContext(new MathContext(2)))
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.33")
                );
    }

    @Test
    void roundingModeChange() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0")
        );

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().withRoundingMode(RoundingMode.CEILING))
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.3333333333333334")
                );
    }


    @Test
    void averageOfZero() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(BigDecimal.ZERO, new BigDecimal("-1"), BigDecimal.ONE);

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals())
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        BigDecimal.ZERO,
                        new BigDecimal("-0.5"),
                        BigDecimal.ZERO
                );
    }

    @Test
    void treatNullAsZero() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(null, BigDecimal.ONE, null, BigDecimal.ONE);

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().treatNullAsZero())
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        BigDecimal.ZERO,
                        new BigDecimal("0.5"),
                        new BigDecimal("0.3333333333333333"),
                        new BigDecimal("0.5")
                );
    }

    @Test
    void ignoresNulls() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(null, BigDecimal.ONE, BigDecimal.TWO);

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals())
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        BigDecimal.ONE,
                        new BigDecimal("1.5")
                );
    }

    @Test
    void treatNullAsNonZero() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(null, BigDecimal.ONE, null, BigDecimal.ONE);

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().treatNullAs(BigDecimal.TEN))
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        BigDecimal.TEN,
                        new BigDecimal("5.5"),
                        new BigDecimal("7"),
                        new BigDecimal("5.5")
                );
    }

    @Test
    void simpleMovingAverageAverageOfBigDecimals() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().simpleMovingAverage(2))
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1.5"),
                        new BigDecimal("6"),
                        new BigDecimal("15"),
                        new BigDecimal("25")
                );
    }

    @Test
    void simpleMovingAverageAverageOfBigDecimalsWithPartials() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        // Act
        final List<BigDecimal> output = input
                .gather(Gatherers4j.averageBigDecimals().simpleMovingAverage(2).includePartialValues())
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("6"),
                        new BigDecimal("15"),
                        new BigDecimal("25")
                );
    }

    @Test
    void withOriginalBigDecimal() {
        // Arrange
        final Stream<BigDecimal> input = Stream.of(
                new BigDecimal("1.0"),
                new BigDecimal("2.0"),
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                new BigDecimal("30.0")
        );

        // Act
        final List<WithOriginal<BigDecimal, BigDecimal>> output = input
                .gather(Gatherers4j.averageBigDecimals().withOriginal())
                .toList();

        // Assert
        assertThat(output)
                .map(WithOriginal::calculated)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.3333333333333333"),
                        new BigDecimal("8.25"),
                        new BigDecimal("12.6")
                );

        assertThat(output)
                .map(WithOriginal::original)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1.0"),
                        new BigDecimal("2.0"),
                        new BigDecimal("10.0"),
                        new BigDecimal("20.0"),
                        new BigDecimal("30.0")
                );
    }

    @Test
    void withMappedField() {
        // Arrange
        final List<TestValueHolder> input = List.of(
                new TestValueHolder(1, new BigDecimal("1.0")),
                new TestValueHolder(2, new BigDecimal("2.0")),
                new TestValueHolder(3, new BigDecimal("10.0")),
                new TestValueHolder(4, new BigDecimal("20.0")),
                new TestValueHolder(5, new BigDecimal("30.0"))
        );

        // Act
        final List<BigDecimal> output = input.stream()
                .gather(Gatherers4j.averageBigDecimalsBy(TestValueHolder::value))
                .toList();

        // Assert
        assertThat(output)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.3333333333333333"),
                        new BigDecimal("8.25"),
                        new BigDecimal("12.6")
                );
    }

    @Test
    void withOriginalRecordByMappedField() {
        // Arrange
        final List<TestValueHolder> input = List.of(
                new TestValueHolder(1, new BigDecimal("1.0")),
                new TestValueHolder(2, new BigDecimal("2.0")),
                new TestValueHolder(3, new BigDecimal("10.0")),
                new TestValueHolder(4, new BigDecimal("20.0")),
                new TestValueHolder(5, new BigDecimal("30.0"))
        );

        // Act
        final List<WithOriginal<TestValueHolder, BigDecimal>> output = input.stream()
                .gather(Gatherers4j.averageBigDecimalsBy(TestValueHolder::value).withOriginal())
                .toList();

        // Assert
        assertThat(output)
                .map(WithOriginal::calculated)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(
                        new BigDecimal("1"),
                        new BigDecimal("1.5"),
                        new BigDecimal("4.3333333333333333"),
                        new BigDecimal("8.25"),
                        new BigDecimal("12.6")
                );

        assertThat(output)
                .map(WithOriginal::original)
                .containsExactlyInAnyOrderElementsOf(input);
    }

    @Test
    void roundingModeCannotBeNull() {
        assertThatThrownBy(() ->
                Stream.of(BigDecimal.ONE).gather(Gatherers4j.averageBigDecimals().withRoundingMode(null))
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void mathContextCannotBeNull() {
        assertThatThrownBy(() ->
                Stream.of(BigDecimal.ONE).gather(Gatherers4j.averageBigDecimals().withMathContext(null))
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void simpleMovingAverageInvalidRangeZero() {
        assertThatThrownBy(() ->
                Stream.of(BigDecimal.ONE).gather(Gatherers4j.averageBigDecimals().simpleMovingAverage(0))
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void simpleMovingAverageInvalidRangeNegative() {
        assertThatThrownBy(() ->
                Stream.of(BigDecimal.ONE).gather(Gatherers4j.averageBigDecimals().simpleMovingAverage(-1))
        ).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    record TestValueHolder(int id, BigDecimal value) {
    }
}