package Server;


public interface FileSearcher {
	public boolean search(String word, String...fileNames);	
	public void stop();

}
