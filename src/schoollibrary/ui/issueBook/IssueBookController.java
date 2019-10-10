
package schoollibrary.ui.issueBook;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
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

public class IssueBookController implements Initializable {
    
    ObservableList<IssueBook> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private JFXDatePicker datePick;
    @FXML
    private TableView<IssueBook> tableViewCol;
    @FXML
    private TableColumn<IssueBook,Integer> noCol;
    @FXML
    private TableColumn<IssueBook,String> b_idCol;
    @FXML
    private TableColumn<IssueBook,String> m_idCol;
    @FXML
    private TableColumn<IssueBook,String> timeCol;
    @FXML
    private TableColumn<IssueBook,String> renewCol;
    @FXML
    private TableColumn<IssueBook,String> subCol;
    @FXML
    private TableColumn<IssueBook,Integer> countCol;  
    @FXML
    private TableColumn<IssueBook,Integer> dateCountCol;
    @FXML
    private TableColumn<IssueBook,Integer> fineCol;

    DatabaseHandler databaseHandler;   
 
       
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        searchKey.setDisable(true);
        datePick.setDisable(true);
        initDropdown();
        
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
    
    private void initCol() {
        noCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        b_idCol.setCellValueFactory(new PropertyValueFactory<>("b_id"));
        m_idCol.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("issue_date"));
        renewCol.setCellValueFactory(new PropertyValueFactory<>("renew_date"));
        subCol.setCellValueFactory(new PropertyValueFactory<>("will_sub"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("r_count"));
        dateCountCol.setCellValueFactory(new PropertyValueFactory<>("d_count"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
    }

    public void loadData() {
        list.clear();
        databaseHandler = DatabaseHandler.getInstance();
        
        LocalDate sDate = LocalDate.now();
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -14);
        
        String query = "SELECT * FROM ISSUE WHERE issueDate <= '"+ sDate +"' AND issueDate >= '"+ sdf.format(cal.getTime())+"' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueTime = result.getString("issueDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                String renewDate; 
                if(rCount == 0){
                    renewDate = "Not renewed";
                }else{
                    renewDate = result.getString("lastRenewDate");    
                }                
                String subDay = result.getString("willSubmit");
                int dCount = countDays(result.getString("lastRenewDate"));
                int fine = Integer.parseInt(result.getString("finePerDay"));
                list.add(new IssueBook(i,bookID,memberID,issueTime,renewDate,subDay,rCount,dCount,fine));
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }

    public void loadSearchData(String stream, String value){    
        String query; 
        if(stream.equals("All")){
            query = "SELECT * FROM ISSUE";
            searchKey.setDisable(true);
            datePick.setDisable(true);
        }else{          
            query = "SELECT * FROM ISSUE WHERE " + stream + " LIKE '%"+value+"%' ";               
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
                String issueTime = result.getString("issueDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                String renewDate; 
                if(rCount == 0){
                    renewDate = "Not renewed";
                }else{
                    renewDate = result.getString("lastRenewDate");    
                }                
                String subDay = result.getString("willSubmit");
                int dCount = countDays(result.getString("lastRenewDate"));
                int fine = Integer.parseInt(result.getString("finePerDay"));
                list.add(new IssueBook(i,bookID,memberID,issueTime,renewDate,subDay,rCount,dCount,fine));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(IssueBookController.class.getName()).log(Level.SEVERE, null, ex);
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
        String query = "SELECT * FROM ISSUE WHERE DATE("+stream+") = '"+value+"' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) { 
                i++;
                String bookID = result.getString("bookID");
                String memberID = result.getString("memberID");
                String issueTime = result.getString("issueDate");
                int rCount = Integer.parseInt(result.getString("renewCount"));
                String renewDate; 
                if(rCount == 0){
                    renewDate = "Not renewed";
                }else{
                    renewDate = result.getString("lastRenewDate");    
                }                
                String subDay = result.getString("willSubmit");
                int dCount = countDays(result.getString("lastRenewDate"));
                int fine = Integer.parseInt(result.getString("finePerDay"));
                list.add(new IssueBook(i,bookID,memberID,issueTime,renewDate,subDay,rCount,dCount,fine));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(IssueBookController.class.getName()).log(Level.SEVERE, null, ex);
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
        }    
    }

    @FXML
    private void cancel(ActionEvent event) {
        if(choiceKey.getValue() == null ||  (datePick.getValue() == null && searchKey.getText().equals(""))){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
        datePick.setValue(null);
        searchKey.setText("");    
        loadData(); 
    }
    
    
    int countDays(String dateIssue){
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
        return dateCount;
    }
    
     private void initDropdown() {
        choiceKey.getItems().add("View All");
        choiceKey.getItems().add("Book ID");
        choiceKey.getItems().add("Member ID");
        choiceKey.getItems().add("Issue Date");
    }
    
    public static class IssueBook{
       public final SimpleIntegerProperty number;
       public final SimpleStringProperty b_id;
       public final SimpleStringProperty m_id;
       public final SimpleStringProperty issue_date;
       public final SimpleStringProperty renew_date;
       public final SimpleStringProperty will_sub;
       public final SimpleIntegerProperty r_count;
       public final SimpleIntegerProperty d_count;
       public final SimpleIntegerProperty fine;
       
       public IssueBook(int no, String bid, String mid, String time, String renewDate, String willSub, int count, int dates, int fine){
            this.number = new SimpleIntegerProperty(no);
            this.b_id = new SimpleStringProperty(bid);
            this.m_id = new SimpleStringProperty(mid);
            this.issue_date = new SimpleStringProperty(time);
            this.renew_date = new SimpleStringProperty(renewDate);
            this.will_sub = new SimpleStringProperty(willSub);
            this.r_count = new SimpleIntegerProperty(count);
            this.d_count = new SimpleIntegerProperty(dates);
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
        public String getRenew_date() {
            return renew_date.get();
        }       
        public String getWill_sub() {
            return will_sub.get();
        }
        public Integer getR_count() {
            return r_count.get();
        }
        public Integer getD_count() {
            return d_count.get();
        }
        public Integer getFine() {
            return fine.get();
        }
        
    }
    
    
    
}
