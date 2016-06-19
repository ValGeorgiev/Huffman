package huffman;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;


public class Huffman {
    // input is an array of frequencies, indexed by character code
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

    public static synchronized void printCodes(HuffmanTree tree, StringBuffer prefix) {
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

    public static void main(String[] args) {
    	System.out.println("Enter how many threads you want to work for you: ");
        
    	String threads;
      
        Scanner scanIn = new Scanner(System.in);
        threads = scanIn.nextLine();

        scanIn.close();            

    	ExecutorService executor = Executors.newFixedThreadPool(5);
       
    	for (int i = 0; i < Integer.parseInt(threads); i++) {
            Runnable worker = new WorkerThread("" + i);
            executor.execute(worker);
        }
       
    	executor.shutdown();
        while (!executor.isTerminated()) {}
        
        System.out.println("Finished all threads");
        
       
        
    }
}