package com.example;
import java.io.IOException;

import ViewModel.view_model;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;

public class gameEntryController {
    public static boolean isHost;
   
    @FXML
    MenuButton GameMode;
    
    @FXML
    private void switchToMainWindow() throws IOException {
        App.setRoot("MainWindow");
    }
    @FXML
    public void HostChosen(){
        GameMode.setText("Host Mode");
        isHost=true;
       
    }
    @FXML
    public void PlayerChosen(){
        GameMode.setText("Player Mode");
        isHost=false;
        
    }
    
}
