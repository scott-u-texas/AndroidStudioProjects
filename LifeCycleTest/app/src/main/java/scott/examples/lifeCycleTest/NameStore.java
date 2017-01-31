package scott.examples.lifeCycleTest;

public class NameStore {

	private String theName;
	
	private static NameStore theInstance;
	
	private NameStore(){};
	
	public static NameStore getInstance() {
		if(NameStore.theInstance == null)
			theInstance = new NameStore();
		return theInstance;
	}
	
	public String getName() {
		return theName;
	}
	
	public String setName(String newName) {
		String oldName = theName;
		theName = newName;
		return oldName;
	}
}
