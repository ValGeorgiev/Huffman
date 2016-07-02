package huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Huffman implements Callable<String> {
	
    private String command;
    private String text;
    private int[] charFreqs;
    private HuffmanTree tree;
    private Integer splitter;
    private int whichThread;
    private boolean flagQuiet;

    public Huffman(String s, String text, int[] charFreqs, HuffmanTree tree, Integer splitter, int whichThread, boolean flagQuiet){
        this.command = s;
        this.text = text;
        this.charFreqs = charFreqs;
        this.tree = tree;
        this.splitter = splitter;
        this.whichThread = whichThread;
        this.flagQuiet = flagQuiet;
    }

	
	public static HuffmanTree buildTree(int[] charFreqs) {
		PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
		// initially, we have a forest of leaves
		// one for each non-empty character
		for (int i = 0; i < charFreqs.length; i++) {
			if (charFreqs[i] > 0) {
				trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));
			}
		}
		 
		assert trees.size() > 0;
		// loop until there is only one tree left
		while (trees.size() > 1) {
			// two trees with least frequency
			HuffmanTree a = trees.poll();
			HuffmanTree b = trees.poll();
		
			// put into new node and re-insert into queue
			trees.offer(new HuffmanNode(a, b));
		}
		return trees.poll();
	}
	
	public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
		assert tree != null;
		if (tree instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf)tree;
	
			// print out character, frequency, and code for this leaf (which is just the prefix)
	        System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
	        
		} else if (tree instanceof HuffmanNode) {
			HuffmanNode node = (HuffmanNode)tree;
	
			// traverse left
			prefix.append('0');
			printCodes(node.left, prefix);
			prefix.deleteCharAt(prefix.length()-1);
	
			// traverse right
			prefix.append('1');
			printCodes(node.right, prefix);
			prefix.deleteCharAt(prefix.length()-1);
		}
	}
	
	public static String reader(String filename) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		try{
			String currentLine;
			br = new BufferedReader(new FileReader(filename));
			
			while((currentLine = br.readLine()) != null){
				sb.append(currentLine);
				sb.append("\n");
			}
			return sb.toString();
		}
		finally{
			if(br != null){
				br.close();
			}
		}
	}
	
    @Override
    public String call() throws Exception {
    	if(!flagQuiet){
    		System.out.println(Thread.currentThread().getName() + " Start. Command = " + command);
    	}
                   
        processCommand(charFreqs, text, tree, splitter, whichThread, flagQuiet);        //return the thread name executing this callable task
        return "";
    }
    
    private void processCommand(int[] charFreqs, String text, HuffmanTree tree, Integer splitter, int whichThread, boolean flagQuiet) {
		int wordLen = text.length();
		int limit = wordLen / splitter;
		int upperBound = whichThread * limit;
		int index;
		
		if(splitter.equals(whichThread)){
			upperBound = wordLen;
		}
		
        try {
            
            // read each character and record the frequencies
            for (index = (whichThread - 1) * limit; index < upperBound; index++){
            	synchronized (this){
            		charFreqs[text.toCharArray()[index]]++;		
            	}
        	}                
           
        } catch (Exception e) {
            e.printStackTrace();            
        }
        
        // build tree
        tree = Huffman.buildTree(charFreqs);
        
        if(!flagQuiet){
	        // print out results
	        Huffman.printCodes(tree, new StringBuffer()); 
	           
	        System.out.println(Thread.currentThread().getName() + " End. ");
        }
	    return;
	}
    
    public static void main(String args[]){
    	long startTime = System.currentTimeMillis();
    	String text = null,
    		threads = null,
    		filename = null;
    	boolean flagQuiet = false;

    	if(args.length > 0){
    		for(int ind = 0; ind < args.length; ind++){
    			if(args[ind].contentEquals("-f")){
    				filename = args[ind + 1];
    				try{
    					text = reader(filename);
    					//assignedText = !assignedText;
    				} catch (IOException ioe) {
    					System.out.println("Problem reading from file: " + ioe.getMessage());
    				}
    			}
    			if(args[ind].contentEquals("-t") || args[ind].contentEquals("-tasks")){
    				threads = args[ind + 1];
    				//assignedThreads = !assignedThreads;
    			}
    			if(args[ind].contentEquals("-q") || args[ind].contentEquals("-quiet")){
    				flagQuiet = true;
    			}
    		}
    	}

    	Scanner scanIn = new Scanner(System.in);
    	if(text == null){
	    	System.out.println("Enter some text: ");
	    	
	    	text = scanIn.nextLine();
    	}

        if(threads == null){
	    	System.out.println("Enter how many threads you want to work for you: ");
	        
	        threads = scanIn.nextLine();            
        }
        scanIn.close();
	        
    	int[] charFreqs = new int[256];
    	
    	int threadsCounter = Integer.parseInt(threads);
    	
    	ExecutorService executor = Executors.newFixedThreadPool(threadsCounter);
    	
		HuffmanTree tree = Huffman.buildTree(charFreqs);
    	
        //create a list to hold the Future object associated with Callable
        List<Future<String>> list = new ArrayList<Future<String>>();
        //Create Huffman instance
        for(int i = 0; i < threadsCounter; i++){
            Callable<String> callable = new Huffman("" + i, text, charFreqs, tree, threadsCounter, i + 1, flagQuiet);

            //submit Callable tasks to be executed by thread pool
            Future<String> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<String> fut : list){
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
            		fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
        if(!flagQuiet){

        	System.out.println("Finished all threads");  
        }
        
      	long endTime   = System.currentTimeMillis();
  		long totalTime = endTime - startTime;
  		System.out.println("Total time for tree building: " + totalTime + " miliseconds");
    }

}