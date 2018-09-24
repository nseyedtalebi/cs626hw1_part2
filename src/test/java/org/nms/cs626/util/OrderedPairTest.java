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
    private OrderedPair forward;
    private OrderedPair reversed;
    private OrderedPair empty;
    private OrderedPair uut;

    @BeforeEach
    public void init(){
        forward = new OrderedPair("a","b");
        reversed = new OrderedPair("b","a");
        empty = new OrderedPair("","");
        uut = new OrderedPair("a","b");
    }

    @Test
    public void reverseWhenTrue_isTrue(){
        uut.executeWhenTrue((pair)->true,OrderedPair::reverse);
        assertEquals(reversed,uut);
    }

    @Test
    public void reverseWhenTrue_isFalse(){
        uut.executeWhenTrue((pair)->false,OrderedPair::reverse);
        assertEquals(forward,uut);
    }

    @Test
    public void AscendingPredicate_false(){
        assertEquals(false, OrderedPair.isAscending(reversed));
    }

    @Test
    public void AscendingPredicate_true(){
        assertEquals(true, OrderedPair.isAscending(forward));
    }

    @Test
    public void DescendingPredicate_false(){
        assertEquals(false,OrderedPair.isDescending(forward));
    }
    @Test
    public void DescendingPredicate_true(){
        assertEquals(true,OrderedPair.isDescending(reversed));
    }

    public Stream<Arguments> getTestPairs(){
        return Stream.of(
                Arguments.of(null,empty),
                Arguments.of(new String[]{},empty),
                Arguments.of(new String[]{"a"},new OrderedPair("a","")),
                Arguments.of(new String[]{"a","b"},forward),
                Arguments.of(new String[]{"a","b","c"},forward)
        );
    }
    @ParameterizedTest
    @MethodSource("getTestPairs")
    public void constructorTests(String[] inputArray, OrderedPair expected){
        assertEquals(expected,new OrderedPair(inputArray));
    }
}
