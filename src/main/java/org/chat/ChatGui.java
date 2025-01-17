package org.chat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ChatGui {

    private JFrame chatFrame;
    private JTextField outgoingMsgTextField;
    private JPanel incomingMsgPanel;
    private JPanel outgoingMsgPanel;
    private String filePathToLoad;
    private JLabel fileLabel;

    public void buildChatGui(ActionListener sendAC) {

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        chatFrame = new JFrame("Chat");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(400, 600);
        chatFrame.setLayout(new BorderLayout(10, 10));

        this.buildIncomingMsgPanel();

        this.buildOutgoingMsgPanel(sendAC);

        // more frame settings
        chatFrame.setVisible(true);
    }

    private void buildIncomingMsgPanel() {
        // IncomingMsg panel
        incomingMsgPanel = new JPanel();
        incomingMsgPanel.setLayout(new BoxLayout(incomingMsgPanel, BoxLayout.Y_AXIS));
        incomingMsgPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

        // Scroller for incoming msg
        JScrollPane chatScrollPane = new JScrollPane(incomingMsgPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chatFrame.add(chatScrollPane, BorderLayout.CENTER);
    }

    private void buildOutgoingMsgPanel(ActionListener sendAC) {
        // Outgoing msg panel
        outgoingMsgPanel = new JPanel(new BorderLayout(10, 10));
        outgoingMsgPanel.setBorder(BorderFactory.createTitledBorder("Outgoing Message"));

        // Text field to write outgoing msg
        outgoingMsgTextField = new JTextField();
        outgoingMsgPanel.add(outgoingMsgTextField, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = this.buildButtonPanel(sendAC);
        outgoingMsgPanel.add(buttonPanel, BorderLayout.EAST);

        chatFrame.add(outgoingMsgPanel, BorderLayout.SOUTH);
    }

    private JPanel buildButtonPanel(ActionListener sendAC) {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JButton sendButton = createButton("Send", sendAC);
        buttonPanel.add(sendButton);

        JButton loadButton = createButton("Load", new LoadButtonActionListener());
        buttonPanel.add(loadButton);
        return buttonPanel;
    }

    // helper method to create buttons faster
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    public void appendMessage(ChatMessage chatMessage){
        // Create a panel for the message
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder(""));

        // Add text message
        JTextArea messageText = new JTextArea(chatMessage.getMessage());
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setEditable(false);
        messageText.setOpaque(false);
        messageText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messagePanel.add(messageText, BorderLayout.CENTER);

        // Add a button to load the file if there's any attached
        if (chatMessage.isFileAttached()){
            JButton saveFileButton = new JButton("Save File");
            messagePanel.add(saveFileButton, BorderLayout.SOUTH);
        }

        // Add message to incoming msg panel
        incomingMsgPanel.add(messagePanel);
        incomingMsgPanel.revalidate();
        incomingMsgPanel.repaint();
    }

    public String getMessageAndClean(){
        String message = outgoingMsgTextField.getText();
        outgoingMsgTextField.setText("");
        outgoingMsgTextField.requestFocus();

        return message;
    }

    // Let the user choose a txt file to load in the chat when the message is sent
    // then saves the file path in filePathToLoad and calls showFileLoaded
    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a TXT file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filter for just .txt files
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        int result = chooser.showOpenDialog(chatFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            filePathToLoad = file.getAbsolutePath();
            this.showFileLoaded(file.getName());
        } else {
            JOptionPane.showMessageDialog(chatFrame, "No file selected");
        }
    }

    // Creates a JLabel above the outgoingMsg text area to display the file name the user selected
    private void showFileLoaded(String fileName) {
        fileLabel = new JLabel("<html><b>File attached:</b> " + fileName + "</html>");
        outgoingMsgPanel.add(fileLabel, BorderLayout.NORTH);
        outgoingMsgPanel.revalidate();
        outgoingMsgPanel.repaint();
    }

    public boolean isFileLoaded() {
        return filePathToLoad != null;
    }

    public String getFilePathAndClean() {
        String filePath = filePathToLoad;
        filePathToLoad = null;

        fileLabel = null;
        outgoingMsgPanel.revalidate();
        outgoingMsgPanel.repaint();

        return filePath;
    }

    class LoadButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            openFileChooser();
        }
    }
}
