package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RebootersSystemHome extends JFrame {

    public RebootersSystemHome() {
        setTitle("Rebooter's Tech Repairing System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create and add header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create and add navigation panel
        JPanel navPanel = createNavigationPanel();
        mainPanel.add(navPanel, BorderLayout.CENTER);

        // Create and add footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(800, 100));

        JLabel titleLabel = new JLabel("Rebooter's Tech Repairing System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttonLabels = {
                "Manage Customer Orders", "Manage Suppliers",
                "Manage Inventory", "Manage Employees",
                "Allocate Employees to Jobs", "Send Notifications to Customers",
                "Generate Monthly Report", "Send Notifications to Employees"
        };

        for (String label : buttonLabels) {
            JButton button = createStyledButton(label);
            navPanel.add(button);
        }

        return navPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (text.equals("Manage Customer Orders")) {
                    new ManageCustomerOrders().setVisible(true);
                    dispose(); // Close the home page
                } else if (text.equals("Manage Suppliers")){
                    new ManageSuppliers().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Manage Inventory")){
                    new ManageInventory().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Manage Employees")){
                    new ManageEmployees().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Allocate Employees to Jobs")){
                    new AllocateEmployees().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Generate Monthly Report")){
                    new MonthlyReportGenerator().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Send Notifications to Customers")){
                    new NotificationServiceCustomer().setVisible(true);
                    dispose(); // Close the home page
                }
                else if (text.equals("Send Notifications to Employees")){
                    new EmployeeNotificationService().setVisible(true);
                    dispose(); // Close the home page
                }

                else{
                    JOptionPane.showMessageDialog(RebootersSystemHome.this,
                            "You clicked: " + text, "Navigation", JOptionPane.INFORMATION_MESSAGE);
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
        footerLabel.setForeground(Color.white);
        footerPanel.add(footerLabel);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        return footerPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RebootersSystemHome().setVisible(true);
            }
        });
    }
}