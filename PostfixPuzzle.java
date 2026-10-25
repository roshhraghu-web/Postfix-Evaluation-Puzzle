package POSTFIX_PROJ2;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Stack;

public class PostfixPuzzle extends JFrame {
    private static final int TOTAL_PUZZLES = 3;

    private final Color BLACK = new Color(18, 18, 18);
    private final Color DARK = new Color(38, 38, 38);
    private final Color MID = new Color(125, 125, 125);
    private final Color PALE = new Color(246, 246, 246);
    private final Color WHITE = Color.WHITE;

    private final Random random = new Random();

    private JLabel playerValue;
    private JLabel scoreValue;
    private JLabel puzzleNumberLabel;
    private JLabel expressionLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;
    private JButton checkButton;
    private JButton nextButton;
    private JButton playAgainButton;

    private String playerName = "Player";
    private String currentExpression;
    private int correctAnswer;
    private int score = 0;
    private int puzzleNumber = 1;
    private boolean answered = false;

    public PostfixPuzzle() {
        super("Postfix Evaluation Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 650));
        setSize(1100, 780);
        setLocationRelativeTo(null);
        setContentPane(buildInterface());
        askPlayerName();
        loadPuzzle();
    }

    private void askPlayerName() {
        String name = JOptionPane.showInputDialog(
                this, "Enter your player name:", "Welcome to HORA GAMES",
                JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            playerName = name.trim();
        }
        if (playerValue != null) {
            playerValue.setText(playerName);
        }
    }

    private JPanel buildInterface() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(WHITE);
        root.setBorder(new EmptyBorder(24, 28, 26, 28));

        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        JLabel title = new JLabel("POSTFIX EVALUATION PUZZLE");
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        title.setForeground(BLACK);

        JLabel brand = new JLabel("◆ HORA GAMES");
        brand.setFont(new Font("SansSerif", Font.BOLD, 17));
        brand.setForeground(BLACK);
        brand.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(title, BorderLayout.CENTER);
        header.add(brand, BorderLayout.EAST);

        JSeparator separator = new JSeparator();
        separator.setForeground(BLACK);
        separator.setBackground(BLACK);

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        top.add(header, BorderLayout.CENTER);
        top.add(separator, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 22));
        center.setOpaque(false);

        JPanel status = new JPanel(new BorderLayout(16, 0));
        status.setOpaque(false);
        JPanel playerPanel = infoPanel("Player:", true);
        playerValue = (JLabel) playerPanel.getClientProperty("valueLabel");
        JPanel scorePanel = infoPanel("Score:", false);
        scoreValue = (JLabel) scorePanel.getClientProperty("valueLabel");
        status.add(playerPanel, BorderLayout.WEST);
        status.add(scorePanel, BorderLayout.EAST);
        center.add(status, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 18);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        JPanel puzzlePanel = roundedPanel();
        puzzlePanel.setLayout(new BorderLayout(10, 18));
        puzzlePanel.setBorder(new EmptyBorder(24, 26, 24, 26));

        puzzleNumberLabel = new JLabel("PUZZLE 1 / 3");
        puzzleNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        puzzleNumberLabel.setForeground(BLACK);

        expressionLabel = new JLabel("", SwingConstants.CENTER);
        expressionLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        expressionLabel.setForeground(BLACK);

