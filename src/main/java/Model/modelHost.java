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
import java.util.concurrent.Semaphore;

import com.example.App;

import Server.ClientHandler;
import Server.DictionaryManager;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import java.util.HashMap;
import java.util.Map;

public class modelHost extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName, mStrLetterTiles, line = "", mWordInput, currentPlayer;
    Socket server;
    boolean flag=false, mIsHost=true, isVertical=false, mValidWord, wordSentFlag, tryAgain = false;
    boolean stop;
    Board board=new Board();
    byte[][] boardData;
    int mScore, port, MaxThreads, numOfPlayers = 0, numPlayersChosen=1;
    Tile[][] letterTiles = new Tile[4][7];
    int[] score= new int[4];
    Tile.Bag bag = new Tile.Bag();
    ArrayList<String> players = new ArrayList<String>();
    private int currentTurnIndex = 0;
    private Semaphore turnSemaphore;
    ClientHandler ch;
    ExecutorService executor;
    Set<Socket> sockets;
    Map<String, Integer> nameNumberMap = new HashMap<>();
    char[][] boardChars= new char[15][15];

    public modelHost (int port, modelGuestHandler ch, int MaxThreads) {
        this.port=port;
        this.ch=ch;
        ch.setHost(this);
        this.MaxThreads=MaxThreads;
        this.executor=Executors.newFixedThreadPool(MaxThreads);
        this.sockets = new HashSet<>();
        this.currentTurnIndex = 0;
        this.turnSemaphore = new Semaphore(1); 
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

    public int getCurrentPlayerIndex(String name){
        return nameNumberMap.get(name);
    }

  //number of players
    @Override
    public boolean mCheckIfEnoughPlayers(){
            if(this.numOfPlayers < this.numPlayersChosen){
                System.out.println("numOfPlayers:"+numOfPlayers);
                return false;
            }
            return true;
    }
    @Override
    public void mOnePlayerClicked(){ 
        this.numPlayersChosen = 1;}
    @Override
    public void mTwoPlayersClicked(){ 
        this.numPlayersChosen = 2;}
    @Override
    public void mThreePlayersClicked(){
        this.numPlayersChosen = 3;}
    @Override
    public void mFourPlayersClicked(){
        this.numPlayersChosen = 4;}
  
    @Override
    public void msetStart(){
        
    }

    public String restartLetterTiles(String nowPlayingName){ 
        char[] mCharLetterTiles=new char[7];
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        for (int i = 0; i < letterTiles[playerNum].length; i++) {
            letterTiles[playerNum][i] = null;
        } 
        fillLetterTilesFromBag(nowPlayingName);
        for (int i = 0; i < letterTiles[playerNum].length; i++) { //for debug
            mCharLetterTiles[i]=letterTiles[playerNum][i].getLetter();  
        }
        mStrLetterTiles = new String(mCharLetterTiles);
        return mStrLetterTiles;
    }

    public String fillLetterTilesFromBag(String nowPlayingName){ 
        char[] mCharLetterTiles=new char[7];
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        for (int i = 0; i < letterTiles[playerNum].length; i++) {
            if (letterTiles[playerNum][i]==null){
                letterTiles[playerNum][i] = bag.getRand();
            }
        } 
        for (int i = 0; i < letterTiles[playerNum].length; i++) { //for debug
            mCharLetterTiles[i]=letterTiles[playerNum][i].getLetter();  
        }
        mStrLetterTiles = new String(mCharLetterTiles);
        return mStrLetterTiles;
    }
    
    public void removeTilesFromLetterTiles(String nowPlayingName, Word w){//remove tiles after they are used
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        Tile[] ts = w.getTiles();
		for(int i=0;i<ts.length;i++) {
			if(ts[i]!=null){
                for(int j=0; j<letterTiles[playerNum].length; j++){
                    if(letterTiles[playerNum][j]!=null){
                        if ((ts[i].getLetter()==letterTiles[playerNum][j].getLetter())){ 
                            letterTiles[playerNum][j]=null;
                            break;
                        }
                    }
                }
            }
		}
    }
    
    public Tile[] wordInputToTiles(String nowPlayingName, String wordInput){ //convert string "wordInput" to tiles array

        char[] charArray = wordInput.toCharArray();
        Tile[] wordTiles= new Tile[charArray.length];
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        for (int i = 0; i < charArray.length; i++) {
            for (Tile tile : letterTiles[playerNum]) {
                System.out.println("Tile: "+tile.letter+"lettertiles: " +letterTiles[playerNum].toString());
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
      

    // Method to determine the next player's turn
    public void nextPlayerTurn() {
        if(!this.tryAgain){
            if(players.size() == 0){return;}
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
            //App.nowPlaying = players.get(currentTurnIndex);
            currentPlayer=players.get(currentTurnIndex);
            Platform.runLater(() -> {
                setChanged();
                notifyObservers();
            });
                                
            // setChanged();
            // notifyObservers();
            System.out.println("corrent player is  "+currentPlayer);
        }
    }
    
    // Method to notify the current player that it's their turn
    public String CurrentPlayer() {
        System.out.println("model host- current player");
        System.out.println("currentTurnIndex"+currentTurnIndex);
        System.out.println("players"+players.toString());
        return players.get(currentTurnIndex);
 
    }
    
    public String getCurrentPlayer(){
        return this.currentPlayer;
    }

    private String findPlayerByName(ArrayList<String> players, String playerName) {
        for (String player : players) {
            if (player.equals(playerName)) {
                return player;
            }
        }
        return null; // Player not found
    }

    public boolean validateWordInput(String nowPlayingName, String wordInput) { //check if letters of word is in tiles
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        Tile[] tilesCopy = Arrays.copyOf(letterTiles[playerNum], letterTiles[playerNum].length);
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
    
    public int[] getScores(){
        return this.score;
    }
    
    public char[][] checkWord(String nowPlayingName ,String wordInput, int mouseRow, int mouseCol) { //gets word and location and checks it, move to modelHost
        if(wordInput == null){
            return getBoardChars();
        }
        
        mWordInput=wordInput;
        wordSentFlag=true;
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        Tile[] tiles=wordInputToTiles(nowPlayingName ,wordInput);
        Word newWord= new Word(tiles, mouseRow, mouseCol, isVertical);
        System.out.println(newWord.toString());
        if(board.boardLegal(newWord) && validateWordInput(nowPlayingName, wordInput)){
            score[playerNum]+= board.tryPlaceWord(newWord);
            System.out.println("score in model host= "+score[playerNum]);
            removeTilesFromLetterTiles(nowPlayingName, newWord);
            tryAgain = false;
            setChanged();
            notifyObservers();
        }
        else{
            //next player is current player
            tryAgain = true;
        }
        return getBoardChars();
    }
    
    
    public void QueuesOrder(){
        //while
        //send current player
        //wait for response- finish

        
    }

    @Override
    public void setPlayerName(String Name) { //send name to host
        mPlayerName=Name;
        nameNumberMap.put(Name,numOfPlayers );
        players.add(Name);
        System.out.println("name in model: " + mPlayerName + " player number: " + numOfPlayers);
        numOfPlayers = numOfPlayers +1;
    }
    
    @Override
    public byte[][] getBonusBoard() {
        return board.getBonus();
    }
    
    @Override
    public char[][] getBoardChars() { //returns a matrix with the board letters and 0's where there aren't any.
        boardData=board.getBonus();
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

    public void SetBoardChars(char[][] updatedBoard){
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                
            }
        }
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
        //throw new UnsupportedOperationException("Unimplemented method 'addListener'");
    }

    @Override
    public void removeListener(InvalidationListener arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeListener'");
    }

    @Override
    public char[][] mSubmitWord(String nowPlayingName, String wordInput, int mouseRow, int mouseCol) {
        System.out.println("in model "+wordInput);
        //char[][] result= checkWord(nowPlayingName, wordInput, mouseRow, mouseCol);
        boardChars=checkWord(nowPlayingName, wordInput, mouseRow, mouseCol);
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                System.out.print(boardChars[i][j]);
            }
            System.out.println("");
        }
        nextPlayerTurn();//update next player
        
    //send board And next player
        
        return boardChars;
    }

    @Override
    public char[] mRequestFillLetterTiles(String nowPlayingName) {
        return fillLetterTilesFromBag(nowPlayingName).toCharArray();
    }

    @Override
    public char[] mRequestRestartLetterTiles(String nowPlayingName) {
        return restartLetterTiles(nowPlayingName).toCharArray();
    }

    @Override
    public void mSkipTurn() {
        nextPlayerTurn();
    }

    @Override
    public String getPlayersName() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            result.append(players.get(i));
            
            if (i < players.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    @Override
    public String CurrentPlayerName() {
        if (currentTurnIndex==0){
            return players.get(0);
        }
        System.out.println("Curren player name at model host "+ getCurrentPlayer());
        return getCurrentPlayer();
    }

}
