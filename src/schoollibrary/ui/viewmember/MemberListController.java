
package schoollibrary.ui.viewmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.addbook.AddBookController;
import schoollibrary.ui.editmember.EditMemberController;
import schoollibrary.ui.main.MainController;
import schoollibrary.ui.memberDetails.MemberDetailsController;
import schoollibrary.util.LibraryAssistantUtil;


public class MemberListController implements Initializable {

    ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    
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
    private TableView<Member> tableViewCol;
    @FXML
    private TableColumn<Member,Integer> nuCol;
    @FXML
    private TableColumn<Member,String> idCol;
    @FXML
    private TableColumn<Member,String> nameCol;
    @FXML
    private TableColumn<Member,String> aDateCol;
    @FXML
    private TableColumn<Member,String> valCol;
       
    DatabaseHandler databaseHandler;
    MainController mainController;
    @FXML
    private JFXButton searchButton;
    @FXML
    private JFXButton addButton;
   
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDropdown();
        searchKey.setDisable(true);
        datePick.setDisable(true);
        
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchKey.setText("");
            datePick.setValue(null);
            if(newValue.equals("Member ID") || newValue.equals("Member Name")){
                searchKey.setDisable(false);
                datePick.setDisable(true);
            }else if(newValue.equals("Added Date")){
                searchKey.setDisable(true);
                datePick.setDisable(false);
            }
        });
        initCol();
        loadData();
    } 
    
    
    @FXML
    private void memberDeleteAction(ActionEvent event) {
        Member selectedMember = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedMember == null){
            AlertMaker.errorAlert("No member selected","Please select a member for delete");
            return;
        }
        String memberId = selectedMember.getM_id();
        String avail = selectedMember.getValidity();
        
        if(avail.equals("false")){
            AlertMaker.errorAlert("Can`t delete","This member has alredy received a book");
            return; 
        }  
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Deleting Member","Are you sure you want to delete the member " + selectedMember.getM_name()+ "?");
       
        if(responce.get() == ButtonType.OK){
            String query1 = "DELETE FROM MEMBER WHERE M_ID = '" + memberId + "' ";
            if(databaseHandler.execAction(query1)){
                AlertMaker.informatinAlert("Success", "The member  " + selectedMember.getM_name()+ "  was deleted successfully");
                list.remove(selectedMember);
                mainController.refreshGraph();
            }else{
                AlertMaker.errorAlert("Failed", "The member  " + selectedMember.getM_name()+ "  could not be deleted");
            }
        }else{
            AlertMaker.errorAlert("Canceled", "Deletion operation canceled");
        }     
    }
    
   
    @FXML
    private void memberEdtAction(ActionEvent event) {
        Member selectedMember = tableViewCol.getSelectionModel().getSelectedItem();
       
        if(selectedMember == null){
            AlertMaker.errorAlert("No member selected","Please select a member for edit");
            return;
        }
        String avail = selectedMember.getValidity();
        
        if(avail.equals("false")){
            AlertMaker.errorAlert("Can`t edit","This member has alredy received a book");
            return; 
        } 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/editmember/editMember.fxml"));
            Parent parent = loader.load();
            EditMemberController controller = (EditMemberController) loader.getController();
            controller.inflateUI(selectedMember,this);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setOnCloseRequest((e)->{
                loadData();
            });
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);     
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @FXML
    private void memberDetailsAction(ActionEvent event) {
        Member selectedMember = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedMember == null){
            AlertMaker.errorAlert("No member selected","Please select a member for edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/memberDetails/member_details.fxml"));
            Parent parent = loader.load();
            MemberDetailsController controller = (MemberDetailsController) loader.getController();
            controller.viewData(selectedMember.getM_id());
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setOnCloseRequest((e)->{
                loadData();
            });
            stage.setTitle("View Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
           
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            case "Member ID":
                loadSearchData("M_ID",searchVal);
                break;
            case "Member Name":
                loadSearchData("MName",searchVal);
                break;
            case "Added Date":
                loadSearchDate("addedDate",searchDate);
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
        nuCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("m_name"));
        aDateCol.setCellValueFactory(new PropertyValueFactory<>("a_date"));
        valCol.setCellValueFactory(new PropertyValueFactory<>("validity"));      
    }
    
    
    public void loadData() {
        list.clear();
        cancelButton.setText("Close");
        String query = "SELECT * FROM MEMBER";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String memberID = result.getString("M_ID");
                String memberName = result.getString("MName");
                String addDate = result.getString("addedDate");
                String membervalidity; 
                if(result.getBoolean("isSubmit")){
                    membervalidity = "Valid";
                }else{
                    membervalidity = "Invalid";
                }
                
                list.add(new Member(i,memberID,memberName,addDate,membervalidity));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }

    
    public void loadSearchData(String stream, String value){
        cancelButton.setText("Cancel");
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        list.clear();  
        String query = "SELECT * FROM MEMBER WHERE " + stream + " LIKE '%"+value+"%' ";       
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String memberID = result.getString("M_ID");
                String memberName = result.getString("MName");
                String addDate = result.getString("addedDate");
                String membervalidity; 
                if(result.getBoolean("isSubmit")){
                    membervalidity = "Valid";
                }else{
                    membervalidity = "Invalid";
                }
                
                list.add(new Member(i,memberID,memberName,addDate,membervalidity));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    public void loadSearchDate(String stream, LocalDate value){
        cancelButton.setText("Cancel");
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        
        list.clear();  
        String query = "SELECT * FROM MEMBER WHERE DATE("+stream+") = '"+value+"' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String memberID = result.getString("M_ID");
                String memberName = result.getString("MName");
                String addDate = result.getString("addedDate");
                String membervalidity; 
                if(result.getBoolean("isSubmit")){
                    membervalidity = "Valid";
                }else{
                    membervalidity = "Invalid";
                }
                
                list.add(new Member(i,memberID,memberName,addDate,membervalidity));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    
    public void getController(MainController mainController){  
        this.mainController = mainController;
    }

    
    private void initDropdown() {
        choiceKey.getItems().add("Member ID");
        choiceKey.getItems().add("Member Name");
        choiceKey.getItems().add("Added Date");
    }

    @FXML
    private void loadAddMember(ActionEvent event) {
    }
    
  
    public static class Member{
        private final SimpleIntegerProperty number;
        private final SimpleStringProperty m_id;
        private final SimpleStringProperty m_name;
        private final SimpleStringProperty a_date;
        private final SimpleStringProperty validity;
      
        public Member(int no, String id, String name, String aDate, String valid){
            this.number = new SimpleIntegerProperty(no);
            this.m_id = new SimpleStringProperty(id);
            this.m_name = new SimpleStringProperty(name);
            this.a_date = new SimpleStringProperty(aDate);
            this.validity = new SimpleStringProperty(valid);
        }
        public Integer getNumber() {
            return number.get();
        }
        public String getM_id() {
            return m_id.get();
        }
        public String getM_name() {
            return m_name.get();
        }      
        public String getA_date() {
            return a_date.get();
        }
        public String getValidity() {
            return validity.get();
        }
    }
    
}
