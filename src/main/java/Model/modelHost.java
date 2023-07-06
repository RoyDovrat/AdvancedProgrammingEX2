package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.example.App;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import Server.BookScrabbleHandler;
import Server.ClientHandler;
import Server.DictionaryManager;
import Server.MyServer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import java.util.HashMap;
import java.util.Map;

public class modelHost extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName;
    String mStrLetterTiles;
    String line = "";
    String mWordInput;
    String currentPlayer;
    Socket server;
    boolean flag=false, mIsHost=true, isVertical=false, mValidWord, wordSentFlag, newGameFlag;
    public boolean tryAgain = false, testMode=false;
    boolean stop;
    Board board=new Board();
    byte[][] boardData;
    int mScore, port, MaxThreads;
    int numOfPlayers = 0;
    int numPlayersChosen=1;
    public Tile[][] letterTiles = new Tile[4][7];
    int[] score= new int[4];
    Tile.Bag bag = new Tile.Bag();
    public ArrayList<String> players = new ArrayList<String>();
    private int currentTurnIndex = 0;
    private Semaphore turnSemaphore;
    ClientHandler ch;
    ExecutorService executor;
    Set<Socket> sockets;
    Map<String, Integer> nameNumberMap = new HashMap<>();
    char[][] boardChars= new char[15][15];
    public static String collectionName = "ap";
    String boardAsString;

    //Resume Variables:
    String ResumeCurrentPlayer;

    public modelHost (int port, modelGuestHandler ch, int MaxThreads, boolean newGame) {
        this.newGameFlag=newGame;
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
    public void close() {
        stop=true;
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
        try {
            updateGuests("startNewGame");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            updateGuests("host,"+players.get(0));
            System.out.println("name of host in mhost: "+players.get(0));
            setChanged();
            notifyObservers("hostName");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!newGameFlag){
            setChanged();
            notifyObservers("resumeMode");
            try {
                updateGuests("resumeMode");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            retrieveFromDB(port);
        }
    }
    public String getHostName(){
        return this.players.get(0);
    }

    public char[][] getBoardArray(){
        return this.boardChars;
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

    public String fillLetterTilesFromBagResumeMode(String nowPlayingName, String tileString) {
        char[] mCharLetterTiles = new char[7];
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        String[] tiles = tileString.split(",");

        for (int i = 0; i < letterTiles[playerNum].length; i++) {
            if (letterTiles[playerNum][i] == null && i < tiles.length) {
                letterTiles[playerNum][i] = bag.getTile(tiles[i].charAt(0));
            }
        }

        for (int i = 0; i < letterTiles[playerNum].length; i++) { // for debug
            mCharLetterTiles[i] = letterTiles[playerNum][i].getLetter();
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
                notifyObservers(" ");
            });
            try {
                updateGuests("currentPlayer,"+currentPlayer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }                    
            System.out.println("corrent player is  "+currentPlayer);
        }
    }

    public void updateGuests(String newUpdate) throws IOException{
        for(Socket client: sockets){
            out=new PrintWriter(client.getOutputStream());
            out.println(newUpdate);
            out.flush();
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
        //System.out.println(newWord.toString());
        if(board.boardLegal(newWord) && validateWordInput(nowPlayingName, wordInput)){
            score[playerNum]+= board.tryPlaceWord(newWord);
            //System.out.println("score in model host= "+score[playerNum]);
            removeTilesFromLetterTiles(nowPlayingName, newWord);
            tryAgain = false;
        }
        else{
            //next player is current player
            tryAgain = true;
        }
        return getBoardChars();
    }

    @Override
    public void setPlayerName(String Name) { //send name to host
        mPlayerName=Name;
        nameNumberMap.put(Name,numOfPlayers );
        players.add(Name);
        //System.out.println("name in model: " + mPlayerName + " player number: " + numOfPlayers);
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

    public static boolean runClient(int port,String query) {
		try {
			Socket server=new Socket("localhost",port);
			PrintWriter out=new PrintWriter(server.getOutputStream());
			Scanner in=new Scanner(server.getInputStream());
			out.println(query);
			out.flush();
			String res=in.next();
           
			in.close();
			out.close();
			server.close();
            if(res.equals("true")){
                return true;
            }
            return false;
		} catch (IOException e) {
            //e.printStackTrace();
		}
        return false;

	}
    
    public boolean CheckWordInTiles(String nowPlayingName,String wordInput) { //check if letters of word is in tiles
        Tile[] tilesCopy = Arrays.copyOf(letterTiles[getCurrentPlayerIndex(nowPlayingName)], letterTiles[0].length);
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

    public boolean checkWordInServer(String wordInput){
        Random r=new Random();
		int port=6000+r.nextInt(1000);
        
		MyServer s=new MyServer(port, new BookScrabbleHandler(),2);
		s.start();
		boolean inDictionary= runClient(port, "Q,./searchFiles/Frank Herbert - Dune.txt,"+
        "./searchFiles/alice_in_wonderland.txt,./searchFiles/Harray Potter.txt,"+
        "./searchFiles/mobydick.txt,./searchFiles/pg10.txt,./searchFiles/shakespeare.txt,"+
        "./searchFiles/The Matrix.txt,"+wordInput);
        if (inDictionary==true){
            inDictionary=runClient(port, "C,./searchFiles/Frank Herbert - Dune.txt,"+
             "./searchFiles/alice_in_wonderland.txt,./searchFiles/Harray Potter.txt,"+
             "./searchFiles/mobydick.txt,./searchFiles/pg10.txt,./searchFiles/shakespeare.txt,"+
             "./searchFiles/The Matrix.txt,"+wordInput);
            //System.out.println("in file challange? "+inDictionary);
        }
        try {Thread.sleep(2000);} catch (InterruptedException e) {}
        
        s.close(); //changed
        return inDictionary;
    }
    
    public char[][] checkWord2(String nowPlayingName, String wordInput, int mouseRow, int mouseCol) { //gets word and location and checks it, move to modelHost
        if(wordInput == null){
            System.out.println("word is null");
            tryAgain=true;
            if(!testMode){
                mValidWord=false;
                setChanged();
                notifyObservers("NotValidWord");
                try {
                    updateGuests("NotValidWord");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return getBoardChars();
        }
        if (!CheckWordInTiles(nowPlayingName, wordInput.toUpperCase())){
            tryAgain=true;
            mValidWord=false;
            System.out.println("word not in tiles");
            if(!testMode){
                setChanged();
                notifyObservers("NotValidWord");
                try {
                    updateGuests("NotValidWord");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return getBoardChars();
            }
            //send messege to guest- tiles incorrect
        }
        mWordInput=wordInput;
        int playerNum = getCurrentPlayerIndex(nowPlayingName);
        //query, challange, if board legal, if word in tiles

        //check in dictionary
        /* 
		Random r=new Random();
		int port=6000+r.nextInt(1000);
		MyServer s=new MyServer(port, new BookScrabbleHandler(),1);
		s.start();
		boolean inDictionary= runClient(port, "Q,./searchFiles/Frank Herbert - Dune.txt,"+
        "./searchFiles/alice_in_wonderland.txt,./searchFiles/Harray Potter.txt,"+
        "./searchFiles/mobydick.txt,./searchFiles/pg10.txt,./searchFiles/shakespeare.txt,"+
        "./searchFiles/The Matrix.txt,"+wordInput);
        if (inDictionary==true){
            inDictionary=runClient(port, "C,./searchFiles/Frank Herbert - Dune.txt,"+
             "./searchFiles/alice_in_wonderland.txt,./searchFiles/Harray Potter.txt,"+
             "./searchFiles/mobydick.txt,./searchFiles/pg10.txt,./searchFiles/shakespeare.txt,"+
             "./searchFiles/The Matrix.txt,"+wordInput);
            System.out.println("in file challange? "+inDictionary);
        }
        */
       
        if (!checkWordInServer(wordInput)){
            tryAgain=true;
            mValidWord=false;
            System.out.println("Word not in dictionarys");
            if(!testMode){
                setChanged();
                notifyObservers("NotValidWord");
                try {
                    updateGuests("NotValidWord");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return getBoardChars();
            
        }

        wordSentFlag=true;

        Tile[] tiles=wordInputToTiles(nowPlayingName,wordInput.toUpperCase());
        Word newWord= new Word(tiles, mouseRow, mouseCol, isVertical);
        //System.out.println(newWord.toString());

        if(board.boardLegal(newWord)){
            score[playerNum]+= board.tryPlaceWord(newWord);
            //System.out.println("score in model host= "+score[playerNum]);
            removeTilesFromLetterTiles(nowPlayingName,newWord); //remove from correct player
            tryAgain=false;
            mValidWord=true;
            if(!testMode){
                setChanged();
                notifyObservers("ValidWord");
                try {
                    updateGuests("ValidWord");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            //next player is current player
            //send messege to guest- place word correctly
            tryAgain = true;
        }
        return getBoardChars();
    }
    
    public char[][] mSubmitWord(String nowPlayingName, String wordInput, int mouseRow, int mouseCol) {
        //System.out.println("in model "+wordInput);
        boardChars= checkWord2(nowPlayingName, wordInput, mouseRow, mouseCol);
        for(int i=0; i<15; i++){
            for(int j=0; j<15; j++){
                System.out.print(boardChars[i][j]);
            }
            System.out.println("");
        }
        if(!testMode){
            nextPlayerTurn();//update next player
            //make the array a string:
            char[] oneDArray = new char[15 * 15];
            //System.out.println("oneDArray is: "+oneDArray.toString());
            int index = 0; // Index of current position in the 1D array
            //System.out.println("index:");
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    oneDArray[index] = boardChars[i][j];
                    index++;
                }
            }
        
            boardAsString = String.valueOf(oneDArray);
            //System.out.println("check word in host: "+str);
            try {
                updateGuests("updatedBoard,"+boardAsString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers("updatedBoard");
        }
        return boardChars;
    }
/* 
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
        //make the array a string:
        char[] oneDArray = new char[15 * 15];
        System.out.println("oneDArray is: "+oneDArray.toString());
        int index = 0; // Index of current position in the 1D array
        System.out.println("index:");
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                oneDArray[index] = boardChars[i][j];
                index++;
            }
        }
        String str = String.valueOf(oneDArray);
        System.out.println("check word in host: "+str);
        try {
            updateGuests("updatedBoard,"+str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setChanged();
        notifyObservers("updatedBoard");
        
        return boardChars;
    }
*/
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
        tryAgain=false;
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
    
    public String arrayNamesToString(ArrayList<String> arrayList) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < arrayList.size(); i++) {
            result.append(arrayList.get(i));

            if (i < arrayList.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static String tileMatrixToString(Tile[][] matrix, ArrayList<String> players) {
    StringBuilder result = new StringBuilder();

    int numPlayers = players.size();
    int numRows = Math.min(numPlayers, matrix.length);

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
        Tile[] row = matrix[rowIndex];

        for (int i = 0; i < row.length; i++) {
            result.append(row[i].getLetter());

            if (i < row.length - 1) {
                result.append(",");
            }
        }

        result.append(":");
    }

    // Remove the last ":" if the matrix is not empty
    if (result.length() > 0) {
        result.deleteCharAt(result.length() - 1);
    }

    return result.toString();
}

     public static String arrayToString(int[] array) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);

            if (i < array.length - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public void updateResumeCurrentPlayer(String ResumeCurrentPlayer){
        currentPlayer=ResumeCurrentPlayer;
        try {
            updateGuests("currentPlayer,"+ResumeCurrentPlayer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void updateResumeBoard(String boardAsString){
        int index = 0;
        board=new Board();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardChars[i][j] = boardAsString.charAt(index++); 
            }
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(boardChars[i][j]!='0'){
                    Tile tile= new Tile(boardChars[i][j], 0);
                    board.placeTiles(tile, i, j); 
                }
            }
        }
        try {
            updateGuests("updatedBoard,"+boardAsString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers("updatedBoard");
    }
    
    public void updateResumeBag(String bagStr){
        bag = new Tile.Bag();
        String[] numbers = bagStr.split(",");
        int[] result = new int[numbers.length];
        
        for (int i = 0; i < numbers.length; i++) {
            result[i] = Integer.parseInt(numbers[i]);
        }
        bag.quantities=result;
    }
    
    public void retrieveFromDB(int gamePort) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scra");
        Bson query = Filters.eq("Game port", gamePort);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        // Execute the query and retrieve the result
        Document result = collection.find(query).first();

        if (result != null) {
            // Access the retrieved fields from the document
            ResumeCurrentPlayer = result.getString("currentPlayer");
            updateResumeCurrentPlayer(ResumeCurrentPlayer);

            String players = result.getString("players");

            boardAsString = result.getString("boardAsString");
            updateResumeBoard(boardAsString);

            String scoreString = result.getString("scoreString");
            String bagStr = result.getString("bag");
            updateResumeBag(bagStr);

            String tiles = result.getString("tiles");

            // Do something with the retrieved data 
            System.out.println("Current Player: " + currentPlayer);
            System.out.println("Players: " + players);
            System.out.println("Board: " + boardAsString);
            System.out.println("Score: " + scoreString);
            System.out.println("Bag: " + bag);
            System.out.println("Tiles: " + tiles);

            // Continue processing the retrieved data
        } else {
            System.out.println("No document found with the specified criteria.");
        }

        mongoClient.close();
    }

    public void saveToDB() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scra");
        MongoCollection<Document> collection = database.getCollection(collectionName);
        int serverPort =this.port; 
        Document document = new Document("Game port" , serverPort);
        document.append("currentPlayer" ,currentPlayer);
        document.append("players", arrayNamesToString(players));
        document.append("boardAsString", boardAsString);
        document.append("scoreString", arrayToString(score));
        document.append("bag", arrayToString(bag.getQuantities()));
        document.append("tiles", tileMatrixToString(letterTiles, players));
        collection.insertOne(document);
        mongoClient.close();
    }


    @Override
    public void stopGame() {
        //save info
        saveToDB();
        System.out.println("game stopping");
    }

}
