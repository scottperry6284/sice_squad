package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.ControlFlow.CFAssignment;
import edu.mit.compilers.codegen.ControlFlow.CFBranch;
import edu.mit.compilers.codegen.ControlFlow.CFEndMethod;
import edu.mit.compilers.codegen.ControlFlow.CFMergeBranch;
import edu.mit.compilers.codegen.ControlFlow.CFMethod;
import edu.mit.compilers.codegen.ControlFlow.CFMethodCall;
import edu.mit.compilers.codegen.ControlFlow.CFNop;
import edu.mit.compilers.codegen.ControlFlow.CFPushScope;
import edu.mit.compilers.codegen.ControlFlow.CFReturn;
import edu.mit.compilers.codegen.ControlFlow.CFContainer;
import edu.mit.compilers.codegen.ControlFlow.CFStatement;
import edu.mit.compilers.codegen.ControlFlow.MethodEnd;
import edu.mit.compilers.semantics.IR;
import edu.mit.compilers.semantics.IR.Expr;

public class Codegen {
	//TODO: there could be name conflicts if something is called "label" or "globalvar"
	public ControlFlow CF;
	public List<Asm> asmOutput;
	public Map<String, String> addedStrings; 
	
	public static class Asm {
		public enum Op { //newline is just whitespace for formatting
			methodlabel, label, pushq, movq, popq, ret, custom, newline, xor, call, cqto,
			jz, jnz, test, inc, dec, cmp, jmp, not, neg, string, align, imul, idiv, and, or, leaq,
			sete, setne, setge, setle, setg, setl, jne, mov, addq, subq, andq, orq, movzx, shr, shl;
		}
		public Op op;
		public String arg1, arg2;
		public Asm(Op op) {
			this.op = op;
		}
		public Asm(Op op, String arg1) {
			this.op = op;
			this.arg1 = arg1;
		}
		public Asm(Op op, String arg1, String arg2) {
			this.op = op;
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
		public String toString() {
			if(op == Op.custom)
				return arg1;
			if(op == Op.label || op == Op.methodlabel)
				return arg1 + ":";
			if (op == Op.string || op == Op.align)
				return "." + op.name() + " " + arg1;
			if (op == Op.leaq)
				return op.name() + " " + arg1 + "(%rip)," + " " + arg2;
			if(op == Op.newline)
				return "";
			if(arg1 == null)
				return op.name();
			if(arg2 == null)
				return op.name() + " " + arg1;
			else return op.name() + " " + arg1 + ", " + arg2;
		}
	}
	public Codegen(ControlFlow CF) {
		this.CF = CF;
	}
	private long labelCount;
	private Map<CFStatement, String> labels;
	private String getLabel(CFStatement CFS) {
		if(!labels.containsKey(CFS)) {
			String label = "label" + labelCount;
			labels.put(CFS, label);
			labelCount++;
			return label;
		}
		return labels.get(CFS);
	}
	private String getAddedStringLabel(String s) {
		if(!addedStrings.containsKey(s)) {
			String name = "format_str_" + addedStrings.size();
			addedStrings.put(s, name);
			return name;
		}
		return addedStrings.get(s);
	}
	private void processImportCallParam(IR.MethodCall call, CFPushScope scope, int i, long additionalOffset) {
		final String[] CCallRegs = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
		
		Object param = call.params.get(i).val;

		// Put all of the strings in a separate list.
		if (param instanceof String) {

			String paramCast = (String) param;
	
			String strLabel = getAddedStringLabel(paramCast);

			if (i<=5) {
				asmOutput.add(new Asm(Asm.Op.leaq, strLabel, CCallRegs[i]));

			} else {
				// If more than 6 args put into r10 and then push to stack.
				asmOutput.add(new Asm(Asm.Op.leaq, paramCast, "%r10"));
				asmOutput.add(new Asm(Asm.Op.pushq, "%r10"));
			}

		} else {
			IR.Node paramCast = ((Expr) param).members.get(0);
	
			if (paramCast instanceof IR.LocationNoArray) {

				IR.LocationNoArray paramCastLocationNoArray = (IR.LocationNoArray) paramCast;
				
				String varVal = getVarLoc(paramCastLocationNoArray, scope, additionalOffset);

				if (i<=5) {
					asmOutput.add(new Asm(Asm.Op.movq, varVal, CCallRegs[i]));
				} else {
					asmOutput.add(new Asm(Asm.Op.pushq, varVal));
				}
	
			}
			else if (paramCast instanceof IR.Literal) {
				IR.Literal paramCastInt = (IR.Literal) paramCast;

				if (i<=5) {
					asmOutput.add(new Asm(Asm.Op.movq, "$" + paramCastInt.val(), CCallRegs[i]));
				} else {
					asmOutput.add(new Asm(Asm.Op.pushq, "$" + paramCastInt.val()));
				}
			}
			else throw new IllegalStateException("Bad IR.Node type: " + paramCast.getClass().getSimpleName());
		}
	}
	private void executeMethod(IR.MethodCall call, CFPushScope scope) {
		boolean importMethod = CF.importMethods.containsKey(call.ID);
		if(!importMethod) {
			if(call.params.size() > 0) {
				long stackPos = -(call.params.size()+2) * ControlFlow.wordSize;
				for(IR.MethodParam i: call.params) {
					IR.Node child0 = ((IR.Expr)i.val).members.get(0);
					if(child0 instanceof IR.LocationNoArray) {
						asmOutput.add(new Asm(Asm.Op.movq, getVarLoc((IR.LocationNoArray)child0, scope), "%rdi"));
						asmOutput.add(new Asm(Asm.Op.movq, "%rdi", stackPos + "(%rsp)"));
					}
					else if(child0 instanceof IR.Literal)
						asmOutput.add(new Asm(Asm.Op.movq, "$" + ((IR.Literal)child0).val(), stackPos + "(%rsp)"));
					else throw new IllegalArgumentException("Bad child0 type: " + child0.getClass().getSimpleName());
					stackPos += ControlFlow.wordSize;
				}
			}
			asmOutput.add(new Asm(Asm.Op.call, call.ID));
			return;
		}
		
		for (int i = 0; i < Math.min(6, call.params.size()); i++) {
			processImportCallParam(call, scope, i, 0);
		}
		asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rax")); //does this help? doesn't seem so
		
		//save %rsp in callee saved register
		asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%r12"));
		stackAddress = "%r12"; //let %r8 be the stack pointer for now
		
		//make divisible by 16
		asmOutput.add(new Asm(Asm.Op.pushq, "$0"));
		asmOutput.add(new Asm(Asm.Op.shr, "$4", "%rsp"));
		asmOutput.add(new Asm(Asm.Op.shl, "$4", "%rsp"));
		
		//push arguments on stack in reverse for import statements when >6 parameters
		if(call.params.size() > 6) {
			//ensure 16 alignment
			if(call.params.size() % 2 == 1)
				asmOutput.add(new Asm(Asm.Op.pushq, "$0"));
			//push arguments
			for(int i=call.params.size()-1; i>=6; i--)
				processImportCallParam(call, scope, i, 0); //4th arg deprecated?
		}
		
		// Make method call.
		asmOutput.add(new Asm(Asm.Op.call, call.ID));
		
		//restore %rsp
		asmOutput.add(new Asm(Asm.Op.movq, "%r12", "%rsp"));
		stackAddress = "%rsp";
	}
	private int vLocCount = 0;
	String stackAddress = "%rsp";
	private String getVarLoc(IR.Location loc, CFPushScope scope) {
		return getVarLoc(loc, scope, 0);
	}
	private String getVarLoc(IR.Location loc, CFPushScope scope, long additionalOffset) {
		vLocCount++;
		String name = loc.ID;
		long stackOffset = additionalOffset, arrayOffset = 0;
		String reg = null;
		if(loc instanceof IR.LocationArray) {
			IR.Node idx = ((IR.LocationArray)(loc)).index.members.get(0);
			if(idx instanceof IR.Literal)
				arrayOffset = ((IR.Literal)idx).val() * ControlFlow.wordSize;
			else {
				reg = vLocCount%2==0? "%rbx": "%rcx";
				asmOutput.add(new Asm(Asm.Op.movq, getVarLoc((IR.Location)idx, scope), reg));
			}
		}
		//for variable indices, do other stuff
		while(scope != null) {
			if(scope.variables.containsKey(name)) {
				if(reg == null)
					return (stackOffset + arrayOffset + scope.variables.get(name).stackOffset) + "(" + stackAddress + ")";
				else return (stackOffset + arrayOffset + scope.variables.get(name).stackOffset) + "(" + stackAddress + ", " + reg + ", " + ControlFlow.wordSize + ")";
			}
			if(scope.stackOffset != 0)
				stackOffset += scope.stackOffset + ControlFlow.wordSize; //+wordSize because we push rbp and a filler 8 if stackOffset!=0
			scope = scope.parent;
		}
		//it's in the global scope
		if(!CF.program.variables.containsKey(name))
			throw new IllegalStateException("Variable with name \"" + name + "\" not found in any scope");
		if(reg == null)
			return (arrayOffset + CF.program.variables.get(name).stackOffset) + " + globalvar";
		else {
			asmOutput.add(new Asm(Asm.Op.imul, "$" + ControlFlow.wordSize, reg));
			asmOutput.add(new Asm(Asm.Op.addq, "$" + CF.program.variables.get(name).stackOffset, reg));
			return "globalvar(" + reg + ")";
		}
	}
	private void pushScope(long size, boolean areMethodParams) {
		asmOutput.add(new Asm(Asm.Op.pushq, "%rbp"));
		asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%rbp"));
		if(areMethodParams) //don't modify params
			asmOutput.add(new Asm(Asm.Op.subq, "$" + size, "%rsp"));
		else for(long i=0; i<size; i+=8)
			asmOutput.add(new Asm(Asm.Op.pushq, "$0")); //fields should be initialized to 0
	}
	private void popScope(long size) {
		asmOutput.add(new Asm(Asm.Op.addq, "$" + size, "%rsp"));
		asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
		asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
	}
	private void processCFS(CFStatement CFS) {
		/*for(int i=0; i<CFS.scope.depth; i++)
			System.out.print("  ");
		System.out.println(CFS.getClass().getSimpleName());*/
		
		asmOutput.add(new Asm(Asm.Op.label, getLabel(CFS)));
		if(CFS instanceof CFPushScope) {
			CFPushScope CFPS = (CFPushScope)CFS;
			if(CFPS.stackOffset > 0)
				pushScope(CFPS.stackOffset, CFS instanceof CFMethod);
		}
		else if(CFS instanceof CFEndMethod) {
			CFEndMethod CFEM = (CFEndMethod)CFS;
			if(CFEM.end == MethodEnd.main) {
				asmOutput.add(new Asm(Asm.Op.mov, "$0", "%rax"));
				asmOutput.add(new Asm(Asm.Op.ret));
			}
			else if(CFEM.end == MethodEnd.nothing) { //void
				//restore the stack
				CFPushScope CFPS = CFS.scope;
				while(CFPS != null) {
					if(CFPS.stackOffset > 0)
						popScope(CFPS.stackOffset);
					CFPS = CFPS.parent;
				}
				asmOutput.add(new Asm(Asm.Op.ret));
			}
			return;
		}
		else if(CFS instanceof CFContainer) {
			return; //don't add jumps/scopes for CFContainer
			// Nothing to do.
		}
		else if(CFS instanceof CFAssignment) {
			addAssignmentStatement((CFAssignment)CFS);
		}
		else if(CFS instanceof CFMethodCall) {
			CFMethodCall CFMC = (CFMethodCall)CFS;
			executeMethod(CFMC.call, CFMC.scope);
		}
		else if(CFS instanceof CFReturn) {
			//get return value
			IR.Expr expr = ((CFReturn)CFS).expr;
			if(expr != null) {
				IR.Node paramCast = (IR.Node)expr.getChildren().get(0);
				// Just put the atomic return in rax.
				if (paramCast instanceof IR.LocationNoArray) {
					IR.LocationNoArray paramLocationNoArray = (IR.LocationNoArray) paramCast;
					String varVal = getVarLoc(paramLocationNoArray, CFS.scope);
					asmOutput.add(new Asm(Asm.Op.movq, varVal, "%rax"));
					
				}
				else if (paramCast instanceof IR.Literal) {
					asmOutput.add(new Asm(Asm.Op.movq, "$" + ((IR.Literal)paramCast).val(), "%rax"));
				}
				else throw new IllegalStateException("Bad return type " + paramCast.getClass().getSimpleName());
			}
			else asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rax")); //return 0 in main and why not as well in other void functions
			
			//restore the stack
			CFPushScope CFPS = CFS.scope;
			while(CFPS != null) {
				if(CFPS.stackOffset > 0)
					popScope(CFPS.stackOffset);
				CFPS = CFPS.parent;
			}
			
			asmOutput.add(new Asm(Asm.Op.ret));
			return; //we already restored the stack
		}
		else if(CFS instanceof CFBranch) {
			CFBranch CFB = (CFBranch)CFS;
			IR.Expr expr = (IR.Expr) CFB.condition;

			if (expr.getChildren().get(0) instanceof IR.LocationNoArray){
				String varVal = getVarLoc(((IR.LocationNoArray) expr.getChildren().get(0)), CFS.scope);
				
				asmOutput.add(new Asm(Asm.Op.movq, varVal, "%rdi"));
				asmOutput.add(new Asm(Asm.Op.movq, "$1", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				// Jump to next2 if false
				asmOutput.add(new Asm(Asm.Op.jne, getLabel(CFB.next2)));

			} else if (expr.getChildren().get(0) instanceof IR.BoolLiteral){
				if (((IR.BoolLiteral) expr.getChildren().get(0)).value == false) {
					// Jump to next2 unconditionally (it's false)
					asmOutput.add(new Asm(Asm.Op.jmp, getLabel(CFB.next2)));
				}
			}
		}
		else if(CFS instanceof CFNop) {
			// Nothing to do.
		}
		else throw new IllegalStateException("Unexpected CFS type: " + CFS.getClass().getCanonicalName());
		if(CFS.next != null) {
			if(CFS.scope.depth > CFS.next.scope.depth) {
				CFPushScope CFPS = CFS.scope;
				while(CFPS!=null && CFPS!=CFS.next.scope) {
					if(CFPS.stackOffset > 0)
						popScope(CFPS.stackOffset);
					CFPS = CFPS.parent;
				}
			}
			if(CFS.next.orderpos != CFS.orderpos+1) {
				asmOutput.add(new Asm(Asm.Op.jmp, getLabel(CFS.next)));
			}
		}
	}
	private List<CFStatement> CFOrder;
	private void genCFOrder(CFStatement CFS, CFMergeBranch stop) {
		if(CFS==null || CFS==stop || CFS.orderpos!=-1)
			return;
		CFS.orderpos = CFOrder.size();
		CFOrder.add(CFS);
		if(CFS instanceof CFContainer) {
			CFContainer CFSC = (CFContainer)CFS;
			genCFOrder(CFSC.start, (CFMergeBranch)CFSC.next);
			genCFOrder(CFSC.next, stop);
			return;
		}
		else if(CFS instanceof CFBranch) {
			CFBranch CFB = (CFBranch)CFS;
			genCFOrder(CFB.next, stop);
			genCFOrder(CFB.next2, stop);
		}
		else {
			genCFOrder(CFS.next, stop);
		}
	}
	private void methodToAsm(String name, ControlFlow.MethodSym method) {
		CFOrder = new ArrayList<>();
		genCFOrder(method.code, null);
		if(name.equals("main")) {
			asmOutput.add(new Asm(Asm.Op.custom, ".globl main"));
			for(int i=0; i<CF.program.stackOffset; i+=8) //initialize global values to 0
				asmOutput.add(new Asm(Asm.Op.movq, "$0", "" + i + " + globalvar"));
		}
		asmOutput.add(new Asm(Asm.Op.methodlabel, name));
		for(CFStatement i: CFOrder)
			processCFS(i);
		asmOutput.add(new Asm(Asm.Op.newline));
	}
	public void build() {
		asmOutput = new ArrayList<>();
		addedStrings = new HashMap<>();
		
		labelCount = 0;
		labels = new HashMap<>();
		//don't add CF.program to scopes because it"s a special global scope
		if(CF.program.stackOffset > 0) {
			asmOutput.add(new Asm(Asm.Op.custom, ".comm globalvar, " + CF.program.stackOffset + ", 8"));
			//asmOutput.add(new Asm(Asm.Op.custom, ".comm import_align, 16 , 16"));
			asmOutput.add(new Asm(Asm.Op.newline));
		}
		for(Map.Entry<String, ControlFlow.MethodSym> method: CF.methods.entrySet())
			methodToAsm(method.getKey(), method.getValue());

		Set<String> usedLabels = new HashSet<>();
		for(Asm i: asmOutput)
			if(i.op==Asm.Op.jz || i.op==Asm.Op.jmp || i.op==Asm.Op.jz || i.op==Asm.Op.jne)
				usedLabels.add(i.arg1);
		asmOutput.removeIf(x -> x.op==Asm.Op.label && !usedLabels.contains(x.arg1));
	}
	public List<String> getAsm() {
		List<String> result = new ArrayList<>();

		for(Map.Entry<String, String> i: addedStrings.entrySet()) {
			result.add(i.getValue() + ":");
			result.add("\t.string " + i.getKey());
			result.add("\t.align 16");
			result.add("");
		}
		
		boolean indent = false;
		for(Asm i: asmOutput) {
			String to_add = new String();
			if(indent)
				to_add += '\t';
			to_add += i;
			if(i.op == Asm.Op.newline)
				indent = false;
			else if(i.op == Asm.Op.methodlabel)
				indent = true;
			result.add(to_add);
		}
		return result;
	}
	public void addAssignmentStatement (CFAssignment inp){
		CFPushScope scope = inp.scope;
		getVarLoc(inp.loc, scope);
		IR.Op top = inp.op; 
		IR.Op.Type t_op = top.type; 
		IR.Expr expr = inp.expr; 
		String location = getVarLoc(inp.loc, scope);
		if (t_op != IR.Op.Type.assign){
			if (t_op == IR.Op.Type.increment){
				asmOutput.add(new Asm(Asm.Op.movq, location, "%rdi")); 
				asmOutput.add(new Asm(Asm.Op.inc, "%rdi"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location)); 
			}
			else if (t_op == IR.Op.Type.decrement){
				asmOutput.add(new Asm(Asm.Op.movq, location, "%rdi")); 
				asmOutput.add(new Asm(Asm.Op.dec, "%rdi"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location)); 
			}
			else if (t_op == IR.Op.Type.plusequals){
				IR.Node child2 = expr.members.get(0); 
				// rdi temp register
				if (child2 instanceof IR.Literal){
					IR.Literal c2 = (IR.Literal) child2;
					asmOutput.add(new Asm(Asm.Op.movq, "$" + c2.val(), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.addq, "%rdi", location));
				}
				else if (child2 instanceof IR.Location){
					IR.Location c2 = (IR.Location) child2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.addq, "%rdi", location));
				}				
			}
			else if (t_op == IR.Op.Type.minusequals){
				IR.Node child2 = expr.members.get(0); 
				// rdi temp register
				if (child2 instanceof IR.Literal){
					IR.IntLiteral c2 = (IR.IntLiteral) child2;
					asmOutput.add(new Asm(Asm.Op.movq, "$" + c2.val(), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.subq, "%rdi", location));
				}
				else if (child2 instanceof IR.Location){
					IR.Location c2 = (IR.Location) child2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.subq, "%rdi", location));
				}				
			}

