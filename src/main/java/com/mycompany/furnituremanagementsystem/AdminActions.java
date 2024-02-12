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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author shifraz
 */
public class AdminActions {
   private static final String USER_FILE_PATH = "user.txt";

    private static boolean updateWorkerProfile(String selectedWorker, String newUsername, String newPassword) {
        StringBuilder updatedContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String currentUsername = fields[0];

                if (currentUsername.equals(selectedWorker)) {
                    fields[0] = newUsername;
                    fields[1] = newPassword;
                }

                updatedContent.append(String.join(",", fields)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
            bw.write(updatedContent.toString());
            bw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void manageWorkerProfileAction() {
        List<String> workerProfiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            // Read each line of the file
            while ((line = br.readLine()) != null) {
                // Split the line into fields based on a delimiter
                String[] fields = line.split(",");
                String currentUsername = fields[0];
                String role = fields[2];

                // Check if the role is not "Administrator"
                if (!role.equals("Administrator")) {
                    workerProfiles.add(currentUsername);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading user profiles. Please try again.");
            return;
        }

        // Display the list of worker profiles for selection
        String selectedWorker = (String) JOptionPane.showInputDialog(
                null,
                "Select a worker profile to manage:",
                "Manage Worker Profile",
                JOptionPane.PLAIN_MESSAGE,
                null,
                workerProfiles.toArray(),
                null);

        // Prompt the user to enter the new username and password for the selected worker profile
        String newUsername = JOptionPane.showInputDialog(null, "Enter new username:");
        String newPassword = JOptionPane.showInputDialog(null, "Enter new password:");

        // Update the user.txt file with the new username and password
        if (newUsername != null && !newUsername.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
            boolean success = updateWorkerProfile(selectedWorker, newUsername, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(null, "Profile updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update profile. Please try again.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
        }
    }

}
