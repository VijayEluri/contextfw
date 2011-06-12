package net.contextfw.web.application.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

public class URLConverterTest {

    @Test
    @Ignore
    public void test1() {
        assertTrue(pattern("/test").matcher("/test").matches());
        assertFalse(pattern("/test").matcher("/test/").matches());
        assertTrue(pattern("/test/*.jpg").matcher("/test/jpoj.jpg").matches());
        assertFalse(pattern("/test/*.jpg").matcher("/test/jpoj/.jpg").matches());
        assertTrue(pattern("/test/\\d+.jpg").matcher("/test/12.jpg").matches());
    }
    
    private Pattern pattern(String url) {
        return Pattern.compile(URLConverter.toUrlPattern(url));
    }
}
