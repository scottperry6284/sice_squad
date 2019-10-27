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
			super(0);
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
			super(1);
			IR.MethodDecl node = (IR.MethodDecl)_node;
			for(IR.MethodDeclParam i: node.params) {
				variables.put(i.ID, new VarSym(i.type, 0, stackOffset));
				stackOffset += wordSize;
			}
			end = new CFEndMethod(1);
			next = makeBlock(node.block, 2, end, null, null);
		}
	}
	public CFStatement makeBlock(IR.Block block, int scope, CFStatement endBlock, CFStatement breakCFS, CFStatement continueCFS) {
		CFPushScope pushScope = new CFPushScope(scope);
		CFStatement start = pushScope;
		pushScope.addFields(block.fields);
		CFStatement cur = pushScope;
		for(IR.Statement i: block.statements) {
			if(i instanceof IR.AssignmentStatement) {
				cur.next = new CFAssignment(scope, (IR.AssignmentStatement)i);
				cur = cur.next;
			}
			else if(i instanceof IR.IfStatement) {
				CFMergeBranch end = new CFMergeBranch(scope);
				IR.IfStatement ifS = (IR.IfStatement)i;
				CFShortCircuit sC = new CFShortCircuit(scope);
				if(ifS.elseBlock == null)
					sC.start = shortCircuit(ifS.condition, scope, makeBlock(ifS.block, scope+1, end, breakCFS, continueCFS), end);
				else sC.start = shortCircuit(ifS.condition, scope, makeBlock(ifS.block, scope+1, end, breakCFS, continueCFS),
								makeBlock(ifS.elseBlock, scope+1, end, breakCFS, continueCFS));
				cur.next = sC;
				cur = end;
			}
			else if(i instanceof IR.WhileStatement) {
				cur.next = new CFNop(scope);
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch(scope);
				IR.WhileStatement wS = (IR.WhileStatement)i;
				CFShortCircuit sC = new CFShortCircuit(scope);
				sC.start = shortCircuit(wS.condition, scope, makeBlock(wS.block, scope+1, cur, end, cur), end);
				cur.next = sC;
				cur = end;
			}
			else if(i instanceof IR.ForStatement) {
				IR.ForStatement fS = (IR.ForStatement)i;
				cur.next = new CFAssignment(scope, fS.initLoc, fS.initExpr);
				cur = cur.next;
				cur.next = new CFNop(scope);
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch(scope);
				CFAssignment iteration = new CFForAssignment(scope+1, fS.iteration);
				iteration.next = cur;
				CFShortCircuit sC = new CFShortCircuit(scope);
				sC.start = shortCircuit(fS.condition, scope, makeBlock(fS.block, scope+1, iteration, end, iteration), end);
				cur.next = sC;
				cur = end;
			}
			else if(i instanceof IR.BreakStatement) {
				pushScope.end = cur.next = new CFPopScope(scope);
				cur = cur.next;
				int parentScope = 2;
				if(breakCFS != null)
					parentScope = breakCFS.scope;
				for(int j=scope-1; j>parentScope; j--) {
					cur.next = new CFPopScope(scope);
					cur = cur.next;
				}
				cur.next = endBlock;
				return start;
			}
			else if(i instanceof IR.ContinueStatement) {
				pushScope.end = cur.next = new CFPopScope(scope);
				cur = cur.next;
				int parentScope = 2;
				if(continueCFS != null)
					parentScope = continueCFS.scope;
				for(int j=scope-1; j>parentScope; j--) {
					cur.next = new CFPopScope(scope);
					cur = cur.next;
				}
				cur.next = endBlock;
				return start;
			}
			else if(i instanceof IR.ReturnStatement) {
				cur.next = new CFReturn(scope, (IR.ReturnStatement)i);
				cur = cur.next;
				break;
			}
			else if(i instanceof IR.MethodCall) {
				cur.next = new CFMethodCall(scope, (IR.MethodCall)i);
				cur = cur.next;
			}
			else throw new IllegalStateException("Bad statement class " + i.getClass().getCanonicalName());
		}
		pushScope.end = cur.next = new CFPopScope(scope);
		cur.next.next = endBlock;
		return start;
	}
	public CFBranch shortCircuit(IR.Expr condition, int scope, CFStatement ifTrue, CFStatement ifFalse) {
		CFBranch branch = new CFBranch(scope, condition);
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
				branch = shortCircuit((IR.Expr)condition.members.get(1), scope, ifFalse, ifTrue); //guaranteed to be IR.Expr by grammar (I think)
			}
			else throw new IllegalStateException("Unrecognized Op");
		}
		else if(condition.members.size() == 3) {
			IR.Node nOp = condition.members.get(1);
			if(nOp instanceof IR.Op) {
				IR.Op op = (IR.Op)nOp;
				if(op.type == IR.Op.Type.andand)
					branch = shortCircuit((IR.Expr)condition.members.get(0), scope,
							 shortCircuit((IR.Expr)condition.members.get(2), scope, ifTrue, ifFalse), ifFalse);
				else if(op.type == IR.Op.Type.oror)
					branch = shortCircuit((IR.Expr)condition.members.get(0), scope,
							 ifTrue, shortCircuit((IR.Expr)condition.members.get(2), scope, ifTrue, ifFalse));
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
		public CFStatement next;
		public int scope;
		public CFStatement(int scope) {
			this.scope = scope;
		}
	}
	public class CFNop extends CFStatement {
		public CFNop(int scope) {
			super(scope);
		}
	}
	public class CFMergeBranch extends CFNop {
		public CFMergeBranch(int scope) {
			super(scope);
		}
	}
	public class CFShortCircuit extends CFStatement {
		public CFStatement start;
		public CFShortCircuit(int scope) {
			super(scope);
		}
	}
	public class CFPushScope extends CFStatement {
		public CFPushScope parent;
		public CFStatement end;
		public Map<String, VarSym> variables;
		long stackOffset;
		public CFPushScope(int scope) {
			super(scope);
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
		public CFPopScope(int scope) {
			super(scope);
		}
	}
	public class CFEndMethod extends CFPopScope {
		public CFEndMethod(int scope) {
			super(scope);
		}
	}
	public class CFAssignment extends CFStatement {
		public IR.Location loc;
		public IR.Op op;
		public IR.Expr expr;
		public CFAssignment(int scope, IR.AssignmentStatement node) {
			super(scope);
			this.loc = node.loc;
			this.op = node.op;
			this.expr = node.assignExpr;
		}
		public CFAssignment(int scope, IR.Location loc, IR.Expr expr) {
			super(scope);
			this.loc = loc;
			this.op = new IR.Op(null, -1, IR.Op.Type.assign); //TODO: this has a null parent. is null parent ok? probably
			this.expr = expr;
		}
	}
	public class CFForAssignment extends CFAssignment {
		public CFForAssignment(int scope, IR.AssignmentStatement node) {
			super(scope, node);
		}
		public CFForAssignment(int scope, IR.Location loc, IR.Expr expr) {
			super(scope, loc, expr);
		}
	}
	public class CFBranch extends CFStatement {
		public IR.Expr condition;
		public CFStatement next2;
		public CFBranch(int scope, IR.Expr condition) {
			super(scope);
			this.condition = condition;
		}
	}
	public class CFReturn extends CFStatement {
		public IR.Expr expr;
		public CFReturn(int scope, IR.ReturnStatement node) {
			super(scope);
			this.expr = node.expr;
		}
	}
	public class CFMethodCall extends CFStatement {
		public String ID;
		public List<Object> params;
		public CFMethodCall(int scope, IR.MethodCall call) {
			super(scope);
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