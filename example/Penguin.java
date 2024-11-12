package org.example;

public class Penguin implements Swimmable, Living{
    private boolean isSwimming = false;

    @Override
    public void swim(){
        System.out.println("Penguin is swimming");
        isSwimming = true;
    }

    public void stopSwimming(){
        if (isSwimming == true)
            System.out.println("Penguin is stop swimming");
        else
            System.out.println("the Penguin is not a swimming");
    }

}
