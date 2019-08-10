
package schoollibrary.ui.main;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schoollibrary.database.DatabaseHandler;
import schoollibrary.util.LibraryAssistantUtil;


public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/schoollibrary/ui/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Library System Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        LibraryAssistantUtil.setStageIcon(stage);
  
        new Thread(() -> {
           DatabaseHandler.getInstance();
        }).start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}   