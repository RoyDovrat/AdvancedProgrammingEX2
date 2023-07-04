package Model;

import javafx.beans.Observable;

public interface interfaceModel extends Observable  {
    //player info:
    public void setPlayerName(String Name);
    public void msetStart();
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

    //number of players
    public boolean mCheckIfEnoughPlayers();
    public void mOnePlayerClicked();
    public void mTwoPlayersClicked();
    public void mThreePlayersClicked();
    public void mFourPlayersClicked();

    //for submitting word:
    public char[][] mSubmitWord(String nowPlayingName, String wordInput, int mouseRow, int mouseCol);
    public char[] mRequestFillLetterTiles(String nowPlayingName);
    public char[] mRequestRestartLetterTiles(String nowPlayingName);

    //displaying player data on board
    public boolean getValidWord();
    public int getScore();
    public String getMStrLetterTiles();
    public int[] getScores();

    public void start(); 
    public void mSkipTurn();
    public String getPlayersName();
    public String CurrentPlayerName();
    public String getCurrentPlayer();
    public char[][] getBoardArray();
    public String getHostName();
    public void stopGame();
}
