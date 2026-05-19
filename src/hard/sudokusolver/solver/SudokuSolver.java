package sudokusolver.solver;

/**
 * @author jpan
 * Sudoku solver using a combination of:
 * 
 * 1. Constraint propagation techniques:
 *    - Naked singles
 *    - Hidden singles
 *    - Pair elimination
 * 
 * 2. Backtracking when logical solving is insufficient
 *
 * The solver first reduces the search space using logic,
 * then applies brute-force search with optimization.
 */
public class SudokuSolver {
    
    private int[][] grid;
    private static int solutions = 0;
    private static int[][] firstSolution = new int[9][9];
    
    public SudokuSolver(int[][] grid) {
        this.grid = grid;
        solutions = 0;
    }
    
    // ============================
    // SOLVING ENTRY POINT
    // ============================

    
    /**
     * Solves the given Sudoku puzzle.
     *
     * This method validates the input grid, applies logical solving strategies,
     * and falls back to backtracking if necessary to find a valid solution.
     *
     * @return a fully solved 9x9 Sudoku grid
     * @throws IllegalArgumentException if the input grid is invalid or unsolvable
     */

    public int[][] solve() {
    
        SudokuValidator.testGrid(grid);

        grid = sudoku(grid);

        return grid;
    }
  
    // ============================
    // LOGIC APPLICATION
    // ============================

    static int[][] sudoku(int[][] puzzle) {

        int[][][] puzzleOptions = new int[9][9][9];

        fillOptions(puzzleOptions, puzzle);

        if(Config.DEBUG){
            SudokuPrinter.print(puzzle);
        }
        
        boolean change = true;
        
        // Apply logical strategies iteratively until no further changes are possible
        while(change && SudokuValidator.isValid(puzzle)){

          change = false;
          change |= checkSingles(puzzleOptions, puzzle);
          change |= checkNakedSingles(puzzleOptions, puzzle);
          change |= checkHiddenSingles(puzzleOptions, puzzle);
          change |= checkPairs(puzzleOptions, puzzle);
        }
        
        if(!SudokuValidator.isSolved(puzzle)){

          if(Config.DEBUG){
            System.out.println("---------------------------------"); 
            System.out.println("before brute force");
            System.out.println("---------------------------------");
            SudokuPrinter.print(puzzleOptions);
            SudokuPrinter.print(puzzle);
          }
          puzzle = bruteForce(puzzleOptions, puzzle);
        }
        else{
          solutions++;
        }
        if(puzzle == null){
          throw new IllegalArgumentException("Invalid puzzle state");
        }
        if(Config.DEBUG){
          System.out.println("---------------------------------"); 
          System.out.println("return");
          System.out.println("---------------------------------");
          SudokuPrinter.print(puzzle);
        }
        if(solutions == 0){
          throw new IllegalArgumentException("no solution found");
        }

        SudokuValidator.testGrid(puzzle);

        return puzzle;
    }
    
    static int[][][] applyLogic(int[][] puzzle) {
        int[][][] puzzleOptions = new int[9][9][9];
        SudokuSolver.fillOptions(puzzleOptions, puzzle);
        if (Config.DEBUG) {
            System.out.println("---------------------------------");
            System.out.println("inside iteration");
            System.out.println("---------------------------------");
            SudokuPrinter.print(puzzleOptions);
            SudokuPrinter.print(puzzle);
        }
        if (!SudokuUtils.hasOptions(puzzleOptions, puzzle)) {
            return null;
        }
        boolean change = true;
        while (change) {
            change = false;
            change |= checkSingles(puzzleOptions, puzzle);
            change |= checkNakedSingles(puzzleOptions, puzzle);
            change |= checkHiddenSingles(puzzleOptions, puzzle);
            change |= checkPairs(puzzleOptions, puzzle);
        }
        if (Config.DEBUG) {
            System.out.println("---------------------------------");
            System.out.println("inside after iteration");
            System.out.println("---------------------------------");
            SudokuPrinter.print(puzzleOptions);
            SudokuPrinter.print(puzzle);
        }
        return puzzleOptions;
    }
    
