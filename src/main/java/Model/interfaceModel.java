package Model;

import javafx.beans.Observable;

public interface interfaceModel extends Observable  {
    //player info:
    public void setPlayerName(String Name);
    //public void mGetGameMode(boolean isHost); 

    // for printing board: 
    public byte[][] getBonusBoard();
    public char[][] getBoardChars();
    public byte mGetDl();
    public byte mGetDw();
    public byte mGetTl();
    public byte mGetTw();

    //for direction of word:
    public void mLeftRightClicked();
    public void mDownClicked();

    //for submitting word:
    public char[][] mSubmitWord(String wordInput, int mouseRow, int mouseCol);
    public char[] mRequestFillLetterTiles();
    public char[] mRequestRestartLetterTiles();

    //displaying player data on board
    public boolean getValidWord();
    public int getScore();
    public String getMStrLetterTiles(); 
    
}
