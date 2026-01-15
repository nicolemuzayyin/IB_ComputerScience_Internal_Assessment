package package_IA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Teacher_StudentInfo extends JFrame {

	private static final long serialVersionUID = 1L;
	private Teacher currentTeacher;
	private ArrayList<Student> allStudents = new ArrayList<>();
	private JPanel contentPane;
	private JTable studentTable;
	private DefaultTableModel model;
	static Color lightPurple = new Color(173, 153, 255);
	static Color lightRed = new Color(252, 157, 142);

	public Teacher_StudentInfo(Teacher teacher) {
		this.currentTeacher = teacher;

		setTitle("CyberDuck - Manage Students");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblStudentInfo = new JLabel("Student Info");
		lblStudentInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblStudentInfo.setFont(new Font("Arial", Font.PLAIN, 24));
		lblStudentInfo.setForeground(lightPurple);
		lblStudentInfo.setBounds(0, 20, 600, 30);
		contentPane.add(lblStudentInfo);

		String[] columnNames = { "Student Name", "Class Name", "Grade Level", "IB Level", "View Student Progress" };
		model = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4;
			}
		};

		studentTable = new JTable(model);
		studentTable.setBackground(Color.WHITE);
		studentTable.setForeground(lightPurple);
		studentTable.setGridColor(lightPurple);
		studentTable.getTableHeader().setBackground(lightPurple);
		studentTable.getTableHeader().setForeground(Color.WHITE);

		studentTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		studentTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		studentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		studentTable.getColumnModel().getColumn(4).setPreferredWidth(100);

		TableColumn viewDetailsColumn = studentTable.getColumnModel().getColumn(4);
		viewDetailsColumn.setCellRenderer(new ButtonRenderer());

		ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());
		buttonEditor.setActionListener(e -> {
			int selectedRow = studentTable.getSelectedRow();
			if (selectedRow != -1) {
				viewStudentDetails(selectedRow);
			}
		});
		viewDetailsColumn.setCellEditor(buttonEditor);

		JScrollPane scrollPane = new JScrollPane(studentTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(50, 60, 500, 250);
		contentPane.add(scrollPane);

		studentTable.setFillsViewportHeight(true);

		JButton backButton = new JButton("Back");
		backButton.setBounds(450, 320, 100, 30);
		backButton.setForeground(Color.WHITE);
		backButton.setBackground(lightRed);
		backButton.setFocusPainted(false);
		backButton.setBorderPainted(true);
		backButton.setBorder(BorderFactory.createLineBorder(lightPurple, 4));
		backButton.setOpaque(true);
		contentPane.add(backButton);
		
		JButton deleteButton = new JButton("Delete Student");
		deleteButton.setForeground(new Color(173, 153, 255));
		deleteButton.setFocusPainted(false);
		deleteButton.setBorderPainted(true);
		deleteButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
		deleteButton.setBackground(Color.WHITE);
		deleteButton.setBounds(50, 322, 100, 30);
		contentPane.add(deleteButton);
		
		JButton editButton = new JButton("Edit Student");
		editButton.setForeground(new Color(173, 153, 255));
		editButton.setFocusPainted(false);
		editButton.setBorderPainted(true);
		editButton.setBorder(BorderFactory.createLineBorder(lightPurple, 1));
		editButton.setBackground(Color.WHITE);
		editButton.setBounds(162, 322, 100, 30);
		contentPane.add(editButton);

		deleteButton.addActionListener(e -> {
			dispose();
			new Teacher_DeleteStudent(currentTeacher).setVisible(true);
		});
		
		editButton.addActionListener(e -> {
			dispose();
			new Teacher_EditStudent(currentTeacher).setVisible(true);
		});
		
		backButton.addActionListener(e -> {
			dispose();
			new Teacher_MainMenu(currentTeacher).setVisible(true);
		});
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent windowEvent) {
                loadStudentData();
            }
        });
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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText((value == null) ? "" : value.toString());
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
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
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

	private void loadStudentData() {
	    model.setRowCount(0);
	    allStudents.clear();
	    

	    for (Student student : Run.AllStudents) {
	        for (Class c : currentTeacher.getAllClasses()) {
	            if (student.getClassID() == c.getClassID()) {
	                model.addRow(new Object[] { 
	                    student.getFullName(), 
	                    c.getClassName(), 
	                    student.getGradeLevel(),
	                    student.getIBLevel(),
	                    "View Details"
	                });
	                allStudents.add(student);
	                break; 
	            }
	        }
	    }
	    
	    model.fireTableDataChanged();
	}

	private void viewStudentDetails(int row) {
		String studentName = (String) model.getValueAt(row, 0);
		Student student = null;

		for (Student s : Run.AllStudents) {
			if (s.getFullName().equalsIgnoreCase(studentName)) {
				student = s;
				break;
			}
		}
		Teacher_ViewStudentProgress viewStudentProgressGUI = new Teacher_ViewStudentProgress(student, currentTeacher);
		viewStudentProgressGUI.setVisible(true);
		dispose();
	}
}
