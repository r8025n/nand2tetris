import java.util.HashMap;

public class SymbolTable {

	private int fieldIndex = 0;
	private static int staticIndex = 0;
	private int argumentIndex = 0;
	private int localIndex = 0;
	private String className = "";

	HashMap<String, Identifier> classLevelMap, subroutineLevelMap;

	SymbolTable() {
		classLevelMap = new HashMap<>();
		subroutineLevelMap = new HashMap<>();
	}

	void setClassName(String str) {
		this.className = str;
	}

	String getClassName() {
		return this.className;
	}

	void startSubroutine(String subroutineType) {
		subroutineLevelMap.clear();
		argumentIndex = 0;
		localIndex = 0;

		if(subroutineType.equals("method"))
			defineIdentifier("sub", "this", className, "argument");

	}

	void defineIdentifier(String level, String name, String type, String kind) {
		Identifier newIdentifier = new Identifier(type, kind, selectIndex(kind));

		if (level.equals("class"))
			classLevelMap.put(name, newIdentifier);
		else
			subroutineLevelMap.put(name, newIdentifier);
	}

	int varCount(String kind) {
		if(kind.equals("field"))
			return fieldIndex;
		else if(kind.equals("static"))
			return staticIndex;
		else if(kind.equals("argument"))
			return argumentIndex;
		else
			return localIndex;
	}

	String getType(String identifier) {
		return searchIdentifier(identifier).type;
	}

	String getKind(String identifier) {
		return searchIdentifier(identifier).kind;
	}

	int getIndex(String identifier) {
		return searchIdentifier(identifier).index;
	}

	Identifier searchIdentifier(String identifier) {
		Identifier id = null;

		if (subroutineLevelMap.containsKey(identifier)){
			id = subroutineLevelMap.get(identifier);
		} 	
		else if(classLevelMap.containsKey(identifier)){
			id = classLevelMap.get(identifier);
		}

		return id;
	}

	int selectIndex(String kind) {
		int returnValue = -1;

		if(kind.equals("this")) {
			//System.out.println("value = "+fieldIndex);
			returnValue = fieldIndex;
			fieldIndex++;
		}
		else if(kind.equals("static")) {			
			returnValue = staticIndex;
			staticIndex++;
		}
		else if(kind.equals("argument")) {
			returnValue = argumentIndex;
			argumentIndex++;
		}
		else if(kind.equals("local")) {
			returnValue = localIndex;
			localIndex++;
		}

		return returnValue;
	}
}