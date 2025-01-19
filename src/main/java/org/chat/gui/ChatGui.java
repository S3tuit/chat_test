package org.chat.gui;

import org.chat.communication.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ChatGui extends BaseFrame{

    private JPanel incomingMsgPanel;

    private JPanel outgoingMsgPanel;
    private JTextField outgoingMsgTextField;

    private String filePathToLoad;
    private JLabel fileLabel;
    private String fileAttachedName;

    private JPanel onlineUserPanel;
    private JLabel onlineUserCountLabel;

    public void buildChatGui(ActionListener sendAC, ActionListener loadAC) {
        // Config the frame
        super.initialize("Chat");
        this.setLayout(new BorderLayout(10, 10));

        this.buildIncomingMsgPanel();

        this.buildOutgoingMsgPanel(sendAC, loadAC);

        this.buildOnlineUsersPanel();

        // more frame settings
        this.setVisible(true);
    }

    private void buildOnlineUsersPanel(){
        // OnlineUsers panel
        onlineUserPanel = new JPanel();
        onlineUserPanel = new JPanel(new BorderLayout(10, 10));
        onlineUserPanel.setBorder(BorderFactory.createTitledBorder("Friends"));

        onlineUserCountLabel = new JLabel("<html><b>People online:</b> " + 1 + "</html>");
        onlineUserPanel.add(onlineUserCountLabel, BorderLayout.NORTH);

        this.add(onlineUserPanel, BorderLayout.WEST);
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

        this.add(chatScrollPane, BorderLayout.CENTER);
    }

    private void buildOutgoingMsgPanel(ActionListener sendAC, ActionListener loadAC) {
        // Outgoing msg panel
        outgoingMsgPanel = new JPanel(new BorderLayout(10, 10));
        outgoingMsgPanel.setBorder(BorderFactory.createTitledBorder("Outgoing Message"));

        // Text field to write outgoing msg
        outgoingMsgTextField = new JTextField();
        outgoingMsgPanel.add(outgoingMsgTextField, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = this.buildButtonPanel(sendAC, loadAC);
        outgoingMsgPanel.add(buttonPanel, BorderLayout.EAST);

        this.add(outgoingMsgPanel, BorderLayout.SOUTH);
    }

    private JPanel buildButtonPanel(ActionListener sendAC, ActionListener loadAC) {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JButton sendButton = createButton("Send", sendAC);
        buttonPanel.add(sendButton);

        JButton loadButton = createButton("Load", loadAC);
        buttonPanel.add(loadButton);
        return buttonPanel;
    }

    public void updateOnlineUserCount(int onlineUserCount) {
        onlineUserCountLabel.setText("<html><b>People online:</b> " + onlineUserCount + "</html>");
    }

    // helper method to create buttons faster
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    public void appendMessage(ChatMessage chatMessage, ActionListener saveFileAC) {
        // Create a panel for the message
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder(chatMessage.getSenderName()));

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
            JButton saveFileButton = this.createButton("Save " + chatMessage.getFileName(), saveFileAC);
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

    // Creates a JLabel above the outgoingMsg text area to display the file name the user selected
    // and assigns the path value to filePathToLoad
    public void showFileLoaded(File file) {

        // if the user didn't select a file does nothing
        if (file == null){
            return;

            // if there's already a loaded file, cleans it
        } else if (this.isFileLoaded()) {
            this.getFilePathAndClean();
        }

        filePathToLoad = file.getAbsolutePath();
        fileAttachedName = file.getName();
        fileLabel = new JLabel("<html><b>File attached:</b> " + fileAttachedName + "</html>");
        outgoingMsgPanel.add(fileLabel, BorderLayout.NORTH);
        outgoingMsgPanel.revalidate();
        outgoingMsgPanel.repaint();
    }

    public boolean isFileLoaded() {
        return filePathToLoad != null;
    }

    // Returns the current File loaded and delete the "File attached: " label above the outgoingMsg
    public String getFilePathAndClean() {
        String filePath = filePathToLoad;
        filePathToLoad = null;

        outgoingMsgPanel.remove(fileLabel);
        fileLabel = null;
        outgoingMsgPanel.revalidate();
        outgoingMsgPanel.repaint();

        return filePath;
    }

    public String getFileAttachedName() {
        return fileAttachedName;
    }
}
