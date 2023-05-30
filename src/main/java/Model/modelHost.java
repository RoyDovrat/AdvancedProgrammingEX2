package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

public class modelHost extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName, mStrLetterTiles, line = "";
    Socket server;
    boolean flag=false, mIsHost=true, isVertical=false, mValidWord;
    Board board=new Board();
    byte[][] boardData;
    int mScore;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();

    /* 
    public modelHost(String name){
        this.playerName=name;
    }
    */
    /* 
    public void createConnection(int port){
        try {
            port=5000;
			server=new Socket("localhost",port);
			out=new PrintWriter(server.getOutputStream());
			in=new Scanner(server.getInputStream());

            
			out.println(playerName);
			out.flush();
			String res=in.nextLine(); //get server-welcome
            System.out.println("model host input"+ res);
			//in.close();
			//out.close();
			//server.close();
		} catch (IOException e) {
			System.out.println("your code ran into an IOException (-10)");
		}
         
        
        try {
            in.close();
            out.close();
            server.close();
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }
    */

    public char[] restartLetterTiles(){ 
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

    public char[] fillLetterTilesFromBag(){ 
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
    
    public void removeTilesFromLetterTiles(Word w){//remove tiles after they are used
        Tile[] ts = w.getTiles();
		for(int i=0;i<ts.length;i++) {
			if(ts[i]!=null){
                //System.out.println("here");
                for(int j=0; j<letterTiles.length; j++){
                    if(letterTiles[j]!=null){
                        if ((ts[i].getLetter()==letterTiles[j].getLetter())){ 
                            letterTiles[j]=null;
                            break;
                        }
                    }
                }
            }
		}
    }
    
    public Tile[] wordInputToTiles(String wordInput){ //convert string "wordInput" to tiles array
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
    
    public boolean validateWordInput(String wordInput) { //check if letters of word is in tiles
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
    
    @Override
    public void setPlayerName(String Name) { //send name to host
        mPlayerName=Name;
        System.out.println("name in model: "+mPlayerName);
    }

    @Override
    public void mGetGameMode(boolean isHost) {
        mIsHost=isHost; //check, problematic
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

}
