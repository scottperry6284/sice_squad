package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.semantics.IR;

public class ControlFlow {
	public IR ir;
	public Node root;
	public ControlFlow(IR ir) {
		this.ir = ir;
	}
	public class VarSym {
		public IR.IRType type;
		long len; //0 = not array
		public VarSym(IR.IRType type, long len) {
			this.type = type;
			this.len = len;
		}
		public boolean isArray() {
			return len != 0;
		}
	}
	public class MethodSym {
		public IR.IRType type;
		public Node code;
		public MethodSym(IR.IRType type, Node code) {
			this.type = type;
			this.code = code;
		}
	}
	public class ImportMethodSym {}
	public Map<String, MethodSym> methods;
	public Map<String, ImportMethodSym> importMethods;
	public class Node {
		public Node parent;
		CFStatement statement;
		public Map<String, VarSym> variables;
		public Node(ControlFlow.Node parent) {
			this.parent = parent;
			variables = new HashMap<>();
			statement = null;
		}
	}
	public class Program extends Node {
		public Program(IR.Node _node) {
			super(null);
			IR.Program node = (IR.Program)_node;
			for(IR.ImportDecl i: node.imports) {
				if(i instanceof IR.ImportDecl) {
					importMethods.put(i.name, new ImportMethodSym());
				}
			}
			for(IR.FieldDecl i: node.fields) {
				if(i instanceof IR.FieldDeclArray) {
					IR.FieldDeclArray x = (IR.FieldDeclArray)i;
					variables.put(x.ID, new VarSym(x.type, x.length));
				}
				else variables.put(i.ID, new VarSym(i.type, 0));
			}
			for(IR.MethodDecl i: node.methods) {
				Node code = new Method(this, i);
				methods.put(i.ID, new MethodSym(i.type, code));
			}
		}
	}
	public class Method extends Node {
		public Method(ControlFlow.Node parent, IR.Node _node) {
			super(parent);
			IR.MethodDecl node = (IR.MethodDecl)_node;
			for(IR.FieldDecl i: node.block.fields) {
				if(i instanceof IR.FieldDeclArray) {
					IR.FieldDeclArray x = (IR.FieldDeclArray)i;
					variables.put(x.ID, new VarSym(x.type, x.length));
				}
				else variables.put(i.ID, new VarSym(i.type, 0));
			}
			for(IR.MethodDeclParam i: node.params) {
				variables.put(i.ID, new VarSym(i.type, 0));
			}
			statement = makeBlock(node.block, new CFNop());
		}
	}
	public CFStatement makeBlock(IR.Block block, CFStatement endBlock) {
		CFStatement cur = new CFNop();
		CFStatement start = cur;
		for(IR.Statement i: block.statements) {
			if(i instanceof IR.AssignmentStatement) {
				cur = new CFAssignment((IR.AssignmentStatement)i);
				cur = cur.next;
			}
			else if(i instanceof IR.IfStatement) {
				CFNop end = new CFNop();
				IR.IfStatement ifS = (IR.IfStatement)i;
				if(ifS.elseBlock == null)
					cur.next = shortCircuit(ifS.condition, makeBlock(ifS.block, end), end);
				else cur.next = shortCircuit(ifS.condition, makeBlock(ifS.block, end), makeBlock(ifS.elseBlock, end));
				cur = end;
			}
			else if(i instanceof IR.WhileStatement) {
				cur.next = new CFNop();
				cur = cur.next;
				CFNop end = new CFNop();
				IR.WhileStatement wS = (IR.WhileStatement)i;
				CFBranch whileBranch = shortCircuit(wS.condition, makeBlock(wS.block, cur), end);
				cur.next = whileBranch;
				cur = end;
			}
			else if(i instanceof IR.BreakStatement) {
				break;
			}
			else if(i instanceof IR.ContinueStatement) {
				cur.next = start; //TODO: I think this works
			}
			else if(i instanceof IR.ReturnStatement) {
				
			}
			else if(i instanceof IR.MethodCall) {
				
			}
			else throw new IllegalStateException("Bad statement class " + i.getClass().getCanonicalName());
		}
		cur.next = endBlock;
		return start;
	}
	public CFBranch shortCircuit(IR.Expr condition, CFStatement ifTrue, CFStatement ifFalse) {
		CFBranch branch = new CFBranch(condition);
		if(condition.members.size() == 1) {
			branch.next = ifTrue;
			branch.next2 = ifFalse;
		}
		else if(condition.members.size() == 2) {
			IR.Node node = condition.members.get(0);
			if(node instanceof IR.Op) {
				IR.Op op = (IR.Op)node;
				if(op.type != IR.Op.Type.not)
					throw new IllegalStateException("Expected Op of type not");
				branch = shortCircuit((IR.Expr)condition.members.get(1), ifFalse, ifTrue); //guaranteed to be IR.Expr by grammar (I think)
			}
			else throw new IllegalStateException("Unrecognized Op");
		}
		else if(condition.members.size() == 3) {
			IR.Node nOp = condition.members.get(1);
			if(nOp instanceof IR.Op) {
				IR.Op op = (IR.Op)nOp;
				if(op.type == IR.Op.Type.andand)
					branch = shortCircuit((IR.Expr)condition.members.get(0),
							 shortCircuit((IR.Expr)condition.members.get(2), ifTrue, ifFalse), ifFalse);
				else if(op.type == IR.Op.Type.oror)
					branch = shortCircuit((IR.Expr)condition.members.get(0),
							 ifTrue, shortCircuit((IR.Expr)condition.members.get(2), ifTrue, ifFalse));
				else {
					branch.next = ifTrue;
					branch.next2 = ifFalse;
				}
			}
			else throw new IllegalStateException("Unrecognized Op");
		}
		else throw new IllegalStateException("Expr with too many children: count = " + condition.members.size());
		return branch;
	}
	public abstract class CFStatement {
		public IR.Expr condition;
		public CFStatement next, next2;
		public CFStatement() {}
	}
	public class CFNop extends CFStatement {
		
	}
	public class CFAssignment extends CFStatement {
		public CFAssignment(IR.AssignmentStatement node) {
			
		}
	}
	public class CFBranch extends CFStatement {
		public CFBranch(IR.Expr condition) {
			this.condition = condition;
		}
	}
	public void build() {
		methods = new HashMap<>();
		importMethods = new HashMap<>();
		root = new Program(ir.root);
	}
}