package scottm.examples.guessfour;

public enum Peg {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    ORANGE,
    PURPLE,
    PINK,
    TEAL;
    
    private static String firstChars;
    
    static{
        firstChars = "";
        for(Peg p : values())
            firstChars += p.toString().charAt(0);
    }
    
    public static String getFirstChars(){
        return firstChars;
    }

    public static char getCodeLetter(Peg p){
        return p.toString().charAt(0);
    }
    
    public static boolean isLegalCodeLetter(char c){
        c = Character.toUpperCase(c);
        return firstChars.contains(c + "");
    }
    
    
    public static Peg charToPeg(char c){
        c = Character.toUpperCase(c);
        for(Peg p : values()){
            if( c == p.toString().charAt(0))
                return p;
        }
        return null;
    }
}
