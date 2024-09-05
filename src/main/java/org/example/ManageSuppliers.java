package org.example;

import org.example.RebootersSystemHome;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageSuppliers extends JFrame {

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlserver://localhost:1434;databaseName=TechRepairDB;" +
            "encrypt=true;trustServerCertificate=true;integratedSecurity=true";

    public ManageSuppliers() {
        setTitle("Rebooter's System - Manage Suppliers");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        loadSuppliers();

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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1200, 100));

        JLabel titleLabel = new JLabel("Rebooter's Tech Repairing System - Manage Suppliers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"Supplier ID", "Supplier Name", "Contact Person", "Email", "Phone", "Address", "Main Part Supplied"};
        tableModel = new DefaultTableModel(columnNames, 0);
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = createStyledButton("Add Supplier");
        JButton updateButton = createStyledButton("Update Supplier");
        JButton removeButton = createStyledButton("Remove Supplier");
        JButton backButton = createStyledButton("Back to Home");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(backButton);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (text) {
                    case "Add Supplier":
                        addSupplier();
                        break;
                    case "Update Supplier":
                        updateSupplier();
                        break;
                    case "Remove Supplier":
                        removeSupplier();
                        break;
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose(); // Close the current page
                        break;
                }
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(41, 128, 185));
        footerPanel.setPreferredSize(new Dimension(1200, 30));

        JLabel footerLabel = new JLabel("© 2024 Tech Repair. All rights reserved.");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        return footerPanel;
    }

    private void loadSuppliers() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Suppliers");

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("SupplierID"));
                row.add(rs.getString("SupplierName"));
                row.add(rs.getString("ContactPerson"));
                row.add(rs.getString("Email"));
                row.add(rs.getString("Phone"));
                row.add(rs.getString("Address"));
                row.add(rs.getString("MainPartSupplied"));
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSupplier() {
        JTextField nameField = new JTextField(20);
        JTextField contactPersonField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField partsField = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Supplier Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(contactPersonField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(addressField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Parts Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(partsField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO Suppliers (SupplierName, ContactPerson, Email, Phone, Address, MainPartSupplied) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, contactPersonField.getText());
                pstmt.setString(3, emailField.getText());
                pstmt.setString(4, phoneField.getText());
                pstmt.setString(5, addressField.getText());
                pstmt.setString(6, partsField.getText());
                pstmt.executeUpdate();

                loadSuppliers();
                JOptionPane.showMessageDialog(this, "Supplier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to update.", "No Supplier Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString(), 20);
        JTextField contactPersonField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString(), 20);
        JTextField emailField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString(), 20);
        JTextField phoneField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString(), 20);
        JTextField addressField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString(), 20);
        JTextField partsField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Supplier Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(contactPersonField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(addressField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Parts Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(partsField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE Suppliers SET SupplierName = ?, ContactPerson = ?, Email = ?, Phone = ?, Address = ?, MainPartSupplied = ? WHERE SupplierID = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, contactPersonField.getText());
                pstmt.setString(3, emailField.getText());
                pstmt.setString(4, phoneField.getText());
                pstmt.setString(5, addressField.getText());
                pstmt.setString(6, partsField.getText());
                pstmt.setInt(7, (int) tableModel.getValueAt(selectedRow, 0));
                pstmt.executeUpdate();

                loadSuppliers();
                JOptionPane.showMessageDialog(this, "Supplier updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to remove.", "No Supplier Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, (int) tableModel.getValueAt(selectedRow, 0));
                pstmt.executeUpdate();

                loadSuppliers();
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
