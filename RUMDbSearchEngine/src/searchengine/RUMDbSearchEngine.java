package searchengine;

import java.util.ArrayList;
import java.util.Collections;

/*
 * This class builds a hash table of words from movies descriptions. Each word maps to a set
 * of movies in which it occurs.
 * 
 * @author Haolin (Daniel) Jin
 * @author Ana Paula Centeno
 * 
 */ 
public class RUMDbSearchEngine {
    
	private int    hashSize;   // the hash table size
	private double threshold;  // load factor threshold. load factor = wordCount/hashSize
    private int    wordCount;  // the number of unique words in the table
    private WordOccurrence[] hashTable;  // the hash table

    private ArrayList<String> noiseWords; // noisewords are not to be inserted in the hash table

	/* 
	 * Constructor initilizes the hash table.
	 * 
	 *  @param hashSize is the size for the hash table 
	 * 	@param threshold for the hash table load factor. Rehash occurs when the ratio 
	 * 	wordCount : hashSize exceeds the threshold.
	 *  @param noiseWordsFile contains words that will not be inserted into the hash table.
	 */
	public RUMDbSearchEngine (int hashSize, double threshold, String noiseWordsFile){

		this.hashSize   = hashSize;
		this.hashTable  = new WordOccurrence[hashSize];
        this.noiseWords = new ArrayList<String>();
		this.threshold  = threshold;
        this.wordCount  = 0;

        // Read noise words from file
        StdIn.setFile(noiseWordsFile);
        while ( !StdIn.isEmpty() ) {
            String word = StdIn.readString();
            if ( !noiseWords.contains(word) )
                noiseWords.add(word);
        }
    }

	/*
	 * Method used to map a word into an array index.
	 * 
	 * @param word the word
	 * @return array index within @hashTable
	 */
	private int hashFunction ( String word ) {
        int hashCode = Math.abs(word.toLowerCase().replaceAll("/[^a-z0-9]/","").hashCode());
		return hashCode % hashSize;
	}

	/*
	 * Returns the hash table load factor
	 * @return the load factor
	 */ 
	public double getLoadFactor () {
		return (double)wordCount/hashSize;
	}

	/*
	 * This method reads movies title and description from the input file.
     * 
	 * @param inputFile the file to be read containg movie's titles and descriptions.
     * 
     * The inputFile format:
     * 		Each line describes a movie's title, and a short description on the movie.
	 * 		title| word1 word2 word3;
     * 
	 *  	Note that title can have multiple words, there is no space between the last 
     *      word on the tile and '|'
	 * 		No duplicate movie name accepted.
     * 
	 * @return ArrayList of ArrayList of Strings, each inner ArrayList refers to a movie, 
	 * 		the first index contains the title, the remaining indices contain the movie's
	 * 		description words (one word per index). 
     * 
	 *  Example: 
	 * 		[
	 * 			[full title1][word1][word2]
	 * 			[full title2][word1]
	 * 			[full title3][word1][word2][word3][word4]
	 * 		]
	 */
	public ArrayList<ArrayList<String>> readInputFile ( String inputFile ) {

		ArrayList<ArrayList<String>> allMovies = new ArrayList<ArrayList<String>>();
		StdIn.setFile(inputFile);
		
        String[] read = StdIn.readAllStrings();
		
        for ( int i = 0; i < read.length; i++ ) {
			ArrayList<String> movie = new ArrayList<String>();
			String t = "";
			do {
                t += " "+read[i];
            } while ( read[i++].indexOf('|') == -1 );
			
            movie.add(t.substring(1,t.length()-1).toLowerCase().replaceAll("/[^a-z0-9]/",""));

			while ( i < read.length ) {
				if ( read[i].indexOf(';') != -1 ) {
					movie.add(read[i].substring(0,  read[i].indexOf(';')));
					break;
				}
				movie.add(read[i].toLowerCase().replaceAll("/[^a-z0-9]/","") );
				i++;
			}
			allMovies.add(movie);
		}
		return allMovies;
	}
	
	/* 
	 * This method calls readInputFile and uses its output to load the movies and their
     * descriptions words into the hashTable.
     * 
     * Use the result from readInputFile() to insert each word and its location
     * into the hash table.
     * 
     * Use isWord() to discard noise words, remove trailing punctuation, and to transform
     * the word into all lowercase character.
     * 
     * Use insertWordLocation() to insert each word into the hash table.
	 * 
     * Use insertWordLocation() to insert the word into the hash table.
     * 
	 * @param inputFile the file to be read containg movie's titles and descriptions
	 * 
	 */
	public void insertMoviesIntoHashTable ( String inputFile ) {
		ArrayList<ArrayList<String>> input = readInputFile(inputFile);

		for(int i = 0; i < input.size(); i++){

			String title = input.get(i).get(0);

			for(int j = 1; j < input.get(i).size(); j++){

				String str = input.get(i).get(j);
				String word = isWord(str);
				if(word == null){
					continue;
				}
				Location loc = new Location(title, j);
				insertWordLocation(word, loc);
			}
		}
	}

