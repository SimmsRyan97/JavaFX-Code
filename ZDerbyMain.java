
package zderby;

import java.sql.DriverManager;
import java.sql.SQLException;
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
        //Proper CLOSE of connection to DB keeps proper incrementing by 1 as set when the table is created
        String conURL = "jdbc:derby:;shutdown=true";    
        try{
            DriverManager.getConnection(conURL);
        }catch (SQLException se){
        if(!(se.getErrorCode() == 50000) && (se.getSQLState().equals("XJ015")))
        System.err.println(se);
        }    
        Platform.exit();
        System.exit(0);  
    }
    public static void main(String[] args) {
        launch(args);
    }
}
