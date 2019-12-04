
package schoollibrary.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
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
import javafx.scene.control.Label;
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
import schoollibrary.ui.comparison.ComparisonController;
import schoollibrary.ui.issueBook.IssueBookController;
import schoollibrary.ui.report.ReportController;
import schoollibrary.ui.setting.Preferences;
import schoollibrary.ui.setting.SettingController;
import schoollibrary.ui.submission.SubmissionController;
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
    @FXML
    private Text subLale2;
    @FXML
    private VBox bookInfoContainer;
    @FXML
    private VBox memberInfoContainer;
    @FXML  
    private Tab issueTab;
    @FXML
    private Label dateTime;
     
     
    PieChart bookChart;
    PieChart memberChart;
    private boolean btnTag1 = true;
    private boolean btnTag2 = true;

    DatabaseHandler databaseHandler;
    Preferences preferences;  
    
   
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
        IssueBookController controller = (IssueBookController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadHistory(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/submission/submission.fxml"));
        loadWindow("History", loader);
        SubmissionController controller = (SubmissionController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadSetting(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/setting/setting.fxml"));
        loadWindow("Setting", loader);
        SettingController controller = (SettingController) loader.getController();
        controller.getController(this);
    } 
    @FXML
    private void loadReport(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/report/report.fxml"));
        loadWindow("Report", loader);
        ReportController controller = (ReportController) loader.getController();
        controller.getController(this);
    }
    @FXML
    private void loadComparison(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/comparison/comparison.fxml"));
        loadWindow("Comparison", loader);
        ComparisonController controller = (ComparisonController) loader.getController();
        controller.getController(this);
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
    @FXML
    private void menuReports(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/report/report.fxml"));
        loadWindow("Report", loader);
        ReportController controller = (ReportController) loader.getController();
        controller.getController(this);
    }

    @FXML
    private void menuComparison(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/comparison/comparison.fxml"));
        loadWindow("Comparison", loader);
        ComparisonController controller = (ComparisonController) loader.getController();
        controller.getController(this);
    }

    @FXML
    private void menuSettings(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/schoollibrary/ui/setting/setting.fxml"));
        loadWindow("Setting", loader);
        SettingController controller = (SettingController) loader.getController();
        controller.getController(this);
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
            
            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH,preferences.getnOfDays());
            
            String query4 = "INSERT INTO REPORT (bookID,memberID,willSubmit,keepDays,finePerDay) VALUES ( " +
                    "'" + bookId + "'," +
                    "'" + memberId + "'," +
                    "'" + sdf.format(cal.getTime()) + "', " +
                    preferences.getnOfDays() + ", " +
                    preferences.getFinePerDay() +
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
            String query1 = "SELECT * FROM REPORT WHERE bookID = '" + bookId + "' AND isSubmit = 'false'";
            ResultSet result = databaseHandler.execQuery(query1);
            try {
                if(result.next()){
                    subDetals1.setVisible(true);
                    subDetals3.setVisible(true);
                    subLale2.setText("Member");
                    
                    String memberId = result.getString("memberID");  
                    int totalFine = countTotalFine(result.getString("lastRenewDate"), Integer.parseInt(result.getString("keepDays")) ,Integer.parseInt(result.getString("finePerDay")) );
                    int dateDelayed = delayedDates(result.getString("lastRenewDate"), Integer.parseInt(result.getString("keepDays")));
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
        int id,fine,maxDate,dateCount,delayedDateCount;
        boolean isLate;
   
        try{
            String query = "SELECT COUNT(bookID) as count FROM REPORT WHERE bookID = '" + bookId + "' AND isSubmit = 'false' ";
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
        
        String query = "SELECT * FROM REPORT WHERE bookID = '" + bookId + "' AND isSubmit = 'false' ";
        ResultSet result = databaseHandler.execQuery(query);  
        try {
            if (result.next()) { 
                id = Integer.parseInt(result.getString("reportID"));
                memberId = result.getString("memberID");
                String dateFrom = result.getString("lastRenewDate");
                maxDate = Integer.parseInt(result.getString("keepDays"));
                fine = countTotalFine(dateFrom, maxDate, Integer.parseInt(result.getString("finePerDay")));
                delayedDateCount = delayedDates(dateFrom,maxDate);
                dateCount = countDays(dateFrom);
                
                if(delayedDateCount == 0){
                    isLate = false;
                }else{
                    isLate = true;
                }
                
                Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Submission Operation","Are you sure want to submit the book");
                
                if(responce.get() == ButtonType.OK){             
                    LocalDate submitDate = LocalDate.now();
                    String query1 = "UPDATE REPORT SET fine = " + fine + "," 
                                    + "submitDate = '" + submitDate + "',"
                                    + "isSubmit = '" + true + "',"
                                    + "isLateSubmit = '" + isLate + "',"
                                    + "nuOfDaysKept = " + dateCount 
                                    + "WHERE reportID = " + id +" ";
                    
                    String query2 = "UPDATE BOOK SET isAvail = true, subCount = subCount+1, fineCollect = fineCollect +'" + fine + "'  WHERE B_ID = '" + bookId + "' ";
                    String query3 = "UPDATE MEMBER SET isSubmit = true, subCount = subCount+1, finePayed = finePayed + '" + fine + "' , delayedDateCount = delayedDateCount + '" + delayedDateCount + "'  WHERE M_ID = '" + memberId + "' ";
                    if(databaseHandler.execAction(query1) && databaseHandler.execAction(query2) && databaseHandler.execAction(query3)){
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
        int id = 0;
        try{
            String query = "SELECT COUNT(bookID) as count FROM REPORT WHERE bookID = '" + bookId + "' AND isSubmit = 'false' ";
            ResultSet result = databaseHandler.execQuery(query);
            result.next();
            int count1 = result.getInt("count");     
            if(count1 == 0){
                AlertMaker.errorAlert("Invalid book ID","This book is not issued" );
                return;
            }
            
            query = "SELECT memberID,reportID FROM REPORT WHERE bookID = '" + bookId + "' AND isSubmit = 'false' ";
            result = databaseHandler.execQuery(query);
            if(result.next()){
                memberId = result.getString("memberID"); 
                id = Integer.parseInt(result.getString("reportID"));
            }
            
        }catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        Optional<ButtonType> responce = AlertMaker.confirmationAlert("Confirm Renew Operation", "Are you sure want to renew the book " + bookName.getText() + "? ");

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,preferences.getnOfDays());
 
        if(responce.get() == ButtonType.OK){
            LocalDate renewDate = LocalDate.now();
            String query1 = "UPDATE REPORT SET renewCount = renewCount+1, "
                                                + "lastRenewDate = '"+renewDate+"', "
                                                + "willSubmit = '"+sdf.format(cal.getTime())+"', "
                                                + "keepDays = " + preferences.getnOfDays() + ", "
                                                + "finePerDay = "+ preferences.getFinePerDay() +"  "
                                                + "WHERE reportID = " + id + "" ;
            
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
    
    
    void clearBookCache(){
        bookName.setText("");
        bookStatus.setText("");
    }
    
    
    void clearMemberCache(){
        memberName.setText("");         
    }
    
    
    public void setSubmssionContent(){
        issue_date.setText("Issue Date");
        renew_count.setText("Renew Count");  
        book_name.setText("Book Name");
        description.setText("Description");
        date_delayed.setText("No of days delayed");
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
   
    
    int countTotalFine(String dateIssue,int days,int fine){  
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths()*30 + intervalPeriod.getYears()*365);
   
        if(dateCount > days){
            return ((dateCount-days)*fine);
        }else{
            return dateCount*0;
        }
    }

    
    int delayedDates(String dateIssue,int maxDate){
        LocalDate dateFrom = LocalDate.parse(dateIssue); 
        LocalDate dateTo = LocalDate.now();
        Period intervalPeriod = Period.between(dateFrom, dateTo);
        int dateCount = (intervalPeriod.getDays() + intervalPeriod.getMonths() + intervalPeriod.getYears());
   
        if(dateCount > maxDate){
            return (dateCount-maxDate);
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
    
    
//    private void initClock() {
//}
      //  test
    

//    private void testData() {
//
//        String query = "INSERT INTO REPORT (bookID,memberID,issueDate,lastRenewDate,willSubmit,renewCount,keepDays,finePerDay,submitDate,isSubmit) VALUES ('1','1','2018-12-12','2018-12-12','2018-12-17',0,7,10,'2018-12-17','true')";
//        String query = "INSERT INTO MEMBER (M_ID,MName,addedDate) VALUES ('11','11','2019-12-12')";
//                    
//        if(databaseHandler.execAction(query)){
//            System.out.println("success");
//        }
//        
//        
//   }

    
     
}