        JLabel instruction = new JLabel("Evaluate the postfix expression.");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 20));
        instruction.setForeground(BLACK);

        puzzlePanel.add(puzzleNumberLabel, BorderLayout.NORTH);
        puzzlePanel.add(expressionLabel, BorderLayout.CENTER);
        puzzlePanel.add(instruction, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.7;
        mainContent.add(puzzlePanel, gbc);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JLabel answerTitle = new JLabel("Your Answer:");
        answerTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        answerTitle.setForeground(BLACK);
        answerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        answerField = new JTextField();
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 22));
        answerField.setForeground(BLACK);
        answerField.setBackground(WHITE);
        answerField.setCaretColor(BLACK);
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(85, 85, 85), 2, true),
                new EmptyBorder(12, 14, 12, 14)));
        answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        answerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        answerField.addActionListener(e -> checkAnswer());

        checkButton = makeButton("CHECK", BLACK, WHITE);
        checkButton.addActionListener(e -> checkAnswer());

        nextButton = makeButton("NEXT PUZZLE", WHITE, BLACK);
        nextButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLACK, 2, true),
                new EmptyBorder(12, 16, 12, 16)));
        nextButton.addActionListener(e -> nextPuzzle());

        playAgainButton = makeButton("PLAY AGAIN", WHITE, BLACK);
        playAgainButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLACK, 2, true),
                new EmptyBorder(12, 16, 12, 16)));
        playAgainButton.setEnabled(false);
        playAgainButton.addActionListener(e -> restartGame());

        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        feedbackLabel.setForeground(BLACK);
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        controls.add(answerTitle);
        controls.add(Box.createVerticalStrut(18));
        controls.add(answerField);
        controls.add(Box.createVerticalStrut(26));
        controls.add(checkButton);
        controls.add(Box.createVerticalStrut(16));
        controls.add(nextButton);
        controls.add(Box.createVerticalStrut(14));
        controls.add(playAgainButton);
        controls.add(Box.createVerticalStrut(12));
        controls.add(feedbackLabel);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainContent.add(controls, gbc);

        center.add(mainContent, BorderLayout.CENTER);

        JPanel reminder = roundedPanel();
        reminder.setLayout(new BorderLayout(18, 0));
        reminder.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel bulb = new JLabel("✦", SwingConstants.CENTER);
        bulb.setFont(new Font("SansSerif", Font.BOLD, 34));
        bulb.setForeground(BLACK);
        bulb.setPreferredSize(new Dimension(56, 60));

        JPanel reminderText = new JPanel();
        reminderText.setOpaque(false);
        reminderText.setLayout(new BoxLayout(reminderText, BoxLayout.Y_AXIS));
        JLabel remember = new JLabel("REMEMBER:");
        remember.setFont(new Font("SansSerif", Font.BOLD, 17));
        remember.setForeground(BLACK);
        JLabel hint = new JLabel("Use a stack to evaluate the postfix expression.");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 16));
        hint.setForeground(BLACK);
        JLabel operators = new JLabel("Operators:   +    -    *    /");
        operators.setFont(new Font("SansSerif", Font.PLAIN, 16));
        operators.setForeground(BLACK);
        reminderText.add(remember);
        reminderText.add(Box.createVerticalStrut(5));
        reminderText.add(hint);
        reminderText.add(Box.createVerticalStrut(4));
        reminderText.add(operators);

        reminder.add(bulb, BorderLayout.WEST);
        reminder.add(reminderText, BorderLayout.CENTER);
        center.add(reminder, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel infoPanel(String labelText, boolean isPlayer) {
        JPanel panel = roundedPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 12));
        panel.setBorder(new EmptyBorder(0, 14, 0, 14));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        label.setForeground(BLACK);
        JLabel value = new JLabel(isPlayer ? playerName : "0");
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(BLACK);
        panel.add(label);
        panel.add(value);
        panel.putClientProperty("valueLabel", value);
        panel.setPreferredSize(new Dimension(isPlayer ? 300 : 170, 56));
        return panel;
    }

    private JPanel roundedPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PALE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MID, 1, true),
                new EmptyBorder(8, 8, 8, 8)));
        return panel;
    }

    private JButton makeButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 21));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        button.setPreferredSize(new Dimension(300, 64));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background == BLACK ? BLACK : MID, 2, true),
                new EmptyBorder(12, 16, 12, 16)));
        return button;
    }

    private void loadPuzzle() {
        if (puzzleNumber > TOTAL_PUZZLES) {
            showGameOver();
            return;
        }

        int a = random.nextInt(9) + 1;
        int b = random.nextInt(9) + 1;
        int c = random.nextInt(9) + 1;

        switch (random.nextInt(4)) {
            case 0:
                currentExpression = a + " " + b + " + " + c + " *";
                break;
            case 1:
                currentExpression = a + " " + b + " * " + c + " +";
                break;
            case 2:
                currentExpression = a + " " + b + " + " + c + " -";
                break;
            default:
                currentExpression = a + " " + b + " - " + c + " *";
                break;
        }

        // Regenerate if the subtraction-first expression could be negative.
        if (currentExpression.equals(a + " " + b + " - " + c + " *") && a < b) {
            currentExpression = a + " " + b + " + " + c + " *";
        }

        correctAnswer = evaluatePostfix(currentExpression);
        puzzleNumberLabel.setText("PUZZLE " + puzzleNumber + " / " + TOTAL_PUZZLES);
        expressionLabel.setText(currentExpression);
        answerField.setText("");
        answerField.setEditable(true);
        feedbackLabel.setText(" ");
        answered = false;
        checkButton.setEnabled(true);
        nextButton.setEnabled(false);
        playAgainButton.setEnabled(false);
        answerField.requestFocusInWindow();
    }

    private int evaluatePostfix(String expression) {
        Stack<Integer> stack = new Stack<>();
        for (String token : expression.split("\\s+")) {
            if (token.matches("-?\\d+")) {
                stack.push(Integer.parseInt(token));
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                int right = stack.pop();
                int left = stack.pop();
                switch (token) {
                    case "+": stack.push(left + right); break;
                    case "-": stack.push(left - right); break;
                    case "*": stack.push(left * right); break;
                    case "/":
                        if (right == 0) throw new ArithmeticException("Division by zero");
                        stack.push(left / right);
                        break;
                    default: throw new IllegalArgumentException("Unknown operator: " + token);
                }
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }
        return stack.pop();
    }

    private void checkAnswer() {
        if (answered) return;

        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            feedbackLabel.setForeground(BLACK);
            feedbackLabel.setText("Please enter an answer.");
            answerField.requestFocusInWindow();
            return;
        }

        try {
            int answer = Integer.parseInt(input);
            answered = true;
            if (answer == correctAnswer) {
                score += 10;
                feedbackLabel.setForeground(new Color(25, 105, 55));
                feedbackLabel.setText("Correct! +10 points");
            } else {
                feedbackLabel.setForeground(new Color(150, 30, 30));
                feedbackLabel.setText("Not quite. Answer: " + correctAnswer);
            }
            scoreValue.setText(String.valueOf(score));
            answerField.setEditable(false);
            checkButton.setEnabled(false);
            nextButton.setEnabled(true);
            nextButton.requestFocusInWindow();
        } catch (NumberFormatException ex) {
            feedbackLabel.setForeground(BLACK);
            feedbackLabel.setText("Enter a whole number.");
        }
    }

    private void nextPuzzle() {
        if (!answered) {
            feedbackLabel.setForeground(BLACK);
            feedbackLabel.setText("Check your answer first.");
            return;
        }
        puzzleNumber++;
        loadPuzzle();
    }

    private void showGameOver() {
        expressionLabel.setText("GAME COMPLETE");
        puzzleNumberLabel.setText("FINAL SCORE");
        feedbackLabel.setForeground(BLACK);
        feedbackLabel.setText(playerName + " scored " + score + " / " + (TOTAL_PUZZLES * 10));
        answerField.setText("");
        answerField.setEditable(false);
        checkButton.setEnabled(false);
        nextButton.setEnabled(false);
        playAgainButton.setEnabled(true);
    }

    private void restartGame() {
        score = 0;
        puzzleNumber = 1;
        scoreValue.setText("0");
        nextButton.setText("NEXT PUZZLE");
        nextButton.setEnabled(false);
        playAgainButton.setEnabled(false);
        loadPuzzle();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use Java's default look and feel if the system one is unavailable.
            }
            new PostfixPuzzle().setVisible(true);
        });
    }
}
