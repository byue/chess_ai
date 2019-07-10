package chess.experiments.ex3;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.List;

public class AlphaBetaSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
	
	public M getBestMove(B board, int myTime, int opTime) {
    	return alphaBeta(this.evaluator, board, ply, -evaluator.infty(), evaluator.infty()).move;
    }
    
    public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> 
			alphaBeta(Evaluator<B> evaluator, B board, int depth, int alpha, int beta) {
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
		BestMove<M> best = new BestMove<M>(alpha);
		for (M move : moves) {
			board.applyMove(move);
			int value = (alphaBeta(evaluator, board, depth - 1, -beta, -alpha)).negate().value;
			board.undoMove();
			if (value > alpha) {
				alpha = value;
				best.value = alpha;
				best.move = move;
			}
			if (alpha >= beta){
				return best;
			}		
		}
		return best;
	}
}