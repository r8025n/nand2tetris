import java.util.HashMap;

public class SymbolTable {

	private int fieldIndex = 0;
	private int staticIndex = 0;
	private int argumentIndex = 0;
	private int localIndex = 0;
	private String className = "";
	//private String subroutineName = "";

	HashMap<String, Identifier> classLevelMap, subroutineLevelMap;

	SymbolTable(String className) {
		this.className = className
		classLevelMap = new HashMap<>();
		subroutineLevelMap = new HashMap<>();
	}

	void startSubroutine() {
		subroutineLevelMap.clear();
		defineIdentifier("sub", "this", className, "argument");

	}

	void defineIdentifier(String level, String name, String type, String kind) {
		Identifier newIdentifier = new Identifier(type, kind, selectIndex(type));

		if (level.equals("class"))
			classLevelMap.put(name, newIdentifier);
		else
			subroutineLevelMap.put(name, newIdentifier);
	}

	int varCount() {
		return (fieldIndex + staticIndex + argumentIndex + localIndex );
	}

	String getType(String identifier) {
		return searchIdentifier(identifier).type;
	}

	String getKind(String identifier) {
		return searchIdentifier(identifier).kind;
	}

	String  getIndex(String identifier) {
		return searchIdentifier(identifier).index;
	}

	Identifier searchIdentifier(String identifier) {
		if (subroutineLevelMap.containsKey(identifier)) 
			return subroutineLevelMap.get(indetifier);
		else if(classLevelMap.containsKey(identifier))
			return subroutineLevelMap.get(identifier);
	}

	int selectIndex(String type) {
		int returnValue;

		if(type.equals("field")) {
			returnValue = fieldIndex;
			fieldIndex++;
		}
		else if(type.equals("static")) {			
			returnValue = staticIndex;
			staticIndex++;
		}
		else if(type.equals("argument")) {
			returnValue = argumentIndex;
			argumentIndex++;
		}
		else if(type.equals("local")) {
			returnValue = localIndex;
			localIndex++;
		}

		return returnValue;
	}
}