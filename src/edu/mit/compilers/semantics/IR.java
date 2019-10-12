package edu.mit.compilers.semantics;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.Utils;

public class IR {
	private ParseTree parseTree;
	private IR.Node root;
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	public abstract static class Node {
		
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
			if(node.type == ParseTree.Node.Type.AST_type)
				node = node.child(0);
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
	private static long getIntValue(ParseTree.Node node) {
		node = node.child(0);
		if(node.type == ParseTree.Node.Type.HEXLITERAL)
			return Long.parseLong(node.text.substring(2), 16);
		return Long.parseLong(node.text);
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
				fields.addAll(FieldDecl.create(node.child(pos++)));
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
	public static class FieldDecl extends IR.Node {
		public IRType type;
		public String ID;
		protected FieldDecl(IRType type, String ID) {
			this.type = type;
			this.ID = ID;
		}
		public static List<FieldDecl> create(ParseTree.Node node)
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
				else ret.add(new FieldDeclArray(type, x.child(0).text, getIntValue(x.child(2))));
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
		public long length;
		private FieldDeclArray(IRType type, String ID, long length) {
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
			ID = node.child(1).text;
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
				fields.addAll(FieldDecl.create(node.child(pos++)));
			statements = new ArrayList<>();
			while(pos < node.children.size()-1)
			{
				int nt = node.child(pos).type;
				if(nt == ParseTree.Node.Type.AST_statement_assignment)
					statements.add(new AssignmentStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_method_call)
					statements.add(new MethodCall(node.child(pos).child(0)));
				else if(nt == ParseTree.Node.Type.AST_statement_if)
					statements.add(new IfStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_for)
					statements.add(new ForStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_while)
					statements.add(new WhileStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_return)
					statements.add(new ReturnStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_break)
					statements.add(new BreakStatement(node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_continue)
					statements.add(new ContinueStatement(node.child(pos)));
				else Utils.logError(new IllegalStateException("Unknown type " + nt + " " + pos));
				pos++;
			}
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.addAll(fields);
			children.addAll(statements);
			return children;
		}
	}
	public abstract static class Statement extends IR.Node {}
	public class AssignmentStatement extends Statement {
		public Location loc;
		public Op op; //three possible ops: plusequal(1), minusequal(1), assignment
		public Expr assignExpr;
		public AssignmentStatement(ParseTree.Node node_loc, ParseTree.Node node_incdec) {
			loc = Location.create(node_loc);
			if(node_incdec.type == ParseTree.Node.Type.INCREMENT) {
				op = new Op(Op.Type.plusequals);
				assignExpr = new Expr(1);
			}
			else if(node_incdec.type == ParseTree.Node.Type.DECREMENT) {
				op = new Op(Op.Type.minusequals);
				assignExpr = new Expr(1);
			}
			else Utils.logError(new IllegalStateException("Expected increment/decrement, but got type " + node_incdec.type));
		}
		public AssignmentStatement(ParseTree.Node node_loc, ParseTree.Node node_op, ParseTree.Node node_expr) {
			loc = Location.create(node_loc);
			op = new Op(node_op);
			assignExpr = new Expr(node_expr);
		}
		public AssignmentStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_assignment);
			loc = Location.create(node.child(0));
			node = node.child(1);
			//increment/decrement are treated as (+/-)=1
			if(node.child(0).type == ParseTree.Node.Type.INCREMENT) {
				op = new Op(Op.Type.plusequals);
				assignExpr = new Expr(1);
			}
			else if(node.child(0).type == ParseTree.Node.Type.DECREMENT) {
				op = new Op(Op.Type.minusequals);
				assignExpr = new Expr(1);
			}
			else {
				op = new Op(node.child(0));
				assignExpr = new Expr(node.child(1));
			}
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(loc);
			children.add(op);
			children.add(assignExpr);
			return children;
		}
	}
	public class IfStatement extends Statement {
		public Expr condition;
		public Block block;
		public List<Block> elseBlock; //can there be multiple else blocks?
		public IfStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_if);
			condition = new Expr(node.child(2));
			block = new Block(node.child(4));
			elseBlock = new ArrayList<>();
			for(int i=6; i<node.children.size(); i+=2)
				elseBlock.add(new Block(node.child(i)));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(condition);
			children.add(block);
			children.addAll(elseBlock);
			return children;
		}
	}
	public class ForStatement extends Statement {
		public LocationNoArray initLoc;
		public Expr initExpr;
		public Expr condition;
		public AssignmentStatement iteration; //might not be great from a design perspective, but it works
		public Block block;
		public ForStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_for);
			initLoc = new LocationNoArray(node.child(2));
			initExpr = new Expr(node.child(4));
			condition = new Expr(node.child(6));
			if(node.children.size() == 12) //inc/dec
				iteration = new AssignmentStatement(node.child(8), node.child(9));
			else iteration = new AssignmentStatement(node.child(8), node.child(9), node.child(10));
			block = new Block(node.child(node.children.size()-1));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(initLoc);
			children.add(initExpr);
			children.add(condition);
			children.add(iteration);
			children.add(block);
			return children;
		}
	}
	public class WhileStatement extends Statement {
		public Expr condition;
		public Block block;
		public WhileStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_while);
			condition = new Expr(node.child(2));
			block = new Block(node.child(4));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(condition);
			children.add(block);
			return children;
		}
	}
	public class ReturnStatement extends Statement {
		public Expr expr;
		public ReturnStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_return);
			expr = new Expr(node.child(1));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(expr);
			return children;
		}
	}
	public class BreakStatement extends Statement {
		public BreakStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_break);
		}
	}
	public class ContinueStatement extends Statement {
		public ContinueStatement(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_statement_continue);
		}
	}
	public static class MethodCall extends Statement {
		public String ID;
		public List<MethodParam> params;
		public MethodCall(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_method_call);
			ID = node.child(0).text;
			params = new ArrayList<>();
			node = node.child(1); //now points to params
			for(int i=1; i<node.children.size(); i+=2)
				params.add(new MethodParam(node.child(i)));
		}
		public String getText() {
			return ID;
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.addAll(params);
			return children;
		}
	}
	public static class MethodParam extends Node {
		public Object val;
		public MethodParam(ParseTree.Node node) {
			if(node.type == ParseTree.Node.Type.AST_import_arg)
				node = node.child(0);
			if(node.type == ParseTree.Node.Type.AST_expr)
				val = new Expr(node);
			else val = node.text;
		}
		public String getText() {
			if(val instanceof String)
				return (String)val;
			return "";
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>(); 
			if(val instanceof Expr)
				children.add((Expr)val);
			return children;
		}
	}
	public static class Expr extends IR.Node {
		public List<Node> members;
		public Expr(long val) {
			members = new ArrayList<>();
			members.add(new IntLiteral(val));
		}
		public Expr(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_expr);
			members = new ArrayList<>();
			for(ParseTree.Node child: node.children) {
				if(child.type == ParseTree.Node.Type.AST_bin_op)
					members.add(new Op(child));
				else if(child.type == ParseTree.Node.Type.MINUS)
					members.add(new Op(Op.Type.minus));
				else if(child.type == ParseTree.Node.Type.NOT)
					members.add(new Op(Op.Type.not));
				else if(child.type == ParseTree.Node.Type.AST_method_call)
					members.add(new MethodCall(child));
				else if(child.type == ParseTree.Node.Type.AST_location_array || 
						child.type == ParseTree.Node.Type.AST_location_noarray)
					members.add(Location.create(child));
				else if(child.type == ParseTree.Node.Type.AST_bool_literal)
					members.add(new BoolLiteral(child));
				else if(child.type == ParseTree.Node.Type.AST_int_literal)
					members.add(new IntLiteral(child));
				else if(child.type == ParseTree.Node.Type.AST_char_literal)
					members.add(new CharLiteral(child));
				else if(child.type == ParseTree.Node.Type.AST_expr)
					members.add(new Expr(child));
				else if(child.type == ParseTree.Node.Type.LPAREN || 
						child.type == ParseTree.Node.Type.RPAREN) //these will always surround an expr
					{;}
				else Utils.logError(new IllegalStateException("Unknown type in Expr: " + child.type));
			}
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.addAll(members);
			return children;
		}
	}
	public static class Op extends Node {
		public enum Type {
			plus, minus, mult, div, mod, less, greater, leq, geq, eq, neq, andand, oror, not,
			plusequals, minusequals, assign;
		}
		public Type type;
		public Op(Type type) {
			this.type = type;
		}
		public Op(ParseTree.Node node) {
			if(node.type == ParseTree.Node.Type.AST_assign_op)
			{
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.ASSIGNMENT) {
					type = Type.assign;
					return;
				}
			}
			if(node.type == ParseTree.Node.Type.AST_compound_assign_op)
			{
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.PLUSEQUALS)
					type = Type.plusequals;
				else if(node.type == ParseTree.Node.Type.MINUSEQUALS)
					type = Type.minusequals;
				else Utils.logError("Expected +=/-=, but got type " + node.type);
				return;
			}
			expectType(node, ParseTree.Node.Type.AST_bin_op);
			node = node.child(0);
			if(node.type == ParseTree.Node.Type.AST_arith_op) {
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.PLUS)
					type = Type.plus;
				else if(node.type == ParseTree.Node.Type.MINUS)
					type = Type.minus;
				else if(node.type == ParseTree.Node.Type.MULT)
					type = Type.mult;
				else if(node.type == ParseTree.Node.Type.DIV)
					type = Type.div;
				else if(node.type == ParseTree.Node.Type.PERCENT)
					type = Type.mod;
				else Utils.logError(new IllegalStateException("Unknown op type " + node.type));
			}
			else if(node.type == ParseTree.Node.Type.AST_rel_op) {
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.LESS)
					type = Type.less;
				else if(node.type == ParseTree.Node.Type.GREATER)
					type = Type.greater;
				else if(node.type == ParseTree.Node.Type.LEQ)
					type = Type.leq;
				else if(node.type == ParseTree.Node.Type.GEQ)
					type = Type.geq;
				else Utils.logError(new IllegalStateException("Unknown op type " + node.type));
			}
			else if(node.type == ParseTree.Node.Type.AST_eq_op) {
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.EQUALITY)
					type = Type.eq;
				else if(node.type == ParseTree.Node.Type.NEQ)
					type = Type.neq;
				else Utils.logError(new IllegalStateException("Unknown op type " + node.type));
			}
			else if(node.type == ParseTree.Node.Type.AST_cond_op) {
				node = node.child(0);
				if(node.type == ParseTree.Node.Type.ANDAND)
					type = Type.andand;
				else if(node.type == ParseTree.Node.Type.OROR)
					type = Type.oror;
				else Utils.logError(new IllegalStateException("Unknown op type " + node.type));
			}
			else Utils.logError(new IllegalStateException("Unknown op type " + node.type));
		}
		public String getText() {
			return type.name();
		}
	}
	public abstract static class Location extends IR.Node {
		public String ID;
		public static Location create(ParseTree.Node node) {
			if(node.type == ParseTree.Node.Type.AST_location_array)
				return new LocationArray(node);
			else return new LocationNoArray(node);
		}
	}
	public static class LocationArray extends Location {
		public Expr index;
		public LocationArray(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_location_array);
			ID = node.child(0).text;
			index = new Expr(node.child(2));
		}
		public String getText() {
			return ID;
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(index);
			return children;
		}
	}
	public static class LocationNoArray extends Location {
		public LocationNoArray(ParseTree.Node node) {
			if(node.type == ParseTree.Node.Type.ID) {
				ID = node.text;
				return;
			}
			expectType(node, ParseTree.Node.Type.AST_location_noarray);
			ID = node.child(0).text;
		}
		public String getText() {
			return ID;
		}
	}
	public static class BoolLiteral extends IR.Node {
		public boolean value;
		public BoolLiteral(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_bool_literal);
			node = node.child(0);
			value = Boolean.parseBoolean(node.text);
		}
		public String getText() {
			return Boolean.toString(value);
		}
	}
	public static class IntLiteral extends IR.Node {
		public long value;
		public IntLiteral(long val) {
			this.value = val;
		}
		public IntLiteral(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_int_literal);
			value = getIntValue(node);
		}
		public String getText() {
			return Long.toString(value);
		}
	}
	public static class CharLiteral extends IR.Node {
		public char value;
		public CharLiteral(ParseTree.Node node) {
			expectType(node, ParseTree.Node.Type.AST_char_literal);
			node = node.child(0);
			value = (char)node.text.getBytes()[0];
		}
		public String getText() {
			String ret = new String();
			ret += value;
			return ret;
		}
	}
	public void build() {
		root = new Program(parseTree.root);
		root.print();
	}
}