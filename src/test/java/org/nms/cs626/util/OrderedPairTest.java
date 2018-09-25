package org.nms.cs626.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/*I think testing here will save me time in the end.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderedPairTest {

    @Test
    public void reverseTest(){
        OrderedPair forward = new OrderedPair("a","b");
        OrderedPair reverse = new OrderedPair("b","a");
        assertEquals(reverse,forward.reverse());
    }
}
