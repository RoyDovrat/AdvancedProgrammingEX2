package ViewModel;

import java.security.PublicKey;
import java.util.Observable;
import java.util.Observer;
import java.util.function.IntPredicate;

import Model.interfaceModel;
import Model.model;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableBooleanValue;

public class view_model extends Observable implements Observer{
    interfaceModel m;
    
    public StringProperty wordInput;
    public BooleanProperty vmValidWord;
    public StringProperty vmCurrentPlayer;
    public IntegerProperty vmPortNum;
    public StringProperty vmStrLetterTiles;
    public StringProperty vmPlayerName;
    public char[] vmLetterTiles;
    public char[][] boardChars;
    int[] scores=  new int[4];

    boolean newGameStarted=false;
    public String hostName;
    

    public view_model (interfaceModel m2){
        this.m=m2;
        ((Observable) m).addObserver(this);
        wordInput=new SimpleStringProperty();
        vmValidWord=new SimpleBooleanProperty();
        vmStrLetterTiles=new SimpleStringProperty();
        vmCurrentPlayer=new SimpleStringProperty();
        vmPlayerName=new SimpleStringProperty();
    }
    public boolean getNewGameStarted(){
        return this.newGameStarted;
    }
    public void setNewGameBoardStarted(boolean flag){
        this.newGameStarted=flag;
    }
    public char[][] vmSubmitWord(String nowPlayingName, int mouseRow, int mouseCol){
        System.out.println("in viewmodel "+wordInput.get());
        String getWordIn = wordInput.get();
        return m.mSubmitWord(nowPlayingName, getWordIn, mouseRow, mouseCol);
    }

    public void vmLeftRightClicked(){
        m.mLeftRightClicked();
    }
    public void vmOnePlayerClicked(){
        m.mOnePlayerClicked();
    }
    public void vmTwoPlayersClicked(){
        m.mTwoPlayersClicked();
    }
    public void vmThreePlayersClicked(){
        m.mThreePlayersClicked();
    }
    public void vmFourPlayersClicked(){
        m.mFourPlayersClicked();
    }
    public void vmDownClicked(){
        m.mDownClicked();
    }
  
    public char[] vmRequestFillLetterTiles(String nowPlayingName){
        return m.mRequestFillLetterTiles(nowPlayingName);
    }
    
    public char[] vmRequestRestartLetterTiles(String nowPlayingName){
        vmLetterTiles=m.mRequestRestartLetterTiles(nowPlayingName);
        return vmLetterTiles;
    }

    public byte[][] getBonus(){
        return m.getBonusBoard();
    }
    
    public byte vmGetDl(){
        return m.mGetDl();
    }
    
    public byte vmGetDw(){
        return m.mGetDw();
    }
    
    public byte vmGetTl(){
        return m.mGetTl();
    }
    
    public byte vmGetTw(){
        return m.mGetTw();
    }
    
    public void vmSetPlayerName(){
        m.setPlayerName(vmPlayerName.get());
    }

    public void vmsetStart(){
        m.msetStart();
    }

    public void stopGame(){
        m.stopGame();
    }


    public boolean vmCheckIfEnoughPlayers(){
        return m.mCheckIfEnoughPlayers();
    }

    public char[][] vmGetCurrentBoard(){
        boardChars=m.getBoardChars();
        return boardChars;
    }
    public String getPlayersName(){
        return m.getPlayersName();
    }
    public int[] vmUpdateScores(){
        return m.getScores();
    }

    public int[] getScores(){
        return m.getScores();
    }
    
    public String vmGetCurrentPlayer(){
        return m.getCurrentPlayer();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==m){ 
            
            if(arg.equals("startNewGame")){
                newGameStarted=true;
                setChanged();
                notifyObservers("startNewGame");
            }
            if (arg.equals("resumeMode")){
                setChanged();
                notifyObservers("startNewGame");
            }
            if(arg.equals("updatedBoard")){
                boardChars=m.getBoardArray();
                System.out.print("Debugging board for update (in view_model):");
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        System.out.print(boardChars[i][j]);
                    }
                    System.out.print("\n");
                } 
                setChanged();
                notifyObservers("updatedBoard");
            }
            if(arg.equals("hostName")){
                hostName=m.getHostName();
                System.out.println("name of host in view_model: "+hostName);
                setChanged();//HERE
                notifyObservers("hostName");
            }
            if(arg.equals("BoardFromHost")){
                vmGetCurrentBoard();
                setChanged();//HERE
                notifyObservers("BoardFromHost");
            }
            if(arg.equals("ValidWord")){
                boolean isValid = m.getValidWord();
                ((WritableBooleanValue) vmValidWord).set(isValid);//getScore
                setChanged();//HERE
                notifyObservers("ValidWord");
            }
            if(arg.equals("NotValidWord")){
                boolean isValid = m.getValidWord();
                ((WritableBooleanValue) vmValidWord).set(isValid);//getScore
                setChanged();//HERE
                notifyObservers("NotValidWord");
            }
            scores=vmUpdateScores();
            String currentPlayer = m.getCurrentPlayer();
            (vmCurrentPlayer).set(currentPlayer);//getScore
            setChanged();//HERE
            notifyObservers(" ");
        }
    }

    public void vmSkipTurn() {
        m.mSkipTurn();  
    }


}
