
package schoollibrary.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javax.swing.JOptionPane;
public final class DatabaseHandler {
    
    private static DatabaseHandler handler = null;
   
    private static final String DB_URL = "jdbc:derby:database;create=true";   
    private static Connection conn = null;
    private static Statement stmt = null;
    
    private DatabaseHandler(){
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
        setupSubmissionTable();
    }
    
    public static DatabaseHandler getInstance(){
        if(handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

  
    void createConnection(){
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Cant load database","Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    void setupBookTable(){
        String TABLE_NAME = "BOOK";
        try{
            stmt =conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " Already exist.. ready for go...");
            }else{
                stmt.execute( " CREATE TABLE " + TABLE_NAME + " ( "
                            + " B_ID varchar(100) primary key,\n "
                            + " BName varchar(200),\n "
                            + " author varchar(200),\n "
                            + " publisher varchar(200),\n "
                            + " price decimal(6,2),\n "
                            + " pages int,\n "
                            + " receiveDate DATE,\n "
                            + " description varchar(200),\n "
                            + " isAvail boolean default true "
                            + " ) " 
                    );        
            }   
        }catch(SQLException e){
            System.err.println(e.getMessage() + "  ...setupDatabase...");
        }finally{
        }
    }
    
    
    void setupMemberTable(){
        String TABLE_NAME = "MEMBER";
        try{
            stmt =conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " Already exist.. ready for go...");
            }else{
                stmt.execute( " CREATE TABLE " + TABLE_NAME + " ( "
                            + " M_ID varchar(100) primary key,\n "                          
                            + " MName varchar(200),\n "
                            + " isSubmit boolean default true "
                            + " ) " 
                    );        
            }   
        }catch(SQLException e){
            System.err.println(e.getMessage() + "  ...setupDatabase...");
        }finally{
        }
    }
    
    
    void setupIssueTable(){
        String TABLE_NAME = "ISSUE";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " Already exist.. ready for go...");
            }else{
                stmt.execute( " CREATE TABLE " + TABLE_NAME + " ( "
                            + " bookID varchar(100) primary key,\n "
                            + " memberID varchar(100) ,\n "
                            + " issueTime DATE DEFAULT CURRENT_DATE,\n "
                            + " renewCount integer DEFAULT 0,\n "
                            + " FOREIGN KEY (bookID) REFERENCES BOOK(B_ID),\n "
                            + " FOREIGN KEY (memberID) REFERENCES MEMBER(M_ID) "
                            + " ) " 
                    );        
            }   
        }catch(SQLException e){
            System.err.println(e.getMessage() + "  ...setupDatabase...");
        }finally{
        }
    }
    
    
    void setupSubmissionTable(){
        String TABLE_NAME = "SUBMISSION";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " Already exist.. ready for go...");
            }else{
                stmt.execute( " CREATE TABLE " + TABLE_NAME + " ( " 
                            + " submissionID INT primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n "
                            + " bookID varchar(100) ,\n "
                            + " memberID varchar(100) ,\n "
                            + " submitTime DATE DEFAULT CURRENT_DATE ,\n "
                            + " issueTime DATE ,\n "
                            + " renewCount integer "
                            + " ) " 
                    );        
            }   
        }catch(SQLException e){
            System.err.println(e.getMessage() + "  ...setupDatabase...");
        }finally{
        }
    }
    
    
    public ResultSet execQuery(String query){
        ResultSet result;
        try{
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }catch(SQLException ex){
            System.out.println("Exception at execQuery:handler" + ex.getLocalizedMessage());
            return null;
        }finally{            
        }
        return result;
    }
    
    
    public boolean execAction(String query){
        try{
            stmt = conn.createStatement();
            stmt.execute(query);
            return true;
        }catch(SQLException ex){
            return false;
        }finally{            
        }
    }
    
    
    public  ObservableList<PieChart.Data> getBookStatistic(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        try {
            String query1 = "SELECT COUNT(*) FROM BOOK";
            String query2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet result = execQuery(query1);
            int count = 0;
            if(result.next()){
                count = result.getInt(1);
            }
            result = execQuery(query2);
            if(result.next()){
                int count1 = result.getInt(1);
                data.add(new PieChart.Data("Available book ("+(count-count1)+")",(count-count1)) );
                data.add(new PieChart.Data("Issued book ("+count1+")",count1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
    
    
    public  ObservableList<PieChart.Data> getMemberStatistic(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        
        try {
            String query1 = "SELECT COUNT(*) FROM MEMBER";
            String query2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet result = execQuery(query1);
            int count = 0;
            if(result.next()){
                count = result.getInt(1);
            }
            result = execQuery(query2);
            if(result.next()){
                int count1 = result.getInt(1);
                data.add(new PieChart.Data("Without book ("+(count-count1)+")",(count-count1)) );
                data.add(new PieChart.Data("with book ("+count1+")",count1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
}