			return; 
		}
		IR.Node child1 = expr.members.get(0); 

		if (child1 instanceof IR.Op){
			IR.Op op = (IR.Op) child1; 
			if (op.type == IR.Op.Type.minus){
				IR.Expr lm2 = (IR.Expr)(expr.members.get(1)); 
				IR.Node child2 = lm2.members.get(0); 
				// rdi temp register
				if (child2 instanceof IR.IntLiteral){
					IR.IntLiteral c2 = (IR.IntLiteral) child2;
					asmOutput.add(new Asm(Asm.Op.movq, "$" + c2.getText(), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.neg, "%rdi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location));
				}
				else if (child2 instanceof IR.LocationNoArray){
					IR.LocationNoArray c2 = (IR.LocationNoArray) child2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.neg, "%rdi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location));
				}
			}
			if (op.type == IR.Op.Type.not){
				IR.Expr lm2 = (IR.Expr)(expr.members.get(1)); 
				IR.Node child2 = lm2.members.get(0); 
				// rdi temp register
				if (child2 instanceof IR.BoolLiteral){
					IR.BoolLiteral c2 = (IR.BoolLiteral) child2;
					if (c2.value == true){
						asmOutput.add(new Asm(Asm.Op.movq, "$0", location)); 
					}
					else{
						asmOutput.add(new Asm(Asm.Op.movq, "$1", location)); 
					}
				}
				else if (child2 instanceof IR.LocationNoArray){
					IR.LocationNoArray c2 = (IR.LocationNoArray) child2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi")); 
					asmOutput.add(new Asm(Asm.Op.neg, "%rdi"));
					asmOutput.add(new Asm(Asm.Op.inc, "%rdi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location));
				}
			}
		}
		else if (expr.members.size() == 1){
			/*if (child1 instanceof IR.LocationArray){
				IR.LocationArray c2 = (IR.LocationArray) child1;
				IR.Node cm = c2.index.members.get(0);
				if (cm instanceof IR.LocationNoArray){
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi"));
					IR.LocationNoArray cm2 = (IR.LocationNoArray) cm; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(cm2, scope), "%rsi"));	
					asmOutput.add(new Asm(Asm.Op.movq, "$16", "%rax"));
					asmOutput.add(new Asm(Asm.Op.imul, "%rsi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rax", "%rsi"));
					asmOutput.add(new Asm(Asm.Op.addq, "%rsi", "%rdi"));
					asmOutput.add(new Asm(Asm.Op.movq, "(%rdi)", "%rdi"));
				}
				else{ 
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi"));
				}
			}
			else if (child1 instanceof IR.LocationNoArray){
				IR.LocationNoArray c2 = (IR.LocationNoArray) child1; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi"));
			}*/
			if (child1 instanceof IR.Location){
				IR.Location c2 = (IR.Location) child1; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi"));
			}
			else if (child1 instanceof IR.BoolLiteral){
				IR.BoolLiteral c2 = (IR.BoolLiteral) child1; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", "%rdi"));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rdi"));
			}
			else if (child1 instanceof IR.CharLiteral){
				IR.CharLiteral c2 = (IR.CharLiteral) child1; 
				int vl = (int) c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rdi")); 
			}
			else if (child1 instanceof IR.IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) child1; 
				long vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rdi")); 
			}
			else if(child1 instanceof IR.MethodCall) {
				executeMethod((IR.MethodCall)child1, inp.scope);
				asmOutput.add(new Asm(Asm.Op.movq, "%rax", "%rdi")); 
			}
			asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location));
		}
		else{
			IR.Op child2 = (IR.Op) (expr.members.get(1));
			IR.Node chil1 = ((IR.Expr)(child1)).members.get(0); 
			IR.Node child3 = ((IR.Expr)(expr.members.get(2))).members.get(0);
			
			if (chil1 instanceof IR.Location){
				IR.Location c2 = (IR.Location) chil1; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rdi"));
			}
			else if (chil1 instanceof IR.BoolLiteral){
				IR.BoolLiteral c2 = (IR.BoolLiteral) chil1; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", "%rdi"));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rdi"));
			}
			else if (chil1 instanceof IR.CharLiteral){
				IR.CharLiteral c2 = (IR.CharLiteral) chil1; 
				long vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rdi")); 
			}
			else if (chil1 instanceof IR.IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) chil1; 
				long vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rdi")); 
			}
			else throw new IllegalStateException("bad assignment child type: " + child1.getClass().getSimpleName());
			
			if (child3 instanceof IR.Location){
				IR.Location c2 = (IR.Location) child3; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), "%rsi"));
			}
			else if (child3 instanceof IR.BoolLiteral){
				IR.BoolLiteral c2 = (IR.BoolLiteral) child3; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", "%rsi"));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rsi"));
			}
			else if (child3 instanceof IR.CharLiteral){
				IR.CharLiteral c2 = (IR.CharLiteral) child3; 
				long vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rsi")); 
			}
			else if (child3 instanceof IR.IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) child3; 
				long vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, "%rsi")); 
			}
			else throw new IllegalStateException("bad assignment child type: " + child3.getClass().getSimpleName());
			
			if (child2 instanceof IR.Op){
				IR.Op c2 = (IR.Op) child2;
				IR.Op.Type ctype = c2.type; 

				if (ctype == IR.Op.Type.plus){
					asmOutput.add(new Asm(Asm.Op.addq, "%rsi", "%rdi"));	
				}
				else if (ctype == IR.Op.Type.minus){
					asmOutput.add(new Asm(Asm.Op.subq, "%rsi", "%rdi"));	
				}
				else if (ctype == IR.Op.Type.mult){
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
					asmOutput.add(new Asm(Asm.Op.imul, "%rsi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rax", "%rdi")); 	
				}
				else if (ctype == IR.Op.Type.div){
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
					asmOutput.add(new Asm(Asm.Op.cqto));
					//asmOutput.add(new Asm(Asm.Op.movq, "$0", "%rdx")); //what's this for? @shwetark? use cqto instead I think
					asmOutput.add(new Asm(Asm.Op.idiv, "%rsi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rax", "%rdi"));
				}
				else if (ctype == IR.Op.Type.mod){
					asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
					asmOutput.add(new Asm(Asm.Op.cqto));
					asmOutput.add(new Asm(Asm.Op.idiv, "%rsi"));
					asmOutput.add(new Asm(Asm.Op.movq, "%rdx", "%rdi"));
				}
				else if (ctype == IR.Op.Type.andand){
					asmOutput.add(new Asm(Asm.Op.andq, "%rsi", "%rdi"));
				}			
				else if (ctype == IR.Op.Type.oror){
					asmOutput.add(new Asm(Asm.Op.orq, "%rsi", "%rdi"));
				}			
				else if (ctype == IR.Op.Type.eq){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
					asmOutput.add(new Asm(Asm.Op.sete, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else if (ctype == IR.Op.Type.neq){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
					asmOutput.add(new Asm(Asm.Op.setne, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else if (ctype == IR.Op.Type.greater){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rsi", "%rdi"));
					asmOutput.add(new Asm(Asm.Op.setg, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else if (ctype ==  IR.Op.Type.less){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rsi", "%rdi"));
					asmOutput.add(new Asm(Asm.Op.setl, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else if (ctype ==  IR.Op.Type.geq){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rsi", "%rdi"));
					asmOutput.add(new Asm(Asm.Op.setge, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else if (ctype == IR.Op.Type.leq){
					asmOutput.add(new Asm(Asm.Op.cmp, "%rsi", "%rdi"));
					asmOutput.add(new Asm(Asm.Op.setle, "%al"));
					asmOutput.add(new Asm(Asm.Op.movzx, "%al", "%rdi"));
				}
				else throw new IllegalStateException("Bad op type: " + ctype.name());
			}
			asmOutput.add(new Asm(Asm.Op.movq, "%rdi", location));			
		}
	}
}