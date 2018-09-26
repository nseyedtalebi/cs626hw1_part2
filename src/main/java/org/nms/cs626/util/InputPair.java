package org.nms.cs626.util;

import org.apache.hadoop.io.IntWritable;

import java.util.Objects;

public class InputPair {
    public final String key;
    public final IntWritable value;

    public InputPair(String key, IntWritable value) {
        this.key = key;
        this.value = value;
    }

    public InputPair(String key, int value) {
        this(key, new IntWritable(value));
    }

    public InputPair(Object[] args) {
        key = (String) args[0];
        value = (IntWritable) args[1];
    }

    @Override
    public String toString() {
        return "InputPair{" + "key='" + key + '\'' + ", value=" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputPair inputPair = (InputPair) o;
        return Objects.equals(key, inputPair.key) && Objects.equals(value, inputPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
