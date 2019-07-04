
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
    private Label avail_c;
    @FXML
    private Label description_c;
    
     DatabaseHandler databaseHandler;
    BookListController bookListController;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    
    
    public void getId(String bookId){
       String query = "SELECT * FROM BOOK WHERE B_ID = '" + bookId + "' " ;
        ResultSet result = databaseHandler.execQuery(query);
        try {
            if(result.next()){
                id_c.setText(bookId);
                name_c.setText(result.getString("BName"));
                author_c.setText(result.getString("author"));
                publisher_c.setText(result.getString("publisher"));
                price_c.setText(result.getString("price"));
                pages_c.setText(result.getString("pages"));
                date_c.setText(result.getString("receiveDate"));
                description_c.setText(result.getString("description"));
                avail_c.setText(result.getString("isAvail"));
                
                name_c.setWrapText(true);
                author_c.setWrapText(true);
                description_c.setWrapText(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
