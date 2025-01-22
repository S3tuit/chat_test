package org.chat.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginGui extends BaseFrame{

    JPasswordField passwordField;
    JTextField usernameField;


    public LoginGui() {
    }

    public void buildGui(ActionListener loginAC) {
        super.initialize("Login");

        // Set layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title label
        JLabel chatAppLabel = new JLabel("Chat App");
        chatAppLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        chatAppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2; // span across 2 col
        add(chatAppLabel, gbc);

        // Username label
        gbc.gridy++; // move to next row
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(usernameLabel, gbc);

        // create username field
        gbc.gridx = 1; // move to second col
        usernameField = new JTextField();
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(usernameField, gbc);

        // Password label
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(passwordLabel, gbc);

        // Password field
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(passwordField, gbc);

        // create login button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 20));
        loginButton.addActionListener(loginAC);
        add(loginButton, gbc);

        // create register label
        gbc.gridy++;
        JLabel registerLabel = new JLabel("<html><a href=\"#\">Don't have an account? Register Here</a></html>");
        registerLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(registerLabel, gbc);

        this.setVisible(true);
    }

    public String getPassword(){
        char[] tmpPswd = passwordField.getPassword();
        passwordField.setText("");
        return new String(tmpPswd);
    }

    public String getUsername(){
        String tmpUsername = usernameField.getText();
        usernameField.setText("");
        return tmpUsername;
    }

    public boolean askToInvalidateCurrSession() {
        int choice = JOptionPane.showConfirmDialog(
                null,
                "There is already an active session. Do you want to invalidate it?",
                "Active Session Detected",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        // Return true if the user chooses "Yes", otherwise false
        return choice == JOptionPane.YES_OPTION;
    }
}
