package chess.aboveAndBeyond;
// 3/9/17
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import chess.aboveAndBeyond.comparators.MoveComparator;
import chess.bots.AlphaBetaSearcher;
import chess.bots.BestMove;

public class JamboreeSearcherIterativeDeepening<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	private static final int DIVIDECUTOFF = 5;
	private static final ForkJoinPool POOL = new ForkJoinPool();
	private static final double PERCENTAGE_SEQUENTIAL = 0.3;
	private int moveCounts = 0;
	
    public M getBestMove(B board, int myTime, int opTime) {
    	moveCounts++;
    	int levels = 5;
    	if (moveCounts > 70) {
    		levels = 8;
    	} else if (moveCounts > 45) {
    		levels = 7;
    	} else if (moveCounts > 3) {
    		levels = 6;
    	} 
    	System.err.println("Move: " + moveCounts + " PLY: " + levels);
    	M testMove = null;
    	List<M> moves = board.generateMoves();
    	Collections.sort(moves, new MoveComparator(board, evaluator));
    	for (int i = 1; i <= levels; i++) {
    		if (testMove != null) {
				moves.remove(testMove);
				moves.add(0, testMove);
			}
	    	testMove = ((BestMove<M>)(POOL.invoke(new JamboreeTask(board, moves, 
	    			DIVIDECUTOFF, cutoff, i, this.evaluator, 0, moves.size(), null, 
	    			-evaluator.infty(),evaluator.infty(), null, true, true)))).move; 
    	}
    	return testMove;
    }
    
    static class JamboreeTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
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
    	boolean firstEntry;
		
    	public JamboreeTask(B board, List<M> moves, int divideCutoff, 
    			int cutoff, int depth, Evaluator<B> evaluator, int lo, int hi,
    			M move, int alpha, int beta, BestMove<M> bestMove, boolean doSeq, boolean firstEntry) {
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
				return AlphaBetaSearcher.alphaBeta(evaluator, board, depth,
					alpha, beta);
			}
    		// sort moves for seq and parallel children
    		if (bestMove == null && !firstEntry) {
    			Collections.sort(moves, new MoveComparator(board, evaluator));
    		}
    		// sequential alpha beta
    		if (doSeq) {
    			lo = (int) Math.ceil(PERCENTAGE_SEQUENTIAL * hi);
    			BestMove<M> best = new BestMove<M>(-evaluator.infty());
    			for (int i = 0; i < lo; i++) {
    				M tryMove = moves.get(i);
    				board.applyMove(tryMove);
    				List<M> moveList = board.generateMoves();
    				JamboreeTask<M, B> task = new JamboreeTask<>(board, moveList, divideCutoff, 
    						cutoff, depth - 1, evaluator, 0, moveList.size(), 
    						null, -beta, -alpha, null, true, false);
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
							moves.get(i), -beta, -bestMove.value, null, true, false));
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
					cutoff, depth, evaluator, lo, mid, null, alpha, beta, bestMove, false, false);
			JamboreeTask<M, B> right = new JamboreeTask<> (board, moves, divideCutoff, 
					cutoff, depth, evaluator, mid, hi, null, alpha, beta, bestMove, false, false);
			right.fork();
			BestMove<M> leftMove = left.compute();
			BestMove<M> rightMove = right.join();
			return (leftMove.value > rightMove.value) ? leftMove : rightMove;
    	}
    }
}