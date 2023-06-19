package com.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    int cCol, cRow, numOfPlayersChosen;
    char[] vLetterTiles;
    boolean vertical, submitFlag=false, isHost;
    public int mouseRow, port_num;
    public int mouseCol;
    String myName;

    @FXML
    String vStrLetterTiles;
    @FXML
    Button submitButton,  SubmitNameB, SkipTurnButton;
  
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
    Button startGameButton;

    @FXML
    Label ColBoard, RowBoard;
    
    @FXML
    Label CurrentPlayer, NoNameError;
    @FXML 
    TextField PlayerName;
    @FXML
    MenuButton GameMode;
    
    @FXML
    TableView<String> scoreTable;
    @FXML
    TableColumn<String, String> nameColumn;

    ArrayList<String> playerNames= new ArrayList<String>();; // ArrayList of player names
    ArrayList<Integer> playerScores= new ArrayList<Integer>();; // ArrayList of player scores


    public BoardDisplayer(/*boolean is_host*/){
        cCol=0;
        cRow=1;
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

    @FXML
    public void SkipTurn(){
        vm.vmSkipTurn();
    }
    
    public void setViewModel(view_model vm) {
        this.vm=vm;
        vm.wordInput.bind(vWordInput.textProperty());//הלוך
        vm.vmPlayerName.bind(PlayerName.textProperty()); 
        validWord.textProperty().bind((vm.vmValidWord.asString()));//חזור
        //CurrentPlayer.textProperty().bind(vm.vmCurrentPlayer);
        // StringProperty CurrentPlayerText = new SimpleStringProperty();
        //CurrentPlayer.textProperty().bind(Bindings.concat("Player: ", vm.vmCurrentPlayer));
        // CurrentPlayer.textProperty().bind(CurrentPlayerText);
    }   
  
    public void setCharacterPosition(int row, int col){
        cCol=col;
        cRow=row;
        redraw();
    }
    
    public void submitNameFunc(){
        this.myName = PlayerName.getText();
        vm.vmSetPlayerName();
        System.out.println("submit pressed");
        if(PlayerName==null){
            NoNameError.setText("Enter Name");
        }
    }
    
    public int getcCol() {
        return cCol;
    }

    public int getcRow() {
        return cRow;
    }
   
    public void shutStartButton(){
        this.startGameButton.setVisible(false);
        this.PlayerName.setVisible(false);
    }

    @FXML
    public void setNewGameBoard(){
        //while(this.NumberOfPlayers)
    
        if (myName!=null){
           
            while(!vm.vmCheckIfEnoughPlayers()){
                System.err.println("not enough players connected");
            } 
            vm.vmsetStart();
            vWordInput.setVisible(true);
            validWord.setVisible(true);
            TileDirection.setVisible(true);
            RowBoard.setVisible(true);
            ColBoard.setVisible(true);
            submitButton.setVisible(true);
            CurrentPlayer.setVisible(true);
            scoreTable.setVisible(true);
            SubmitNameB.setVisible(false);
            redraw();
            getPlayersName();
            vLetterTiles=vm.vmRequestRestartLetterTiles(this.myName);
            drawLetterTiles(vLetterTiles);
            shutStartButton();
            SkipTurnButton.setVisible(true);
        }
        else{
            NoNameError.setText("Enter Name");
        }

    }
    
    public void getPlayersName(){
        String namses= vm.getPlayersName();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        
        separateNames(namses);
        
        ObservableList<String> items = FXCollections.observableArrayList(playerNames);
        scoreTable.setItems(items);
        
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        //scoreColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(0).asObject());
        scoreTable.setFixedCellSize(40); // Set a fixed height for each table row
        scoreTable.prefHeightProperty().bind(Bindings.size(scoreTable.getItems()).multiply(scoreTable.getFixedCellSize()).add(30)); // Adjust the table height based on the number of rows
    }
    
    public void updatePlayerScores(int[] scores) {
        if (scores.length == playerNames.size()) {
            if (playerScores == null) {
                playerScores = new ArrayList<>();
            } else {
                playerScores.clear();
            }

            for (int i = 0; i < scores.length; i++) {
                playerScores.add(scores[i]);
            }
        }
    }
  
    public void updateScores(int[] scores) {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            int score = scores[i];
            String item = playerName + "             " + score;
            items.add(item);
        }

        scoreTable.setItems(items);
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
        boardChars=vm.vmSubmitWord(this.myName, mouseRow,mouseCol); 
        for(int i=0; i<boardChars[0].length; i++){
            for(int j=0; j<boardChars.length; j++){   
            }   
        }
        updateScores(vm.getScores());
        submitFlag=true;
        drawLetterTiles(vm.vmRequestFillLetterTiles(this.myName));
        redraw();
        vWordInput.setText(""); 
    }
    @FXML
    public void LeftRightClicked(){
        TileDirection.setText("Horizontal");
        
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

    public void separateNames(String names) {
        String[] nameArray = names.split(",");
        for (String name : nameArray) {
            String trimmedName = name.trim(); // Remove leading/trailing whitespace if needed
            playerNames.add(trimmedName);
        }
    }

    public void initialize() {
        vWordInput.setVisible(false);
        validWord.setVisible(false);
        TileDirection.setVisible(false);
        RowBoard.setVisible(false);
        ColBoard.setVisible(false);
        submitButton.setVisible(false);
        CurrentPlayer.setVisible(false);
        SkipTurnButton.setVisible(false);
        scoreTable.setVisible(false);
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
                }
            }  
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (o==vm){
            boardChars=vm.boardChars;
            int[] scores= vm.getScores();
            //updatePlayerScores(scores);
            updateScores(scores);
            scoreTable.refresh();
            redraw();
        }
    }
    
}