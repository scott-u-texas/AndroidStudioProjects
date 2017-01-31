package scottm.examples.guessfour;


public class Feedback {
    private int numBlack;
    private int numWhite;
    
    /**
     * Create a result.
     * @param black The number of black pegs. 
     * Black pegs represent elements of a guess that are the
     * correct color in the correct position.
     * <tt>black</tt> >= 0
     * @param white The number of white pegs. 
     * White pegs represent elements of a guess that are the
     * correct color, but in the wrong position.
     * <tt>white</tt> >= 0
     */
    public Feedback(int black, int white){
        if(black < 0)
            throw new IllegalArgumentException("parameter black must be greater than 0. black: " + black);
        if(white < 0)
            throw new IllegalArgumentException("parameter white must be greater than 0. black: " + white);
        numBlack = black;
        numWhite = white;
    }
    
    public int numBlack(){
        return numBlack;
    }
    
    public int numWhite(){
        return numWhite;
    }
    
    public int totalPegs(){
        return numBlack() + numWhite();
    }
    
    public String toString(){
        String result = "";
        for(int i = 0; i < numBlack; i++)
        	result += "b";
        for(int i = 0; i < numWhite; i++)
        	result += "w";
        return result;
    }   
    
    public String toStringAlt() {
        String result = "Result: ";
        result += numBlack + " black peg";
        result += (numBlack != 1) ? "s" : "";
        result += ", " + numWhite + " white peg";
        result += (numWhite != 1) ? "s" : "";
        return result;   
    }
}

