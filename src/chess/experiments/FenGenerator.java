package chess.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.AlphaBetaSearcher;
import chess.bots.LazySearcher;
import chess.bots.SimpleSearcher;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FenGenerator {
	public static final String FILE1 = "src/chess/experiments/fenEx.txt";
	PrintStream output = new PrintStream(new File(FILE1));
    public Searcher<ArrayMove, ArrayBoard> whitePlayer;
    public Searcher<ArrayMove, ArrayBoard> blackPlayer;
    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    private ArrayBoard board;
    
    public static void main(String[] args) throws FileNotFoundException {
        FenGenerator game = new FenGenerator();
        game.play();
        System.err.println("FILE DONE");
    }

    public FenGenerator() throws FileNotFoundException {
        setupWhitePlayer(new SimpleSearcher<ArrayMove, ArrayBoard>(), 4, 4);
        setupBlackPlayer(new AlphaBetaSearcher<ArrayMove, ArrayBoard>(), 4, 4);
    }
    
    public void play() throws FileNotFoundException {
       PrintStream output = new PrintStream(new File("src/chess/experiments/fenEx.txt"));
       this.board = ArrayBoard.FACTORY.create().init(STARTING_POSITION);
       Searcher<ArrayMove, ArrayBoard> currentPlayer = this.blackPlayer;
       
       int turn = 0;
       
       /* Note that this code does NOT check for stalemate... */
       while (!board.inCheck() || board.generateMoves().size() > 0) {
           currentPlayer = currentPlayer.equals(this.whitePlayer) ? this.blackPlayer : this.whitePlayer;
           output.println(board.fen());
           System.out.printf("%3d: " + board.fen() + "\n", turn);
           this.board.applyMove(currentPlayer.getBestMove(board, 1000, 1000));
           turn++;
       }
       output.close();
    }
    
    public Searcher<ArrayMove, ArrayBoard> setupPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        return searcher; 
    }
    public void setupWhitePlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.whitePlayer = setupPlayer(searcher, depth, cutoff);
    }
    public void setupBlackPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.blackPlayer = setupPlayer(searcher, depth, cutoff);
    }
}