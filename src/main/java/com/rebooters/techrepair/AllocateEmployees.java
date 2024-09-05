package com.rebooters.techrepair;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class AllocateEmployees extends JFrame {
    private JTable jobTable, orderTable, employeeTable;
    private DefaultTableModel jobTableModel, orderTableModel, employeeTableModel;
    private Connection connection;
    private static final String DB_URL =
            "jdbc:sqlserver://SIMTHASS\\SQLEXPRESS:1434;databaseName=TechRepairDB;" +
                    "encrypt=true;trustServerCertificate=true; integratedSecurity=true";

    public AllocateEmployees() {
        setTitle("Rebooter's System - Allocate Employees to Jobs");
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

        loadJobs();
        loadPendingOrders();
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

    private void loadJobs() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Jobs");
            jobTableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("JobID"));
                row.add(rs.getString("JobName"));
                row.add(rs.getString("RequiredSkills"));
                row.add(rs.getString("AssignedEmployees"));
                row.add(rs.getString("Work_Status"));
                jobTableModel.addRow(row);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading jobs", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPendingOrders() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CustomerOrders WHERE Status = 'Pending'");
            orderTableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("OrderID"));
                row.add(rs.getString("CustomerName"));
                row.add(rs.getString("Device"));
                row.add(rs.getString("ServiceType"));
                row.add(rs.getString("Status"));
                orderTableModel.addRow(row);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEmployees() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Employees");
            employeeTableModel.setRowCount(0);
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
                employeeTableModel.addRow(row);
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
        JLabel titleLabel = new JLabel("Allocate Employees to Jobs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        String[] orderColumnNames = {"Order ID", "Customer Name", "Device", "Service Type", "Status"};
        orderTableModel = new DefaultTableModel(orderColumnNames, 0);
        orderTable = new JTable(orderTableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(25);
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        topPanel.add(orderScrollPane);

        String[] employeeColumnNames = {"Employee ID", "First Name", "Last Name", "Job Role", "Working Type", "Schedule", "Contact Number", "Email"};
        employeeTableModel = new DefaultTableModel(employeeColumnNames, 0);
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        JScrollPane employeeScrollPane = new JScrollPane(employeeTable);
        topPanel.add(employeeScrollPane);

        splitPane.setTopComponent(topPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        String[] jobColumnNames = {"Job ID", "Job Name", "Required Skills", "Assigned Employees", "Work_Status"};
        jobTableModel = new DefaultTableModel(jobColumnNames, 0);
        jobTable = new JTable(jobTableModel);
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobTable.setRowHeight(25);
        JScrollPane jobScrollPane = new JScrollPane(jobTable);
        bottomPanel.add(jobScrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton allocateButton = createStyledButton("Allocate Employees");
        JButton updateButton = createStyledButton("Update Allocation");
        JButton backButton = createStyledButton("Back to Home");
        buttonsPanel.add(allocateButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(backButton);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(bottomPanel);

        contentPanel.add(splitPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private void allocateEmployees() {
        int selectedOrderRow = orderTable.getSelectedRow();
        int selectedEmployeeRow = employeeTable.getSelectedRow();

        if (selectedOrderRow == -1 || selectedEmployeeRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order and an employee to allocate", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) orderTableModel.getValueAt(selectedOrderRow, 0);
        String orderServiceType = (String) orderTableModel.getValueAt(selectedOrderRow, 3);
        int employeeId = (int) employeeTableModel.getValueAt(selectedEmployeeRow, 0);
        String employeeName = (String) employeeTableModel.getValueAt(selectedEmployeeRow, 1) + " " + (String) employeeTableModel.getValueAt(selectedEmployeeRow, 2);
        String employeeEmail = (String) employeeTableModel.getValueAt(selectedEmployeeRow, 7);

        try {
            String sqlJob = "INSERT INTO Jobs (JobName, RequiredSkills, AssignedEmployees, Emp_Status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmtJob = connection.prepareStatement(sqlJob);
            pstmtJob.setString(1, orderServiceType);
            pstmtJob.setString(2, orderServiceType);
            pstmtJob.setString(3, String.valueOf(employeeId)); // Store EmployeeID instead of name
            pstmtJob.setString(4, "Assigned");
            pstmtJob.executeUpdate();
            pstmtJob.close();

            String sqlOrder = "UPDATE CustomerOrders SET Status = 'In Progress' WHERE OrderID = ?";
            PreparedStatement pstmtOrder = connection.prepareStatement(sqlOrder);
            pstmtOrder.setInt(1, orderId);
            pstmtOrder.executeUpdate();
            pstmtOrder.close();

            // Send email notification
            String subject = "New Job Assignment: " + orderServiceType;
            String message = String.format("Dear %s,\n\nYou have been assigned to a new job:\n\nJob Name: %s\nRequired Skills: %s\n\nPlease review the details and prepare accordingly.\n\nBest regards,\nTech Repair Team",
                    employeeName, orderServiceType, orderServiceType);
            sendEmailNotification(employeeEmail, subject, message);

            JOptionPane.showMessageDialog(this, "Employee allocated successfully and notification sent", "Success", JOptionPane.INFORMATION_MESSAGE);

            loadJobs();
            loadPendingOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error allocating employees: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAllocation() {
        int selectedJobRow = jobTable.getSelectedRow();

        if (selectedJobRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job to update", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jobId = (int) jobTableModel.getValueAt(selectedJobRow, 0);
        String currentStatus = (String) jobTableModel.getValueAt(selectedJobRow, 4);
        String currentEmployees = (String) jobTableModel.getValueAt(selectedJobRow, 3);

        JComboBox<String> statusField = new JComboBox<>(new String[]{"Processing", "Completed"});
        statusField.setSelectedItem(currentStatus);

        Vector<String> availableEmployees = loadAvailableEmployees();
        JList<String> employeeList = new JList<>(availableEmployees);
        employeeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane employeeScrollPane = new JScrollPane(employeeList);

        for (int i = 0; i < availableEmployees.size(); i++) {
            if (currentEmployees.contains(availableEmployees.get(i))) {
                employeeList.addSelectionInterval(i, i);
            }
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Update Job Status:"), BorderLayout.NORTH);
        panel.add(statusField, BorderLayout.CENTER);
        panel.add(new JLabel("Select Employees:"), BorderLayout.WEST);
        panel.add(employeeScrollPane, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Job Allocation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newStatus = statusField.getSelectedItem().toString();
            List<String> selectedEmployees = employeeList.getSelectedValuesList();

            if (selectedEmployees.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No employees selected", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String sql = "UPDATE Jobs SET AssignedEmployees = ?, Work_Status = ? WHERE JobID = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, String.join(", ", selectedEmployees));
                pstmt.setString(2, newStatus);
                pstmt.setInt(3, jobId);
                pstmt.executeUpdate();
                pstmt.close();

                if (newStatus.equals("Completed")) {
                    updateCustomerOrderStatus(jobId);
                }

                JOptionPane.showMessageDialog(this, "Job allocation updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                loadJobs();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating allocation: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Vector<String> loadAvailableEmployees() {
        Vector<String> availableEmployees = new Vector<>();
        try {
            String sql = "SELECT EmployeeID, FirstName, LastName FROM Employees";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                availableEmployees.add(rs.getString("EmployeeID") + " - " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableEmployees;
    }

    private void updateCustomerOrderStatus(int jobId) {
        try {
            String sql = "SELECT JobName FROM Jobs WHERE JobID = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String jobName = rs.getString("JobName");

                String updateSql = "UPDATE CustomerOrders SET Status = 'Completed' WHERE ServiceType = ?";
                PreparedStatement updatePstmt = connection.prepareStatement(updateSql);
                updatePstmt.setString(1, jobName);
                updatePstmt.executeUpdate();
                updatePstmt.close();
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendEmailNotification(String recipientEmail, String subject, String messageText) {
        // Email configuration
        String senderEmail = "rebootersrepairservice@gmail.com\n";  // Replace with your Gmail address
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
                    case "Allocate Employees":
                        allocateEmployees();
                        break;
                    case "Update Allocation":
                        updateAllocation();
                        break;
                    case "Back to Home":
                        new RebootersSystemHome().setVisible(true);
                        dispose();
                        break;
                    default:
                        JOptionPane.showMessageDialog(AllocateEmployees.this, "You clicked: " + text, "Action", JOptionPane.INFORMATION_MESSAGE);
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
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return footerPanel;
    }
}
