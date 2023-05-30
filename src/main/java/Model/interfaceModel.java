package Model;

public interface interfaceModel {
    //player info:
    public void setPlayerName(String Name);
    public void mGetGameMode(boolean isHost); 

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

    //displaying player data on board
    public boolean getValidWord();
    public int getScore();
    public String getMStrLetterTiles();

    
    
}
