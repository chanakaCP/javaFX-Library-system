
package schoollibrary.ui.submission;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
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
import schoollibrary.ui.main.MainController;


public class SubmissionController implements Initializable {

    ObservableList<SubmissionController.Submission> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private JFXDatePicker datePick;
    @FXML
    private TableView<Submission> tableViewCol;
    @FXML
    private TableColumn<Submission,Integer> noCol;
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
    @FXML
    private TableColumn<Submission,Integer> fineCol;
    @FXML
    private TableColumn<Submission,Integer> dateCountCol;
    
    
    DatabaseHandler databaseHandler;   
    MainController mainController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDropdown();
        searchKey.setDisable(true);
        datePick.setDisable(true);
       
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchKey.setText("");
            datePick.setValue(null);
            switch (newValue) {
                case "View All":          
                    searchKey.setDisable(true);
                    datePick.setDisable(true);
                    break;
                case "Book ID":
                case "Member ID":                   
                    searchKey.setDisable(false);
                    datePick.setDisable(true);
                    break;
                case "Issue Date":
                case "Submission Date":
                    searchKey.setDisable(true);
                    datePick.setDisable(false);
                    break;
                default:
                    break;
            }
        });
        initCol();
        loadData();
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
        if(choice.equals("View All")){
            loadSearchData("All",searchVal);
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
                loadSearchDate("issueDate",searchDate);
                break;
             case "Submission Date":
                loadSearchDate("submitDate",searchDate);
                break;
        }    
    }

    
    @FXML
    private void cancel(ActionEvent event) {
        if(choiceKey.getValue() == null ||  (datePick.getValue() == null && searchKey.getText().equals("")) ){  
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
            return;
        }
        datePick.setValue(null);
        searchKey.setText("");    
        loadData();     
    }

    
    private void initCol() {
        noCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        b_idCol.setCellValueFactory(new PropertyValueFactory<>("b_id"));
        m_idCol.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        i_timeCol.setCellValueFactory(new PropertyValueFactory<>("issue_date"));
        s_timeCol.setCellValueFactory(new PropertyValueFactory<>("submission_date"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("r_count"));
        dateCountCol.setCellValueFactory(new PropertyValueFactory<>("count_date"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
    }

    
    private void loadData() {
        list.clear();
        cancelButton.setText("Cancel");
        LocalDate sDate = LocalDate.now();
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -14);
        
        String query = "SELECT * FROM REPORT WHERE ( submitDate <= '"+ sDate +"' AND submitDate >= '"+ sdf.format(cal.getTime())+"' ) AND isSubmit = 'true' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueDate");
                String submissionDate = result.getString("submitDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                int dCount = Integer.parseInt(result.getString("nuOfDaysKept"));
                int fine = Integer.parseInt(result.getString("fine"));
                list.add(new Submission(i,bookID,memberID,issueDate,submissionDate,rCount,dCount,fine));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    public void loadSearchData(String stream, String value){
        cancelButton.setText("Close");
        String query; 
        if(stream.equals("All")){
            query = "SELECT * FROM REPORT WHERE isSubmit = 'true'";
            searchKey.setDisable(true);
            datePick.setDisable(true);
        }else{          
            query = "SELECT * FROM REPORT WHERE " + stream + " LIKE '%"+value+"%' AND isSubmit = 'true' ";               
            if(searchKey.isDisable()){
                searchKey.setDisable(true);
                datePick.setDisable(false);
            }else{

                searchKey.setDisable(false);
                datePick.setDisable(true);
            }
        }
    
        list.clear();         
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) { 
                i++;
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueDate");
                String submissionDate = result.getString("submitDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                int dCount = Integer.parseInt(result.getString("nuOfDaysKept"));
                int fine = Integer.parseInt(result.getString("fine"));
                list.add(new Submission(i,bookID,memberID,issueDate,submissionDate,rCount,dCount,fine));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    public void loadSearchDate(String stream, LocalDate value){
        cancelButton.setText("Close");
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        
        list.clear();  
        String query = "SELECT * FROM REPORT WHERE DATE("+stream+") = '"+value+"' AND isSubmit = 'true' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) { 
                i++;
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueDate = result.getString("issueDate");
                String submissionDate = result.getString("submitDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                int dCount = Integer.parseInt(result.getString("nuOfDaysKept"));
                int fine = Integer.parseInt(result.getString("fine"));
                list.add(new Submission(i,bookID,memberID,issueDate,submissionDate,rCount,dCount,fine));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(SubmissionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    private void initDropdown() {
        choiceKey.getItems().clear();
        choiceKey.getItems().add("View All");
        choiceKey.getItems().add("Book ID");
        choiceKey.getItems().add("Member ID");
        choiceKey.getItems().add("Issue Date");
        choiceKey.getItems().add("Submission Date");
    }

    public void getController(MainController mainController){  
        this.mainController = mainController;
    }
    
    
    public static class Submission{
       private final SimpleIntegerProperty number;
       private final SimpleStringProperty b_id;
       private final SimpleStringProperty m_id;
       private final SimpleStringProperty issue_date;
       private final SimpleStringProperty submission_date;
       private final SimpleIntegerProperty r_count;
       private final SimpleIntegerProperty count_date;
       private final SimpleIntegerProperty fine;
       
       public Submission(int no, String bid, String mid, String i_date, String s_date, int count, int d_count, int fine){
            this.number = new SimpleIntegerProperty(no);
            this.b_id = new SimpleStringProperty(bid);
            this.m_id = new SimpleStringProperty(mid);
            this.issue_date = new SimpleStringProperty(i_date);
            this.submission_date = new SimpleStringProperty(s_date);
            this.r_count = new SimpleIntegerProperty(count);
            this.count_date = new SimpleIntegerProperty(d_count);
            this.fine = new SimpleIntegerProperty(fine);
        }

        public Integer getNumber() {
            return number.get();
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
        public Integer getFine() {
            return fine.get();
        }
        public Integer getCount_date() {
            return count_date.get();
        }
        public Integer getR_count() {
            return r_count.get();
        }      
    }
    
}
