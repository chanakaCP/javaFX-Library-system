
package schoollibrary.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import schoollibrary.alert.AlertMaker;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.ui.addbook.AddBookController;
import schoollibrary.ui.addmember.AddMemberController;
import schoollibrary.ui.setting.Preferences;
import schoollibrary.ui.viewbook.BookListController;
import schoollibrary.ui.viewmember.MemberListController;
import schoollibrary.util.LibraryAssistantUtil;

public class MainController implements Initializable {
    
    @FXML
    private JFXTextField bookIdInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookStatus;
    @FXML
    private JFXTextField memberIdInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberStatus;
    @FXML
    private JFXButton issueBtn; 
    @FXML
    private JFXTextField bookIdInput2;  
    @FXML
    private VBox subDetals1;
    @FXML
    private VBox subDetals3;
    @FXML
    private Text book_name;
    @FXML
    private Text description;
    @FXML
    private Text member_id;
    @FXML
    private Text member_name;
    @FXML
    private Text issue_date;
    @FXML
    private Text renew_count;       
    @FXML
    private Text total_fine;
    @FXML
    private StackPane rootPane; 
    @FXML
    private Text subLale2;
    @FXML
    private VBox bookInfoContainer;
    @FXML
    private VBox memberInfoContainer;
    @FXML
    
    private Tab issueTab;
    PieChart bookChart;
    PieChart memberChart;
    private boolean btnTag1 = true;
    private boolean btnTag2 = true;

