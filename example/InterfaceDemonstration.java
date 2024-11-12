package org.example;

public class InterfaceDemonstration {
    public static void main(String[] args) {
        Living[] animals = new Living[2];
        Penguin penguin = new Penguin();
        Duck duck = new Duck();
        animals[0] = penguin;
        animals[1] = duck;
        for (Living living: animals){
            living.live();
        }
    }
}