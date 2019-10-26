
package edu.mit.compilers.semantics;
import edu.mit.compilers.semantics.IR.*; 
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import edu.mit.compilers.Utils;
import java.util.List;


public class SymbolTable {

	public int level;
	public SymbolTable parent = null;
	public HashMap<String, IR> SymbolTableEntries = new HashMap<String, IR>();
	public ArrayList<SymbolTable> childNodes = new ArrayList<SymbolTable>() ;

	public Boolean add (String ID, IR node){
		if (SymbolTableEntries.containsKey(ID)){
			return false;
		} else {
			SymbolTableEntries.put(ID, node);
			return true;
		}
	}

	public IR find (String ID) {
		if(SymbolTableEntries.containsKey(ID)) {
			return SymbolTableEntries.get(ID);
		}
		if(parent != null) {
			return parent.find(ID);
		}
		return null;
	}

	public String prettyPrint() {
		return "\t".repeat(level) + Integer.toString(SymbolTableEntries.size());
	}
}