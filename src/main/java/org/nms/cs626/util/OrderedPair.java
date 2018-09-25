package org.nms.cs626.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OrderedPair {
    private String left;
    private String right;


    public OrderedPair(String first, String second){
        left=first;
        right=second;
    }

    public OrderedPair reverse(){
        return new OrderedPair(right,left);
    }

    @Override
    public String toString() {
        return left+" "+right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedPair that = (OrderedPair) o;
        return this.left.equals(that.left) && this.right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left,right);
    }

}
