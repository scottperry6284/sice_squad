
package edu.mit.compilers.semantics;
import edu.mit.compilers.semantics.IR.*; 
import java.util.List;

public class Semantics{
    public static Boolean check4 (IR.Node node){
        boolean ans = true; 
        if (node instanceof FieldDeclArray){
            if (((FieldDeclArray)node).length <= 0) ans = false; 
        }
        List <IR.Node> children = node.getChildren(); 
        for (int child=0; child<children.size(); child++){
            ans &= check4 (children.get(child)); 
        }
        return ans; 
    }

}
