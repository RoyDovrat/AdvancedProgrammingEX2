package com.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import Model.Board;
import Model.Tile;
import ViewModel.view_model;

public class BoardDisplayer implements Observer {
    
    byte[][] boardData; //removed to model
    char[][] boardChars=new char[15][15];
    view_model vm;  
    int cCol, cRow;
    char[] vLetterTiles;
    
    private int column = 0, line=0, tile=0;
    boolean vertical, submitFlag=false;
    public int mouseRow;
    public int mouseCol;

    @FXML
    String vStrLetterTiles;

    @FXML
    TextField vWordInput;

    @FXML
    Label validWord;
    
    @FXML
    MenuButton TileDirection;

    @FXML
    MenuItem TileDown;
    
    @FXML
    MenuItem TileRight;

    @FXML
    Canvas myCanvas;
    
    @FXML
    Canvas LetterTilesCanvas;
    
    @FXML
    private Button startGameButton;

    @FXML
    Label ColBoard, RowBoard;

    @FXML
    Label score;

    public BoardDisplayer(){
        //wallFileName=new SimpleStringProperty();
        cCol=0;
        cRow=1;
    }
    
    public void setViewModel(view_model vm) {
        this.vm=vm;
        vm.wordInput.bind(vWordInput.textProperty());//הלוך
        validWord.textProperty().bind((vm.vmValidWord.asString()));//חזור
        //score.textProperty().bind(vm.vmScore);
        StringProperty scoreText = new SimpleStringProperty();
        scoreText.bind(Bindings.concat("score: ", vm.vmScore.asString()));
        score.textProperty().bind(scoreText);
    }   
  
    public void setCharacterPosition(int row, int col){
        cCol=col;
        cRow=row;
        redraw();
    }
    
    public int getcCol() {
        return cCol;
    }

    public int getcRow() {
        return cRow;
    }
   
    public void shutStartButton(){
        this.startGameButton.setVisible(false);
    }

