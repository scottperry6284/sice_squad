
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
}
