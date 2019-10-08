package edu.mit.compilers.semantics;
import edu.mit.compilers.grammar.*;

public class IR {
	private ParseTree parseTree;
	private IR.Node root;
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	public static class Node {
		public enum Type {
			program, import_decl, field_decl, method_decl, method_param, block, type,
			statement_assignment, statement_method_call, statement_if, statement_for,
			statement_while, statement_return, statement_break, statement_continue,
			assign_expr, assign_op, compound_assign_op, method_params_none,
			method_params_local, method_params_import, expr, import_arg, bin_op,
			arith_op, rel_op, eq_op, cond_op, location_array, location_noarray,
			char_literal, bool_literal, int_literal,
			invalid;
		}
		public final IR.Node.Type type;
		public final String text;
		private Node(ParseTree.Node node)
		{
			int nt = node.type;
			if(nt == ParseTree.Node.Type.AST_program)
			{
				type = Type.program;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_import_decl)
			{
				type = Type.import_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_field_decl)
			{
				type = Type.field_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_decl)
			{
				type = Type.method_decl;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_param)
			{
				type = Type.method_param;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_block)
			{
				type = Type.block;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_type)
			{
				type = Type.type;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_type)
			{
				type = Type.type;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_assignment)
			{
				type = Type.statement_assignment;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_method_call)
			{
				type = Type.statement_method_call;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_if)
			{
				type = Type.statement_if;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_for)
			{
				type = Type.statement_for;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_while)
			{
				type = Type.statement_while;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_return)
			{
				type = Type.statement_return;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_break)
			{
				type = Type.statement_break;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_statement_continue)
			{
				type = Type.statement_continue;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_assign_expr)
			{
				type = Type.assign_expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_assign_op)
			{
				type = Type.assign_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_compound_assign_op)
			{
				type = Type.compound_assign_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_none)
			{
				type = Type.method_params_none;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_local)
			{
				type = Type.method_params_local;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_method_params_import)
			{
				type = Type.method_params_import;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_expr)
			{
				type = Type.expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_import_arg)
			{
				type = Type.import_arg;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_bin_op)
			{
				type = Type.bin_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_arith_op)
			{
				type = Type.arith_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_rel_op)
			{
				type = Type.rel_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_eq_op)
			{
				type = Type.eq_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_cond_op)
			{
				type = Type.cond_op;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_location_array)
			{
				type = Type.location_array;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_location_noarray)
			{
				type = Type.location_noarray;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_char_literal)
			{
				type = Type.char_literal;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_expr)
			{
				type = Type.expr;
				text = "";
			}
			else if(nt == ParseTree.Node.Type.AST_bool_literal)
			{
				type = Type.bool_literal;
				text = "";
			}
			else
			{
				type = Type.invalid;
				text = "";
			}
		}
	}
	
	public void build() {
		root = new Node(parseTree.root);
	}
	
}