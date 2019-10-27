package edu.mit.compilers.semantics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import edu.mit.compilers.Utils;

public class IR {
	private ParseTree parseTree;
	public IR.Node root;
	
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	public static class IRTraverser {
		private Queue<IR.Node> nodes;
		public IRTraverser(IR ir) {
			nodes = new ArrayDeque<>();
			nodes.add(ir.root);
		}
		public boolean hasNext() {
			return !nodes.isEmpty();
		}
		public IR.Node getNext() {
			IR.Node _node = nodes.poll();
			if(_node.getChildren() != null)
				for(IR.Node child: _node.getChildren())
					nodes.add(child);
			return _node;
		}
	}
	public abstract static class Node {
		public Node parent;
		// Add Symbol table
		public SymbolTable symbolTable;

		// Add Method table
		public MethodSymbolTable methodTable;
		public int line;
		protected Node(Node parent, ParseTree.Node node) {
			this.parent = parent;
			this.line = node.line;
		}
		protected Node(Node parent, int line) {
			this.parent = parent;
			this.line = line;
		}
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
			super(null, node);
			expectType(node, ParseTree.Node.Type.AST_program);
			int pos = 0;
			imports = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_import_decl)
				imports.add(new ImportDecl(this, node.child(pos++)));
			fields = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_field_decl)
				fields.addAll(FieldDecl.create(this, node.child(pos++)));
			methods = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_method_decl)
				methods.add(new MethodDecl(this, node.child(pos++)));
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
		public ImportDecl(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_import_decl);
			name = node.child(1).text;
		}
		public String getText() {
			return name;
		}
	}
	public abstract static class FieldDecl extends IR.Node {
		public IRType type;
		public String ID;
		protected FieldDecl(IR.Node parent, ParseTree.Node node, IRType type, String ID) {
			super(parent, node);
			this.type = type;
			this.ID = ID;
		}
		public static List<FieldDecl> create(IR.Node parent, ParseTree.Node node)
		{
			expectType(node, ParseTree.Node.Type.AST_field_decl);
			List<FieldDecl> ret = new ArrayList<>();
			IRType type;
			type = IRType.getType(node.child(0));
			for(int i=1; i<node.children.size(); i+=2)
			{
				ParseTree.Node x = node.child(i);
				if(x.children.size() == 1)
					ret.add(new FieldDeclNoArray(parent, x, type, x.child(0).text));
				else ret.add(new FieldDeclArray(parent, x, type, x.child(0).text, getIntValue(x.child(2))));
			}
			return ret;
		}
	}
	public static class FieldDeclNoArray extends FieldDecl {
		private FieldDeclNoArray(IR.Node parent, ParseTree.Node node, IRType type, String ID) {
			super(parent, node, type, ID);
		}
		public String getText() {
			return type.getName() + " " + ID;
		}
	}
	public static class FieldDeclArray extends FieldDecl {
		public long length;
		private FieldDeclArray(IR.Node parent, ParseTree.Node node, IRType type, String ID, long length) {
			super(parent, node, type, ID);
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
		public MethodDecl(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_method_decl);
			type = IRType.getType(node.child(0));
			ID = node.child(1).text;
			params = new ArrayList<>();
			int pos = 3;
			for(; node.children.get(pos).type != ParseTree.Node.Type.RPAREN; pos++) {
				ParseTree.Node x = node.child(pos);
				if(x.type != ParseTree.Node.Type.COMMA)
					params.add(new MethodDeclParam(this, x, IRType.getType(x.child(0)), x.child(1).text));
			}
			block = new Block(this, node.child(pos+1));
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
		public MethodDeclParam(IR.Node parent, ParseTree.Node node, IRType type, String ID) {
			super(parent, node, type, ID);
		}
	}
	public class Block extends IR.Node {
		public List<FieldDecl> fields;
		public List<Statement> statements;
		public Block(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_block);
			int pos = 1;
			fields = new ArrayList<>();
			while(pos<node.children.size() && node.child(pos).type==ParseTree.Node.Type.AST_field_decl)
				fields.addAll(FieldDecl.create(this, node.child(pos++)));
			statements = new ArrayList<>();
			while(pos < node.children.size()-1)
			{
				int nt = node.child(pos).type;
				if(nt == ParseTree.Node.Type.AST_statement_assignment)
					statements.add(new AssignmentStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_method_call)
					statements.add(new MethodCall(this, node.child(pos).child(0)));
				else if(nt == ParseTree.Node.Type.AST_statement_if)
					statements.add(new IfStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_for)
					statements.add(new ForStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_while)
					statements.add(new WhileStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_return)
					statements.add(new ReturnStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_break)
					statements.add(new BreakStatement(this, node.child(pos)));
				else if(nt == ParseTree.Node.Type.AST_statement_continue)
					statements.add(new ContinueStatement(this, node.child(pos)));
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
	public abstract static class Statement extends IR.Node {
		protected Statement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
		}
	}
	public class AssignmentStatement extends Statement {
		public Location loc;
		public Op op; //three possible ops: plusequal, minusequal, assignment
		public Expr assignExpr;
		public AssignmentStatement(IR.Node parent, ParseTree.Node node_loc, ParseTree.Node node_incdec) {
			super(parent, node_loc);
			loc = Location.create(this, node_loc);
			if(node_incdec.type == ParseTree.Node.Type.INCREMENT) {
				op = new Op(this, node_loc, Op.Type.increment);
				assignExpr = null;
			}
			else if(node_incdec.type == ParseTree.Node.Type.DECREMENT) {
				op = new Op(this, node_loc, Op.Type.decrement);
				assignExpr = null;
			}
			else Utils.logError(new IllegalStateException("Expected increment/decrement, but got type " + node_incdec.type));
		}
		public AssignmentStatement(IR.Node parent, ParseTree.Node node_loc, ParseTree.Node node_op, ParseTree.Node node_expr) {
			super(parent, node_loc);
			loc = Location.create(this, node_loc);
			op = new Op(this, node_op);
			assignExpr = new Expr(this, node_expr);
		}
		public AssignmentStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_assignment);
			loc = Location.create(this, node.child(0));
			node = node.child(1);
			if(node.child(0).type == ParseTree.Node.Type.INCREMENT) {
				op = new Op(this, node.child(0), Op.Type.increment);
				assignExpr = null;
			}
			else if(node.child(0).type == ParseTree.Node.Type.DECREMENT) {
				op = new Op(this, node.child(0), Op.Type.decrement);
				assignExpr = null;
			}
			else {
				op = new Op(this, node.child(0));
				assignExpr = new Expr(this, node.child(1));
			}
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(loc);
			children.add(op);
			if(assignExpr != null)
				children.add(assignExpr);
			return children;
		}
	}
	public class IfStatement extends Statement {
		public Expr condition;
		public Block block;
		public Block elseBlock;
		public IfStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_if);
			condition = new Expr(this, node.child(2));
			block = new Block(this, node.child(4));
			if(node.children.size() > 6)
				elseBlock = new Block(this, node.child(6));
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(condition);
			children.add(block);
			if(elseBlock != null)
				children.add(elseBlock);
			return children;
		}
	}
	public class ForStatement extends Statement {
		public LocationNoArray initLoc;
		public Expr initExpr;
		public Expr condition;
		public AssignmentStatement iteration; //might not be great from a design perspective, but it works
		public Block block;
		public ForStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_for);
			initLoc = new LocationNoArray(this, node.child(2));
			initExpr = new Expr(this, node.child(4));
			condition = new Expr(this, node.child(6));
			if(node.children.size() == 12) //inc/dec
				iteration = new AssignmentStatement(this, node.child(8), node.child(9));
			else iteration = new AssignmentStatement(this, node.child(8), node.child(9), node.child(10));
			block = new Block(this, node.child(node.children.size()-1));
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
		public WhileStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_while);
			condition = new Expr(this, node.child(2));
			block = new Block(this, node.child(4));
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
		public ReturnStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_return);
			if(node.children.size() == 3)
				expr = new Expr(this, node.child(1));
			else expr = null; //returns nothing
		}
		public List<IR.Node> getChildren() {
			if(expr == null)
				return null;
			List<IR.Node> children = new ArrayList<>();
			children.add(expr);
			return children;
		}
	}
	public class BreakStatement extends Statement {
		public BreakStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_break);
		}
	}
	public class ContinueStatement extends Statement {
		public ContinueStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_continue);
		}
	}
	public static class MethodCall extends Statement {
		public String ID;
		public List<MethodParam> params;
		public MethodCall(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_method_call);
			ID = node.child(0).text;
			params = new ArrayList<>();
			node = node.child(1); //now points to params
			for(int i=1; i<node.children.size(); i+=2)
				params.add(new MethodParam(this, node.child(i)));
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
		public MethodParam(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			if(node.type == ParseTree.Node.Type.AST_import_arg)
				node = node.child(0);
			if(node.type == ParseTree.Node.Type.AST_expr)
				val = new Expr(this, node);
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
		public Expr(IR.Node parent, ParseTree.Node node, long val) {
			super(parent, node);
			members = new ArrayList<>();
			members.add(new IntLiteral(this, node, val));
		}
		public Expr(IR.Node parent, ParseTree.Node node, List<Node> members) {
			super(parent, node);
			this.members = members;
		}
		public Expr(IR.Node parent, int line, List<Node> members) {
			super(parent, line);
			this.members = members;
		}
		public Expr(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_expr);
			members = new ArrayList<>();
			for(ParseTree.Node child: node.children) {
				if(child.type == ParseTree.Node.Type.AST_bin_op)
					members.add(new Op(this, child));
				else if(child.type == ParseTree.Node.Type.MINUS)
					members.add(new Op(this, child, Op.Type.minus));
				else if(child.type == ParseTree.Node.Type.NOT)
					members.add(new Op(this, child, Op.Type.not));
				else if(child.type == ParseTree.Node.Type.AST_method_call)
					members.add(new MethodCall(this, child));
				else if(child.type == ParseTree.Node.Type.AST_location_array || 
						child.type == ParseTree.Node.Type.AST_location_noarray)
					members.add(Location.create(this, child));
				else if(child.type == ParseTree.Node.Type.AST_bool_literal)
					members.add(new BoolLiteral(this, child));
				else if(child.type == ParseTree.Node.Type.AST_int_literal) { //semantic check due to signs
					if(!members.isEmpty() && (members.size()==1 || members.get(members.size()-2) instanceof Op) &&
						(members.get(members.size()-1) instanceof Op) &&
						((Op)members.get(members.size()-1)).type == Op.Type.minus) {
							members.remove(members.size()-1);
							child.child(0).text = "-" + child.child(0).text;
							members.add(new IntLiteral(this, child));
						}
					else members.add(new IntLiteral(this, child));
				}
				else if(child.type == ParseTree.Node.Type.AST_char_literal)
					members.add(new CharLiteral(this, child));
				else if(child.type == ParseTree.Node.Type.AST_len)
					members.add(new Len(this, child));
				else if(child.type == ParseTree.Node.Type.AST_expr)
					members.add(new Expr(this, child));
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
		public String getType (){
			// NOTE: DO NOT CALL THIS FUNCTION UNTIL AFTER THE IR IS FINISHED BUILDING

			IR.Node child1 = members.get(0); 
			if (child1 instanceof LocationArray){
				return ((LocationArray)child1).getType(); 
			}
			if (child1 instanceof LocationNoArray){
				return ((LocationNoArray)child1).getType(); 
			}
			if (child1 instanceof MethodCall){
				System.out.println("ID: " + ((MethodCall)child1).ID); 
				String ans = this.methodTable.MethodTableEntries.get(((MethodCall)child1).ID).type.getName(); 
				
				
				if (ans.equals("void")){
					throw new IllegalStateException ("Bad method call in expression."); 
				}
				return ans; 
			}
			if (child1 instanceof IntLiteral){
				return "int"; 
			}
			if (child1 instanceof BoolLiteral){
				return "bool";
			}
			if (child1 instanceof CharLiteral){
				return "char"; 
			}
			if (child1 instanceof Len){
				return "int"; 
			}
			if (child1 instanceof Op){
				if (((Op)child1).type == Op.Type.minus) return "int"; 
				if (((Op)child1).type == Op.Type.not) {
					Expr child2 = (Expr)members.get(1); 
					if (child2.getType() != "bool"){
						throw new IllegalStateException ("Bad not expression."); 
					}
					return "bool"; 
				}
			}
			if (child1 instanceof Expr){
				if (members.size() == 1) return ((Expr)child1).getType(); 
				else{
					IR.Node child2 = members.get(1); 
					if (child2 instanceof Op){
						if (((Op)child2).type == Op.Type.plus || ((Op)child2).type == Op.Type.minus || ((Op)child2).type == Op.Type.mult || ((Op)child2).type == Op.Type.div || ((Op)child2).type == Op.Type.mod){
							return "int"; 
						}
						if (((Op)child2).type == Op.Type.andand || ((Op)child2).type == Op.Type.oror){
							if ((((Expr)child1).getType()) != "bool"){
								throw new IllegalStateException ("Bad conditional operator."); 
							}
							if ((((Expr)(members.get(2))).getType()) != "bool"){
								throw new IllegalStateException ("Bad conditional operator."); 
							}
						}
						return "bool";
					}
				}
			}
			return "none"; 
		}
	}
	public static class Len extends Node {
		public String ID;
		public Len(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_len);
			ID = node.child(2).text;
		}
		public String getText() {
			return ID;
		}
	}
	public static class Op extends Node {
		public enum Type {
			plus, minus, mult, div, mod, less, greater, leq, geq, eq, neq, andand, oror, not,
			plusequals, minusequals, assign, increment, decrement;
		}
		public Type type;
		public Op(IR.Node parent, ParseTree.Node node, Type type) {
			super(parent, node);
			this.type = type;
		}
		public Op(IR.Node parent, int line, Type type) {
			super(parent, line);
			this.type = type;
		}
		public Op(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
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
		public Location(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
		}
		public static Location create(IR.Node parent, ParseTree.Node node) {
			if(node.type == ParseTree.Node.Type.AST_location_array)
				return new LocationArray(parent, node);
			else return new LocationNoArray(parent, node);
		}
		public String getType(){
			return "void"; 
		}
	}
	public static class LocationArray extends Location {
		public Expr index;
		public LocationArray(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_location_array);
			ID = node.child(0).text;
			index = new Expr(this, node.child(2));
		}
		public String getText() {
			return ID;
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> children = new ArrayList<>();
			children.add(index);
			return children;
		}
		public String getType(){
			return ((FieldDeclArray)(this.symbolTable.find(this.ID))).type.getName(); 
		}
	}
	public static class LocationNoArray extends Location {
		public LocationNoArray(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
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
		public String getType(){
			System.out.println("LOL: " + this.ID); 
			return ((FieldDecl)(this.symbolTable.find(this.ID))).type.getName(); 
		}
	}
	public abstract static class Literal extends IR.Node {
		public Literal(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
		}
		public Literal(IR.Node parent, int line) {
			super(parent, line);
		}
	}
	public static class BoolLiteral extends Literal {
		public boolean value;
		public BoolLiteral(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_bool_literal);
			node = node.child(0);
			value = Boolean.parseBoolean(node.text);
		}
		public String getText() {
			return Boolean.toString(value);
		}
	}
	public static class IntLiteral extends Literal {
		public long value;
		public IntLiteral(IR.Node parent, ParseTree.Node node, long val) {
			super(parent, node);
			this.value = val;
		}
		public IntLiteral(IR.Node parent, int line, long val) {
			super(parent, line);
			this.value = val;
		}
		public IntLiteral(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_int_literal);
			value = getIntValue(node);
		}
		public String getText() {
			return Long.toString(value);
		}
	}
	public static class CharLiteral extends Literal {
		public char value;
		public CharLiteral(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
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
		postprocess();
	}
	private final Op.Type[] exprPrecedence = new Op.Type[] {Op.Type.oror, Op.Type.andand, Op.Type.neq, Op.Type.eq, Op.Type.less,
			Op.Type.leq, Op.Type.greater, Op.Type.geq, Op.Type.plus, Op.Type.minus, Op.Type.mult,
			Op.Type.div, Op.Type.mod, Op.Type.not}; //Op.Type.minus is alraedy manually handled
	private void parseExpr(Expr expr) {
		for(Op.Type cur: exprPrecedence) {
			for(int i=0; i<expr.members.size(); i++) {
				Node _node = expr.members.get(i);
				if(_node instanceof Op) {
					Op op = (Op)_node;
					if(op.type == cur) {
						List<Node> newMembers = new ArrayList<>();
						Expr e1 = new Expr(expr, expr.line, expr.members.subList(0, i));
						Expr e2 = new Expr(expr, expr.line, expr.members.subList(i+1, expr.members.size()));
						parseExpr(e1);
						parseExpr(e2);
						newMembers.add(e1);
						newMembers.add(new Op(expr, op.line, op.type));
						newMembers.add(e2);
						expr.members = newMembers;
						return;
					}
				}
			}
		}
	}
	public void postprocess() {
		IRTraverser irTraverser = new IRTraverser(this);
		while(irTraverser.hasNext()) {
			IR.Node _node = irTraverser.getNext();
			if(_node instanceof Expr) {
				Expr expr = (Expr)_node;
				parseExpr(expr);
			}
		}
	}
}
