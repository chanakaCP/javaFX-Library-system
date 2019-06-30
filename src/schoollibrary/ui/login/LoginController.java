
package schoollibrary.ui.login;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;
import schoollibrary.ui.main.MainController;
import schoollibrary.ui.setting.Preferences;
import schoollibrary.util.LibraryAssistantUtil;


public class LoginController implements Initializable {
    private AnchorPane rootPane;
    @FXML
    private Label titleLable;
    @FXML
    private TextField logName;
    @FXML
    private PasswordField logPassword;
    @FXML
    private AnchorPane rootPaneLog;
  
    Preferences preference;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
    }    

    @FXML
    private void loginAction(ActionEvent event) {
        titleLable.setText("Library System Login");
        titleLable.setStyle(" -fx-text-fill:#cfd8dc");
        
        String user = logName.getText();
        String pwd = DigestUtils.shaHex(logPassword.getText());
        
        if(user.equals(preference.getUsername()) && pwd.equals(preference.getPassword())){
            closeStage();
            loadMain();
        }else if(!user.equals(preference.getUsername() )) {
            titleLable.setText("Invalid Username");
            titleLable.setStyle(" -fx-text-fill: #e65100");
        }else if(!pwd.equals(preference.getPassword())){
            titleLable.setText("Invalid Password");
            titleLable.setStyle(" -fx-text-fill: #e65100");
        }
    }

    
    @FXML
    private void cancelAction(ActionEvent event) {
        System.exit(0);
    }
    
    
    private void closeStage() {
        Stage stage = (Stage)rootPaneLog.getScene().getWindow();
        stage.close();
    }
     
     
    void loadMain(){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/schoollibrary/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library System");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
