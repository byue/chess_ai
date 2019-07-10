package chess.experiments.ex3;

import chess.aboveAndBeyond.comparators.MoveComparator;
import chess.bots.BestMove;
import chess.experiments.SimpleTimer;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class JamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	private static AtomicInteger jamNodes = new AtomicInteger(0);
	private static final int DIVIDECUTOFF = 5;
	private static ForkJoinPool POOL = new ForkJoinPool();
	private static final double PERCENTAGE_SEQUENTIAL = 0.5;

	private long runtime = 0; 

	public void reset() {
		this.runtime = 0;
	}
	
	public long getRuntime() {
		return this.runtime;
	}

	public static void setProcessors(int num) {
	    POOL = new ForkJoinPool(num);
    }

    public static void resetProcessors() {
	    POOL = new ForkJoinPool();
    }

    public M getBestMove(B board, int myTime, int opTime) {
    	List<M> moves = board.generateMoves();
    	SimpleTimer.start();
    	M m = ((BestMove<M>)(POOL.invoke(new JamboreeTask(board, moves, DIVIDECUTOFF, cutoff, ply, this.evaluator, 0, moves.size(), null, -evaluator.infty(), evaluator.infty(), null, true)))).move;
    	this.runtime = SimpleTimer.stop();
    	return m;
    }
    
    class JamboreeTask <M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
    	int divideCutoff;
    	B board;
    	int depth;
    	Evaluator<B> evaluator;
    	List<M> moves; 
    	int hi;
    	int lo;
    	M move;
    	int alpha;
    	int beta;
    	int cutoff;
    	BestMove<M> bestMove;
    	boolean doSeq;
		
    	public JamboreeTask(B board, List<M> moves, int divideCutoff, 
    			int cutoff, int depth, Evaluator<B> evaluator, int lo, int hi,
    			M move, int alpha, int beta, BestMove<M> bestMove, boolean doSeq) {
    		this.board = board;
    		this.divideCutoff = divideCutoff;
    		this.depth = depth;
    		this.evaluator = evaluator;	
    		this.moves = moves;
    		this.lo = lo;
    		this.hi = hi;
    		this.move = move;
    		this.alpha = alpha;
    		this.beta = beta;
    		this.cutoff = cutoff;
    		this.bestMove = bestMove;
    		this.doSeq = doSeq;
    	}
    	
    	public BestMove<M> compute() {
    		// expensive operations for parallel children
    		if (move != null) {
    			board = board.copy();
        		board.applyMove(move);
        		jamNodes.addAndGet(1);
        		if (depth > cutoff && !moves.isEmpty()){
        			moves = board.generateMoves();
        			hi = moves.size();
        		}
    		}
    		// leaf case
			if (moves.isEmpty()) {
				if (board.inCheck()) {
					return new BestMove<M>(-evaluator.mate() - depth);
				} else {
					return new BestMove<M>(-evaluator.stalemate());
				}
			}  
    		// base case
    		if (depth <= cutoff) {
				BestMove<M> retMove = AlphaBetaSearcher.alphaBeta(evaluator, board, depth, alpha, beta);
				return retMove;
			}
    		// sort moves for seq and parallel children
    		if (bestMove == null) {
    			Collections.sort(moves, new MoveComparator(board, evaluator));
    		}
    		// sequential alpha beta
    		if (doSeq) {
    			lo = (int) Math.ceil(PERCENTAGE_SEQUENTIAL * hi);
    			BestMove<M> best = new BestMove<M>(-evaluator.infty());
    			for (int i = 0; i < lo; i++) {
    				M tryMove = moves.get(i);
    				board.applyMove(tryMove);
    				jamNodes.addAndGet(1);
    				List<M> moveList = board.generateMoves();
    				JamboreeTask<M, B> task = new JamboreeTask<>(board, moveList, divideCutoff, 
    						cutoff, depth - 1, evaluator, 0, moveList.size(), 
    						null, -beta, -alpha, null, true);
    				int value = task.compute().negate().value;
    				board.undoMove();
    				if (value > alpha) {
    					alpha = value;
    					best.value = value;
    					best.move = tryMove;
    				}
    				if (alpha >= beta) {
    					return best;
    				}	
    			}
    			bestMove = best;
    		}
    		// parallel alpha beta, takes bestMove from divide and conquer as researched move/alpha value
    		else if (hi - lo <= divideCutoff) {
    			ArrayList<JamboreeTask<M,B>> tasks = new ArrayList<JamboreeTask<M,B>>();
				for (int i = lo; i < hi; i++) {
					tasks.add(new JamboreeTask<M,B> (board, moves, divideCutoff, 
							cutoff, depth - 1, evaluator, 0, 0, 
							moves.get(i), -beta, -bestMove.value, null, true));
					if (i != lo) {
						tasks.get(i - lo).fork();
					}
				}
				for (int i = 0; i < tasks.size(); i++) {
					int alphaVal;
					if (i == 0) {
						alphaVal = tasks.get(i).compute().negate().value;
					} else {
						alphaVal = tasks.get(i).join().negate().value;
					}
					if (alphaVal > bestMove.value) {
						bestMove.value = alphaVal;
						bestMove.move = moves.get(i + lo);
					}
					if (bestMove.value >= beta){
						return bestMove;
					}	
				}
				return bestMove;
    		}
    		// divide and conquer
    		int mid = lo + (hi - lo) / 2;
			JamboreeTask<M, B> left = new JamboreeTask<> (board, moves, divideCutoff,
					cutoff, depth, evaluator, lo, mid, null, alpha, beta, bestMove, false);
			JamboreeTask<M, B> right = new JamboreeTask<> (board, moves, divideCutoff, 
					cutoff, depth, evaluator, mid, hi, null, alpha, beta, bestMove, false);
			right.fork();
			BestMove<M> leftMove = left.compute();
			BestMove<M> rightMove = right.join();
			return (leftMove.value > rightMove.value) ? leftMove : rightMove;
    	}
    }
}