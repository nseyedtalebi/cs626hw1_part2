package org.nms.cs626.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OrderedPair {
    private String[] values;

    public OrderedPair(String first, String second){
        values = new String[]{"",""};
        values[0] = first == null ? "" : first;
        values[1] = second == null ? "" : second;
    }

    public OrderedPair(String[] inputArray){
        values = new String[]{"",""};
        if(inputArray == null) return;
        values = Arrays.copyOfRange(inputArray,0,2);
        for(int i=0; i < values.length; i++){
            if(values[i] == null) values[i]="";
        }
    }

    public void executeWhenTrue(Predicate<OrderedPair> orderBy, Consumer<OrderedPair> action){
        if(!orderBy.test(this)) return;
        action.accept(this);
    }

    public static boolean isAscending(OrderedPair p){
        return p.values[0].compareTo(p.values[1]) < 0;
    }

    public static boolean isDescending(OrderedPair p){
        return p.values[0].compareTo(p.values[1]) > 0;
    }

    public void reverse(){
        String tmp = values[0];
        values[0] = values[1];
        values[1] = tmp;
    }

    @Override
    public String toString() {
        return "OrderedPair{" + "values=" + Arrays.toString(values) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedPair that = (OrderedPair) o;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public OrderedPair copy(){
        return new OrderedPair(this.values);
    }
}
