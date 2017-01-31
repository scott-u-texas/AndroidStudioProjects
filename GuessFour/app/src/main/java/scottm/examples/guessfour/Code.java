package scottm.examples.guessfour;

import java.util.Random;

public class Code {
    private Peg[] myPegs;
    private static Random randNumGen = new Random();
    
    /**
     * Generate a random code.
     * @param numPegs > 0, numColors > 0
     */
    public Code(int numPegs, int numColors){
        if( !(numPegs > 0))
            throw new IllegalArgumentException("number of pegs must be greater than 0. size: " + numPegs);
        if( !(numColors > 0))
            throw new IllegalArgumentException("number of colors must be greater than 0. size: " + numColors);
        myPegs = new Peg[numPegs];
        numColors = Math.min(numColors, Peg.values().length);
        for(int i = 0; i < myPegs.length; i++){
            myPegs[i] = Peg.values()[ randNumGen.nextInt(numColors) ];
        }
    }
    
    /**
     * Create a new code based on the parameter.
     * @param pegs != null, pegs.length > 0
     */
    public Code(Peg[] pegs){
        if( pegs == null )
            throw new IllegalArgumentException("parameter pegs cannot be null.");
        if( !(pegs.length > 0) )
            throw new IllegalArgumentException("parameter pegs must have a length greater than 0.");
        myPegs = new Peg[pegs.length];
        System.arraycopy(pegs, 0, myPegs, 0, pegs.length);
    }
    
    public Code(String firstChars){
        myPegs = new Peg[firstChars.length()];
        for(int i = 0; i < getLength(); i++)
            myPegs[i] = Peg.charToPeg(firstChars.charAt(i));
    }
    
    /**
     * Get the result of this code compared to otherCode.
     * @param otherCode <tt>otherCode != null, otherCode.getLength() == this.getLength()</tt>
     * @return the result of this code compared to otherCode. Each peg in otherCode that 
     * is the same color and position as this code result in a black
     * peg in the result. Each peg that is the correct color, but in the wrong
     * position results in a white peg in the result
     */
    public Feedback getResult(Code otherCode){
        if( otherCode == null )
            throw new IllegalArgumentException("parameter otherCode cannot be null.");
        if( otherCode.getLength() != getLength() )
            throw new IllegalArgumentException("parameter otherCode must have the same length" +
                    "as this code. otherCode.getLength(): " + otherCode.getLength() + " != " +
                    getLength());
        int black = 0;
        int white = 0;
        
        // get working copies of each codes pegs, so they can be altered
        Peg[] thisPegs = getCopyOfPegs();
        Peg[] otherPegs = otherCode.getCopyOfPegs();
        
        //count number of black, scratch out matches
        for(int i = 0; i < getLength(); i++)
            if(thisPegs[i].equals(otherPegs[i])){
                black++;
                thisPegs[i] = null;
                otherPegs[i] = null;
            }
        
        // count number of white, scratch out matches
        // careful of null!
        for(int iThis= 0; iThis < getLength(); iThis++){
            for(int iOther = 0; iOther < getLength(); iOther++){
                if( thisPegs[iThis] != null && thisPegs[iThis].equals(otherPegs[iOther])){
                    white++;
                    thisPegs[iThis] = null;
                    otherPegs[iOther] = null;
                }
            }
        }
        
        return new Feedback(black, white);
    }
    
    private Peg[] getCopyOfPegs(){
        Peg[] copy =  new Peg[getLength()];
        System.arraycopy(myPegs, 0, copy, 0, getLength());
        return copy;
    }
    
    public int getLength(){
        return myPegs.length;
    }
    
    public String toString(){
        String result = "";
        for(Peg p : myPegs){
            result += p.toString().charAt(0);
        }
        return result;
    }
    
    // pre: 0 <= pos < getLength();
    public Peg getPeg(int pos) {
    	return myPegs[pos];
    }
    
    
}
