
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

    // TODO: Change names so first charater lower case.
    public HashMap<String, MethodDecl> MethodTableEntries = new HashMap<String, MethodDecl>();
    public HashMap<String, ImportDecl> ImportTableEntries = new HashMap<String, ImportDecl>();

    public boolean addMethod(MethodDecl method){

        if (MethodTableEntries.containsKey(method.ID) || ImportTableEntries.containsKey(method.ID)) {
            return false;
        }
        else {
            MethodTableEntries.put(method.ID, method);
            return true;
        }
    }

    public MethodDecl getMethod(String ID) {
        return MethodTableEntries.get(ID);
    }

    public boolean addImport(ImportDecl importStatement){

        if (
            ImportTableEntries.containsKey(importStatement.name)
            || MethodTableEntries.containsKey(importStatement.name)
        ) {
            return false;
        }
        else {
            ImportTableEntries.put(importStatement.name, importStatement);
            return true;
        }
    }

    public ImportDecl getImport(String ID) {
        return ImportTableEntries.get(ID);
    }

    public boolean contains(String ID){
        if (MethodTableEntries.containsKey(ID) || ImportTableEntries.containsKey(ID)) {
            return true;
        }
        return false;
    }
}

