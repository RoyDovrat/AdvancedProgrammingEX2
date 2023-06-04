package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
    boolean tilesSentFlag=false, mIsHost=true, isVertical=false, mValidWord, wordSentFlag, playerSentFlag = true;
    Board board=new Board();
    byte[][] boardData;
    int mScore;
    Tile[] letterTiles = new Tile[7];
    Tile.Bag bag = new Tile.Bag();
    String resp, strTiles;
    boolean respValid;
    
    public modelGuest(int port){
        try {
            Socket server=new Socket("localhost", port);
            out=new PrintWriter(server.getOutputStream());
			in=new Scanner(server.getInputStream());
            
            Thread t= new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(in.hasNext()){
                            String res= in.nextLine(); //input from host
                            System.out.println("response in guest"+res);
                            setResponse(res);
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

            }catch (Exception e){
                System.out.println("getResponse exception");
            }
        }
        this.respValid = false;
        return this.resp;
    }
    
    @Override
    public void setPlayerName(String Name) { //send name to host
        mPlayerName=Name;
        System.out.println("name in model: "+mPlayerName);
        out.println("joinGame,"+mPlayerName);
        out.flush();
        strTiles= getResponseFromHost();
        tilesSentFlag = true;
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
        System.out.println("wordsub in model and row and col"+wordInput);
        String outputStr = "wordsub,"+mouseRow+","+mouseCol+","+wordInput;
        out.println(outputStr);//send to host
        out.flush();
       // tilesSentFlag = true;
        System.out.println("wordsub was sent to host from guest");
        String strFromHost= getResponseFromHost();
        System.out.println("string length is: "+strFromHost.length());
        System.out.println("strFromHost "+strFromHost);

        char[][] boardArray = new char[15][15]; // Convert the string to a 2D char array
        int index = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardArray[i][j] = strFromHost.charAt(index++);
            }
        }
        return boardArray;
    }

    @Override
    public char[] mRequestFillLetterTiles() {
        String str = strTiles;
        if(tilesSentFlag){
            return str.toCharArray();
        }
        char[] res= new char[7];
        return res;
    }

    @Override
    public char[] mRequestRestartLetterTiles() {
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

}
