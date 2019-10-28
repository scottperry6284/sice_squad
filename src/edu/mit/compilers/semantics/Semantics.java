
package edu.mit.compilers.semantics;
import edu.mit.compilers.semantics.IR.*; 
import java.util.List;

public class Semantics{
    public static void check4 (IR.Node node){
        if (node instanceof FieldDeclArray){
            if (((FieldDeclArray)node).length <= 0) {
                throw new IllegalStateException ("Bad array size."); 
            }
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check4 (children.get(child)); 
        }
    }
    public static void check20 (IR.Node node){
        if (node == null) return; 
        if ((node instanceof ContinueStatement) || (node instanceof BreakStatement)){
            IR.Node par = node.parent; 
            boolean good = false; 
            while (par != null){
                if ((par instanceof WhileStatement) || (par instanceof ForStatement)){
                    good = true; 
                    break; 
                }
                par = par.parent; 
            }
            if (!good){
                throw new IllegalStateException ("Bad break or continue."); 
            }
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check20 (children.get(child)); 
        }
    }
    public static void check8 (IR.Node node){
        if (node == null) return; 
        if (node instanceof ReturnStatement){
            if (!(((ReturnStatement)node).expr == null || ((ReturnStatement)node).expr.members == null)){
                if (((ReturnStatement)node).expr.members.size() > 0){
                    IR.Node par = node.parent; 
                    boolean good = false; 
                    while (par != null){
                        if ((par instanceof MethodDecl) && !(((MethodDecl)par).type.getName().equals("void"))){
                            good = true; 
                            break; 
                        }
                        par = par.parent; 
                    }
                    if (!good){
                        throw new IllegalStateException ("Bad use of return."); 
                    }                 
                }   
            }
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check8 (children.get(child)); 
        }
    }
    public static boolean check3 (IR.Node node){
        if (node == null) return false; 
        boolean ans = false; 
        if (node instanceof MethodDecl){
            if ((((MethodDecl)node).type.getName().equals("void")) && ((((MethodDecl)node).ID).equals("main"))){
                if (((MethodDecl)node).params.size() == 0){
                    ans = true; 
                }
            }
        }
        
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return ans; 

        for (int child=0; child<children.size(); child++){
            ans |= check3 (children.get(child)); 
        }
        return ans; 
    }
    public static void check6 (IR.Node node){
        /* also handles check 16 and 17 */
        if (node == null) return; 
        if (node instanceof Expr){
            String ret = ((Expr)node).getType(); 
        }
        
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check6 (children.get(child)); 
        }
    }   
    public static void check9 (IR.Node node){
        if (node == null) return; 
        if (node instanceof ReturnStatement){
            String type = ""; 
            if (!(((ReturnStatement)node).expr == null)){
                type = ((ReturnStatement)node).expr.getType(); 
            }
            else{
                type = "void"; 
            }
            IR.Node par = node.parent; 
            boolean good = false; 
            while (par != null){
                if (par instanceof MethodDecl){
                    if (((MethodDecl)par).type.getName().equals(type)) good = true; 
                    break; 
                }
                par = par.parent; 
            }
            if (!good){
                throw new IllegalStateException ("Bad use of return."); 
            }       
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check9 (children.get(child)); 
        }
    }
    public static void check12 (IR.Node node){
        if (node == null) return; 
        if (node instanceof LocationArray){
            Node arr = node.symbolTable.find(((LocationArray)node).ID); 

            if (!(arr instanceof FieldDeclArray)){
                throw new IllegalStateException ("Bad location array."); 
            }
            System.out.println("HEHE: " + ((LocationArray)node).index.getType());
            if (((LocationArray)node).index.getType() != "int"){
                throw new IllegalStateException ("Bad location array."); 
            }
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check12 (children.get(child)); 
        }
    }
    public static void check13 (IR.Node node){
        if (node == null) return; 
        if (node instanceof Len){
            Node arr = node.symbolTable.find(((Len)node).ID); 
            if (!(arr instanceof FieldDeclArray)){
                throw new IllegalStateException ("Bad argument to len."); 
            }
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check13 (children.get(child)); 
        }
    }
    public static void check18 (IR.Node node){
        /* also handles check 19 */
        if (node == null) return; 
        if (node instanceof AssignmentStatement){
            System.out.println("HAXD");
            System.out.println(((AssignmentStatement)node).assignExpr != null);
            if ((((AssignmentStatement)node).assignExpr) != null){
                System.out.println("HEHE");
                System.out.println(((AssignmentStatement)node).loc.getType());
                System.out.println(((AssignmentStatement)node).assignExpr.getType());
                if (!(((AssignmentStatement)node).loc.getType().equals(((AssignmentStatement)node).assignExpr.getType()))){
                    throw new IllegalStateException ("Bad assignment."); 
                }
            }
            if ((((AssignmentStatement)node).op.type == Op.Type.minusequals) || (((AssignmentStatement)node).op.type == Op.Type.plusequals)){
                if (!(((AssignmentStatement)node).loc.getType().equals("int"))){
                    throw new IllegalStateException ("Bad assignment."); 
                }
                if (!(((AssignmentStatement)node).assignExpr.getType().equals("int"))){
                    throw new IllegalStateException ("Bad assignment."); 
                }
            }
            if ((((AssignmentStatement)node).op.type == Op.Type.increment) || (((AssignmentStatement)node).op.type == Op.Type.decrement)){
                if (!(((AssignmentStatement)node).loc.getType().equals("int"))){
                    throw new IllegalStateException ("Bad assignment."); 
                }
            }
        }
        if (node instanceof ForStatement){
            if (((ForStatement)node).initLoc == null || ((ForStatement)node).initExpr == null){
                throw new IllegalStateException ("Bad assignment."); 
            }
            if (!(((ForStatement)node).initLoc.getType().equals(((ForStatement)node).initExpr.getType()))){
                throw new IllegalStateException ("Bad assignment."); 
            }    
        }
        List <IR.Node> children = node.getChildren(); 
        if (children == null) return; 

        for (int child=0; child<children.size(); child++){
            check18 (children.get(child)); 
        }
    }
}