    // ============================
    // BACKTRACKING
    // ============================

    
    /**
     * Applies backtracking to find a valid Sudoku solution.
     *
     * Uses recursion and selects the cell with the fewest candidates
     * to minimize branching.
     *
     * @param puzzleOptions possible values for each cell
     * @param puzzle current grid state
     * @return solved grid
     */

    static int[][] bruteForce(int[][][] puzzleOptions, int[][] puzzle) throws IllegalArgumentException{
    
    if(SudokuValidator.isSolved(puzzle)){
      if(Config.DEBUG){
        System.out.println("found solution");
      }
      solutions++;
      
      if(solutions == 1){
          firstSolution = new int[9][9];
          SudokuUtils.copyArray(firstSolution, puzzle);
      }

      if(solutions > 1){
        throw new IllegalArgumentException();
      }
      return puzzle;
    }
    
    int pos = SudokuUtils.findBestOptions(puzzleOptions, puzzle);
    
    if(pos < 0){
      return null;
    }
    
    if(Config.DEBUG){
      System.out.println("best position " + pos);
    }
    for(int i=0;i<9;i++){
      
      if(puzzleOptions[pos/9][pos%9][i] == 0){
        continue;
      }
      
      int[][] tempPuzzle = new int[9][9];
      int[][][] tempPuzzleOptions = new int[9][9][9];
      
      
      SudokuUtils.copyArray(tempPuzzle,puzzle);
      
      tempPuzzle[pos/9][pos%9] = i+1;
      
      tempPuzzleOptions = applyLogic(tempPuzzle);

      if(tempPuzzleOptions == null) {
          continue;
      }

      int[][] result = bruteForce(tempPuzzleOptions, tempPuzzle);

    }  
    return firstSolution;
  }
    
    // ============================
    // CONSTRAINT HANDLING
    // ============================

