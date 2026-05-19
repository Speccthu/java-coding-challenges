package sudokusolver.solver;

/**
 *
 * @author jpan
 */
public class SudokuUtils {

    /**
     * Performs a deep copy of a 9x9x9 integer array from source to destination.
     *
     * @param destination destination array
     * @param source source array to be copied
     */
    public static void copyArray(int[][][] destination, int[][][] source) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.arraycopy(source[y][x], 0, destination[y][x], 0, 9);
            }
        }
    }
    
    /**
     * Performs a deep copy of a 9x9 integer array from source to destination.
     *
     * @param destination destination array
     * @param source source array to be copied
     */
    public static void copyArray(int[][] destination, int[][] source) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                destination[y][x] = source[y][x];
            }
        }
    }

    /**
     * Counts the number of available candidates for a given cell in the 9x9x9
     * options grid.
     */
    private static int countOptions(int[][][] puzzleOptions, int y, int x) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (puzzleOptions[y][x][i] != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the position of the empty cell with the fewest candidates.
     * This is used to reduce branching during backtracking.
     *
     * @param puzzleOptions a 3D array representing possible values for each
     * cell
     * @param puzzle the 9x9 Sudoku grid
     * 
     * @return the position of the cell (row * 9 + column) with the fewest
     * candidates,
     * or -1 if no valid cell is found
     */
    public static int findBestOptions(int[][][] puzzleOptions, int[][] puzzle) {
        int count = 0;
        int best = 99;
        int pos = 0;
        boolean found = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (puzzle[y][x] != 0) {
                    continue;
                }
                count = countOptions(puzzleOptions, y, x);
                if (count < best && count > 1) {
                    best = count;
                    pos = y * 9 + x;
                    found = true;
                }
            }
        }
        if (found) {
            return pos;
        }
        return -1;
    }

    
    /**
     * Checks whether all empty cells still have at least one valid candidate.
     *
     * @param puzzleOptions a 3D array representing possible values for each cell
     * @param puzzle the current Sudoku grid
     * @return true if every empty cell has at least one possible value,
     * false if any cell has no valid candidates (invalid state)
     */
    public static boolean hasOptions(int[][][] puzzleOptions, int[][] puzzle) {
        boolean hasOption;
        
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (puzzle[y][x] == 0) {
                    hasOption = false;
                    for (int n = 0; n < 9; n++) {
                        if (puzzleOptions[y][x][n] != 0) {
                            hasOption = true;
                            break;
                        }
                    }
                    if (!hasOption) {
                        if (Config.DEBUG) {
                            System.out.println("Invalid state: cell has no valid candidates");
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
