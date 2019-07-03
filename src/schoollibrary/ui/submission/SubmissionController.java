
package schoollibrary.ui.submission;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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


public class SubmissionController implements Initializable {

    ObservableList<SubmissionController.Submission> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private JFXDatePicker datePick;
    @FXML
    private TableView<Submission> tableViewCol;
    @FXML
    private TableColumn<Submission,String> b_idCol;
    @FXML
    private TableColumn<Submission,String> m_idCol;
    @FXML
    private TableColumn<Submission,String> i_timeCol;
    @FXML
    private TableColumn<Submission,String> s_timeCol;
    @FXML
    private TableColumn<Submission,Integer> countCol;

  
    DatabaseHandler databaseHandler;   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceKey.getItems().add("Book ID");
        choiceKey.getItems().add("Member ID");
        choiceKey.getItems().add("Issue Date");
        choiceKey.getItems().add("Submission Date");
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue.equals("Book ID") || newValue.equals("Member ID")){
                searchKey.setDisable(false);
                datePick.setDisable(true);
            }else if(newValue.equals("Issue Date") || newValue.equals("Submission Date")){
                searchKey.setDisable(true);
                datePick.setDisable(false);
            }
        });
        initCol();
        loadData();
    }    

    
    private void initCol() {
        b_idCol.setCellValueFactory(new PropertyValueFactory<>("b_id"));
        m_idCol.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        i_timeCol.setCellValueFactory(new PropertyValueFactory<>("issue_date"));
        s_timeCol.setCellValueFactory(new PropertyValueFactory<>("submission_date"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("r_count"));
    }

    
    private void loadData() {
        searchKey.setDisable(true);
        datePick.setDisable(true);
        list.clear();
        databaseHandler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM SUBMISSION";
        ResultSet result = databaseHandler.execQuery(query);
     
        try {
            while (result.next()) {
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueTime");
                String submissionDate = result.getString("submitTime");
                String rCount = result.getString("renewCount");
                int rCountInt = Integer.parseInt(rCount);
                list.add(new Submission(bookID,memberID,issueDate,submissionDate,rCountInt));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    public void loadSearchData(String stream, String value){
       
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        
        list.clear();  
        String query = "SELECT * FROM SUBMISSION WHERE " + stream + " = '%"+value+"%' ";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) { 
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueTime");
                String submissionDate = result.getString("submitTime");
                String rCount = result.getString("renewCount");
                int rCountInt = Integer.parseInt(rCount);
                list.add(new Submission(bookID,memberID,issueDate,submissionDate,rCountInt));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    public void loadSearchDate(String stream, LocalDate value){
       
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        
        list.clear();  
        String query = "SELECT * FROM SUBMISSION WHERE DATE("+stream+") = '"+value+"' ";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) { 
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueTime");
                String submissionDate = result.getString("submitTime");
                String rCount = result.getString("renewCount");
                int rCountInt = Integer.parseInt(rCount);
                list.add(new Submission(bookID,memberID,issueDate,submissionDate,rCountInt));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    @FXML
    private void searchAction(ActionEvent event) {
        String choice = choiceKey.getValue();
        String searchVal = searchKey.getText();
        LocalDate searchDate = datePick.getValue();
        
        if(choice == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        } 
        if(datePick.isDisable() && searchVal.isEmpty()){
            AlertMaker.errorAlert("Can`t search","Please enter value for search");
            return;
        }
        if(searchKey.isDisable() && searchDate == null){
            AlertMaker.errorAlert("Can`t search","Please enter date for search");
            return;
        }  
        switch (choice) {
            case "Book ID":
                loadSearchData("bookID",searchVal);
                break;
            case "Member ID":
                loadSearchData("memberID",searchVal);
                break;
            case "Issue Date":
                loadSearchDate("issueTime",searchDate);
                break;
             case "Submission Date":
                loadSearchDate("submitTime",searchDate);
                break;
        }    
    }

    
    @FXML
    private void cancel(ActionEvent event) {
        if(searchKey.getText().isEmpty() && datePick.getValue() == null){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
        datePick.setValue(null);
        searchKey.setText("");
        loadData();
    }
    
    
    public static class Submission{
       public final SimpleStringProperty b_id;
       public final SimpleStringProperty m_id;
       public final SimpleStringProperty issue_date;
       public final SimpleStringProperty submission_date;
       public final SimpleIntegerProperty r_count;
       
       public Submission(String bid, String mid, String i_date, String s_date, int count){
            this.b_id = new SimpleStringProperty(bid);
            this.m_id = new SimpleStringProperty(mid);
            this.issue_date = new SimpleStringProperty(i_date);
            this.submission_date = new SimpleStringProperty(s_date);
            this.r_count = new SimpleIntegerProperty(count);
        }


        public String getB_id() {
            return b_id.get();
        }

        public String getM_id() {
            return m_id.get();
        }

        public String getIssue_date() {
            return issue_date.get();
        }

        public String getSubmission_date() {
            return submission_date.get();
        }

        public Integer getR_count() {
            return r_count.get();
        }
        
        
       
    }
    
}
