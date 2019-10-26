
package edu.mit.compilers.semantics;

import edu.mit.compilers.semantics.IR.*; 
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import edu.mit.compilers.Utils;
import java.util.List;

public class MethodSymbolTable {

    public int level;
    public HashMap<String, MethodDecl > MethodTableEntries = new HashMap<String, MethodDecl >();

    // Can handle both methods and imports because if the global scope.
    public boolean add(MethodDecl method){

        if (MethodTableEntries.containsKey(method.ID)) {
            return false;
        }
        else {
            MethodTableEntries.put(method.ID, method);
            return true;
        }
    }
}

