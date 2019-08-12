
package schoollibrary.ui.stat;

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
import javafx.scene.layout.VBox;
import schoollibrary.database.DatabaseHandler;


public class StatController implements Initializable {

    @FXML
    private VBox bookInfoContainer;
    @FXML
    private VBox memberInfoContainer;

    PieChart bookChart;
    PieChart memberChart;
    DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        initGraph();          
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
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return countRow;  
    }
        

//    no of book is to be submit today
//    no of boos late to submit

//    no of book issued in a week
//    chart for book issued in a week

//    no of book submit in a week
//    chart for book submit in a week

//    no of book issued in a month
//    chart for book issued in a month

//    no of book submit in a month
//    chart for book submit in a month
 
    
//    total fine collect
//    book issued in this week
//    
    
}
