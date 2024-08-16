public class Main {
    public static void main(String[] args) {
        WelcomeScreen well = new WelcomeScreen();
        well.display();

        boolean validChoice = false;
        int level = 0;

        while (!validChoice) {
            validChoice = well.levelChoice();
            if (validChoice) {
                level = well.getChoice();
            } else {
                System.out.println("Invalid choice. Please choose a valid level.");
            }
        }

        // Run the chosen game level
        if (level == 1) {
            // Start Level1 game
            Level1.main(args);
        } else if (level == 2) {
            // Start BattleshipGame
            Level2.main(args);
        }
    }
}
