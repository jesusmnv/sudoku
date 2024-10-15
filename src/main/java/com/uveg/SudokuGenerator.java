package com.uveg;

import java.util.Random;

public class SudokuGenerator {
  private static final int GRID_SIZE = 9;
  private int[][] board;
  private Random random;

  public SudokuGenerator() {
    board = new int[GRID_SIZE][GRID_SIZE];
    random = new Random();
  }

  // Generates a complete, valid Sudoku grid
  public int[][] generateGrid() {
    fillGrid(0, 0); // Start filling from the top-left corner
    return board;
  }

  // Recursive backtracking algorithm to fill the grid
  private boolean fillGrid(int row, int col) {
    if (row == GRID_SIZE) {
      return true; // Reached the end of the grid
    }

    int nextRow = (col == GRID_SIZE - 1) ? row + 1 : row;
    int nextCol = (col == GRID_SIZE - 1) ? 0 : col + 1;

    // Try placing numbers 1-9 in a random order
    int[] numbers = shuffleArray();
    for (int num : numbers) {
      if (isValidPlacement(row, col, num)) {
        board[row][col] = num;

        // Continue filling the rest of the grid
        if (fillGrid(nextRow, nextCol)) {
          return true;
        }

        // Backtrack if placing num doesn't lead to a solution
        board[row][col] = 0;
      }
    }

    return false; // Backtracking
  }

  // Checks if placing the number at board[row][col] is valid
  private boolean isValidPlacement(int row, int col, int num) {
    // Check the row and column
    for (int i = 0; i < GRID_SIZE; i++) {
      if (board[row][i] == num || board[i][col] == num) {
        return false;
      }
    }

    // Check the 3x3 subgrid
    int startRow = row - row % 3;
    int startCol = col - col % 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (board[startRow + i][startCol + j] == num) {
          return false;
        }
      }
    }

    return true;
  }

  // Shuffles the numbers 1-9 to place them in random order
  private int[] shuffleArray() {
    int[] array = new int[GRID_SIZE];
    for (int i = 0; i < GRID_SIZE; i++) {
      array[i] = i + 1;
    }

    // Shuffle the array
    for (int i = GRID_SIZE - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      int temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }

    return array;
  }
}