/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.furnituremanagementsystem;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author shifraz
 */
public class SalespersonActions {

    public static void showSaleOrderQuotationDialog(JFrame parent) {
        JDialog saleOrderQuotationDialog = new JDialog(parent, "Sale Order Quotation", true);
        saleOrderQuotationDialog.setLayout(new GridLayout(5, 1));

        JButton createButton = new JButton("Create");
        JButton modifyButton = new JButton("Modify");
        JButton removeButton = new JButton("Remove");
        JButton searchButton = new JButton("Search");

        createButton.addActionListener(e -> createSaleOrder(parent));
        modifyButton.addActionListener(e -> modifySaleOrder(parent));
        removeButton.addActionListener(e -> removeSaleOrder(parent));
        searchButton.addActionListener(e -> searchSaleOrder(parent));

        saleOrderQuotationDialog.add(createButton);
        saleOrderQuotationDialog.add(modifyButton);
        saleOrderQuotationDialog.add(removeButton);
        saleOrderQuotationDialog.add(searchButton);

        saleOrderQuotationDialog.setSize(400, 300);
        saleOrderQuotationDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        saleOrderQuotationDialog.setLocationRelativeTo(null);
        saleOrderQuotationDialog.setVisible(true);
    }

    private static void createSaleOrder(JFrame parent) {
        String salespersonUsername = JOptionPane.showInputDialog(parent, "Enter Salesperson Username:");

        if (salespersonUsername != null && !salespersonUsername.isEmpty()) {
            JDialog createDialog = new JDialog(parent, "Create Sale Order", true);
            createDialog.setLayout(new GridLayout(0, 2));

            JTextField productIdField = new JTextField();
            JTextField furnitureNameField = new JTextField();
            JTextField typeField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField oldPriceField = new JTextField();
            JTextField designerField = new JTextField();

            createDialog.add(new JLabel("Product ID:"));
            createDialog.add(productIdField);
            createDialog.add(new JLabel("Furniture Name:"));
            createDialog.add(furnitureNameField);
            createDialog.add(new JLabel("Type:"));
            createDialog.add(typeField);
            createDialog.add(new JLabel("Price:"));
            createDialog.add(priceField);
            createDialog.add(new JLabel("Old Price:"));
            createDialog.add(oldPriceField);
            createDialog.add(new JLabel("Designer:"));
            createDialog.add(designerField);

            JButton confirmButton = new JButton("Create Sale Order");
            confirmButton.addActionListener(confirmEvent -> {
                String productId = productIdField.getText();
                String furnitureName = furnitureNameField.getText();
                String type = typeField.getText();
                String price = priceField.getText();
                String oldPrice = oldPriceField.getText();
                String designer = designerField.getText();

                if (productId.isEmpty() || furnitureName.isEmpty() || type.isEmpty()
                        || price.isEmpty() || oldPrice.isEmpty() || designer.isEmpty()) {
                    JOptionPane.showMessageDialog(createDialog, "Please fill in all fields.");
                } else {
                    String row = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            productId, furnitureName, type, price, oldPrice, designer, "unapproved", "in progress", salespersonUsername);

                    try (FileWriter writer = new FileWriter("sale_order.txt", true)) {
                        writer.write(row);
                        writer.write(System.lineSeparator()); // Add a new line after writing the row
                        JOptionPane.showMessageDialog(createDialog, "Sale order created successfully.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(createDialog, "Error creating sale order.");
                    }

                    createDialog.dispose();
                }
            });

            createDialog.add(confirmButton);
            createDialog.setSize(400, 200);
            createDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(parent, "Please enter a valid Salesperson Username.");
        }
    }

    private static void modifySaleOrder(JFrame parent) {
        String productId = JOptionPane.showInputDialog(parent, "Enter the Product ID to modify:");

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
                ex.printStackTrace();
            }

