package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.semantics.IR;

public class ControlFlow {
	public IR ir;
	public CFProgram program;
	public final static long wordSize = 16; //TODO: bools and ints are currently the same size
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
	public class CFProgram extends CFPushScope {
		public CFProgram(IR.Node _node) {
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
			addFieldsMethodParams(node.params);
			CFEndMethod end;
			if(!node.ID.equals("main"))
				end = new CFEndMethod(this, -1, node.type==IR.IRType.void_? MethodEnd.nothing: MethodEnd.error);
			else end = new CFEndMethod(this, -1, MethodEnd.main);
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
				IR.IfStatement IF = (IR.IfStatement)i;
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				CFContainer box = new CFContainer(pushScope, i.line);
				if(IF.elseBlock == null)
					box.start = shortCircuit(IF.condition, pushScope, makeBlock(IF.block, pushScope, end, breakCFS, continueCFS), end);
				else box.start = shortCircuit(IF.condition, pushScope, makeBlock(IF.block, pushScope, end, breakCFS, continueCFS),
								makeBlock(IF.elseBlock, pushScope, end, breakCFS, continueCFS));
				cur.next = box;
				box.next = end;
				cur = end;
			}
			else if(i instanceof IR.WhileStatement) {
				IR.WhileStatement WHILE = (IR.WhileStatement)i;
				cur.next = new CFNop(pushScope, i.line);
				cur = cur.next;
				CFStatement loopBackTo = cur;
				if(WHILE.calcCondition != null) {
					for(IR.Statement statement: WHILE.calcCondition) {
						IR.AssignmentStatement AS = (IR.AssignmentStatement)statement;
						cur.next = new CFAssignment(pushScope, AS.line, AS);
						cur = cur.next;
					}
				}
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				CFContainer box = new CFContainer(pushScope, i.line);
				box.start = shortCircuit(WHILE.condition, pushScope, makeBlock(WHILE.block, pushScope, loopBackTo, end, loopBackTo), end);
				cur.next = box;
				box.next = end;
				cur = end;
			}
			else if(i instanceof IR.ForStatement) {
				IR.ForStatement FOR = (IR.ForStatement)i;
				//we already pull out the initial for assignment while postprocessing the IR
				/*cur.next = new CFAssignment(pushScope, i.line, FOR.initLoc, FOR.initExpr);
				cur = cur.next;*/
				cur.next = new CFNop(pushScope, i.line);
				cur = cur.next;
				CFStatement loopBackTo = cur;
				if(FOR.calcCondition != null) {
					for(IR.Statement statement: FOR.calcCondition) {
						IR.AssignmentStatement AS = (IR.AssignmentStatement)statement;
						cur.next = new CFAssignment(pushScope, AS.line, AS);
						cur = cur.next;
					}
				}
				CFMergeBranch end = new CFMergeBranch(pushScope, i.line);
				CFAssignment iteration = new CFForAssignment(pushScope, i.line, FOR.iteration);
				iteration.next = loopBackTo;
				CFContainer box = new CFContainer(pushScope, i.line);
				box.start = shortCircuit(FOR.condition, pushScope, makeBlock(FOR.block, pushScope, iteration, end, iteration), end);
				cur.next = box;
				box.next = end;
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
		cur.next = endBlock;
		return start;
	}
	public CFBranch shortCircuit(IR.Expr condition, CFPushScope scope, CFStatement ifTrue, CFStatement ifFalse) {
		CFBranch branch = new CFBranch(scope, condition.line, condition);
		if(condition.members.size() == 1) {
			branch.next = ifTrue;
			branch.next2 = ifFalse;
		}
		/*else if(condition.members.size() == 2) {
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
		}*/
		else throw new IllegalStateException("Expr with wrong number of children: count = " + condition.members.size());
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
	public enum MethodEnd {
		nothing, error, main;
	}
	public class CFEndMethod extends CFNop {
		public MethodEnd end;
		public CFEndMethod(CFPushScope scope, int line, MethodEnd end) {
			super(scope, line);
			this.end = end;
		}
	}
	public class CFMergeBranch extends CFNop {
		public CFMergeBranch(CFPushScope scope, int line) {
			super(scope, line);
		}
	}
	public class CFContainer extends CFStatement {
		public CFStatement start;
		public CFContainer(CFPushScope scope, int line) {
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
		public void addFieldsMethodParams(List<IR.MethodDeclParam> params) {
			for(IR.MethodDeclParam i: params) {
				variables.put(i.ID, new VarSym(i.type, 0, stackOffset));
				stackOffset += wordSize;
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
		public IR.MethodCall call;
		public CFMethodCall(CFPushScope scope, int line, IR.MethodCall call) {
			super(scope, line);
			this.call = call;
		}
	}
	public void build() {
		methods = new LinkedHashMap<>();
		importMethods = new LinkedHashMap<>();
		program = new CFProgram(ir.root);
	}
}