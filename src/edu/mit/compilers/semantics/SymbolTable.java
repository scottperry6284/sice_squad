public class SymbolElement {
    HashMap<String, Boolean> node;
    SymbolElement parent; 

	public SymbolElement(SymbolElement par) {
        parent = par; 

		node = new HashMap<String, Boolean>();
	}
    
    public void add (String var){
        node.put(var, true); 
    }

    public boolean find (String var){
        if (node.get(var)) return true; 
        if (parent == null){
            return false; 
        }
        return parent.find(var); 
    } 
}