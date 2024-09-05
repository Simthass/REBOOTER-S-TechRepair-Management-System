package org.example;

import org.example.RebootersSystemHome;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageInventory extends JFrame {

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
            "encrypt=true;trustServerCertificate=true; integratedSecurity=true";

    public ManageInventory() {
        setTitle("Rebooter's System - Manage Inventory");
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

        // Load inventory from database
        loadInventory();

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

    private void loadInventory() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Inventory");

            // Clear existing table data
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ItemID"));
                row.add(rs.getString("ItemName"));
                row.add(rs.getInt("Quantity"));
                row.add(rs.getInt("ReorderLevel"));
                row.add(rs.getInt("SupplierID"));
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading inventory", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("Manage Inventory");
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
        String[] columnNames = {"Item ID", "Item Name", "Quantity", "Reorder Level", "Supplier ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = createStyledButton("Add Item");
        JButton updateButton = createStyledButton("Update Item");
        JButton removeButton = createStyledButton("Remove Item");
        JButton checkLowStockButton = createStyledButton("Check Low Stock");
        JButton backButton = createStyledButton("Back to Home");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(checkLowStockButton);
        buttonsPanel.add(backButton);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void addItem() {
        JTextField itemNameField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField reorderLevelField = new JTextField(20);
        JTextField supplierIDField = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(itemNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(quantityField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Reorder Level:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(reorderLevelField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Supplier ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(supplierIDField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO Inventory (ItemName, Quantity, ReorderLevel, SupplierID) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, itemNameField.getText());
                pstmt.setInt(2, Integer.parseInt(quantityField.getText()));
                pstmt.setInt(3, Integer.parseInt(reorderLevelField.getText()));
                pstmt.setInt(4, Integer.parseInt(supplierIDField.getText()));
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventory(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField itemNameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString(), 20);
        JTextField quantityField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString(), 20);
        JTextField reorderLevelField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString(), 20);
        JTextField supplierIDField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(itemNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(quantityField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Reorder Level:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(reorderLevelField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Supplier ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(supplierIDField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE Inventory SET ItemName=?, Quantity=?, ReorderLevel=?, SupplierID=? WHERE ItemID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, itemNameField.getText());
                pstmt.setInt(2, Integer.parseInt(quantityField.getText()));
                pstmt.setInt(3, Integer.parseInt(reorderLevelField.getText()));
                pstmt.setInt(4, Integer.parseInt(supplierIDField.getText()));
                pstmt.setInt(5, itemId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventory(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM Inventory WHERE ItemID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, itemId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Item deleted successfully");
                loadInventory(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting item", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkLowStock() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Inventory WHERE Quantity <= ReorderLevel");

            StringBuilder message = new StringBuilder("Low stock items:\n\n");
            boolean hasLowStock = false;

            while (rs.next()) {
                hasLowStock = true;
                message.append(rs.getString("ItemName"))
                        .append(" - Quantity: ")
                        .append(rs.getInt("Quantity"))
                        .append(" (Reorder Level: ")
                        .append(rs.getInt("ReorderLevel"))
                        .append(")\n");
            }

            rs.close();
            stmt.close();

            if (hasLowStock) {
                JOptionPane.showMessageDialog(this, message.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No items are low in stock.", "Stock Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking low stock", "Database Error", JOptionPane.ERROR_MESSAGE);
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
                    case "Add Item":
                        addItem();
                        break;
                    case "Update Item":
                        updateItem();
                        break;
                    case "Remove Item":
                        deleteItem();
                        break;
                    case "Check Low Stock":
                        checkLowStock();
                        break;
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose(); // Close the current page
                        break;
                    default:
                        JOptionPane.showMessageDialog(ManageInventory.this,
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