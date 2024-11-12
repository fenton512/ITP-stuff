package org.example;

public class Duck implements Flyable, Living, Swimmable{
    private boolean isSwimming = false;
    public static final String NAME = "Duck";
    private boolean isFlying = false;
    private boolean isLiving = false;
    @Override
    public void swim(){
        System.out.println("Duck is swimming");
        isSwimming = true;
    }

    public void stopSwimming(){
        if (isSwimming == true)
            System.out.println("Duck is stop swimming");
        else
            System.out.println("the Duck is not a swimming");
    }

    @Override
    public void fly() {
        System.out.println("Duck is flying");
        isFlying = true;
    }

    public void stopFlying(){
        if (isFlying == true)
            System.out.println("Duck is stop flying");
        else
            System.out.println("the Duck is not a flying");
    }




}
