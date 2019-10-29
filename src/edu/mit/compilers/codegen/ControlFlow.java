package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.ControlFlow.CFBranch;
import edu.mit.compilers.semantics.IR;

public class ControlFlow {
	public IR ir;
	public Program program;
	public final static long wordSize = 8; //TODO: bools and ints are currently the same size
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
		public CFMethod code;
		public MethodSym(IR.IRType type, CFMethod code) {
			this.type = type;
			this.code = code;
		}
	}
	public class ImportMethodSym {}
	public Map<String, MethodSym> methods;
	public Map<String, ImportMethodSym> importMethods;
	public class Program extends CFPushScope {
		public Program(IR.Node _node) {
			super(null, _node.line);
			IR.Program node = (IR.Program)_node;
			for(IR.ImportDecl i: node.imports)
				if(i instanceof IR.ImportDecl)
					importMethods.put(i.name, new ImportMethodSym());
			super.addFields(node.fields);
			for(IR.MethodDecl i: node.methods)
				methods.put(i.ID, new MethodSym(i.type, new CFMethod(i)));
		}
	}
	public class CFMethod extends CFPushScope {
		public CFMethod(IR.Node _node) {
			super(null, _node.line);
			IR.MethodDecl node = (IR.MethodDecl)_node;
			for(IR.MethodDeclParam i: node.params) {
				variables.put(i.ID, new VarSym(i.type, 0, stackOffset));
				stackOffset += wordSize;
			}
			CFEndMethod end = new CFEndMethod(this, -1);
			next = makeBlock(node.block, this, end, null, null);
		}
	}
	public CFStatement makeBlock(IR.Block block, CFPushScope parentScope, CFStatement endBlock, CFStatement breakCFS, CFStatement continueCFS) {
		CFPushScope pushScope = new CFPushScope(parentScope, block.line);
		pushScope.addFields(block.fields);
		CFStatement start = pushScope;
		CFStatement cur = pushScope;
		for(IR.Statement i: block.statements) {
			if(i instanceof IR.AssignmentStatement) {
				cur.next = new CFAssignment(pushScope, i.line, (IR.AssignmentStatement)i);
				cur = cur.next;
			}
			else if(i instanceof IR.IfStatement) {
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				IR.IfStatement ifS = (IR.IfStatement)i;
				CFShortCircuit sC = new CFShortCircuit(pushScope, i.line);
				if(ifS.elseBlock == null)
					sC.start = shortCircuit(ifS.condition, pushScope, makeBlock(ifS.block, pushScope, end, breakCFS, continueCFS), end);
				else sC.start = shortCircuit(ifS.condition, pushScope, makeBlock(ifS.block, pushScope, end, breakCFS, continueCFS),
								makeBlock(ifS.elseBlock, pushScope, end, breakCFS, continueCFS));
				cur.next = sC;
				sC.next = end;
				cur = end;
			}
			else if(i instanceof IR.WhileStatement) {
				cur.next = new CFNop(pushScope, i.line);
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				IR.WhileStatement wS = (IR.WhileStatement)i;
				CFShortCircuit sC = new CFShortCircuit(pushScope, i.line);
				sC.start = shortCircuit(wS.condition, pushScope, makeBlock(wS.block, pushScope, cur, end, cur), end);
				cur.next = sC;
				sC.next = end;
				cur = end;
			}
			else if(i instanceof IR.ForStatement) {
				IR.ForStatement fS = (IR.ForStatement)i;
				cur.next = new CFAssignment(pushScope, i.line, fS.initLoc, fS.initExpr);
				cur = cur.next;
				cur.next = new CFNop(pushScope, i.line);
				cur = cur.next;
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				CFAssignment iteration = new CFForAssignment(pushScope, i.line, fS.iteration);
				iteration.next = cur;
				CFShortCircuit sC = new CFShortCircuit(pushScope, i.line);
				sC.start = shortCircuit(fS.condition, pushScope, makeBlock(fS.block, pushScope, iteration, end, iteration), end);
				cur.next = sC;
				sC.next = end;
				cur = end;
			}
			else if(i instanceof IR.BreakStatement) {
				cur.next = breakCFS;
				return start;
			}
			else if(i instanceof IR.ContinueStatement) {
				cur.next = continueCFS;
				return start;
			}
			else if(i instanceof IR.ReturnStatement) {
				cur.next = new CFReturn(pushScope, i.line, (IR.ReturnStatement)i);
				cur = cur.next;
				break;
			}
			else if(i instanceof IR.MethodCall) {
				cur.next = new CFMethodCall(pushScope, i.line, (IR.MethodCall)i);
				cur = cur.next;
			}
			else throw new IllegalStateException("Bad statement class " + i.getClass().getCanonicalName());
		}
		cur.next = new CFNop(pushScope, -1);
		cur.next.next = endBlock;
		return start;
	}
	public CFBranch shortCircuit(IR.Expr condition, CFPushScope scope, CFStatement ifTrue, CFStatement ifFalse) {
		CFBranch branch = new CFBranch(scope, condition.line, condition);
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
		public int line;
		public int orderpos;
		public CFPushScope scope;
		public CFStatement(CFPushScope scope, int line) {
			this.scope = scope;
			this.line = line;
			this.orderpos = -1;
		}
	}
	public class CFNop extends CFStatement {
		public CFNop(CFPushScope scope, int line) {
			super(scope, line);
		}
	}
	public class CFEndMethod extends CFNop {
		public CFEndMethod(CFPushScope scope, int line) {
			super(scope, line);
		}
	}
	public class CFMergeBranch extends CFNop {
		public CFMergeBranch(CFPushScope scope, int line) {
			super(scope, line);
		}
	}
	public class CFShortCircuit extends CFStatement {
		public CFStatement start;
		public CFShortCircuit(CFPushScope scope, int line) {
			super(scope, line);
		}
	}
	public class CFPushScope extends CFStatement {
		public Map<String, VarSym> variables;
		public long stackOffset;
		public CFPushScope parent;
		public int depth;
		public CFPushScope(CFPushScope parent, int line) {
			super(null, line);
			this.scope = this;
			this.parent = parent;
			if(parent == null)
				this.depth = 0;
			else this.depth = parent.depth+1;
			this.variables = new HashMap<>();
			this.stackOffset = 0;
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
	public class CFAssignment extends CFStatement {
		public IR.Location loc;
		public IR.Op op;
		public IR.Expr expr;
		public CFAssignment(CFPushScope scope, int line, IR.AssignmentStatement node) {
			super(scope, line);
			this.loc = node.loc;
			this.op = node.op;
			this.expr = node.assignExpr;
		}
		public CFAssignment(CFPushScope scope, int line, IR.Location loc, IR.Expr expr) {
			super(scope, line);
			this.loc = loc;
			this.op = new IR.Op(null, -1, IR.Op.Type.assign); //TODO: this has a null parent. is null parent ok? probably
			this.expr = expr;
		}
	}
	public class CFForAssignment extends CFAssignment {
		public CFForAssignment(CFPushScope scope, int line, IR.AssignmentStatement node) {
			super(scope, line, node);
		}
		public CFForAssignment(CFPushScope scope, int line, IR.Location loc, IR.Expr expr) {
			super(scope, line, loc, expr);
		}
	}
	public class CFBranch extends CFStatement {
		public IR.Expr condition;
		public CFStatement next2;
		public CFBranch(CFPushScope scope, int line, IR.Expr condition) {
			super(scope, line);
			this.condition = condition;
		}
	}
	public class CFReturn extends CFStatement {
		public IR.Expr expr;
		public CFReturn(CFPushScope scope, int line, IR.ReturnStatement node) {
			super(scope, line);
			this.expr = node.expr;
		}
	}
	public class CFMethodCall extends CFStatement {
		public String ID;
		public List<Object> params;
		public CFMethodCall(CFPushScope scope, int line, IR.MethodCall call) {
			super(scope, line);
			this.ID = call.ID;
			this.params = new ArrayList<>();
			for(IR.MethodParam i: call.params)
				this.params.add(i);
		}
	}
	private int tempExprCount;
	private Map<IR.Expr, String> tempExpr;
	private String getTempVar(IR.Expr node) {
		if(!tempExpr.containsKey(node)) {
			String name = "@temp" + tempExprCount;
			tempExpr.put(node, name);
			return name;
		}
		return tempExpr.get(node);
	}
	private CFStatement breakUpExprBool(CFStatement inCFS, IR.Expr expr) {
		CFStatement cur = new CFNop(inCFS.scope, inCFS.line);
		CFStatement start = cur;
		if(expr.members.size() == 2) {
			
		}
		return start;
	}
	private CFStatement breakUpCFSExpr(CFStatement CFS) {
		if(CFS instanceof CFBranch) {
			CFBranch CFB = (CFBranch)CFS;
			return breakUpExprBool(CFB, CFB.condition);
		}
		else if(CFS instanceof CFAssignment) {
			CFAssignment CFAS = (CFAssignment)CFS;
			
		}
		return CFS;
	}
	private void postprocessExpr(CFStatement CFS, CFStatement stop) {
		if(CFS==null || CFS==stop)
			return;
		
		else if(CFS instanceof CFShortCircuit) {
			CFShortCircuit CFSC = (CFShortCircuit)CFS;
			CFSC.start = breakUpCFSExpr(CFSC.start);
			CFSC.next = breakUpCFSExpr(CFSC.next);
			postprocessExpr(CFSC.start, CFSC.next);
			postprocessExpr(CFSC.next, stop);
		}
		else if(CFS instanceof CFBranch) {
			CFBranch CFB = (CFBranch)CFS;
			CFB.next = breakUpCFSExpr(CFB.next);
			CFB.next2 = breakUpCFSExpr(CFB.next2);
			postprocessExpr(CFB.next, stop);
			postprocessExpr(CFB.next2, stop);
		}
		else {
			CFS.next = breakUpCFSExpr(CFS.next);
			postprocessExpr(CFS.next, stop);
		}
	}
	public void build() {
		methods = new LinkedHashMap<>();
		importMethods = new LinkedHashMap<>();
		program = new Program(ir.root);
		tempExprCount = 0;
		
		tempExpr = new HashMap<>();
		for(Map.Entry<String, MethodSym> i: methods.entrySet())
			postprocessExpr(i.getValue().code, null);
	}
}