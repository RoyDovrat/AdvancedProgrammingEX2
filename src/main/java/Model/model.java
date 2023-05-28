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
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import Server.ClientHandler;
import Server.MyServer;
import Server.MainTrain.ClientHandler1;

public class model extends Observable {
    byte[][] boardData;
    Board board=new Board();
    boolean mValidWord;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();
    String mStrLetterTiles;
    boolean isVertical=false;
    int mScore;
    static boolean mIsHost=false;
    boolean wordSentFlag=false;
    static String mWordInput;
    private static CountDownLatch latch;
    private Socket          socket   = null;
    private ServerSocket    host   = null;
    private DataInputStream in       =  null;
    /* 

    public model(int port){
        try{
            host=new ServerSocket(port);
            System.out.println("Server started");
 
            System.out.println("Waiting for a client ...");
 
            socket = host.accept();
            System.out.println("Client accepted");
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
            String line = "";
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(line);
    
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");
 
            // close connection
            socket.close();
            in.close();
        }catch(IOException i){
            System.out.println(i);
        }
    }

    public class Client {
        // initialize socket and input output streams
        private Socket socket = null;
        private DataInputStream input = null;
        private DataOutputStream out = null;
        private byte[][] boardData;
        public Client(String address, int port){
        // establish a connection
            try {
                socket = new Socket(address, port);
                System.out.println("Connected");
    
                // takes input from terminal
                input = new DataInputStream(System.in);
    
                // sends output to the socket
                out = new DataOutputStream(
                    socket.getOutputStream());
            }
            catch (UnknownHostException u) {
                System.out.println(u);
                return;
            }
            catch (IOException i) {
                System.out.println(i);
                return;
            }
     
            // string to read message from input
            String line = "";
            // keep reading until "Over" is input
            while (!line.equals("Over")) {
                try {
                    line = input.readLine();
                    out.writeUTF(line);
                }
                catch (IOException i) {
                    System.out.println(i);
                }
            }
    
            // close the connection
            try {
                input.close();
                out.close();
                socket.close();
            }
            catch (IOException i) {
                System.out.println(i);
            }
        }
    }
    /* 
    public static class ClientHandler1 implements ClientHandler{
		PrintWriter out;
		Scanner in;		
		@Override
		public void handleClient(InputStream inFromclient, OutputStream outToClient) {
			out=new PrintWriter(outToClient);
			in=new Scanner(inFromclient);
			String text = in.next();
            System.out.println("in client handler: "+text);

			out.println(text);
			out.flush();
		}

		@Override
		public void close() {
			in.close();
			out.close();
		}
		
	}
    
    public static boolean testServer() {
        //latch = new CountDownLatch(1
		boolean ok=true;
		Random r=new Random();
		int port=6000+r.nextInt(1000);
		MyServer s=new MyServer(port, new ClientHandler1(),3);
		int c = Thread.activeCount();
		s.start(); // runs in the background
        //latch.countDown(); // Signal that the server is ready
		try {
            if (mIsHost==true){
                Host(port);
            }
            else{
                client1(port);
			    //client1(port);
			    //client1(port);
			    //client1(port);
            }

		}catch(Exception e) {
			System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");			
			ok=false;
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {}
		s.close();
		
		try {Thread.sleep(2000);} catch (InterruptedException e) {}
		
		if (Thread.activeCount()!=c) {
			System.out.println("you have a thread open after calling close method (-100)");
			ok=false;
		}
		return ok;
	}
    */
    /* 
    public  void Host(int port) {		
		new Thread(
		()->{
			try{
				Socket host=new Socket("localhost", port);	
                PrintWriter outToClient=new PrintWriter(host.getOutputStream());
				Scanner in=new Scanner(host.getInputStream());	
				
				
				String input=in.next();
                System.out.println("in host: "+input);

				in.close();
				outToClient.close();
				host.close();
			}catch (Exception e){
				System.out.println("Exception was thrown when running a client (-25)");
			}
		}).start();
	}
    
    public void client1(int port) {		
		new Thread(
		()->{
			try{
                
				Socket host=new Socket("localhost", port);		
                PrintWriter outToHost=new PrintWriter(host.getOutputStream());
                Scanner in=new Scanner(host.getInputStream());

                
                //String newWord=
                //Random r=new Random();
				
				//Random revital=new Random();
				//int randomNum = revital.nextInt(100);
				//String text = ""+(1000+r.nextInt(100000));
				//String rev=new StringBuilder(text).reverse().toString();
                
                String wordOutput= mWordInput;
                System.out.println("in client"+wordOutput);
				outToHost.println(wordOutput);
				outToHost.flush();
				
				//String response=in.next();
                
				if(response==null||!response.equals(rev)) 
					System.out.println("problem getting the right response from your server, cannot continue the test (-25)");
				
                in.close();
				outToHost.close();
				host.close();
			}catch (Exception e){
				System.out.println("Exception was thrown when running a client (-25)");
			}
		}).start();
	}
	*/
    public void setBoardData(byte[][] bonusMatrix) {
        this.boardData = bonusMatrix;
        //redraw();
    }
     
