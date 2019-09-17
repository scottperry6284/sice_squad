header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

{@SuppressWarnings("unchecked")}
class DecafScanner extends Lexer;
options
{
  k = 9;
}

tokens 
{
  "class";
}

// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  /** Whether to display debug information. */
  private boolean trace = false;
  

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws CharStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws CharStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\t' | '\n' {newline();}) {_ttype = Token.SKIP; };
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };

CHARLITERAL : '\'' CHARINTERNAL '\'';
STRINGLITERAL: '"' (CHARINTERNAL)* '"';
DECIMALLITERAL: (DIGIT)+;
HEXLITERAL: "0x" (HEXDIGIT)+;

IDSTRING: (RESERVED_TRUE NOTALETTERORDIGIT) => RESERVED_TRUE {$setType(RESERVED_TRUE);} |
		  (RESERVED_FALSE NOTALETTERORDIGIT) => RESERVED_FALSE {$setType(RESERVED_FALSE);} |
		  (RESERVED_BOOL NOTALETTERORDIGIT) => RESERVED_BOOL {$setType(RESERVED_BOOL);} |
		  (RESERVED_BREAK NOTALETTERORDIGIT) => RESERVED_BREAK {$setType(RESERVED_BREAK);} |
		  (RESERVED_IMPORT NOTALETTERORDIGIT) => RESERVED_IMPORT {$setType(RESERVED_IMPORT);} |
		  (RESERVED_CONTINUE NOTALETTERORDIGIT) => RESERVED_CONTINUE {$setType(RESERVED_CONTINUE);} |
		  (RESERVED_ELSE NOTALETTERORDIGIT) => RESERVED_ELSE {$setType(RESERVED_ELSE);} |
		  (RESERVED_FOR NOTALETTERORDIGIT) => RESERVED_FOR {$setType(RESERVED_FOR);} |
		  (RESERVED_WHILE NOTALETTERORDIGIT) => RESERVED_WHILE {$setType(RESERVED_WHILE);} |
		  (RESERVED_IF NOTALETTERORDIGIT) => RESERVED_IF {$setType(RESERVED_IF);} |
		  (RESERVED_INT NOTALETTERORDIGIT) => RESERVED_INT {$setType(RESERVED_INT);} |
		  (RESERVED_RETURN NOTALETTERORDIGIT) => RESERVED_RETURN {$setType(RESERVED_RETURN);} |
		  (RESERVED_LEN NOTALETTERORDIGIT) => RESERVED_LEN {$setType(RESERVED_LEN);} |
		  (RESERVED_VOID NOTALETTERORDIGIT) => RESERVED_VOID {$setType(RESERVED_VOID);} |
		  ID {$setType(ID);};
protected RESERVED_TRUE: "true";
protected RESERVED_FALSE: "false";
protected RESERVED_BOOL: "bool";
protected RESERVED_BREAK: "break";
protected RESERVED_IMPORT: "import";
protected RESERVED_CONTINUE: "continue";
protected RESERVED_ELSE: "else";
protected RESERVED_FOR: "for";
protected RESERVED_WHILE: "while";
protected RESERVED_IF: "if";
protected RESERVED_INT: "int";
protected RESERVED_RETURN: "return";
protected RESERVED_LEN: "len";
protected RESERVED_VOID: "void";
protected ID: LETTER (LETTER|DIGIT)*;

DIV: "/";
MULT: "*";
PERCENT: '%';
SEMICOLON: ';';
LBRACKET: '[';
RBRACKET: ']';
LPAREN: '(';
RPAREN: ')';
COMMA: ',';
ANDAND: "&&";
OROR: "||";
OPERATOR: (PLUSEQUALS) => PLUSEQUALS {$setType(PLUSEQUALS);} |
		  (MINUSEQUALS) => MINUSEQUALS {$setType(MINUSEQUALS);} |
		  (DECREMENT) => DECREMENT {$setType(DECREMENT);} |
		  (INCREMENT) => INCREMENT {$setType(INCREMENT);} |
		  (LEQ) => LEQ {$setType(LEQ);} |
		  (NEQ) => NEQ {$setType(NEQ);} |
		  (EQUALITY) => EQUALITY {$setType(EQUALITY);} |
		  (GEQ) => GEQ {$setType(GEQ);} |
		  (PLUS) => PLUS {$setType(PLUS);} |
		  (MINUS) => MINUS {$setType(MINUS);} |
		  (LESS) => LESS {$setType(LESS);} |
		  (GREATER) => GREATER {$setType(GREATER);} |
		  (ASSIGNMENT) => ASSIGNMENT {$setType(ASSIGNMENT);} |
		  NOT {$setType(NOT);};
protected PLUSEQUALS: "+=";
protected MINUSEQUALS: "-=";
protected DECREMENT: "--";
protected INCREMENT: "++";
protected PLUS: "+";
protected MINUS: "-";
protected LEQ: "<=";
protected NEQ: "!=";
protected EQUALITY: "==";
protected GEQ: ">=";
protected LESS: "<";
protected GREATER: ">";
protected ASSIGNMENT: "=";
protected NOT: "!";

protected ESC: '\\' ('n'|'\"'|'t'|'\\'|'\'');
protected CHARINTERNAL: (ESC|~('\''|'\\'|'\"'|'\n'|'\t'));
protected LETTER: ('a'..'z')|('A'..'Z')|'_';
protected NONLETTER: ('\n')|('\t')|('\040'..'\100')|('\133'..'\136')|('\140')|('\173'..'\176');
protected NOTALETTERORDIGIT: ('\n')|('\t')|('\040'..'\057')|('\072'..'\100')|('\133'..'\136')|('\140')|('\173'..'\176');
protected DIGIT: '0'..'9';
protected HEXDIGIT: DIGIT | ('a'..'f') | ('A'..'F');
protected SKIPPABLE: WS_ | SL_COMMENT;
protected BADTOKEN: ;
