
package schoollibrary.ui.viewmember;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
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
import schoollibrary.util.LibraryAssistantUtil;


public class MemberListController implements Initializable {

    ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Member> tableViewCol;
    @FXML
    private TableColumn<Member,String> idCol;
    @FXML
    private TableColumn<Member,String> nameCol;
    @FXML
    private TableColumn<Member,Boolean> valCol;
       
    DatabaseHandler databaseHandler;
    MainController mainController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceKey.getItems().add("Member ID");
        choiceKey.getItems().add("Member Name");
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchKey.setText("");
            if(newValue.equals("Member ID") || newValue.equals("Member Name")){
                searchKey.setDisable(false);
            }
        });
        initCol();
        loadData();
    } 
    
    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("m_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("m_name"));
        valCol.setCellValueFactory(new PropertyValueFactory<>("validity"));      
    }
    
    public void loadData() {
        searchKey.setDisable(true);
        list.clear();
        databaseHandler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM MEMBER";

        ResultSet result = databaseHandler.execQuery(query);
     
        try {
            while (result.next()) {
                String memberID = result.getString("M_ID");
                String memberName = result.getString("MName");
                boolean membervalidity = result.getBoolean("isSubmit");
                
                list.add(new Member(memberID,memberName,membervalidity));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }

    public void loadSearchData(String stream, String value){
        list.clear();  
        String query;
        if(stream.equals("M_ID")){
            query = "SELECT * FROM MEMBER WHERE " + stream + " = '"+value+"' ";
        }else{
            query = "SELECT * FROM MEMBER WHERE " + stream + " LIKE '%"+value+"%' ";
        }
        System.out.println(query);
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) { 
                String memberID = result.getString("M_ID");
                String memberName = result.getString("MName");
                boolean membervalidity = result.getBoolean("isSubmit");
                
                list.add(new Member(memberID,memberName,membervalidity));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    @FXML
    private void memberDeleteAction(ActionEvent event) {
        Member selectedMember = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedMember == null){
            AlertMaker.errorAlert("No member selected","Please select a member for delete");
            return;
        }
        String memberId = selectedMember.getM_id();
        boolean avail = selectedMember.getValidity();
        
        if(avail == false){
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
        boolean avail = selectedMember.getValidity();
        
        if(avail == false){
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
    private void searchAction(ActionEvent event) {
        String choice = choiceKey.getValue();
        String searchVal = searchKey.getText();
          
        if(choice == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        } 
        if(searchVal.isEmpty()){
            AlertMaker.errorAlert("Can`t search","Please enter value for search");
            return;
        }

        switch (choice) {
            case "Member ID":
                loadSearchData("M_ID",searchVal);
                break;
            case "Member Name":
                loadSearchData("MName",searchVal);
                break;
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        if(choiceKey.getValue() == null){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
        searchKey.setText("");
        choiceKey.setValue(null);        
        choiceKey.setValue(null);
        loadData();
    }
    
    public void getController(MainController mainController){  
        this.mainController = mainController;
    }
    
    
    public static class Member{
        private final SimpleStringProperty m_id;
        private final SimpleStringProperty m_name;
        private final SimpleBooleanProperty validity;
      
        public Member(String id, String name, boolean valid){
            this.m_id = new SimpleStringProperty(id);
            this.m_name = new SimpleStringProperty(name);
            this.validity = new SimpleBooleanProperty(valid);
        }

        public String getM_id() {
            return m_id.get();
        }

        public String getM_name() {
            return m_name.get();
        }
        
        public boolean getValidity() {
            return validity.get();
        }
    }
    
}