    public char[] restartLetterTiles(){ //move to model
        char[] mCharLetterTiles=new char[7];
        for (int i = 0; i < letterTiles.length; i++) {
            letterTiles[i] = null;
        } 
        fillLetterTilesFromBag();
        for (int i = 0; i < letterTiles.length; i++) { //for debug
            System.out.println(letterTiles[i].tileToString(letterTiles[i]));
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
    
    public char[][] getBoardChars(){
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

    public void removeTilesFromLetterTiles(Word w){
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
    
    public char[][] mSubmitWord(String wordInput, int mouseRow, int mouseCol) {
        mWordInput=wordInput;
        //mValidWord=validateWordInput(wordInput);
        wordSentFlag=true;
        Tile[] tiles=wordInputToTiles(wordInput);
        Word newWord= new Word(tiles, mouseRow, mouseCol, isVertical);
        System.out.println("newWord.toString()");
        if(board.boardLegal(newWord) && validateWordInput(wordInput)){
            mScore= board.tryPlaceWord(newWord);
            mValidWord=true;
            removeTilesFromLetterTiles(newWord);
            fillLetterTilesFromBag();
        }
        
        setChanged();
        notifyObservers();
        return getBoardChars();
    }
    
    public void mLeftRightClicked(){
        isVertical=false;
    }
    
    public void mDownClicked(){
        isVertical=true;
    }
    

    public boolean validateWordInput(String wordInput) {
        // Create a copy of the Tile array to track tile usage
        Tile[] tilesCopy = Arrays.copyOf(letterTiles, letterTiles.length);
        // Convert the wordInput string to a character array
        char[] chars = wordInput.toCharArray();
        // Iterate over each character in the wordInput
        for (char c : chars) {
            boolean found = false;
    
            // Search for a matching character in the tilesCopy array
            for (int i = 0; i < tilesCopy.length; i++) {
                if (tilesCopy[i] != null && tilesCopy[i].getLetter() == c) {
                    // Character found in the tilesCopy array
                    found = true;

                    // Mark the tile as used by setting its value to null
                    tilesCopy[i] = null;
    
                    // Break out of the inner loop since we found a match
                    break;
                }
            }
    
            // If no matching character is found in the tilesCopy array, return false
            if (!found) {
                return false;
            }
        }
    
        // All characters in wordInput have been matched with tiles
        return true;
    }

    public Tile[] wordInputToTiles(String wordInput){
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
    
    public boolean getValidWord() {
        return mValidWord;
    }
    
    public int getScore() {
        return mScore;
    }
    
    public String getMStrLetterTiles(){
        return mStrLetterTiles;
    }

    public void mGetGameMode(boolean isHost) {
        /* 
        mIsHost=isHost;
        if (isHost){
            model host = new model(5000);
        }
        else{
            Client client = new Client("127.0.0.1", 5000);
        }
        */
        //boolean ok=testServer();
        //System.out.println("Server is working? "+ok);
    }
        
}
