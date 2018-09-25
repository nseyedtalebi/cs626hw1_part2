package org.nms.cs626;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.map.WrappedMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nms.cs626.util.OrderedPair;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AppTest {
    @Mock
    private Mapper.Context mockContext;

    private IntWritable one = new IntWritable(1);
    private App myApp;
    private App.Map myMap;
    @BeforeEach
    public void init(){
        initMocks(this);
        myApp = new App();
        myMap = new App.Map();
    }
    public static Stream<Arguments> getMapTestArgs(){
        //LongWritable offset, Text lineText, Mapper.Context context
        return Stream.of(
            Arguments.of("\"C1699312\",\"C1585558\"",new OrderedPair("C1585558","C1699312")),
            Arguments.of("\"C1585558\",\"C1699312\"",new OrderedPair("C1585558","C1699312")),
            Arguments.of("C1585558,C1699312",new OrderedPair("C1585558","C1699312"))
        );
    }

    @ParameterizedTest
    @MethodSource("getMapTestArgs")
    public void mapTest(String inputLine,OrderedPair expected) throws IOException, InterruptedException{
        //Found useful example of how to mock mapreduce stuff here:
        //https://www.baeldung.com/mockito-argument-matchers
        ArgumentCaptor<OrderedPair> outputCaptor = ArgumentCaptor.forClass(OrderedPair.class);
        doNothing().when(mockContext).write(outputCaptor.capture(),any(IntWritable.class));
        myMap.map(new LongWritable(0),new Text(inputLine),mockContext);
        assertEquals(expected,outputCaptor.getValue());
    }
}
