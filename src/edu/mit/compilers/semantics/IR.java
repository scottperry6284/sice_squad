package edu.mit.compilers.semantics;
import edu.mit.compilers.grammar.*;

public class IR {
	private ParseTree parseTree;
	private IR.Node root;
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	public static class Node {
		public enum Type {
			program, import_decl, field_decl, method_decl, method_param, block, type,
			
			invalid;
		}
		public final IR.Node.Type type;
		public final String text;
		private Node(ParseTree.Node node)
		{
			int nt = node.type;
			if(nt == ParseTree.Node.Type.AST_program)
			{
				type = Type.program;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_import_decl)
			{
				type = Type.import_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_field_decl)
			{
				type = Type.field_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_decl)
			{
				type = Type.method_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_param)
			{
				type = Type.method_param;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_block)
			{
				type = Type.block;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_type)
			{
				type = Type.type;
				text = "";
			}
			else
			{
				type = Type.invalid;
				text = "";
			}
		}
	}
	
	public void build() {
		root = new Node(parseTree.root);
	}
	
}