    @FXML
    public void setNewGameBoard(){
        sendGameMode(gameEntryController.isHost);
        redraw();
        vLetterTiles=vm.vmRestartLetterTiles();
        drawLetterTiles(vLetterTiles);
        shutStartButton();
    }
    public void sendGameMode(boolean isHost){
        vm.vmSendGameMode(isHost);
    }
    public void drawLetterTiles(char[] letterTiles) {
        Double W = LetterTilesCanvas.getWidth();
        Double H = LetterTilesCanvas.getHeight();
        GraphicsContext gc = LetterTilesCanvas.getGraphicsContext2D();
        Image tileImg = null;
        
        gc.clearRect(0, 0, W, H);
        double tileHeight = H / 10;
        double tileWidth = W / 2;
        
        for (int i = 0; i < 7; i++) {
            
            if (i < letterTiles.length) {
                int j = 0;
                String name_letter = "./resources2/" +letterTiles[i] +".jpg";
                try {
                    tileImg = new Image(new FileInputStream(name_letter));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                DisplayImage(gc, tileImg, i, j, tileWidth, tileHeight); 
            }
        }
    }
    
    @FXML
    public void submitWord(){
        
        
        System.out.println("word input in view: "+vWordInput.getText());
        boardChars=vm.vmSubmitWord(mouseRow,mouseCol); 
        for(int i=0; i<boardChars[0].length; i++){
            for(int j=0; j<boardChars.length; j++){
                
            }
            
        }
        
        submitFlag=true;
        drawLetterTiles(vm.vmFillLetterTiles());
        redraw();
        vWordInput.setText("");
        //if(validWord==true){

        //} 
    }
    @FXML
    public void LeftRightClicked(){
        TileDirection.setText("Horizonal");
        
        vm.vmLeftRightClicked(); 
    }
    @FXML
    public void downClicked(){
        TileDirection.setText("Vertical");
        
        vm.vmDownClicked(); 
    }
    
    public void handleMouseClick(MouseEvent event) {
        Double W= myCanvas.getWidth();
        Double H= myCanvas.getHeight();
        Double w= W / (boardData[0].length+1);
        Double h= H / (boardData.length+1);
        double mouseX = event.getX();
        double mouseY = event.getY();
        // Calculate the row and column indices based on mouse coordinates
        int column = (int) (mouseX / w) - 1;
        int row = (int) (mouseY / h) - 1;
    
        mouseCol = column;
        mouseRow = row;
        ColBoard.setText("Column: " + (column+1));
        String characterLabel = String.valueOf((char) ('A' + row));
        RowBoard.setText("Row: " + characterLabel);

    }
    
    public void DisplayImage(GraphicsContext gc, Image img, int i, int j, double w, double h){
       
        int k = 25;
        if (img==null){
            gc.fillRect(j*w+k, i*h+k, w, h);
        }
        else{
            gc.drawImage(img, j*w+k, i*h+k, w, h);
        }
    }

    public void createImage(){
        
    }

    public void redraw(){
        boardData=vm.getBonus();
        Text tempText = new Text();
        tempText.setFont(Font.getDefault());
        
        
        if(boardData!=null){
            Double W= myCanvas.getWidth();
            Double H= myCanvas.getHeight();
            Double w= W / (boardData[0].length+1);
            Double h= H / (boardData.length+1);
            GraphicsContext gc= myCanvas.getGraphicsContext2D();
            Image yellowTile=null;
            Image doubleLetter=null;
            Image trippleWord= null;
            Image trippleLetter= null;
            Image doubleWord= null, middleTile=null;
            Image tileImg = null;
            try {
                yellowTile = new Image(new FileInputStream("./resources2/yellowTile.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                trippleWord = new Image(new FileInputStream("./resources2/trippleWord.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                trippleLetter = new Image(new FileInputStream("./resources2/trippleLetter.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                doubleWord = new Image(new FileInputStream("./resources2/doubleWord.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                doubleLetter = new Image(new FileInputStream("./resources2/doubleLetter.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                middleTile = new Image(new FileInputStream("./resources2/middleTile.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            gc.clearRect(0, 0, W, H);
            for(int i=0; i<boardData.length; i++){
                for(int j=0; j<boardData[i].length; j++){
                     
                    if (i==0){
                        String columnNumber = String.valueOf(j + 1);
                        double textWidth = columnNumber.length() * gc.getFont().getSize() * 0.6; // Approximate width based on font size
                        double textX = (j + 1.1) * w - textWidth / 2 +5;
                        double textY = h / 2 + gc.getFont().getSize() / 4;
                        gc.fillText(columnNumber, textX, textY);
                    }
                    if (j==0){
                        String characterLabel = String.valueOf((char) ('A' + i));
                        double textHeight = gc.getFont().getSize() * 0.6; // Approximate height based on font size
                        double textX = w / 2 - gc.getFont().getSize() / 3;
                        double textY = (i + 1) * h + textHeight / 2+10;
                        gc.fillText(characterLabel, textX, textY);
                    }
                    
                    boolean taken=false;
                     
                    if (submitFlag==true){
                        if (boardChars[i][j] != '0'){
                            String name_letter = "./resources2/" +boardChars[i][j] +".jpg";
                            try {
                                tileImg = new Image(new FileInputStream(name_letter));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            DisplayImage(gc, tileImg, i, j, w, h); 
                            taken=true;
                        }
                    }
                    if (taken==true){

                    }
                    else if((i==7 & j==7)){
                        DisplayImage(gc, middleTile, i, j, w, h);   
                    }
                    else if(boardData[i][j]==0){
                        DisplayImage(gc, yellowTile, i, j, w, h);
                    }
                    else if(boardData[i][j]==vm.vmGetTw()){
                        DisplayImage(gc, trippleWord, i, j, w, h);
                    }
                    else if(boardData[i][j]==vm.vmGetDw()){
                        DisplayImage(gc, doubleWord, i, j, w, h);

                    }
                    else if(boardData[i][j]==vm.vmGetTl()){
                        DisplayImage(gc, trippleLetter, i, j, w, h);
                    }
                    else if(boardData[i][j]==vm.vmGetDl()){
                        DisplayImage(gc, doubleLetter, i, j, w, h);
                    }
                    //else{
                     //   gc.fillRect(j*w, i*h, w, h);
                    //}
                }
            }
            
        }
        
    
    }
    @Override
    public void update(Observable o, Object arg) {
        if (o==vm){
            redraw();
        }
        // throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}