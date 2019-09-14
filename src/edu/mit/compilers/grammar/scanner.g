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
  k = 2;
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
WS_ : (' ' | '\n' {newline();}) {_ttype = Token.SKIP; };
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };

CHARLITERAL : '\'' CHARINTERNAL '\'';
STRINGLITERAL: '"' (CHARINTERNAL)* '"';
ID options { paraphrase = "an identifier"; } : LETTER (LETTER|DIGIT)*;
INTLITERAL: ('-'|) (("0x" (HEXDIGIT)(HEXDIGIT)*) | ((DIGIT)(DIGIT)*));
OPERATOR: '+'|'-'|'*'|'/';
RESERVED: "bool"|"break"|"import"|"continue"|"else"|"false"|"for"|"while"|"if"|"int"|"return"|"len"|"true"|"void";
BOOLLITERAL: ("true"|"false");

protected
ESC: '\\' ('n'|'\"'|'t'|'\\'|'\'');
protected
CHARINTERNAL: (ESC|~('\''|'\\'|'\"'|'\n'|'\t'));
protected
LETTER: ('a'..'z')|('A'..'Z')|'_';
protected
DIGIT: '0'..'9';
protected
HEXDIGIT: DIGIT | ('a'..'f') | ('A'..'F');
protected
SKIPPABLE: WS_ | SL_COMMENT;
