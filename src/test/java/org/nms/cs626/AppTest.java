package org.nms.cs626;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.nms.cs626.util.MapperOutputPair;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppTest {
    @Mock
    private Mapper.Context mockMapperContext;
    @Mock
    private Reducer.Context mockReducerContext;

    private App.Map myMap;
    private App.Reduce myReduce;
    //Found useful example of how to mock mapreduce stuff here:
    //https://www.baeldung.com/mockito-argument-matchers

    private ArgumentCaptor<Text> mapperOutputTextCaptor = ArgumentCaptor.forClass(Text.class);
    private ArgumentCaptor<Text> reducerOutputTextCaptor = ArgumentCaptor.forClass(Text.class);
    private ArgumentCaptor<IntWritable> mapperOutputIntWritableCaptor = ArgumentCaptor.forClass(IntWritable.class);
    private ArgumentCaptor<IntWritable> reducerOutputIntWritableCaptor = ArgumentCaptor.forClass(IntWritable.class);

    private static IntWritable one = new IntWritable(1);
    private static IntWritable negone = new IntWritable(-1);
    private static IntWritable two = new IntWritable(2);



    @BeforeEach
    public void init() throws IOException, InterruptedException {
        initMocks(this);
        myMap = new App.Map();
        myReduce = new App.Reduce();

        doNothing().when(mockMapperContext).write(mapperOutputTextCaptor.capture(), mapperOutputIntWritableCaptor.capture());
        doNothing().when(mockReducerContext).write(reducerOutputTextCaptor.capture(), reducerOutputIntWritableCaptor.capture());
    }


    public static Stream<Arguments> getMapTestArgs(){
        return Stream.of(Arguments.of("\"C1699312\",\"C1585558\"",new MapperOutputPair("C1699312",negone),new MapperOutputPair("C1585558",one)),
                         Arguments.of("\"C1585558\",\"C1699312\"",new MapperOutputPair("C1585558",negone),new MapperOutputPair("C1699312",one)),
                         Arguments.of("\"C2345799\",\"C0718399\"",new MapperOutputPair("C2345799",negone),new MapperOutputPair("C0718399",one))
        );
    }

    @ParameterizedTest
    @MethodSource("getMapTestArgs")
    public void mapTest(String inputLine,MapperOutputPair firstPair, MapperOutputPair secondPair)
            throws IOException, InterruptedException{
        myMap.map(new LongWritable(0),new Text(inputLine), mockMapperContext);
        List<Text> resultKeys = mapperOutputTextCaptor.getAllValues();
        List<IntWritable> resultValues = mapperOutputIntWritableCaptor.getAllValues();
        List<MapperOutputPair> resultPairs = new ArrayList<>();
        for(int i=0;i<2;i++){
            resultPairs.add(new MapperOutputPair(resultKeys.get(i),resultValues.get(i)));
        }
        System.out.println(firstPair);
        System.out.println(secondPair);
        assertEquals(firstPair,resultPairs.get(0));
        assertEquals(secondPair,resultPairs.get(1));
    }

    public static Stream<Arguments> getReduceTestArgs(){
        return Stream.of(
                Arguments.of(new Text("C1699312"), Arrays.asList(negone,one),null,null),
                Arguments.of(new Text("C1585558"), Arrays.asList(one,negone),null,null),
                Arguments.of(new Text("C2345799"), Arrays.asList(negone),null,null),
                Arguments.of(new Text("C0718399"), Arrays.asList(one),new Text("C0718399"),one),
                Arguments.of(new Text("C0718399"), Arrays.asList(one,one),new Text("C0718399"),two)
        );
    }

    @ParameterizedTest
    @MethodSource("getReduceTestArgs")
    public void reduceTest(Text keyin, Iterable<IntWritable> valuesIn,Text expectedKey,IntWritable expectedValue)
    throws IOException, InterruptedException {
        myReduce.reduce(keyin, valuesIn, mockReducerContext);
        Text actualKey = (expectedKey != null) ? reducerOutputTextCaptor.getValue() : null;
        IntWritable actualValue = (expectedValue != null) ? reducerOutputIntWritableCaptor.getValue() : null;
        assertEquals(expectedKey, actualKey);
        assertEquals(expectedValue, actualValue);
    }

}
