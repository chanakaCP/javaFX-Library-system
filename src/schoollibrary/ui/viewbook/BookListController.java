
package schoollibrary.ui.viewbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
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
import schoollibrary.ui.bookDetails.BookDetailsController;
import schoollibrary.ui.editbook.EditBookController;
import schoollibrary.ui.main.MainController;
import schoollibrary.util.LibraryAssistantUtil;

public class BookListController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();
    
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXComboBox<String> choiceKey;
    @FXML
    private JFXTextField searchKey;
    @FXML
    private JFXDatePicker datePick;
    @FXML
    private TableView<Book> tableViewCol;
    @FXML
    private TableColumn<Book,Integer> nuCol;
    @FXML
    private TableColumn<Book,String> idCol;
    @FXML
    private TableColumn<Book,String> nameCol;
    @FXML
    private TableColumn<Book,String> authorCol;
    @FXML
    private TableColumn<Book,String> publisherCol;
    @FXML
    private TableColumn<Book,String> rDateCol;
    @FXML
    private TableColumn<Book,String> aDateCol;
    @FXML
    private TableColumn<Book,String> availabilityCol;
    
    DatabaseHandler databaseHandler;
    MainController mainController;

           
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initDropdown();
        searchKey.setDisable(true);
        datePick.setDisable(true);
               
        choiceKey.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            searchKey.setText("");
            datePick.setValue(null);
            if(newValue.equals("Book ID") || newValue.equals("Book Name") || newValue.equals("Author") || newValue.equals("Publisher") ){
                searchKey.setDisable(false);
                datePick.setDisable(true);
            }else if(newValue.equals("Recieved Date") || newValue.equals("Added Date")){
                searchKey.setDisable(true);
                datePick.setDisable(false);
            }
        });
        initCol();
        loadData();
    }    

    
    @FXML
    private void loadAddBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/addbook/add_book.fxml"));
        loadWindow("Add Book", loader);
        AddBookController controller = (AddBookController) loader.getController();
        controller.getControllerFromBookList(this);  
    }
    
    @FXML
    private void bookDeleteAction(ActionEvent event) {
        Book selectedBook = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedBook == null){
            AlertMaker.errorAlert("No book selected","Please select a book for delete");
            return;
        }
        String bookId = selectedBook.getB_id();
        String avail = selectedBook.getAvailability();
        
        if(avail.equals("false")){
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
        String avail = selectedBook.getAvailability();
        
        if(avail.equals("false")){
            AlertMaker.errorAlert("Can`t edit","This book has been ISSUED");
            return; 
        }  
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/editbook/editBook.fxml"));
            Parent parent = loader.load();
            EditBookController controller = (EditBookController) loader.getController();
            controller.inflateUI(selectedBook,this);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setOnCloseRequest((e)->{
                loadData();
            });
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
           
        } catch (IOException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    
    @FXML
    public void bookDetailsAction(ActionEvent event){
        Book selectedBook = tableViewCol.getSelectionModel().getSelectedItem();
        
        if(selectedBook == null){
            AlertMaker.errorAlert("No book selected","Please select a book for edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/bookDetails/book_details.fxml"));
            Parent parent = loader.load();
            BookDetailsController controller = (BookDetailsController) loader.getController();
            controller.viewData(selectedBook.getB_id());
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setOnCloseRequest((e)->{
                loadData();
            });
            stage.setTitle("View Book");
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
        LocalDate searchDate = datePick.getValue();
        
        if(choice == null){
            AlertMaker.errorAlert("Can`t search","Please select a field for search");
            return;
        } 
        if(datePick.isDisable() && searchVal.isEmpty()){
            AlertMaker.errorAlert("Can`t search","Please enter value for search");
            return;
        }
        if(searchKey.isDisable() && searchDate == null){
            AlertMaker.errorAlert("Can`t search","Please enter date for search");
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
            case "Recieved Date":
                loadSearchDate("receivedDate",searchDate);
                break;
            case "Added Date":
                loadSearchDate("addedDate",searchDate);
                break;
        }    
    }

    
    @FXML
    private void cancel(ActionEvent event) {   
        if(choiceKey.getValue() == null ||  (datePick.getValue() == null && searchKey.getText().equals("")) ){  
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
            return;
        }
        datePick.setValue(null);
        searchKey.setText("");    
        loadData();  
    }

     
    public void initCol() {

        nuCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("b_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("b_name"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        rDateCol.setCellValueFactory(new PropertyValueFactory<>("r_date"));
        aDateCol.setCellValueFactory(new PropertyValueFactory<>("a_date"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    
    public void loadData() {   
        list.clear();
        cancelButton.setText("Close");
        String query = "SELECT * FROM BOOK";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String bookID = result.getString("B_ID");
                String bookName = result.getString("BName");
                String category = result.getString("category");
                String bookAuth = result.getString("author");
                String bookPub = result.getString("publisher");
                String bookPr = result.getString("price");
                String bookPg = result.getString("pages");
                String bookRecDate = result.getString("receivedDate");
                String bookAddDate = result.getString("addedDate");
                String bookDes = result.getString("description");
                String bookAvail;
                if(result.getBoolean("isAvail")){
                    bookAvail = "Available";
                }else{
                    bookAvail = "Not Available";
                }
                
                list.add(new Book(i,bookID,bookName,category,bookAuth,bookPub,bookPr,bookPg,bookRecDate,bookAddDate,bookDes,bookAvail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }

    
    public void loadSearchData(String stream, String value){
        cancelButton.setText("Cancel");
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        list.clear(); 
        String query = "SELECT * FROM BOOK WHERE " + stream + " LIKE '%"+value+"%' ";     
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String bookID = result.getString("B_ID");
                String bookName = result.getString("BName");
                String category = result.getString("category");
                String bookAuth = result.getString("author");
                String bookPub = result.getString("publisher");
                String bookPr = result.getString("price");
                String bookPg = result.getString("pages");
                String bookRecDate = result.getString("receivedDate");
                String bookAddDate = result.getString("addedDate");
                String bookDes = result.getString("description");
                String bookAvail;
                if(result.getBoolean("isAvail")){
                    bookAvail = "Available";
                }else{
                    bookAvail = "Not Available";
                }
                
                list.add(new Book(i,bookID,bookName,category,bookAuth,bookPub,bookPr,bookPg,bookRecDate,bookAddDate,bookDes,bookAvail));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
     
    
    public void loadSearchDate(String stream, LocalDate value){
        cancelButton.setText("Cancel");
        if(searchKey.isDisable()){
            searchKey.setDisable(true);
            datePick.setDisable(false);
        }else{
            searchKey.setDisable(false);
            datePick.setDisable(true);
        }
        
        list.clear();  
        String query = "SELECT * FROM BOOK WHERE DATE("+stream+") = '"+value+"' ";
        ResultSet result = databaseHandler.execQuery(query);
        int i=0;
        try {
            while (result.next()) {
                i++;
                String bookID = result.getString("B_ID");
                String bookName = result.getString("BName");
                String category = result.getString("category");
                String bookAuth = result.getString("author");
                String bookPub = result.getString("publisher");
                String bookPr = result.getString("price");
                String bookPg = result.getString("pages");
                String bookRecDate = result.getString("receivedDate");
                String bookAddDate = result.getString("addedDate");
                String bookDes = result.getString("description");
                String bookAvail;
                if(result.getBoolean("isAvail")){
                    bookAvail = "Available";
                }else{
                    bookAvail = "Not Available";
                }
                
                list.add(new Book(i,bookID,bookName,category,bookAuth,bookPub,bookPr,bookPg,bookRecDate,bookAddDate,bookDes,bookAvail));
            }
        } catch (SQLException ex) {           
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableViewCol.setItems(list);
    }
    
    void loadWindow(String title, FXMLLoader loader){
        try {
            Parent parent = (Parent) loader.load(); 
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getController(MainController mainController){  
        this.mainController = mainController;
    }
 
    
    private void initDropdown() {
        choiceKey.getItems().add("Book ID");
        choiceKey.getItems().add("Book Name");
        choiceKey.getItems().add("Author");
        choiceKey.getItems().add("Publisher");
        choiceKey.getItems().add("Recieved Date");
        choiceKey.getItems().add("Added Date");
    }

    
    public static class Book{
        
        private final SimpleIntegerProperty number;
        private final SimpleStringProperty b_id;
        private final SimpleStringProperty b_name;
        private final SimpleStringProperty category;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty price;
        private final SimpleStringProperty pages;
        private final SimpleStringProperty r_date;
        private final SimpleStringProperty a_date;
        private final SimpleStringProperty description;
        private final SimpleStringProperty availability;

        public Book(int no, String id, String name, String cat, String auth, String pub, String pr, String pg, String recDate, String addDate, String des, String avail){
            this.number = new SimpleIntegerProperty(no);
            this.b_id = new SimpleStringProperty(id);
            this.b_name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(cat);
            this.author = new SimpleStringProperty(auth);
            this.publisher = new SimpleStringProperty(pub);
            this.price = new SimpleStringProperty(pr);
            this.pages = new SimpleStringProperty(pg);
            this.r_date = new SimpleStringProperty(recDate);
            this.a_date = new SimpleStringProperty(addDate);
            this.description = new SimpleStringProperty(des);
            this.availability = new SimpleStringProperty(avail);
        }

        public Integer getNumber() {
            return number.get();
        }
        public String getB_id() {
            return b_id.get();
        }
        public String getB_name() {
            return b_name.get();
        }
        public String getCategory() {
            return category.get();
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
        public String getA_date() {
            return a_date.get();
        }
        public String getDescription() {
            return description.get();
        }        
        public String getAvailability() {
            return availability.get();
        }
    }
  
    
    
}
