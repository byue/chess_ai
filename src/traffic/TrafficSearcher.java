package traffic;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;

public class TrafficSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
    	return AlphaBetaSearcher.alphaBeta(this.evaluator, 
    			board, ply, -evaluator.infty(), evaluator.infty()).move;
    }
}