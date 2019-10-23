package edu.mit.compilers.semantics;
import edu.mit.compilers.irclasses.*;

public class SymbolTable {

	public int level;
	public SymbolTable parent = null;
	public HashMap<String, IR> SymbolTableEntries = new HashMap<String, IR>();
	public ArrayList<Symboltable> childNodes = new ArrayList<Symboltable>() ;

	public Boolean add (String ID, IR node){
		if (SymbolTableEntries.containsKey(Id)){
			return false;
		} else {
			SymbolTableEntries.put(ID, node);
			return true;
		}
	}

	public IR find (String ID) {
		if(hashSymbolTable.containsKey(id)) {
			return hashSymbolTable.get(id);
		}
		if(parent != null) {
			return parent.find(ID);
		}
		return null;
	}

	public String prettyPrint() {
		return "\t".repeat(level) + Integer.toString(hashSymbolTable.size());
	}
}