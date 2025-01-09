package upt.cebp;

import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterUI {

    public static void main(String[] args) {
        // Start with the login screen
        SwingUtilities.invokeLater(LoginRegisterUI::createLoginScreen);
    }

    private static void createLoginScreen() {
        // Frame for Login
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 250);
        loginFrame.setLayout(new GridLayout(4, 1, 10, 10));

        // Username Field
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField(20);
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");

        // Register Button
        JButton registerButton = new JButton("Set up an account");

        // Action for Register Button
        registerButton.addActionListener(e -> {
            loginFrame.dispose(); // Close the login frame
            createRegisterScreen();
        });

        // Add components to Login Frame
        loginFrame.add(usernamePanel);
        loginFrame.add(passwordPanel);
        loginFrame.add(loginButton);
        loginFrame.add(registerButton);

        // Display the login frame
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static void createRegisterScreen() {
        // Frame for Registration
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setSize(400, 350);
        registerFrame.setLayout(new GridLayout(6, 1, 10, 10));

        // Username Field
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username/Email:"));
        JTextField usernameField = new JTextField(20);
        usernamePanel.add(usernameField);

        // Full Name Field
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField(20);
        namePanel.add(nameField);

        // Balance Field
        JPanel balancePanel = new JPanel();
        balancePanel.add(new JLabel("Balance:"));
        JTextField balanceField = new JTextField(20);
        balancePanel.add(balanceField);

        // Password Field
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);

        // Register Button
        JButton registerButton = new JButton("Register");

        // Register Button Action
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String fullName = nameField.getText();
            String balance = balanceField.getText();
            String password = new String(passwordField.getPassword());

            // Validate password
            if (!password.matches("[a-zA-Z0-9\\-_ ]+")) {
                JOptionPane.showMessageDialog(registerFrame,
                        "Password can only contain alphanumeric characters, dash, underscore, and space.",
                        "Invalid Password", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate balance is a number
            try {
                Double.parseDouble(balance);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(registerFrame,
                        "Balance must be a numeric value.",
                        "Invalid Balance", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mock registration success
            JOptionPane.showMessageDialog(registerFrame,
                    "Account created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            registerFrame.dispose(); // Close the register frame
            createLoginScreen(); // Return to login screen
        });

        // Add components to Register Frame
        registerFrame.add(usernamePanel);
        registerFrame.add(namePanel);
        registerFrame.add(balancePanel);
        registerFrame.add(passwordPanel);
        registerFrame.add(registerButton);

        // Display the register frame
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);
    }
}