    /**
	 * Given a word, returns it as a word if it is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters and digits, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return word (word without trailing punctuation, LOWER CASE)
	 */
	private String isWord ( String word ) {
		int p = 0;
    	char ch = word.charAt(word.length()-(p+1));
    	while (ch == '.' || ch == ',' || ch == '?' || ch == ':' || ch == ';' || ch == '!') {
    		p++;
            if ( p == word.length() ) {
                // the entire word is punctuation
                return null;
            }
    		int index = word.length()-(p+1);
    		if (index == -1) {
    			System.out.flush();
    		}
    		ch = word.charAt(word.length()-(p+1));	
    	}
        
    	word = word.substring(0,word.length()-p);

    	// are all characters alphabetic letters?
    	for (int i=0; i < word.length(); i++) {
    		if (!Character.isLetterOrDigit(word.charAt(i))) {
    			return null;
    		}
    	}
    	word = word.toLowerCase();
    	if (noiseWords.contains(word)) {
    		return null;
    	}
    	return word;
	}

    /*
	 * Prints the entire hash table
	 */
	public void print () {

        for ( int i = 0; i < hashTable.length; i++ ) {
            
            StdOut.printf("[%d]->", i);
            for ( WordOccurrence ptr = hashTable[i]; ptr != null; ptr = ptr.next ) {

                StdOut.print(ptr.toString());
                if ( ptr.next != null ) {
                    StdOut.print("->");
                }
            }
            StdOut.println();
        }
	}

	/*
	 * This method inserts a Location object @loc into the matching WordOccurrence object
	 * in the hash table. If the word is not present into the hash table, add a new 
     * WordOccurrence object into hash table. 
	 * 		
	 * @param word to be inserted
	 * @param loc the word's position within the description.
	 */
	public void insertWordLocation (String word, Location loc) {
		int index = hashFunction(word);
		//if hashtable index is empty
		if(hashTable[index] == null){
			WordOccurrence wrdOcc = new WordOccurrence(word);
			wrdOcc.addOccurrence(loc);
			hashTable[index] = wrdOcc;
			wordCount++;
		} else{
			//checks if word is in hashtable index if not empty
			for(WordOccurrence ptr = hashTable[index]; ptr != null; ptr = ptr.next){
				if(ptr.getWord().equals(word)){
					ptr.addOccurrence(loc);
					break;
				}
				else if(ptr.next == null){
					WordOccurrence tmp = new WordOccurrence(word);
					tmp.addOccurrence(loc);
					tmp.next = hashTable[index];
					hashTable[index] = tmp;
					wordCount++;
					
				}
			}
		}
		if(getLoadFactor() > threshold){
			rehash(hashSize * 2);
		}
    }

	/*
	 * Rehash the hash table to newHashSize. Rehash happens when the load factor is
     * greater than the @threshold (load factor = wordCount/hashSize).
     * 
	 * @param newHashSize is the new hash size
	 */

	private void find(WordOccurrence first, WordOccurrence second){
		if(first.getLocations().size() == 0) return;
		for(int i = 0; i < first.getLocations().size(); i++){
			Location pos = first.getLocations().get(i);
			second.addOccurrence(pos.getTitle(), pos.getPosition());
		}
	}

	private void rehash (int newHashSize){
		hashSize = newHashSize;
		WordOccurrence[] newHash = new WordOccurrence[newHashSize];
		for(int i = 0; i < newHashSize; i++){
			newHash[i] = null;
		}
		for(int i = 0; i < hashTable.length; i++){
			WordOccurrence curr = hashTable[i];
			while(curr != null){
				int pos = hashFunction(curr.getWord());
				if(newHash[pos] == null){
					newHash[pos] = new WordOccurrence(curr.getWord());
					find(curr, newHash[pos]);
				} else{
					WordOccurrence curr2 = newHash[pos];
					while(curr2.next != null){
						curr2 = curr2.next;
					}
					curr2.next = new WordOccurrence(curr.getWord());
					find(curr, curr2.next);
				}
				curr = curr.next;
			}
		}

		hashTable = newHash;
	}

	/* 
	 * Find the WordOccurrence object with the target word in the hash table
	 * @param word search target
	 * @return @word WordOccurrence object
	 */
	public WordOccurrence getWordOccurrence (String word) {
		int index = hashFunction(word);
		for(WordOccurrence ptr = hashTable[index]; ptr != null; ptr = ptr.next){
			
			if(ptr.getWord().equals(word)) { return ptr; }

		}
		return null;
	}
    
