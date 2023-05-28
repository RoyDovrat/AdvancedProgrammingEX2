package ViewModel;

import java.util.Observable;
import java.util.Observer;
import java.util.function.IntPredicate;

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
    model m;
    
    public StringProperty wordInput;
    public BooleanProperty vmValidWord;
    public IntegerProperty vmScore;
    public StringProperty vmStrLetterTiles;
    public char[] vmLetterTiles;
    

    public view_model (model m){
        this.m=m;
        wordInput=new SimpleStringProperty();
        vmValidWord=new SimpleBooleanProperty();
        vmStrLetterTiles=new SimpleStringProperty();
        vmScore=new SimpleIntegerProperty();
    }

    public char[][] vmSubmitWord(int mouseRow, int mouseCol){
        return m.mSubmitWord(wordInput.get(), mouseRow, mouseCol);
    }

    public void vmLeftRightClicked(){
        m.mLeftRightClicked();
    }
    
    public void vmDownClicked(){
        m.mDownClicked();
    }

    public void vmSendGameMode(boolean isHost){
        m.mGetGameMode(isHost);
    }
    
    public char[] vmFillLetterTiles(){
        return m.fillLetterTilesFromBag();
    }
    
    public char[] vmRestartLetterTiles(){
        vmLetterTiles=m.restartLetterTiles();
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
    
    @Override
    public void update(Observable o, Object arg) {
        if(o==m){ 
            ((WritableBooleanValue) vmValidWord).set(m.getValidWord());//getScore
            (vmScore).set(m.getScore());//getScore
        }
        //throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
