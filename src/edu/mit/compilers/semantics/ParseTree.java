package edu.mit.compilers.semantics;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import edu.mit.compilers.grammar.DecafParser;

public class ParseTree {
	private DecafParser parser;
	public Node root;
	public ParseTree(DecafParser parser) {
		this.parser = parser;
	}
	private void printASTInner(AST ast, int d) {
		while(ast != null) {
			for(int i=0; i<d; i++)
				System.out.print("  ");
			System.out.println(ast.getText());
			printASTInner(ast.getFirstChild(), d+1);
			ast = ast.getNextSibling();
		}
	}
	public void printAST()
	{
		AST ast = parser.getAST();
		printASTInner(ast, 0);
	}
	private class Node {
		public final List<Node> children;
		public final int line, col;
		
		public Node(AST ast)
		{
			line = ast.getLine();
			col = ast.getColumn();
			children = new ArrayList<>();
			while(ast != null)
			{
				ast.getFirstChild();
				children.add(new Node(ast));
				ast = ast.getNextSibling();
			}
		}
	}
	public void build() {
		//root = new Node(parser.getAST());
		AST ast = parser.getAST();
		/*while(ast != null)
		{
			System.out.println(ast.getText() + " " + ast.getNumberOfChildren());
			ast = ast.getNextSibling();
		}*/
	}
}
