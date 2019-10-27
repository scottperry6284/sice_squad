package edu.mit.compilers.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.mit.compilers.codegen.ControlFlow.CFAssignment;
import edu.mit.compilers.codegen.ControlFlow.CFBranch;
import edu.mit.compilers.codegen.ControlFlow.CFEndMethod;
import edu.mit.compilers.codegen.ControlFlow.CFMergeBranch;
import edu.mit.compilers.codegen.ControlFlow.CFMethodCall;
import edu.mit.compilers.codegen.ControlFlow.CFNop;
import edu.mit.compilers.codegen.ControlFlow.CFPushScope;
import edu.mit.compilers.codegen.ControlFlow.CFReturn;
import edu.mit.compilers.codegen.ControlFlow.CFShortCircuit;
import edu.mit.compilers.codegen.ControlFlow.CFStatement;
import edu.mit.compilers.semantics.IR;
import edu.mit.compilers.semantics.IR.Op;

public class Codegen {
	//TODO: there could be name conflicts if something is called "label" or "globalvar"
	public ControlFlow CF;
	public List<Asm> asmOutput;
	public static class Asm {
		public enum Op { //newline is just whitespace for formatting
			methodlabel, label, pushq, movq, popq, add, sub, ret, custom, newline, xor, jz, jnz, test, inc, dec, cmp, jmp;
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
	private void addLabel(CFStatement _CFS) {
		if(!labels.containsKey(_CFS)) {
			String label = "label" + labelCount;
			labels.put(_CFS, label);
			labelCount++;
			asmOutput.add(new Asm(Asm.Op.label, label));
		}
		else asmOutput.add(new Asm(Asm.Op.label, labels.get(_CFS))); //a label's already been created for this
	}
	private void addLabelIfNonexistent(CFStatement _CFS) {
		if(!labels.containsKey(_CFS)) {
			String label = "label" + labelCount;
			labels.put(_CFS, label);
			labelCount++;
		}
	}
	private String getVarLoc(String name, CFStatement CFS) {
		CFPushScope scope;
		if(CFS instanceof CFPushScope)
			scope = (CFPushScope)CFS;
		else scope = CFS.parentScope;
		long stackOffset = 0;
		while(scope != null) {
			if(scope.variables.containsKey(name))
				return (stackOffset + scope.variables.get(name).stackOffset) + "(%rsp)";
			stackOffset += scope.stackOffset + 8; //+8 because we push rbp every scope
			scope = scope.parentScope;
		}
		//it's in the global scope
		if(!CF.program.variables.containsKey(name))
			throw new IllegalStateException("Variable with name \"" + name + "\" not found in any scope");
		return CF.program.variables.get(name).stackOffset + "(%globalvar)";
	}
	private void processCFS(CFStatement CFS) {
		for(int i=0; i<CFS.scope; i++)
			System.out.print("  ");
		System.out.println(CFS.getClass().getCanonicalName());
		addLabel(CFS);
		if(CFS instanceof CFPushScope) { //or Method
			CFPushScope CFPS = (CFPushScope)CFS;
			if(CFPS.stackOffset > 0) {
				asmOutput.add(new Asm(Asm.Op.pushq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%rbp"));
				asmOutput.add(new Asm(Asm.Op.sub, "%rsp", "$" + CFPS.stackOffset));
			}
		}
		else if(CFS instanceof CFEndMethod) {
			if(CFS.parentScope.stackOffset > 0) {
				asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
				asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$" + CFS.parentScope.stackOffset));
			}
			asmOutput.add(new Asm(Asm.Op.ret));
		}
		else if(CFS instanceof CFAssignment) {
			asmOutput.add(new Asm(Asm.Op.custom, "CFAssignment"));
			CFAssignment CFAS = (CFAssignment)CFS;
			String loc1 = getVarLoc(CFAS.loc.ID, CFAS.parentScope);
			if(CFAS.op.type == IR.Op.Type.increment)
				asmOutput.add(new Asm(Asm.Op.inc, loc1));
			else if(CFAS.op.type == IR.Op.Type.decrement)
				asmOutput.add(new Asm(Asm.Op.dec, loc1));
			else {
				IR.Node mem0 = CFAS.expr.members.get(0);
				if(mem0 instanceof IR.Location)
				{
					String loc2 = getVarLoc(((IR.Location)mem0).ID, CFS.parentScope);
					if(CFAS.op.type == IR.Op.Type.assign)
						asmOutput.add(new Asm(Asm.Op.movq, loc2, loc1));
					else if(CFAS.op.type == IR.Op.Type.plusequals)
						asmOutput.add(new Asm(Asm.Op.add, loc2, loc1));
					else if(CFAS.op.type == IR.Op.Type.minusequals)
						asmOutput.add(new Asm(Asm.Op.sub, loc2, loc1));
					else throw new IllegalStateException("Unexpected op type: " + CFAS.op.type.name());
				}
				else
				{
					//TODO: handle multiply nested expr e.g. ((x))
					
					if(CFAS.expr.members.size() == 1) {
						if(mem0 instanceof IR.MethodCall) {
							
						}
						else if(mem0 instanceof IR.Len) {
							
						}
						else if(mem0 instanceof IR.IntLiteral) {
							
						}
						else if(mem0 instanceof IR.CharLiteral) {
							
						}
						else if(mem0 instanceof IR.BoolLiteral) {
							
						}
						else throw new IllegalStateException("Unexpected expr member: " + mem0.getClass().getCanonicalName());
					}
					else if(CFAS.expr.members.size() == 2) {
						IR.Node mem1 = CFAS.expr.members.get(1);
						if(!(mem1 instanceof IR.Location))
							throw new IllegalStateException("Expected IR.Location, got " + mem1.getClass().getCanonicalName());
						if(mem0 instanceof IR.Op) {
							if(((IR.Op)mem0).type == IR.Op.Type.not) {
								if(mem1 instanceof IR.LocationNoArray)
									asmOutput.add(new Asm(Asm.Op.xor, "$1", getVarLoc(((IR.LocationNoArray)mem1).ID, CFS.parentScope)));
								else {} //TODO: handle arrays
							}
							else throw new IllegalStateException("Unexpected op type: " + ((IR.Op)mem0).type.name());
						}
						else throw new IllegalStateException("Unexpected expr member: " + mem0.getClass().getCanonicalName());
					}
					else if(CFAS.expr.members.size() == 3) {
						IR.Node mem1 = CFAS.expr.members.get(1);
						if(!(mem1 instanceof IR.Op))
							throw new IllegalStateException("Expected IR.Op, got " + mem1.getClass().getCanonicalName());
						
					}
					else throw new IllegalStateException("Bad expr size: " + CFAS.expr.members.size());
				}
			}
		}
		else if(CFS instanceof CFMethodCall) {
			CFMethodCall CFMC = (CFMethodCall)CFS;
			if(CF.importMethods.containsKey(CFMC.ID)) {
				
			}
			else if(CF.methods.containsKey(CFMC.ID)) {
				
			}
			else throw new IllegalStateException("Unknown method ID: " + CFMC.ID);
		}
		else if(CFS instanceof CFReturn) {
			asmOutput.add(new Asm(Asm.Op.custom, "CFReturn"));
		}
		else if(CFS instanceof CFBranch) {
			asmOutput.add(new Asm(Asm.Op.custom, "CFBranch"));
		}
		else if(CFS instanceof CFNop) {

		}
		else if(CFS instanceof CFShortCircuit) {
			
		}
		else throw new IllegalStateException("Unexpected CFS type: " + CFS.getClass().getCanonicalName());
		if(CFS.next != null) {
			if(CFS.scope > CFS.next.scope) {
				CFPushScope CFPS;
				if(CFS instanceof CFPushScope)
					CFPS = (CFPushScope)CFS;
				else CFPS = CFS.parentScope;
				while(CFPS!=null && CFPS.scope>CFS.next.scope) {
					if(CFPS.stackOffset > 0) {
						asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$" + CFPS.stackOffset));
						asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
						asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
					}
					CFPS = CFPS.parentScope;
				}
				addLabelIfNonexistent(CFS);
				
			}
			if(CFS.next.orderpos != CFS.orderpos+1) {
				addLabelIfNonexistent(CFS.next);
				asmOutput.add(new Asm(Asm.Op.jmp, labels.get(CFS.next)));
			}
		}
	}
	private List<CFStatement> CFOrder;
	private void genCFOrder(CFStatement CFS, CFPushScope parentScope, CFMergeBranch scopeStop) {
		if(CFS==null || CFS==scopeStop || CFS.orderpos!=-1)
			return;
		CFS.orderpos = CFOrder.size();
		CFS.parentScope = parentScope;
		CFOrder.add(CFS);
		if(CFS instanceof CFShortCircuit) {
			CFShortCircuit CFSC = (CFShortCircuit)CFS;
			genCFOrder(CFSC.start, parentScope, (CFMergeBranch)CFSC.next);
			genCFOrder(CFSC.next, parentScope, scopeStop);
			return;
		}
		else if(CFS instanceof CFBranch) {
			CFBranch CFB = (CFBranch)CFS;
			genCFOrder(CFB.next, parentScope, scopeStop);
			genCFOrder(CFB.next2, parentScope, scopeStop);
		}
		else if(CFS instanceof CFPushScope) {
			CFPushScope CFPS = (CFPushScope)CFS;
			genCFOrder(CFS.next, CFPS, scopeStop);
		}
		else {
			genCFOrder(CFS.next, parentScope, scopeStop);
		}
	}
	private void methodToAsm(String name, ControlFlow.MethodSym method) {
		CFOrder = new ArrayList<>();
		genCFOrder(method.code, null, null);
		if(name.equals("main"))
			asmOutput.add(new Asm(Asm.Op.custom, ".globl main"));
		asmOutput.add(new Asm(Asm.Op.methodlabel, name));
		for(CFStatement i: CFOrder)
			processCFS(i);
		asmOutput.add(new Asm(Asm.Op.newline));
	}
	public void build() {
		asmOutput = new ArrayList<>();
		
		labelCount = 0;
		labels = new HashMap<>();
		//don't add CF.program to scopes because it's a special global scope
		asmOutput.add(new Asm(Asm.Op.custom, ".comm globalvar, " + CF.program.stackOffset + ", 8"));
		asmOutput.add(new Asm(Asm.Op.newline));
		for(Map.Entry<String, ControlFlow.MethodSym> method: CF.methods.entrySet())
			methodToAsm(method.getKey(), method.getValue());

		Set<String> usedLabels = new HashSet<>();
		for(Asm i: asmOutput)
			if(i.op==Asm.Op.jz || i.op==Asm.Op.jmp || i.op==Asm.Op.jz)
				usedLabels.add(i.arg1);
		asmOutput.removeIf(x -> x.op==Asm.Op.label && !usedLabels.contains(x.arg1));
		
		System.out.println();
		boolean indent = false;
		for(Asm i: asmOutput) {
			if(indent)
				System.out.print("\t");
			System.out.println(i);
			if(i.op == Asm.Op.newline)
				indent = false;
			else if(i.op == Asm.Op.methodlabel)
				indent = true;
		}
	}
}
