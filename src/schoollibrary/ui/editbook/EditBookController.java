package schoollibrary.ui.editbook;

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
import schoollibrary.ui.viewbook.BookListController;

public class EditBookController implements Initializable {

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
    private JFXTextField pages;
    @FXML
    private JFXDatePicker r_date;
    @FXML
    private JFXTextField description;

    DatabaseHandler databaseHandler;
    BookListController bookListController;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void updateBook(ActionEvent event) {
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
        String query = "UPDATE BOOK SET BName = '" + bookName + "', "
                        + "author = '" + bookAuthor + "',"
                        + "publisher = '" + bookPublisher + "',"
                        + "price = " + bookPrice + ","
                        + "pages = " + bookPage + ","
                        + "receiveDate = '" + bookRecieveDate + "',"
                        + "description = '" + bookDescription + "' "
                        + "WHERE B_ID = '" + bookID +"' ";
        
        if(databaseHandler.execAction(query)){
            bookListController.loadData();
            AlertMaker.informatinAlert("Success","Update book Successfully"); 
        }else{
            AlertMaker.errorAlert("Failed","Update Failed");
        }
    }

    
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    
    public void inflateUI(BookListController.Book book,BookListController bookListController){
        String date = book.getR_date();
        LocalDate localDate = LocalDate.parse(date);
        b_id.setText(book.getB_id());
        b_id.setEditable(false);
        b_name.setText(book.getB_name());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        price.setText(book.getPrice());
        pages.setText(book.getPages());
        r_date.setValue(localDate);
        description.setText(book.getDescription());
        this.bookListController = bookListController;
    }
    

}
