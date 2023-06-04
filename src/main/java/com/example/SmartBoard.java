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
public class SmartBoard extends AnchorPane {
    BoardDisplayer board;
    interfaceModel m;
    //modelGuestHandler gh= new modelGuestHandler(0, null)
    
    public SmartBoard(){
        super();
        try {
            gameEntryController entry= new gameEntryController();
            board = new BoardDisplayer();
            view_model vm;
            if(App.isHost){
                m= new modelHost(5000, new modelGuestHandler(), 2);
                m.start();
                System.out.println("is host in smart");
            }
            else{
                m= new modelGuest(5000);
                System.out.println("is host in smart");
            }
            
            //m= new modelHost();
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