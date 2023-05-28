package Server;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    int port;
    boolean stop;
    ClientHandler ch;
    int MaxThreads;
    ExecutorService executor;
    Set<Socket> sockets;

    public MyServer (int port, ClientHandler ch, int MaxThreads) {
        this.port=port;
        this.ch=ch;
        this.MaxThreads=MaxThreads;
        this.executor=Executors.newFixedThreadPool(MaxThreads);
        this.sockets = new HashSet<>();
    }

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

    private void startServer() throws IOException{
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
									ClientHandler chNew = chClass.getDeclaredConstructor().newInstance();
                        
									chNew.handleClient(client.getInputStream(), client.getOutputStream());
									
									client.close();
								} catch (InstantiationException |IllegalArgumentException| InvocationTargetException |NoSuchMethodException|SecurityException|IllegalAccessException e) {
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
}
