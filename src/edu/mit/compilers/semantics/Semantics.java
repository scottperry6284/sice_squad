
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
                        if ((par instanceof MethodDecl) && ((MethodDecl)par).type.getName() != "void"){
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
}
