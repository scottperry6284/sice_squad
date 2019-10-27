package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.semantics.IR;

public class ControlFlow {
	public IR ir;
	public Program program;
	private final long wordSize = 8; //TODO: bools and ints are currently the same size
	private int tempExprCnt;
	public ControlFlow(IR ir) {
		this.ir = ir;
	}
	public class VarSym {
		public IR.IRType type;
		long len; //0 = not array
		public long stackOffset;
		public VarSym(IR.IRType type, long len, long stackOffset) {
			this.type = type;
			this.len = len;
			this.stackOffset = stackOffset;
		}
		public boolean isArray() {
			return len != 0;
		}
	}
	public class MethodSym {
		public IR.IRType type;
		public CFStatement code;
		public MethodSym(IR.IRType type, CFStatement code) {
			this.type = type;
			this.code = code;
		}
	}
	public class ImportMethodSym {}
	public Map<String, MethodSym> methods;
	public Map<String, ImportMethodSym> importMethods;
	public class Program extends CFPushScope {
		public Program(IR.Node _node) {
			IR.Program node = (IR.Program)_node;
			for(IR.ImportDecl i: node.imports)
				if(i instanceof IR.ImportDecl)
					importMethods.put(i.name, new ImportMethodSym());
			super.addFields(node.fields);
			for(IR.MethodDecl i: node.methods)
				methods.put(i.ID, new MethodSym(i.type, new Method(i)));
		}
	}
	public class Method extends CFPushScope {
		public Method(IR.Node _node) {
			IR.MethodDecl node = (IR.MethodDecl)_node;
			for(IR.MethodDeclParam i: node.params) {
				variables.put(i.ID, new VarSym(i.type, 0, stackOffset));
				stackOffset += wordSize;
			}
			end = new CFEndMethod();
			next = makeBlock(node.block, end);
		}
	}
	public CFStatement makeBlock(IR.Block block, CFStatement endBlock) {
		CFPushScope pushScope = new CFPushScope();
		CFStatement start = pushScope;
		pushScope.addFields(block.fields);
		CFStatement cur = pushScope;
		for(IR.Statement i: block.statements) {
			if(i instanceof IR.AssignmentStatement) {
				cur = new CFAssignment((IR.AssignmentStatement)i);
				cur = cur.next;
			}
			else if(i instanceof IR.IfStatement) {
				CFMergeBranch end = new CFMergeBranch();
				IR.IfStatement ifS = (IR.IfStatement)i;
				CFShortCircuit sC = new CFShortCircuit();
				if(ifS.elseBlock == null)
					sC.start = shortCircuit(ifS.condition, makeBlock(ifS.block, end), end);
				else sC.start = shortCircuit(ifS.condition, makeBlock(ifS.block, end), makeBlock(ifS.elseBlock, end));
				cur.next = sC;
				cur = end;
			}
			else if(i instanceof IR.WhileStatement) {
				cur.next = new CFNop();
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch();
				IR.WhileStatement wS = (IR.WhileStatement)i;
				CFBranch whileBranch = shortCircuit(wS.condition, makeBlock(wS.block, cur), end);
				cur.next = whileBranch;
				cur = end;
			}
			else if(i instanceof IR.ForStatement) {
				IR.ForStatement fS = (IR.ForStatement)i;
				cur.next = new CFAssignment(fS.initLoc, fS.initExpr);
				cur = cur.next;
				cur.next = new CFNop();
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch();
				CFAssignment iteration = new CFAssignment(fS.iteration);
				iteration.next = end;
				CFBranch forBranch = shortCircuit(fS.condition, makeBlock(fS.block, iteration), end);
				cur.next = forBranch;
				cur = end;
			}
			else if(i instanceof IR.BreakStatement) {
				break; //exit the current scope
			}
			else if(i instanceof IR.ContinueStatement) {
				cur.next = start; //TODO: I think this works
			}
			else if(i instanceof IR.ReturnStatement) {
				cur.next = new CFReturn((IR.ReturnStatement)i);
				cur = cur.next;
				break;
			}
			else if(i instanceof IR.MethodCall) {
				cur.next = new CFMethodCall((IR.MethodCall)i);
				cur = cur.next;
			}
			else throw new IllegalStateException("Bad statement class " + i.getClass().getCanonicalName());
		}
		pushScope.end = cur.next = new CFPopScope();
		cur.next.next = endBlock;
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
	public class CFMergeBranch extends CFNop {
		
	}
	public class CFShortCircuit extends CFStatement {
		public CFStatement start;
	}
	public class CFPushScope extends CFStatement {
		public CFPushScope parent;
		public CFStatement end;
		public Map<String, VarSym> variables;
		long stackOffset;
		public CFPushScope() {
			variables = new HashMap<>();
			stackOffset = 0;
		}
		//note that stackOffset isn't actually used for global variables
		public void addFields(List<IR.FieldDecl> fields) {
			for(IR.FieldDecl i: fields) {
				if(i instanceof IR.FieldDeclArray) {
					IR.FieldDeclArray x = (IR.FieldDeclArray)i;
					variables.put(x.ID, new VarSym(x.type, x.length, stackOffset));
					stackOffset += wordSize * x.length;
				}
				else {
					variables.put(i.ID, new VarSym(i.type, 0, stackOffset));
					stackOffset += wordSize;
				}
			}
		}
	}
	public class CFPopScope extends CFStatement {
		
	}
	public class CFEndMethod extends CFPopScope {
		
	}
	public class CFAssignment extends CFStatement {
		public IR.Location loc;
		public IR.Op op;
		public IR.Expr expr;
		public CFAssignment(IR.AssignmentStatement node) {
			loc = node.loc;
			op = node.op;
			expr = node.assignExpr;
		}
		public CFAssignment(IR.Location loc, IR.Expr expr) {
			this.loc = loc;
			this.op = new IR.Op(null, IR.Op.Type.assign); //TODO: this has a null parent. is null parent ok? probably
			this.expr = expr;
		}
	}
	public class CFBranch extends CFStatement {
		public CFBranch(IR.Expr condition) {
			this.condition = condition;
		}
	}
	public class CFReturn extends CFStatement {
		public IR.Expr expr;
		public CFReturn(IR.ReturnStatement node) {
			this.expr = node.expr;
		}
	}
	public class CFMethodCall extends CFStatement {
		public String ID;
		public List<Object> params;
		public CFMethodCall(IR.MethodCall call) {
			this.ID = call.ID;
			this.params = new ArrayList<>();
			for(IR.MethodParam i: call.params)
				this.params.add(i);
		}
	}
	public void build() {
		methods = new HashMap<>();
		importMethods = new HashMap<>();
		tempExprCnt = 0;
		program = new Program(ir.root);
	}
}