package org.ParseFrame;

public class Main {
    public static void main(String[] args) {
        double kilometersConstant = 1.609344;
        double myDouble = 234.923454;
        int myInt = 50; // a statement is a complete line
        // java statements can be assignment expressions.
        System.out.println(myDouble +
                "split in two");
        // java ignores whitespaces.
        // you can add as little or as much as you want.
        boolean gameOver = true;
        int score = 4000;
        int levelCompleted = 12;
        int bonus = 500;
        if(score == 4000) {
            System.out.println("You win!");
        } else {
            System.out.println("You lose!");
        }
        System.out.printf("The Value my double is: " + myDouble);

    }
}