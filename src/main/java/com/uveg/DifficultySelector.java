package com.uveg;

import java.util.Random;

public class DifficultySelector {

  // Method to return the number of cells to reveal based on the selected
  // difficulty
  public static int getDifficultyLevel(String difficulty) {
    Random random = new Random();

    switch (difficulty) {
      case "Easy":
        return random.nextInt(5) + 36; // 36 to 40 givens
      case "Medium":
        return random.nextInt(6) + 30; // 30 to 35 givens
      case "Hard":
        return random.nextInt(5) + 25; // 25 to 29 givens
      default:
        return 36; // Default to Easy if no selection
    }
  }
}
