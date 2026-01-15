package package_IA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class Teacher_ViewStudentProgress extends JFrame {
    private static final long serialVersionUID = 1L;
    private Teacher currentTeacher;
    private JTable attemptsTable;
    private DefaultTableModel tableModel;
    static Color lightPurple = new Color(173, 153, 255);
    static Color lightRed = new Color(252, 157, 142);

    public Teacher_ViewStudentProgress(Student student, Teacher teacher) {
        this.currentTeacher = teacher;
        
        setTitle("CyberDuck - Student Progress");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Student Progress: " + student.getFullName());
        headerLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        headerLabel.setForeground(lightPurple);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel chartPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g, student);
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);

        String[] columnNames = {"Date", "Question Title", "Correct?"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attemptsTable = new JTable(tableModel);
        attemptsTable.setBackground(Color.WHITE);
        attemptsTable.setForeground(lightPurple);
        attemptsTable.setGridColor(lightPurple);
        attemptsTable.getTableHeader().setBackground(lightPurple);
        attemptsTable.getTableHeader().setForeground(Color.WHITE);

        List<Attempt> attempts = student.getAllAttempts();
        for (Attempt attempt : attempts) {
            String questionTitle = "";
            for (Question question : Run.AllQuestions) {
                if (question.getQuestionID() == attempt.getQuestionID()) {
                    questionTitle = question.getQuestionTitle();
                    break;
                }
            }
            tableModel.addRow(new Object[]{
                attempt.getDateCompleted(),
                questionTitle,
                attempt.getIs_Correct()
            });
        }
    
        JScrollPane tableScrollPane = new JScrollPane(attemptsTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setForeground(lightPurple);
        searchButton.setBackground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(true);
        searchButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            attemptsTable.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(chartPanel, BorderLayout.NORTH);
        centerPanel.add(searchPanel, BorderLayout.CENTER);
        centerPanel.add(tableScrollPane, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(lightRed);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        backButton.setOpaque(true);
        backButton.setPreferredSize(new Dimension(70, 20));
        backButton.addActionListener(e -> {
            dispose();
            new Teacher_StudentInfo(currentTeacher).setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    //Graphics2D Source (See Crit C Source 29)
    private void drawBarChart(Graphics g, Student student) {
        // Define chart dimensions and spacing
        int width = 400;
        int height = 300;
        int barWidth = 80;
        int spacing = 60;
        int bottomMargin = 50;
        int leftMargin = 50;

        // Calculate statistics from student data
        int correctQuestions = student.getCorrectAttempts();
        int incorrectQuestions = student.getIncorrectAttempts();
        int totalQuestions = correctQuestions + incorrectQuestions;
        
        // Calculate percentages, handling division by zero
        double correctPercentage = totalQuestions == 0 ? 0 : (double) correctQuestions / totalQuestions * 100;
        double incorrectPercentage = totalQuestions == 0 ? 0 : (double) incorrectQuestions / totalQuestions * 100;

        // Enable anti-aliasing for smoother rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw chart title
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(lightPurple);
        String title = "Question Performance (%)";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (width - titleWidth) / 2, 30);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(leftMargin, height - bottomMargin, width - leftMargin, height - bottomMargin); // X-axis
        g2d.drawLine(leftMargin, 40, leftMargin, height - bottomMargin); // Y-axis

        // Calculate bar positions
        int x1 = (width - (2 * barWidth + spacing)) / 2;
        int x2 = x1 + barWidth + spacing;

        // Draw bars for correct and incorrect attempts
        drawBar(g2d, x1, correctPercentage, correctQuestions, height, bottomMargin, barWidth, Color.GREEN, "Correct");
        drawBar(g2d, x2, incorrectPercentage, incorrectQuestions, height, bottomMargin, barWidth, Color.RED, "Incorrect");

        // Draw percentage scale on Y-axis
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i <= 100; i += 20) {
            int y = height - bottomMargin - (i * (height - bottomMargin - 40) / 100);
            g2d.drawString(i + "%", leftMargin - 35, y + 5);
            g2d.drawLine(leftMargin - 5, y, leftMargin, y);
        }

        // Draw total questions count at bottom
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Total Questions: " + totalQuestions, 
            (width - g2d.getFontMetrics().stringWidth("Total Questions: " + totalQuestions)) / 2, 
            height - 10);
    }

    private void drawBar(Graphics2D g2d, int x, double percentage, int value, int height, 
                        int bottomMargin, int barWidth, Color color, String label) {
        // Calculate bar height based on percentage
        int barHeight = (int) (percentage * (height - bottomMargin - 40) / 100);
        int y = height - bottomMargin - barHeight;

        // Draw filled bar
        g2d.setColor(color);
        g2d.fillRect(x, y, barWidth, barHeight);

        // Draw bar outline
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, barWidth, barHeight);

        // Draw percentage above bar
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String percentageStr = String.format("%.1f%%", percentage);
        int strWidth = g2d.getFontMetrics().stringWidth(percentageStr);
        g2d.drawString(percentageStr, x + (barWidth - strWidth) / 2, y - 20);

        // Draw value above percentage
        String valueStr = String.valueOf(value);
        strWidth = g2d.getFontMetrics().stringWidth(valueStr);
        g2d.drawString(valueStr, x + (barWidth - strWidth) / 2, y - 5);

        // Draw label below bar
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        int labelWidth = g2d.getFontMetrics().stringWidth(label);
        g2d.drawString(label, x + (barWidth - labelWidth) / 2, height - bottomMargin + 20);
    }
}
