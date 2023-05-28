package com.example;

import Model.model;
import ViewModel.view_model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
public class SmartBoard extends AnchorPane {
    BoardDisplayer board;

    public SmartBoard(){
        super();
        try {
            board = new BoardDisplayer();
            model m = new model();
            view_model vm = new view_model(m); // View-Model
            m.addObserver(vm);
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
