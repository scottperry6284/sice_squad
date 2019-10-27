package edu.mit.compilers.semantics;
import edu.mit.compilers.semantics.IR.*;

// import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
// import java.util.List;
import java.util.Queue;
import java.util.prefs.NodeChangeEvent;
// import java.util.zip.Adler32;

// import javax.swing.plaf.SliderUI;

import edu.mit.compilers.Utils;
import java.util.List;

public class ScottSemantics {
    private boolean errors_reported = false;

    public static void RootCheck(IR.Node rootNode) {

        IR.Program rootNodeCast = (Program) rootNode;

        // Need to handle issue where there is method global conflict in global scope.

        MethodSymbolTable methodTable = new MethodSymbolTable();

        // Check 13: The argument of the len operator must be an array variable,
        // and it must return an int
        // TODO: Pre-populate the method table with the len function.
        // IR.MethodDecl lenFunction = new IR.MethodDecl();
        // lenFunction.ID = "len";
        // lenFunction.

        SymbolTable symbolTable = new SymbolTable();
        rootNode.methodTable = methodTable;
        rootNode.symbolTable = symbolTable;
        NodeCheck(rootNodeCast, symbolTable, methodTable, null);        
    }

    public static void NodeCheck(
            IR.Node node,
            SymbolTable currentScope,
            MethodSymbolTable methodTable,
            MethodDecl currentMethod
    ){

        node.symbolTable = currentScope;
        node.methodTable = methodTable;
    
        if (node instanceof Program) {
            for(IR.Node childNode : node.getChildren()) {
                NodeCheck(childNode, currentScope, methodTable, currentMethod);
            }

        // Check that Main Method Exists since it will be added to the method table.
        MainDeclared(node);

        // TODO: Check that there are no Double Declarations.

        } else if(node instanceof MethodDecl){
            
            IR.MethodDecl nodeCast = (IR.MethodDecl) node;

            // Check if method is in symbol table and if not try to add it to method table.
            if(
                currentScope.SymbolTableEntries.containsKey(nodeCast.ID)
                || !methodTable.addMethod(nodeCast)
            ){
                System.out.println("Method Name Already Declared");
            }

            // Create new scope at for the method.
            SymbolTable methodScope = new SymbolTable();
            methodScope.level = currentScope.level + 1;
            methodScope.parent = currentScope;

            // Add arguements to symbol table and check for double declaration.
            // TODO: Do I also need to check the method table?
            for(MethodDeclParam param : nodeCast.params) {
                if(!methodScope.add(param.ID, param)) {
                    System.out.println("Arguement Already Declared");
                }
            }

            // Perform NodeCheck on the method's block
            NodeCheck(nodeCast.block, methodScope, methodTable, nodeCast); 

        } else if(node instanceof ImportDecl) {

            ImportDecl nodeCast = (ImportDecl) node; 

            // TODO: Do I need to check if import name in symbol table?
            if(
                nodeCast.methodTable.MethodTableEntries.containsKey(nodeCast.name) 
                || !methodTable.addImport(nodeCast)
            ) {
                System.out.println("Import Name Already Declared");
            }

        } else if(node instanceof FieldDeclNoArray) {
            
            IR.FieldDeclNoArray nodeCast = (IR.FieldDeclNoArray) node;

            // Check that field has not already been declared.
            // TODO: Do I need to check if name in method table?
            if(
                !currentScope.add(nodeCast.ID, nodeCast)
                || (currentScope.level == 0 && (methodTable.contains(nodeCast.ID)))
            ) {
                System.out.println("Field Name Already used in Declaration");
            }

        } else if(node instanceof FieldDeclArray) {
            
            IR.FieldDeclArray nodeCast = (IR.FieldDeclArray) node;
            
            // Check that field has not already been declared.
            // TODO: Do I need to check if name in method table?
            if(!currentScope.add(nodeCast.ID, nodeCast)) {
                System.out.println("Field Already Declared");
            }

            // Check 4: Length in array declaration must be greater than zero.
            if(nodeCast.length <= 0) {
                System.out.println("Length in array declaration must be greater than zero.");
            }

        } else if(node instanceof Block) {

            IR.Block nodeCast = (IR.Block) node;
            // Check that fields haven't been declared in params???

            // Perform NodeCheck on all the fields.
            for (FieldDecl field : nodeCast.fields){
                NodeCheck(field, currentScope, methodTable, currentMethod);
            }

            // Perform NodeCheck on all the statements.
            for (Statement statement : nodeCast.statements){
                NodeCheck(statement, currentScope, methodTable, currentMethod);
            }

        } else if(node instanceof AssignmentStatement) {
            AssignmentStatement nodeCast = (AssignmentStatement) node;
        
            NodeCheck(nodeCast.loc, currentScope, methodTable, currentMethod);

            NodeCheck(nodeCast.assignExpr, currentScope, methodTable, currentMethod);

        } else if(node instanceof IfStatement) {
            
            IfStatement nodeCast = (IfStatement) node; 

            // Perform NodeCheck on condition in order to populate its symbol tables.
            NodeCheck(nodeCast.condition, currentScope, methodTable, currentMethod);

            // Check 14: Check that the type of Expr is Bool
            if (nodeCast.condition.getType() != "bool"){
                System.out.println("Conditional type must be bool");
            }

            // New scope at for the if statement block.
            SymbolTable ifStatementScope = new SymbolTable();
            ifStatementScope.level = currentScope.level + 1;
            ifStatementScope.parent = currentScope;

            // Perform Node Check on if Block.
            NodeCheck(nodeCast.block, ifStatementScope, methodTable, currentMethod); 

            // New scope at for the else statement block.
            SymbolTable elseStatementScope = new SymbolTable();
            elseStatementScope.level = currentScope.level + 1;
            elseStatementScope.parent = currentScope;

            // Perform Node Check on else Block.
            if (nodeCast.elseBlock != null) {
                NodeCheck(nodeCast.elseBlock, elseStatementScope, methodTable, currentMethod);  
            }

        } else if(node instanceof ForStatement) {

            ForStatement nodeCast = (ForStatement) node; 

            // Need to do before checking condition so that I do symbol table populated for loop var.
            NodeCheck(nodeCast.initExpr, currentScope, methodTable, currentMethod);
           
            // Perform NodeCheck on condition in order to populate its symbol tables.
            NodeCheck(nodeCast.condition, currentScope, methodTable, currentMethod);

            // Check 14: Check that the type of Expr is Bool
            if (nodeCast.condition.getType() != "bool"){
                System.out.println("Conditional type must be bool");
            }

            // New scope at for the for for statement block.
            SymbolTable forScope = new SymbolTable();
            forScope.level = currentScope.level + 1;
            forScope.parent = currentScope;

            // Perform Node Check on for Block.
            NodeCheck(nodeCast.block, forScope, methodTable, currentMethod); 

        } else if(node instanceof WhileStatement) {

            WhileStatement nodeCast = (WhileStatement) node; 
           
            // Perform NodeCheck on condition in order to populate its symbol tables.
            NodeCheck(nodeCast.condition, currentScope, methodTable, currentMethod);

            // Check 14: Check that the type of Expr is Bool
            if (nodeCast.condition.getType() != "bool"){
                System.out.println("Conditional type must be bool");
            }

            // New scope at for the for for statement block.
            SymbolTable whileScope = new SymbolTable();
            whileScope.level = currentScope.level + 1;
            whileScope.parent = currentScope;

            // Perform Node Check on for Block.
            NodeCheck(nodeCast.block, whileScope, methodTable, currentMethod); 

        } else if(node instanceof ReturnStatement) {
            ReturnStatement nodeCast = (ReturnStatement) node;
            
            if (nodeCast.expr != null) {
                NodeCheck(nodeCast.expr, currentScope, methodTable, currentMethod);
            }

        } else if(node instanceof BreakStatement) {
            

        } else if(node instanceof ContinueStatement) {
            

        } else if(node instanceof MethodCall) {

            MethodCall nodeCast = (MethodCall) node;

            // Semantic Check 11: The identifier in a method statement must be a
            // declared method or import.
            if(
                currentScope.find(nodeCast.ID) != null || !methodTable.contains(nodeCast.ID)
            ) {
                System.out.println("Method not Declared");
            }

            // Semantic Check 5: The number and types of arguments in a method call (non-imports)
            // must be the same as the number and types of the formals,
            // i.e., the signatures must be identical.
            if (methodTable.MethodTableEntries.containsKey(nodeCast.ID)) {

                // Check that number of params equal between method call and method signiture.
                if (nodeCast.params.size() != methodTable.getMethod(nodeCast.ID).params.size()) {
                    System.out.println(
                        "Method call has different number of arguements than method signiture."
                    );
                }

                // Walk through the arguements in the method call and check that they match
                // the types of the method parameters.

                for (int i = 0; i < nodeCast.params.size(); i++) {
        
                    // Semantic Check 7.
                    if (nodeCast.params.get(i).val instanceof String) {
                        System.out.println(
                            "String literals may not be used as arguments to non-import methods."
                        );
                    }
 
                    Expr paramCast = (Expr) nodeCast.params.get(i).val;

                    NodeCheck(paramCast, currentScope, methodTable, currentMethod);

                    if (
                        paramCast.members.size() >= 1
                        && paramCast.members.get(0) instanceof IR.LocationNoArray
                    ) {

                        IR.LocationNoArray paramLocationNoArray = (
                            (IR.LocationNoArray) paramCast.members.get(0)
                        );

                        if (currentScope.find(paramLocationNoArray.ID) instanceof IR.FieldDeclArray) {
                            System.out.println(
                                "Array variables may not be used as arguments to non-import methods."
                            );
                        }
                    }
                    
                    // Semantic Check 5.
                    // TODO: Use enum properly.

                    if (
                        !paramCast.getType().equals(
                            methodTable.getMethod(nodeCast.ID).params.get(i).type.getName()
                        )
                    ) {
                        System.out.println(
                            "Type of parameters in method call does not match type of parameters "
                            + "in method signiture"
                        );
                    }

                }

            }
        } else if(node instanceof Expr) {

            Expr nodeCast = (Expr) node;
            
            // Perform NodeCheck on expr's to populate symbol tables.
            for (IR.Node member : nodeCast.members) {
                NodeCheck(member, currentScope, methodTable, currentMethod);
            } 

        } else if(node instanceof LocationNoArray) {

            LocationNoArray nodeCast = (LocationNoArray) node;
           
            // Check 2: No identifier is used before it is declared.
            if (currentScope.find(nodeCast.ID) == null){
                System.out.println("Variable not declared");
            }

        } else if(node instanceof LocationArray) {

            LocationArray nodeCast = (LocationArray) node;
            
            // Check 2: No identifier is used before it is declared.
            if (currentScope.find(nodeCast.ID) == null){
                System.out.println("Variable not declared");
            }
            
            NodeCheck(nodeCast.index, currentScope, methodTable, currentMethod);
            // Could put check 12 here.
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