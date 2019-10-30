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
import edu.mit.compilers.codegen.ControlFlow.CFMethodCall;
import edu.mit.compilers.codegen.ControlFlow.CFNop;
import edu.mit.compilers.codegen.ControlFlow.CFPushScope;
import edu.mit.compilers.codegen.ControlFlow.CFReturn;
import edu.mit.compilers.codegen.ControlFlow.CFContainer;
import edu.mit.compilers.codegen.ControlFlow.CFStatement;
import edu.mit.compilers.semantics.IR;
import edu.mit.compilers.semantics.IR.Op;

public class Codegen {
	//TODO: there could be name conflicts if something is called "label" or "globalvar"
	public ControlFlow CF;
	public List<Asm> asmOutput;
	public List<Asm> asmStringOutput;
	public 
	public static class Asm {
		public enum Op { //newline is just whitespace for formatting
			methodlabel, label, pushq, movq, popq, add, sub, ret, custom, newline, xor, jz, jnz, test, inc, dec, cmp, jmp, not, neg;
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
	private void writeAsmLabel(CFStatement CFS) {
		if(!labels.containsKey(CFS)) {
			String label = "label" + labelCount;
			labels.put(CFS, label);
			labelCount++;
			asmOutput.add(new Asm(Asm.Op.label, label));
		}
		else asmOutput.add(new Asm(Asm.Op.label, labels.get(CFS))); //a label's already been created for this
	}
	private String getLabel(CFStatement CFS) {
		if(!labels.containsKey(CFS)) {
			String label = "label" + labelCount;
			labels.put(CFS, label);
			labelCount++;
			return label;
		}
		return labels.get(CFS);
	}
	private String getVarLoc(IR.Location loc, CFPushScope scope) {
		String name = loc.ID;
		long stackOffset = 0;
		while(scope != null) {
			if(scope.variables.containsKey(name)) {
				if(loc instanceof IR.LocationArray)
					return (stackOffset + scope.variables.get(name).stackOffset) + "(%rsp)"; //TODO: deal with arrays
				else return (stackOffset + scope.variables.get(name).stackOffset) + "(%rsp)";
			}
			stackOffset += scope.stackOffset + ControlFlow.wordSize; //+8 because we push rbp every scope
			scope = scope.parent;
		}
		//it's in the global scope
		if(!CF.program.variables.containsKey(name))
			throw new IllegalStateException("Variable with name \"" + name + "\" not found in any scope");
		return CF.program.variables.get(name).stackOffset + "(%globalvar)";
	}
	private String getVarVal(IR.Location loc, CFPushScope scope) {
		return '[' + getVarLoc(loc, scope) + ']';
	}
	private void processCFS(CFStatement CFS) {
		for(int i=0; i<CFS.scope.depth; i++)
			System.out.print("  ");
		System.out.println(CFS.getClass().getCanonicalName());
		writeAsmLabel(CFS);
		if(CFS instanceof CFPushScope) { //or Method
			CFPushScope CFPS = (CFPushScope)CFS;
			if(CFPS.stackOffset > 0) {
				asmOutput.add(new Asm(Asm.Op.pushq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.movq, "%rsp", "%rbp"));
				asmOutput.add(new Asm(Asm.Op.sub, "%rsp", "$" + CFPS.stackOffset));
			}
		}
		else if(CFS instanceof CFEndMethod) {
			if(CFS.scope.stackOffset > 0) {
				asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
				asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
				asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$" + CFS.scope.stackOffset));
			}
			asmOutput.add(new Asm(Asm.Op.ret));
		}
		else if(CFS instanceof CFAssignment) {
			asmOutput.add(new Asm(Asm.Op.custom, "CFAssignment"));
			CFAssignment CFAS = (CFAssignment)CFS;
			String loc1 = getVarLoc(CFAS.loc, CFAS.scope);
			if(CFAS.op.type == IR.Op.Type.increment)
				asmOutput.add(new Asm(Asm.Op.inc, loc1));
			else if(CFAS.op.type == IR.Op.Type.decrement)
				asmOutput.add(new Asm(Asm.Op.dec, loc1));
			else {
				IR.Node mem0 = CFAS.expr.members.get(0);
				if(mem0 instanceof IR.Location)
				{
					String loc2 = getVarLoc((IR.Location)mem0, CFS.scope);
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
									asmOutput.add(new Asm(Asm.Op.xor, "$1", getVarLoc((IR.LocationNoArray)mem1, CFS.scope)));
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

				asmOutput.add(new Asm(Asm.Op.custom, "CFMethodCall"));

				// Build assembly instructions for the params.
				for (Object param: params) {
					// Check if param is a string.

					// Put all of the strings in a seperate list.
					if (param instanceof String) {
						// Create Tag.

						asmStringOutput.add(new Asm(Asm.Op.pushq, label);

						asmOutput.add(new Asm(Asm.Op.pushq, param.));
						// Do .string instruction

						// align the memory.


					}
					else if (param instanceof IR.BoolLiteral) {
						IR.BoolLiteral paramCast = (IR.BoolLiteral) param;
						if (paramCast.value) {
							asmOutput.add(new Asm(Asm.Op.pushq, "$1"));
						} else {
							asmOutput.add(new Asm(Asm.Op.pushq, "$0"));
						}
					}

					else if (param instanceof IR.IntLiteral) {
						IR.IntLiteral paramCast = (IR.IntLiteral) param;
						asmOutput.add(new Asm(Asm.Op.pushq, "$" + paramCast.getText()));
					}

					else { // Must be a literial I can look up in the Symbol Table.
						// Push to stack.
						asmOutput.add(new Asm(Asm.Op.jmp, CFMC.ID));

					}
				}

				// Jump to method's location.
				asmOutput.add(new Asm(Asm.Op.jmp, CFMC.ID));

			}
			else {
				throw new IllegalStateException("Unknown method ID: " + CFMC.ID);
			}
		}
		else if(CFS instanceof CFReturn) {
			asmOutput.add(new Asm(Asm.Op.custom, "CFReturn"));
		}
		else if(CFS instanceof CFBranch) {
			//TODO: handle arrays
			CFBranch CFB = (CFBranch)CFS;
			IR.Expr expr = CFB.condition;
			asmOutput.add(new Asm(Asm.Op.cmp, "Expr", "$1"));
			asmOutput.add(new Asm(Asm.Op.jnz, getLabel(CFB.next2)));
		}
		else if(CFS instanceof CFNop) {

		}
		else if(CFS instanceof CFContainer) {
			return;
		}
		else throw new IllegalStateException("Unexpected CFS type: " + CFS.getClass().getCanonicalName());
		if(CFS.next != null) {
			if(CFS.scope.depth > CFS.next.scope.depth) {
				CFPushScope CFPS = CFS.scope;
				while(CFPS!=null && CFPS!=CFS.next.scope) {
					if(CFPS.stackOffset > 0) {
						asmOutput.add(new Asm(Asm.Op.add, "%rsp", "$" + CFPS.stackOffset));
						asmOutput.add(new Asm(Asm.Op.movq, "%rbp", "%rsp"));
						asmOutput.add(new Asm(Asm.Op.popq, "%rbp"));
					}
					CFPS = CFPS.parent;
				}
			}
			if(CFS.next.orderpos != CFS.orderpos+1) {
				asmOutput.add(new Asm(Asm.Op.jmp, labels.get(CFS.next)));
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
	public void addAssignmentStatement (CFAssignment inp){
		CFPushScope scope = inp.scope;
		String location = getVarLoc (inp.loc, scope); 
		IR.Op t_op = inp.op; 
		IR.Expr expr = inp.expr; 
		if (t_op != IR.Op.assign){
			if (t_op == IR.Op.increment){
				asmOutput.add(new Asm(Asm.Op.inc, location));
			}
			else if (t_op == IR.Op.decrement){
				asmOutput.add(new Asm(Asm.Op.dec, location));
			}
			else if (t_op == IR.Op.plusequals){
				IR.Node child2 = expr.members.get(0); 
				// rdi temp register
				if (child2 instanceof IntLiteral){
					IR.IntLiteral c2 = (IntLiteral) child2;
					asmOutput.add(new Asm(Asm.Op.movq, '$' + c2.getText(), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.add, location, '%rdi'));
				}
				else if (child2 instanceof LocationNoArray){
					IR.LocationNoArray c2 = (LocationNoArray) c2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.add, location, '%rdi'));
				}				
			}
			else if (t_op == IR.Op.minusequals){
				IR.Node child2 = expr.members.get(0); 
				// rdi temp register
				if (child2 instanceof IntLiteral){
					IR.IntLiteral c2 = (IntLiteral) child2;
					asmOutput.add(new Asm(Asm.Op.movq, '$' + c2.getText(), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.sub, location, '%rdi'));
				}
				else if (child2 instanceof LocationNoArray){
					IR.LocationNoArray c2 = (LocationNoArray) c2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.sub, location, '%rdi'));
				}				
			}

			return; 
		}
		IR.Node child1 = expr.members.get(0); 

		if (child1 instanceof IR.Op){
			IR.Op op = (IR.Op) child1; 
			if (op == IR.Op.minus){
				IR.Node child2 = expr.members.get(1); 
				// rdi temp register
				if (child2 instanceof IntLiteral){
					IR.IntLiteral c2 = (IntLiteral) child2;
					asmOutput.add(new Asm(Asm.Op.movq, '$' + c2.getText(), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.neg, '%rdi'));
					asmOutput.add(new Asm(Asm.Op.movq, '%rdi', location));
				}
				else if (child2 instanceof LocationNoArray){
					IR.LocationNoArray c2 = (LocationNoArray) c2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.neg, '%rdi'));
					asmOutput.add(new Asm(Asm.Op.movq, '%rdi', location));
				}
			}
			if (op == IR.Op.not){
				IR.Node child2 = expr.members.get(1); 
				// rdi temp register
				if (child2 instanceof BoolLiteral){
					IR.BoolLiteral c2 = (BoolLiteral) child2;
					if (c2.value == true){
						asmOutput.add(new Asm(Asm.Op.movq, '$0', location)); 
					}
					else{
						asmOutput.add(new Asm(Asm.Op.movq, '$1', location)); 
					}
				}
				else if (child2 instanceof LocationNoArray){
					IR.LocationNoArray c2 = (LocationNoArray) c2; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi')); 
					asmOutput.add(new Asm(Asm.Op.neg, '%rdi'));
					asmOutput.add(new Asm(Asm.Op.inc, '%rdi'));
					asmOutput.add(new Asm(Asm.Op.movq, '%rdi', location));
				}
			}
		}
		else if (expr.members.size() == 1){
			if (child1 instanceof LocationArray){
				IR.LocationArray c2 = (LocationArray) c2;
				IR.Node cm = c2.index.members.get(0);
				if (cm instanceof LocationNoArray){
					asmOutput.add(new Asm(Asm.Op.movq, getVarLoc(c2, scope), '%rdi'));
					IR.LocationNoArray cm2 = (LocationNoArray) cm; 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(cm2, scope), '%rsi'));	
					asmOutput.add(new Asm(Asm.Op.movq, "$16", "%rax"));
					asmOutput.add(new Asm(Asm.Op.mul, '%rsi'));
					asmOutput.add(new Asm(Asm.Op.movq, "%rax", "%rsi"));
					asmOutput.add(new Asm(Asm.Op.add, "%rdi", '%rsi'));
					asmOutput.add(new Asm(Asm.Op.movq, "[%rdi]", "%rdi"));
				}
				else{ 
					asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi'));
				}
			}
			if (child1 instanceof LocationNoArray){
				IR.LocationNoArray c2 = (LocationNoArray) c2; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi'));
			}
			if (child1 instanceof BoolLiteral){
				IR.BoolLiteral c2 = (BoolLiteral) c2; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", '%rdi'));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", '%rdi'));
			}
			if (child1 instanceof CharLiteral){
				IR.CharLiteral c2 = (CharLiteral) c2; 
				int vl = (int) c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rdi')); 
			}
			if (child1 instanceof IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) c2; 
				int vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rdi')); 
			}
			asmOutput.add(new Asm(Asm.Op.movq, '%rdi', location));
		}
		else{
			IR.Op child2 = (IR.Op) (expr.members.get(1));
			IR.Node child3 = expr.members.get(2);
			if (child1 instanceof LocationNoArray){
				IR.LocationNoArray c2 = (LocationNoArray) c2; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rdi'));
			}
			if (child1 instanceof BoolLiteral){
				IR.BoolLiteral c2 = (BoolLiteral) c2; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", '%rdi'));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", '%rdi'));
			}
			if (child1 instanceof CharLiteral){
				IR.CharLiteral c2 = (CharLiteral) c2; 
				int vl = (int) c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rdi')); 
			}
			if (child1 instanceof IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) c2; 
				int vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rdi')); 
			}
			if (child3 instanceof LocationNoArray){
				IR.LocationNoArray c2 = (LocationNoArray) c2; 
				asmOutput.add(new Asm(Asm.Op.movq, getVarVal(c2, scope), '%rsi'));
			}
			if (child3 instanceof BoolLiteral){
				IR.BoolLiteral c2 = (BoolLiteral) c2; 
				if (c2.value == true) 
					asmOutput.add(new Asm(Asm.Op.movq, "$1", '%rsi'));
				else
					asmOutput.add(new Asm(Asm.Op.movq, "$0", '%rsi'));
			}
			if (child3 instanceof CharLiteral){
				IR.CharLiteral c2 = (CharLiteral) c2; 
				int vl = (int) c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rsi')); 
			}
			if (child3 instanceof IntLiteral){
				IR.IntLiteral c2 = (IR.IntLiteral) c2; 
				int vl = c2.value; 

				asmOutput.add(new Asm(Asm.Op.movq, "$" + vl, '%rsi')); 
			}
			if (child2 instanceof IR.Op.plus){
				asmOutput.add(new Asm(Asm.Op.add, '%rdi', '%rsi'));	
			}
			if (child2 instanceof IR.Op.minus){
				asmOutput.add(new Asm(Asm.Op.sub, '%rdi', '%rsi'));	
			}
			if (child2 instanceof IR.Op.mult){
				asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
				asmOutput.add(new Asm(Asm.Op.mul, '%rsi'));
				asmOutput.add(new Asm(Asm.op.movq, "%rax", "%rdi")); 	
			}
			if (child2 instanceof IR.Op.div){
				asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
				asmOutput.add(new Asm(Asm.Op.div, '%rsi'));
				asmOutput.add(new Asm(Asm.op.movq, "%rax", "%rdi"));
			}
			if (child2 instanceof IR.Op.mod){
				asmOutput.add(new Asm(Asm.Op.movq, "%rdi", "%rax"));
				asmOutput.add(new Asm(Asm.Op.div, '%rsi'));
				asmOutput.add(new Asm(Asm.op.movq, "%rdx", "%rdi"));
			}
			if (child2 instanceof IR.Op.andand){
				asmOutput.add(new Asm(Asm.Op.and, "%rdi", "%rsi"));
			}			
			if (child2 instanceof IR.Op.oror){
				asmOutput.add(new Asm(Asm.Op.or, "%rdi", "%rsi"));
			}			
			if (child2 instanceof IR.Op.eq){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.sete, "%rdi"));
			}
			if (child2 instanceof IR.Op.neq){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.setne, "%rdi"));
			}
			if (child2 instanceof IR.Op.greater){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.setg, "%rdi"));
			}
			if (child2 instanceof IR.Op.less){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.setl, "%rdi"));
			}
			if (child2 instanceof IR.Op.geq){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.setge, "%rdi"));
			}
			if (child2 instanceof IR.Op.leq){
				asmOutput.add(new Asm(Asm.Op.cmp, "%rdi", "%rsi"));
				asmOutput.add(new Asm(Asm.Op.setle, "%rdi"));
			}
			asmOutput.add(new Asm(Asm.Op.movq, '%rdi', location));			
		}
	}
}
