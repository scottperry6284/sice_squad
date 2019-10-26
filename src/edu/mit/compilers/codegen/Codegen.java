package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.semantics.IR;

public class Codegen {
	//TODO: there could be name conflicts if something is called "label" or "globalvar"
	public ControlFlow CF;
	public List<Asm> asmOutput;
	public static class Asm {
		public enum Op { //newline is just whitespace for formatting
			label, pushq, movq, popq, add, sub, ret, custom, newline, xor;
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
	private List<ControlFlow.CFPushScope> scopes;
	private long labelCount;
	private Map<ControlFlow.CFStatement, String> labels;
	private Set<String> usedLabels;
	private void addLabel(ControlFlow.CFStatement _CFS) {
		if(!labels.containsKey(_CFS)) {
			String label = "label" + labelCount;
			labels.put(_CFS, label);
			labelCount++;
			asmOutput.add(new Asm(Asm.Op.label, label));
		}
		else asmOutput.add(new Asm(Asm.Op.label, labels.get(_CFS))); //a label's already been created for this
	}
	private void addLabelIfNonexistent(ControlFlow.CFStatement _CFS) {
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
			stackOffset += scopes.get(i).stackOffset + 8; //+8 because we push rbp every scop
		}
		//it's in the global scope
		if(!CF.program.variables.containsKey(name))
			throw new IllegalStateException("Variable with name " + name + " not found in any scope");
		return CF.program.variables.get(name).stackOffset + "(%globalvar)";
	}
	private ControlFlow.CFStatement processCFS(ControlFlow.CFStatement _CFS) {
		while(_CFS != null) {
			addLabel(_CFS);
			if(_CFS instanceof ControlFlow.CFPushScope) { //or Method
				ControlFlow.CFPushScope CFS = (ControlFlow.CFPushScope)_CFS;
				scopes.add(CFS);
				asmOutput.add(new Asm(Asm.Op.pushq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%rbp"));
				asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$-" + CFS.stackOffset));
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof ControlFlow.CFPopScope) { //or CFEndMethod
				scopes.remove(scopes.size()-1);
				asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
				asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
				if(_CFS instanceof ControlFlow.CFEndMethod)
					asmOutput.add(new Asm(Asm.Op.ret));
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof ControlFlow.CFBranch) {
				ControlFlow.CFBranch CFS = (ControlFlow.CFBranch)_CFS;
				addLabelIfNonexistent(CFS.next);
				addLabelIfNonexistent(CFS.next2);
				processCFS(CFS.next);
				_CFS = processCFS(CFS.next2);
			}
			else if(_CFS instanceof ControlFlow.CFAssignment) {
				ControlFlow.CFAssignment CFS = (ControlFlow.CFAssignment)_CFS;
				String loc1 = getVarLoc(CFS.loc.ID);
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
						else throw new IllegalStateException("Unexpected expr member: " + mem0.getClass().getCanonicalName());
					}
					else if(CFS.expr.members.size() == 2) {
						IR.Node mem1 = CFS.expr.members.get(1);
						 
						if(mem0 instanceof IR.Op) {
							if(((IR.Op)mem0).type == IR.Op.Type.not) {
								asmOutput.add(new Asm(Asm.Op.xor, "$1", mem1.getText()));
							}
							else throw new IllegalStateException("Unexpected op type: " + ((IR.Op)mem0).type.name());
						}
						else throw new IllegalStateException("Unexpected expr member: " + mem0.getClass().getCanonicalName());
					}
					else if(CFS.expr.members.size() == 3) {
						IR.Node mem1 = CFS.expr.members.get(1);
						
					}
					else throw new IllegalStateException("Bad expr size: " + CFS.expr.members.size());
				}
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof ControlFlow.CFMethodCall) {
				ControlFlow.CFMethodCall CFS = (ControlFlow.CFMethodCall)_CFS;
				if(CF.importMethods.containsKey(CFS.ID)) {
					
				}
				else if(CF.methods.containsKey(CFS.ID)) {
					
				}
				else throw new IllegalStateException("Unknown method ID: " + CFS.ID);
				_CFS = _CFS.next;
			}
			else if(_CFS instanceof ControlFlow.CFMergeBranch)
				return _CFS;
		}
		return null;
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
			processCFS(method.getValue().code);
		}
		
		asmOutput.removeIf(x -> x.op==Asm.Op.label && !usedLabels.contains(x.arg1));
		
		for(Asm i: asmOutput)
			System.out.println(i);
	}
}