            if (productIdExists) {
                String[] options = {"Furniture Name", "Type", "Price", "Old Price", "Designer"};
                int choice = JOptionPane.showOptionDialog(parent, "Which part would you like to modify?", "Modify",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice >= 0) {
                    String selectedOption = options[choice];
                    String newValue = JOptionPane.showInputDialog(parent, "Enter the new value for " + selectedOption + ":");

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
                            JOptionPane.showMessageDialog(parent, "Item with Product ID " + productId + " has been modified.");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(parent, "Error updating item.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(parent, "Please enter a valid value.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Product ID does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Please enter a valid Product ID.");
        }
    }

    private static void removeSaleOrder(JFrame parent) {
        String productId = JOptionPane.showInputDialog(parent, "Enter the Product ID to remove:");

        if (productId != null && !productId.isEmpty()) {
            boolean productIdExists = false;
            List<String> lines = new ArrayList<>();

            try (Scanner scanner = new Scanner(new File("sale_order.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(productId + ",")) {
                        productIdExists = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            if (productIdExists) {
                try (FileWriter writer = new FileWriter("sale_order.txt")) {
                    for (String line : lines) {
                        writer.write(line + "\n");
                    }
                    JOptionPane.showMessageDialog(parent, "Item with Product ID " + productId + " has been removed.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "Error removing item.");
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Product ID does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Please enter a valid Product ID.");
        }
    }

    private static void searchSaleOrder(JFrame parent) {
        String productId = JOptionPane.showInputDialog(parent, "Enter the Product ID to search:");

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
                ex.printStackTrace();
            }

            if (productIdExists) {
                JOptionPane.showMessageDialog(parent, "Item found for Product ID " + productId + ":\n" + foundItem);
            } else {
                JOptionPane.showMessageDialog(parent, "Product ID does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(parent, "Please enter a valid Product ID.");
        }
    }

    public static void listAllPersonalSales(JFrame parent) {
        // Check if the username is not null or empty
        String usernameToSearch = JOptionPane.showInputDialog(parent, "Please confirm your username");

        // Check if the username is not null or empty
        if (usernameToSearch != null && !usernameToSearch.isEmpty()) {
            // Check if the username exists in the file
            boolean usernameExists = false;
            StringBuilder foundItems = new StringBuilder();

            try (Scanner scanner = new Scanner(new File("sale_order.txt"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] fields = line.split(",");

                    // Check if the column 9 (index 8) matches the entered username
                    if (fields.length > 8 && fields[8].trim().equals(usernameToSearch)) {
                        usernameExists = true;
                        // Append formatted item details
                        foundItems.append("Product ID: ").append(fields[0]).append("\n");
                        foundItems.append("Furniture Name: ").append(fields[1]).append("\n");
                        foundItems.append("Type: ").append(fields[2]).append("\n");
                        foundItems.append("Price: ").append(fields[3]).append("\n");
                        foundItems.append("Old Price: ").append(fields[4]).append("\n");
                        foundItems.append("Designer: ").append(fields[5]).append("\n");
                        foundItems.append("Approval Status: ").append(fields[6]).append("\n");
                        foundItems.append("Progress Status: ").append(fields[7]).append("\n\n");
                    }
                }
            } catch (FileNotFoundException ex) {
            }

            if (usernameExists) {
                // Create a JTextArea to display the information of the found items
                JTextArea textArea = new JTextArea(foundItems.toString());
                textArea.setEditable(false);

                // Create a JScrollPane to make the text area scrollable
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                // Display the scrollable list of found items
                JOptionPane.showMessageDialog(parent, scrollPane, "Items found for " + usernameToSearch, JOptionPane.PLAIN_MESSAGE);
            } else {
                // Show an error message if the entered username does not exist in the file
                JOptionPane.showMessageDialog(parent, "Username does not exist.");
            }
        } else {
            // Show an error message if the user entered an empty username
            JOptionPane.showMessageDialog(parent, "Please enter a valid Username.");
        }
    }

}
