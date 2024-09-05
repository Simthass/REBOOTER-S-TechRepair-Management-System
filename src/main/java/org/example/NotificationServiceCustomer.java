package org.example;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class NotificationServiceCustomer {

    private Connection connection;

    public NotificationServiceCustomer() {
        initializeDBConnection();
        monitorCustomerOrders();
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

    private void monitorCustomerOrders() {
        try {
            Statement stmt = connection.createStatement();
            String query = "SELECT OrderID, CustomerName, Device, ServiceType, Status, Customer_Email " +
                    "FROM CustomerOrders WHERE Status = 'Completed'";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String customerEmail = rs.getString("Customer_Email");
                String customerName = rs.getString("CustomerName");
                String device = rs.getString("Device");
                String serviceType = rs.getString("ServiceType");

                String subject = "Your Device is Ready for Collection!";
                String message = String.format("Dear %s,\n\nYour device (%s) serviced for %s is ready for collection.\n\nThank you for choosing Rebooter's Tech Repair Service.\n\nBest regards,\nRebooter's Tech Repair Team",
                        customerName, device, serviceType);

                sendEmailNotification(customerEmail, subject, message);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error monitoring customer orders.", "Monitoring Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null,"Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error sending email notification.", "Email Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void setVisible(boolean b) {
    }
}
