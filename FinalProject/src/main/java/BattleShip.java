public class BattleShip {
    private char[][] board;
    private final char empty_cell = '*';
    private final char ship_cell = 'S';
    private final char hit_cell = 'H';
    private final char miss_cell = 'M';

    public BattleShip() {
        board = new char[10][10];
        initBoard();
    }

    void initBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = empty_cell;
            }
        }
    }

    void dispBoard(boolean showShips) {
        int i, j;

        // Print column headers
        System.out.print("   ");
        for (i = 0; i < board.length; i++) {
            System.out.print((i+1) + "   ");
        }
        System.out.println();

        // Print board with row indices and grid lines
        for (i = 0; i < board.length; i++) {
            System.out.print((i+1) + " ");  // Row index
            for (j = 0; j < board[i].length; j++) {
                char cell = board[i][j];
                if (cell == ship_cell && !showShips) {
                    System.out.print("| " + empty_cell + " "); // Hide ships
                } else {
                    System.out.print("| " + cell + " ");
                }
            }
            System.out.println("|");
            System.out.println("  " + "------------------------------------------");
        }
    }

    void placeMark(int row, int col, char mark) {
        if ((row >= 0 && row < board.length) && (col >= 0 && col < board[row].length)) {
            board[row][col] = mark;
        } else {
            System.out.println("Invalid Position");
        }
    }

    char getCell(int row, int col) {
        return board[row][col];
    }
}
