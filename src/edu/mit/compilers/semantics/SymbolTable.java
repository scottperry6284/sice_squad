package edu.mit.compilers.semantics;

public class SymbolTable {
    /*HashMap<String, Boolean> node;
    SymbolElement parent; 

	public SymbolElement(SymbolElement par) {
        parent = par; 

		node = new HashMap<String, Boolean>();
	}
    
    public void add (String var){
        node.put(var, true); 
    }

    public int find (String var){
        if (node.get(var)) return 0; 
        if (parent == null){
            return -1; 
        }
        int lev = parent.find(var); 
        if (lev == -1) return -1; 
        return 1 + lev; 
    } */
}