package edu.mit.compilers.semantics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.mit.compilers.Utils;

public class IR {
	private ParseTree parseTree;
	public IR.Node root;
	
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	private static class IRTraverser {
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
		if(node.type == ParseTree.Node.Type.HEXLITERAL) {
			if(node.text.charAt(0) == '-')
				return Long.parseLong("-" + node.text.substring(3), 16);
			return Long.parseLong(node.text.substring(2), 16);
		}
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
		protected FieldDecl(IR.Node parent, int line) {
			super(parent, line);
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
		public FieldDeclNoArray(IR.Node parent, int line, LocationNoArray loc, IRType type) {
			super(parent, line);
			this.ID = loc.ID;
			this.type = type;
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
		public Block(IR.Node parent, AssignmentStatement AS) {
			super(parent, -1);
			fields = new ArrayList<>();
			statements = new ArrayList<>();
			statements.add(AS);
		}
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
		protected Statement(IR.Node parent, int line) {
			super(parent, line);
		}
	}
	public class AssignmentStatement extends Statement {
		public Location loc;
		public Op op; //three possible ops: plusequal, minusequal, assignment
		public Expr assignExpr;
		public AssignmentStatement(IR.Node parent, int line, Location varLoc, Op.Type type, Expr expr) {
			super(parent, line);
			this.loc = varLoc;
			this.op = new Op(this, line, type);
			this.assignExpr = expr;
		}
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
		public boolean flip = false;
		public IfStatement(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_statement_if);
			condition = new Expr(this, node.child(2));
			block = new Block(this, node.child(4));
			if(node.children.size() > 6)
				elseBlock = new Block(this, node.child(6));
		}
		public String getText() {
			return flip? "flip": "";
		}
		public IfStatement(Expr expr, boolean flip, AssignmentStatement AS) {
			super(null, -1);
			condition = expr;
			block = new Block(this, AS);
			this.flip = flip;
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
	private class CalcConditionHolder extends IR.Node { //only used for pretty printing
		public List<Statement> calcCondition;
		public CalcConditionHolder(List<Statement> calcCondition) {
			super(null, -1);
			this.calcCondition = calcCondition;
		}
		public List<IR.Node> getChildren() {
			List<IR.Node> ret = new ArrayList<>();
			ret.addAll(calcCondition);
			return ret;
		}
	}
	public class ForStatement extends Statement {
		public LocationNoArray initLoc;
		public Expr initExpr;
		public Expr condition;
		public List<Statement> calcCondition; //all Assignment or If
		public List<Statement> iterationStatements; //all Assignment or If
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
			if(initLoc != null) {
				children.add(initLoc);
				children.add(initExpr);
			}
			children.add(condition);
			if(calcCondition != null) {
				IR.Node dummy = new CalcConditionHolder(calcCondition);
				children.add(dummy);
			}
			if(iterationStatements != null) {
				IR.Node dummy = new CalcConditionHolder(iterationStatements);
				children.add(dummy);
			}
			else children.add(iteration);
			children.add(block);
			return children;
		}
	}
	public class WhileStatement extends Statement {
		public Expr condition;
		public List<Statement> calcCondition; //all Assignment or If
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
			if(calcCondition != null) {
				IR.Node dummy = new CalcConditionHolder(calcCondition);
				children.add(dummy);
			}
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
			for(int i=1; i<node.children.size()-1; i+=2)
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
		public List<IR.Node> members;
		public Expr(Expr expr) {
			super(expr.parent, expr.line);
			members = new ArrayList<>(expr.members);
		}
		public Expr(IR.Node parent, ParseTree.Node node, long val) {
			super(parent, node);
			members = new ArrayList<>();
			members.add(new IntLiteral(this, node, val));
		}
		public Expr(IR.Node parent, ParseTree.Node node, List<IR.Node> members) {
			super(parent, node);
			this.members = members;
		}
		public Expr(IR.Node parent, int line, List<IR.Node> members) {
			super(parent, line);
			this.members = members;
		}
		public Expr(IR.Node parent, int line, IR.Node child) {
			super(parent, line);
			this.members = new ArrayList<>();
			this.members.add(child);
		}
		public Expr(IR.Node parent, int line, Op.Type optype, IR.Node child2) {
			super(parent, line);
			this.members = new ArrayList<>();
			this.members.add(new Op(this, line, optype));
			this.members.add(child2);
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
		public IRType getT() {
			String stype = getType();
			if(stype.equals("int") || stype.equals("any"))
				return IRType.int_;
			else if(stype.equals("bool"))
				return IRType.bool_;
			else throw new IllegalStateException("bad type " + stype);
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
				//System.out.println("ID: " + ((MethodCall)child1).ID); 
				if (this.methodTable.MethodTableEntries.containsKey(((MethodCall)child1).ID)){
					String ans = this.methodTable.MethodTableEntries.get(((MethodCall)child1).ID).type.getName(); 
					
					
					if (ans.equals("void")){
						throw new IllegalStateException ("Bad method call in expression."); 
					}
					return ans; 
				}
				return "int"; //assume imports return ints
				//return "any";
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
				if (((Op)child1).type == Op.Type.minus) {
					Expr child2 = (Expr)members.get(1); 
					if (child2.getType() != "int"){
						throw new IllegalStateException ("Bad minus expression."); 
					}	
					return "int"; 
				}
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
							if ((((Expr)child1).getType()) != "int"){
								throw new IllegalStateException ("Bad arith operator."); 
							}
							if ((((Expr)(members.get(2))).getType()) != "int"){
								throw new IllegalStateException ("Bad arith operator."); 
							}							
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
						if (((Op)child2).type == Op.Type.eq || ((Op)child2).type == Op.Type.neq){
							if (!((((Expr)child1).getType()).equals(((Expr)(members.get(2))).getType()))){
								throw new IllegalStateException ("Bad eq operator."); 
							}					
							if (!((((Expr)child1).getType()).equals("int")) && !((((Expr)child1).getType()).equals("bool"))){
								throw new IllegalStateException ("Bad eq operator."); 
							}	
						}
						if (((Op)child2).type == Op.Type.less || ((Op)child2).type == Op.Type.greater || ((Op)child2).type == Op.Type.leq || ((Op)child2).type == Op.Type.geq){
							if ((((Expr)child1).getType()) != "int"){
								throw new IllegalStateException ("Bad rel operator."); 
							}
							if ((((Expr)(members.get(2))).getType()) != "int"){
								throw new IllegalStateException ("Bad rel operator."); 
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
		protected Location(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
		}
		protected Location(IR.Node parent, int line) {
			super(parent, line);
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
		public LocationNoArray(IR.Node parent, int line, String ID) {
			super(parent, line);
			this.ID = ID;
		}
		public String getText() {
			return ID;
		}
		public IRType getT() {
			String stype = getType();
			if(stype.equals("int"))
				return IRType.int_;
			else if(stype.equals("bool"))
				return IRType.bool_;
			else throw new IllegalStateException("bad type " + stype);
		}
		public String getType() {
			if ((this.symbolTable.find(this.ID)) instanceof FieldDeclArray){
				throw new IllegalStateException("Bad array");
			}
			return ((FieldDecl)(this.symbolTable.find(this.ID))).type.getName(); 
		}
	}
	public abstract static class Literal extends IR.Node {
		protected Literal(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
		}
		protected Literal(IR.Node parent, int line) {
			super(parent, line);
		}
		public abstract long val();
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
		public long val() {
			return value? 1: 0;
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
		public long val() {
			return value;
		}
	}
	public static class CharLiteral extends Literal {
		public char value;
		public CharLiteral(IR.Node parent, ParseTree.Node node) {
			super(parent, node);
			expectType(node, ParseTree.Node.Type.AST_char_literal);
			node = node.child(0);
			value = (char)node.text.getBytes()[1];
		}
		public String getText() {
			String ret = new String();
			ret += value;
			return ret;
		}
		public long val() {
			return value;
		}
	}
	public void build() {
		root = new Program(parseTree.root);
		postprocess();
	}
	private final Op.Type[] binOpExprPrecedence = new Op.Type[] {Op.Type.oror, Op.Type.andand, Op.Type.neq, Op.Type.eq, Op.Type.less,
			Op.Type.leq, Op.Type.greater, Op.Type.geq, Op.Type.plus, Op.Type.minus, Op.Type.mult,
			Op.Type.div, Op.Type.mod};
	private final Op.Type[] unaryOpExprPrecedence = new Op.Type[] {Op.Type.minus, Op.Type.not};
	private void parseExpr(Expr expr) {
		if(expr.members.size()==1 && (expr.members.get(0) instanceof Expr)) {
			parseExpr((Expr)expr.members.get(0));
			return;
		}
		for(Op.Type cur: binOpExprPrecedence) {
			for(int i=expr.members.size()-1; i>=0; i--) {
				Node _node = expr.members.get(i);
				if(_node instanceof Op) {
					Op op = (Op)_node;
					if(op.type == cur) {
						if(cur==Op.Type.minus && (i==0 || (expr.members.get(i-1) instanceof Op))) //NEGATIVE sign, not MINUS
							continue;
						Expr e1 = new Expr(expr, expr.line, expr.members.subList(0, i));
						Expr e2 = new Expr(expr, expr.line, expr.members.subList(i+1, expr.members.size()));
						parseExpr(e1);
						parseExpr(e2);
						List<Node> newMembers = new ArrayList<>();
						newMembers.add(e1);
						newMembers.add(new Op(expr, op.line, op.type));
						newMembers.add(e2);
						expr.members = newMembers;
						return;
					}
				}
			}
		}
		//I'm operating with the assumption that NEGATION and NOT effectively have the same precedence
		if(expr.members.size() > 2) {
			for(Op.Type cur: unaryOpExprPrecedence) {
				Node _node = expr.members.get(0);
				if(_node instanceof Op) {
					Op op = (Op)_node;
					if(op.type == cur) {
						Expr e2 = new Expr(expr, expr.line, expr.members.subList(1, expr.members.size()));
						parseExpr(e2);
						List<Node> newMembers = new ArrayList<>();
						newMembers.add(new Op(expr, op.line, op.type));
						newMembers.add(e2);
						expr.members = newMembers;
						return;
					}
				}
			}
		}
	}
	private void fixNesting(Expr expr) {
		if(expr.members.size() == 1) {
			Node child0 = expr.members.get(0);
			if(child0 instanceof Expr) {
				expr.members = ((Expr)child0).members;
				for(IR.Node i: expr.members)
					i.parent = expr;
				fixNesting(expr);
			}
		}
		else if(expr.members.size() == 2) {
			if(!(expr.members.get(1) instanceof Expr))
				expr.members.set(1, new Expr(expr, expr.line, expr.members.get(1)));
			else fixNesting((Expr)expr.members.get(1));
		}
		else if(expr.members.size() == 3) {
			if(!(expr.members.get(0) instanceof Expr))
				expr.members.set(0, new Expr(expr, expr.line, expr.members.get(0)));
			else fixNesting((Expr)expr.members.get(0));
			if(!(expr.members.get(2) instanceof Expr))
				expr.members.set(2, new Expr(expr, expr.line, expr.members.get(2)));
			else fixNesting((Expr)expr.members.get(2));
		}
	}
	private Map<Expr, LocationNoArray> tempExpr;
	public LocationNoArray exprToTempVar(Block block, IRType type, Expr expr) {
		if(!tempExpr.containsKey(expr)) {
			String name = "@temp" + tempExpr.size();
			LocationNoArray loc = new LocationNoArray(block, block.line, name);
			FieldDeclNoArray field_decl = new FieldDeclNoArray(block, block.line, loc, type);
			block.fields.add(field_decl);
			loc.symbolTable = new SymbolTable();
			loc.symbolTable.add(name, field_decl);
			tempExpr.put(expr, loc);
		}
		return tempExpr.get(expr);
	}
	//return true if expr is (LocationNoArray) or (Literal)
	private boolean isAtomicExpr(Expr expr) {
		return     expr.members.size()==1 && (
				 ((expr.members.get(0) instanceof LocationNoArray) ||
				  (expr.members.get(0) instanceof Literal)));
	}
	//returns true if expr can be directly computed in assembly ((Atomic op Atomic) or (op Atomic) or (Atomic) or (LocationArray[Atomic]) or (MethodCall(atomics...)))
	private boolean isBasicExpr(Expr expr) {
		if(	   isAtomicExpr(expr) || 
			  (expr.members.size()==1 && ((expr.members.get(0) instanceof LocationArray) && isAtomicExpr(((LocationArray)expr.members.get(0)).index))) ||
			  (expr.members.size()==2 && isAtomicExpr((Expr)expr.members.get(1))) ||
			  (expr.members.size()==3 && isAtomicExpr((Expr)expr.members.get(0)) && isAtomicExpr((Expr)expr.members.get(2))))
			return true;
		
		if(expr.members.size()==1 && (expr.members.get(0) instanceof MethodCall)) {
			for(MethodParam param: ((MethodCall)expr.members.get(0)).params)
				if((param.val instanceof Expr) && !isAtomicExpr((Expr)param.val))
					return false;
			return true;
		}
		return false;
	}
	private int goExpr(Block blockpar, IR.Node parent, List<Statement> statements) {
		if(statements == null)
			return 0;
		int changes = 0;
		for(int i=0; i<statements.size(); i++) {
			Statement statement = statements.get(i);
			List<Statement> toAdd = new ArrayList<>(), postAdd = new ArrayList<>();
			if(statement instanceof AssignmentStatement) {
				AssignmentStatement AS = (AssignmentStatement)statement;
				Expr e = AS.assignExpr;
				if(e == null) {} //inc/dec
				else if(AS.loc instanceof LocationArray && !isAtomicExpr(((LocationArray)AS.loc).index)) { //LocationArray[non-atomic]
					LocationArray LA = (LocationArray)AS.loc;
					LocationNoArray t = exprToTempVar(blockpar, LA.index.getT(), LA.index);
					AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t, Op.Type.assign, LA.index);
					LA.index = new Expr(parent, parent.line, t);
					toAdd.add(a0);
				}
				//use else if because we don't want to process the right side until the left side is fully simplified (to avoid stuff like calling methods in an array index expression twice)
				else if(AS.op.type==Op.Type.plusequals || AS.op.type==Op.Type.minusequals) {
					if(!isAtomicExpr(e)) {
						Expr origExpr = new Expr(e); //deep copy (kind of)
						LocationNoArray t0 = exprToTempVar(blockpar, e.getT(), origExpr);
						AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t0, Op.Type.assign, origExpr);
						e.members.clear();
						e.members.add(t0);
						toAdd.add(a0);
					}
				}
				else if(e.members.size() == 3) {
					Expr child0 = (Expr)e.members.get(0);
					Op op = (Op)e.members.get(1);
					Expr child2 = (Expr)e.members.get(2);
					if(op.type == Op.Type.andand) {
						e.members = child0.members;
						IfStatement IF = new IfStatement(new Expr(parent, parent.line, AS.loc), false, 
										 new AssignmentStatement(parent, parent.line, AS.loc, Op.Type.assign, child2));
						postAdd.add(IF);
						while(goExpr(IF.block, IF.block, IF.block.statements) > 0) {}
					}
					else if(op.type == Op.Type.oror) {
						e.members = child0.members;
						IfStatement IF = new IfStatement(new Expr(parent, parent.line, AS.loc), true, 
										 new AssignmentStatement(parent, parent.line, AS.loc, Op.Type.assign, child2));
						postAdd.add(IF);
						while(goExpr(IF.block, IF.block, IF.block.statements) > 0) {}
					}
					else {
						if(!isAtomicExpr(child0)) {
							LocationNoArray t0 = exprToTempVar(blockpar, child0.getT(), child0);
							AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t0, Op.Type.assign, child0);
							e.members.set(0, new Expr(parent, parent.line, t0));
							toAdd.add(a0);
						}
						if(!isAtomicExpr(child2)) {
							LocationNoArray t2 = exprToTempVar(blockpar, child2.getT(), child2);
							AssignmentStatement a2 = new AssignmentStatement(parent, parent.line, t2, Op.Type.assign, child2);
							e.members.set(2, new Expr(parent, parent.line, t2));
							toAdd.add(a2);
						}
					}
				}
				else if(e.members.size() == 2) {
					Expr child1 = (Expr)e.members.get(1);
					if(!isAtomicExpr(child1)) {
						LocationNoArray t1 = exprToTempVar(blockpar, child1.getT(), child1);
						AssignmentStatement a1 = new AssignmentStatement(parent, parent.line, t1, Op.Type.assign, child1);
						e.members.set(1, new Expr(parent, parent.line, t1));
						toAdd.add(a1);
					}
				}
				else if(e.members.size()==1) {
					if(!isBasicExpr(e)) { 
						if(e.members.get(0) instanceof MethodCall) {
							MethodCall MC = (MethodCall)e.members.get(0);
							for(int j=MC.params.size()-1; j>=0; j--) {
								MethodParam param = MC.params.get(j);
								if(param.val instanceof Expr) {
									Expr e2 = (Expr)param.val;
									if(!isAtomicExpr(e2)) {
										LocationNoArray t = exprToTempVar(blockpar, e2.getT(), e2);
										AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t, Op.Type.assign, e2);
										param.val = new Expr(parent, parent.line, t);
										toAdd.add(a0);
									}
								}
							}
						}
						else { //LocationArray[non-atomic]
							LocationArray LA = (LocationArray)e.members.get(0);
							Expr e2 = LA.index;
							LocationNoArray t = exprToTempVar(blockpar, e2.getT(), e2);
							AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t, Op.Type.assign, e2);
							LA.index = new Expr(parent, parent.line, t);
							toAdd.add(a0);
						}
					}
				}
				else throw new IllegalStateException("Expr has bad size, size = " + e.members.size());
			}
			else if(statement instanceof MethodCall) {
				MethodCall MC = (MethodCall)statement;
				for(MethodParam param: MC.params){
					if(param.val instanceof Expr) {
						Expr e = (Expr)param.val;
						if(!isAtomicExpr(e)) {
							LocationNoArray t = exprToTempVar(blockpar, e.getT(), e);
							AssignmentStatement a0 = new AssignmentStatement(parent, parent.line, t, Op.Type.assign, e);
							param.val = new Expr(parent, parent.line, t);
							toAdd.add(a0);
						}
					}
				}
			}
			statements.addAll(i, toAdd);
			statements.addAll(i+toAdd.size()+1, postAdd);
			i += toAdd.size() + postAdd.size();
			changes += toAdd.size() + postAdd.size();
		}
		return changes;
	}
	private void processExprInBlock(Block block) {
		//extract exprs from statements
		for(int i=0; i<block.statements.size(); i++) {
			Statement st = block.statements.get(i);
			List<Statement> toAdd = new ArrayList<>();
			if(st instanceof IfStatement) {
				IfStatement IF = (IfStatement)st;
				if(!isAtomicExpr(IF.condition)) {
					LocationNoArray tempLoc = exprToTempVar(block, IF.condition.getT(), IF.condition);
					toAdd.add(new AssignmentStatement(block, block.line, tempLoc, Op.Type.assign, IF.condition));
					IF.condition = new Expr(block, block.line, tempLoc);
				}
			}
			else if(st instanceof ForStatement) {
				ForStatement FOR = (ForStatement)st;
				//pull the initial assignment out
				toAdd.add(new AssignmentStatement(FOR, FOR.line, FOR.initLoc, Op.Type.assign, FOR.initExpr));
				//NOTE: since initLoc and initExpr are null, we can't run semantics any more
				FOR.initLoc = null;
				FOR.initExpr = null;
				if(!isAtomicExpr(FOR.condition)) {
					LocationNoArray tempLoc = exprToTempVar(block, FOR.condition.getT(), FOR.condition);
					FOR.calcCondition = new ArrayList<>();
					FOR.calcCondition.add(new AssignmentStatement(block, block.line, tempLoc, Op.Type.assign, FOR.condition));
					FOR.condition = new Expr(block, block.line, tempLoc);
				}
				FOR.iterationStatements = new ArrayList<>();
				FOR.iterationStatements.add(FOR.iteration);
				FOR.iteration = null;
			}
			else if(st instanceof WhileStatement) {
				WhileStatement WHILE = (WhileStatement)st;
				if(!isAtomicExpr(WHILE.condition)) {
					LocationNoArray tempLoc = exprToTempVar(block, WHILE.condition.getT(), WHILE.condition);
					WHILE.calcCondition = new ArrayList<>();
					WHILE.calcCondition.add(new AssignmentStatement(block, block.line, tempLoc, Op.Type.assign, WHILE.condition));
					WHILE.condition = new Expr(block, block.line, tempLoc);
				}
			}
			else if(st instanceof ReturnStatement) {
				ReturnStatement RET = (ReturnStatement)st;
				if(RET.expr!=null && !isAtomicExpr(RET.expr)) {
					LocationNoArray tempLoc = exprToTempVar(block, RET.expr.getT(), RET.expr);
					toAdd.add(new AssignmentStatement(block, block.line, tempLoc, Op.Type.assign, RET.expr));
					RET.expr = new Expr(block, block.line, tempLoc);
				}
			}
			block.statements.addAll(i, toAdd);
			i += toAdd.size();
		}
		
		int changes = 1;
		while(changes > 0) {
			changes = 0;
			changes += goExpr(block, block, block.statements);
			for(int i=0; i<block.statements.size(); i++) {
				Statement st = block.statements.get(i);
				if(st instanceof ForStatement) {
					ForStatement FOR = (ForStatement)st;
					changes += goExpr(block, FOR, FOR.calcCondition);
					changes += goExpr(block, FOR, FOR.iterationStatements);
				}
				else if(st instanceof WhileStatement) {
					WhileStatement WHILE = (WhileStatement)st;
					changes += goExpr(block, WHILE, WHILE.calcCondition);
				}
			}
		}
	}
	/**
	 * Final guaranteed Expr format:
	 * atomic -> LocationNoArray | Literal | LocationArray[Literal]
	 * Expr -> (atomic Op atomic) | (Op atomic) | atomic | LocationArray[atomic] | method_call
	 * method_call -> MethodName(atomic*)
	 * If/For/While/Return expr -> atomic
	 * AssignmentStatement -> (atomic | LocationArray[atomic]) ((-- | ++) | ((+= | -=) atomic) | (= basic))
	 */
	private void postprocess() {
		List<Expr> exprs = new ArrayList<>();
		Queue<IR.Node> nodes = new ArrayDeque<>(); 
		nodes.add(root);
		while(!nodes.isEmpty()) {
			IR.Node _node = nodes.poll();
			if(_node instanceof Expr)
				exprs.add((Expr)_node);
			if(_node.getChildren() != null)
				for(IR.Node child: _node.getChildren())
					nodes.add(child);
		}
		Collections.reverse(exprs);
		for(Expr expr: exprs) {
			//turn expressions into trees that obey order of operations
			parseExpr(expr);
			//modify nesting so that expression trees obey the grammar and remove unnecessary parentheses
			fixNesting(expr);
		}
	}
	public void simplifyExpr() { //CALL AFTER SEMANTICS
		tempExpr = new HashMap<>();
		IRTraverser irTraverser = new IRTraverser(this);
		List<Block> blocks = new ArrayList<>();
		while(irTraverser.hasNext()) {
			IR.Node _node = irTraverser.getNext();
			if (_node instanceof Expr){
				Expr nodeExpr = (Expr) _node;
				for (int i = 0; i < nodeExpr.getChildren().size(); i++){
					Node child = nodeExpr.getChildren().get(i);
					if (child instanceof Len){
						Len nodeLen = (Len) child;
						FieldDeclArray nodeFieldDeclArray = (FieldDeclArray) nodeLen.symbolTable.find(nodeLen.ID);
						nodeExpr.members.set(i, new IntLiteral(nodeExpr, nodeExpr.line, nodeFieldDeclArray.length));
					}
				}
			}
			if(_node instanceof Block)
				blocks.add((Block)_node);
		}
		//do this later to avoid ConcurrentModificationException
		//chunk up expressions so that all expressions can be done in 1 assembly instruction
		//NOTE: IR.Node.parent is messed up after this
		//NOTE: Some LocationNoArrays are referenced multiple times after this
		for(Block block: blocks)
			processExprInBlock(block);
	}
}
