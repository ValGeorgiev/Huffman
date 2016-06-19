package huffman;

public class WorkerThread implements Runnable {
 
    private String command;

    public WorkerThread(String s){
        this.command = s;
    }

    @Override
    public void run() {
    	int[] charFreqs;
    	String test;
        System.out.println(Thread.currentThread().getName() + " Start. Command = " + command);
		
        synchronized (this) {
            System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
            

            // we will assume that all our characters will have
            // code less than 256, for simplicity
        	charFreqs = new int[256];
    		test = "ABRACADABRA";
    		
        }
         
        // print out results
        
        charFreqs = processCommand(charFreqs, test);
       
        
        // build tree
        HuffmanTree tree = Huffman.buildTree(charFreqs);
        
        // print out results
        Huffman.printCodes(tree, new StringBuffer());
        
        System.out.println(Thread.currentThread().getName() + " End. ");

    }

	private int[] processCommand(int[] charFreqs, String test) {
				
        try {
            
            // read each character and record the frequencies
            for (char c : test.toCharArray())
                charFreqs[c]++;
            
           
        } catch (Exception e) {
            e.printStackTrace();            
        }
    	return charFreqs;
	}

    @Override
    public String toString(){
       return this.command;
    }
}
