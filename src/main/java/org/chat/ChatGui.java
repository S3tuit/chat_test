package org.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChatGui {

    private JFrame chatFrame;
    private JTextArea incomingChatTextArea;
    private JTextField outgoingMsgTextField;
    private JPanel incomingMsgPanel;

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
        // chatFrame.pack();
        chatFrame.setVisible(true);
    }

    private void buildIncomingMsgPanel() {
        // IncomingMsg panel
        incomingMsgPanel = new JPanel(new BorderLayout(10, 10));
        incomingMsgPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

        // JTextArea for incoming msg
        incomingChatTextArea = new JTextArea();
        incomingChatTextArea.setEditable(false);
        incomingChatTextArea.setLineWrap(true);
        incomingChatTextArea.setWrapStyleWord(true);

        // Scroller for incoming msg
        JScrollPane chatScrollPane = new JScrollPane(incomingChatTextArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        incomingMsgPanel.add(chatScrollPane, BorderLayout.CENTER);

        chatFrame.add(incomingMsgPanel, BorderLayout.CENTER);
    }

    private void buildOutgoingMsgPanel(ActionListener sendAC) {
        // Outgoing msg panel
        JPanel outgoingMsgPanel = new JPanel(new BorderLayout(10, 10));
        outgoingMsgPanel.setBorder(BorderFactory.createTitledBorder("Outgoing Message"));

        // Text field to write outgoing msg
        outgoingMsgTextField = new JTextField();
        outgoingMsgPanel.add(outgoingMsgTextField, BorderLayout.CENTER);

        // Button to send outgoing msg
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(sendAC);
        outgoingMsgPanel.add(sendButton, BorderLayout.EAST);

        chatFrame.add(outgoingMsgPanel, BorderLayout.SOUTH);
    }

    public void appendMessage(String message){
        incomingChatTextArea.append(message + "\n");
    }

    public String getMessageAndClean(){
        String message = outgoingMsgTextField.getText();
        outgoingMsgTextField.setText("");
        outgoingMsgTextField.requestFocus();

        return message;
    }
}
