
package schoollibrary.ui.viewbook;

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
import schoollibrary.ui.editbook.EditBookController;
import schoollibrary.ui.main.MainController;
import schoollibrary.util.LibraryAssistantUtil;

public class BookListController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private TableView<Book> tableViewCol;
    @FXML
    private TableColumn<Book,String> idCol;
    @FXML
    private TableColumn<Book,String> nameCol;
    @FXML
    private TableColumn<Book,String> authorCol;
    @FXML
    private TableColumn<Book,String> publisherCol;
    @FXML
    private TableColumn<Book,String> priceCol;
    @FXML
    private TableColumn<Book,String> pageCol;
    @FXML
    private TableColumn<Book,String> dateCol;
    @FXML
    private TableColumn<Book,String> descriptionCol;
    @FXML
    private TableColumn<Book,Boolean> availabilityCol;
    
    DatabaseHandler databaseHandler;
    MainController mainController;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceKey.getItems().add("Book ID");
        choiceKey.getItems().add("Book Name");
        choiceKey.getItems().add("Author");
        choiceKey.getItems().add("Publisher");
        choiceKey.getItems().add("Desription");
        
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(newValue.equals("Book ID") || newValue.equals("Book Name") || newValue.equals("Author") || newValue.equals("Publisher") || newValue.equals("Desription") ){
                searchKey.setDisable(false);
            }
        });
        initCol();
        loadData();
    }    

    
    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("b_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("b_name"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        pageCol.setCellValueFactory(new PropertyValueFactory<>("pages"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("r_date"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    
    public void loadData() {
        searchKey.setDisable(true);
        list.clear();
        databaseHandler = DatabaseHandler.getInstance();
        String query = "SELECT * FROM BOOK";

        ResultSet result = databaseHandler.execQuery(query);
     
        try {
            while (result.next()) {
                String bookID = result.getString("B_ID");
                String bookName = result.getString("BName");
                String bookAuth = result.getString("author");
                String bookPub = result.getString("publisher");
                String bookPr = result.getString("price");
                String bookPg = result.getString("pages");
                String bookRecDate = result.getString("receiveDate");
                String bookDes = result.getString("description");
                boolean bookAvail = result.getBoolean("isAvail");
                
                list.add(new Book(bookID,bookName,bookAuth,bookPub,bookPr,bookPg,bookRecDate,bookDes,bookAvail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }

    
    public void loadSearchData(String stream, String value){
        list.clear(); 
        String query;
        if(stream.equals("B_ID")){
            query = "SELECT * FROM BOOK WHERE " + stream + " = '"+value+"' ";
        }else{
            query = "SELECT * FROM BOOK WHERE " + stream + " LIKE '%"+value+"%' ";
        }
        System.out.println(query);
        ResultSet result = databaseHandler.execQuery(query);
        try {
            while (result.next()) { 
                String bookID = result.getString("B_ID");
                String bookName = result.getString("BName");
                String bookAuth = result.getString("author");
                String bookPub = result.getString("publisher");
                String bookPr = result.getString("price");
                String bookPg = result.getString("pages");
                String bookRecDate = result.getString("receiveDate");
                String bookDes = result.getString("description");
                boolean bookAvail = result.getBoolean("isAvail");
                
                list.add(new Book(bookID,bookName,bookAuth,bookPub,bookPr,bookPg,bookRecDate,bookDes,bookAvail));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
     
    
    @FXML
    private void bookDeleteAction(ActionEvent event) {
        Book selectedBook = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedBook == null){
            AlertMaker.errorAlert("No book selected","Please select a book for delete");
            return;
        }
        String bookId = selectedBook.getB_id();
        boolean avail = selectedBook.getAvailability();
        
        if(avail == false){
            AlertMaker.errorAlert("Can`t delete","This book has been ISSUED");
            return; 
        }
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Deleting Book","Are you sure you want to delete the book " + selectedBook.getB_name() + "?");
        if(responce.get() == ButtonType.OK){
            String query = "DELETE FROM BOOK WHERE B_ID = '" + bookId + "' ";
            if(databaseHandler.execAction(query)){
                AlertMaker.informatinAlert("Success", "The book  " + selectedBook.getB_name() + "  was deleted successfully");
                list.remove(selectedBook);
                mainController.refreshGraph();
            }else{
                AlertMaker.errorAlert("Failed", "The book  " + selectedBook.getB_name() + "  could not be deleted");
            }
        }else{
            AlertMaker.errorAlert("Canceled", "Deletion operation canceled");
        }
    }

    
    @FXML
    private void bookEditAction(ActionEvent event) {
        Book selectedBook = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedBook == null){
            AlertMaker.errorAlert("No book selected","Please select a book for edit");
            return;
        }
        boolean avail = selectedBook.getAvailability();
        
        if(avail == false){
            AlertMaker.errorAlert("Can`t edit","This book has been ISSUED");
            return; 
        }  
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/editbook/editBook.fxml"));
            Parent parent = loader.load();
            EditBookController controller = (EditBookController) loader.getController();
            controller.inflateUI(selectedBook,this);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setOnCloseRequest((e)->{
                loadData();
            });
            stage.setTitle("Edit Book");
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
            case "Book ID":
                loadSearchData("B_ID",searchVal);
                break;
            case "Book Name":
                loadSearchData("BName",searchVal);
                break;
            case "Author":
                loadSearchData("author",searchVal);
                break;
            case "Publisher":
                loadSearchData("publisher",searchVal);
                break;
            case "Desription":
                loadSearchData("description",searchVal);
                break;
        }    
    }

    
    @FXML
    private void cancel(ActionEvent event) {   
        if(searchKey.getText().isEmpty()){
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        }
        searchKey.setText("");
        loadData();
    }

    
    public void getController(MainController mainController){  
        this.mainController = mainController;
    }
 
    
    public static class Book{
        
        private final SimpleStringProperty b_id;
        private final SimpleStringProperty b_name;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty price;
        private final SimpleStringProperty pages;
        private final SimpleStringProperty r_date;
        private final SimpleStringProperty description;
        private final SimpleBooleanProperty availability;

        public Book(String id, String name, String auth, String pub, String pr, String pg, String date, String des, boolean avail){
            this.b_id = new SimpleStringProperty(id);
            this.b_name = new SimpleStringProperty(name);
            this.author = new SimpleStringProperty(auth);
            this.publisher = new SimpleStringProperty(pub);
            this.price = new SimpleStringProperty(pr);
            this.pages = new SimpleStringProperty(pg);
            this.r_date = new SimpleStringProperty(date);
            this.description = new SimpleStringProperty(des);
            this.availability = new SimpleBooleanProperty(avail);
        }

        public String getB_id() {
            return b_id.get();
        }
        public String getB_name() {
            return b_name.get();
        }
        public String getAuthor() {
            return author.get();
        }
        public String getPublisher() {
            return publisher.get();
        }
        public String getPrice() {
            return price.get();
        }
        public String getPages() {
            return pages.get();
        }
        public String getR_date() {
            return r_date.get();
        }     
        public String getDescription() {
            return description.get();
        }        
        public boolean getAvailability() {
            return availability.get();
        }
    }
  
    
    
}