
package schoollibrary.ui.setting;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schoollibrary.database.DatabaseHandler;


public class Setting extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/schoollibrary/ui/setting/setting.fxml"));
        Scene scene = new Scene(root);
        
       
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Settings");

        new Thread(() -> {
            DatabaseHandler.getInstance();
        }).start();
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}  