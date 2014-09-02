package net.scottdukes.dukesbox.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(JUnit4.class)
public class StringUtils_after_Tests {
    private static final String TEST_STRING = "foobarbaz";

    @Test
    public void shouldReturnTextAfterPrefix() {
        final String returnVal = StringUtils.after(TEST_STRING, "bar");
        assertThat(returnVal).isEqualTo("baz");
    }

    @Test
    public void shouldReturnOriginalStringWhenPrefixNotFound() {
        final String returnVal = StringUtils.after(TEST_STRING, "missing");
        assertThat(returnVal).isEqualTo(TEST_STRING);
    }

    @Test
    public void shouldReturnEmptyStringWhenPrefixAtEndOfString() {
        final String returnVal = StringUtils.after(TEST_STRING, "baz");
        assertThat(returnVal).isEmpty();
    }
}
