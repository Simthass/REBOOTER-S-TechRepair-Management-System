package com.rebooters.techrepair;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class NotificationServiceCustomer extends JFrame {

    private Connection connection;
    private JTable ordersTable;
    private JTextArea emailBodyTextArea;

    public NotificationServiceCustomer() {
        setTitle("Rebooter's System - Send Notifications to Customers");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeDBConnection();
        initializeUI();
        loadCompletedOrders();
    }

    private void initializeDBConnection() {
        try {
            String DB_URL = "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
                    "encrypt=true;trustServerCertificate=true;integratedSecurity=true";
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table to show completed orders
        ordersTable = new JTable();
        ordersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ordersTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(ordersTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Completed Orders"));
        tableScrollPane.setPreferredSize(new Dimension(1000, 300)); // Set a preferred size for the table
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton sendEmailButton = createStyledButton("Send Email");
        sendEmailButton.addActionListener(e -> sendEmails());
        JButton backButton = createStyledButton("Back to Home");
        backButton.addActionListener(e -> goBackToHome());
        buttonsPanel.add(sendEmailButton);
        buttonsPanel.add(backButton);

        // Email body panel
        JPanel emailBodyPanel = new JPanel(new BorderLayout(5, 5));
        emailBodyPanel.setBorder(BorderFactory.createTitledBorder("Email Body"));

        emailBodyTextArea = new JTextArea(10, 40);
        emailBodyTextArea.setText("Dear [CustomerName],\n\nYour device ([Device]) serviced for [ServiceType] is ready for collection.\n\nThank you for choosing Rebooter's Tech Repair Service.\n\nBest regards,\nRebooter's Tech Repair Team");
        emailBodyTextArea.setWrapStyleWord(true);
        emailBodyTextArea.setLineWrap(true);
        emailBodyTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane textAreaScrollPane = new JScrollPane(emailBodyTextArea);
        emailBodyPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        contentPanel.add(emailBodyPanel, BorderLayout.SOUTH);



        // Add buttons panel to the bottom of the content panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(emailBodyPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1000, 80));

        JLabel titleLabel = new JLabel("Send Notifications to Customers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(41, 128, 185));
        footerPanel.setPreferredSize(new Dimension(1000, 30));

        JLabel footerLabel = new JLabel("© 2024 Rebooter's Tech Repair. All rights reserved.");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.add(footerLabel, BorderLayout.CENTER);

        return footerPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    private void loadCompletedOrders() {
        try {
            Statement stmt = connection.createStatement();
            String query = "SELECT OrderID, CustomerName, Device, ServiceType, Status, Customer_Email " +
                    "FROM CustomerOrders WHERE Status = 'Completed'";

            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new Object[]{"OrderID", "CustomerName", "Device", "ServiceType", "Status", "Customer_Email"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("OrderID"),
                        rs.getString("CustomerName"),
                        rs.getString("Device"),
                        rs.getString("ServiceType"),
                        rs.getString("Status"),
                        rs.getString("Customer_Email")
                });
            }
            ordersTable.setModel(model);
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer orders.", "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendEmails() {
        int[] selectedRows = ordersTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No customers selected.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i : selectedRows) {
            String customerEmail = ordersTable.getValueAt(i, 5).toString();
            String customerName = ordersTable.getValueAt(i, 1).toString();
            String device = ordersTable.getValueAt(i, 2).toString();
            String serviceType = ordersTable.getValueAt(i, 3).toString();

            String subject = "Your Device is Ready for Collection!";
            String message = emailBodyTextArea.getText()
                    .replace("[CustomerName]", customerName)
                    .replace("[Device]", device)
                    .replace("[ServiceType]", serviceType);

            sendEmailNotification(customerEmail, subject, message);
        }
    }

    private void sendEmailNotification(String recipientEmail, String subject, String messageText) {
        // Email configuration
        String senderEmail = "rebootersrepairservice@gmail.com";  // Replace with your Gmail address
        String senderPassword = "ytti wrpl iqhw ofan ";  // Replace with your App Password

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            JOptionPane.showMessageDialog(this, "Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending email to " + recipientEmail, "Email Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToHome() {
        this.dispose();
        new RebootersSystemHome().setVisible(true);
    }


}