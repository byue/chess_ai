package chess.experiments.ex2;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

public class ExperimentTimer {

	// use depth 5
	public static void getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int cutoff) { 
        searcher.setDepth(5);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

	public static void wramUp(String fen) {
		JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
		// loop for JVM wram-up
		System.out.println("Wram up");
		for(int i = 0; i < 100; i++) {
			getBestMove(fen, jam, 3);
		}
		System.out.println("Wram up done!");
	}
	
	public static long[][] testCase(String fen, int[] cutoffs) {
		ParallelSearcher<ArrayMove, ArrayBoard> para = new ParallelSearcher<>();
		JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
		// run test 25 times and take the average
		long[][] runtimes = new long[2][4]; 
		for(int i = 0; i < 25; i++) {
			// cutoff 1 to 4
			for(int cutoff : cutoffs) {
				para.reset();
				jam.reset();
				getBestMove(fen, para, cutoff);
				getBestMove(fen, jam, cutoff);
				runtimes[0][cutoff-1] += para.getRuntime();
				runtimes[1][cutoff-1] += jam.getRuntime();
			}
			System.out.println("One Trial Finished!");
		}
		// take average
		for(int i = 0; i < runtimes.length; i++) {
			for(int j = 0; j < runtimes[0].length; j++) {
				System.out.println("Taking avg");
				runtimes[i][j] /= 25;
			}
		}
		// return
		return runtimes;
	}
    
    public static void main(String[] args) throws IOException {
    	// cutoff to test
    	int[] cutoffs = {1, 2, 3, 4};
    	// fen to test: beginning middle end
    	String[] fens = {"rnbqkbnr/pp1ppppp/8/2p5/8/2N5/PPPPPPPP/R1BQKBNR w KQkq c6",
    					 "r3k2r/pp5p/2n1p1p1/q1pp1p2/5B2/2PP1Q2/P1P2PPP/R4RK1 b Hkq -",
    					 "2k3r1/p6p/2n5/3pp3/1pp5/2qPP3/P1P1K2P/R1R5 w Hh -"};

    	// wram up JVM
    	wramUp(fens[0]);

        PrintStream output = new PrintStream(new File("/home/jiangpreston04/p3-lassi/src/chess/experiments/ex2/SeqCutoff.csv"));
        //--------------------------------------------------------
        output.println("Beginning");
        output.println("Cutoff" + "," + "Parallel" + "," + "Jamboree");
        long[][] testBegin = testCase(fens[0], cutoffs);
		for(int j = 0; j < testBegin[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testBegin[0][j]) + "," + String.valueOf(testBegin[1][j]));
		}
		output.println();
		//--------------------------------------------------------
		output.println("Middle");
        output.println("Cutoff" + "," + "Parallel" + "," + "Jamboree");
        long[][] testMiddle = testCase(fens[1], cutoffs);
		for(int j = 0; j < testMiddle[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testMiddle[0][j]) + "," + String.valueOf(testMiddle[1][j]));
		}
		output.println();
		//--------------------------------------------------------
		output.println("End");
        output.println("Cutoff" + "," + "Parallel" + "," + "Jamboree");
        long[][] testEnd = testCase(fens[2], cutoffs);
		for(int j = 0; j < testEnd[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testEnd[0][j]) + "," + String.valueOf(testEnd[1][j]));
		}
		output.println();
		System.out.println("FILE DONE");
    }
}
