/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.furnituremanagementsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author shifraz
 */
public class UserProfileManager {
    private static final String USER_FILE_PATH = "user.txt";

    private static boolean isValidCredentials(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String currentUsername = fields[0];
                    String currentPassword = fields[1];
                    if (currentUsername.equals(username) && currentPassword.equals(password)) {
                        return true;
                    }
                } else {
                    System.err.println("Invalid format in user.txt: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user file: " + e.getMessage());
        }
        return false;
    }

    private static boolean updateUserProfile(String oldUsername, String oldPassword, String newUsername, String newPassword) {
        StringBuilder updatedContent = new StringBuilder();
        boolean userFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String currentUsername = fields[0];
                    String currentPassword = fields[1];
                    if (currentUsername.equals(oldUsername) && currentPassword.equals(oldPassword)) {
                        fields[0] = newUsername;
                        fields[1] = newPassword;
                        userFound = true;
                    }
                    updatedContent.append(String.join(",", fields)).append("\n");
                } else {
                    System.err.println("Invalid line format in user.txt: " + line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (userFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
                bw.write(updatedContent.toString());
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static void manageUserProfile() {
        String oldUsername = JOptionPane.showInputDialog(null, "Enter old username:");
        String oldPassword = JOptionPane.showInputDialog(null, "Enter old password:");

        // Validate old username and password using UserProfileManager
        if (isValidCredentials(oldUsername, oldPassword)) {
            String newUsername = JOptionPane.showInputDialog(null, "Enter new username:");
            String newPassword = JOptionPane.showInputDialog(null, "Enter new password:");

            // Check if newUsername and newPassword are not null or empty
            if (newUsername != null && !newUsername.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
                // Attempt to update the user profile using UserProfileManager
                boolean success = updateUserProfile(oldUsername, oldPassword, newUsername, newPassword);

                // Display appropriate message based on the success of the profile update
                if (success) {
                    JOptionPane.showMessageDialog(null, "Profile updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update profile. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid new username or password. Please try again.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid old username or password. Please try again.");
        }
    }
    
}
