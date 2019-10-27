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
import edu.mit.compilers.codegen.ControlFlow.CFMethodCall;
import edu.mit.compilers.codegen.ControlFlow.CFNop;
import edu.mit.compilers.codegen.ControlFlow.CFPopScope;
import edu.mit.compilers.codegen.ControlFlow.CFPushScope;
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
			label, pushq, movq, popq, add, sub, ret, custom, newline, xor, jz, jnz, test, inc, dec;
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
			if(op == Op.label)
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
	private List<CFPushScope> scopes;
	private long labelCount;
	private Map<CFStatement, String> labels;
	private Set<String> usedLabels;
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
	private String getVarLoc(String name) {
		long stackOffset = 0;
		for(int i=scopes.size()-1; i>=0; i--) {
			if(scopes.get(i).variables.containsKey(name))
				return (stackOffset + scopes.get(i).variables.get(name).stackOffset) + "(%rsp)";
			stackOffset += scopes.get(i).stackOffset + 8; //+8 because we push rbp every scope
		}
		//it's in the global scope
		if(!CF.program.variables.containsKey(name))
			throw new IllegalStateException("Variable with name \"" + name + "\" not found in any scope");
		return CF.program.variables.get(name).stackOffset + "(%globalvar)";
	}
	private void processCFS(CFStatement _CFS, CFStatement stop) {
		while(_CFS != null && _CFS != stop) {
			addLabel(_CFS);
			if(_CFS instanceof CFPushScope) { //or Method
				CFPushScope CFS = (CFPushScope)_CFS;
				scopes.add(CFS);
				asmOutput.add(new Asm(Asm.Op.pushq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%rbp"));
				asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$-" + CFS.stackOffset));
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof CFPopScope) { //or CFEndMethod
				scopes.remove(scopes.size()-1);
				asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
				asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
				if(_CFS instanceof CFEndMethod)
					asmOutput.add(new Asm(Asm.Op.ret));
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof CFShortCircuit) {
				CFShortCircuit CFS = (CFShortCircuit)_CFS;
				Queue<CFStatement> statements = new ArrayDeque<>();
				statements.add(CFS.start);
				boolean nextSet = false;
				while(!statements.isEmpty()) {
					CFStatement cur = statements.poll();
					if(cur instanceof CFBranch) {
						CFBranch CFB = (CFBranch)cur;
						asmOutput.add(new Asm(Asm.Op.test, ""));
						asmOutput.add(new Asm(Asm.Op.jnz, ""));
						statements.add(CFB.next);
						statements.add(CFB.next2);
					}
					else if(cur instanceof CFPushScope) {
						if(_CFS!=cur && nextSet)
							throw new IllegalStateException("CFShortCircuit has more than one next CFPushScope");
						_CFS = cur;
						nextSet = true;
					}
				}
				if(!nextSet)
					throw new IllegalStateException("CFShortCircuit has no next CFPushScope");
				List<CFPushScope> oldScopes = new ArrayList<>(scopes);
				processCFS(_CFS, ((CFPushScope)_CFS).end);
				scopes = oldScopes;
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof CFAssignment) {
				CFAssignment CFS = (CFAssignment)_CFS;
				String loc1 = getVarLoc(CFS.loc.ID);
				if(CFS.op.type == IR.Op.Type.increment)
					asmOutput.add(new Asm(Asm.Op.inc, loc1));
				else if(CFS.op.type == IR.Op.Type.decrement)
					asmOutput.add(new Asm(Asm.Op.dec, loc1));
				else {
					IR.Node mem0 = CFS.expr.members.get(0);
					if(mem0 instanceof IR.Location)
					{
						String loc2 = getVarLoc(((IR.Location)mem0).ID);
						if(CFS.op.type == IR.Op.Type.assign)
							asmOutput.add(new Asm(Asm.Op.movq, loc2, loc1));
						else if(CFS.op.type == IR.Op.Type.plusequals)
							asmOutput.add(new Asm(Asm.Op.add, loc2, loc1));
						else if(CFS.op.type == IR.Op.Type.minusequals)
							asmOutput.add(new Asm(Asm.Op.sub, loc2, loc1));
						else throw new IllegalStateException("Unexpected op type: " + CFS.op.type.name());
					}
					else
					{
						//TODO: handle multiply nested expr e.g. ((x))
						
						if(CFS.expr.members.size() == 1) {
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
						else if(CFS.expr.members.size() == 2) {
							IR.Node mem1 = CFS.expr.members.get(1);
							if(!(mem1 instanceof IR.Location))
								throw new IllegalStateException("Expected IR.Location, got " + mem1.getClass().getCanonicalName());
							if(mem0 instanceof IR.Op) {
								if(((IR.Op)mem0).type == IR.Op.Type.not) {
									if(mem1 instanceof IR.LocationNoArray)
										asmOutput.add(new Asm(Asm.Op.xor, "$1", getVarLoc(((IR.LocationNoArray)mem1).ID)));
									else {} //TODO: handle arrays
								}
								else throw new IllegalStateException("Unexpected op type: " + ((IR.Op)mem0).type.name());
							}
							else throw new IllegalStateException("Unexpected expr member: " + mem0.getClass().getCanonicalName());
						}
						else if(CFS.expr.members.size() == 3) {
							IR.Node mem1 = CFS.expr.members.get(1);
							if(!(mem1 instanceof IR.Op))
								throw new IllegalStateException("Expected IR.Op, got " + mem1.getClass().getCanonicalName());
							
						}
						else throw new IllegalStateException("Bad expr size: " + CFS.expr.members.size());
					}
				}
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof CFMethodCall) {
				CFMethodCall CFS = (CFMethodCall)_CFS;
				if(CF.importMethods.containsKey(CFS.ID)) {
					
				}
				else if(CF.methods.containsKey(CFS.ID)) {
					
				}
				else throw new IllegalStateException("Unknown method ID: " + CFS.ID);
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof CFNop) {
				_CFS = _CFS.next;
			}
			else throw new IllegalStateException("Unexpected CFS type: " + _CFS.getClass().getCanonicalName());
			for(int i=0; i<_CFS.scope; i++)
				System.out.print("  ");
			System.out.println(_CFS.getClass().getCanonicalName());
		}
	}
	public void build() {
		asmOutput = new ArrayList<>();
		
		labelCount = 0;
		labels = new HashMap<>();
		usedLabels = new HashSet<>();
		scopes = new ArrayList<>();
		//don't add CF.program to scopes because it's a special global scope
		asmOutput.add(new Asm(Asm.Op.custom, ".comm globalvar, " + CF.program.stackOffset));
		for(Map.Entry<String, ControlFlow.MethodSym> method: CF.methods.entrySet()) {
			String name = method.getKey();
			if(name.equals("main"))
				asmOutput.add(new Asm(Asm.Op.custom, ".globl main"));
			asmOutput.add(new Asm(Asm.Op.label, name));
			processCFS(method.getValue().code, null);
		}
		
		asmOutput.removeIf(x -> x.op==Asm.Op.label && !usedLabels.contains(x.arg1));
		
		for(Asm i: asmOutput)
			System.out.println(i);
	}
}
