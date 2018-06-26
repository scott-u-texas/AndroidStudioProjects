package tutorials.cs371m.androidtictactoe;

/**
 * Created by scottm on 6/7/2016.
 */
public class WinData {

    public enum Outcome {HUMAN, TIE, ANDROID}

    private int[] counters;

    public WinData() {
        counters = new int[Outcome.values().length];
    }

    public void incrementWin(Outcome outcome) {
        counters[outcome.ordinal()]++;
    }

    public int getCount(Outcome outcome) {
        return counters[outcome.ordinal()];
    }
}
