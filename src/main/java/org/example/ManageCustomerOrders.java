package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ManageCustomerOrders extends JFrame {

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
            "encrypt=true;trustServerCertificate=true;integratedSecurity=true";

    public ManageCustomerOrders() {
        setTitle("Rebooter's System - Manage Customer Orders");
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
            JOptionPane.showMessageDialog(this, "Failed to connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
        }

        loadOrders();

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

    private void loadOrders() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CustomerOrders");

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("OrderID"));
                row.add(rs.getString("CustomerName"));
                row.add(rs.getString("Device"));
                row.add(rs.getString("ServiceType"));
                row.add(rs.getString("Status"));
                row.add(rs.getString("TotalPrice"));
                row.add(rs.getString("Customer_Email"));// Correct column name
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("Manage Customer Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"Order ID", "Customer Name", "Device", "Service Type", "Status", "Total Price", "Customer_Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = createStyledButton("Add Order");
        JButton updateButton = createStyledButton("Update Order");
        JButton removeButton = createStyledButton("Remove Order");
        JButton backButton = createStyledButton("Back to Home");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(backButton);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void addOrder() {
        JTextField customerNameField = new JTextField(20);
        JTextField deviceField = new JTextField(20);
        JTextField serviceTypeField = new JTextField(20);
        JTextField TotalPriceField = new JTextField(20);
        JTextField EmailField = new JTextField(20);
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed", "Cancelled"});

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(customerNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Device:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(deviceField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Service Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(serviceTypeField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(EmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(statusField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Total Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(TotalPriceField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Order",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO CustomerOrders (CustomerName, Device, ServiceType, Status, TotalPrice, Customer_Email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, customerNameField.getText());
                pstmt.setString(2, deviceField.getText());
                pstmt.setString(3, serviceTypeField.getText());
                pstmt.setString(4, statusField.getSelectedItem().toString());
                pstmt.setString(5, TotalPriceField.getText());
                pstmt.setString(6, EmailField.getText());
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Order added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField customerNameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString(), 20);
        JTextField deviceField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString(), 20);
        JTextField serviceTypeField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString(), 20);
        JTextField TotalPriceField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString(), 20);
        JTextField EmailField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString(), 20);        JComboBox<String> statusField = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed", "Cancelled"});
        statusField.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(customerNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Device:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(deviceField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Service Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(serviceTypeField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(EmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(statusField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Total Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(TotalPriceField, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel, "Update Order",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE CustomerOrders SET CustomerName=?, Device=?, ServiceType=?, Status=?, TotalPrice=?, Customer_Email=? WHERE OrderID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, customerNameField.getText());
                pstmt.setString(2, deviceField.getText());
                pstmt.setString(3, serviceTypeField.getText());
                pstmt.setString(4, statusField.getSelectedItem().toString());
                pstmt.setString(5, TotalPriceField.getText());
                pstmt.setString(6, EmailField.getText());
                pstmt.setInt(7, orderId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Order updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM CustomerOrders WHERE OrderID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
                pstmt.close();
                JOptionPane.showMessageDialog(this, "Order deleted successfully");
                loadOrders();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting order", "Database Error", JOptionPane.ERROR_MESSAGE);
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
                    case "Add Order":
                        addOrder();
                        break;
                    case "Update Order":
                        updateOrder();
                        break;
                    case "Remove Order":
                        deleteOrder();
                        break;
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose();
                        break;
                    default:
                        JOptionPane.showMessageDialog(ManageCustomerOrders.this,
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
