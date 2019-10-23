package edu.mit.compilers.semantics;
import edu.mit.compilers.irclasses.*;

public class MethodSymbolTable {

    public int level;
    public HashMap<String, IR> SymbolTableEntries = new HashMap<String, IR>();

    // Can handle both methods and imports because if the global scope.
    public boolean add(IR method){

        if (methodTable.containsKey(method.ID)) {
            return false;
        }
        else {
            methodTable.put(method.ID);
            return true;
        }
    }
}
