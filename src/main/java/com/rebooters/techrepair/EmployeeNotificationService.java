package com.rebooters.techrepair;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Properties;

public class EmployeeNotificationService extends JFrame {

    private Connection connection;
    private JTable jobsTable;
    private JTextArea emailBodyTextArea;

    public EmployeeNotificationService() {
        setTitle("Rebooter's System - Employee Job Notifications");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeDBConnection();
        initializeUI();
        loadAssignedJobs();
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

        // Table to show assigned jobs
        jobsTable = new JTable();
        jobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        jobsTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(jobsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Assigned Jobs"));
        tableScrollPane.setPreferredSize(new Dimension(1000, 300)); // Set a preferred size for the table
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Email body panel
        JPanel emailBodyPanel = new JPanel(new BorderLayout(5, 5));
        emailBodyPanel.setBorder(BorderFactory.createTitledBorder("Email Body"));

        emailBodyTextArea = new JTextArea(10, 40);
        emailBodyTextArea.setText("Dear [EmployeeName],\n\nYou have been assigned to a new job:\n\nJob Name: [JobName]\nRequired Skills: [RequiredSkills]\n\nPlease review the details and prepare accordingly.\n\nBest regards,\nTech Repair Team");
        emailBodyTextArea.setWrapStyleWord(true);
        emailBodyTextArea.setLineWrap(true);
        emailBodyTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane textAreaScrollPane = new JScrollPane(emailBodyTextArea);
        emailBodyPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton sendNotificationButton = createStyledButton("Send Notification");
        sendNotificationButton.addActionListener(e -> sendSelectedJobNotification());
        JButton backButton = createStyledButton("Back to Home");
        backButton.addActionListener(e -> goBackToHome());
        buttonsPanel.add(sendNotificationButton);
        buttonsPanel.add(backButton);

        // Add buttons panel and email body panel to the bottom of the content panel
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

        JLabel titleLabel = new JLabel("Employee Job Notifications");
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

    private void loadAssignedJobs() {
        try {
            String query = "SELECT JobID, JobName, RequiredSkills, AssignedEmployees, Work_Status FROM Jobs WHERE Emp_Status = 'Assigned'";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                DefaultTableModel model = new DefaultTableModel(new Object[]{"Job ID", "Job Name", "Required Skills", "Assigned Employees", "Work Status"}, 0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("JobID"),
                            rs.getString("JobName"),
                            rs.getString("RequiredSkills"),
                            rs.getString("AssignedEmployees"),
                            rs.getString("Work_Status")
                    });
                }
                jobsTable.setModel(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assigned jobs: " + e.getMessage(), "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendSelectedJobNotification() {
        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job to send notification.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jobName = jobsTable.getValueAt(selectedRow, 1).toString();
        String requiredSkills = jobsTable.getValueAt(selectedRow, 2).toString();
        String assignedEmployees = jobsTable.getValueAt(selectedRow, 3).toString();

        for (String employee : assignedEmployees.split(",")) {
            employee = employee.trim();
            try {
                String query = "SELECT Email, FirstName, LastName FROM Employees WHERE FirstName + ' ' + LastName = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, employee);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String employeeEmail = rs.getString("Email");
                            String employeeName = rs.getString("FirstName") + " " + rs.getString("LastName");

                            String subject = "New Job Assignment: " + jobName;
                            String message = emailBodyTextArea.getText()
                                    .replace("[EmployeeName]", employeeName)
                                    .replace("[JobName]", jobName)
                                    .replace("[RequiredSkills]", requiredSkills);

                            sendEmailNotification(employeeEmail, subject, message);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error sending notification: " + e.getMessage(), "Notification Error", JOptionPane.ERROR_MESSAGE);
            }
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
            JOptionPane.showMessageDialog(this, "Notification sent to " + recipientEmail, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending email: " + e.getMessage(), "Email Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToHome() {
        // Implement the logic to navigate back to the home screen
        this.dispose();
        new RebootersSystemHome().setVisible(true);
    }

}
