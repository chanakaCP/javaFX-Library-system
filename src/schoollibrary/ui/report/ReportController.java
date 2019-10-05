
package schoollibrary.ui.report;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import schoollibrary.database.DatabaseHandler;


public class ReportController implements Initializable {

    @FXML
    private VBox bookInfoContainer;
    @FXML
    private VBox memberInfoContainer;

    PieChart bookChart;
    PieChart memberChart;
    DatabaseHandler databaseHandler;
  
    @FXML
    private Label trBookIssue_c;
    @FXML
    private Label trBookRenew_c;
    @FXML
    private Label trBookSubmit_c;
    @FXML
    private Label trBookWillSubmit_c;
    @FXML
    private Label trLateSubmit_c;
    @FXML
    private Label trFine_c;
    @FXML
    private Label trNewBook_c;
    @FXML
    private Label trNewMember_c;
    @FXML
    private Label arBookIssue_c;
    @FXML
    private Label arBookRenew_c;
    @FXML
    private Label arBookSubmit_c;
    @FXML
    private Label arBookWillSubmit_c;
    @FXML
    private Label arLateSubmit_c;
    @FXML
    private Label arFine_c;
    @FXML
    private Label arBook_c;
    @FXML
    private Label arMember_c;
   
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initGraph();   
        loadAllTimeData();
    }    
    
  
    public void initGraph() {
        int count;
        bookChart = new PieChart(databaseHandler.getBookStatistic());
        bookInfoContainer.getChildren().add(bookChart);
        memberChart = new PieChart(databaseHandler.getMemberStatistic());
        memberInfoContainer.getChildren().add(memberChart); 
        
        count = countData("BOOK");
        bookChart.setTitle("Total Books  :  "+count);
        bookChart.setTitleSide(Side.TOP);
        bookChart.setLegendVisible(true);
        bookChart.setLabelsVisible(false);
        bookChart.setLegendSide(Side.BOTTOM); 
       
        count = countData("MEMBER");
        memberChart.setTitle("Total Members  :  "+count);
        memberChart.setTitleSide(Side.TOP);
        memberChart.setLegendVisible(true);
        memberChart.setLabelsVisible(false);
        memberChart.setLegendSide(Side.BOTTOM);
    }
    
    
    int countData(String table){      
        int countRow = 0;
        try {
            String query = "SELECT COUNT(*) as count FROM "+ table + " ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            countRow = result.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return countRow;  
    }
        

    public void loadAllTimeData(){
        int issueCount = countData("ISSUE");
        int submitCount = countData("SUBMISSION");
        int bookCount = countData("BOOK");
        int memberCount = countData("MEMBER");

        arBookIssue_c.setText(String.valueOf(issueCount + submitCount));
        arBookSubmit_c.setText(String.valueOf(submitCount));
        arBookWillSubmit_c.setText(String.valueOf(issueCount));
        arBook_c.setText(String.valueOf(bookCount));
        arMember_c.setText(String.valueOf(memberCount));
        
        try{
            String query = "SELECT COUNT(*) as count FROM SUBMISSION WHERE renewCount > 0 ";
            String query1 = "SELECT SUM(fine) FROM SUBMISSION WHERE isLateSubmit = 'true' ";
            String query2 = "SELECT COUNT(*) as countLate FROM SUBMISSION WHERE isLateSubmit = 'true' ";
            
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            arBookRenew_c.setText(String.valueOf(result.getInt("count")));
            
            result = databaseHandler.execQuery(query1);
            result.next();
            arFine_c.setText(String.valueOf(result.getInt(1)));
       
            result = databaseHandler.execQuery(query2);
            result.next();
            arLateSubmit_c.setText(String.valueOf(result.getInt("countLate")));
        }catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }     
    }
  
    
    
    
}
