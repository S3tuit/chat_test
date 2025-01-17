package org.chat;

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
}
