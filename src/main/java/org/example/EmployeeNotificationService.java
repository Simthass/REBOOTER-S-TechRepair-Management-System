package org.example;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.sql.*;
import java.util.Properties;

public class EmployeeNotificationService {

    private Connection connection;

    public EmployeeNotificationService() {
        initializeDBConnection();
        monitorJobAssignments();
    }

    private void initializeDBConnection() {
        try {
            String DB_URL = "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
                    "encrypt=true;trustServerCertificate=true;integratedSecurity=true";
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void monitorJobAssignments() {
        try {
            // Query to find jobs that have been recently assigned
            String query = "SELECT j.JobID, j.JobName, j.RequiredSkills, j.AssignedEmployees, " +
                    "e.Email, e.FirstName, e.LastName " +
                    "FROM Jobs j " +
                    "CROSS APPLY STRING_SPLIT(j.AssignedEmployees, ',') s " +
                    "JOIN Employees e ON e.FirstName + ' ' + e.LastName = TRIM(s.value) " +
                    "WHERE j.Emp_Status = 'Assigned'";

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String employeeEmail = rs.getString("Email");
                    String employeeName = rs.getString("FirstName") + " " + rs.getString("LastName");
                    String jobName = rs.getString("JobName");
                    String requiredSkills = rs.getString("RequiredSkills");

                    String subject = "New Job Assignment: " + jobName;
                    String message = String.format("Dear %s,\n\nYou have been assigned to a new job:\n\nJob Name: %s\nRequired Skills: %s\n\nPlease review the details and prepare accordingly.\n\nBest regards,\nTech Repair Team",
                            employeeName, jobName, requiredSkills);

                    sendEmailNotification(employeeEmail, subject, message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error monitoring job assignments: " + e.getMessage(), "Monitoring Error", JOptionPane.ERROR_MESSAGE);
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
            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error sending email notification.", "Email Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setVisible(boolean b) {
        // This method is empty as it's not applicable for a non-GUI class
    }
}