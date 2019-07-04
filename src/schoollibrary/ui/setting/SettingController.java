
package schoollibrary.ui.setting;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;


public class SettingController implements Initializable {

    @FXML
    private JFXTextField u_name;
    @FXML
    private JFXPasswordField passoword;
    @FXML
    private JFXPasswordField c_password;
    @FXML
    private JFXTextField no_of_days;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField fine;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValue();
    }    

    @FXML
    private void saveSetting(ActionEvent event) {
        String uname = u_name.getText();
        String pass = passoword.getText();
        String conPass = c_password.getText();
        int ndays = Integer.parseInt(no_of_days.getText());
        int dayFine = Integer.parseInt(fine.getText());
        
        if(pass.length() < 16){
            if(pass.equals(conPass)){
                Preferences newPreference = Preferences.getPreferences();
                newPreference.setUsername(uname);
                newPreference.setPassword(pass);
                newPreference.setConfirmPassword(conPass);
                newPreference.setnOfDays(ndays);
                newPreference.setFinePerDay(dayFine);

                Preferences.writeNewPreferences(newPreference);
                closeStage();
            
            }else{
                AlertMaker.errorAlert("Passworrd Error","Password didn`t match");
            }
        }else{
            AlertMaker.errorAlert("Password Error","Password must be of maximum 15 characters length");
        }  
        
    }

    
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void initDefaultValue() {
        Preferences preferences = Preferences.getPreferences();
        u_name.setText(String.valueOf(preferences.getUsername()));
        passoword.setText(String.valueOf(preferences.getPassword()));
        c_password.setText(String.valueOf(preferences.getConfirmPassword()));
        no_of_days.setText(String.valueOf(preferences.getnOfDays()));
        fine.setText(String.valueOf(preferences.getFinePerDay()));
    }
 
    
    private void closeStage() {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }
    
}
