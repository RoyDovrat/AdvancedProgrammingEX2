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
    public IntegerProperty vmScore;
    public IntegerProperty vmPortNum;
    public StringProperty vmStrLetterTiles;
    public StringProperty vmPlayerName;
    public char[] vmLetterTiles;
    public char[][] boardChars;
    

    public view_model (interfaceModel m2){
        this.m=m2;
        ((Observable) m).addObserver(this);
        wordInput=new SimpleStringProperty();
        vmValidWord=new SimpleBooleanProperty();
        vmStrLetterTiles=new SimpleStringProperty();
        vmScore=new SimpleIntegerProperty();
        vmPlayerName=new SimpleStringProperty();
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


    public boolean vmCheckIfEnoughPlayers(){
        return m.mCheckIfEnoughPlayers();
    }

    public void vmGetCurrentBoard(){
        boardChars=m.getBoardChars();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==m){ 
            vmGetCurrentBoard();
            boolean isValid = m.getValidWord();
            ((WritableBooleanValue) vmValidWord).set(isValid);//getScore
            int score = m.getScore();
            (vmScore).set(score);//getScore
            setChanged();
            notifyObservers();;
        }
    }

    public void vmSkipTurn() {
        m.mSkipTurn();
    }


}
