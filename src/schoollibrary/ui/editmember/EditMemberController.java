
package schoollibrary.ui.editmember;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.viewmember.MemberListController;

public class EditMemberController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField m_id;
    @FXML
    private JFXTextField m_name;

    DatabaseHandler databaseHandler;
    MemberListController memberListController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void updateMember(ActionEvent event) {
        String memberID = m_id.getText();
        String memberName = m_name.getText();
              
        if(memberName.trim().isEmpty()){
            AlertMaker.errorAlert("Error","Please fill all the fields");
            return;
        }
        String query = "UPDATE MEMBER SET MName = '" + memberName + "' "
                        + "WHERE M_ID = '" + memberID +"' ";
        
        if(databaseHandler.execAction(query)){
            memberListController.loadData();
            AlertMaker.informatinAlert("Success","Update member Successfully"); 
        }else{
            AlertMaker.errorAlert("Failed","Update Failed");
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void inflateUI(MemberListController.Member member,MemberListController memberListController){
        m_id.setText(member.getM_id());
        m_id.setEditable(false);
        m_name.setText(member.getM_name());     
        this.memberListController = memberListController;
    }
    
}
