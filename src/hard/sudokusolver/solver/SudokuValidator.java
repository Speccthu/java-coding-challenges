package sudokusolver.solver;

/**
 *
 * @author jpan
 */
public class SudokuValidator {

    
    /**
     * Checks whether the given Sudoku grid follows all validation rules.
     *
     * Ensures that no duplicate numbers exist in any row, column,
     * or 3x3 subgrid.
     *
     * @param puzzle the Sudoku grid to validate
     * @return true if the grid is valid, false otherwise
     */
    public static boolean isValid(int[][] puzzle) {
        int[] seen;
        for (int y = 0; y < 9; y++) {
            seen = new int[9];
            for (int x = 0; x < 9; x++) {
                if (!checkValidCell(puzzle, seen, y, x)) {
                    return false;
                }
            }
        }
        for (int x = 0; x < 9; x++) {
            seen = new int[9];
            for (int y = 0; y < 9; y++) {
                if (!checkValidCell(puzzle, seen, y, x)) {
                    return false;
                }
            }
        }
        for (int y0 = 0; y0 < 9; y0 += 3) {
            for (int x0 = 0; x0 < 9; x0 += 3) {
                seen = new int[9];
                for (int y = y0; y < y0 + 3; y++) {
                    for (int x = x0; x < x0 + 3; x++) {
                        if (!checkValidCell(puzzle, seen, y, x)) {
                            return false;
                        }      
                    }
                }
            }
        }
        return true;
    }

    
    /**
     * Checks whether the Sudoku puzzle is completely solved.
     *
     * A puzzle is considered solved if all cells are filled
     * and the grid is valid.
     *
     * @param puzzle the Sudoku grid
     * @return true if the puzzle is solved, false otherwise
     */
    public static boolean isSolved(int[][] puzzle) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (puzzle[y][x] == 0) {
                    return false;
                }
            }
        }
        return isValid(puzzle);
    }

    
    /**
     * Validates the structure and values of the Sudoku grid.
     *
     * Ensures the grid is 9x9, contains valid values (0–9),
     * and does not violate Sudoku rules.
     *
     * @param grid the Sudoku grid
     * @throws IllegalArgumentException if the grid is invalid
     */
    public static void testGrid(int[][] grid) throws IllegalArgumentException {
        if (grid.length != 9) {
            throw new IllegalArgumentException("invalid array size");
        }
        for (int i = 0; i < 9; i++) {
            if (grid[i].length != 9) {
                throw new IllegalArgumentException("invalid array size");
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] > 9 || grid[i][j] < 0) {
                    throw new IllegalArgumentException("invalid array number");
                }
            }
        }
        if (!isValid(grid)) {
            throw new IllegalArgumentException("invalid grid state");
        }
    }
    
    /**
     * Checks whether a single cell violates constraints in the current row/column/box
     */
    private static boolean checkValidCell(int[][] puzzle, int[] seen, int y, int x){
        
        int value = puzzle[y][x];
        
        if (value != 0) {
            if (seen[value - 1] == value) {
                return false;
            } else {
                seen[value - 1] = value;
            }
        }
        return true;
    }
}
