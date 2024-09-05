package com.rebooters.techrepair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageEmployees extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
            "encrypt=true;trustServerCertificate=true; integratedSecurity=true";

    public ManageEmployees() {
        setTitle("Rebooter's System - Manage Employees");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create and add header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create and add content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Create and add footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load employees from database
        loadEmployees();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadEmployees() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Employees");

            // Clear existing table data
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("EmployeeID"));
                row.add(rs.getString("FirstName"));
                row.add(rs.getString("LastName"));
                row.add(rs.getString("JobRole"));
                row.add(rs.getString("WorkingType"));
                row.add(rs.getString("Schedule"));
                row.add(rs.getString("ContactNumber"));
                row.add(rs.getString("Email"));
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("Manage Employees");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table
        String[] columnNames = {"ID", "First Name", "Last Name", "Job Role", "Working Type", "Schedule", "Contact Number", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = createStyledButton("Add Employee");
        JButton updateButton = createStyledButton("Update Employee");
        JButton removeButton = createStyledButton("Remove Employee");
        JButton backButton = createStyledButton("Back to Home");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(backButton);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void addEmployee() {
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField jobRoleField = new JTextField(20);
        JComboBox<String> workingTypeField = new JComboBox<>(new String[]{"Office", "Field"});
        JTextField scheduleField = new JTextField(20);
        JTextField contactNumberField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(firstNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(lastNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Job Role:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(jobRoleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Working Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(workingTypeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(scheduleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(contactNumberField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(emailField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO Employees (FirstName, LastName, JobRole, WorkingType, Schedule, ContactNumber, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, firstNameField.getText());
                pstmt.setString(2, lastNameField.getText());
                pstmt.setString(3, jobRoleField.getText());
                pstmt.setString(4, workingTypeField.getSelectedItem().toString());
                pstmt.setString(5, scheduleField.getText());
                pstmt.setString(6, contactNumberField.getText());
                pstmt.setString(7, emailField.getText());
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Employee added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField firstNameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString(), 20);
        JTextField lastNameField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString(), 20);
        JTextField jobRoleField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString(), 20);
        JComboBox<String> workingTypeField = new JComboBox<>(new String[]{"Office", "Field"});
        workingTypeField.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
        JTextField scheduleField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString(), 20);
        JTextField contactNumberField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString(), 20);
        JTextField emailField = new JTextField(tableModel.getValueAt(selectedRow, 7).toString(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(firstNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(lastNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Job Role:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(jobRoleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Working Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(workingTypeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(scheduleField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(contactNumberField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(emailField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE Employees SET FirstName=?, LastName=?, JobRole=?, WorkingType=?, Schedule=?, ContactNumber=?, Email=? WHERE EmployeeID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, firstNameField.getText());
                pstmt.setString(2, lastNameField.getText());
                pstmt.setString(3, jobRoleField.getText());
                pstmt.setString(4, workingTypeField.getSelectedItem().toString());
                pstmt.setString(5, scheduleField.getText());
                pstmt.setString(6, contactNumberField.getText());
                pstmt.setString(7, emailField.getText());
                pstmt.setInt(8, employeeId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Employee updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete");
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM Employees WHERE EmployeeID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, employeeId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Employee deleted successfully");
                loadEmployees(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting item", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (text) {
                    case "Add Employee":
                        addEmployee();
                        break;
                    case "Update Employee":
                        updateEmployee();
                        break;
                    case "Remove Employee":
                        deleteEmployee();
                        break;
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose(); // Close the current page
                        break;
                    default:
                        JOptionPane.showMessageDialog(ManageEmployees.this,
                                "You clicked: " + text, "Action", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(41, 128, 185));
        footerPanel.setPreferredSize(new Dimension(800, 30));

        JLabel footerLabel = new JLabel("© 2024 Tech Repair. All rights reserved.");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        return footerPanel;
    }
}