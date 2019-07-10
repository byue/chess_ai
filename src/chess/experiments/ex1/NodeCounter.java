package chess.experiments.ex1;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
//
public class NodeCounter {
	public static final int DEPTH = 5;

    public static void getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) { 
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }
    
    public static void main(String[] args) throws IOException {
    	BufferedReader br = new BufferedReader(new FileReader("/src/chess/experiments/ex1/fenEx.txt"));
    	//"/home/yue_bryan123/p3-lassi/src/chess/experiments/ex1/fenEx.txt"
        ArrayList<String> fens = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
        	fens.add(line);
        }
        int moves = fens.size();
        ArrayList<Integer> simpleCounts = new ArrayList<Integer>();
        ArrayList<Integer> paraCounts = new ArrayList<Integer>();
        ArrayList<Integer> alphaCounts = new ArrayList<Integer>();
        ArrayList<Integer> jamCounts = new ArrayList<Integer>();
        for (int i = 1; i <= DEPTH; i++) {
        	if (i - 1 > 0) {
        		System.err.println("DEPTH " + (i - 1) + " DONE");
        	}
	        for (String fen : fens) {
	        	 Searcher<ArrayMove, ArrayBoard> simple = new SimpleSearcher<>();
	             Searcher<ArrayMove, ArrayBoard> para = new ParallelSearcher<>();
	             Searcher<ArrayMove, ArrayBoard> alpha = new AlphaBetaSearcher<>();
	             Searcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
	        	((SimpleSearcher<ArrayMove, ArrayBoard>) simple).reset();
	        	((ParallelSearcher<ArrayMove, ArrayBoard>) para).reset();
	        	((AlphaBetaSearcher<ArrayMove, ArrayBoard>) alpha).reset();
	        	((JamboreeSearcher<ArrayMove, ArrayBoard>) jam).reset();
	        	getBestMove(fen, simple, i, Math.max(i/2, 1));
	        	getBestMove(fen, para, i, Math.max(i/2, 1));	        
	        	getBestMove(fen, alpha, i, Math.max(i/2, 1));
	        	getBestMove(fen, jam, i, Math.max(i/2, 1));
	        	simpleCounts.add(((SimpleSearcher<ArrayMove, ArrayBoard>) simple).getSteps());
	        	paraCounts.add(((ParallelSearcher<ArrayMove, ArrayBoard>) para).getSteps());
	        	alphaCounts.add(((AlphaBetaSearcher<ArrayMove, ArrayBoard>) alpha).getSteps());
	        	jamCounts.add(((JamboreeSearcher<ArrayMove, ArrayBoard>) jam).getSteps());
	        }
        }
        PrintStream output = new PrintStream(new File("src/chess/experiments/ex1/nodes.csv"));
        for (int i = 0; i < DEPTH; i++) {
        	String header = "Ply " + (i+1) + " moves,";
        	for (int m = 0; m < moves; m++) {
        		header += (m + 1) + ",";
        	}
        	output.println(header);
	        for (int k = 0; k < 4; k++) {
	        	String a = "";
	        	if (k == 0) {
	        		a += "Minimax,";
	        		for (int j = i * 64; j < (i * 64) + 64; j++) {
	            		a += (paraCounts.get(j) + ",");
	            	}
	        	} else if (k == 1) {
	        		a += "Parallel Minimax,";
	        		for (int j = i * 64; j < (i * 64) + 64; j++) {
	            		a += (paraCounts.get(j) + ",");
	            	}
	        	} else if (k == 2) {
	        		a += "Alphabeta,";
	        		for (int j = i * 64; j < (i * 64) + 64; j++) {
	            		a += (alphaCounts.get(j) + ",");
	            	}
	        	} else {
	        		a += "Jamboree,";
	        		for (int j = i * 64; j < (i * 64) + 64; j++) {
	            		a += (jamCounts.get(j) + ",");
	            	}
	        	}
	        	output.println(a);
	        }
        }
        output.println("Ply,Minimax Avg,ParallelMinimax Avg,AlphaBeta Avg,Jamboree Avg");
        for (int m = 0; m < DEPTH; m++) {
	        String b = "" + (m + 1) + ",";
	        
        	Double mavg = 0.0;
	        for (int i = m*64; i < m*64 + 64; i++) {
	        	mavg += simpleCounts.get(i);
	        }
	        mavg /= moves;
	        b += (mavg + ",");
	        
	        Double pavg = 0.0;
	        for (int i = m*64; i < m*64 + 64; i++) {
	        	pavg += paraCounts.get(i);
	        }
	        pavg /= moves;
	        b += (pavg + ",");
	        
	        Double aavg = 0.0;
	        for (int i = m*64; i < m*64 + 64; i++) {
	        	aavg += alphaCounts.get(i);
	        }
	        aavg /= moves;
	        b += (aavg + ",");
	        
	        Double javg = 0.0;
	        for (int i = m*64; i < m*64 + 64; i++) {
	        	javg += jamCounts.get(i);
	        }
	        javg /= moves;
	        b += (javg + ",");
	        
	        output.println(b);   
        }
        
        br.close();
        System.err.println("FILE DONE");
    }
}
