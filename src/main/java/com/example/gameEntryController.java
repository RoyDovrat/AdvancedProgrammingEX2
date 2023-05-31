package com.example;
import java.io.IOException;

import ViewModel.view_model;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

public class gameEntryController {
    //public static boolean isHost;
    public boolean isHost;
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
    public void GuestChosen(){
        GameMode.setText("Guest Mode");
        isHost=false;
    }
    
    
}
