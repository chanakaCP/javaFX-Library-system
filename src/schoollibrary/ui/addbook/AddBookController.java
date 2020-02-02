
package schoollibrary.ui.addbook;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.main.MainController;
import schoollibrary.ui.viewbook.BookListController;


public class AddBookController implements Initializable {
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField b_id;
    @FXML
    private JFXTextField b_name;
     @FXML
    private JFXComboBox<String> catSelector;
    @FXML
    private JFXTextField category;
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
    BookListController bookListController;
//    MainController mainController;
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        category.setDisable(true);
        initComboBox();   

        catSelector.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue != null && newValue.equals("Add New Category")){
                category.setDisable(false);
            }else{
                category.setDisable(true);
            }
        });

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
        String bookCategory;
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
        
        if(category.isDisable()){
            bookCategory = catSelector.getValue();
        }else{
            bookCategory = category.getText();
        }
              
        String query = "INSERT INTO BOOK (B_ID,BName,category,author,publisher,price,pages,receivedDate,description) "
                        + "VALUES ( '" + bookID + "','" 
                                       + bookName + "','" 
                                       + bookCategory + "','" 
                                       + bookAuthor + "','" 
                                       + bookPublisher + "'," 
                                       + bookPrice + "," 
                                       + bookPage + ",'" 
                                       + bookRecieveDate + "','" 
                                       + bookDescription + "')";

        if(databaseHandler.execAction(query)){
            AlertMaker.informatinAlert("Success","Book Insert Successfully"); 
            b_id.setText("");
            b_name.setText("");
            category.setText("");
            author.setText("");
            publisher.setText("");
            price.setText("");
            description.setText("");
            pages.setText("");
            r_date.setValue(null);
            bookListController.loadData();
//            mainController.refreshGraph(); 
            initComboBox();
        }else{
            AlertMaker.errorAlert("Can`t save","Please fill all the fields correctly");
        }   
    }

    
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }
    
//    public void getController(MainController mainController){  
//        this.mainController = mainController;
//    }

    public void getControllerFromBookList(BookListController bookListController){  
        this.bookListController = bookListController;
    }

    private void initComboBox() {
        catSelector.getItems().clear();
        catSelector.getItems().add("Add New Category");
       
        String query = "SELECT DISTINCT category FROM BOOK";
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) {
                catSelector.getItems().add(result.getString("category"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }

    
 
}
