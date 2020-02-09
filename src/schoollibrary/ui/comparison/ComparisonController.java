 
package schoollibrary.ui.comparison;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.main.MainController;



public class ComparisonController implements Initializable {
    
    ObservableList<ComparisonController.Comparison> list = FXCollections.observableArrayList();
    HashMap<String, List<Integer>> countMap ;
    ArrayList<String> keyArray;
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXComboBox<String> timeSelect;
    @FXML
    private JFXComboBox<String> catSelect;
    @FXML
    private TableView<Comparison> tableViewCol;
    @FXML
    private TableColumn<Comparison,Integer> noCol;
    @FXML
    private TableColumn<Comparison,String> idCol;
    @FXML
    private TableColumn<Comparison,Integer> issueCol;
    @FXML
    private TableColumn<Comparison,Integer> subCol;
    @FXML
    private JFXComboBox<String> graphCatSelect;
    @FXML
    private JFXComboBox<String> graphTimeSelect;
    @FXML
    private final NumberAxis yAxis = new NumberAxis();
    @FXML
    private final CategoryAxis xAxis =  new CategoryAxis();
    @FXML
    private BarChart<String, Number> chart;
       
    private XYChart.Series firstChart; 
    private XYChart.Series secondChart; 
    
    DatabaseHandler databaseHandler;   
    MainController mainController;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDropdown();
        initGraphDropdown();
        initCol();
        loadData("non");
    }    

    
    private void initCol() {
        noCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("searchID"));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issueCount"));
        subCol.setCellValueFactory(new PropertyValueFactory<>("submitCount"));   
    }
    
    
    @FXML
    private void searchAction(ActionEvent event) {
        
        String timeSec = timeSelect.getValue();
        String catSec = catSelect.getValue();
        LocalDate sDate = LocalDate.now();
        int year = sDate.getYear();
        int month = sDate.getMonth().getValue();
        int lastMonth = sDate.minusMonths(1).getMonth().getValue();
        int lastYear = sDate.minusYears(1).getYear();
        
        if(timeSec == null || catSec == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        }    
        switch (timeSec) {
            case "All time":
                if(catSec.equals("Date")){
                    loadData(catSec);
                }else{
                    loadAllTimeData(catSec);
                }          
                break;
            case "This month":
                loadMonthData(catSec,year,month);
                break;     
            case "Last month":
                loadMonthData(catSec,year,lastMonth);
                break;
            case "This Year":
                loadMonthData(catSec,year,0);
                break;
            case "Last Year":
                loadMonthData(catSec,lastYear,0);  
                break;                        
            default:
                break;
        }
    }

    
    @FXML
    private void graphSearchAction(ActionEvent event) {
        String timeSec = graphTimeSelect.getValue();
        String catSec = graphCatSelect.getValue();
        
        if(timeSec == null || catSec == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        }
        switch (catSec) {
            case "Total Add":
                loadAddGraph(timeSec);
                break;
            case "Transaction":
                loadTransactionGraph(timeSec);
                break;
            case "Late Submission Count":
                loadLateSubmissionGraph(timeSec);
                break;
            case "Total Fine Payed":
                loadFinePayedGraph(timeSec);
                break;
        }     
    }
    
    
    @FXML
    private void cancel(ActionEvent event) {
        if(timeSelect.getValue() == null && catSelect.getValue() == null){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
            return;
        } 
        timeSelect.getItems().clear();
        catSelect.getItems().clear();
        list.clear();
        initDropdown();
        loadData("non");
    }

    
    @FXML
    private void graphCancel(ActionEvent event) {
    }
    
    
    private void loadData(String category) {
        list.clear();
        countMap =  new HashMap<>();        
        String query1,query2,rowId;
        int i=0;
        
        LocalDate sDate = LocalDate.now();
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -14);
        
        if(category.equals("Date")){
            cancelButton.setText("Cancel");
            query1 = "SELECT issueDate, COUNT(*) as count1 FROM REPORT GROUP BY issueDate";
            query2 = "SELECT submitDate, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY submitDate  ";
        }else{
            cancelButton.setText("Close");
            query1 = "SELECT issueDate, COUNT(*) as count1 FROM REPORT WHERE issueDate <= '"+ sDate +"' AND issueDate >= '"+ sdf.format(cal.getTime())+"' GROUP BY issueDate ";
            query2 = "SELECT submitDate, COUNT(*) as count2 FROM REPORT WHERE submitDate <= '"+ sDate +"' AND submitDate >= '"+ sdf.format(cal.getTime())+"' AND isSubmit = 'true' GROUP BY submitDate ";
        }
        try {
            ResultSet result1 = databaseHandler.execQuery(query1);
            ResultSet result2 = databaseHandler.execQuery(query2);
            
            while (result1.next()) {            
                rowId = result1.getString("issueDate");
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0, result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
            }
            while (result2.next()) {                 
                rowId = result2.getString("submitDate");      
                if(countMap.get(rowId) == null){
                    countMap.put(rowId,new ArrayList<>());
                    countMap.get(rowId).add(0,0);
                } 
                countMap.get(rowId).add(1,result2.getInt("count2"));  
            }
            int issueCount, submitCount;
            for (Map.Entry cmIterator : countMap.entrySet()) {
                i++;
                String key = String.valueOf(cmIterator.getKey());
                issueCount = countMap.get(key).get(0);
                submitCount = countMap.get(key).get(1);
                list.add(new Comparison(i,key,issueCount,submitCount));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        }       
        tableViewCol.setItems(list); 
        countMap.clear();
    }
    
    
    private void loadAllTimeData(String category) {
        cancelButton.setText("Cancel");
        list.clear();
        String query1 = null, id = null;
        int i=0;
        switch (category) {
            case "Book":
                query1 =  "SELECT bookID, COUNT(issueDate) as count1, COUNT(submitDate) as count2 FROM REPORT GROUP BY bookID";
                id = "bookID";
                break;
            case "Member":
                query1 = "SELECT memberID, COUNT(issueDate) as count1, COUNT(submitDate) as count2 FROM REPORT GROUP BY memberID";
                id = "memberID";
                break;
            default:
                break;
        }
        
        ResultSet result = databaseHandler.execQuery(query1);
        try {
            while (result.next()){
                i++;
                int countRow1 = result.getInt("count1");
                int countRow2 = result.getInt("count2");
                String rowId = result.getString(id);
                list.add(new Comparison(i,rowId,countRow1,countRow2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);    
    }
    
    
    
    private void loadMonthData(String category, int year, int month) {
        cancelButton.setText("Cancel");
        list.clear();
        countMap =  new HashMap<>();        
        String query1 = null, query2 = null, id = null,rowId;
        int startMonth,endMonth,issueCount,submitCount,i=0;
        if(month == 0){
            startMonth = 1;
            endMonth = 12;
        }else{
            startMonth = month;
            endMonth = month;
        }
        switch (category) {
            case "Book":
                query1 =  "SELECT DISTINCT bookID, COUNT(issueDate) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+startMonth+"-1' AND issueDate <= '"+year+"-"+endMonth+"-31' GROUP BY bookID";
                query2 =  "SELECT DISTINCT bookID, COUNT(submitDate) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+startMonth+"-1' AND submitDate <= '"+year+"-"+endMonth+"-31' AND isSubmit = 'true' GROUP BY bookID";            
                id = "bookID";
                break;
            case "Member":
                query1 =  "SELECT DISTINCT memberID, COUNT(issueDate) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+startMonth+"-1' AND issueDate <= '"+year+"-"+endMonth+"-31' GROUP BY memberID";
                query2 =  "SELECT DISTINCT memberID, COUNT(submitDate) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+startMonth+"-1' AND submitDate <= '"+year+"-"+endMonth+"-31' AND isSubmit = 'true' GROUP BY memberID";            
                id = "memberID";
                break;              
            case "Date":
                query1 =  "SELECT DISTINCT issueDate, COUNT(*) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+startMonth+"-1' AND issueDate <= '"+year+"-"+endMonth+"-31' GROUP BY issueDate";
                query2 =  "SELECT DISTINCT submitDate, COUNT(*) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+startMonth+"-1' AND submitDate <= '"+year+"-"+endMonth+"-31' AND isSubmit = 'true' GROUP BY submitDate";
                id = "issueDate";
                break;
            default:
                break;
        }
        try {
            ResultSet result1 = databaseHandler.execQuery(query1);
            ResultSet result2 = databaseHandler.execQuery(query2);
            
            while (result1.next()) {
                rowId = result1.getString(id);
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0, result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
            }
            while (result2.next()) {
                if("issueDate".equals(id)){
                    rowId = result2.getString("submitDate"); 
                }else{
                    rowId = result2.getString(id);    
                }
                
                if(countMap.get(rowId) == null){
                    countMap.put(rowId,new ArrayList<>());
                    countMap.get(rowId).add(0,0);
                } 
                countMap.get(rowId).add(1,result2.getInt("count2"));                         
            }
            for (Map.Entry cmIterator : countMap.entrySet()) {
                i++;
                String key = String.valueOf(cmIterator.getKey());
                issueCount = countMap.get(key).get(0);
                submitCount = countMap.get(key).get(1);
                list.add(new Comparison(i,key,issueCount,submitCount));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        }       
        tableViewCol.setItems(list);  
        countMap.clear();
    }

    
    private void loadTransactionGraph(String timeSec) {
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }       
        chart.getData().removeAll();     
        countMap =  new HashMap<>(); 
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series();
        secondChart = new XYChart.Series();
        
        xAxis.setLabel("Duration");
        yAxis.setLabel("Count");
        firstChart.setName("Issue");
        secondChart.setName("Submission");
            
        String query1 = null, query2 = null,rowId;
        switch (timeSec) {
            case "By Day":
                query1 = "SELECT issueDate as issueDate, COUNT(*) as count1 FROM REPORT GROUP BY issueDate" ;
                query2 = "SELECT submitDate as submitDate, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY submitDate";
                break;
            case "By Week": 
//              group by weekly                
                break;
            case "By Month":
                query1 = "SELECT count(*) as count1, MONTH(issueDate) AS mon, YEAR(issueDate) as year FROM REPORT GROUP BY MONTH(issueDate), YEAR(issueDate)" ; 
                query2 = "SELECT count(*) as count2, MONTH(submitDate) AS mon, YEAR(submitDate) as year FROM REPORT WHERE isSubmit = 'true' GROUP BY MONTH(submitDate), YEAR(submitDate)" ;
                break;
            case "By Year": 
                query1 = "SELECT YEAR(issueDate) as issueDate, COUNT(*) as count1 FROM REPORT GROUP BY YEAR(issueDate)" ;
                query2 = "SELECT YEAR(submitDate) as submitDate, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY YEAR(submitDate)";
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query1);
            ResultSet result2 = databaseHandler.execQuery(query2);

            while (result1.next()) {          
                if(timeSec.equals("By Month")){
                    rowId = result1.getString("year")+"-"+result1.getString("mon");
                }else{
                    rowId = result1.getString("issueDate");
                }
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0, result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
                keyArray.add(rowId);
            }
            while (result2.next()) {
                if(timeSec.equals("By Month")){
                    rowId = result2.getString("year")+"-"+result2.getString("mon");
                }else{
                    rowId = result2.getString("submitDate");      
                }
                if(countMap.get(rowId) == null){
                    countMap.put(rowId,new ArrayList<>());
                    countMap.get(rowId).add(0,0);
                    keyArray.add(rowId);
                }
                countMap.get(rowId).add(1,result2.getInt("count2"));  
            }
            Collections.sort(keyArray);
            
            for(int i=0;i<keyArray.size();i++){
                firstChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(0)));
                secondChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(1)));
            }
            chart.getData().retainAll();
            chart.getData().addAll(firstChart);
            chart.getData().addAll(secondChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    private void loadAddGraph(String timeSec) {
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }    
        chart.getData().removeAll();
        countMap =  new HashMap<>();  
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series();
        secondChart = new XYChart.Series();
        
        xAxis.setLabel("Duration");
        yAxis.setLabel("Count");
        firstChart.setName("Book");
        secondChart.setName("Member");
        
        String query1 = null, query2 = null,rowId;
        switch (timeSec) {
            case "By Day":
                query1 = "SELECT addedDate , COUNT(*) as count1 FROM BOOK GROUP BY addedDate" ;
                query2 = "SELECT addedDate, COUNT(*) as count2 FROM MEMBER GROUP BY addedDate";
                break;
            case "By Week": 
//              group by weekly
                break;
            case "By Month":
                query1 = "SELECT count(*) as count1, MONTH(addedDate) AS mon, YEAR(addedDate) as year FROM BOOK GROUP BY MONTH(addedDate), YEAR(addedDate)" ; 
                query2 = "SELECT count(*) as count2, MONTH(addedDate) AS mon, YEAR(addedDate) as year FROM MEMBER GROUP BY MONTH(addedDate), YEAR(addedDate)" ; 
                break;
            case "By Year": 
                query1 = "SELECT YEAR(addedDate)as addedDate, COUNT(*) as count1 FROM BOOK GROUP BY YEAR(addedDate)" ;
                query2 = "SELECT YEAR(addedDate)as addedDate, COUNT(*) as count2 FROM MEMBER GROUP BY YEAR(addedDate)";
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query1);
            ResultSet result2 = databaseHandler.execQuery(query2);

            while (result1.next()) {   
                if(timeSec.equals("By Month")){
                    rowId = result1.getString("year")+"-"+result1.getString("mon");
                }else{
                    rowId = result1.getString("addedDate");      
                }
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0,result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
                keyArray.add(rowId);
            }
            while (result2.next()) {
                if(timeSec.equals("By Month")){
                    rowId = result2.getString("yer")+"-"+result2.getString("mon");
                }else{
                    rowId = result2.getString("addedDate");      
                }
                if(countMap.get(rowId) == null){
                    countMap.put(rowId,new ArrayList<>());
                    countMap.get(rowId).add(0,0);
                    keyArray.add(rowId);
                }
                countMap.get(rowId).add(1,result2.getInt("count2"));  
            }
            Collections.sort(keyArray);
            
            for(int i=0;i<keyArray.size();i++){
                firstChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(0)));
                secondChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(1)));
            }
            chart.getData().retainAll();
            chart.getData().addAll(firstChart);
            chart.getData().addAll(secondChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }

    
    private void loadLateSubmissionGraph(String timeSec) {
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }  
        chart.getData().removeAll();
        countMap =  new HashMap<>();  
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series();  
        
        xAxis.setLabel("Duration");
        yAxis.setLabel("Count");
        firstChart.setName("Late Submission");
    
        String query = null,rowId;
        switch (timeSec) {
            case "By Day":
                query = "SELECT count(*) as count, submitDate FROM REPORT WHERE isSubmit = 'true' AND isLateSubmit = 'true' GROUP BY submitDate" ;
                break;
            case "By Week": 
//                by weekly
                break;
            case "By Month":
                query = "SELECT count(*) as count, MONTH(submitDate) AS mon, YEAR(submitDate) as year FROM REPORT WHERE isSubmit = 'true' AND isLateSubmit = 'true' GROUP BY MONTH(submitDate), YEAR(submitDate)" ;
                break;
            case "By Year": 
                query = "SELECT count(*) as count, YEAR(submitDate) as submitDate FROM REPORT WHERE isSubmit = 'true' AND isLateSubmit = 'true' GROUP BY YEAR(submitDate)" ;
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query);

            while (result1.next()) {            
                if(timeSec.equals("By Month")){
                    rowId = result1.getString("mon")+"-"+result1.getString("yera");
                }else{
                    rowId = result1.getString("submitDate");    
                }
                
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0,result1.getInt(1));
                keyArray.add(rowId);
            }
            Collections.sort(keyArray);
            
            for(int i=0;i<keyArray.size();i++){
                firstChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(0)));
            }
            chart.getData().retainAll();
            chart.getData().addAll(firstChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void loadFinePayedGraph(String timeSec) {
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }  
        chart.getData().removeAll();
        countMap =  new HashMap<>();  
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series();  
        
        xAxis.setLabel("Duration");
        yAxis.setLabel("Collected Fine");
        firstChart.setName("Fine Collected");
           
        String query = null,rowId;
        switch (timeSec) {
            case "By Day":
                query = "SELECT SUM(fine), submitDate FROM REPORT WHERE isSubmit = 'true' GROUP BY submitDate" ;
                break;
            case "By Week": 
//                by weekly
                break;
            case "By Month":
                query = "SELECT SUM(fine), MONTH(submitDate) AS mon, YEAR(submitDate) as year FROM REPORT WHERE isSubmit = 'true' GROUP BY MONTH(submitDate), YEAR(submitDate)" ;
                break;
            case "By Year": 
                query = "SELECT SUM(fine), YEAR(submitDate) as submitDate FROM REPORT WHERE isSubmit = 'true' GROUP BY YEAR(submitDate)" ;
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query);

            while (result1.next()) {            
                if(timeSec.equals("By Month")){
                    rowId = result1.getString("mon")+"-"+result1.getString("year");
                }else{
                    rowId = result1.getString("submitDate");    
                }
                
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0,result1.getInt(1));
                keyArray.add(rowId);
            }
            Collections.sort(keyArray);
            
            for(int i=0;i<keyArray.size();i++){
                firstChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(0)));
            }
            chart.getData().retainAll();
            chart.getData().addAll(firstChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    private void initDropdown() {
        timeSelect.getItems().add("All time");
        timeSelect.getItems().add("This month");
        timeSelect.getItems().add("Last month");
        timeSelect.getItems().add("This Year");
        timeSelect.getItems().add("Last Year");
        catSelect.getItems().add("Book");
        catSelect.getItems().add("Member");
        catSelect.getItems().add("Date");
    }

    
    private void initGraphDropdown() {
        graphCatSelect.getItems().add("Total Add");
        graphCatSelect.getItems().add("Transaction");
        graphCatSelect.getItems().add("Late Submission Count");
        graphCatSelect.getItems().add("Total Fine Payed");
        graphTimeSelect.getItems().add("By Day");
        graphTimeSelect.getItems().add("By Week");
        graphTimeSelect.getItems().add("By Month");
        graphTimeSelect.getItems().add("By Year");
    }

    public void getController(MainController mainController) {
        this.mainController = mainController;
    }

    
    public static class Comparison{
        private final SimpleIntegerProperty number;
        private final SimpleStringProperty searchID;
        private final SimpleIntegerProperty issueCount;
        private final SimpleIntegerProperty submitCount;
        
        public Comparison(int no, String id, int countIssue, int countSubmission){
            this.number = new SimpleIntegerProperty(no);
            this.searchID = new SimpleStringProperty(id);
            this.issueCount = new SimpleIntegerProperty(countIssue);
            this.submitCount = new SimpleIntegerProperty(countSubmission);
        }

        public Integer getNumber() {
            return number.get();
        }
        public String getSearchID() {
            return searchID.get();
        }
        public Integer getIssueCount() {
            return issueCount.get();
        }
        public Integer getSubmitCount() {
            return submitCount.get();
        }      
    }
    
    
}
