
package schoollibrary.ui.addmember;

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
import schoollibrary.ui.main.MainController;

public class AddMemberController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField m_id;
    @FXML
    private JFXTextField m_name;

    DatabaseHandler databaseHandler;
    MainController mainController ;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void addMember(ActionEvent event) {
        String memberID = m_id.getText();
        String memberName = m_name.getText();
        
        if(memberID.isEmpty()||memberName.isEmpty()){
            AlertMaker.errorAlert("Can`t save","Please fill all the field");
            return;
        }
        String query = "INSERT INTO MEMBER VALUES ( " +
                        "'" + memberID + "'," +
                        "'" + memberName + "'," +
                        "'" + "true" + "'" +
                ")";
        
        if(databaseHandler.execAction(query)){      
            AlertMaker.informatinAlert("Success","Insert Member Successfully");
            m_id.setText("");
            m_name.setText("");
            mainController.refreshGraph();
        }else{
            AlertMaker.errorAlert("Can`t save","This Member ID is alredy exsist");
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    
    public void getController(MainController mainController){  
        this.mainController = mainController;
    }
}
