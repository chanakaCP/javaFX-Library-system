
package schoollibrary.ui.setting;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import schoollibrary.alert.AlertMaker;


public final class Preferences {
    public static final String CONFIG_FILE = "config.txt";
    
    String username;
    String password;
    String confirmPassword;
    int nOfDays;  
    
    public Preferences(){
        username = "admin";
        setPassword("admin");
        setConfirmPassword("admin");
        nOfDays = 7;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = DigestUtils.shaHex(password);
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = DigestUtils.shaHex(confirmPassword);
    }

    public int getnOfDays() {
        return nOfDays;
    }

    public void setnOfDays(int nOfDays) {
        this.nOfDays = nOfDays;
    }
    
    
    public static void initConfig(){
        Writer writer =  null;
        try {
            Preferences preference =  new Preferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference,writer);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public static Preferences getPreferences(){
        Gson gson = new Gson();
        Preferences preference = new Preferences();
        try {
            preference =  gson.fromJson(new FileReader(CONFIG_FILE),Preferences.class);
        } catch (FileNotFoundException ex) {
            initConfig();
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preference;
    }
   
    
    public static void writeNewPreferences(Preferences newPreferences){
        Writer writer =  null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(newPreferences,writer);
            AlertMaker.informatinAlert("Success","Settings Updated");
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            AlertMaker.errorAlert("Failed","Can`t save CONFIGURATION file");
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
