package Test;

import java.io.IOException;

import com.example.App;
import com.example.gameEntryController;

import Model.model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class modelGuestTest {
    
    
    public static void main(String[] args){
        try {
            testApp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        testGameEntryController();
    }
    @Test
    public static void testApp() throws Exception {
        // Load the FXML file and initialize the gameEntryController
        FXMLLoader loader = new FXMLLoader(App.class.getResource("gameEntry.fxml"));
        Parent root = loader.load();
        gameEntryController controller = loader.getController();

        // Set the GameMode and assert the updated text
        controller.HostChosen();

        // Add more assertions as needed for other methods and behavior
        // ...

        // Create a stage and set the scene with the loaded FXML file
        Stage stage = new Stage();
        Scene scene = new Scene(root, 900, 640);
        stage.setScene(scene);
        stage.show();

        // Optionally, you can also test the behavior of the App class itself
        // by calling its methods and asserting the expected results
        // ...

        // Close the stage (optional, depending on your test scenario)
        stage.close();
    }

    @Test
    public static void testGameEntryController() {
        gameEntryController controller = new gameEntryController();

        // Choose host mode
        try {
            controller.HostChosen();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        // Choose one player
        controller.onePlayerGame();
        

        // Simulate starting the game
        try {
            controller.switchToMainWindow();
        } catch (IOException e) {
            // Handle the exception
        }
    }
    
}
