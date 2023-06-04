package Model;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import Server.ClientHandler;
import Server.DictionaryManager;
import Server.MyServer;
import javafx.beans.InvalidationListener;
import Server.MainTrain.ClientHandler1;

public class model extends Observable {
    byte[][] boardData;
    Board board=new Board();
    boolean mValidWord;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();
    String mStrLetterTiles, mPlayerName;
    boolean isVertical=false;
    int mScore;
    static boolean mIsHost=false;
    boolean wordSentFlag=false;
    static String mWordInput;
    private static CountDownLatch latch;
    private Socket          socket   = null;
    //private ServerSocket    host   = null;
    private DataInputStream in       =  null;
    MyServer host;
    ArrayList<String> players = new ArrayList<String>();
    //modelGuest2 guest;

    public void initServerForGuest(int port){
        int numOfPlayers=2;
        //host=new MyServer(port, new modelGuestHandler(numOfPlayers, this), 4);
        host.start(); //server of Host
    }
    
    public void setPlayerName(String Name){
        mPlayerName=Name;
        players.add(mPlayerName);
        System.out.println("name in model: "+mPlayerName);
        //setGuest();
    }
/* 
    public void setGuest(){
        guest= new modelGuest(mPlayerName);
        initServerForGuest(5000);
        guest.createConnection(5000);

    }
   */ 
    public void setBoardData(byte[][] bonusMatrix) {
        this.boardData = bonusMatrix;
        //redraw();
    }
     
    public char[] restartLetterTiles(){ //move to modelHost
        char[] mCharLetterTiles=new char[7];
        for (int i = 0; i < letterTiles.length; i++) {
            letterTiles[i] = null;
        } 
        fillLetterTilesFromBag();
        for (int i = 0; i < letterTiles.length; i++) { //for debug
            //System.out.println(letterTiles[i].tileToString(letterTiles[i]));
            mCharLetterTiles[i]=letterTiles[i].getLetter();  
        }
        mStrLetterTiles = new String(mCharLetterTiles);
        return mCharLetterTiles;
    }
    
    public char[] fillLetterTilesFromBag(){ ////move to modelHost
        char[] mCharLetterTiles=new char[7];
        for (int i = 0; i < letterTiles.length; i++) {
            if (letterTiles[i]==null){
                letterTiles[i] = bag.getRand();
            }
        } 
        for (int i = 0; i < letterTiles.length; i++) { //for debug
            //System.out.println(letterTiles[i].tileToString(letterTiles[i]));
            mCharLetterTiles[i]=letterTiles[i].getLetter();  
        }
        return mCharLetterTiles;
    }
    
    public byte[][] getBonusBoard(){
        return board.getBonus();
    }
    
    public byte mGetDl(){
        return board.dl;
    }
    
    public byte mGetDw(){
        return board.dw;
    }
    
    public byte mGetTl(){
        return board.tl;
    }
    
    public byte mGetTw(){
        return board.tw;
    }
    
    public char[][] getBoardChars(){ //returns a matrix with the board letters and 0's where there aren't any.
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

    public void removeTilesFromLetterTiles(Word w){//remove tiles after they are used, move to hostModel
        Tile[] ts = w.getTiles();
		for(int i=0;i<ts.length;i++) {
			if(ts[i]!=null){
                //System.out.println("here");
                for(int j=0; j<letterTiles.length; j++){
                    if(letterTiles[j]!=null){
                        if ((ts[i].getLetter()==letterTiles[j].getLetter())){ //.equals(letterTiles[j])
                            letterTiles[j]=null;
                            break;
                        }
                    }
                }
            }
		}
    }
    
    public char[][] mSubmitWord(String wordInput, int mouseRow, int mouseCol) { //gets word and location and checks it, move to modelHost
        /* 
        boolean ok=true;
		Random r=new Random();
		int port=6000+r.nextInt(1000);
		MyServer s=new MyServer(port, new ClientHandler1(),3);
        s.start();
        */
        mWordInput=wordInput;
        DictionaryManager dm=DictionaryManager.get();
		
		boolean ans = dm.query("./searchFiles/alice_in_wonderland.txt",
        /* "./searchFiles/Frank Herbert - Dune.txt",*/wordInput);
        
        //mValidWord=validateWordInput(wordInput);
        System.out.println("in file query? "+ans);
        if (ans==true){
            ans=dm.challenge("./searchFiles/alice_in_wonderland.txt",
            /* "./searchFiles/Frank Herbert - Dune.txt",*/wordInput);
        }
        System.out.println("in file challange? "+ans);
        wordSentFlag=true;
        Tile[] tiles=wordInputToTiles(wordInput);
        Word newWord= new Word(tiles, mouseRow, mouseCol, isVertical);
        System.out.println("newWord.toString()");
        if(board.boardLegal(newWord) && validateWordInput(wordInput)){
            //guest.line=wordInput;
            //guest.flag=true;
            mScore= board.tryPlaceWord(newWord);
            mValidWord=true;
            removeTilesFromLetterTiles(newWord);
            fillLetterTilesFromBag();
            //guest.line="over";
        }
        
        setChanged();
        notifyObservers();
        return getBoardChars();
    }
    
    public void mLeftRightClicked(){ // in binterface
        isVertical=false;
    }
    
    public void mDownClicked(){//in interface
        isVertical=true;
    }
    
    public boolean validateWordInput(String wordInput) { //check if letters of word is in tiles, move to hostModel
        Tile[] tilesCopy = Arrays.copyOf(letterTiles, letterTiles.length);
        char[] chars = wordInput.toCharArray();
        for (char c : chars) {
            boolean found = false;
    
            // Search for a matching character in the tilesCopy array
            for (int i = 0; i < tilesCopy.length; i++) {
                if (tilesCopy[i] != null && tilesCopy[i].getLetter() == c) { // Character found in the tilesCopy array
                    found = true;
                    tilesCopy[i] = null; // Mark the tile as used by setting its value to null
                    break;
                }
            }
            if (!found) {
                return false;// If no matching character is found in the tilesCopy array, return false
            }
        }
        return true;
    }

    public Tile[] wordInputToTiles(String wordInput){ //convert string "wordInput" to tiles array, move to hostModel
        char[] charArray = wordInput.toCharArray();
        Tile[] wordTiles= new Tile[charArray.length];
        
        for (int i = 0; i < charArray.length; i++) {
            for (Tile tile : letterTiles) {
                if (tile.getLetter() == charArray[i]) {
                    wordTiles[i] = tile;
                    break;
                }
            }
        }

        // Display the contents of wordTiles
        for (Tile tile : wordTiles) {
            System.out.println("Letter: " + tile.getLetter() + ", Score: " + tile.getScore());
        }
        return wordTiles;
    }
    
    public boolean getValidWord() {  //returns if the word is valid or not, in interface
        return mValidWord;
    }
    
    public int getScore() {
        return mScore;
    }
    
    public String getMStrLetterTiles(){
        return mStrLetterTiles;
    }

    public void mInitGame() {
        //setPlayerName();
        
    }

    
}
