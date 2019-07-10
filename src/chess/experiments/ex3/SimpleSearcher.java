package chess.experiments.ex3;
// 2/20/17

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.List;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        return minimax(this.evaluator, board, ply).move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> 
    		minimax(Evaluator<B> evaluator, B board, int depth) {
    	if (depth == 0) {
    		return new BestMove<M>(evaluator.eval(board));
    	} 
    	List<M> moves = board.generateMoves();
    	if (moves.isEmpty()) {
    		if (board.inCheck()) {
    			return new BestMove<M>(-evaluator.mate() - depth);
    		} else {
    			return new BestMove<M>(-evaluator.stalemate());
    		}
    	}  
    	BestMove<M> best = new BestMove<M>(-evaluator.infty());
    	for (M move : moves) {
    		board.applyMove(move);
    		int value = (minimax(evaluator, board, depth - 1)).negate().value;
    		board.undoMove();
    		if (value > best.value) {
    			best.value = value;
    			best.move = move;
    		}
    	}
    	return best;
    }
}