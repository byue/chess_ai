package chess.experiments.ex1;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

import java.util.concurrent.RecursiveTask;
import cse332.chess.interfaces.Evaluator;
import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.BestMove;
import java.util.concurrent.atomic.AtomicInteger;

// 2/21
public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
	public AtomicInteger paraNodes = new AtomicInteger(0);
	private static final int divideCutoff = 4;
	private static final ForkJoinPool POOL = new ForkJoinPool();
	
	public M getBestMove(B board, int myTime, int opTime) {
		paraNodes = new AtomicInteger(0);
		List<M> moves = board.generateMoves();
    	return (POOL.invoke(new GetBestMoveTask(board, moves, cutoff, 
    			divideCutoff, ply, this.evaluator, 0, moves.size(), null))).move; 
    }
	
	public void reset() {
    	paraNodes = new AtomicInteger(0);
    }
	
	public int getSteps() {
    	return paraNodes.get();
    }
	
	class GetBestMoveTask extends RecursiveTask<BestMove<M>> {
    	int cutoff;
    	int divideCutoff;
    	B board;
    	int depth;
    	Evaluator<B> evaluator;
    	List<M> moves; 
    	int hi;
    	int lo;
    	M move;
		
    	public GetBestMoveTask(B board, List<M> moves, int cutoff, int divideCutoff, 
    			int depth, Evaluator<B> evaluator, int lo, int hi, M move) {
    		this.board = board;
    		this.cutoff = cutoff;
    		this.divideCutoff = divideCutoff;
    		this.depth = depth;
    		this.evaluator = evaluator;	
    		this.moves = moves;
    		this.lo = lo;
    		this.hi = hi;
    		this.move = move;
    	}
    	
    	public BestMove<M> compute() {
    		// expensive operations for children
    		if (move != null) {
    			board = board.copy();
        		board.applyMove(move);
        		paraNodes.addAndGet(1);
        		moves = board.generateMoves();
        		hi = moves.size();	
    		}
    		// base case, switch to sequential
			if (depth <= cutoff || moves.isEmpty()) {
				Searcher<ArrayMove, ArrayBoard> simple = new SimpleSearcher<>();
				BestMove<M> retMove = ((SimpleSearcher<ArrayMove, ArrayBoard>) simple).minimax(evaluator, board, depth);
				int counts = ((SimpleSearcher<ArrayMove, ArrayBoard>) simple).getSteps();
				paraNodes.addAndGet(counts);
				return retMove;
			}
			// stop dividing, make next moves
			if (hi - lo <= divideCutoff) {
				// fork all moves and return best move in parallel for each move
				BestMove<M> bestMove = new BestMove<M>(-evaluator.infty());
				ArrayList<GetBestMoveTask> tasks = new ArrayList<GetBestMoveTask>();
				for (int i = lo; i < hi; i++) {
					tasks.add(new GetBestMoveTask (board, moves, cutoff, 
							divideCutoff, depth - 1, evaluator, 0, 0, moves.get(i)));
				}
				for (int i = 1; i < tasks.size(); i++) {
					tasks.get(i).fork();
				}
				for (int i = 0; i < tasks.size(); i++) {
					int candidateVal;
					if (i == 0) {
						candidateVal = tasks.get(i).compute().negate().value;
					} else {
						candidateVal = tasks.get(i).join().negate().value;
					}
					if (candidateVal > bestMove.value) {
						bestMove.move = moves.get(i + lo);
						bestMove.value = candidateVal;
					}	
				}
				return bestMove;
			} 
			// divide and conquer move list in half. 
			int mid = lo + (hi - lo) / 2;
			GetBestMoveTask left = new GetBestMoveTask (board, moves, cutoff, 
					divideCutoff, depth, evaluator, lo, mid, null);
			GetBestMoveTask right = new GetBestMoveTask (board, moves, cutoff, 
					divideCutoff, depth, evaluator, mid, hi, null);
			right.fork();
			BestMove<M> leftMove = left.compute();
			BestMove<M> rightMove = right.join();
			return (leftMove.value > rightMove.value) ? leftMove : rightMove;		
    	}
    }
}