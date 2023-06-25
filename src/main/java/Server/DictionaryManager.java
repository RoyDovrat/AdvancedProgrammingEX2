package Server;

import java.util.HashMap;


public class DictionaryManager {
	//private String[] fileNames;
	HashMap<String, Dictionary> map;
	private static DictionaryManager DM;
	
	private DictionaryManager () {
        this.map=new HashMap<String, Dictionary>();
    }
	
	public static DictionaryManager get() {
		if (DM == null) {
			DM = new DictionaryManager();
			}
		return DM;
		}

	public boolean query(String... filenames) {
		String searchWord = filenames[filenames.length - 1];
		boolean existFile=false;
		for (int i = 0; i < filenames.length - 1; i++) {
		    String filename = filenames[i];
		    if (!map.containsKey(filename)) {
		        map.put(filename, new Dictionary(filename));
		    }
		}
		for (int i = 0; i < filenames.length - 1; i++) {
		    String filename = filenames[i];
			if (map.get(filename).query(searchWord)) {
				existFile=true;
			}
		}
		return existFile;
	}
	
	public boolean challenge(String... filenames) {
		String searchWord = filenames[filenames.length - 1];
		boolean existFile=false;
		for (int i = 0; i < filenames.length - 1; i++) {
		    String filename = filenames[i];
		    if (!map.containsKey(filename)) {
		        map.put(filename, new Dictionary(filename));
		    }
		}
		for (int i = 0; i < filenames.length - 1; i++) {
		    String filename = filenames[i];
			if (map.get(filename).challenge(searchWord)) {
				existFile=true;
			}
		}
		return existFile;
	}
	public int getSize() {
		return map.size();
		
	}
	public static void main(String[] args) {

	}

}
