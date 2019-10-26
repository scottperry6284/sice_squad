package edu.mit.compilers.semantics;
import edu.mit.compilers.semantics.IR.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.zip.Adler32;

import javax.swing.plaf.SliderUI;

import edu.mit.compilers.Utils;
import java.util.List;

public class ScottSemantics {
    private boolean errors_reported = false;

    public static void RootCheck(IR.Node rootNode) {
        MethodSymbolTable methodTable = new MethodSymbolTable();
        SymbolTable symbolTable = new SymbolTable();
        rootNode.methodTable = methodTable;
        rootNode.symbolTable = symbolTable;
        NodeCheck(rootNode, symbolTable, methodTable, null);
    }

    public static void NodeCheck(
            IR.Node node,
            SymbolTable currentScope,
            MethodSymbolTable methodTable,
            MethodDecl currentMethod
    ){

        if (node instanceof Program) {
            for(IR.Node childNode : node.getChildren()) {
                NodeCheck(childNode, currentScope, methodTable, currentMethod);
            }

        // Check that Main Method Exists since it will be added to the method table.
        MainDeclared(node);

        // Check that there are no Double Declarations.

        } else if(node instanceof MethodDecl){
            
            IR.MethodDecl castNode = (IR.MethodDecl) node;

            // Check if method is in Symbol Table and if not try to add it to method table.
            if(currentScope.SymbolTableEntries.containsKey(castNode.ID) || !methodTable.addMethod(castNode)){
                System.out.println("Method Already Declared");
            }

            // New Scope at for the Method.
            SymbolTable methodScope = new SymbolTable();
            methodScope.parent = currentScope;
            castNode.symbolTable = methodScope;

            // Add arguemets to symbol table and check for double declaration.
            for(MethodDeclParam param : castNode.params) {
                if(!methodScope.add(param.ID, param)) {
                    System.out.println("Arguement Already Declared");
                }
            }
        } else if(node instanceof MethodDecl){
            
            IR.MethodDecl castNode = (IR.MethodDecl) node;

            // Check if method is in Symbol Table and if not try to add it to method table.
            if(currentScope.SymbolTableEntries.containsKey(castNode.ID) || !methodTable.addMethod(castNode)){
                System.out.println("Method Already Declared");
            }

            // New Scope at for the Method.
            SymbolTable methodScope = new SymbolTable();
            methodScope.parent = currentScope;
            castNode.symbolTable = methodScope;

            // Add arguemets to symbol table and check for double declaration.
            for(MethodDeclParam param : castNode.params) {
                if(!methodScope.add(param.ID, param)) {
                    System.out.println("Arguement Already Declared");
                }
            }

            // 

        } else if(node instanceof ImportDecl) {
            ImportDecl nodeCast = (ImportDecl) node; 
            if(!nodeCast.methodTable.MethodTableEntries.containsKey(nodeCast.name)){
                System.out.println("Import Already Declared");
            }

        } else if(node instanceof FieldDeclNoArray) {
            
            IR.FieldDeclNoArray castNode = (IR.FieldDeclNoArray) node;

            // Check that field has not already been declared.
            if(!currentScope.add(castNode.ID, castNode)) {
                System.out.println("Field Already Declared");
            }

        } else if(node instanceof FieldDeclArray) {
            
            IR.FieldDeclArray castNode = (IR.FieldDeclArray) node;
            
            // Check that field has not already been declared.
            if(!currentScope.add(castNode.ID, castNode)) {
                System.out.println("Field Already Declared");
            }

            // Check 4: Length in array declaration must be greater than zero.
            if(castNode.length <= 0) {
                System.out.println("Length in array declaration must be greater than zero.");
            }
        } else if(node instanceof Block) {
            
        }
    }

    // Rule 3
    private static void MainDeclared(IR.Node root){
        if(root.methodTable.MethodTableEntries.containsKey("main")){
            IR.Node mainMethod = root.methodTable.MethodTableEntries.get("main");
            if(!(mainMethod instanceof MethodDecl)) {
                System.out.println("main not method");
            }

            // Check Return Type.


            // Check that main method has no arguements.

        }
    }
}