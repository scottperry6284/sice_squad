package edu.mit.compilers.semantics;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParserTokenTypes;

public class ParseTree {
	private DecafParser parser;
	public Node root;
	public ParseTree(DecafParser parser) {
		this.parser = parser;
	}
	private void printAST(AST ast, int d) {
		while(ast != null) {
			for(int i=0; i<d; i++)
				System.out.print("  ");
			System.out.println(ast.getText());
			printAST(ast.getFirstChild(), d+1);
			ast = ast.getNextSibling();
		}
	}
	public void printAST()
	{
		AST ast = parser.getAST();
		printAST(ast, 0);
	}
	public static class Node {
		public class Type implements DecafParserTokenTypes {};
		public List<Node> children;
		public final int line, col;
		public final int type;
		public String text;
		
		public Node(AST ast) {
			line = ast.getLine();
			col = ast.getColumn();
			type = ast.getType();
			text = ast.getText();
			children = new ArrayList<>();
			ast = ast.getFirstChild();
			while(ast != null) {
				children.add(new Node(ast));
				ast = ast.getNextSibling();
			}
		}
		private void print(int d) {
			for(int i=0; i<d; i++)
				System.out.print("  ");
			System.out.println(text);
			for(Node child: children)
				child.print(d+1);
		}
		public void print() {
			print(0);
		}
		public Node child(int idx) {
			return children.get(idx);
		}
	}
	public void build() {
		root = new Node(parser.getAST());
		root.print();
	}
}
