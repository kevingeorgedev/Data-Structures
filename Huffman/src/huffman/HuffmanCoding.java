package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                System.exit(1);
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }
    
    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /**
     * Reads a given text file character by character, and returns an arraylist
     * of CharFreq objects with frequency > 0, sorted by frequency
     * 
     * @param filename The text file to read from
     * @return Arraylist of CharFreq objects, sorted by frequency
     */
    public static ArrayList<CharFreq> makeSortedList(String filename) {
        StdIn.setFile(filename);
        String input = StdIn.readAll();
        ArrayList<CharFreq> arr = new ArrayList<CharFreq>();
        
        
        for(int i = 0; i < 128; i++){
            arr.add(new CharFreq(null, 0.0));
        }
        for(int i = 0; i < input.length(); i++){
            char x = input.charAt(i);
            if(arr.get((int) x).getCharacter() == null){
                arr.set((int) x, new CharFreq(input.charAt(i), 1));
            }
            else{
                CharFreq tmp = arr.get((int) x);
                tmp.setProbOccurrence(tmp.getProbOccurrence() + 1.0);
                arr.set((int) x, tmp);
            }
        }
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i).getCharacter() == null){
                arr.remove(i);
                i = i-1;
                continue;
            }
            if(arr.get(i).getCharacter() == ' '){
                CharFreq tmp = arr.get(i);
                double prob = tmp.getProbOccurrence()/input.length();
                tmp.setProbOccurrence(prob);
                arr.set(i, tmp);
                continue;
            }
            CharFreq tmp = arr.get(i);
            double prob = tmp.getProbOccurrence()/input.length();
            tmp.setProbOccurrence(prob);
            arr.set(i, tmp);
        }

        if(arr.size() == 1){
            char x = arr.get(0).getCharacter();
            int y = ((int) x) + 1;
            if(y == 128) y = 0;
            arr.add(new CharFreq((char) y, 0));
        }

        Collections.sort(arr);
        
        return arr;
    }

    /**
     * Uses a given sorted arraylist of CharFreq objects to build a huffman coding tree
     * 
     * @param sortedList The arraylist of CharFreq objects to build the tree from
     * @return A TreeNode representing the root of the huffman coding tree
     */
    public static TreeNode makeTree(ArrayList<CharFreq> sortedList) {
        Queue<TreeNode> source = new Queue<TreeNode>(), target = new Queue<TreeNode>();
        
        for(int i = 0; i < sortedList.size(); i++){
            source.enqueue(new TreeNode(sortedList.get(i), null, null));
        }

        if(source.size() == 0){
            return null;
        }
        else if(source.size() == 1){
            return source.dequeue();
        }

        while(!source.isEmpty() || target.size() != 1){
            TreeNode parent = new TreeNode(new CharFreq(null, 0), null, null);
            for(int i = 0; i < 2; i++){
                if(target.isEmpty()){
                    if(i != 0){
                        parent.setRight(source.dequeue());
                    } else{
                        parent.setLeft(source.dequeue());
                    }
                } else{
                    if(source.isEmpty()){
                        if(i != 0){
                            parent.setRight(target.dequeue());
                        } else{
                            parent.setLeft(target.dequeue());
                        }
                    } else{

                        if(i != 0){
                            
                            if(source.peek().getData().getProbOccurrence() > target.peek().getData().getProbOccurrence()){
                                parent.setRight(target.dequeue());
                            } else{
                                parent.setRight(source.dequeue());
                            }
                            
                        } else{

                            if(source.peek().getData().getProbOccurrence() > target.peek().getData().getProbOccurrence()){
                                parent.setLeft(target.dequeue());
                            } else{
                                parent.setLeft(source.dequeue());
                            }
                            
                        }
                    }
                }
            }
            CharFreq tmp = parent.getData();
            tmp.setProbOccurrence(parent.getLeft().getData().getProbOccurrence() + parent.getRight().getData().getProbOccurrence());
            parent.setData(tmp);
            target.enqueue(parent);
        }
        
        return target.dequeue();
    }

    /**
     * Uses a given huffman coding tree to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null
     * 
     * @param root The root of the given huffman coding tree
     * @return Array of strings containing only 1's and 0's representing character encodings
     */
    public static String[] makeEncodings(TreeNode root) {
        String[] arr = new String[128];

        for(int i = 0; i < 128; i++){
            arr[i] = null;
        }

        inOrder(root, arr, "");
        return arr;
    }

    public static void inOrder(TreeNode x, String[] arr, String curr){
        if(x.getLeft() == null && x.getRight() == null){
            char a = x.getData().getCharacter();
            arr[(int) a] = curr;
            return;
        } 
        inOrder(x.getLeft(), arr, curr + "0");
        inOrder(x.getRight(), arr, curr + "1");
    }

    /**
     * Using a given string array of encodings, a given text file, and a file name to encode into,
     * this method makes use of the writeBitString method to write the final encoding of 1's and
     * 0's to the encoded file.
     * 
     * @param encodings The array containing binary string encodings for each ASCII character
     * @param textFile The text file which is to be encoded
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public static void encodeFromArray(String[] encodings, String textFile, String encodedFile) {
        StdIn.setFile(textFile);
        String str = "";
        String input = StdIn.readAll();

        for(int i = 0; i < input.length(); i++){
            char curr = input.charAt(i);
            int index = (int) curr;
            if(encodings[index] == null) continue;
            str = str + encodings[index];
        }
        writeBitString(encodedFile, str);
    }
    
    /**
     * Using a given encoded file name and a huffman coding tree, this method makes use of the 
     * readBitString method to convert the file into a bit string, then decodes the bit string
     * using the tree, and writes it to a file.
     * 
     * @param encodedFile The file which contains the encoded text we want to decode
     * @param root The root of your Huffman Coding tree
     * @param decodedFile The file which you want to decode into
     */
    public static void decode(String encodedFile, TreeNode root, String decodedFile) {
        StdOut.setFile(decodedFile);
        String decode = readBitString(encodedFile);
        String str = traverse(root, decode);
        StdOut.print(str);
    }

    public static String traverse(TreeNode root, String input){
        TreeNode ptr = root;
        String str = "";
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == '0'){
                ptr = ptr.getLeft();
                if(ptr.getLeft() == null){
                    str = str + ptr.getData().getCharacter();
                    ptr = root;
                    continue;
                }
            }
            else if(input.charAt(i) == '1'){
                ptr = ptr.getRight();
                if(ptr.getRight() == null){
                    str = str + ptr.getData().getCharacter();
                    ptr = root;
                    continue;
                }
            }
        }
        return str;
    }

}
