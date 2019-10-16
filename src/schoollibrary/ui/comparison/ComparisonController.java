
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;



public class ComparisonController implements Initializable {
    
    ObservableList<ComparisonController.Comparison> list = FXCollections.observableArrayList();
    
    
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
   
    
    DatabaseHandler databaseHandler;   
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDropdown();
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
        int pastMonth = sDate.minusMonths(1).getMonth().getValue();
        
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
                loadMonthData(catSec,year,pastMonth);
                break;
            default:
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

    
    private void loadData(String category) {
        list.clear();
        HashMap<String, List<Integer>> countMap = new HashMap<>();
        String query1,query2,rowId;
        
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
            int i=0;
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
        String query1 = null, query2 = null, id = null;
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
        int i=0;
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
        HashMap<String, List<Integer>> countMap = new HashMap<>();
        list.clear();      
        String query1 = null, query2 = null, id = null;
             
        switch (category) {
            case "Book":
                query1 =  "SELECT DISTINCT bookID, COUNT(issueDate) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+month+"-1' AND issueDate <= '"+year+"-"+month+"-31' GROUP BY bookID";
                query2 =  "SELECT DISTINCT bookID, COUNT(submitDate) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+month+"-1' AND submitDate <= '"+year+"-"+month+"-31' AND isSubmit = 'true' GROUP BY bookID";
                id = "bookID";
                break;
            case "Member":
                query1 =  "SELECT DISTINCT memberID, COUNT(issueDate) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+month+"-1' AND issueDate <= '"+year+"-"+month+"-31' GROUP BY memberID";
                query2 =  "SELECT DISTINCT memberID, COUNT(submitDate) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+month+"-1' AND submitDate <= '"+year+"-"+month+"-31' AND isSubmit = 'true' GROUP BY memberID";
                id = "memberID";
                break;              
            case "Date":
                query1 =  "SELECT DISTINCT issueDate, COUNT(*) as count1 FROM REPORT WHERE issueDate >= '"+year+"-"+month+"-1' AND issueDate <= '"+year+"-"+month+"-31' GROUP BY issueDate";
                query2 =  "SELECT DISTINCT submitDate, COUNT(*) as count2 FROM REPORT WHERE submitDate >= '"+year+"-"+month+"-1' AND submitDate <= '"+year+"-"+month+"-31' AND isSubmit = 'true' GROUP BY submitDate";
                id = "issueDate";
                break;
            default:
                break;
        }
         
        int i=0;
        String rowId;
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
                if(id.equals("issueDate")){
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

    
    private void initDropdown() {
        
        timeSelect.getItems().add("All time");
        timeSelect.getItems().add("This month");
        timeSelect.getItems().add("Last month");
        catSelect.getItems().add("Book");
        catSelect.getItems().add("Member");
        catSelect.getItems().add("Date");
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
