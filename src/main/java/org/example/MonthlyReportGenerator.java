package org.example;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MonthlyReportGenerator extends JFrame {

    private Connection connection;

    public MonthlyReportGenerator() {
        setTitle("Rebooter's System - Monthly Report Generator");
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

        // Initialize DB connection
        initializeDBConnection();

        // Generate the monthly report
        generateMonthlyReport();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("Monthly Report Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnBackHome = createStyledButton("Back to Home");

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(btnBackHome, gbc);

        return contentPanel;
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
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose(); // Close the current page
                        break;
                    default:
                        JOptionPane.showMessageDialog(MonthlyReportGenerator.this,
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

    private void generateMonthlyReport() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Monthly_Report.pdf"));
            document.open();

            // Generate Sales Report
            generateSalesReport(document);

            // View Top-Selling Items
            viewTopSellingItems(document);

            // View Areas for Improvement
            viewAreasForImprovement(document);

            document.close();
            JOptionPane.showMessageDialog(this, "Monthly report generated successfully!", "Report Generation", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating the monthly report.", "Report Generation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSalesReport(Document document) throws Exception {
        document.add(new Paragraph("Sales Report:"));
        document.add(new Paragraph(" "));

        Statement stmt = connection.createStatement();
        String query = "SELECT OrderID, Device, ServiceType, TotalPrice " +
                "FROM CustomerOrders";
        ResultSet rs = stmt.executeQuery(query);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Add table header
        addCell(table, "Order ID", Element.ALIGN_CENTER);
        addCell(table, "Device Name", Element.ALIGN_CENTER);
        addCell(table, "Service Type", Element.ALIGN_CENTER);
        addCell(table, "Total Price", Element.ALIGN_CENTER);

        double totalAmount = 0;

        while (rs.next()) {
            int orderId = rs.getInt("OrderID");
            String device = rs.getString("Device");
            String serviceType = rs.getString("ServiceType");
            double totalPrice = rs.getDouble("TotalPrice");

            addCell(table, String.valueOf(orderId), Element.ALIGN_CENTER);
            addCell(table, device, Element.ALIGN_LEFT);
            addCell(table, serviceType, Element.ALIGN_LEFT);
            addCell(table, String.format("Rs. %.2f", totalPrice), Element.ALIGN_CENTER);

            totalAmount += totalPrice;
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total Amount: Rs. " + String.format("%.2f", totalAmount)));

        rs.close();
        stmt.close();
    }

    private void viewTopSellingItems(Document document) throws Exception {
        document.add(new Paragraph("Top-Selling Items:"));
        document.add(new Paragraph(" "));

        Statement stmt = connection.createStatement();

        // Correct the query to match the actual database structure
        String query = "SELECT TOP 5 j.JobName, COUNT(co.OrderID) AS TotalJobs, SUM(co.TotalPrice) AS TotalRevenue " +
                "FROM Jobs j " +
                "JOIN CustomerOrders co ON j.JobID = co.OrderID " + // Adjust join condition
                "GROUP BY j.JobName " +
                "ORDER BY TotalJobs DESC";

        ResultSet rs = stmt.executeQuery(query);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Add table header
        addCell(table, "Job Name", Element.ALIGN_CENTER);
        addCell(table, "Total Jobs", Element.ALIGN_CENTER);
        addCell(table, "Total Revenue", Element.ALIGN_CENTER);

        while (rs.next()) {
            String jobName = rs.getString("JobName");
            int totalJobs = rs.getInt("TotalJobs");
            double totalRevenue = rs.getDouble("TotalRevenue");

            addCell(table, jobName, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(totalJobs), Element.ALIGN_CENTER);
            addCell(table, String.format("Rs. %.2f", totalRevenue), Element.ALIGN_CENTER);
        }

        document.add(table);
        rs.close();
        stmt.close();
    }

    private void viewAreasForImprovement(Document document) throws Exception {
        document.add(new Paragraph("Areas for Improvement:"));
        document.add(new Paragraph(" "));

        Statement stmt = connection.createStatement();
        String query = "SELECT JobName, COUNT(*) AS TotalJobs " +
                "FROM Jobs WHERE Work_Status <> 'Completed' " +
                "GROUP BY JobName ORDER BY TotalJobs ASC";
        ResultSet rs = stmt.executeQuery(query);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Add table header
        addCell(table, "Job Name", Element.ALIGN_CENTER);
        addCell(table, "Total Uncompleted Jobs", Element.ALIGN_CENTER);

        while (rs.next()) {
            String jobName = rs.getString("JobName");
            int totalJobs = rs.getInt("TotalJobs");

            addCell(table, jobName, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(totalJobs), Element.ALIGN_CENTER);
        }

        document.add(table);
        rs.close();
        stmt.close();
    }

    private void addCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(8);
        table.addCell(cell);
    }
}