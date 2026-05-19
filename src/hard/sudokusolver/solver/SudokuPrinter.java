package sudokusolver.solver;

/**
 *
 * @author jpan
 */

public class SudokuPrinter {
    
    /**
    * Prints the Sudoku grid in a readable 9x9 format.
    * @param puzzle the 9x9 Sudoku grid
    */

    public static void print(int[][] puzzle) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.out.print(puzzle[y][x]);
                if (x == 2 || x == 5) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (y == 2 || y == 5) {
                System.out.println("-----------");
            }
        }
        System.out.println();
    }
    
    
    /**
     * Prints the candidate values for each cell.
     * Empty values are displayed as '.'.
     *
     * @param puzzleOptions a 9x9x9 array representing possible values for each cell
     */

    public static void print(int[][][] puzzleOptions) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.out.print("[");
                for (int n = 0; n < 9; n++) {
                    if (puzzleOptions[y][x][n] != 0) {
                        System.out.print(n + 1);
                    } else {
                        System.out.print(".");
                    }
                }
                System.out.print("] ");
                if (x == 2 || x == 5) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (y == 2 || y == 5) {
                System.out.println();
            }
        }
        System.out.println();
    } 
}
