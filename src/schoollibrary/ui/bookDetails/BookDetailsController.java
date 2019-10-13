
package schoollibrary.ui.bookDetails;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.viewbook.BookListController;


public class BookDetailsController implements Initializable {

    @FXML
    private Label id_c;
    @FXML
    private Label name_c;
    @FXML
    private Label cat_c;
    @FXML
    private Label author_c;
    @FXML
    private Label publisher_c;
    @FXML
    private Label price_c;
    @FXML
    private Label pages_c;
    @FXML
    private Label date_c;
    @FXML
    private Label aDate_c;
    @FXML
    private Label avail_c;
    @FXML
    private Label description_c;
    @FXML
    private Label issueCount_c;
    @FXML
    private Label subCount_c;
    @FXML
    private Label renewCount_c;
    @FXML
    private Label fineCount_c;
    
    
    DatabaseHandler databaseHandler;
    BookListController bookListController;   
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    
    
    public void viewData(String bookId){
        String query = "SELECT * FROM BOOK WHERE B_ID = '" + bookId + "' " ;
        ResultSet result = databaseHandler.execQuery(query);
        try {
            if(result.next()){
                id_c.setText(bookId);
                name_c.setText(result.getString("BName"));
                name_c.setWrapText(true);    
                cat_c.setText(result.getString("category"));
                cat_c.setWrapText(true);
                author_c.setText(result.getString("author"));
                author_c.setWrapText(true);
                publisher_c.setText(result.getString("publisher"));
                publisher_c.setWrapText(true);
                price_c.setText(result.getString("price"));
                pages_c.setText(result.getString("pages"));
                date_c.setText(result.getString("receivedDate"));
                aDate_c.setText(result.getString("addedDate"));
                description_c.setText(result.getString("description"));
                description_c.setWrapText(true);
                issueCount_c.setText(result.getString("issueCount"));
                subCount_c.setText(result.getString("subCount"));
                renewCount_c.setText(result.getString("renewCount"));
                fineCount_c.setText(result.getString("fineCollect")); 
                
                if(result.getString("isAvail").equals("true")){
                    avail_c.setText("Available");                    
                }else{
                    avail_c.setText("Not available");
                }                
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
