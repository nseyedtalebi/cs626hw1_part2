package org.nms.cs626.util;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.util.Objects;

public class MapperOutputPair {
    public final Text key;
    public final IntWritable value;

    public MapperOutputPair(Text key, IntWritable value){
        this.key = key;
        this.value = value;
    }

    public MapperOutputPair(String key, IntWritable value) {
        this.key = new Text(key);
        this.value = value;
    }

    public MapperOutputPair(String key, int value) {
        this(key, new IntWritable(value));
    }

    public MapperOutputPair(Object[] args) {
        key =  (Text)args[0];
        value = (IntWritable) args[1];
    }

    @Override
    public String toString() {
        return "MapperOutputPair{" + "key='" + key + '\'' + ", value=" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapperOutputPair mapperOutputPair = (MapperOutputPair) o;
        return Objects.equals(key, mapperOutputPair.key) && Objects.equals(value, mapperOutputPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
