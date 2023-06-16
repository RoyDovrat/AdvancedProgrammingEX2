package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import com.example.App;

import Server.DictionaryManager;
import javafx.beans.InvalidationListener;

public class modelGuest extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName, mStrLetterTiles, line = "", mWordInput, CurrentPlayerName;
    Socket server;
    boolean tilesSentFlag=false, mIsHost=true, isVertical=false, mValidWord, wordSentFlag, playerSentFlag = true;
    Board board=new Board();
    byte[][] boardData;
    int mScore, myIndex;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();
    String resp, strTiles;
    boolean respValid;
    private Semaphore turnSemaphore;
    boolean myTurn = false; 
    char[][] boardArray;
    
    public modelGuest(int port){
        try {
            Socket server=new Socket("localhost", port);
            out=new PrintWriter(server.getOutputStream());
			in=new Scanner(server.getInputStream());
            this.turnSemaphore = new Semaphore(0); 
            
            Thread t= new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(in.hasNext()){
                            String res= in.nextLine(); //input from host
                            System.out.println("response in guest"+res);
                            setResponse(res);
                            if (res.equals("YourTurn")) { //new
                                myTurn = true;
                                turnSemaphore.release();
                            }
                        }
                        else{//System.out.println("no next input in modelGuest");
                            }
                        
                        
                    }
                }        
            });
            t.start();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void setResponse(String string) {
        this.resp = string;
        this.respValid = true;
    }

    private String getResponseFromHost() {
        while(!this.respValid){
            try{
                Thread.sleep(1000);
                 System.out.println("still waiting");
            }catch (Exception e){
                System.out.println("getResponse exception");
            }
        }
        this.respValid = false;
        return this.resp;
    }
    
    public String CurrentPlayerName(){ //returns the name of the player which is in turn
        out.println(mPlayerName+",requestCurrentPlayer");
        out.flush();
        CurrentPlayerName = getResponseFromHost();
        return CurrentPlayerName;
    }

    public void updateBoard(){
        Thread r= new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try{
                            Thread.sleep(1000);
                            out.println(CurrentPlayerName+",updateBoard");
                            out.flush();
                            String strFromHost= getResponseFromHost();
                            String [] args= strFromHost.split(",");
                            strFromHost = args[0];//the board

                            boardArray = new char[15][15]; // Convert the string to a 2D char array
                            int index = 0;
                            for (int i = 0; i < 15; i++) {
                                for (int j = 0; j < 15; j++) {
                                    boardArray[i][j] = strFromHost.charAt(index++);
                                }
                            }
                            setChanged();
                            notifyObservers();
                        }catch (Exception e){
                            System.out.println("getResponse exception");
                        }
                    }
                }        
            });
            r.start();
    }
    
    @Override
    public void setPlayerName(String Name) { //send name to host
            mPlayerName=Name;
            System.out.println("name in model: "+mPlayerName);
            out.println(mPlayerName +",submitName,"+mPlayerName);
            out.flush();
            String str = getResponseFromHost();
            String [] args= str.split(",");
            args[0].equals(this.mPlayerName);
            if(args[1].equals("wait")){
                System.err.println("commuication is ok");
            }
        }
    
    @Override
    public void msetStart(){
        out.println(mPlayerName +",getTiles");
        out.flush();
        strTiles= getResponseFromHost();
        System.out.println(strTiles);
        tilesSentFlag = true;
    }
   
    @Override
    public boolean mCheckIfEnoughPlayers(){  
            System.out.println("guest mCheckIfEnoughPlayers");
            out.println(mPlayerName+",joingame");//send to host
            out.flush();
            String strFromHost= getResponseFromHost();
            System.out.println("guest mCheckIfEnoughPlayers" + strFromHost);
            String [] args= strFromHost.split(",");
            if( args[0].equals(mPlayerName) && args[1].equals("enough")){
                return args[2].equals("true");
            }
        return false;
    }

    
    
    @Override
    public byte[][] getBonusBoard() {
        return board.getBonus();
    }
    
    //number of players
    @Override
    public void mOnePlayerClicked(){}
    @Override
    public void mTwoPlayersClicked(){}
    @Override
    public void mThreePlayersClicked(){}
    @Override
    public void mFourPlayersClicked(){}

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
    public char[][] mSubmitWord(String nowPlayingName, String wordInput, int mouseRow, int mouseCol) {
        CurrentPlayerName=CurrentPlayerName();
        if (!mPlayerName.equals(CurrentPlayerName)) { //new
             System.out.println("");
            out.println(mPlayerName+",getCurrentBoard");//send to host
            out.flush();
            //add a note- not your turn
            System.out.println("not your turn");
        }
         else{
            System.out.println("wordsub in model and row and col"+wordInput);
            String outputStr = mPlayerName+",wordsub,"+mouseRow+","+mouseCol+","+wordInput;
            out.println(outputStr);//send to host
            out.flush();
             // tilesSentFlag = true;
            System.out.println("wordsub was sent to host from guest");
            
        }
        String strFromHost= getResponseFromHost();
        String [] args= strFromHost.split(",");
        strFromHost = args[0];//the board
        System.out.println("args 1: "+args[1]);
        System.out.println("string length is: "+strFromHost.length());
        System.out.println("strFromHost "+strFromHost);


        char[][] boardArray = new char[15][15]; // Convert the string to a 2D char array
        int index = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardArray[i][j] = strFromHost.charAt(index++);
            }
        }
        //myTurn = mPlayerName.equals(App.nowPlaying); //new
        return boardArray;
    }

    @Override
    public char[] mRequestFillLetterTiles(String nowPlayingName) {
        out.println(mPlayerName+",FillTiles");
        out.flush();
        strTiles= getResponseFromHost();
        return strTiles.toCharArray();
    }

    @Override
    public char[] mRequestRestartLetterTiles(String nowPlayingName) {
        String str = strTiles;
        if(tilesSentFlag){
            return str.toCharArray();
        }
        char[] res= new char[7];
        return res;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'start'");
    }
    @Override
    public void mSkipTurn() {
        out.println(mPlayerName +",SkipTurn");
        out.flush();
    }

}
