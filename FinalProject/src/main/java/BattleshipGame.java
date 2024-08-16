import java.util.Scanner;

public class BattleshipGame {
    private static final int board_size = 10;
    private static final char empty_cell = '*';
    private static final char ship_cell = 'S';
    private static final char hit_cell = 'H';
    private static final char miss_cell = 'M';
    private static final int ship_size = 3;

    private static BattleShip player1Board = new BattleShip();
    private static BattleShip player2Board = new BattleShip();
    private static String player1Name;
    private static String player2Name;

    private static int player1ShipsRemaining = 3;
    private static int player2ShipsRemaining = 3;

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

        if (level == 2) {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Player 1 name: ");
            player1Name = scanner.nextLine();
            System.out.print("Enter Player 2 name: ");
            player2Name = scanner.nextLine();

            // Display empty boards
            System.out.println(player1Name + ", here is your empty board:");
            player1Board.dispBoard(false);
            System.out.println(player2Name + ", here is your empty board:");
            player2Board.dispBoard(false);

            System.out.println(player1Name + ", place your ships:");
            placeShips(scanner, player1Board);
            System.out.println(player2Name + ", place your ships:");
            placeShips(scanner, player2Board);

            // Show boards with ships placed
            System.out.println(player1Name + ", your board with ships placed:");
            player1Board.dispBoard(true);
            System.out.println(player2Name + ", your board with ships placed:");
            player2Board.dispBoard(true);

            boolean gameOver = false;
            while (!gameOver) {
                gameOver = playerTurn(scanner, player1Name, player2Board, player2Name);
                if (!gameOver) {
                    gameOver = playerTurn(scanner, player2Name, player1Board, player1Name);
                }
            }
            scanner.close();
        }
    }

    private static void placeShips(Scanner scanner, BattleShip board) {
        for (int shipNum = 1; shipNum <= 3; shipNum++) {
            boolean validPlacement = false;
            while (!validPlacement) {
                System.out.print("Enter coordinates for ship " + shipNum + " (e.g., 1,1 1,2 1,3): ");
                String[] coordinates = scanner.nextLine().split(" ");
                validPlacement = placeShip(board, coordinates);
                if (!validPlacement) {
                    System.out.println("Invalid ship placement. Coordinates must be consecutive and within the board. Try again.");
                }
            }
        }
    }

    private static boolean placeShip(BattleShip board, String[] coordinates) {
        if (coordinates.length != ship_size) return false;

        int[][] parsedCoordinates = new int[ship_size][2];

        for (int i = 0; i < ship_size; i++) {
            String[] coord = coordinates[i].split(",");
            if (coord.length != 2) return false;

            int x, y;
            try {
                x = Integer.parseInt(coord[0]) - 1;
                y = Integer.parseInt(coord[1]) - 1;
                if (x < 0 || x >= board_size || y < 0 || y >= board_size || board.getCell(x, y) != empty_cell) {
                    return false;
                }
                parsedCoordinates[i][0] = x;
                parsedCoordinates[i][1] = y;
            } catch (NumberFormatException e) {
                return false; // Invalid number format
            }
        }

        // Check if coordinates are consecutive
        boolean isValid = true;
        boolean isHorizontal = parsedCoordinates[0][0] == parsedCoordinates[1][0];
        boolean isVertical = parsedCoordinates[0][1] == parsedCoordinates[1][1];

        for (int i = 1; i < ship_size; i++) {
            if (isHorizontal) {
                if (parsedCoordinates[i][0] != parsedCoordinates[i - 1][0] || parsedCoordinates[i][1] != parsedCoordinates[i - 1][1] + 1) {
                    isValid = false;
                    break;
                }
            } else if (isVertical) {
                if (parsedCoordinates[i][1] != parsedCoordinates[i - 1][1] || parsedCoordinates[i][0] != parsedCoordinates[i - 1][0] + 1) {
                    isValid = false;
                    break;
                }
            } else {
                isValid = false;
                break;
            }
        }

        if (!isValid) return false;

        for (int i = 0; i < ship_size; i++) {
            board.placeMark(parsedCoordinates[i][0], parsedCoordinates[i][1], ship_cell); // Mark ships as 'S'
        }
        return true;
    }

    private static boolean playerTurn(Scanner scanner, String currentPlayerName, BattleShip opponentBoard, String opponentName) {
        boolean validAttack = false;
        while (!validAttack) {
            System.out.print(currentPlayerName + ", enter the coordinates to attack (e.g., 1,1): ");
            String[] input = scanner.nextLine().split(",");
            if (input.length != 2) {
                System.out.println("Invalid input format. Please enter coordinates as row,column.");
                continue;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(input[0]) - 1;
                y = Integer.parseInt(input[1]) - 1;
                if (x < 0 || x >= board_size || y < 0 || y >= board_size) {
                    System.out.println("Coordinates out of bounds. Please enter values between 1 and " + board_size + ".");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter valid integers.");
                continue;
            }

            if (opponentBoard.getCell(x, y) == ship_cell) { // Check for ship directly
                System.out.println("Hit!");
                opponentBoard.placeMark(x, y, hit_cell);
                if (checkForSunkShip(opponentBoard, x, y)) {
                    // Correctly update remaining ships count
                    if (currentPlayerName.equals(player1Name)) {
                        player2ShipsRemaining--;
                    } else {
                        player1ShipsRemaining--;
                    }
                    System.out.println("You sunk a ship! " + getRemainingShips(opponentName) + " ships remaining.");
                    if (getRemainingShips(opponentName) == 0) {
                        System.out.println("Congratulations! " + currentPlayerName + " wins!");
                        return true; // Game over
                    }
                } else {
                    System.out.println("You hit a ship!");
                }
            } else {
                System.out.println("Miss!");
                opponentBoard.placeMark(x, y, miss_cell);
            }
            validAttack = true;
            // Show the updated board
            System.out.println(opponentName + "'s board after attack:");
            opponentBoard.dispBoard(false);
        }
        return false; // Game not over
    }

    private static boolean checkForSunkShip(BattleShip board, int hitX, int hitY) {
        return checkDirection(board, hitX, hitY, 1, 0) || // Horizontal
                checkDirection(board, hitX, hitY, 0, 1);  // Vertical
    }

    private static boolean checkDirection(BattleShip board, int hitX, int hitY, int dx, int dy) {
        int hits = 0;

        // Check in both directions from the hit point
        for (int i = -2; i <= 2; i++) {
            int x = hitX + i * dx;
            int y = hitY + i * dy;

            if (x >= 0 && x < board_size && y >= 0 && y < board_size) {
                if (board.getCell(x, y) == hit_cell) {
                    hits++;
                } else if (board.getCell(x, y) == 'S') { // Check for remaining ship cells
                    return false; // Not all parts of the ship have been hit
                }
            }
        }
        return hits == ship_size; // The ship is sunk if all parts have been hit
    }

    private static int getRemainingShips(String playerName) {
        if (playerName.equals(player1Name)) {
            return player1ShipsRemaining;
        } else {
            return player2ShipsRemaining;
        }
    }
}