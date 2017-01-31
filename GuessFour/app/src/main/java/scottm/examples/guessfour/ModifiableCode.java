package scottm.examples.guessfour;

public class ModifiableCode {
	
	private Peg[] myPegs;
	private int size;
	
	public ModifiableCode(int cap) {
		myPegs = new Peg[cap];
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean isComplete() {
		return size == myPegs.length;
	}
	
	public int size() {
		return size;
	}
	
	public int maxSize() {
		return myPegs.length;
	}
	
	public void addPeg(Peg p) {
		myPegs[size] = p;
		size++;
	}
	
	public Peg removePeg() {
		size--;
		Peg result = myPegs[size];
		myPegs[size] = null;
		return result;
	}
	
	public Code toCode() {
		return new Code(myPegs);
	}
	
	public Peg getPeg(int pos) {
		return myPegs[pos];
	}
	
	public void clear() {
		size = 0;
		for(int i = 0; i < myPegs.length; i++)
			myPegs[i] = null;
	}
	
    public String toString(){
        String result = "";
        for(int i = 0; i < size; i++){
            result += myPegs[i].toString().charAt(0);
        }
        return result;
    }
}
