/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3050_final_project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author LeAndre
 */
public class StockController implements Initializable {
    private Stage stage;
    
    Integer days;
    Integer transactions;
    String currentLine;
    ArrayList<Integer> prices = new ArrayList();
    
    @FXML
    private Label fromFile;
    @FXML
    private Label lastAction;
    @FXML
    private Label processedData;
    @FXML
    private TextField daysTextField;
    @FXML
    private TextField transactionsTextField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane processedPane;
    
    public void ready(Stage stage) {
        this.stage = stage;
        daysTextField.setText("0");
        transactionsTextField.setText("0");
        days = null;
        transactions = null;
    }
    
    @FXML
    private void handleSetDays(ActionEvent event) {
        days = Integer.parseInt(daysTextField.getText());
        lastAction.setText("Number of days has been set.");
//        fromFile.setMinHeight(18 * days);
//        anchorPane.setMinHeight(18 * days);
    }
    
    @FXML
    private void handleSetTransactions(ActionEvent event) {
        transactions = Integer.parseInt(transactionsTextField.getText());
        lastAction.setText("Number of transactions has been set.");
//        processedData.setMinHeight(18 * transactions);
//        processedPane.setMinHeight(18 * transactions);
    }
    
    @FXML
    private void handleOpen(ActionEvent event) {
        if(days != null && days > 0) {
            fromFile.setText("");
            prices.clear();
            processedData.setText("");
            
            int[] p = new int[days];
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
                    new ExtensionFilter("All Files", "*.*"));
            File file = fileChooser.showOpenDialog(stage);
            lastAction.setText("Waiting for input file");
            if(file != null) {
                try {
                   processFileInput(days, transactions, file);
                }
                catch (Exception ex) {
                    fromFile.setText("Error: " + ex);
                }
                for(int k = 0; k < days; k++)
                    p[k] = prices.get(k);
                populateDataLabel(days, transactions, p);
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Number of days not found!");
            alert.setContentText("Either the number of days has not been set or is invalid. Must be a positive integer to continue.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        stage.close();
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        fromFile.setText("");
        prices.clear();
        processedData.setText("");
    }
    
    public Boolean parse(String line) {
        for(int i = 0; i < line.length(); i++) {
            if(!Character.isDigit(line.charAt(i))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("A non integer was found!");
                alert.setContentText("Parsing error");
                alert.showAndWait();
                
                fromFile.setText("");
                prices.clear();
                return false;
                //stage.close();
            }
        }
        return true;
    }
    
    public void processFileInput(int days, int transactions, File file) throws FileNotFoundException, IOException {
        fromFile.setMinHeight(18 * days);
        anchorPane.setMinHeight(18 * days);

        processedData.setMinHeight(18 * transactions);
        processedPane.setMinHeight(18 * transactions);

        lastAction.setText("Input file received");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for(int i = 1; i <= days; i++) {
            currentLine = reader.readLine();
            if(parse(currentLine)) {
                prices.add(Integer.parseInt(currentLine));
                fromFile.setText(fromFile.getText() + "\nDay " + i  +": " + currentLine);
            }
        }
    }
    
    public void populateDataLabel(int days, int transactions, int prices[]) {
        int profits[][] = new int[transactions+1][prices.length];

        for (int i = 1; i < profits.length; i++) {
            for (int j = 1; j < profits[0].length; j++) {
                int maxVal = 0;
                for (int m = 0; m < j; m++) {
                    maxVal = Math.max(maxVal, prices[j] - prices[m] + profits[i-1][m]);
                }
                profits[i][j] = Math.max(profits[i][j-1], maxVal);
            }
        }
        int i = profits.length - 1;
        int j = profits[0].length - 1;

        Deque<Integer> stack = new LinkedList<>();
        while(true) {
            if(i == 0 || j == 0) {
                break;
            }
            if (profits[i][j] == profits[i][j-1]) {
                j = j - 1;
            } else {
                stack.addFirst(j);
                int maxDiff = profits[i][j] - prices[j];
                for (int k = j-1; k >= 0; k--) {
                    if (profits[i-1][k] - prices[k] == maxDiff) {
                        i = i - 1;
                        j = k;
                        stack.addFirst(j);
                        break;
                    }
                }
            }
        }
        while(!stack.isEmpty()) {
            processedData.setText(processedData.getText() + "Buy at price " + prices[stack.pollFirst()] + "\t");
            processedData.setText(processedData.getText() + "Sell at price " + prices[stack.pollFirst()] + "\n");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
