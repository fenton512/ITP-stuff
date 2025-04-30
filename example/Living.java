package org.example;

public interface Living {
    default void live(){
        System.out.println(getClass().getSimpleName() + " lives");
    }
}
