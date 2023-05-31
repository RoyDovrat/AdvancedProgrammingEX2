package com.example;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;



public class MainWindowController {   // implements Initializable  { //
//Board board = new Board();

    BoardDisplayer gameBoard= new BoardDisplayer();

//byte[][] bonusMatrix = board.getBonus();
   
    
    /* 
    @FXML
    BoardDisplayer boardDisplayer;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        boardDisplayer.setBoardData(bonusMatrix);

        boardDisplayer.addEventFilter(MouseEvent.MOUSE_CLICKED, (e)->boardDisplayer.requestFocus());
        
        boardDisplayer.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event){
                int r=boardDisplayer.getcRow();
                int c=boardDisplayer.getcCol();
                if (event.getCode() == KeyCode.UP){
                    boardDisplayer.setCharacterPosition(r-1, c);
                }
                if (event.getCode() == KeyCode.DOWN){
                    boardDisplayer.setCharacterPosition(r+1, c);
                }
                if (event.getCode() == KeyCode.RIGHT){
                    boardDisplayer.setCharacterPosition(r, c+1);
                }
                if (event.getCode() == KeyCode.LEFT){
                    boardDisplayer.setCharacterPosition(r, c-1);
                }
            }
        });
    }
    */
    /* 
    @FXML
    public void startGame(){
        System.out.println("Start");
        //gameBoard.restartLetterTiles();
    }
    
    @FXML
    public void openFile(){
        System.out.println("OpenFile");
         
        FileChooser fc= new FileChooser();
        fc.setTitle("open maze file");
        fc.setInitialDirectory(new File("./resources2"));
        //fc.setSelectedExtensionFilter(null);
        File chosen= fc.showOpenDialog(null);    
        if (chosen != null){
            System.out.println(chosen.getName());
        }
    }
    */
    

}
