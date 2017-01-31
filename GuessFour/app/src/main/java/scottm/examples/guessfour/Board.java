package scottm.examples.guessfour;

import java.util.Scanner;

public class Board {

	private int guessesSoFar;
	private Code goal;
	private Code[] guesses;
	private Feedback[] results;
	private boolean solved;
	private ModifiableCode inProgressGuess;
	private String blankGuess;

	public Board(int numGuesses, String oldCodes) {
		Scanner sc = new Scanner(oldCodes);
		int numLines = sc.nextInt();
		int partialLine = sc.nextInt();
		sc.nextLine();
		String secretCode = sc.nextLine();
		initData(numGuesses, new Code(secretCode));
		int limit = numLines - partialLine - 1; // subtract 1 for secret code
		for(int i = 0; i < limit; i++) {
			addPegsHelper(sc);
			addGuess();
		}

		if(partialLine == 1) {
			addPegsHelper(sc);
		}
	}

	private void addPegsHelper(Scanner sc){
		String guess = sc.nextLine();
		for(int j = 0; j < guess.length(); j++)
			addPeg(Peg.charToPeg(guess.charAt(j)));
	}

	public Board(int numGuesses, Code secretCode){
		initData(numGuesses, secretCode);
	}

	private void initData(int numGuesses, Code secretCode) {
		goal = secretCode;
		guessesSoFar = 0;
		guesses = new Code[numGuesses];
		results = new Feedback[numGuesses];
		solved = false;
		String temp = "";
		for(int i = 0 ; i < codeSize(); i++)
			temp += ".";
		blankGuess = temp;
		inProgressGuess = new ModifiableCode(codeSize());
	}

	public void addPeg(Peg p) {
		inProgressGuess.addPeg(p);
	}

	public void removeLastPeg() {
		inProgressGuess.removePeg();
	}

	public boolean currentGuessFull() {
		return inProgressGuess.size() == inProgressGuess.maxSize();
	}

	public boolean currentGuessEmpty() {
		return inProgressGuess.size() == 0;
	}

	// pre: currentGuessFull
	public void addGuess(){
		if(!currentGuessFull())
			throw new IllegalStateException("cannot add guess to board until current guess is complete.");
		guesses[guessesSoFar] = inProgressGuess.toCode();
		inProgressGuess.clear();
		results[guessesSoFar] = goal.getResult(guesses[guessesSoFar]);
		solved = results[guessesSoFar].numBlack() == codeSize();
		guessesSoFar++;
	}

	public int codeSize(){
		return goal.getLength();
	}

	public boolean solved(){
		return solved;
	}

	public int guessesSoFar() {
		return guessesSoFar;
	}

	public int maxAllowedGuesses() {
		return guesses.length;
	}

	// pre: 0 <= guessNum < maxGuesses, 0 <= pegNum < codeSize()
	public Peg getPeg(int guessNum, int pegNum) {	
		Peg result;
		if(guessNum < guessesSoFar)
			return guesses[guessNum].getPeg(pegNum);
		else if(guessNum == guessesSoFar)
			result = inProgressGuess.getPeg(pegNum);
		else
			return null;
		return result;
	}

	public Peg getSecretPeg(int num) {
		return goal.getPeg(num);
	}

	public String getFeedback(int guessNum) {
		return results[guessNum].toString();
	}

	public String toString(){
		String result = "";
		// create a String with the guesses and result so far
		for(int i = 0; i < guessesSoFar; i++)
			result += guesses[i] + " " + results[i] + "\n";

		//if solved show the goal 
		if( solved )
			result = goal + "\n" + result;
		// if not solved show the guesses remaining
		else{
			result = blankGuess + " Secret Code" + "\n" + result;
			int guessesLeft = guesses.length - guessesSoFar;
			for(int i = 0; i < guessesLeft; i++)
				result += blankGuess + "\n";
		}
		return result;
	}

	public String toStringCodes(){
		StringBuilder sb = new StringBuilder();
		int partialGuess = (currentGuessEmpty() ? 0 : 1);
		int numLines = 1 + guessesSoFar + partialGuess; // first 1 for goal / secret code
		sb.append(numLines + " " + (currentGuessEmpty() ? 0 : 1) + "\n"); 
		sb.append(goal + "\n");
		for(int i = 0; i < guessesSoFar; i++)
			sb.append(guesses[i] + "\n");
		if(partialGuess == 1)
			sb.append(inProgressGuess + "\n");
		return sb.toString();
	}
}
