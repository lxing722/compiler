
import java.util.*;

/**
 * A class for the WordCount data structure.
 * A WordCount object is a map which pairs a word (string)
 * with a list of information (Info)
 */
public class WordCount {
	private Map<String, ArrayList<Info>> wordMap = new TreeMap<String, ArrayList<Info>>();
	
	/**
	 * Builds an empty WordCount
	 */
	public WordCount() {

	}
	
	/**
	 * Adds the given 'info' in the list of
	 * Infos of the given word 'word'
	 */
	public void add(String word, Info info) {
		if(wordMap.containsKey(word)){
			wordMap.get(word).add(info);
		}
		else{
			ArrayList<Info> vec = new ArrayList<Info>();
			vec.add(info);
			wordMap.put(word,vec);
		}
	}
	
	/**
	 * Returns an iterator over the informations of
	 * the given word 'word'. If 'word' has no information
	 * returns null
	 */
	public Iterator<Info> getListIterator(String word) {
		if(wordMap.containsKey(word)){
			Iterator<Info> iter = wordMap.get(word).iterator();
			return iter;
		}
		else
			return null;
	}
	
	/**
	 * Displays the WordCount on System.out
	 */
	public void display() {
		for(Map.Entry<String, ArrayList<Info>> entry : wordMap.entrySet()){
			Iterator<Info> iter = getListIterator(entry.getKey());
			System.out.printf("%15s",entry.getKey() + " (" + entry.getValue().size() + "): ");
			while(iter.hasNext()){
				System.out.print(iter.next().toString() + " ");
			}
			System.out.print("\n");
		}
	}
}
