import java.util.*;

public class Main {
    public static void main(String[] args) {
        BoardManager game = new BoardManager();
        Scanner scanner = new Scanner(System.in);
        Queue<Integer> turns = new LinkedList<>();
        Set<Integer> usedUndo = new HashSet<>(); // Track players who already used their undo

        int numOfPlayers = setupPlayers(scanner, turns);
        playGame(scanner, turns, game, usedUndo);

        scanner.close();
    }

    private static int setupPlayers(Scanner scanner, Queue<Integer> turns) {
        int num = 0;
        while (true) {
            System.out.print("Enter number of players (2-5): ");
            try {
                num = Integer.parseInt(scanner.nextLine());
                if (num >= 2 && num <= 5) {
                    for (int i = 1; i <= num; i++) turns.offer(i);
                    return num;
                }
            } catch (Exception e) { /* continue */ }
            System.out.println("Invalid input. Enter a number between 2 and 5.");
        }
    }

    public static void playGame(Scanner scanner, Queue<Integer> turns, BoardManager game, Set<Integer> usedUndo) {
        while (!turns.isEmpty()) {
            int currentPlayer = turns.poll();
            game.displayBoard();
            System.out.println("\nPLAYER " + currentPlayer + "'s turn");

            int r = getValidInput(scanner, "row");
            int c = getValidInput(scanner, "column");

            if (game.isCellRevealed(r, c)) {
                System.out.println("Already revealed! Try again.");
                ((LinkedList<Integer>) turns).addFirst(currentPlayer);
                continue;
            }

            boolean safe = game.reveal(r, c);

            if (!safe) {
                System.out.println("💥 BOOM! PLAYER " + currentPlayer + " hit a mine and is OUT!");
                if (turns.isEmpty()) {
                    System.out.println("No players left. GAME OVER.");
                    game.displayBoard();
                    return;
                }
                continue; // Player is not added back to the queue
            }

            if (game.isWin()) {
                game.displayBoard();
                System.out.println("🎉 Congratulations! PLAYER " + currentPlayer + " wins!");
                return;
            }

            // Undo logic
            if (!usedUndo.contains(currentPlayer)) {
                System.out.print("Do you want to undo your move? (Y/N): ");
                String choice = scanner.next();
                if (choice.equalsIgnoreCase("Y")) {
                    game.undoLastMove();
                    usedUndo.add(currentPlayer);
                    System.out.println("Move undone. Passing turn to next player.");
                }
            }

            turns.offer(currentPlayer); // Add back to end of queue for next cycle
        }
    }

    private static int getValidInput(Scanner scanner, String type) {
        while (true) {
            System.out.print("Enter " + type + " (1-6): ");
            try {
                int val = Integer.parseInt(scanner.next());
                if (val >= 1 && val <= 6) return val - 1;
            } catch (Exception e) { }
            System.out.println("Invalid input. Please enter 1-6.");
        }
    }
}