import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Level1 {

    private static final int BOARD_SIZE = 10;
    private static final char EMPTY_CELL = '*';
    private static final char SHIP_CELL = 'S';
    private static final char HIT_CELL = 'H';
    private static final char MISS_CELL = 'M';
    private static final int SHIP_SIZE = 3;
    private static final int MAX_MOVES = 40; // 10 for human 10 for computer

    private static BattleShip playerBoard = new BattleShip();
    private static BattleShip computerBoard = new BattleShip();
    private static String playerName = "Player";
    private static final String COMPUTER_NAME = "Computer";

    private static int playerShipsRemaining = 3;
    private static int computerShipsRemaining = 3;

    private static int moveCount = 0; // Track the number of moves
    private static int pointsh = 0;
    private static int pointsC = 0;
    public static SoundPlayer music = new SoundPlayer();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get player name
        System.out.print("Enter your name: ");
        playerName = scanner.nextLine();

        // Place ships
        System.out.println(playerName + ", place your ships:");
        placeShips(scanner, playerBoard);
        placeComputerShips();

        // Start game
        boolean gameOver = false;
        while (moveCount < MAX_MOVES && !gameOver) {

            gameOver = playerTurn(scanner);
            if (!gameOver && moveCount < MAX_MOVES) {
                gameOver = computerTurn();
            }
            System.out.println("Moves left: " + ((MAX_MOVES - moveCount)/2));

        }

        if (!gameOver) {
            System.out.println("Game over! Maximum number of moves reached.");
        }
        Leader();
        scanner.close();
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

    private static void placeComputerShips() {
        Random rand = new Random();
        int shipsPlaced = 0;
        while (shipsPlaced < 3) {
            int x = rand.nextInt(BOARD_SIZE);
            int y = rand.nextInt(BOARD_SIZE);
            boolean horizontal = rand.nextBoolean();
            if (placeComputerShip(computerBoard, x, y, horizontal)) {
                shipsPlaced++;
            }
        }
    }

    private static boolean placeComputerShip(BattleShip board, int x, int y, boolean horizontal) {
        int endX = horizontal ? x : x + SHIP_SIZE - 1;
        int endY = horizontal ? y + SHIP_SIZE - 1 : y;
        if (endX >= BOARD_SIZE || endY >= BOARD_SIZE) return false;

        for (int i = 0; i < SHIP_SIZE; i++) {
            int cx = horizontal ? x : x + i;
            int cy = horizontal ? y + i : y;
            if (board.getCell(cx, cy) != EMPTY_CELL) return false;
        }

        for (int i = 0; i < SHIP_SIZE; i++) {
            int cx = horizontal ? x : x + i;
            int cy = horizontal ? y + i : y;
            board.placeMark(cx, cy, SHIP_CELL);
        }

        return true;
    }

    private static boolean placeShip(BattleShip board, String[] coordinates) {
        if (coordinates.length != SHIP_SIZE) return false;

        int[][] parsedCoordinates = new int[SHIP_SIZE][2];

        for (int i = 0; i < SHIP_SIZE; i++) {
            String[] coord = coordinates[i].split(",");
            if (coord.length != 2) return false;

            int x, y;
            try {
                x = Integer.parseInt(coord[0]) - 1;
                y = Integer.parseInt(coord[1]) - 1;
                if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE || board.getCell(x, y) != EMPTY_CELL) {
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

        for (int i = 1; i < SHIP_SIZE; i++) {
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

        for (int i = 0; i < SHIP_SIZE; i++) {
            board.placeMark(parsedCoordinates[i][0], parsedCoordinates[i][1], SHIP_CELL);
        }
        return true;
    }

    private static boolean playerTurn(Scanner scanner) {
        boolean validAttack = false;
        while (!validAttack) {
            System.out.print(playerName + ", enter the coordinates to attack (e.g., 1,1): ");
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
                if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
                    System.out.println("Coordinates out of bounds. Please enter values between 1 and " + BOARD_SIZE + ".");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter valid integers.");
                continue;
            }

            if (computerBoard.getCell(x, y) == SHIP_CELL) {
                System.out.println("Hit!");
                pointsh++;
                computerBoard.placeMark(x, y, HIT_CELL);
                if (checkForSunkShip(computerBoard, x, y)) {
                    computerShipsRemaining--;
                    System.out.println("You sunk a ship! " + computerShipsRemaining + " ships remaining.");
                    music.playSound("C:\\Users\\dell\\Downloads\\victorymale-version-230553.wav");
                    pointsh += 5;
                    if (computerShipsRemaining == 0) {
                        System.out.println(playerName + " wins!");
                        return true;
                    }
                }
            } else if (computerBoard.getCell(x, y) == EMPTY_CELL) {
                System.out.println("Miss!");
                computerBoard.placeMark(x, y, MISS_CELL);
            } else {
                System.out.println("You already attacked this coordinate. Try again.");
                continue;
            }
            moveCount++;

            // Display the grids after the player's turn
            System.out.println("\n" + playerName + "'s Board:");
            playerBoard.dispBoard(false); // Show player's own board without ships
            System.out.println("\n" + COMPUTER_NAME + "'s Board:");
            computerBoard.dispBoard(false); // Show computer's board with hits/misses

            validAttack = true;
        }

        return false;
    }

    private static boolean computerTurn() {
        Random rand = new Random();
        boolean validAttack = false;
        while (!validAttack) {
            int x = rand.nextInt(BOARD_SIZE);
            int y = rand.nextInt(BOARD_SIZE);

            if (playerBoard.getCell(x, y) == SHIP_CELL) {
                System.out.println("Computer hit your ship at " + (x + 1) + "," + (y + 1) + "!");
                pointsC++;
                playerBoard.placeMark(x, y, HIT_CELL);
                if (checkForSunkShip(playerBoard, x, y)) {
                    playerShipsRemaining--;
                    System.out.println("The computer sunk one of your ships! You have " + playerShipsRemaining + " ships remaining.");
                    pointsC += 5;
                    if (playerShipsRemaining == 0) {
                        System.out.println("Computer wins!");
                        music.playSound("C:\\Users\\dell\\Downloads\\victorymale-version-230553.wav");
                        return true;
                    }
                }
            } else if (playerBoard.getCell(x, y) == EMPTY_CELL) {
                System.out.println("Computer missed at " + (x + 1) + "," + (y + 1) + ".");
                playerBoard.placeMark(x, y, MISS_CELL);
            } else {
                continue;
            }
            moveCount++;

            // Display the grids after the computer's turn
            System.out.println("\n" + playerName + "'s Board:");
            playerBoard.dispBoard(false); // Show player's own board with hits/misses
            System.out.println("\n" + COMPUTER_NAME + "'s Board:");
            computerBoard.dispBoard(false); // Show computer's board with hits/misses

            validAttack = true;
        }

        return false;
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

            if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
                if (board.getCell(x, y) == HIT_CELL) {
                    hits++;
                } else if (board.getCell(x, y) == SHIP_CELL) { // Check for remaining ship cells
                    return false; // Not all parts of the ship have been hit
                }
            }
        }
        return hits == SHIP_SIZE; // The ship is sunk if all parts have been hit
    }
    public static void Leader(){
        LeaderBoard Lead = new LeaderBoard();
        Lead.addOrUpdatePlayer(playerName,pointsh);
        Lead.addOrUpdatePlayer(COMPUTER_NAME,pointsC);
        Lead.displayLeaderboard();
    }
}
