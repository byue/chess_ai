package chess.experiments.ex3;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FindBestProcessor {
	// two searcher
    static ParallelSearcher<ArrayMove, ArrayBoard> para = new ParallelSearcher<>();
	static JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
	// runtime with different number of processors
	static long[] runtimes = new long[32];
	static long bestSoFar;
	private static final int TRAILS = 100;

	// use depth 5, cutoff 2
	public static void getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher) {
        searcher.setDepth(5);
        searcher.setCutoff(2);
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    public static void setProcessors(int num) {
		System.out.println("num is " + num);
		ParallelSearcher.setProcessors(num);
		JamboreeSearcher.setProcessors(num);
	}

	public static void wramUp(String fen) {
		JamboreeSearcher<ArrayMove, ArrayBoard> jam = new JamboreeSearcher<>();
		// loop for JVM wram-up
		for(int i = 0; i < 25; i++) {
			getBestMove(fen, jam);
		}
		System.out.println("Wram up Finished");
	}

	public static long getRuntime(String fen, int numOfPro, AbstractSearcher searcher) {
	    setProcessors(numOfPro);
	    // move
		searcher.reset();
        getBestMove(fen, searcher);
        return searcher.getRuntime();
	}

	public static long[][] getRuntime(String fen, AbstractSearcher searcher) {
		// run test 100 times and take the average
		long[][] runtimes = new long[2][32];

		System.out.println("Taking Runtime");
		for(int i = 0; i < TRAILS; i++) {
		    for(int core = 1; core <= 32; core++) {
		    	runtimes[0][core - 1] += getRuntime(fen, core, para);
				runtimes[1][core - 1] += getRuntime(fen, core, jam);
			}
            /*// number of processors
            int minP = 1;
            int maxP = 32;
            int midP;
            // runtime
            long minR, maxR, midR;

			// get to the right range;
			int nextP = minP;
			// first runtime
			long nextR = 0;
			while(nextP <= maxP) {
				long temp = getRuntime(fen, nextP, searcher);
				// record runtime
				runtimes[nextP - 1] = temp;
				divide[nextP - 1] += 1;
			    if(temp > nextR && nextP != 1) {
			    	break;
				}
				nextR = temp;
				nextP *= 2;
			}
			System.out.println("Found Bound!");
			// search!
			if(nextP == 64) {
                minP = nextP / 4;
			} else {
				minP = nextP / 2;
			}
			// always check up to 32 processors
			maxP = 32;
			// binary search for the best
            nextR = 0;
			while(minP <= maxP) {
			    long temp = getRuntime(fen, minP, searcher);
				runtimes[minP - 1] = temp;
				divide[minP - 1]++;
				// min runtime
				nextR = Math.min(nextR, temp);
				minP++;
			}*/
			System.out.println("One Trial Finished!");
		}
		System.out.println("Taking Runtime Done");

		// take average
		System.out.println("Taking avg");
		for(int i = 0; i < runtimes[0].length; i++) {
			runtimes[0][i] /= TRAILS;
			runtimes[1][i] /= TRAILS;
		}
		System.out.println("Taking avg Done");
		// return
		return runtimes;
	}

    public static void main(String[] args) throws IOException {
    	// fen to test: beginning middle end
    	String[] fens = {"rnbqkbnr/pp1ppppp/8/2p5/8/2N5/PPPPPPPP/R1BQKBNR w KQkq c6",
    					 "r3k2r/pp5p/2n1p1p1/q1pp1p2/5B2/2PP1Q2/P1P2PPP/R4RK1 b Hkq -",
    					 "2k3r1/p6p/2n5/3pp3/1pp5/2qPP3/P1P1K2P/R1R5 w Hh -"};

    	// wram up JVM
    	wramUp(fens[0]);
		// /home/jiangpreston04/p3-lassi/src/chess/experiments/ex2/SeqCutoff.csv
        PrintStream output = new PrintStream(new File("/home/yue_bryan123/p3-lassi/src/chess/experiments/ex3/Processors.csv"));
        //--------------------------------------------------------
        output.println("Beginning");
        output.println("NumProce" + "," + "Parallel" + "," + "Jamboree");
        long[][] testBegin = getRuntime(fens[0], para);
		//long[] testBeginJam = getRuntime(fens[0], jam);
		for(int j = 0; j < testBegin[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testBegin[0][j]) + "," + String.valueOf(testBegin[1][j]));
		}
		output.println();
		//--------------------------------------------------------
		output.println("Middle");
        output.println("NumProce" + "," + "Parallel" + "," + "Jamboree");
        long[][] testMid = getRuntime(fens[1], para);
		//long[] testMidJam = getRuntime(fens[1], jam);
		for(int j = 0; j < testMid[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testMid[0][j]) + "," + String.valueOf(testMid[1][j]));
		}
		output.println();
		//--------------------------------------------------------
		output.println("End");
        output.println("NumProce" + "," + "Parallel" + "," + "Jamboree");
        long[][] testEnd = getRuntime(fens[2], para);
		//long[] testEndJam = getRuntime(fens[2], jam);
		for(int j = 0; j < testEnd[0].length; j++) {
			output.println((j + 1) + "," + String.valueOf(testEnd[0][j]) + "," + String.valueOf(testEnd[1][j]));
		}
		output.println();
		System.out.println("FILE DONE");
    }
}
