
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
        }catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e){
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
                            + " category varchar(200),\n "
                            + " author varchar(200),\n "
                            + " publisher varchar(200),\n "
                            + " price decimal(6,2),\n "
                            + " pages int,\n "
                            + " receivedDate DATE,\n "
                            + " addedDate DATE DEFAULT CURRENT_DATE,\n "
                            + " description varchar(200),\n "
                            + " issueCount int default 0 ,\n "
                            + " renewCount int default 0 ,\n "
                            + " subCount int default 0 ,\n "
                            + " fineCollect int default 0 ,\n "
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
                            + " addedDate DATE DEFAULT CURRENT_DATE,\n "
                            + " issueCount int default 0 ,\n "
                            + " renewCount int default 0 ,\n "
                            + " subCount int default 0 ,\n "
                            + " finePayed int default 0 ,\n "
                            + " delayedDateCount int default 0 ,\n "
                            + " isSubmit boolean default true "
                            + " ) " 
                    );        
            }   
        }catch(SQLException e){
            System.err.println(e.getMessage() + "  ...setupDatabase...");
        }finally{
        }
    }
   
    
    
    void setupSubmissionTable(){
        String TABLE_NAME = "REPORT";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " Already exist.. ready for go...");
            }else{
                stmt.execute( " CREATE TABLE " + TABLE_NAME + " ( " 
                            + " reportID INT primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n "
                            + " bookID varchar(100) ,\n "
                            + " memberID varchar(100) ,\n "
                            + " issueDate DATE DEFAULT CURRENT_DATE,\n "
                            + " lastRenewDate DATE DEFAULT CURRENT_DATE,\n "
                            + " willSubmit DATE,\n "
                            + " keepDays integer,\n "
                            + " finePerDay integer,\n "
                            + " fine integer DEFAULT 0,\n "
                            + " renewCount integer DEFAULT 0,\n"
                            + " submitDate DATE DEFAULT NULL ,\n "
                            + " isSubmit boolean DEFAULT false,\n"
                            + " isLateSubmit boolean DEFAULT false,\n"                        
                            + " nuOfDaysKept integer DEFAULT 0"
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
            String query2 = "SELECT COUNT(*) FROM REPORT WHERE isSubmit = 'false' ";
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
            String query2 = "SELECT COUNT(*) FROM REPORT WHERE isSubmit = 'false'";
            ResultSet result = execQuery(query1);
            int count = 0;
            if(result.next()){
                count = result.getInt(1);
            }
            result = execQuery(query2);
            if(result.next()){
                int count1 = result.getInt(1);
                data.add(new PieChart.Data("with book ("+count1+")",count1));
                data.add(new PieChart.Data("Without book ("+(count-count1)+")",(count-count1)) );
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

}


