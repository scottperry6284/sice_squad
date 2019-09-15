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
IDSTRING: (BOOLLITERAL NOTALETTERORDIGIT) => BOOLLITERAL {$setType(BOOLLITERAL);} |
		  (RESERVED NONLETTER) => RESERVED {$setType(RESERVED);} |
		  ID {$setType(ID);};
protected
BOOLLITERAL: "true"|"false";
protected
RESERVED: "bool"|"break"|"import"|"continue"|"else"|"false"|"for"|"while"|"if"|"int"|"return"|"len"|"true"|"void";
protected
ID: LETTER (LETTER|DIGIT)*;
INTLITERAL: (("0x" (HEXDIGIT)+) | ((DIGIT)+));
OPERATOR: ("+=") => "+=" |
	      ("-=") => "-=" |
		  ('-')+ | ('+')+ | ('/'|'*'|"<="|'<'|"=="|'='|">="|'>'|"!="|"&&"|"||"|'%'|'!'|']'|'['|')'|'('|','|';');

protected
ESC: '\\' ('n'|'\"'|'t'|'\\'|'\'');
protected
CHARINTERNAL: (ESC|~('\''|'\\'|'\"'|'\n'|'\t'));
protected
LETTER: ('a'..'z')|('A'..'Z')|'_';
protected
NONLETTER: ('\n')|('\t')|('\040'..'\100')|('\133'..'\136')|('\140')|('\173'..'\176');
protected
NOTALETTERORDIGIT: ('\n')|('\t')|('\040'..'\057')|('\072'..'\100')|('\133'..'\136')|('\140')|('\173'..'\176');
protected
DIGIT: '0'..'9';
protected
HEXDIGIT: DIGIT | ('a'..'f') | ('A'..'F');
protected
SKIPPABLE: WS_ | SL_COMMENT;
