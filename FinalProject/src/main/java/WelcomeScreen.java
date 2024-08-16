import java.util.Scanner;

public class WelcomeScreen {
    private int choice;

    public void display() {
        System.out.println("                                          走: 走: 走: 走: 走: 走: 走: 走: 走: 走: 走: 走 ");
        System.out.println("                                          走      WELCOME TO MY BATTLEFIELD       走 ");
        System.out.println("                                          走: 走: 走: 走: 走: 走: 走: 走: 走: 走: 走: 走 ");
        System.out.println();
    }

    public boolean levelChoice() {
        System.out.println("                                                      CHOOSE LEVEL ");
        System.out.println("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^                                                                ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^");
        System.out.println("<        Level 1        >                                 or                             <          Level 2             >");
        System.out.println("<  Compete with our AI  >                                                                <    Compete with Human Brain  >");
        System.out.println("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^                                                                ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^");

        System.out.println("Your choice is:");
        Scanner scan = new Scanner(System.in);
        choice = scan.nextInt();

        return setChoice(choice);  // Returns true if the choice is valid
    }

    public int getChoice() {
        return choice;
    }

    public boolean setChoice(int userChoice) {
        if (userChoice == 1 || userChoice == 2) {
            choice = userChoice;
            if (choice == 1) {
                System.out.println("You chose Level 1.");
            } else {
                System.out.println("You chose Level 2.");
            }
            return true;
        } else {
            System.out.println("Level doesn't exist.");
            return false;
        }
    }
}
