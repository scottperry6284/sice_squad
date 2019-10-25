header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

class DecafParser extends Parser;
options
{
  importVocab = DecafScanner;
  k = 3;
  buildAST = true;
}
tokens
{
  AST_program;
  AST_import_decl;
  AST_field_decl;
  AST_field_decl_inner;
  AST_method_decl;
  AST_method_decl_param;
  AST_block;
  AST_type;
  AST_statement_assignment;
  AST_statement_method_call;
  AST_statement_if;
  AST_statement_for;
  AST_statement_while;
  AST_statement_return;
  AST_statement_break;
  AST_statement_continue;
  AST_assign_expr;
  AST_assign_op;
  AST_compound_assign_op;
  AST_method_call;
  AST_method_params_none;
  AST_method_params_local;
  AST_method_params_import;
  AST_expr;
  AST_import_arg;
  AST_bin_op;
  AST_arith_op;
  AST_rel_op;
  AST_eq_op;
  AST_cond_op;
  AST_location_array;
  AST_location_noarray;
  AST_char_literal;
  AST_bool_literal;
  AST_int_literal;
  AST_len;
}

// Java glue code that makes error reporting easier.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  // Do our own reporting of errors so the parser can return a non-zero status
  // if any errors are detected.
  /** Reports if any errors were reported during parse. */
  private boolean error;

  @Override
  public void reportError (RecognitionException ex) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
  }
  public boolean getError () {
    return error;
  }

  // Selectively turns on debug mode.

  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws TokenStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws TokenStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

program: (import_decl)* (field_decl)* (method_decl)* EOF
		 {## = #(#[AST_program, "program"], ##);};

import_decl: RESERVED_IMPORT ID SEMICOLON
			 {## = #(#[AST_import_decl, "import_decl"], ##);};
			 
field_decl: type field_decl_inner (COMMA field_decl_inner)* SEMICOLON
			{## = #(#[AST_field_decl, "field_decl"], ##);};
			
protected field_decl_inner: (ID (LBRACKET int_literal RBRACKET)?)
							{## = #(#[AST_field_decl_inner, "field_decl_inner"], ##);};

method_decl: (RESERVED_VOID | type) ID LPAREN (method_decl_param (COMMA method_decl_param)*)? RPAREN block
			 {## = #(#[AST_method_decl, "method_decl"], ##);};
			 
protected method_decl_param: type ID
						{## = #(#[AST_method_decl_param, "method_decl_param"], ##);};

block: LCURLY (field_decl)* (statement)* RCURLY
	   {## = #(#[AST_block, "block"], ##);};

type: (RESERVED_INT | RESERVED_BOOL)
	  {## = #(#[AST_type, "type"], ##);};

statement: (statement1 | statement2 | statement3 | statement4 | statement5 | statement6 | statement7 | statement8);
  
protected statement1: location assign_expr SEMICOLON
					  {## = #(#[AST_statement_assignment, "AST_statement_assignment"], ##);};
protected statement2: method_call SEMICOLON
					  {## = #(#[AST_statement_method_call, "AST_statement_method_call"], ##);};
protected statement3: RESERVED_IF LPAREN expr RPAREN block (RESERVED_ELSE block)*
					  {## = #(#[AST_statement_if, "AST_statement_if"], ##);};
protected statement4: RESERVED_FOR LPAREN ID ASSIGNMENT expr SEMICOLON
					  expr SEMICOLON 
					  location ((compound_assign_op expr) | INCREMENT | DECREMENT) RPAREN block
					  {## = #(#[AST_statement_for, "AST_statement_for"], ##);};
protected statement5: RESERVED_WHILE LPAREN expr RPAREN block
	 				  {## = #(#[AST_statement_while, "AST_statement_while"], ##);};
protected statement6: RESERVED_RETURN (expr)? SEMICOLON
					  {## = #(#[AST_statement_return, "AST_statement_return"], ##);};
protected statement7: RESERVED_BREAK SEMICOLON
					  {## = #(#[AST_statement_break, "AST_statement_break"], ##);};
protected statement8: RESERVED_CONTINUE SEMICOLON
					  {## = #(#[AST_statement_continue, "AST_statement_continue"], ##);};

assign_expr: (INCREMENT | DECREMENT | (assign_op expr))
			 {## = #(#[AST_assign_expr, "AST_assign_expr"], ##);};

assign_op: (ASSIGNMENT | compound_assign_op)
		   {## = #(#[AST_assign_op, "AST_assign_op"], ##);};

compound_assign_op: (PLUSEQUALS | MINUSEQUALS)
		  		    {## = #(#[AST_compound_assign_op, "AST_compound_assign_op"], ##);};

protected method_call: ID method_params
					   {## = #(#[AST_method_call, "AST_method_call"], ##);};

method_params: (LPAREN RPAREN) => LPAREN RPAREN 
			   {## = #(#[AST_method_params_none, "AST_method_params_none"], ##);}
			   |
			   (LPAREN expr (COMMA expr)* RPAREN) => (LPAREN expr (COMMA expr)* RPAREN)
			   {## = #(#[AST_method_params_local, "AST_method_params_local"], ##);}
			   |
			   LPAREN import_arg (COMMA import_arg)* RPAREN
			   {## = #(#[AST_method_params_import, "AST_method_params_import"], ##);};

expr: expr_nonroot
	  {## = #(#[AST_expr, "AST_expr"], ##);};
	  
protected expr_nonroot: expr_t (bin_op expr_nonroot)?;

protected expr_t: (MINUS) => MINUS expr_t |
				  (NOT) => NOT expr_t |
				  (method_call) => method_call |
				  location | literal | len | (LPAREN expr RPAREN);

len: RESERVED_LEN LPAREN ID RPAREN
	{## = #(#[AST_len, "AST_len"], ##);};

import_arg: (expr | STRINGLITERAL)
			{## = #(#[AST_import_arg, "AST_import_arg"], ##);};
			
bin_op: (arith_op | rel_op | eq_op | cond_op)
		{## = #(#[AST_bin_op, "AST_bin_op"], ##);};

arith_op: (PLUS | MINUS | MULT | DIV | PERCENT)
		  {## = #(#[AST_arith_op, "AST_arith_op"], ##);};

rel_op: (LESS | GREATER | LEQ | GEQ)
		{## = #(#[AST_rel_op, "AST_rel_op"], ##);};

eq_op: (EQUALITY | NEQ)
	   {## = #(#[AST_eq_op, "AST_eq_op"], ##);};
	   
cond_op: (ANDAND | OROR)
		 {## = #(#[AST_cond_op, "AST_cond_op"], ##);};

location: (location_array) => location_array |
		  location_noarray;
		  
location_noarray: ID
							{## = #(#[AST_location_noarray, "AST_location_noarray"], ##);};

location_array: ID LBRACKET expr RBRACKET
						  {## = #(#[AST_location_array, "AST_location_array"], ##);};

literal: int_literal | char_literal | bool_literal;

protected char_literal: CHARLITERAL
			  {## = #(#[AST_char_literal, "AST_char_literal"], ##);};

protected bool_literal: (RESERVED_TRUE | RESERVED_FALSE)
			  {## = #(#[AST_bool_literal, "AST_bool_literal"], ##);};

protected int_literal: (DECIMALLITERAL | HEXLITERAL)
			 {## = #(#[AST_int_literal, "AST_int_literal"], ##);};


