
package schoollibrary.ui.comparison;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class ComparisonController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton searchButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXComboBox<String> timeSelector;
    @FXML
    private JFXComboBox<String> catSelector;
    @FXML
    private TableView<?> tableViewCol;
    @FXML
    private TableColumn<?, ?> noCol;
    @FXML
    private TableColumn<?, ?> idCol;
    @FXML
    private TableColumn<?, ?> issueCol;
    @FXML
    private TableColumn<?, ?> subCol;
   ;
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timeSelector.getItems().add("View All");
        timeSelector.getItems().add("This Month");
        timeSelector.getItems().add("Last Month");
        catSelector.getItems().add("By Book");
        catSelector.getItems().add("By Member");
        catSelector.getItems().add("By date");
    }    

    @FXML
    private void searchAction(ActionEvent event) {
    }

    @FXML
    private void cancel(ActionEvent event) {
        if(timeSelector.getValue() == null && catSelector.getValue() == null){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
        
        timeSelector.setValue(null);
        catSelector.setValue(null);
//        loadData();
    }
    
}
