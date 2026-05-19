package sudokusolver;

import sudokusolver.solver.SudokuSolver;
import sudokusolver.solver.SudokuValidator;
import sudokusolver.solver.SudokuPrinter;

/**
 * @author jpan
 * Simple test runner for verifying the Sudoku solver.
 *
 * Executes multiple puzzles and checks if solutions are valid.
 */

public class SudokuTest {

    public static void main(String[] args) {

        int[][] puzzle1 = {{0, 9, 1, 0, 0, 0, 7, 0, 0},
                         {0, 0, 8, 0, 0, 6, 0, 0, 0},
                         {0, 0, 6, 0, 4, 3, 0, 2, 0},
                         {0, 4, 0, 0, 0, 0, 3, 7, 0},
                         {0, 0, 3, 0, 7, 8, 0, 1, 0},
                         {0, 0, 0, 0, 9, 0, 0, 8, 0},
                         {7, 6, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 9, 0, 0, 0, 0, 4, 0},
                         {0, 0, 0, 0, 0, 0, 5, 0, 1}};

        int[][] puzzle2 = {{0, 0, 0, 0, 0, 2, 7, 5, 0},
                         {0, 1, 8, 0, 9, 0, 0, 0, 0},  
                         {0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                         {4, 9, 0, 0, 0, 0, 0, 0, 0}, 
                         {0, 3, 0, 0, 0, 0, 0, 0, 8},
                         {0, 0, 0, 7, 0, 0, 2, 0, 0}, 
                         {0, 0, 0, 0, 3, 0, 0, 0, 9}, 
                         {7, 0, 0, 0, 0, 0, 0, 0, 0}, 
                         {5, 0, 0, 0, 0, 0, 0, 8, 0}};
        
        int[][] puzzle3 = {{0, 0, 6, 3, 0, 0, 0, 0, 2},
                         {0, 3, 0, 0, 4, 0, 0, 6, 0},
                         {7, 0, 0, 0, 0, 1, 9, 0, 0},
                         {2, 0, 0, 0, 0, 8, 7, 0, 0},
                         {0, 1, 0, 0, 5, 0, 0, 4, 0},
                         {0, 0, 9, 1, 0, 0, 0, 0, 5},
                         {0, 0, 7, 4, 0, 0, 0, 0, 8},
                         {0, 9, 0, 0, 1, 0, 0, 2, 0},
                         {3, 0, 0, 0, 0, 5, 6, 0, 0}};
        
        int[][] puzzle4 = {{0, 5, 5, 7, 0, 0, 0, 0, 0},
                         {0, 3, 0, 0, 0, 0, 0, 0, 0},
                         {7, 0, 0, 0, 0, 0, 0, 0, 0},
                         {2, 0, 0, 0, 0, 0, 2, 0, 0},
                         {0, 1, 0, 0, 5, 0, 0, 0, 0},
                         {0, 0, 4, 0, 0, 0, 3, 0, 5},
                         {0, 0, 7, 0, 0, 0, 0, 0, 8},
                         {0, 0, 0, 0, 0, 5, 0, 0, 0},
                         {4, 0, 0, 0, 0, 1, 0, 0, 0}};
        

        testPuzzle(puzzle1, "Test 1");
        
        testPuzzle(puzzle2, "Test 2");
        
        testPuzzle(puzzle3, "Test 3");
        
        try {
            testPuzzle(puzzle4, "Test 4");
        } catch (Exception e) {
            System.out.println("Test 4 correctly failed: " + e.getMessage());
        }

    }
    
    public static void testPuzzle(int[][] puzzle, String name) {
        SudokuSolver solver = new SudokuSolver(puzzle);
        int[][] solution = solver.solve();
        
        SudokuPrinter.print(solution);

        if (!SudokuValidator.isSolved(solution)) {
            throw new RuntimeException("Test failed: solution is invalid");
        }
        System.out.println(name + " passed!");
        System.out.println();
    }
}
