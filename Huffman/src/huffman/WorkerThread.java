package huffman;

public class WorkerThread implements Runnable {
 
    private String command;
    private String text;
    private int[] charFreqs;
    private HuffmanTree tree;

    public WorkerThread(String s, String text, int[] charFreqs, HuffmanTree tree){
        this.command = s;
        this.text = text;
        this.charFreqs = charFreqs;
        this.tree = tree;
    }

    @Override
    public void run() {
       
    	System.out.println(Thread.currentThread().getName() + " Start. Command = " + command);
                   
        processCommand(charFreqs, text, tree);
       
    }

	private void processCommand(int[] charFreqs, String text, HuffmanTree tree) {
				
        try {
            
            // read each character and record the frequencies
            for (char c : text.toCharArray()){
            	synchronized (this){
            		charFreqs[c]++;		
            	}
        	}                
           
        } catch (Exception e) {
            e.printStackTrace();            
        }
        
        // build tree
        tree = Huffman.buildTree(charFreqs);
        
        // print out results
        Huffman.printCodes(tree, new StringBuffer()); 
           
        System.out.println(Thread.currentThread().getName() + " End. ");
    	return;
	}
	
	

    @Override
    public String toString(){
       return this.command;
    }
}
