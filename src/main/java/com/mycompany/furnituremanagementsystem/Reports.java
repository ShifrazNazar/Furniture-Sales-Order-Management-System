/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.furnituremanagementsystem;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author shifraz
 */
public class Reports {

    private static final String FILE_PATH = "sale_order.txt";

    public void generateReports() {
        List<String> workDoneReport = generateWorkDoneReport();
        List<String> approvedOrdersReport = generateApprovedOrdersReport();
        List<String> revenueReport = generateRevenueReport();
        List<String> salesBySalespersonReport = generateSalesBySalespersonReport();

        // Create and display the report window with all reports
        ReportWindow reportWindow = new ReportWindow(workDoneReport, approvedOrdersReport, revenueReport, salesBySalespersonReport);
        reportWindow.setVisible(true);
    }

    private List<String> readFileAndFilter(int statusIndex, String targetStatus) {
        List<String> filteredReport = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                // Check if the line has the expected number of fields
                if (fields.length >= statusIndex + 1) {
                    String status = fields[statusIndex].trim();

                    if (status.equals(targetStatus)) {
                        filteredReport.add(line);
                    }
                } else {
                    // Handle lines with insufficient fields (e.g., log a warning)
                    System.err.println("Skipping line: " + line + " - Insufficient fields.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filteredReport;
    }

    public List<String> generateWorkDoneReport() {
        return readFileAndFilter(7, "work done");
    }

    public List<String> generateApprovedOrdersReport() {
        List<String> approvedStatuses = List.of("approved", "closed");
        List<String> approvedOrdersReport = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                // Check if the line has the expected number of fields
                if (fields.length >= 7) {
                    String approvalStatus = fields[6].trim();

                    if (approvedStatuses.contains(approvalStatus)) {
                        approvedOrdersReport.add(line);
                    }
                } else {
                    // Handle lines with insufficient fields (e.g., log a warning)
                    System.err.println("Skipping line: " + line + " - Insufficient fields.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return approvedOrdersReport;
    }

    public List<String> generateRevenueReport() {
        List<String> revenueReport = new ArrayList<>();
        double totalRevenue = 0;
        Map<String, Map.Entry<String, Double>> productPrice = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4) { // Check if the line has at least 4 fields
                    String productId = fields[0].trim();
                    String productName = fields[1].trim();
                    String priceStr = fields[3].trim();

                    try {
                        double price = Double.parseDouble(priceStr);
                        productPrice.put(productId, Map.entry(productName, price));
                        totalRevenue += price;
                    } catch (NumberFormatException e) {
                        // Handle invalid price format
                        System.err.println("Invalid price format in line: " + line);
                    }
                } else {
                    // Handle lines with insufficient fields
                    System.err.println("Skipping line: " + line + " - Insufficient fields.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add individual products and their prices to the report
        for (Map.Entry<String, Map.Entry<String, Double>> entry : productPrice.entrySet()) {
            String productId = entry.getKey();
            String productName = entry.getValue().getKey();
            double price = entry.getValue().getValue();
            revenueReport.add(productId + "," + productName + "," + price);
        }
        // Add total revenue at the end
        revenueReport.add("Total Revenue: ,," + totalRevenue);

        return revenueReport;
    }

    public List<String> generateSalesBySalespersonReport() {
        List<String> salesBySalespersonReport = new ArrayList<>();
        Map<String, Integer> salesBySalesperson = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 9) { // Check if the line has at least 9 fields
                    String salesperson = fields[8].trim();
                    salesBySalesperson.put(salesperson, salesBySalesperson.getOrDefault(salesperson, 0) + 1);
                } else {
                    // Handle lines with insufficient fields
                    System.err.println("Skipping line: " + line + " - Insufficient fields.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add sales count by each salesperson to the report
        for (Map.Entry<String, Integer> entry : salesBySalesperson.entrySet()) {
            String salesperson = entry.getKey();
            int salesCount = entry.getValue();
            salesBySalespersonReport.add(salesperson + "," + salesCount);
        }

        return salesBySalespersonReport;
    }

    public class ReportWindow extends JFrame {

        public ReportWindow(List<String> workDoneReport, List<String> approvedOrdersReport, List<String> revenueReport, List<String> salesBySalespersonReport) {
            initComponents(workDoneReport, approvedOrdersReport, revenueReport, salesBySalespersonReport);
        }

        private void initComponents(List<String> workDoneReport, List<String> approvedOrdersReport, List<String> revenueReport, List<String> salesBySalespersonReport) {
            setTitle("Reports");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(800, 600);

            String[] columnNames = {"Item ID", "Item Name", "Status"};

            Object[][] workDoneData = new Object[workDoneReport.size()][3];
            Object[][] approvedOrdersData = new Object[approvedOrdersReport.size()][3];
            Object[][] revenueData = new Object[revenueReport.size()][3];
            Object[][] salesBySalespersonData = new Object[salesBySalespersonReport.size()][2];

            // Populate work done report data
            for (int i = 0; i < workDoneReport.size(); i++) {
                String[] fields = workDoneReport.get(i).split(",");
                workDoneData[i][0] = fields[0];
                workDoneData[i][1] = fields[1];
                workDoneData[i][2] = fields[7];
            }

            // Populate approved orders report data
            for (int i = 0; i < approvedOrdersReport.size(); i++) {
                String[] fields = approvedOrdersReport.get(i).split(",");
                approvedOrdersData[i][0] = fields[0];
                approvedOrdersData[i][1] = fields[1];
                approvedOrdersData[i][2] = fields[6];
            }

            // Populate revenue report data
            for (int i = 0; i < revenueReport.size(); i++) {
                String[] fields = revenueReport.get(i).split(",");
                revenueData[i][0] = fields[0];
                revenueData[i][1] = fields[1];
                revenueData[i][2] = fields[2];
            }

            // Populate sales by salesperson report data
            for (int i = 0; i < salesBySalespersonReport.size(); i++) {
                String[] fields = salesBySalespersonReport.get(i).split(",");
                salesBySalespersonData[i][0] = fields[0];
                salesBySalespersonData[i][1] = fields[1];
            }

            // Create table models
            DefaultTableModel workDoneModel = new DefaultTableModel(workDoneData, columnNames);
            DefaultTableModel approvedOrdersModel = new DefaultTableModel(approvedOrdersData, columnNames);
            DefaultTableModel revenueModel = new DefaultTableModel(revenueData, new String[]{"Product ID", "Product Name", "Price"});
            DefaultTableModel salesBySalespersonModel = new DefaultTableModel(salesBySalespersonData, new String[]{"Salesperson", "Sales Count"});

            // Create tables
            JTable workDoneTable = new JTable(workDoneModel);
            JTable approvedOrdersTable = new JTable(approvedOrdersModel);
            JTable revenueTable = new JTable(revenueModel);
            JTable salesBySalespersonTable = new JTable(salesBySalespersonModel);

            // Add tables to scroll panes
            JScrollPane workDoneScrollPane = new JScrollPane(workDoneTable);
            JScrollPane approvedOrdersScrollPane = new JScrollPane(approvedOrdersTable);
            JScrollPane revenueScrollPane = new JScrollPane(revenueTable);
            JScrollPane salesBySalespersonScrollPane = new JScrollPane(salesBySalespersonTable);

            // Create tabs for each report
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Work Done Report", workDoneScrollPane);
            tabbedPane.addTab("Approved Orders Report", approvedOrdersScrollPane);
            tabbedPane.addTab("Revenue Report", revenueScrollPane);
            tabbedPane.addTab("Sales by Salesperson", salesBySalespersonScrollPane);

            getContentPane().add(tabbedPane, BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) {
        Reports reports = new Reports();
        reports.generateReports();
    }
}
