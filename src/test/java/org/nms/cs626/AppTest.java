package org.nms.cs626;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.join.TupleWritable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.lf5.util.StreamUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.nms.cs626.util.InputPair;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppTest {
    @Mock
    private Mapper.Context mockMapperContext;
    @Mock
    private Reducer.Context mockReducerContext;
    @Mock
    private Counter mockCounter;

    private App myApp;
    private App.Map myMap;
    private App.Reduce myReduce;
    //Found useful example of how to mock mapreduce stuff here:
    //https://www.baeldung.com/mockito-argument-matchers
    //private ArgumentCaptor<OrderedPair> orderedPairCaptor = ArgumentCaptor.forClass(OrderedPair.class);
    private ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
    private ArgumentCaptor<IntWritable> intWritableCaptor = ArgumentCaptor.forClass(IntWritable.class);
    //private static OrderedPair testPair = new OrderedPair("C1585558","C1699312");
    private static IntWritable one = new IntWritable(1);
    private static IntWritable negone = new IntWritable(-1);



    @BeforeEach
    public void init() throws IOException, InterruptedException {
        initMocks(this);
        myApp = new App();
        myMap = new App.Map();
        myReduce = new App.Reduce();

        doNothing().when(mockMapperContext).write(stringCaptor.capture(),intWritableCaptor.capture());
        //doNothing().when(mockReducerContext).write(orderedPairCaptor.capture(), intWritableCaptor.capture());
        //doReturn(mockCounter).when(mockReducerContext).getCounter(App.Reduce.CountersEnum.class.getName()
        //,App.Reduce.CountersEnum.UNIQUE_OUTPUT_PAIRS.toString());
        //doNothing().when(mockCounter).increment(anyLong());
    }


    public static Stream<Arguments> getMapTestArgs(){
        //LongWritable offset, Text lineText, Mapper.Context context
        return Stream.of(Arguments.of("\"C1699312\",\"C1585558\"",Arguments.of("C1699312",negone),Arguments.of("C1585558",one)),
                         Arguments.of("\"C1585558\",\"C1699312\"",Arguments.of("C1585558",negone),Arguments.of("C1699312",one)),
                         Arguments.of("\"C2345799\",\"C0718399\"",Arguments.of("C2345799",negone),Arguments.of("C0718399",one))
        );
    }

    @ParameterizedTest
    @MethodSource("getMapTestArgs")
    public void mapTest(String inputLine,Arguments firstPairArgs, Arguments secondPairArgs)
            throws IOException, InterruptedException{
        InputPair firstPair = new InputPair(firstPairArgs.get());
        InputPair secondPair = new InputPair(secondPairArgs.get());
        myMap.map(new LongWritable(0),new Text(inputLine), mockMapperContext);
        List<String> resultKeys = stringCaptor.getAllValues();
        List<IntWritable> resultValues = intWritableCaptor.getAllValues();
        List<InputPair> resultPairs = new ArrayList<>();
        for(int i=0;i<2;i++){
            resultPairs.add(new InputPair(resultKeys.get(i),resultValues.get(i)));
        }
        assertEquals(firstPair,resultPairs.get(0));
        assertEquals(secondPair,resultPairs.get(1));
    }

    public static Stream<Arguments> getReduceTestArgs(){
        return Stream.of();
    }

    @ParameterizedTest
    @MethodSource("getReduceTestArgs")
    public void reduceTest(String keyin, Iterable<IntWritable> valuesIn,IntWritable expectedOutput)
    throws IOException, InterruptedException {
        myReduce.reduce(keyin, valuesIn, mockReducerContext);

    }

    @ParameterizedTest
    @MethodSource("getReduceTestArgs")
    public void reduceCountersTest(String keyin, Iterable<IntWritable> valuesIn)
    throws IOException, InterruptedException {
        myReduce.reduce(keyin, valuesIn, mockReducerContext);
        verify(mockCounter,times(1)).increment(1);
    }
}
