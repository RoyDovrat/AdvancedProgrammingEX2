package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class modelGuest extends Observable implements interfaceModel {
    PrintWriter out;
    Scanner in;
    String mPlayerName, line = "", mStrLetterTiles;
    Socket server;
    boolean flag=false, mIsHost=false, isVertical=false, mValidWord;
    Board board=new Board();
    byte[][] boardData;
    int mScore;

    /* 
    public modelGuest(String name){
        this.playerName=name;
    }
    public void createConnection(int port){
        try {
            port=5000;
			server=new Socket("localhost",port);
			out=new PrintWriter(server.getOutputStream());
			in=new Scanner(server.getInputStream());

            
			out.println(playerName);
			out.flush();
			String res=in.nextLine(); //get server-welcome
            System.out.println("model guest input"+ res);
			//in.close();
			//out.close();
			//server.close();
            
            System.out.println("in model guest line1 "+line);
		} catch (IOException e) {
			System.out.println("your code ran into an IOException (-10)");
		}
         
 
         // keep reading until "Over" is input
         Runnable r= ()->{
            while (!line.equals("over")) {
                System.out.println("1");
                //out.writeUTF(line);
            }
        };
        new Thread(r).start();

    
        System.out.println("in model guest line "+line);
            
        //}
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
