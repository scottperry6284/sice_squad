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

program: (import_decl)* (field_decl)* (method_decl)* EOF;

import_decl: RESERVED_IMPORT ID SEMICOLON;
field_decl: type field_decl_inner (COMMA field_decl_inner)* SEMICOLON;
protected field_decl_inner: (ID (LBRACKET int_literal RBRACKET)?);
method_decl: (RESERVED_VOID | type) ID LPAREN (method_param (COMMA method_param)*)? RPAREN block;
protected method_param: type ID;
block: LCURLY (field_decl)* (statement)* RCURLY;
type: RESERVED_INT | RESERVED_BOOL;

statement: (statement1 | statement2 | statement3 | statement4 | statement5 | statement6 | statement7 | statement8);
protected statement1: location assign_expr SEMICOLON;
protected statement2: method_call SEMICOLON;
protected statement3: RESERVED_IF LPAREN expr RPAREN block (RESERVED_ELSE block)*;
protected statement4: RESERVED_FOR LPAREN ID ASSIGNMENT expr SEMICOLON
					  expr SEMICOLON 
					  location ((compound_assign_op expr) | INCREMENT | DECREMENT) RPAREN block;
protected statement5: RESERVED_WHILE LPAREN expr RPAREN block;		 
protected statement6: RESERVED_RETURN expr SEMICOLON;
protected statement7: RESERVED_BREAK SEMICOLON;
protected statement8: RESERVED_CONTINUE SEMICOLON;

assign_expr: INCREMENT | DECREMENT | (assign_op expr);

assign_op: ASSIGNMENT | compound_assign_op;

compound_assign_op: PLUSEQUALS | MINUSEQUALS;

method_call: ID method_params;

method_params: (LPAREN RPAREN) => LPAREN RPAREN |
			   (LPAREN expr (COMMA expr)* RPAREN) => (LPAREN expr (COMMA expr)* RPAREN) |
			   LPAREN import_arg (COMMA import_arg)* RPAREN;

expr: expr_t (bin_op expr)?;
protected expr_t: (MINUS) => MINUS expr_t |
				  (NOT) => NOT expr_t |
				  (method_call) => method_call |
				  location | literal | (RESERVED_LEN ID) | (LPAREN expr RPAREN);

import_arg: expr | STRINGLITERAL; 
bin_op: arith_op | rel_op | eq_op | cond_op;
arith_op: PLUS | MINUS | MULT | DIV | PERCENT;
rel_op: LESS | GREATER | LEQ | GEQ;
eq_op: EQUALITY | NEQ;
cond_op: ANDAND | OROR;

location: (location_array) => location_array |
		  location_noarray;
protected location_noarray: ID;
protected location_array: ID LBRACKET expr RBRACKET;

literal: int_literal | CHARLITERAL | bool_literal;

bool_literal: RESERVED_TRUE | RESERVED_FALSE;

int_literal: DECIMALLITERAL | HEXLITERAL;