	/*
	 * Finds all occurrences of wordA and wordB in the hash table, and add them to an 
	 * ArrayList of MovieSearchResult based on titles.
	 * 		(no need to calculate distance here)
     * 
	 * @param wordA is the first queried word
	 * @param wordB is the second queried word
	 * @return ArrayList of MovieSearchResult objects.
	 */

	private int find (String title, ArrayList<MovieSearchResult> arr) {
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getTitle().equals(title)) {
				return i;
			}
		}
		//Return -1 to define that the element wasn't found
		return -1;
	}

	public ArrayList<MovieSearchResult> createMovieSearchResult (String wordA, String wordB) {
		
		ArrayList<MovieSearchResult> result = new ArrayList<MovieSearchResult>();
		WordOccurrence word = getWordOccurrence(wordA);

		if(word == null) return null;
		ArrayList<Location> titlesOfA = word.getLocations();

		for(int i = 0; i < titlesOfA.size(); i++) {
			String title = titlesOfA.get(i).getTitle();
			int index = find(title, result);
			int pos = titlesOfA.get(i).getPosition();
			if(index == -1) {
				MovieSearchResult curr = new MovieSearchResult(title);
				curr.addOccurrenceA(pos);
				result.add(curr);
			} else {
				MovieSearchResult oldAdd = result.get(index);
				oldAdd.addOccurrenceA(pos);
			}
		}

		word = getWordOccurrence(wordB);
		if(word == null) return null;
		ArrayList<Location> titlesOfB = word.getLocations();
		for(int i = 0; i < titlesOfB.size(); i++) {
			String title = titlesOfB.get(i).getTitle();
			int index = find(title, result);
			int pos = titlesOfB.get(i).getPosition();
			if(index == -1) {
				MovieSearchResult curr = new MovieSearchResult(title);
				curr.addOccurrenceB(pos);
				result.add(curr);
			}else {
				MovieSearchResult oldAdd = result.get(index);
				oldAdd.addOccurrenceB(pos);
			}
		}

		return result;
	}

	/*
	 * 
     * Computes the minimum distance between the two wordA and wordB in @msr.
     * In another words, this method computes how close these two words appear 
     * in the description of the movie (MovieSearchResult refers to one movie).
     * 
     * If the movie's description doesn't contain one, or both words set distance to -1;
	 * 
	 *  NOTE: the ArrayLists for A and B will always be in order since the words were added in order.
	 * 		
	 * The shortest distance between two words can be found by keeping track of the index 
     * of previous wordA and wordB, then find the next location of either word and calculate 
     * the distance between the word and the previous location of the other word.
     * 
	 * For example:
	 * 		wordA locations: 1 3 5 11
	 * 		wordB locations: 4 10 12 
	 * 		start previousA as 1, and previousB as 4, calculate distance as abs(1-4) = 3
	 * 		because 1<4, 	update previousA to 3, 	abs(4-3)   = 1 , smallest so far
	 * 		because 3<4, 	update previousA to 5, 	abs(5-4)   = 1 
	 * 		because 5>4, 	update previousB to 10, abs(5-10)  = 5
	 * 		because 5<10, 	update previousA to 11, abs(11-10) = 1
	 * 		End because all elements from A have been used.
	 * 			
	 * @param msr the MovieSearchResult object to be updated with the minimum distance between its 
     * words.
	 */
	public void calculateMinDistance(MovieSearchResult msr){
		for(int i = 0; i < msr.getArrayListA().size(); i++){
			int a = msr.getArrayListA().get(i);
			for(int j = 0; j < msr.getArrayListB().size(); j++){
				int b = msr.getArrayListB().get(j);
				if(i == 0){
					msr.setMinDistance(Math.abs(a - b));
					continue;
				}
				int x = Math.abs(a - b);
				if(msr.getMinDistance() > x) msr.setMinDistance(x);
			}
		}
	}

	/*
	 * This method's purpose is to search the movie database to find movies that 
     * contain two words (wordA and wordB) in their description.
	 * 
     * @param wordA the first word to search
     * @param wordB the second word to search
     * 
     * @return ArrayList of MovieSearchResult, with length <= 10. Each
	 * MovieSearchResult object returned must have a non -1 distance (meaning that
     * both words appear in the description). The ArrayList is expected to be 
     * sorted from the smallest distance to the greatest.
	 * 		
	 * 	NOTE: feel free to use Collections.sort( arrayListOfMovieSearchResult ); to sort.
	 */
	public ArrayList<MovieSearchResult> topTenSearch(String wordA, String wordB){
		ArrayList<MovieSearchResult> results = createMovieSearchResult(wordA, wordB);
		if(results == null){
			return null;
		}
		for(int i = 0; i < results.size(); i++){
			calculateMinDistance(results.get(i));
			if(results.get(i).getMinDistance() == -1){
				results.remove(i);
				i--;
			}
		}
		if(results.size() > 10){
			while(results.size() > 10){
				results.remove(results.size() - 1);
			}
		}

		Collections.sort(results);
		return results;
	}
}
