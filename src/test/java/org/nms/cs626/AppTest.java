package org.nms.cs626;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.nms.cs626.util.OrderedPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppTest {
    @Mock
    private Mapper.Context mockContext;
    @Mock
    private Counter mockCounter;

    private IntWritable one = new IntWritable(1);
    private App myApp;
    private App.Map myMap;
    private App.Reduce myReduce;
    //Found useful example of how to mock mapreduce stuff here:
    //https://www.baeldung.com/mockito-argument-matchers
    private ArgumentCaptor<OrderedPair> orderedPairCaptor = ArgumentCaptor.forClass(OrderedPair.class);
    private ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
    private static OrderedPair testPair = new OrderedPair("C1585558","C1699312");



    @BeforeEach
    public void init() throws IOException, InterruptedException {
        initMocks(this);
        myApp = new App();
        myMap = new App.Map();
        myReduce = new App.Reduce();
        doNothing().when(mockContext).write(orderedPairCaptor.capture(),any(IntWritable.class));
        doNothing().when(mockContext).write(orderedPairCaptor.capture(),longCaptor.capture());
        doNothing().when(mockCounter).increment(anyLong());
    }
    public static Stream<Arguments> getMapTestArgs(){
        //LongWritable offset, Text lineText, Mapper.Context context
        return Stream.of(
            Arguments.of("\"C1699312\",\"C1585558\"",testPair),
            Arguments.of("\"C1585558\",\"C1699312\"",testPair),
            Arguments.of("C1585558,C1699312",testPair)
        );
    }

    @ParameterizedTest
    @MethodSource("getMapTestArgs")
    public void mapTest(String inputLine,OrderedPair expected) throws IOException, InterruptedException{
        myMap.map(new LongWritable(0),new Text(inputLine),mockContext);
        assertEquals(expected, orderedPairCaptor.getValue());
    }

    public static Stream<Arguments> getReduceTestArgs(){
        return Stream.of(
                Arguments.of(testPair, Arrays.asList(new int[]{1}),1),
                Arguments.of(testPair,Arrays.asList(new int[]{1,2}),3),
                Arguments.of(testPair.reverse(),Arrays.asList(new int[]{1}),1)
        );
    }

    @ParameterizedTest
    @MethodSource("getReduceTestArgs")
    public void reduceTest(OrderedPair keyin, Iterable<IntWritable> valuesIn,int expectedOutput){
        myReduce.reduce(keyin, valuesIn, mockContext);
        assertEquals(expectedOutput,(long)longCaptor.getValue());
        assertEquals(keyin,orderedPairCaptor.getValue());
    }

    @ParameterizedTest
    @MethodSource("getReduceTestArgs")
    public void reduceCountersTest(OrderedPair keyin, Iterable<IntWritable> valuesIn,int expectedOutput){
        myReduce.reduce(keyin, valuesIn, mockContext);
        verify(mockCounter,times(1)).increment(1);
    }
}
