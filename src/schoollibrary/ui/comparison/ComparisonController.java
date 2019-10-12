
package schoollibrary.ui.comparison;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private JFXButton searchButton;
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
        
        if(timeSec == null || catSec == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        }    
        switch (timeSec) {
            case "All time":
                loadAllTimeData(catSec);
                break;
            case "This month":
                loadThisMonthData(catSec);
                break;     
            case "Last month":
                loadLastMonthData(catSec);
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
//        loadData();
    }

    
//    private void loadData() {
//        list.clear();
//        
//        LocalDate sDate = LocalDate.now();
//        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, -14);
//        
//        String query = "SELECT * FROM ISSUE WHERE issueDate <= '"+ sDate +"' AND issueDate >= '"+ sdf.format(cal.getTime())+"' ";
//        ResultSet result = databaseHandler.execQuery(query);
//        int i=0;
//
//        
////        to modify
//         
//    }
    
    
    private void loadAllTimeData(String category) {
        String query1 = null,id = null;
        switch (category) {
            case "By book":
                query1 =  "SELECT bookID, COUNT(issueDate) as count1, COUNT(submitDate) as count2 FROM REPORT GROUP BY bookID";
                id = "bookID";
                break;
            case "By member":
                query1 = "SELECT memberID, COUNT(issueDate) as count1, COUNT(submitDate) as count2 FROM REPORT GROUP BY memberID";
                id = "memberID";
                break;
            case "By date":
                break;
            default:
                break;
        }
        list.clear();
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
    
    
    private void initDropdown() {
        
        timeSelect.getItems().add("All time");
        timeSelect.getItems().add("This month");
        timeSelect.getItems().add("Last month");
        catSelect.getItems().add("By book");
        catSelect.getItems().add("By member");
        catSelect.getItems().add("By date");
    }

    private void loadThisMonthData(String category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadLastMonthData(String category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
