package org.chat.gui;

import javax.swing.*;

public abstract class BaseFrame extends JFrame {


    // Basic config shared between all the guis.
    // Remember to set a layout manager after calling this method
    public void initialize(String title) {

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        this.setTitle(title);
        this.setSize(550, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

}
