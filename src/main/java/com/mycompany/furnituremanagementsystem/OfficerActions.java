/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.furnituremanagementsystem;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author shifraz
 */
public class OfficerActions {

    public static void submitSalesForProduction() {
        // Prompt the user to enter the product ID
        String productId = JOptionPane.showInputDialog(null, "Enter the Product ID:");

        if (productId != null && !productId.isEmpty()) {
            // Check if the product ID exists in the file and if it's unapproved
            try (Scanner scanner = new Scanner(new File("sale_order.txt"))) {
                boolean found = false;
                StringBuilder orderDetails = new StringBuilder();
                List<String> updatedLines = new ArrayList<>(); // Store updated lines

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    if (fields.length > 0 && fields[0].trim().equals(productId)) {
                        found = true;
                        // Check if the sale order is unapproved
                        if (fields.length > 6 && fields[6].trim().equals("unapproved")) {
                            // Change the status to approved
                            fields[6] = "approved";
                            String updatedLine = String.join(",", fields);
                            updatedLines.add(updatedLine); // Store updated line

                            // Construct invoice message
                            orderDetails.append("Product ID: ").append(fields[0]).append("\n");
                            orderDetails.append("Furniture Name: ").append(fields[1]).append("\n");
                            orderDetails.append("Type: ").append(fields[2]).append("\n");
                            orderDetails.append("Price: ").append(fields[3]).append("\n");
                            orderDetails.append("Old Price: ").append(fields[4]).append("\n");
                            orderDetails.append("Designer: ").append(fields[5]).append("\n");
                            orderDetails.append("Status: Approved");

                            break;
                        } else {
                            JOptionPane.showMessageDialog(null, "Sale order is already approved.", "Information", JOptionPane.INFORMATION_MESSAGE);
                            return; // No need to proceed further
                        }
                    }
                }

                if (found) {
                    // Update the sale order file with the new lines
                    updateSaleOrder(updatedLines);
                    // Display sale invoice
                    JOptionPane.showMessageDialog(null, orderDetails.toString(), "Sale Invoice", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Product ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid Product ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void updateSaleOrder(List<String> updatedLines) {
        File file = new File("sale_order.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> newContent = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line to extract the product ID
                String[] fields = line.split(",");
                if (fields.length > 0) {
                    String productId = fields[0].trim();

                    // Check if the line needs to be updated
                    boolean updated = false;
                    for (String updatedLine : updatedLines) {
                        if (updatedLine.startsWith(productId)) {
                            newContent.add(updatedLine);
                            updated = true;
                            break;
                        }
                    }
                    if (!updated) {
                        newContent.add(line);
                    }
                }
            }

            // Write the updated content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String newLine : newContent) {
                    writer.write(newLine + System.lineSeparator());
                }
                System.out.println("Sale order updated successfully.");
            } catch (IOException e) {
                System.err.println("Failed to write updated content to file: " + e.getMessage());
            }
        } catch (IOException ex) {
            System.err.println("Error reading file: " + ex.getMessage());
        }
    }

    public static void checkProductStatus() {
        // Prompt the user to input a product ID
        String productId = JOptionPane.showInputDialog(null, "Enter the Product ID:");

        if (productId != null && !productId.isEmpty()) {
            // Check if the product ID exists in the file and determine its status
            try (BufferedReader reader = new BufferedReader(new FileReader("sale_order.txt"))) {
                boolean found = false;

                while (true) {
                    String line = reader.readLine();

                    // Check if end of file is reached
                    if (line == null) {
                        break;
                    }

                    // Split the line into fields
                    String[] fields = line.split(",");

                    // Check if the first field (product ID) matches the input
                    if (fields.length > 0 && fields[0].trim().equals(productId)) {
                        // Check if the array has at least 8 elements (including the status)
                        if (fields.length > 7) {
                            String status = fields[7].trim(); // Assuming status field is at index 7

                            // Display the product status
                            if (status.equalsIgnoreCase("work done")) {
                                JOptionPane.showMessageDialog(null, "Product with ID " + productId + " is work done.", "Product Status", JOptionPane.INFORMATION_MESSAGE);
                            } else if (status.equalsIgnoreCase("in progress")) {
                                JOptionPane.showMessageDialog(null, "Product with ID " + productId + " is in progress.", "Product Status", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Product with ID " + productId + " status is unknown.", "Product Status", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid data format for product ID " + productId, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        return; // Exit method after finding the product
                    }
                }

                // If product ID is not found
                if (!found) {
                    JOptionPane.showMessageDialog(null, "Product ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading sale order file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid Product ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
  public static void processSaleOrderDialog(JFrame parent, boolean modal) {
        JDialog saleOrderDialog = new JDialog(parent, modal);
        saleOrderDialog.setLayout(new GridLayout(2, 1)); // Adjust the layout to accommodate 2 buttons

        JButton modifyButton = new JButton("Modify");
        JButton searchButton = new JButton("Search");

        modifyButton.addActionListener(e -> {
            modifySaleOrder(saleOrderDialog);
        });

        searchButton.addActionListener(e -> {
            searchSaleOrder(saleOrderDialog);
        });

        saleOrderDialog.add(modifyButton);
        saleOrderDialog.add(searchButton);

        saleOrderDialog.setTitle("Process Sale Order");
        saleOrderDialog.setSize(300, 150);
        saleOrderDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        saleOrderDialog.setLocationRelativeTo(null);
        saleOrderDialog.setVisible(true);
    }

    private static void modifySaleOrder(JDialog saleOrderDialog) {
        String productId = JOptionPane.showInputDialog(saleOrderDialog, "Enter the Product ID to modify:");
        if (productId != null && !productId.isEmpty()) {
            boolean productIdExists = false;
            List<String> lines = new ArrayList<>();

            try (Scanner scanner = new Scanner(new File("sale_order.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(productId + ",")) {
                        productIdExists = true;
                    }
                    lines.add(line);
                }
            } catch (FileNotFoundException ex) {
                
            }

            if (productIdExists) {
                String[] options = {"Furniture Name", "Type", "Price", "Old Price", "Designer"};
                int choice = JOptionPane.showOptionDialog(saleOrderDialog, "Which part would you like to modify?", "Modify",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice >= 0) {
                    String selectedOption = options[choice];
                    String newValue = JOptionPane.showInputDialog(saleOrderDialog, "Enter the new value for " + selectedOption + ":");

                    if (newValue != null && !newValue.isEmpty()) {
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.startsWith(productId + ",")) {
                                String[] fields = line.split(",");
                                switch (selectedOption) {
                                    case "Furniture Name":
                                        fields[1] = newValue;
                                        break;
                                    case "Type":
                                        fields[2] = newValue;
                                        break;
                                    case "Price":
                                        fields[3] = newValue;
                                        break;
                                    case "Old Price":
                                        fields[4] = newValue;
                                        break;
                                    case "Designer":
                                        fields[5] = newValue;
                                        break;
                                }
                                String updatedLine = String.join(",", fields);
                                lines.set(i, updatedLine);
                                break;
                            }
                        }

                        try (FileWriter writer = new FileWriter("sale_order.txt")) {
                            for (String line : lines) {
                                writer.write(line + "\n");
                            }
                            JOptionPane.showMessageDialog(saleOrderDialog, "Item with Product ID " + productId + " has been modified.");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(saleOrderDialog, "Error updating item.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(saleOrderDialog, "Please enter a valid value.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(saleOrderDialog, "Product ID does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(saleOrderDialog, "Please enter a valid Product ID.");
        }
    }

    private static void searchSaleOrder(JDialog saleOrderDialog) {
        String productId = JOptionPane.showInputDialog(saleOrderDialog, "Enter the Product ID to search:");
        if (productId != null && !productId.isEmpty()) {
            boolean productIdExists = false;
            String foundItem = "";

            try (Scanner scanner = new Scanner(new File("sale_order.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(productId + ",")) {
                        productIdExists = true;
                        foundItem = line;
                        break;
                    }
                }
            } catch (FileNotFoundException ex) {
                // Handle file not found exception
                
            }

            if (productIdExists) {
                JOptionPane.showMessageDialog(saleOrderDialog, "Item found for Product ID " + productId + ":\n" + foundItem);
            } else {
                JOptionPane.showMessageDialog(saleOrderDialog, "Product ID does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(saleOrderDialog, "Please enter a valid Product ID.");
        }
    }

}
