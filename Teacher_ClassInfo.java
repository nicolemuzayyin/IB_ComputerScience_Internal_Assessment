package package_IA;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class Teacher_ClassInfo extends JFrame {
    private static final long serialVersionUID = 1L;
    private Teacher currentTeacher;
    private JPanel contentPane;
    private JTable classTable;
    private DefaultTableModel model;
    static Color lightPurple = new Color(173, 153, 255);
    static Color lightRed = new Color(252, 157, 142);

    public Teacher_ClassInfo(Teacher teacher) {
        this.currentTeacher = teacher;

        setTitle("CyberDuck - Manage Classes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblClassInfo = new JLabel("Class Info");
        lblClassInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblClassInfo.setFont(new Font("Arial", Font.PLAIN, 24));
        lblClassInfo.setForeground(lightPurple);
        lblClassInfo.setBounds(0, 20, 600, 30);
        contentPane.add(lblClassInfo);


        String[] columnNames = { "Class Name", "Number of Students", "Student Info" };
        model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        classTable = new JTable(model);
        classTable.setBackground(Color.WHITE);
        classTable.setForeground(lightPurple);
        classTable.setGridColor(lightPurple);
        classTable.getTableHeader().setBackground(lightPurple);
        classTable.getTableHeader().setForeground(Color.WHITE);

        classTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        classTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        classTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        TableColumn viewStudentsColumn = classTable.getColumnModel().getColumn(2);
        viewStudentsColumn.setCellRenderer(new ButtonRenderer());
        
        ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
        buttonEditor.setActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow != -1) {
                viewStudents(selectedRow);
            }
        });
        viewStudentsColumn.setCellEditor(buttonEditor);

        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(50, 60, 500, 250);
        contentPane.add(scrollPane);

        classTable.setFillsViewportHeight(true);

        JButton addButton = new JButton("Add Class");
        addButton.setBounds(50, 320, 100, 30);
        addButton.setForeground(lightPurple);
        addButton.setBackground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(true);
        addButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

        JButton deleteButton = new JButton(" Delete Class");
        deleteButton.setBounds(160, 320, 100, 30);
        deleteButton.setForeground(lightPurple);
        deleteButton.setBackground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(true);
        deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

        JButton editButton = new JButton("Edit Class");
        editButton.setBounds(270, 320, 100, 30);
        editButton.setForeground(lightPurple);
        editButton.setBackground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(true);
        editButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));

        JButton backButton = new JButton("Back");
        backButton.setBounds(450, 320, 100, 30);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(lightRed);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
        backButton.setOpaque(true);

        contentPane.add(addButton);
        contentPane.add(deleteButton);
        contentPane.add(editButton);
        contentPane.add(backButton);

        backButton.addActionListener(e -> {
            dispose();
            new Teacher_MainMenu(currentTeacher).setVisible(true);
        });

        addButton.addActionListener(e -> {
            new Teacher_AddNewClass(currentTeacher).setVisible(true);
            dispose();
        });

        deleteButton.addActionListener(e -> {
            new Teacher_DeleteClass(currentTeacher).setVisible(true);
            dispose();
        });

        editButton.addActionListener(e -> {
            new Teacher_EditClass(currentTeacher).setVisible(true);
            dispose();
        });

        loadClassData();
        setVisible(true);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
            setOpaque(true);
            setForeground(lightPurple);
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(true);
            setBorder(BorderFactory.createLineBorder(lightPurple, 1));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(lightPurple);
            setBackground(Color.WHITE);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
		protected JButton button;
        private String label;
        private boolean isPushed;
        private ActionListener actionListener;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public void setActionListener(ActionListener actionListener) {
            this.actionListener = actionListener;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(lightPurple);
                button.setBackground(Color.WHITE);
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && actionListener != null) {
                actionListener.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, label));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void loadClassData() {
        model.setRowCount(0);

        for (Class c : currentTeacher.AllClasses) {
            int studentCount = c.getStudentList().size();
            model.addRow(new Object[]{c.getClassName(), studentCount, "View Students"});
        }
    }
    

    private void viewStudents(int row) {
        // Get the class name from the selected row in the table
        String className = (String) model.getValueAt(row, 0);

        // Initialize variable to store the found class
        Class selectedClass = null;
        // Search for the class in teacher's class list
        for (Class c : currentTeacher.AllClasses) {
            if (c.getClassName().equalsIgnoreCase(className)) {
                selectedClass = c;
                break;
            }
        }

        // If class is found, open the view students window
        if (selectedClass != null) {
            Teacher_ViewStudentsOfClass viewStudentsFrame = new Teacher_ViewStudentsOfClass(currentTeacher, selectedClass);
            viewStudentsFrame.refreshData(); 
            viewStudentsFrame.setVisible(true);
            dispose();
        } else {
            // Show error if class is not found
            JOptionPane.showMessageDialog(this, "Class not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
