 
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;



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
    private LineChart<String,Number> chart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    
    private XYChart.Series firstChart; 
    private XYChart.Series secondChart; 
    
    DatabaseHandler databaseHandler;   
    
       
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
                loadNewAddGraph(timeSec);
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
        chart.getData().removeAll();
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }  
        countMap =  new HashMap<>(); 
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series<>();
        secondChart = new XYChart.Series<>();
        
        xAxis.setLabel("Day");
        yAxis.setLabel("Count");
        firstChart.setName("Issue");
        secondChart.setName("Submission");
            
        String query1 = null, query2 = null,rowId;
        switch (timeSec) {
            case "By Day":
                query1 = "SELECT issueDate as firstData, COUNT(*) as count1 FROM REPORT GROUP BY issueDate" ;
                query2 = "SELECT submitDate as secondData, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY submitDate";
                break;
            case "By Week": 
                query1 = "SELECT DATEADD(week, DATEDIFF(week, 0, issueDate), 0) as firstData, COUNT(*) as count1 FROM REPORT GROUP BY DATEADD(week, DATEDIFF(week, 0, issueDate), 0) " ;
                query2 = "SELECT DATEADD(week, DATEDIFF(week, 0, submitDate), 0) as secondData, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY DATEADD(week, DATEDIFF(week, 0, submitDate), 0)";
                break;
            case "By Month":
                query1 = "SELECT count(*) as count1, cast(datepart(yyyy,issueDate) +' '+ datename(m,column) as varchar) as firstData FROM REPORT GROUP BY CAST(YEAR(issueDate) AS VARCHAR(4)) + '-' + CAST(MONTH(issueDate) AS VARCHAR(2)) " ;
                query2 = "SELECT count(*) as count2, CAST(YEAR(submitDate) AS VARCHAR(4)) + '-' + CAST(MONTH(submitDate) AS VARCHAR(2)) as secondDate FROM REPORT GROUP BY CAST(YEAR(submitDate) AS VARCHAR(4)) + '-' + CAST(MONTH(submitDate) AS VARCHAR(2)) ";
                break;
            case "By Year": 
                query1 = "SELECT YEAR(issueDate) as firstData, COUNT(*) as count1 FROM REPORT GROUP BY YEAR(issueDate)" ;
                query2 = "SELECT YEAR(submitDate) as secondData, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY YEAR(submitDate)";
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query1);
            ResultSet result2 = databaseHandler.execQuery(query2);

            while (result1.next()) {            
                rowId = result1.getString("firstData");
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0, result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
                keyArray.add(rowId);
            }
            while (result2.next()) {
                rowId = result2.getString("secondData");      
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
            chart.getData().addAll(firstChart);
            chart.getData().addAll(secondChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    private void loadNewAddGraph(String timeSec) {
        chart.getData().removeAll();
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }    
        countMap =  new HashMap<>();  
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series<>();
        secondChart = new XYChart.Series<>();
        
        xAxis.setLabel("Day");
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
//                query1 = "SELECT DATEADD(week, DATEDIFF(week, 0, issueDate), 0) as firstData, COUNT(*) as count1 FROM REPORT GROUP BY DATEADD(week, DATEDIFF(week, 0, issueDate), 0) " ;
//                query2 = "SELECT DATEADD(week, DATEDIFF(week, 0, submitDate), 0) as secondData, COUNT(*) as count2 FROM REPORT WHERE isSubmit = 'true' GROUP BY DATEADD(week, DATEDIFF(week, 0, submitDate), 0)";
                break;
            case "By Month":
//                query1 = "SELECT count(*) as count1, cast(datepart(yyyy,issueDate) +' '+ datename(m,column) as varchar) as firstData FROM REPORT GROUP BY CAST(YEAR(issueDate) AS VARCHAR(4)) + '-' + CAST(MONTH(issueDate) AS VARCHAR(2)) " ;
//                query2 = "SELECT count(*) as count2, CAST(YEAR(submitDate) AS VARCHAR(4)) + '-' + CAST(MONTH(submitDate) AS VARCHAR(2)) as secondDate FROM REPORT GROUP BY CAST(YEAR(submitDate) AS VARCHAR(4)) + '-' + CAST(MONTH(submitDate) AS VARCHAR(2)) ";
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
                rowId = result1.getString("addedDate");
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0, result1.getInt("count1"));
                countMap.get(rowId).add(1, 0);
                keyArray.add(rowId);
            }
            while (result2.next()) {
                rowId = result2.getString("addedDate");      
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
            chart.getData().addAll(firstChart);
            chart.getData().addAll(secondChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }

    
    private void loadLateSubmissionGraph(String timeSec) {
        chart.getData().removeAll();
        if(firstChart != null){
            firstChart.getData().clear();
            secondChart.getData().clear();
        }  
        countMap =  new HashMap<>();  
        keyArray =  new ArrayList<>();
        firstChart = new XYChart.Series<>();  
        
        xAxis.setLabel("Day");
        yAxis.setLabel("Collected Fine");
        firstChart.setName("Book");
           
        String query1 = null,rowId;
        switch (timeSec) {
            case "By Day":
                query1 = "SELECT submitDate, SUM(fine) as sum FROM BOOK REPORT BY submitDate" ;
                break;
            case "By Week": 
//                query1 = "SELECT DATEADD(week, DATEDIFF(week, 0, issueDate), 0) as firstData, COUNT(*) as count1 FROM REPORT GROUP BY DATEADD(week, DATEDIFF(week, 0, issueDate), 0) " ;
                break;
            case "By Month":
//                query1 = "SELECT count(*) as count1, cast(datepart(yyyy,issueDate) +' '+ datename(m,column) as varchar) as firstData FROM REPORT GROUP BY CAST(YEAR(issueDate) AS VARCHAR(4)) + '-' + CAST(MONTH(issueDate) AS VARCHAR(2)) " ;
                break;
            case "By Year": 
                query1 = "SELECT YEAR(submitDate) as submitDate, SUM(fine) as sum FROM REPORT GROUP BY YEAR(submitDate)" ;
                break;
            default:
                break;
        }
        try {            
            ResultSet result1 = databaseHandler.execQuery(query1);

            while (result1.next()) {            
                rowId = result1.getString("submitDate");
                countMap.put(rowId,new ArrayList<>());
                countMap.get(rowId).add(0,result1.getInt("sum"));
                keyArray.add(rowId);
            }
            Collections.sort(keyArray);
            
            for(int i=0;i<keyArray.size();i++){
                firstChart.getData().add(new XYChart.Data(keyArray.get(i),countMap.get(keyArray.get(i)).get(0)));
            }
            chart.getData().addAll(firstChart);
            countMap.clear();
        } catch (SQLException ex) {
            Logger.getLogger(ComparisonController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    
    private void loadFinePayedGraph(String timeSec) {
        
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
