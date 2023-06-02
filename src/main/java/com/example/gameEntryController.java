package com.example;
import java.io.IOException;

import ViewModel.view_model;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

public class gameEntryController {
    public boolean isHost;
    //private BooleanProperty isHost = new SimpleBooleanProperty(false);
    //public boolean isHost;
    @FXML
    MenuButton GameMode;

    @FXML
    private void switchToMainWindow() throws IOException {
        App.setRoot("MainWindow");
    }

    /* 
    public void HostChosen(){
        GameMode.setText("Host Mode");
        setIsHost(true);
    }
    */
    @FXML
    public void HostChosen() throws IOException{
        GameMode.setText("Host Mode");
        System.out.println("gameEntryController: host mode");
        App.setIsHost(true);
        //isHost=true;
    }
    @FXML
    public void GuestChosen(){
        GameMode.setText("Guest Mode");
        setIsHost(false);
    }

    
    public boolean getIsHost() {
        return isHost;
    }
    
    public void setIsHost(boolean isHost) {
        this.isHost=isHost;
    }
     
}