    DatabaseHandler databaseHandler;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        
        initGraph();          
    }    
    
     
    @FXML
    private void loadAddMember(ActionEvent event) {          
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/addmember/add_member.fxml"));           
        loadWindow("Add Member", loader);
        AddMemberController controller = (AddMemberController) loader.getController();
        controller.getController(this); 
    }
    @FXML
    private void loadAddBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/addbook/add_book.fxml"));
        loadWindow("Add Book", loader);
        AddBookController controller = (AddBookController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadViewMember(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/viewmember/member_list.fxml"));
        loadWindow("Members", loader);
        MemberListController controller = (MemberListController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadViewBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/viewbook/book_list.fxml"));
        loadWindow("Books", loader);
        BookListController controller = (BookListController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadIssuedBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/issueBook/issueBook.fxml"));
        loadWindow("Currently issued Books", loader);
    }
    @FXML
    private void loadHistory(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/submission/submission.fxml"));
        loadWindow("History", loader);
    }
    @FXML
    private void loadTimetable(ActionEvent event) {
    }
    @FXML
    private void loadSetting(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/setting/setting.fxml"));
        loadWindow("Setting", loader);
    } 
    @FXML
    private void menuClose(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void menuAddBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/addbook/add_book.fxml"));
        loadWindow("Add Book", loader);
        AddBookController controller = (AddBookController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void menuAddMember(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/addmember/add_member.fxml"));           
        loadWindow("Add Member", loader);
        AddMemberController controller = (AddMemberController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void menuViewBook(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/viewbook/book_list.fxml"));
        loadWindow("Books", loader);
        BookListController controller = (BookListController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void menuViewMember(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/viewmember/member_list.fxml"));
        loadWindow("Members", loader);
        MemberListController controller = (MemberListController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void menuFullScreen(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
    
    void clearBookCache(){
        bookName.setText("");
        bookStatus.setText("");
    }
    
    void clearMemberCache(){
        memberName.setText("");         
    }
    
    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        if(btnTag2){
            issueBtn.setDisable(false);
        }
        String id = bookIdInput.getText().trim();
        if(id.isEmpty()){
            bookName.setText("Book Name");
            bookStatus.setText("Availability");
        }else{
            String query = "SELECT BName,isAvail FROM BOOK WHERE B_ID = '"+ id +"' ";
            ResultSet result = databaseHandler.execQuery(query);
            boolean flag = false;

            try {
                while (result.next()){
                    String bName = result.getString("BName");
                    boolean bStatus = result.getBoolean("isAvail");
                    flag = true; 

                    if(bStatus){
                        bookStatus.setText("Available");
                        if(btnTag2){
                            issueBtn.setDisable(false);    
                        }
                        btnTag1 = true;
                    }else{
                        bookStatus.setText("Not available");
                        issueBtn.setDisable(true);
                        btnTag1 = false;
                    }
                    bookName.setText(bName);
                             
                }
                if(!flag){
                    bookName.setText("No such book available for this ID");
                    bookStatus.setText("");
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    
    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        if(btnTag1){
            issueBtn.setDisable(false);        
        }
        String id = memberIdInput.getText().trim();
        if(id.isEmpty()){
            memberName.setText("Member Name ");
            memberStatus.setText("Validity");
        }else{
            String query = "SELECT MName,isSubmit FROM MEMBER WHERE M_ID = '" + id + "' ";
            ResultSet result = databaseHandler.execQuery(query);
            boolean flag = false;

            try {
                while (result.next()){
                    String mName = result.getString("MName");
                    boolean mStatus = result.getBoolean("isSubmit");
                    flag = true; 
                    
                    if(mStatus){
                        memberStatus.setText("Valid member"); 
                        if(btnTag1){
                            issueBtn.setDisable(false);
                        }
                        btnTag2 = true;
                    }else{
                        memberStatus.setText("Invalid member"); 
                        issueBtn.setDisable(true);
                        btnTag2 = false;
                    }
                    memberName.setText(mName);    
                }
                if(!flag){
                    memberName.setText("No such member available for this ID");
                    memberStatus.setText("");  
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @FXML
    private void issueBookOperation(ActionEvent event) {
        String bookId = bookIdInput.getText().trim();
        String memberId = memberIdInput.getText().trim(); 
        
        try{
            String query = "SELECT COUNT(B_ID) as count FROM BOOK WHERE B_ID = '" + bookId + "' ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            int count1 = result.getInt("count");     
            if(count1 == 0){
                AlertMaker.errorAlert("Invalid book ID ","No such a book for book ID " + bookId );
                return;
            }
            String query1 = "SELECT COUNT(M_ID) as count FROM MEMBER WHERE M_ID = '" + memberId + "' ";
            ResultSet result2 = databaseHandler.execQuery(query1);
            result2.next();
            int count2 = result2.getInt("count");     
            if(count2 == 0){
                AlertMaker.errorAlert("Invalid member ID ","No such a member for member ID " + memberId );
                return;
            }
        }catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } 
        
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Issue Operation","Are you sure want to issue the book ? ");
        
        if(responce.get() == ButtonType.OK){     
            String query2 = "SELECT isAvail FROM BOOK WHERE B_ID = '"+ bookId +"' ";
            String query3 = "SELECT isSubmit FROM MEMBER WHERE M_ID = '"+ memberId +"' ";
            ResultSet result2 = databaseHandler.execQuery(query2);
            boolean status;
            try {
                if (result2.next()){
                    status = result2.getBoolean("isAvail");  
                    if(!status){
                        AlertMaker.errorAlert("Can`t issue","This book has alredy issued");
                        return;
                    }
                } 
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
            result2 = databaseHandler.execQuery(query3);
            
            try {
                if (result2.next()){
                    status = result2.getBoolean("isSubmit");  
                    if(!status){
                        AlertMaker.errorAlert("Can`t issue","This member has alredy received a book");
                        return;
                    }
                } 
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }     
            
            String query4 = "INSERT INTO ISSUE (bookID,memberID) VALUES ( " +
                    "'" + bookId + "'," +
                    "'" + memberId + "'" +
                    ")";
            String query5 = "UPDATE BOOK SET isAvail = false WHERE B_ID = '" + bookId + "' ";
            String query6 = "UPDATE MEMBER SET isSubmit = false WHERE M_ID = '" + memberId + "' ";                
            if(databaseHandler.execAction(query4) && databaseHandler.execAction(query5) && databaseHandler.execAction(query6)){
                AlertMaker.informatinAlert("Success","Book issue complete");
                bookIdInput.setText("");
                memberIdInput.setText("");
                bookName.setText("Book Name");
                bookStatus.setText("Availability");
                memberName.setText("Member Name ");
                memberStatus.setText("Validity");
                refreshGraph();
            }else{
                AlertMaker.errorAlert("Failed","Issue operation failed");
            }
            
        }else{
            AlertMaker.informatinAlert("Canceled", "Issue operation canceled");
        }
     }

    
    @FXML
    private void loadBookInfo2(ActionEvent event) {
        String bookId = bookIdInput2.getText().trim();
        if(bookId.isEmpty()){
            subDetals1.setVisible(true);
            subDetals3.setVisible(true);
            setSubmssionContent();
        }
        else{
            String query1 = "SELECT * FROM ISSUE WHERE bookID = '" + bookId + "' ";
            ResultSet result = databaseHandler.execQuery(query1);
            try {
                if(result.next()){
                    subDetals1.setVisible(true);
                    subDetals3.setVisible(true);
                    subLale2.setText("Member");
                    
                    String memberId = result.getString("memberID");  
                    int totalFine = countTotalFine(result.getString("lastRenewDate"));
                    issue_date.setText("Date  :  "+result.getString("issueDate"));
                    renew_count.setText("Renew count  :  "+result.getString("renewCount"));
                    total_fine.setText("Total fine  :  Rs  "+totalFine);
                            
                    String query2 = "SELECT BName,description FROM BOOK WHERE B_ID = '" + bookId + "' ";
                    ResultSet result2 = databaseHandler.execQuery(query2);

                    if(result2.next()){
                        book_name.setText("Name  :  "+result2.getString("BName"));
                        description.setText("Description  :  "+result2.getString("description"));
                    }

                    query2 = "SELECT M_ID,MName FROM MEMBER WHERE M_ID = '" + memberId + "' ";
                    result2 = databaseHandler.execQuery(query2);
                    if(result2.next()){ 
                        member_id.setText("ID  :  "+result2.getString("M_ID"));
                        member_name.setText("Name  :  "+result2.getString("MName"));
                    }
                }else{
                    subDetals1.setVisible(false);
                    subDetals3.setVisible(false);
                    member_id.setText("No such issued book for this ID");
                    member_name.setText("");
                    subLale2.setText("");
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }     
    } 

    
    @FXML
    private void submitBookOperation(ActionEvent event) {
        String bookId = bookIdInput2.getText().trim();
        String memberId;
        String issueDate;
        int renew_Count;
        int totalFine;
   
        try{
            String query = "SELECT COUNT(bookID) as count FROM ISSUE WHERE bookID = '" + bookId + "' ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            int count1 = result.getInt("count");     
            if(count1 == 0){
                AlertMaker.errorAlert("Invalid book ID","This book is not issued" );
                return;
            }
        }catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        String query = "SELECT * FROM ISSUE WHERE bookID = '" + bookId + "' ";
        ResultSet result = databaseHandler.execQuery(query);  
        try {
            if (result.next()) {
                issueDate = result.getString("issueDate");
                renew_Count = result.getInt("renewCount");
                memberId = result.getString("memberID");
                String dateFrom = result.getString("lastRenewDate");
                totalFine = countTotalFine(dateFrom);
                
                Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Submission Operation","Are you sure want to submit the book");
                if(responce.get() == ButtonType.OK){
                    String query1 = "INSERT INTO SUBMISSION (bookID,memberID,issueDate,fine,renewCount) VALUES ( " +
                                    "'" + bookId + "'," +
                                    "'" + memberId + "'," +
                                    "'" + issueDate + "'," +
                                    totalFine +", " +
                                    renew_Count  +
                                    ")";
                    String query2 = "DELETE FROM ISSUE WHERE bookID = '" + bookId + "'";
                    String query3 = "UPDATE BOOK SET isAvail = true WHERE B_ID = '" + bookId + "' ";
                    String query4 = "UPDATE MEMBER SET isSubmit = true WHERE M_ID = '" + memberId + "' ";
                    if(databaseHandler.execAction(query1) && databaseHandler.execAction(query2) && databaseHandler.execAction(query3) && databaseHandler.execAction(query4)){
                        AlertMaker.informatinAlert("Success","Book submission complete");
                        bookIdInput2.setText("");
                        setSubmssionContent();
                        refreshGraph();
                    }else{
                        AlertMaker.errorAlert("Failed","Submission operation failed");
                    }
                }else{
                    AlertMaker.informatinAlert("Canceled","Submission operation canceled");   
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @FXML
    private void renewBookOperation(ActionEvent event) {
        String bookId = bookIdInput2.getText().trim();
        try{
            String query = "SELECT COUNT(bookID) as count FROM ISSUE WHERE bookID = '" + bookId + "' ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            int count1 = result.getInt("count");     
            if(count1 == 0){
                AlertMaker.errorAlert("Invalid book ID","This book is not issued" );
                return;
            }
        }catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Renew Operation", "Are you sure want to renew the book " + bookName.getText() + "? ");
        
        if(responce.get() == ButtonType.OK){
            LocalDate renewDate = LocalDate.now();
            String query = "UPDATE ISSUE SET renewCount = renewCount+1, lastRenewDate = '"+renewDate+"'  WHERE bookID = '" + bookId + "' "; 
            
            if(databaseHandler.execAction(query)){
                AlertMaker.informatinAlert("Success","Book has been renewed"); 
                bookIdInput2.setText("");
                setSubmssionContent();
            }else{
                AlertMaker.errorAlert("Failed","Renew has been failed");
            }
        }else{
            AlertMaker.informatinAlert("Canceled","Renew operation canceled");   
        }
    }

    
    public void setSubmssionContent(){
        issue_date.setText("Issue Date");
        renew_count.setText("Renew Count");  
        book_name.setText("Book Name");
        description.setText("Description");
        member_id.setText("Member ID");
        member_name.setText("Name");
        subLale2.setText("Member");
    } 

    
    public void initGraph() {
        bookChart = new PieChart(databaseHandler.getBookStatistic());
        bookInfoContainer.getChildren().add(bookChart);
        memberChart = new PieChart(databaseHandler.getMemberStatistic());
        memberInfoContainer.getChildren().add(memberChart);
        bookChart.setLegendVisible(true);
        bookChart.setLabelsVisible(false);
        bookChart.setLegendSide(Side.BOTTOM);  
        memberChart.setLegendVisible(true);
        memberChart.setLabelsVisible(false);
        memberChart.setLegendSide(Side.BOTTOM );
        issueTab.setOnSelectionChanged((Event event) -> {
            if(issueTab.isSelected())
                refreshGraph();
        });
    }
    
    
    public void refreshGraph(){
        bookChart.setData(databaseHandler.getBookStatistic());
        memberChart.setData(databaseHandler.getMemberStatistic());
    }
   
    int countTotalFine(String dateIssue){
        Preferences preferences = Preferences.getPreferences();
        int finePerDay = preferences.getFinePerDay();
        int maxNoOfDays = preferences.getnOfDays();
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
        System.out.println(dateCount);
        if(dateCount > maxNoOfDays){
            return dateCount*finePerDay;
        }else{
            return dateCount*0;
        }
    }
    
}
