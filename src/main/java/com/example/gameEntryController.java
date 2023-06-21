package com.example;
import java.io.IOException;

import ViewModel.view_model;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import java.util.Random;


public class gameEntryController {
    public boolean isHost;
    public int port_num;

    public gameEntryController(){
       
    }

    @FXML
    MenuButton GameMode;
    
    @FXML
    MenuButton NumberOfPlayers;

    @FXML
    private void switchToMainWindow() throws IOException {
      //  System.out.println("is host value is:"+isHost);
        if(!App.isHost){App.port_guest = setEnterPort();}
        App.setRoot("MainWindow");
    }
    @FXML
    Label portNum;
    @FXML
    TextField txtEnterPort;
    @FXML
    public void HostChosen() throws IOException{
        GameMode.setText("Host Mode");
        NumberOfPlayers.setVisible(true);
        System.out.println("gameEntryController: host mode");
        App.setIsHost(true);
        //Object random;
        int port =  (int) (Math.random() * (1001) + 5000);
        App.setPortNumber(port);
        portNum.setVisible(true);
        txtEnterPort.setVisible(false);
        String str = "" + port;
        portNum.setText("Host number: " + str);
        System.out.println("port number in gameEntry:"+port);
        portNum.setVisible(true);

    }
    @FXML
    public void GuestChosen(){
        txtEnterPort.setVisible(true);
        NumberOfPlayers.setVisible(false);
        portNum.setVisible(false);
        GameMode.setText("Guest Mode");
        setIsHost(false);
    }

    public int setEnterPort() {
        String portStr = txtEnterPort.getText();
        System.out.println("port string input is:" +portStr);
        return Integer.parseInt(portStr);
    }   

    public boolean getIsHost() {
        return isHost;
    }
    
    public void setIsHost(boolean isHost) {
        this.isHost=isHost;
    }

    public void setPortNumber(int port) {
        this.port_num=port;
    }

    public void printNum(){
        System.out.println("printNum "+ port_num);
    }
     
    @FXML
    public void onePlayerGame(){
        NumberOfPlayers.setText("One Player");
        App.numOfPlayersChosen = 1;
    }
    
    @FXML
    public void twoPlayersGame(){
        NumberOfPlayers.setText("Two Players");
        App.numOfPlayersChosen = 2;
    }
    @FXML
    public void threePlayersGame(){
        NumberOfPlayers.setText("Three Players");
        App.numOfPlayersChosen = 3;
    }
    @FXML
    public void fourPlayersGame(){
        NumberOfPlayers.setText("Four Players");
        App.numOfPlayersChosen = 4;
    }


}