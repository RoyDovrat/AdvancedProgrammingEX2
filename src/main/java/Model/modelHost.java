package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import Server.ClientHandler;
import Server.DictionaryManager;
import javafx.beans.InvalidationListener;

public class modelHost extends Observable implements interfaceModel {
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
    ArrayList<String> players = new ArrayList<String>();

    int port;
    boolean stop;
    ClientHandler ch;
    int MaxThreads;
    ExecutorService executor;
    Set<Socket> sockets;

    public modelHost (int port, modelGuestHandler ch, int MaxThreads) {
        this.port=port;
        this.ch=ch;
        ch.setHost(this);
        this.MaxThreads=MaxThreads;
        this.executor=Executors.newFixedThreadPool(MaxThreads);
        this.sockets = new HashSet<>();
    }
    
    @Override
    public void start()  {
        stop= false;

        new Thread(() -> {
			try {
				startServer(ch);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
    }
    
    private void startServer(ClientHandler chNew) throws IOException{
        try {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);
            while(!stop) {
                try {
                    Socket client=server.accept();
                    sockets.add(client);
                    executor.execute(() -> 
                        {
                            try {
                            	 Class<? extends ClientHandler> chClass = this.ch.getClass();
                                 try {
									//ClientHandler chNew = chClass.getDeclaredConstructor().newInstance();
                        
									chNew.handleClient(client.getInputStream(), client.getOutputStream());
									
									client.close();
								} catch (IllegalArgumentException| SecurityException e) {
									e.printStackTrace();
								}
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                } catch(SocketTimeoutException e) {}
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            for (Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public void hello(){
        System.out.println("hello yanai");
    }

    public String restartLetterTiles(){ 
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
        
        return mStrLetterTiles;
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
    
    public char[][] checkWord(String wordInput, int mouseRow, int mouseCol) { //gets word and location and checks it, move to modelHost

        mWordInput=wordInput;
        //DictionaryManager dm=DictionaryManager.get();
		
		//boolean ans = dm.query("./searchFiles/alice_in_wonderland.txt",
        /* "./searchFiles/Frank Herbert - Dune.txt",*///wordInput);
        
        //mValidWord=validateWordInput(wordInput);
        //System.out.println("in file query? "+ans);
        //if (ans==true){
        //    ans=dm.challenge("./searchFiles/alice_in_wonderland.txt",
            /* "./searchFiles/Frank Herbert - Dune.txt",*///wordInput);
        //}
        //System.out.println("in file challange? "+ans);
        wordSentFlag=true;
        Tile[] tiles=wordInputToTiles(wordInput);
        Word newWord= new Word(tiles, mouseRow, mouseCol, isVertical);
        System.out.println(newWord.toString());
        if(board.boardLegal(newWord) && validateWordInput(wordInput)){
            //guest.line=wordInput;
            //guest.flag=true;
            mScore= board.tryPlaceWord(newWord);
            //mValidWord=true;
            //removeTilesFromLetterTiles(newWord);
            //fillLetterTilesFromBag();
            //guest.line="over";
        }
        
        setChanged();
        notifyObservers();
        return getBoardChars();
    }
    
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
        char[][] result= checkWord(wordInput, mouseRow, mouseCol);
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                System.out.print(result[i][j]);
            }
            System.out.println("");
        }
        return result;
    }

    @Override
    public char[] mRequestFillLetterTiles() {
        return fillLetterTilesFromBag();
    }

    @Override
    public char[] mRequestRestartLetterTiles() {
        return restartLetterTiles().toCharArray();
    }

}
