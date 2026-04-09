import java.util.Random;
import java.util.Stack;

public class BoardManager {
    protected Cell[][] board;
    protected static final int SIZE = 6;
    protected static final int MINES = 8;
    private int revealedCells = 0;
    private Stack<int[]> moveHistory = new Stack<>();

    // Instance variables instead of static for better encapsulation
    private int zeroRow;
    private int zeroCol;

    public BoardManager() {
        Random rand = new Random();
        board = new Cell[SIZE][SIZE];
        zeroRow = rand.nextInt(SIZE);
        zeroCol = rand.nextInt(SIZE);
        initBoard();
        placeMines();
    }

    private void initBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = new Cell();
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < MINES) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            // Ensure we don't place a mine on the designated "Zero" cell
            if (!board[row][col].isMine() && !(row == zeroRow && col == zeroCol)) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }
    
    public boolean isCellRevealed(int row, int col) {
        return board[row][col].isRevealed();
    }

    public boolean reveal(int row, int col) {
        if (board[row][col].isRevealed()) return true;

        moveHistory.push(new int[]{row, col});
        board[row][col].setRevealed(true);

        if (row == zeroRow && col == zeroCol) {
            revealAroundZero();
        }

        if (board[row][col].isMine()) {
            return false;
        } else {
            revealedCells++;
            return true;
        }
    }

    public boolean undoLastMove() {
        if (moveHistory.isEmpty()) return false;

        int[] lastMove = moveHistory.pop();
        int r = lastMove[0];
        int c = lastMove[1];

        // Based on your rules: Cannot undo a mine hit
        if (board[r][c].isMine()) {
            moveHistory.push(lastMove); // Put it back
            return false;
        }

        board[r][c].setRevealed(false);
        revealedCells--;
        return true;
    }

    public boolean isWin() {
        return revealedCells == (SIZE * SIZE - MINES);
    }

    public void displayBoard() {
        System.out.print("   ");
        for (int col = 1; col <= SIZE; col++) System.out.print(col + "  ");
        System.out.println();

        for (int row = 0; row < SIZE; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col].isRevealed()) {
                    if (board[row][col].isMine()) System.out.print("[*]");
                    else if (row == zeroRow && col == zeroCol) System.out.print("[0]");
                    else printFlagCount(row, col);
                } else {
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
    }

    private void printFlagCount(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i, nc = c + j;
                if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && board[nr][nc].isMine()) count++;
            }
        }
        System.out.print(count == 0 ? "[-]" : "[" + count + "]");
    }

    private void revealAroundZero() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = zeroRow + i, nc = zeroCol + j;
                if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && !board[nr][nc].isRevealed()) {
                    board[nr][nc].setRevealed(true);
                    if (!board[nr][nc].isMine()) revealedCells++;
                }
            }
        }
    }
}