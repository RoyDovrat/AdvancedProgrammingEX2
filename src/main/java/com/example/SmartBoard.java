package com.example;

//import java.sql.Time;
import java.util.Observable;

//import javax.swing.plaf.synth.SynthStyle;

import Model.interfaceModel;
import Model.model;
import Model.modelGuest;
import Model.modelHost;
import ViewModel.view_model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
public class SmartBoard extends AnchorPane {
    BoardDisplayer board;
    interfaceModel m;
    gameEntryController entry= new gameEntryController();
    public SmartBoard(){
        super();
        try {
            board = new BoardDisplayer();
            System.out.println("isHost on smart:"+ entry.isHost);
            if(entry.isHost==true){
                m= new modelHost();
                System.out.println("yes host");
            }
            else{
                m= new modelGuest();
                System.out.println("not host");
            }
            
            //model m = new model();
            //m= new modelHost();
            view_model vm = new view_model(m); // View-Model

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
