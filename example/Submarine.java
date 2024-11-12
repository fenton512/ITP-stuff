package org.example;

public class Submarine implements Swimmable{
    private boolean isSwimming = false;
    @Override
    public void swim(){
        System.out.println("Submarine is swimming");
        isSwimming = true;
    }

    public void stopSwimming(){
        if (isSwimming == true)
            System.out.println("submarine is stop swimming");
        else
            System.out.println("the submarine is not a swimming");
    }
}
