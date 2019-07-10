package chess.experiments.ex4;
//
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

public class ExperimentTimer {
	public static final int TRIALS = 100;
	public static final int CORES = 10;
	public static final int CUTOFF = 2;
	public static final int DEPTH = 5;
	// use depth 5, cutoff 2
	public static void getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int cutoff) { 
        searcher.setDepth(DEPTH);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

	public static void wramUp(String fen) {
		JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
		// loop for JVM wram-up
		for(int i = 0; i < 100; i++) {
			getBestMove(fen, jam, 3);
		}
	}
    
    public static void main(String[] args) throws IOException {
    	// fen to test: beginning middle end
    	String[] fens = {"rnbqkbnr/pp1ppppp/8/2p5/8/2N5/PPPPPPPP/R1BQKBNR w KQkq c6",
    					 "r3k2r/pp5p/2n1p1p1/q1pp1p2/5B2/2PP1Q2/P1P2PPP/R4RK1 b Hkq -",
    					 "2k3r1/p6p/2n5/3pp3/1pp5/2qPP3/P1P1K2P/R1R5 w Hh -"};
    	// wram up JVM
    	wramUp(fens[0]);
        int counter = 0;
    	ParallelSearcher<ArrayMove, ArrayBoard> para = new ParallelSearcher<>();
		JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
		AlphaBetaSearcher<ArrayMove, ArrayBoard> alpha = new AlphaBetaSearcher<>();
		SimpleSearcher<ArrayMove, ArrayBoard> simple = new SimpleSearcher<>();
		ParallelSearcher.setProcessors(CORES);
		JamboreeSearcher.setProcessors(CORES);
        ArrayList<Long> simpleTime = new ArrayList<Long>();
        ArrayList<Long> paraTime = new ArrayList<Long>();
        ArrayList<Long> alphaTime = new ArrayList<Long>();
        ArrayList<Long> jamTime = new ArrayList<Long>();       
        for (String fen : fens) {
        	counter++;
        	System.err.println("Board State: " + counter);
        	for (int i = 0; i < TRIALS; i++) {
        		para.reset();
    			jam.reset();
    			alpha.reset();
    			simple.reset();
    			getBestMove(fen, para, 3);
    			getBestMove(fen, jam, CUTOFF); 
    			getBestMove(fen, alpha, CUTOFF);;
    			getBestMove(fen, simple, CUTOFF);
        		simpleTime.add(simple.getRuntime());
        		paraTime.add(para.getRuntime());
        		alphaTime.add(alpha.getRuntime());
        		jamTime.add(jam.getRuntime());
        	}	
        }
        PrintStream output = new PrintStream(new File 
        		("/home/yue_bryan123/p3-lassi/src/chess/experiments/ex4/Algos.csv"));
        		//("/home/jiangpreston04/p3-lassi/src/chess/experiments/ex4/Algos.csv"));
        		//("src/chess/experiments/ex4/Algos.csv"));   
        for (int k = 0; k < 3; k++) {
        	String a = "";
            if (k == 0) {
            	a += "Early Game,";
            } else if (k == 1) {
            	a += "Middle Game,";
            } else {
            	a += "End Game,";
            }
	        for (int i = 1; i <= TRIALS; i++) {
	        	a += (i + ",");
	        }
	        output.println(a);     
	        for (int i = 0; i < 4; i++) {
	        	String b = "";
	        	if (i == 0) {
	        		b += "Minimax,";
	        	} else if (i == 1) {
	        		b += "Parallel Minimax,";
	        	} else if (i == 2) {
	        		b += "Alphabeta,";
	        	} else {
	        		b += "Jamboree,";
	        	}
	        	for (int j = 0; j < TRIALS; j++) {
	        		if (i == 0) {
	            		b += (simpleTime.get((k*TRIALS) + j) + ",");
	            	} else if (i == 1) {
	            		b += (paraTime.get((k*TRIALS) + j) + ",");
	            	} else if (i == 2) {
	            		b += (alphaTime.get((k*TRIALS) + j) + ",");
	            	} else {
	            		b += (jamTime.get((k*TRIALS) + j) + ",");
	            	}
	        	}
	        	output.println(b);
	        }
        }
        output.println("Algorithm,Early Avg,Middle Avg,End Avg");
        // algo type
        for (int i = 0; i < 4; i++) {
        	String b = "";
        	if (i == 0) {
        		b += "Minimax,";
        	} else if (i == 1) {
        		b += "Parallel Minimax,";
        	} else if (i == 2) {
        		b += "Alphabeta,";
        	} else {
        		b += "Jamboree,";
        	}
        	// game state
        	for (int j = 0; j < 3; j++) {
        		long total = 0;
        		// trials in a state
        		for (int m = 0; m < TRIALS; m++) {
        			if (i == 0) {
                		total += simpleTime.get((j*TRIALS) + m);
                	} else if (i == 1) {
                		total += paraTime.get((j*TRIALS) + m);
                	} else if (i == 2) {
                		total += alphaTime.get((j*TRIALS) + m);
                	} else {
                		total += jamTime.get((j*TRIALS) + m);
                	}
        		}
        		double avg = (double) total / TRIALS;
        		b += (avg + ",");
        	}
        	output.println(b);  	
        }    
		System.err.println("FILE DONE");
    }
}
