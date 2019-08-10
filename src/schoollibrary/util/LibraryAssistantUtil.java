package schoollibrary.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LibraryAssistantUtil {
    public static final String IMAGE_LOC = "/schoollibrary/ui/resources/icon2.png";
    
    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMAGE_LOC));
    }
}
