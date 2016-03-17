
package zderby;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ZDerbyMain extends Application {
      
    @Override
    public void start(Stage stage) throws Exception {
        Pane root = FXMLLoader.load(getClass().getResource("dbdata.fxml"));
        
        Scene scene = new Scene(root);
        stage.setTitle("Derby DB with JavaFX"); 
        stage.setOnCloseRequest(e -> handle(e));
        stage.setScene(scene);
        stage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource("dbdata.css").toExternalForm());
        stage.show();
    }
    private void handle(WindowEvent e) {
        Platform.exit();
        System.exit(0);  
    }
    public static void main(String[] args) {
        launch(args);
    }
}
