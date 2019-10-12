
package schoollibrary.ui.report;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
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
   
    PieChart bookChart;
    PieChart memberChart;
    DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initGraph();   
        loadAllTimeData();
        loadTodayData();
    }    
    
  
    public void initGraph() {
        int count;
        bookChart = new PieChart(databaseHandler.getBookStatistic());
        bookInfoContainer.getChildren().add(bookChart);
        memberChart = new PieChart(databaseHandler.getMemberStatistic());
        memberInfoContainer.getChildren().add(memberChart); 
        
        count = countData("BOOK",false);
        bookChart.setTitle("Total Books  :  "+count);
        bookChart.setTitleSide(Side.TOP);
        bookChart.setLegendVisible(true);
        bookChart.setLabelsVisible(false);
        bookChart.setLegendSide(Side.BOTTOM); 
       
        count = countData("MEMBER",false);
        memberChart.setTitle("Total Members  :  "+count);
        memberChart.setTitleSide(Side.TOP);
        memberChart.setLegendVisible(true);
        memberChart.setLabelsVisible(false);
        memberChart.setLegendSide(Side.BOTTOM);
    }
    
    
    int countData(String table,boolean temp){      
        int countRow = 0;
        String query;
        try {
            if(temp){
                query = "SELECT COUNT(*) as count FROM "+ table + " WHERE isSubmit = 'true' ";
            }else{
                query = "SELECT COUNT(*) as count FROM "+ table + " ";
            }
            
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            countRow = result.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return countRow;  
    }
    
    
    int countTodayData(String table, String dateCol){      
        int countRow = 0;
        LocalDate date = LocalDate.now();

        try {
            String query;
            switch (dateCol) {
                case "lastRenewDate":
                    query = "SELECT COUNT(*) as count FROM "+ table + " WHERE DATE("+ dateCol +") = '"+ date +"' AND renewCount > 0 ";
                    break;
                case "submitDate":
                    query = "SELECT COUNT(*) as count FROM "+ table + " WHERE DATE("+ dateCol +") = '"+ date +"' AND isSubmit = 'true' ";
                    break;
                default:
                    query = "SELECT COUNT(*) as count FROM "+ table + " WHERE DATE("+ dateCol +") = '"+ date +"' ";
                    break;
            }
            
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            countRow = result.getInt("count");
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return countRow;  
    }
        

    public void loadAllTimeData(){
        int issueCount = countData("REPORT",false);
        int submitCount = countData("REPORT",true);
        int bookCount = countData("BOOK",false);
        int memberCount = countData("MEMBER",false);

        arBookIssue_c.setText(String.valueOf(issueCount));
        arBookSubmit_c.setText(String.valueOf(submitCount));
        arBookWillSubmit_c.setText(String.valueOf(issueCount - submitCount));
        arBook_c.setText(String.valueOf(bookCount));
        arMember_c.setText(String.valueOf(memberCount));
        
        try{
            String query  = "SELECT COUNT(*) as count FROM REPORT WHERE renewCount > 0 ";
            String query1 = "SELECT SUM(fine) FROM REPORT WHERE isLateSubmit = 'true' ";
            String query2 = "SELECT COUNT(*) as countLate FROM REPORT WHERE isLateSubmit = 'true' ";
            
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
//           ------ Cant load data error ------
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
  
    
    
    public void loadTodayData(){
        LocalDate date = LocalDate.now();
         
        int issueCount = countTodayData("REPORT","issueDate");
        int renewCount = countTodayData("REPORT","lastRenewDate");
        int submitCount = countTodayData("REPORT","submitDate");
        int bookCount = countTodayData("BOOK","addedDate");
        int memberCount = countTodayData("MEMBER","addedDate"); 
        
        trBookIssue_c.setText(String.valueOf(issueCount));
        trBookRenew_c.setText(String.valueOf(renewCount));
        trBookSubmit_c.setText(String.valueOf(submitCount));
        trNewBook_c.setText(String.valueOf(bookCount));
        trNewMember_c.setText(String.valueOf(memberCount));

        try{
            String query  = "SELECT COUNT(*) as count FROM REPORT WHERE DATE(willSubmit) = '"+ date +"' AND isSubmit = 'false' ";
            String query1 = "SELECT SUM(fine) FROM REPORT WHERE isLateSubmit = 'true' AND DATE(submitDate) = '"+ date +"' AND isSubmit = 'true' ";
            String query2 = "SELECT COUNT(*) as countLate FROM REPORT WHERE isLateSubmit = 'true' AND DATE(submitDate) = '"+ date +"' AND isSubmit = 'true' ";
            
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            trBookWillSubmit_c.setText(String.valueOf(result.getInt("count")));
            
            result = databaseHandler.execQuery(query1);
            result.next();
            trFine_c.setText(String.valueOf(result.getInt(1)));
       
            result = databaseHandler.execQuery(query2);
            result.next();
            trLateSubmit_c.setText(String.valueOf(result.getInt("countLate")));
        }catch (SQLException ex) {
//           ------ Cant load data error ------
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);

        }  
    }
    
    
    int countDays(String dateIssue){
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
        return dateCount;
    }
    
}
