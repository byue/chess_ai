package chess.aboveAndBeyond.comparators;

import java.util.Comparator;

import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

public class MoveComparator <M extends Move<M>, B extends Board<M, B>> implements Comparator<M> {
	B board;
	Evaluator<B> evaluator;
	
	public MoveComparator(B board, Evaluator<B> evaluator) {
		this.board = board;
		this.evaluator = evaluator;
	}
	
	@Override
	public int compare(M o1, M o2) {
		board.applyMove(o1);
		Integer v1 = evaluator.eval(board);
		board.undoMove();
		board.applyMove(o2);
		Integer v2 = evaluator.eval(board);
		board.undoMove();
		return v1.compareTo(v2);
	}
}