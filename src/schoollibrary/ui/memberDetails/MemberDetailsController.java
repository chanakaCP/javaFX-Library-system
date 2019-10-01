
package schoollibrary.ui.memberDetails;

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
import schoollibrary.ui.viewmember.MemberListController;


public class MemberDetailsController implements Initializable {

    @FXML
    private Label id_c;  
    @FXML
    private Label name_c;   
    @FXML
    private Label validity_c; 
    @FXML
    private Label issueCount_c;  
    @FXML
    private Label subCount_c;   
    @FXML
    private Label renewCount_c;
    @FXML
    private Label fineCount_c;
    @FXML
    private Label dateCount_c;
    @FXML
    private Label aDate_c;
    
    
    DatabaseHandler databaseHandler;
    MemberListController memberListController;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }    
    
    
    public void viewData(String memberId){
        
        String query = "SELECT * FROM MEMBER WHERE M_ID = '" + memberId + "' " ;
        ResultSet result = databaseHandler.execQuery(query);
        try {
            if(result.next()){
                id_c.setText(memberId);
                name_c.setText(result.getString("MName"));
                name_c.setWrapText(true);
                aDate_c.setText(result.getString("addedDate"));
                issueCount_c.setText(result.getString("issueCount"));
                subCount_c.setText(result.getString("subCount"));
                renewCount_c.setText(result.getString("renewCount"));
                fineCount_c.setText(result.getString("finePayed"));  
                dateCount_c.setText(result.getString("delayedDateCount"));
                
                if(result.getString("isSubmit").equals("true")){
                    validity_c.setText("Valid Member");
                }else{
                    validity_c.setText("Invalid Member");
                }            
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
