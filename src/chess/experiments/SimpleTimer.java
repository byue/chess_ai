package chess.experiments;

public class SimpleTimer {
	private static long time = 0;
	
	public static void reset() {
		time = 0;
	}
	
	public static void start() {
		time = System.nanoTime();
	}
	
	public static long stop() {
		return System.nanoTime() - time;
	}
}
