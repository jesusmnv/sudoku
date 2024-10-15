package com.uveg;

import javax.swing.*;

import java.awt.event.ActionEvent;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class App {
  private static final int GRID_SIZE = 9; // 9x9 Sudoku grid
  private List<JTextField> gridCells = new ArrayList<>();
  private SudokuGenerator generator = new SudokuGenerator();
  private JComboBox<String> difficultyComboBox;
  private JPanel gridPanel;
  private int[][] originalGrid; // Store the original Sudoku grid

  public static void main(String[] args) {
    // Set JTattoo Look and Feel
    try {
      UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Launch the application
    new App().createAndShowGUI();
  }

  public void createAndShowGUI() {
    // Create the main frame
    JFrame frame = new JFrame("Sudoku Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 400); // Set the size of the window

    // Create a panel with a GridLayout for the 9x9 Sudoku board
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

    // Initialize blank grid
    // initializeBlankGrid();

    // Difficulty selection
    JPanel difficultyPanel = new JPanel();
    difficultyComboBox = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
    difficultyPanel.add(new JLabel("Select Difficulty:"));
    difficultyPanel.add(difficultyComboBox);

    // Add action listener to the difficulty combo box
    difficultyComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
        if (!selectedDifficulty.equals("Select Difficulty")) {
          fillGridWithSudoku(selectedDifficulty);
        }
      }
    });

    // Button panel for actions
    JPanel buttonPanel = new JPanel();
    JButton validateButton = new JButton("Validate");
    JButton restartButton = new JButton("Restart");
    JButton closeButton = new JButton("Close");

    // Add action listener to Validate button
    validateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        validateInputs();
      }
    });

    // Add action listener to Restart button
    restartButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fillGridWithSudoku((String) difficultyComboBox.getSelectedItem());
      }
    });

    // Add action listener to Close button
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0); // Close the application
      }
    });

    // Add buttons to button panel
    buttonPanel.add(validateButton);
    buttonPanel.add(restartButton);
    buttonPanel.add(closeButton);

    // Add the grid panel and difficulty panel to the frame
    frame.add(difficultyPanel, BorderLayout.NORTH);
    frame.add(gridPanel, BorderLayout.CENTER);
    frame.add(buttonPanel, BorderLayout.SOUTH);

    // Set the frame to be visible
    frame.setVisible(true);

    fillGridWithSudoku("Easy");
  }

  private JTextField createGridCell(int row, int col, int value) {
    JTextField cell = new JTextField();
    cell.setHorizontalAlignment(JTextField.CENTER);
    cell.setFont(new Font("Arial", Font.BOLD, 20)); // Set font and size
    cell.setBackground(Color.WHITE); // Default background color
    cell.setBorder(BorderFactory.createLineBorder(Color.ORANGE)); // Add a border
    cell.setCaretColor(Color.WHITE); // Hide the cursor by setting it to the same color as the background

    // Disable focus highlight and customize it manually
    cell.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        highlightRowAndColumn(row, col);
      }

      @Override
      public void focusLost(FocusEvent e) {
        resetHighlight();
      }
    });

    if (value != 0) {
      cell.setText(String.valueOf(value)); // Set the generated number
      cell.setEditable(false); // Make non-editable for initial values
      cell.setBackground(Color.YELLOW); // Different color for initial numbers
    } else {
      cell.setText(""); // Empty cell
      cell.setEditable(true); // Allow user to fill it
      cell.setBackground(Color.WHITE);
    }

    cell.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    return cell;
  }

  // Fill the grid based on the selected difficulty
  private void fillGridWithSudoku(String difficulty) {
    originalGrid = generator.generateGrid(); // Generate a complete Sudoku grid

    // Get the number of givens based on difficulty
    int numbersToReveal = DifficultySelector.getDifficultyLevel(difficulty);

    revealNumbers(originalGrid, numbersToReveal);
  }

  // Reveal numbers on the grid based on difficulty
  private void revealNumbers(int[][] board, int numbersToReveal) {
    gridPanel.removeAll(); // Clear the existing grid
    gridCells.clear(); // Clear the list of grid cells

    // Initialize the grid with empty cells
    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        JTextField cell = createGridCell(row, col, 0);
        gridCells.add(cell);
        gridPanel.add(cell);
      }
    }

    // Reveal numbers randomly based on the difficulty
    int revealedCount = 0;
    while (revealedCount < numbersToReveal) {
      int randomRow = (int) (Math.random() * GRID_SIZE);
      int randomCol = (int) (Math.random() * GRID_SIZE);
      int index = randomRow * GRID_SIZE + randomCol;
      JTextField cell = gridCells.get(index);

      if (cell.getText().isEmpty()) {
        cell.setText(String.valueOf(board[randomRow][randomCol])); // Set the number in the cell
        cell.setEditable(false); // Disable editing for revealed cells
        cell.setBackground(Color.getHSBColor(50, 200, 41)); // Highlight revealed cells
        revealedCount++;
      }
    }

    gridPanel.revalidate(); // Refresh the grid
    gridPanel.repaint();
  }

  // Validate user inputs against the original grid
  private void validateInputs() {
    boolean isValid = true; // Assume valid until proven otherwise
    StringBuilder errorMessage = new StringBuilder("The following cells are incorrect:");

    boolean hasUserInput = true; // Assume there is user input initially

    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        JTextField cell = gridCells.get(row * GRID_SIZE + col);
        String inputValue = cell.getText();

        // Check if the cell is empty
        if (inputValue.isEmpty()) {
          hasUserInput = false; // No user input
          isValid = false; // Mark validation as invalid
          errorMessage.append("\nCell (").append(row + 1).append(", ").append(col + 1).append(") is empty.");
          continue; // Skip to the next cell
        }

        int inputNumber;
        try {
          inputNumber = Integer.parseInt(inputValue);
        } catch (NumberFormatException e) {
          inputNumber = 0; // Set to 0 if not a valid number
          isValid = false; // Mark validation as invalid
          errorMessage.append("\nCell (").append(row + 1).append(", ").append(col + 1)
              .append(") contains invalid input: ").append(inputValue);
          continue; // Skip to the next cell
        }

        // Check if input number matches the original grid
        if (inputNumber != originalGrid[row][col]) {
          isValid = false; // Found an incorrect cell
          errorMessage.append("\nCell (").append(row + 1).append(", ").append(col + 1).append("): ").append(inputValue);
        }
      }
    }

    // Check if there was no user input at all
    if (!hasUserInput) {
      errorMessage.append("\nPlease fill in all cells!");
      isValid = false; // Mark validation as invalid
    }

    // Show result message
    if (isValid) {
      JOptionPane.showMessageDialog(gridPanel, "All inputs are correct!", "Validation Result",
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(gridPanel, errorMessage.toString(), "Validation Result", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Highlights the row and column of the selected cell
  private void highlightRowAndColumn(int selectedRow, int selectedCol) {
    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        JTextField cell = gridCells.get(row * GRID_SIZE + col);
        if (row == selectedRow || col == selectedCol) {
          cell.setBackground(Color.getHSBColor(100, 200, 1)); // Highlight color for row and column
        } else {
          cell.setBackground(Color.WHITE); // Reset to white if not in the same row/column
        }
      }
    }
  }

  // Reset all cell highlights
  private void resetHighlight() {
    for (JTextField cell : gridCells) {
      cell.setBackground(Color.WHITE); // Reset background color to white
    }
  }
}