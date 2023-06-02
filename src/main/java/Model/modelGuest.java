package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

import Server.DictionaryManager;
import javafx.beans.InvalidationListener;

public class modelGuest extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName, mStrLetterTiles, line = "", mWordInput;
    Socket server;
    boolean flag=false, mIsHost=true, isVertical=false, mValidWord, wordSentFlag;
    Board board=new Board();
    byte[][] boardData;
    int mScore;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();

    @Override
    public void setPlayerName(String Name) { //send name to host
        mPlayerName=Name;
        System.out.println("name in model: "+mPlayerName);
    }
    
    @Override
    public byte[][] getBonusBoard() {
        return board.getBonus();
    }
    
    @Override
    public char[][] getBoardChars() { //returns a matrix with the board letters and 0's where there aren't any.
        boardData=board.getBonus();
        char[][] boardChars= new char[15][15];
        Tile[][] copyBoard=board.getTiles();
        for(int i=0; i<boardData.length; i++){
            for(int j=0; j<boardData.length; j++){
                if(copyBoard[i][j]!=null){
                    boardChars[i][j]=copyBoard[i][j].getLetter();
                }
                else{
                    boardChars[i][j]='0';
                }
            }
        }
        return boardChars;
    }
    
    @Override
    public byte mGetDl() {
        return board.dl;
    }
    @Override
    public byte mGetDw() {
        return board.dw;
    }
    @Override
    public byte mGetTl() {
        return board.tl;
    }
    @Override
    public byte mGetTw() {
        return board.tw;
    }
    
    @Override
    public void mLeftRightClicked() {
        isVertical=false;
    }
    @Override
    public void mDownClicked() {
        isVertical=true;
    }
    
    @Override
    public boolean getValidWord() {
        return mValidWord;
    }
    
    @Override
    public int getScore() {
        return mScore;
    }

    @Override
    public String getMStrLetterTiles() {
        return mStrLetterTiles;
    }

    @Override
    public void addListener(InvalidationListener arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addListener'");
    }

    @Override
    public void removeListener(InvalidationListener arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeListener'");
    }

    @Override
    public char[][] mSubmitWord(String wordInput, int mouseRow, int mouseCol) {
        System.out.println("in model "+wordInput);
        char[][] result= new char[15][15];
        return result;
    }

    @Override
    public char[] mRequestFillLetterTiles() {
        char[] res= new char[7];
        return res;
    }

    @Override
    public char[] mRequestRestartLetterTiles() {
        char[] res= new char[7];
        return res;
    }

}
