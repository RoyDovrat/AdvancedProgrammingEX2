package com.example;

//import java.sql.Time;
import java.util.Observable;
//import javax.swing.plaf.synth.SynthStyle;
import Model.interfaceModel;
import Model.model;
import Model.modelGuest;
import Model.modelGuestHandler;
import Model.modelHost;
import ViewModel.view_model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import java.util.Random;

public class SmartBoard extends AnchorPane {
    BoardDisplayer board;
    interfaceModel m;
    
    public SmartBoard(){
        super();
        try {
            Random random = new Random();
            boolean newGameFlag=App.newGame;
            gameEntryController entry= new gameEntryController();
            board = new BoardDisplayer();
            view_model vm;
            System.out.println("view model vm");
            if(App.isHost){ 
                int port = App.port_num;
                m= new modelHost(port, new modelGuestHandler(), 2, newGameFlag);
                m.start();
                System.out.println("smartBoard port number: "+port);
            }
            else{//guest
                int guestPort = App.port_guest;
                System.out.println(guestPort);
                System.out.println("guestPort is:"+ guestPort);
                int port = guestPort;//5000;//to change
                m= new modelGuest(port);
            }
            
            
            vm = new view_model(m); // View-Model

            vm.addObserver(board);
            
            FXMLLoader loader = new
            FXMLLoader(getClass().getResource("GameBoard.fxml"));
            loader.setController(board);
            Node b = loader.load();
            board.setViewModel(vm); 
            this.getChildren().add(b);
        }
        catch (Exception e){}
    }
}