package edu.mit.compilers.semantics;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.Utils;
import edu.mit.compilers.semantics.ParseTree.Node;

public class IR {
	private ParseTree parseTree;
	private IR.Node root;
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	public abstract static class Node {
		/*public enum Type {
			program, import_decl, field_decl, method_decl, method_param, block, type,
			statement_assignment, statement_method_call, statement_if, statement_for,
			statement_while, statement_return, statement_break, statement_continue,
			assign_expr, assign_op, compound_assign_op, method_params_none,
			method_params_local, method_params_import, expr, import_arg, bin_op,
			arith_op, rel_op, eq_op, cond_op, location_array, location_noarray,
			char_literal, bool_literal, int_literal, ID,
			invalid;
		}
		private Node(ParseTree.Node node) {
			int nt = node.type;
			if(nt == ParseTree.Node.Type.AST_program) {
				type = Type.program;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_import_decl) {
				type = Type.import_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_field_decl) {
				type = Type.field_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_decl) {
				type = Type.method_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_param) {
				type = Type.method_param;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_block) {
				type = Type.block;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_type) {
				type = Type.type;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_type) {
				type = Type.type;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_assignment) {
				type = Type.statement_assignment;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_method_call) {
				type = Type.statement_method_call;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_if) {
				type = Type.statement_if;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_for) {
				type = Type.statement_for;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_while) {
				type = Type.statement_while;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_return) {
				type = Type.statement_return;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_break) {
				type = Type.statement_break;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_continue) {
				type = Type.statement_continue;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_assign_expr) {
				type = Type.assign_expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_assign_op) {
				type = Type.assign_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_compound_assign_op) {
				type = Type.compound_assign_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_none) {
				type = Type.method_params_none;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_local) {
				type = Type.method_params_local;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_import) {
				type = Type.method_params_import;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_expr) {
				type = Type.expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_import_arg) {
				type = Type.import_arg;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_bin_op) {
				type = Type.bin_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_arith_op) {
				type = Type.arith_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_rel_op) {
				type = Type.rel_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_eq_op) {
				type = Type.eq_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_cond_op) {
				type = Type.cond_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_location_array) {
				type = Type.location_array;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_location_noarray) {
				type = Type.location_noarray;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_char_literal) {
				type = Type.char_literal;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_expr) {
				type = Type.expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_bool_literal) {
				type = Type.bool_literal;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.ID) {
				type = Type.ID;
				text = node.text;
			}
			else {
				type = Type.invalid;
				text = "";
				Utils.logError(new IllegalStateException("Invalid parse tree type: " + nt));
			}
			children = new ArrayList<>();
			for(ParseTree.Node child: node.children) {
				children.add(new Node(child));
			}
		}*/
		
		private void print(int d) {
			for(int i=0; i<d; i++)
				System.out.print("  ");
			String text = getText();
			System.out.println(this.getClass().getSimpleName() + (text.length()==0? "": " \"" + text + "\""));
			List<IR.Node> children = getChildren();
			if(children != null) {
				for(Node child: children)
					child.print(d+1);
			}
		}
		public void print() {
			print(0);
		}
		public String getText() {
			return "";
		}
		public List<IR.Node> getChildren() {
			return null;
		}
		protected static void expectType(ParseTree.Node node, int expected) {
			if(node.type != expected)
				Utils.logError(new IllegalStateException("Expected type " + expected + ", but got " + node.type));
		}
		protected static void expectEnd(ParseTree.Node node, int pos) {
			if(node.children.size() != pos)
				Utils.logError(new IllegalStateException("Did not reach the end of child list (have pos " + pos + 
						", end is at pos " + node.children.size()));
		}
		protected static void expectLast(ParseTree.Node node, int pos, int expected) {
			if(node.children.size() != pos+1)
				Utils.logError(new IllegalStateException("Did not reach 1 before end of child list (have pos " + pos + 
						", end is at pos " + node.children.size()));
			else if(node.child(pos).type != expected)
				Utils.logError(new IllegalStateException("Expected type " + expected + ", but got " + node.type));
		}
	}
	public class Program extends IR.Node {
		public List<ImportDecl> imports;
		public List<FieldDecl> fields;
		public List<MethodDecl> methods;
		public Program(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_program);
			int pos = 0;
			imports = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_import_decl)
				imports.add(new ImportDecl(node.child(pos++)));
			fields = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_field_decl)
				fields.addAll(FieldDecl.get(node.child(pos++)));
			methods = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_method_decl)
				methods.add(new MethodDecl(node.child(pos++)));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.addAll(imports);
			children.addAll(fields);
			children.addAll(methods);
			return children;
		}
	}
	public class ImportDecl extends IR.Node {
		public String name;
		public ImportDecl(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_import_decl);
			name = node.child(1).text;
		}
		public String getText() {
			return name;
		}
	}
	public enum IRType {
		int_, bool_, void_;
		public String getName()
		{
			switch(this)
			{
			case int_:
				return "int";
			case bool_:
				return "bool";
			case void_:
				return "void";
			}
			Utils.logError(new IllegalStateException("Unknown IRType in IRType.getName()"));
			return null;
		}
		public static IRType getType(ParseTree.Node node)
		{
			if(node.type == ParseTree.Node.Type.RESERVED_BOOL)
				return IRType.bool_;
			else if(node.type == ParseTree.Node.Type.RESERVED_INT)
				return IRType.int_;
			else if(node.type == ParseTree.Node.Type.RESERVED_VOID)
				return IRType.void_;
			Utils.logError(new IllegalStateException("Unknown IRType in IRType.getType()"));
			return null;
		}
	}
	public static class FieldDecl extends IR.Node {
		public IRType type;
		public String ID;
		protected FieldDecl(IRType type, String ID) {
			this.type = type;
			this.ID = ID;
		}
		public static List<FieldDecl> get(ParseTree.Node node)
		{
			expectType(node, ParseTree.Node.Type.AST_field_decl);
			List<FieldDecl> ret = new ArrayList<>();
			IRType type;
			type = IRType.getType(node.child(0));
			for(int i=1; i<node.children.size(); i+=2)
			{
				ParseTree.Node x = node.child(i);
				if(x.children.size() == 1)
					ret.add(new FieldDeclNoArray(type, x.child(0).text));
				else ret.add(new FieldDeclArray(type, x.child(0).text, Integer.parseInt(x.child(2).text)));
			}
			return ret;
		}
	}
	public static class FieldDeclNoArray extends FieldDecl {
		private FieldDeclNoArray(IRType type, String ID) {
			super(type, ID);
		}
		public String getText() {
			return type.getName() + " " + ID;
		}
	}
	public static class FieldDeclArray extends FieldDecl {
		public int length;
		private FieldDeclArray(IRType type, String ID, int length) {
			super(type, ID);
			this.length = length;
		}
		public String getText() {
			return type.getName() + " " + ID + "[" + length + "]";
		}
	}
	public class MethodDecl extends IR.Node {
		public IRType type;
		public String ID;
		public List<MethodDeclParam> params;
		public Block block;
		public MethodDecl(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_method_decl);
			type = IRType.getType(node.child(0));
			ID = node.children.get(1).text;
			params = new ArrayList<>();
			int pos = 3;
			for(; node.children.get(pos).type != ParseTree.Node.Type.RPAREN; pos++) {
				ParseTree.Node x = node.child(pos);
				if(x.type != ParseTree.Node.Type.COMMA)
					params.add(new MethodDeclParam(IRType.getType(x.child(0)), x.child(1).text));
			}
			block = new Block(node.child(pos+1));
		}
		public String getText() {
			String ret = type.getName() + " " + ID + "(";
			boolean first = true;
			for(MethodDeclParam i: params)
			{
				if(first)
					first = false;
				else ret += ", ";
				ret += i.getText();
			}
			ret += ")";
			return ret;
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(block);
			return children;
		}
	}
	public class MethodDeclParam extends FieldDeclNoArray {
		public MethodDeclParam(IRType type, String ID) {
			super(type, ID);
		}
	}
	public class Block extends IR.Node {
		public List<FieldDecl> fields;
		public List<Statement> statements;
		public Block(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_block);
			int pos = 1;
			fields = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_field_decl)
				fields.addAll(FieldDecl.get(node.child(pos++)));
			statements = new ArrayList<>();
			while(pos < node.children.size()-1)
				statements.add(new Statement(node.child(pos++)));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.addAll(fields);
			children.addAll(statements);
			return children;
		}
	}
	public class Statement extends IR.Node {
		public Statement(ParseTree.Node node) {
			
		}
	}
	public void build() {
		root = new Program(parseTree.root);
		root.print();
	}
	
}