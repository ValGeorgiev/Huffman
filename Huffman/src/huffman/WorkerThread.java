package huffman;

public class WorkerThread implements Runnable {
 
    private String command;
    private String text;
    private int[] charFreqs;
    private HuffmanTree tree;
    private Integer splitter;
    private int whichThread;
    private boolean flagQuiet;

    public WorkerThread(String s, String text, int[] charFreqs, HuffmanTree tree, Integer splitter, int whichThread, boolean flagQuiet){
        this.command = s;
        this.text = text;
        this.charFreqs = charFreqs;
        this.tree = tree;
        this.splitter = splitter;
        this.whichThread = whichThread;
        this.flagQuiet = flagQuiet;
    }

    @Override
    public void run() {
       
    	if(!flagQuiet){
    		System.out.println(Thread.currentThread().getName() + " Start. Command = " + command);
    	}
                   
        processCommand(charFreqs, text, tree, splitter, whichThread, flagQuiet);
       
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
	
	

    @Override
    public String toString(){
       return this.command;
    }
}
