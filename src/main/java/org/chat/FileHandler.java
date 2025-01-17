package org.chat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    // Reads a file as a byte array
    public static byte[] readFileAsByte(String filePath) {
        Path path = Paths.get(filePath);
        try{
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("Error while reading file " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // Saves byte array into a file
    public static void saveBytesToFile(String filePath, byte[] data) {
        Path path = Paths.get(filePath);
        try{
            Files.write(path, data);
        } catch (IOException e) {
            System.out.println("Error while saving file " + filePath);
            e.printStackTrace();
        }
    }

    // Let the user choose a txt file to load in the chat when the message is sent
    // returns the file selected
    public File letUserChooseFile(JFrame frame) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a TXT file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filter for just .txt files
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        File fileToOpen = null;
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            fileToOpen = chooser.getSelectedFile();
        } else {
            JOptionPane.showMessageDialog(frame, "No file selected");
        }
        return fileToOpen;
    }
}