    static void fillOptions(int[][][] puzzleOptions, int[][] puzzle) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                for (int i = 0; i < 9; i++) {
                    puzzleOptions[y][x][i] = i + 1;
                }
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (puzzle[y][x] != 0) {
                    keep(puzzleOptions, puzzle, y, x, puzzle[y][x]);
                }
            }
        }
    }
    
    static boolean keep(int[][][] puzzleOptions, int[][] puzzle, int y, int x, int num) {
        boolean change = false;
        if (puzzle[y][x] != num) {
            change = true;
        }
        puzzle[y][x] = num;
        for (int i = 0; i < 9; i++) {
            if (i != num - 1) {
                puzzleOptions[y][x][i] = 0;
            }
        }
        change |= removeColumn(puzzleOptions, y, x, num);
        change |= removeRow(puzzleOptions, y, x, num);
        change |= removeBox(puzzleOptions, y, x, num);
        return change;
    }
    
    static boolean removeColumn(int[][][] puzzleOptions, int y, int x, int num) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            if (i != y) {
                if (puzzleOptions[i][x][num - 1] != 0) {
                    puzzleOptions[i][x][num - 1] = 0;
                    change = true;
                }
            }
        }
        return change;
    }
    static boolean removeColumn(int[][][] puzzleOptions, int y1, int y2, int y3, int x, int num) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            if (i != y1 && i != y2 && i != y3) {
                if (puzzleOptions[i][x][num - 1] != 0) {
                    puzzleOptions[i][x][num - 1] = 0;
                    change = true;
                }
            }
        }
        return change;
    }
    static boolean removeRow(int[][][] puzzleOptions, int y, int x, int num) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            if (i != x) {
                if (puzzleOptions[y][i][num - 1] != 0) {
                    puzzleOptions[y][i][num - 1] = 0;
                    change = true;
                }
            }
        }
        return change;
    }
    static boolean removeRow(int[][][] puzzleOptions, int y, int x1, int x2, int x3, int num) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            if (i != x1 && i != x2 && i != x3) {
                if (puzzleOptions[y][i][num - 1] != 0) {
                    puzzleOptions[y][i][num - 1] = 0;
                    change = true;
                }
            }
        }
        return change;
    }
    static boolean removeBox(int[][][] puzzleOptions, int y, int x, int num) {
        boolean change = false;
        int y0 = (y / 3) * 3;
        int x0 = (x / 3) * 3;
        for (int i = y0; i < y0 + 3; i++) {
            for (int j = x0; j < x0 + 3; j++) {
                if (i != y || j != x) {
                    if (puzzleOptions[i][j][num - 1] != 0) {
                        puzzleOptions[i][j][num - 1] = 0;
                        change |= true;
                    }
                }
            }
        }
        return change;
    }
    
    // ============================
    // SOLVING STRATEGIES
    // ============================

    static boolean checkNakedSingles(int[][][] puzzleOptions, int[][] puzzle) {
        boolean change = false;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (puzzle[y][x] == 0) {
                    int count = 0;
                    int val = 0;
                    for (int n = 0; n < 9; n++) {
                        if (puzzleOptions[y][x][n] != 0) {
                            count++;
                            val = n + 1;
                        }
                    }
                    if (count == 1) {
                        change |= SudokuSolver.keep(puzzleOptions, puzzle, y, x, val);
                    }
                }
            }
        }
        return change;
    }
    
    static boolean checkSingles(int[][][] puzzleOptions, int[][] puzzle) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            change |= checkRowSingles(puzzleOptions, puzzle, i);
            change |= checkColumnSingles(puzzleOptions, puzzle, i);
        }
        change |= checkBoxesSingles(puzzleOptions, puzzle);
        return change;
    }
    static boolean checkColumnSingles(int[][][] puzzleOptions, int[][] puzzle, int x) {
        boolean change = false;
        int[] num = new int[9];
        int count = 0;
        int i = 0;
        for (int y = 0; y < 9; y++) {
            if (puzzle[y][x] == 0) {
                count++;
                i = y;
            } else {
                num[puzzle[y][x] - 1] = puzzle[y][x];
            }
        }
        if (count == 0) {
            return change;
        }
        if (count == 1) {
            for (int n = 0; n < 9; n++) {
                if (num[n] == 0) {
                    change |= SudokuSolver.keep(puzzleOptions, puzzle, i, x, n + 1);
                }
            }
        }
        return change;
    }
    static boolean checkRowSingles(int[][][] puzzleOptions, int[][] puzzle, int y) {
        boolean change = false;
        int[] num = new int[9];
        int count = 0;
        int i = 0;
        for (int x = 0; x < 9; x++) {
            if (puzzle[y][x] == 0) {
                count++;
                i = x;
            } else {
                num[puzzle[y][x] - 1] = puzzle[y][x];
            }
        }
        if (count == 0) {
            return change;
        }
        if (count == 1) {
            for (int n = 0; n < 9; n++) {
                if (num[n] == 0) {
                    change |= SudokuSolver.keep(puzzleOptions, puzzle, y, i, n + 1);
                }
            }
        }
        return change;
    }
    static boolean checkBoxesSingles(int[][][] puzzleOptions, int[][] puzzle) {
        boolean change = false;
        for (int y = 0; y < 7; y += 3) {
            for (int x = 0; x < 7; x += 3) {
                change |= checkBoxSingles(puzzleOptions, puzzle, y, x);
            }
        }
        return change;
    }
    static boolean checkBoxSingles(int[][][] puzzleOptions, int[][] puzzle, int y0, int x0) {
        boolean change = false;
        int[] num = new int[9];
        int count = 0;
        int iy = 0;
        int ix = 0;
        for (int y = y0; y < y0 + 3; y++) {
            for (int x = x0; x < x0 + 3; x++) {
                if (puzzle[y][x] == 0) {
                    count++;
                    iy = y;
                    ix = x;
                } else {
                    num[puzzle[y][x] - 1] = puzzle[y][x];
                }
            }
        }
        if (count == 0) {
            return change;
        }
        if (count == 1) {
            for (int n = 0; n < 9; n++) {
                if (num[n] == 0) {
                    change |= SudokuSolver.keep(puzzleOptions, puzzle, iy, ix, n + 1);
                }
            }
        }
        return change;
    }

    static boolean checkHiddenSingles(int[][][] puzzleOptions, int[][] puzzle) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                change |= checkRowHiddenSingles(puzzleOptions, puzzle, i, j);
                change |= checkColumnHiddenSingles(puzzleOptions, puzzle, i, j);
            }
        }
        for (int i = 0; i < 9; i++) {
            change |= checkBoxesHiddenSingles(puzzleOptions, puzzle, i);
        }
        return change;
    }
    static boolean checkColumnHiddenSingles(int[][][] puzzleOptions, int[][] puzzle, int x, int n) {
        boolean change = false;
        int count = 0;
        int i = 0;
        for (int y = 0; y < 9; y++) {
            if (puzzleOptions[y][x][n] != 0 && puzzle[y][x] == 0) {
                count++;
                i = y;
            }
        }
        if (count == 1) {
            change = SudokuSolver.keep(puzzleOptions, puzzle, i, x, n + 1);
        }
        return change;
    }
    static boolean checkRowHiddenSingles(int[][][] puzzleOptions, int[][] puzzle, int y, int n) {
        boolean change = false;
        int count = 0;
        int i = 0;
        for (int x = 0; x < 9; x++) {
            if (puzzleOptions[y][x][n] != 0 && puzzle[y][x] == 0) {
                count++;
                i = x;
            }
        }
        if (count == 1) {
            change = SudokuSolver.keep(puzzleOptions, puzzle, y, i, n + 1);
        }
        return change;
    }
    static boolean checkBoxesHiddenSingles(int[][][] puzzleOptions, int[][] puzzle, int num) {
        boolean change = false;
        for (int y = 0; y < 7; y += 3) {
            for (int x = 0; x < 7; x += 3) {
                change |= checkBoxHiddenSingles(puzzleOptions, puzzle, y, x, num);
            }
        }
        return change;
    }
    static boolean checkBoxHiddenSingles(int[][][] puzzleOptions, int[][] puzzle, int y0, int x0, int n) {
        boolean change = false;
        int count = 0;
        int iy = 0;
        int ix = 0;
        for (int y = y0; y < y0 + 3; y++) {
            for (int x = x0; x < x0 + 3; x++) {
                if (puzzleOptions[y][x][n] != 0 && puzzle[y][x] == 0) {
                    count++;
                    iy = y;
                    ix = x;
                }
            }
        }
        if (count == 1) {
            change = SudokuSolver.keep(puzzleOptions, puzzle, iy, ix, n + 1);
        }
        return change;
    }

    static boolean checkPairs(int[][][] puzzleOptions, int[][] puzzle) {
        boolean change = false;
        for (int i = 0; i < 9; i++) {
            change |= checkBoxesPairs(puzzleOptions, puzzle, i);
        }
        return change;
    }
    static boolean checkBoxesPairs(int[][][] puzzleOptions, int[][] puzzle, int num) {
        boolean change = false;
        for (int y = 0; y < 7; y += 3) {
            for (int x = 0; x < 7; x += 3) {
                change |= checkBoxPairs(puzzleOptions, puzzle, y, x, num);
            }
        }
        return change;
    }
    static boolean checkBoxPairs(int[][][] puzzleOptions, int[][] puzzle, int y0, int x0, int n) {
        boolean change = false;
        boolean inlinex = true;
        boolean inliney = true;
        int count = 0;
        int iy = 0;
        int ix = 0;
        for (int y = y0; y < y0 + 3; y++) {
            for (int x = x0; x < x0 + 3; x++) {
                if (puzzleOptions[y][x][n] == n + 1) {
                    if (count == 0) {
                        iy = y;
                        ix = x;
                    } else {
                        if (y != iy) {
                            inlinex = false;
                        }
                        if (x != ix) {
                            inliney = false;
                        }
                    }
                    count++;
                }
            }
        }
        if (count < 2 || count > 3) {
            return change;
        }
        if (inlinex) {
            change |= SudokuSolver.removeRow(puzzleOptions, iy, x0, x0 + 1, x0 + 2, n + 1);
        }
        if (inliney) {
            change |= SudokuSolver.removeColumn(puzzleOptions, y0, y0 + 1, y0 + 2, ix, n + 1);
        }
        return change;
    }

    

    


    

    

    
        
}