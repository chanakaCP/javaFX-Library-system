
package schoollibrary.ui.addbook;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.main.MainController;


public class AddBookController implements Initializable {
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField b_id;
    @FXML
    private JFXTextField b_name;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXTextField price;
    @FXML
    private JFXTextField description;
    @FXML
    private JFXTextField pages;
    @FXML
    private JFXDatePicker r_date;
   
    DatabaseHandler databaseHandler;
    MainController mainController;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void saveBook(ActionEvent event) {
        String bookID = b_id.getText();
        String bookName = b_name.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();
        String bookDescription = description.getText();
        String bookPrice = price.getText();
        String bookPage = pages.getText();
        LocalDate bookRecieveDate = r_date.getValue();
        
        try{
            float bookPricefloat = Float.parseFloat(bookPrice);
        }catch (NumberFormatException e ) {
            AlertMaker.errorAlert("Invalid input","Please enter valid arguments for PRICE");
            return;
        }       
        try{
            int bookPageInt =  Integer.parseInt(bookPage);
        }catch (NumberFormatException e ) {
            AlertMaker.errorAlert("Invalid input","Please enter valid arguments for NO OF PAGES");
            return;
        }       
    
        if(bookID.trim().isEmpty()||bookName.trim().isEmpty()||bookAuthor.trim().isEmpty()||bookPublisher.trim().isEmpty()||
           bookPrice.trim().isEmpty()||bookPage.trim().isEmpty()||bookRecieveDate == null){
            
            AlertMaker.errorAlert("Error","Please fill all the fields");
            return;
        } 
        
        String query = "INSERT INTO BOOK VALUES ( " +
                        "'" + bookID + "'," +
                        "'" + bookName + "'," +
                        "'" + bookAuthor + "'," +
                        "'" + bookPublisher + "'," +
                              bookPrice + "," +
                              bookPage + "," +
                        "'" + bookRecieveDate + "'," +
                        "'" + bookDescription + "'," +
                        "'" + "true" + "'" +
                ")";
        
        if(databaseHandler.execAction(query)){
            AlertMaker.informatinAlert("Success","Insert Book Successfully"); 
            b_id.setText("");
            b_name.setText("");
            author.setText("");
            publisher.setText("");
            price.setText("");
            description.setText("");
            pages.setText("");
            r_date.setValue(null);
            mainController.refreshGraph();
        }else{
            AlertMaker.errorAlert("Can`t save","This book is alredy exsist");
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
