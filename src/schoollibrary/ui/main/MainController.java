
package schoollibrary.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    private Text date_delayed;
    @FXML
    private Text total_fine;
    @FXML
    private StackPane rootPane;
//    @FXML
//    private BorderPane borderPane;
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
    Preferences preferences;  
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox menubar;
    @FXML
    private JFXButton menuBtn;
    @FXML
    private JFXTabPane tabbar;
    @FXML
    private HBox info;
    @FXML
    private VBox issue_btn;
    @FXML
    private Tab submissionTab;
    @FXML
    private HBox renew_btn;
    @FXML
    private JFXButton renewBtn;
    @FXML
    private JFXButton subBtn;
    @FXML
    private Text subLale1;
    @FXML
    private VBox subDetals2;
    @FXML
    private Text subLale3;
    
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        preferences = Preferences.getPreferences();        
        initGraph();          
//        testData();
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
    private void loadSetting(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/setting/setting.fxml"));
        loadWindow("Setting", loader);
    } 
    @FXML
    private void loadReport(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/report/report.fxml"));
        loadWindow("Report", loader);
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
            String query5 = "UPDATE BOOK SET isAvail = false , issueCount = issueCount+1   WHERE B_ID = '" + bookId + "' ";
            String query6 = "UPDATE MEMBER SET isSubmit = false , issueCount = issueCount+1  WHERE M_ID = '" + memberId + "' ";                
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
                    int dateDelayed = delayedDates(result.getString("lastRenewDate"));
                    issue_date.setText("Date  :  "+result.getString("issueDate"));
                    renew_count.setText("Renew count  :  "+result.getString("renewCount"));
                    date_delayed.setText("Date delayed  :  "+dateDelayed);
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
        int fine;
   
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
                fine = countTotalFine(dateFrom);
                
                Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Submission Operation","Are you sure want to submit the book");
                if(responce.get() == ButtonType.OK){
                    int delayedDateCount = delayedDates(dateFrom);
                    int dateCount = countDays(dateFrom);
                    String query1 = "INSERT INTO SUBMISSION (bookID,memberID,issueDate,fine,renewCount,nuOfDaysKept) VALUES ( " +
                                    "'" + bookId + "'," +
                                    "'" + memberId + "'," +
                                    "'" + issueDate + "'," +
                                    fine + ", " +
                                    renew_Count+ ", "  +
                                    dateCount  +
                                    ")";
                    String query2 = "DELETE FROM ISSUE WHERE bookID = '" + bookId + "'";
                    String query3 = "UPDATE BOOK SET isAvail = true, subCount = subCount+1, fineCollect = fineCollect +'" + fine + "'  WHERE B_ID = '" + bookId + "' ";
                    String query4 = "UPDATE MEMBER SET isSubmit = true, subCount = subCount+1, finePayed = finePayed +'" + fine + "' , delayedDateCount = delayedDateCount +'" + delayedDateCount + "'  WHERE M_ID = '" + memberId + "' ";
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
        String memberId = null;
        try{
            String query = "SELECT COUNT(bookID) as count FROM ISSUE WHERE bookID = '" + bookId + "' ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            int count1 = result.getInt("count");     
            if(count1 == 0){
                AlertMaker.errorAlert("Invalid book ID","This book is not issued" );
                return;
            }
            
            query = "SELECT memberID FROM ISSUE WHERE bookID = '" + bookId + "' ";
            result = databaseHandler.execQuery(query);
            if(result.next()){
                memberId = result.getString("memberID"); 
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Renew Operation", "Are you sure want to renew the book " + bookName.getText() + "? ");
        
        if(responce.get() == ButtonType.OK){
            LocalDate renewDate = LocalDate.now();
            String query1 = "UPDATE ISSUE SET renewCount = renewCount+1, lastRenewDate = '"+renewDate+"'  WHERE bookID = '" + bookId + "' "; 
            String query2 = "UPDATE BOOK SET renewCount = renewCount+1  WHERE B_ID = '" + bookId + "' ";
            String query3 = "UPDATE MEMBER SET renewCount = renewCount+1  WHERE M_ID = '" + memberId + "' ";
            
            if(databaseHandler.execAction(query1) && databaseHandler.execAction(query2) && databaseHandler.execAction(query3)){
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
        total_fine.setText("Total Fine");
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
        memberChart.setLegendSide(Side.BOTTOM);
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
        int finePerDay = preferences.getFinePerDay();
        int maxNoOfDays = preferences.getnOfDays();
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths()*30 + intervalPeriod.getYears()*365);
   
        if(dateCount > maxNoOfDays){
            return ((dateCount-maxNoOfDays)*finePerDay);
        }else{
            return dateCount*0;
        }
    }

    
    int delayedDates(String dateIssue){
        int maxNoOfDays = preferences.getnOfDays();
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
   
        if(dateCount > maxNoOfDays){
            return (dateCount-maxNoOfDays);
        }else{
            return (dateCount*0);
        }
    }
    
    
    int countDays(String dateIssue){
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
        return dateCount;
    }
//  test
    
    
//    private void testData() {
        
        
//        String query6 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,issueDate,submitDate,fine,renewCount) VALUES (11, '11','11','2019-6-2','2019-6-9', 1 ,1)";
//        String query7 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,issueDate,submitDate,fine,renewCount) VALUES (12, '12','12','2019-6-3','2019-6-10', 1 ,1)";
//        String query6 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,fine,submitDate,issueDate,renewCount) VALUES (123,'11','11',200,'2019-6-4','2019-6-15',0)";
//        String query4 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,issueDate,submitDate,fine,renewCount) VALUES (13, '13','13','2019-6-5','2019-6-12', 1 ,1)";
//        String query5 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,issueDate,submitDate,fine,renewCount) VALUES (14, '14','14','2019-6-6','2019-6-13', 1 ,1)";
//        String query8 = "INSERT INTO SUBMISSION (submissionID,bookID,memberID,issueDate,submitDate,fine,renewCount) VALUES (15, '15','15','2019-6-6','2019-6-13', 1 ,1)";
//        String query6 = "INSERT INTO SUBMISSION VALUES (55,'5','5',0,'2019-6-25','2019-7-5',1)";
//        System.out.println(query6);
//        if(databaseHandler.execAction(query6)){
//            System.out.println("success");
//        }        
//        String query1 = "INSERT INTO MEMBER VALUES ( '2','asd','true')";
//        String query2 = "INSERT INTO MEMBER VALUES ( '3','aasd','true')";
//        String query3 = "INSERT INTO MEMBER VALUES ( '4','asdasd','true')";
//        String query4 = "INSERT INTO MEMBER VALUES ( '5','asdadf','true')";
//        String query5 = "INSERT INTO MEMBER VALUES ( '6','asdew','true')";
            
//        String query1 = "INSERT INTO BOOK VALUES ( '2','book2','auth2','pub3', 12 ,12,'2013-2-12','asdasdasasda','true')";
//        String query2 = "INSERT INTO BOOK VALUES ( '3','book3','auth3','pub2', 12 ,12,'2013-2-12','asdasdasasda','true')";
//        String query3 = "INSERT INTO BOOK VALUES ( '4','book4','auth2','pub3', 12 ,12,'2013-2-12','asdasdasasda','true')";
//        String query4 = "INSERT INTO BOOK VALUES ( '5','book5','auth5','pub1', 12 ,12,'2013-2-12','asdasdasasda','true')";
//        String query5 = "INSERT INTO BOOK VALUES ( '6','book6','auth5','pub1', 12 ,12,'2013-2-12','asdasdasasda','true')";
           
//    
//          String query1 = "INSERT INTO ISSUE (bookID,memberID,issueDate,renewCount,lastRenewDate) VALUES ( '11','11','2019-9-20',0,'2019-9-20')";
//            if(databaseHandler.execAction(query1)){
//            System.out.println("success");
//        }  
//          String query7 = "UPDATE BOOK SET isAvail = true WHERE B_ID = '5' ";
//          String query8 = "UPDATE MEMBER SET isSubmit = true WHERE M_ID = '5' ";  
//             String query9 = "DELETE FROM ISSUE WHERE bookID = '5' ";
//                    String query8 = "UPDATE BOOK SET isAvail = true WHERE B_ID = '4' ";
//                    String query9 = "UPDATE MEMBER SET isSubmit = true WHERE M_ID = '4' ";
//
//
//          System.out.println(query6);
//          System.out.println(query7);
//          System.out.println(query8);
//          System.out.println(query9);
//    
//    
//           if(databaseHandler.execAction(query8) && databaseHandler.execAction(query7) && databaseHandler.execAction(query6) ){
//               System.out.println("success");
//           }else{
//               System.out.println("fasle");
//           }
//        if(databaseHandler.execAction(query7) && databaseHandler.execAction(query8) && databaseHandler.execAction(query9) ){
//                        System.out.println("success");
//                       
//        }
//                              
//    }

   
   

    
}
