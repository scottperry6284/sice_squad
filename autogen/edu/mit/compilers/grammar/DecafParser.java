// $ANTLR 2.7.7 (2006-11-01): "parser.g" -> "DecafParser.java"$

package edu.mit.compilers.grammar;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class DecafParser extends antlr.LLkParser       implements DecafParserTokenTypes
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

protected DecafParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DecafParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected DecafParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DecafParser(TokenStream lexer) {
  this(lexer,3);
}

public DecafParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		traceIn("program");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST program_AST = null;
			
			try {      // for error handling
				{
				_loop3:
				do {
					if ((LA(1)==RESERVED_IMPORT)) {
						import_decl();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop3;
					}
					
				} while (true);
				}
				{
				_loop5:
				do {
					if ((LA(1)==RESERVED_BOOL||LA(1)==RESERVED_INT) && (LA(2)==ID) && (LA(3)==SEMICOLON||LA(3)==LBRACKET||LA(3)==COMMA)) {
						field_decl();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop5;
					}
					
				} while (true);
				}
				{
				_loop7:
				do {
					if ((LA(1)==RESERVED_BOOL||LA(1)==RESERVED_INT||LA(1)==RESERVED_VOID)) {
						method_decl();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop7;
					}
					
				} while (true);
				}
				AST tmp1_AST = null;
				tmp1_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp1_AST);
				match(Token.EOF_TYPE);
				if ( inputState.guessing==0 ) {
					program_AST = (AST)currentAST.root;
					program_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_program,"program")).add(program_AST));
					currentAST.root = program_AST;
					currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
						program_AST.getFirstChild() : program_AST;
					currentAST.advanceChildToEnd();
				}
				program_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_0);
				} else {
				  throw ex;
				}
			}
			returnAST = program_AST;
		} finally { // debugging
			traceOut("program");
		}
	}
	
	public final void import_decl() throws RecognitionException, TokenStreamException {
		
		traceIn("import_decl");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST import_decl_AST = null;
			
			try {      // for error handling
				AST tmp2_AST = null;
				tmp2_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp2_AST);
				match(RESERVED_IMPORT);
				AST tmp3_AST = null;
				tmp3_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp3_AST);
				match(ID);
				AST tmp4_AST = null;
				tmp4_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp4_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					import_decl_AST = (AST)currentAST.root;
					import_decl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_import_decl,"import_decl")).add(import_decl_AST));
					currentAST.root = import_decl_AST;
					currentAST.child = import_decl_AST!=null &&import_decl_AST.getFirstChild()!=null ?
						import_decl_AST.getFirstChild() : import_decl_AST;
					currentAST.advanceChildToEnd();
				}
				import_decl_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_1);
				} else {
				  throw ex;
				}
			}
			returnAST = import_decl_AST;
		} finally { // debugging
			traceOut("import_decl");
		}
	}
	
	public final void field_decl() throws RecognitionException, TokenStreamException {
		
		traceIn("field_decl");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST field_decl_AST = null;
			
			try {      // for error handling
				type();
				astFactory.addASTChild(currentAST, returnAST);
				field_decl_inner();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop11:
				do {
					if ((LA(1)==COMMA)) {
						AST tmp5_AST = null;
						tmp5_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp5_AST);
						match(COMMA);
						field_decl_inner();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop11;
					}
					
				} while (true);
				}
				AST tmp6_AST = null;
				tmp6_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp6_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					field_decl_AST = (AST)currentAST.root;
					field_decl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_field_decl,"field_decl")).add(field_decl_AST));
					currentAST.root = field_decl_AST;
					currentAST.child = field_decl_AST!=null &&field_decl_AST.getFirstChild()!=null ?
						field_decl_AST.getFirstChild() : field_decl_AST;
					currentAST.advanceChildToEnd();
				}
				field_decl_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_2);
				} else {
				  throw ex;
				}
			}
			returnAST = field_decl_AST;
		} finally { // debugging
			traceOut("field_decl");
		}
	}
	
	public final void method_decl() throws RecognitionException, TokenStreamException {
		
		traceIn("method_decl");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_decl_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case RESERVED_VOID:
				{
					AST tmp7_AST = null;
					tmp7_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp7_AST);
					match(RESERVED_VOID);
					break;
				}
				case RESERVED_BOOL:
				case RESERVED_INT:
				{
					type();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp8_AST = null;
				tmp8_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp8_AST);
				match(ID);
				AST tmp9_AST = null;
				tmp9_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp9_AST);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case RESERVED_BOOL:
				case RESERVED_INT:
				{
					method_param();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop19:
					do {
						if ((LA(1)==COMMA)) {
							AST tmp10_AST = null;
							tmp10_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp10_AST);
							match(COMMA);
							method_param();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop19;
						}
						
					} while (true);
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp11_AST = null;
				tmp11_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp11_AST);
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					method_decl_AST = (AST)currentAST.root;
					method_decl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_decl,"method_decl")).add(method_decl_AST));
					currentAST.root = method_decl_AST;
					currentAST.child = method_decl_AST!=null &&method_decl_AST.getFirstChild()!=null ?
						method_decl_AST.getFirstChild() : method_decl_AST;
					currentAST.advanceChildToEnd();
				}
				method_decl_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_3);
				} else {
				  throw ex;
				}
			}
			returnAST = method_decl_AST;
		} finally { // debugging
			traceOut("method_decl");
		}
	}
	
	public final void type() throws RecognitionException, TokenStreamException {
		
		traceIn("type");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST type_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case RESERVED_INT:
				{
					AST tmp12_AST = null;
					tmp12_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp12_AST);
					match(RESERVED_INT);
					type_AST = (AST)currentAST.root;
					break;
				}
				case RESERVED_BOOL:
				{
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp13_AST);
					match(RESERVED_BOOL);
					if ( inputState.guessing==0 ) {
						type_AST = (AST)currentAST.root;
						type_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_type,"type")).add(type_AST));
						currentAST.root = type_AST;
						currentAST.child = type_AST!=null &&type_AST.getFirstChild()!=null ?
							type_AST.getFirstChild() : type_AST;
						currentAST.advanceChildToEnd();
					}
					type_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_4);
				} else {
				  throw ex;
				}
			}
			returnAST = type_AST;
		} finally { // debugging
			traceOut("type");
		}
	}
	
	protected final void field_decl_inner() throws RecognitionException, TokenStreamException {
		
		traceIn("field_decl_inner");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST field_decl_inner_AST = null;
			
			try {      // for error handling
				{
				AST tmp14_AST = null;
				tmp14_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp14_AST);
				match(ID);
				{
				switch ( LA(1)) {
				case LBRACKET:
				{
					AST tmp15_AST = null;
					tmp15_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp15_AST);
					match(LBRACKET);
					int_literal();
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp16_AST = null;
					tmp16_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp16_AST);
					match(RBRACKET);
					break;
				}
				case SEMICOLON:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				}
				field_decl_inner_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_5);
				} else {
				  throw ex;
				}
			}
			returnAST = field_decl_inner_AST;
		} finally { // debugging
			traceOut("field_decl_inner");
		}
	}
	
	protected final void int_literal() throws RecognitionException, TokenStreamException {
		
		traceIn("int_literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST int_literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case DECIMALLITERAL:
				{
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp17_AST);
					match(DECIMALLITERAL);
					int_literal_AST = (AST)currentAST.root;
					break;
				}
				case HEXLITERAL:
				{
					AST tmp18_AST = null;
					tmp18_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp18_AST);
					match(HEXLITERAL);
					if ( inputState.guessing==0 ) {
						int_literal_AST = (AST)currentAST.root;
						int_literal_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_int_literal,"AST_int_literal")).add(int_literal_AST));
						currentAST.root = int_literal_AST;
						currentAST.child = int_literal_AST!=null &&int_literal_AST.getFirstChild()!=null ?
							int_literal_AST.getFirstChild() : int_literal_AST;
						currentAST.advanceChildToEnd();
					}
					int_literal_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = int_literal_AST;
		} finally { // debugging
			traceOut("int_literal");
		}
	}
	
	protected final void method_param() throws RecognitionException, TokenStreamException {
		
		traceIn("method_param");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_param_AST = null;
			
			try {      // for error handling
				type();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp19_AST);
				match(ID);
				if ( inputState.guessing==0 ) {
					method_param_AST = (AST)currentAST.root;
					method_param_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_param,"method_param")).add(method_param_AST));
					currentAST.root = method_param_AST;
					currentAST.child = method_param_AST!=null &&method_param_AST.getFirstChild()!=null ?
						method_param_AST.getFirstChild() : method_param_AST;
					currentAST.advanceChildToEnd();
				}
				method_param_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_7);
				} else {
				  throw ex;
				}
			}
			returnAST = method_param_AST;
		} finally { // debugging
			traceOut("method_param");
		}
	}
	
	public final void block() throws RecognitionException, TokenStreamException {
		
		traceIn("block");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST block_AST = null;
			
			try {      // for error handling
				AST tmp20_AST = null;
				tmp20_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp20_AST);
				match(LCURLY);
				{
				_loop23:
				do {
					if ((LA(1)==RESERVED_BOOL||LA(1)==RESERVED_INT)) {
						field_decl();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop23;
					}
					
				} while (true);
				}
				{
				_loop25:
				do {
					if ((_tokenSet_8.member(LA(1)))) {
						statement();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop25;
					}
					
				} while (true);
				}
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp21_AST);
				match(RCURLY);
				if ( inputState.guessing==0 ) {
					block_AST = (AST)currentAST.root;
					block_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_block,"block")).add(block_AST));
					currentAST.root = block_AST;
					currentAST.child = block_AST!=null &&block_AST.getFirstChild()!=null ?
						block_AST.getFirstChild() : block_AST;
					currentAST.advanceChildToEnd();
				}
				block_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_9);
				} else {
				  throw ex;
				}
			}
			returnAST = block_AST;
		} finally { // debugging
			traceOut("block");
		}
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		traceIn("statement");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case RESERVED_IF:
				{
					statement3();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RESERVED_FOR:
				{
					statement4();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RESERVED_WHILE:
				{
					statement5();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RESERVED_RETURN:
				{
					statement6();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RESERVED_BREAK:
				{
					statement7();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RESERVED_CONTINUE:
				{
					statement8();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
					if ((LA(1)==ID) && (_tokenSet_10.member(LA(2)))) {
						statement1();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((LA(1)==ID) && (LA(2)==LPAREN)) {
						statement2();
						astFactory.addASTChild(currentAST, returnAST);
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				statement_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement_AST;
		} finally { // debugging
			traceOut("statement");
		}
	}
	
	protected final void statement1() throws RecognitionException, TokenStreamException {
		
		traceIn("statement1");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement1_AST = null;
			
			try {      // for error handling
				location();
				astFactory.addASTChild(currentAST, returnAST);
				assign_expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp22_AST = null;
				tmp22_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp22_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					statement1_AST = (AST)currentAST.root;
					statement1_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_assignment,"AST_statement_assignment")).add(statement1_AST));
					currentAST.root = statement1_AST;
					currentAST.child = statement1_AST!=null &&statement1_AST.getFirstChild()!=null ?
						statement1_AST.getFirstChild() : statement1_AST;
					currentAST.advanceChildToEnd();
				}
				statement1_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement1_AST;
		} finally { // debugging
			traceOut("statement1");
		}
	}
	
	protected final void statement2() throws RecognitionException, TokenStreamException {
		
		traceIn("statement2");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement2_AST = null;
			
			try {      // for error handling
				method_call();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp23_AST = null;
				tmp23_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp23_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					statement2_AST = (AST)currentAST.root;
					statement2_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_method_call,"AST_statement_method_call")).add(statement2_AST));
					currentAST.root = statement2_AST;
					currentAST.child = statement2_AST!=null &&statement2_AST.getFirstChild()!=null ?
						statement2_AST.getFirstChild() : statement2_AST;
					currentAST.advanceChildToEnd();
				}
				statement2_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement2_AST;
		} finally { // debugging
			traceOut("statement2");
		}
	}
	
	protected final void statement3() throws RecognitionException, TokenStreamException {
		
		traceIn("statement3");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement3_AST = null;
			
			try {      // for error handling
				AST tmp24_AST = null;
				tmp24_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp24_AST);
				match(RESERVED_IF);
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(LPAREN);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp26_AST);
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop33:
				do {
					if ((LA(1)==RESERVED_ELSE)) {
						AST tmp27_AST = null;
						tmp27_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp27_AST);
						match(RESERVED_ELSE);
						block();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop33;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					statement3_AST = (AST)currentAST.root;
					statement3_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_if,"AST_statement_if")).add(statement3_AST));
					currentAST.root = statement3_AST;
					currentAST.child = statement3_AST!=null &&statement3_AST.getFirstChild()!=null ?
						statement3_AST.getFirstChild() : statement3_AST;
					currentAST.advanceChildToEnd();
				}
				statement3_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement3_AST;
		} finally { // debugging
			traceOut("statement3");
		}
	}
	
	protected final void statement4() throws RecognitionException, TokenStreamException {
		
		traceIn("statement4");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement4_AST = null;
			
			try {      // for error handling
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp28_AST);
				match(RESERVED_FOR);
				AST tmp29_AST = null;
				tmp29_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp29_AST);
				match(LPAREN);
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp30_AST);
				match(ID);
				AST tmp31_AST = null;
				tmp31_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp31_AST);
				match(ASSIGNMENT);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp32_AST);
				match(SEMICOLON);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp33_AST);
				match(SEMICOLON);
				location();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case PLUSEQUALS:
				case MINUSEQUALS:
				{
					{
					compound_assign_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case INCREMENT:
				{
					AST tmp34_AST = null;
					tmp34_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp34_AST);
					match(INCREMENT);
					break;
				}
				case DECREMENT:
				{
					AST tmp35_AST = null;
					tmp35_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp35_AST);
					match(DECREMENT);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp36_AST = null;
				tmp36_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp36_AST);
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					statement4_AST = (AST)currentAST.root;
					statement4_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_for,"AST_statement_for")).add(statement4_AST));
					currentAST.root = statement4_AST;
					currentAST.child = statement4_AST!=null &&statement4_AST.getFirstChild()!=null ?
						statement4_AST.getFirstChild() : statement4_AST;
					currentAST.advanceChildToEnd();
				}
				statement4_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement4_AST;
		} finally { // debugging
			traceOut("statement4");
		}
	}
	
	protected final void statement5() throws RecognitionException, TokenStreamException {
		
		traceIn("statement5");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement5_AST = null;
			
			try {      // for error handling
				AST tmp37_AST = null;
				tmp37_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp37_AST);
				match(RESERVED_WHILE);
				AST tmp38_AST = null;
				tmp38_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp38_AST);
				match(LPAREN);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp39_AST = null;
				tmp39_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp39_AST);
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					statement5_AST = (AST)currentAST.root;
					statement5_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_while,"AST_statement_while")).add(statement5_AST));
					currentAST.root = statement5_AST;
					currentAST.child = statement5_AST!=null &&statement5_AST.getFirstChild()!=null ?
						statement5_AST.getFirstChild() : statement5_AST;
					currentAST.advanceChildToEnd();
				}
				statement5_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement5_AST;
		} finally { // debugging
			traceOut("statement5");
		}
	}
	
	protected final void statement6() throws RecognitionException, TokenStreamException {
		
		traceIn("statement6");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement6_AST = null;
			
			try {      // for error handling
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp40_AST);
				match(RESERVED_RETURN);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp41_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					statement6_AST = (AST)currentAST.root;
					statement6_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_return,"AST_statement_return")).add(statement6_AST));
					currentAST.root = statement6_AST;
					currentAST.child = statement6_AST!=null &&statement6_AST.getFirstChild()!=null ?
						statement6_AST.getFirstChild() : statement6_AST;
					currentAST.advanceChildToEnd();
				}
				statement6_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement6_AST;
		} finally { // debugging
			traceOut("statement6");
		}
	}
	
	protected final void statement7() throws RecognitionException, TokenStreamException {
		
		traceIn("statement7");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement7_AST = null;
			
			try {      // for error handling
				AST tmp42_AST = null;
				tmp42_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp42_AST);
				match(RESERVED_BREAK);
				AST tmp43_AST = null;
				tmp43_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp43_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					statement7_AST = (AST)currentAST.root;
					statement7_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_break,"AST_statement_break")).add(statement7_AST));
					currentAST.root = statement7_AST;
					currentAST.child = statement7_AST!=null &&statement7_AST.getFirstChild()!=null ?
						statement7_AST.getFirstChild() : statement7_AST;
					currentAST.advanceChildToEnd();
				}
				statement7_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement7_AST;
		} finally { // debugging
			traceOut("statement7");
		}
	}
	
	protected final void statement8() throws RecognitionException, TokenStreamException {
		
		traceIn("statement8");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement8_AST = null;
			
			try {      // for error handling
				AST tmp44_AST = null;
				tmp44_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp44_AST);
				match(RESERVED_CONTINUE);
				AST tmp45_AST = null;
				tmp45_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp45_AST);
				match(SEMICOLON);
				if ( inputState.guessing==0 ) {
					statement8_AST = (AST)currentAST.root;
					statement8_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_statement_continue,"AST_statement_continue")).add(statement8_AST));
					currentAST.root = statement8_AST;
					currentAST.child = statement8_AST!=null &&statement8_AST.getFirstChild()!=null ?
						statement8_AST.getFirstChild() : statement8_AST;
					currentAST.advanceChildToEnd();
				}
				statement8_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_11);
				} else {
				  throw ex;
				}
			}
			returnAST = statement8_AST;
		} finally { // debugging
			traceOut("statement8");
		}
	}
	
	public final void location() throws RecognitionException, TokenStreamException {
		
		traceIn("location");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST location_AST = null;
			
			try {      // for error handling
				boolean synPredMatched77 = false;
				if (((LA(1)==ID) && (LA(2)==LBRACKET))) {
					int _m77 = mark();
					synPredMatched77 = true;
					inputState.guessing++;
					try {
						{
						location_array();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched77 = false;
					}
					rewind(_m77);
inputState.guessing--;
				}
				if ( synPredMatched77 ) {
					location_array();
					astFactory.addASTChild(currentAST, returnAST);
					location_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (_tokenSet_12.member(LA(2)))) {
					location_noarray();
					astFactory.addASTChild(currentAST, returnAST);
					location_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_12);
				} else {
				  throw ex;
				}
			}
			returnAST = location_AST;
		} finally { // debugging
			traceOut("location");
		}
	}
	
	public final void assign_expr() throws RecognitionException, TokenStreamException {
		
		traceIn("assign_expr");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST assign_expr_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case INCREMENT:
				{
					AST tmp46_AST = null;
					tmp46_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp46_AST);
					match(INCREMENT);
					assign_expr_AST = (AST)currentAST.root;
					break;
				}
				case DECREMENT:
				{
					AST tmp47_AST = null;
					tmp47_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp47_AST);
					match(DECREMENT);
					assign_expr_AST = (AST)currentAST.root;
					break;
				}
				case PLUSEQUALS:
				case MINUSEQUALS:
				case ASSIGNMENT:
				{
					{
					assign_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					}
					if ( inputState.guessing==0 ) {
						assign_expr_AST = (AST)currentAST.root;
						assign_expr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_assign_expr,"AST_assign_expr")).add(assign_expr_AST));
						currentAST.root = assign_expr_AST;
						currentAST.child = assign_expr_AST!=null &&assign_expr_AST.getFirstChild()!=null ?
							assign_expr_AST.getFirstChild() : assign_expr_AST;
						currentAST.advanceChildToEnd();
					}
					assign_expr_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_13);
				} else {
				  throw ex;
				}
			}
			returnAST = assign_expr_AST;
		} finally { // debugging
			traceOut("assign_expr");
		}
	}
	
	public final void method_call() throws RecognitionException, TokenStreamException {
		
		traceIn("method_call");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_call_AST = null;
			
			try {      // for error handling
				AST tmp48_AST = null;
				tmp48_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp48_AST);
				match(ID);
				method_params();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					method_call_AST = (AST)currentAST.root;
					method_call_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_call,"AST_method_call")).add(method_call_AST));
					currentAST.root = method_call_AST;
					currentAST.child = method_call_AST!=null &&method_call_AST.getFirstChild()!=null ?
						method_call_AST.getFirstChild() : method_call_AST;
					currentAST.advanceChildToEnd();
				}
				method_call_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = method_call_AST;
		} finally { // debugging
			traceOut("method_call");
		}
	}
	
	public final void expr() throws RecognitionException, TokenStreamException {
		
		traceIn("expr");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_AST = null;
			
			try {      // for error handling
				expr_t();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case DIV:
				case MULT:
				case PERCENT:
				case ANDAND:
				case OROR:
				case PLUS:
				case MINUS:
				case LEQ:
				case NEQ:
				case EQUALITY:
				case GEQ:
				case LESS:
				case GREATER:
				{
					bin_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case SEMICOLON:
				case RBRACKET:
				case RPAREN:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					expr_AST = (AST)currentAST.root;
					expr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_expr,"AST_expr")).add(expr_AST));
					currentAST.root = expr_AST;
					currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
						expr_AST.getFirstChild() : expr_AST;
					currentAST.advanceChildToEnd();
				}
				expr_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_14);
				} else {
				  throw ex;
				}
			}
			returnAST = expr_AST;
		} finally { // debugging
			traceOut("expr");
		}
	}
	
	public final void compound_assign_op() throws RecognitionException, TokenStreamException {
		
		traceIn("compound_assign_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST compound_assign_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case PLUSEQUALS:
				{
					AST tmp49_AST = null;
					tmp49_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp49_AST);
					match(PLUSEQUALS);
					compound_assign_op_AST = (AST)currentAST.root;
					break;
				}
				case MINUSEQUALS:
				{
					AST tmp50_AST = null;
					tmp50_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp50_AST);
					match(MINUSEQUALS);
					if ( inputState.guessing==0 ) {
						compound_assign_op_AST = (AST)currentAST.root;
						compound_assign_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_compound_assign_op,"AST_compound_assign_op")).add(compound_assign_op_AST));
						currentAST.root = compound_assign_op_AST;
						currentAST.child = compound_assign_op_AST!=null &&compound_assign_op_AST.getFirstChild()!=null ?
							compound_assign_op_AST.getFirstChild() : compound_assign_op_AST;
						currentAST.advanceChildToEnd();
					}
					compound_assign_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = compound_assign_op_AST;
		} finally { // debugging
			traceOut("compound_assign_op");
		}
	}
	
	public final void assign_op() throws RecognitionException, TokenStreamException {
		
		traceIn("assign_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST assign_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case ASSIGNMENT:
				{
					AST tmp51_AST = null;
					tmp51_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp51_AST);
					match(ASSIGNMENT);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				case PLUSEQUALS:
				case MINUSEQUALS:
				{
					compound_assign_op();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						assign_op_AST = (AST)currentAST.root;
						assign_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_assign_op,"AST_assign_op")).add(assign_op_AST));
						currentAST.root = assign_op_AST;
						currentAST.child = assign_op_AST!=null &&assign_op_AST.getFirstChild()!=null ?
							assign_op_AST.getFirstChild() : assign_op_AST;
						currentAST.advanceChildToEnd();
					}
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = assign_op_AST;
		} finally { // debugging
			traceOut("assign_op");
		}
	}
	
	public final void method_params() throws RecognitionException, TokenStreamException {
		
		traceIn("method_params");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_params_AST = null;
			
			try {      // for error handling
				boolean synPredMatched48 = false;
				if (((LA(1)==LPAREN) && (LA(2)==RPAREN))) {
					int _m48 = mark();
					synPredMatched48 = true;
					inputState.guessing++;
					try {
						{
						match(LPAREN);
						match(RPAREN);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched48 = false;
					}
					rewind(_m48);
inputState.guessing--;
				}
				if ( synPredMatched48 ) {
					AST tmp52_AST = null;
					tmp52_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp52_AST);
					match(LPAREN);
					AST tmp53_AST = null;
					tmp53_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp53_AST);
					match(RPAREN);
					if ( inputState.guessing==0 ) {
						method_params_AST = (AST)currentAST.root;
						method_params_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_params_none,"AST_method_params_none")).add(method_params_AST));
						currentAST.root = method_params_AST;
						currentAST.child = method_params_AST!=null &&method_params_AST.getFirstChild()!=null ?
							method_params_AST.getFirstChild() : method_params_AST;
						currentAST.advanceChildToEnd();
					}
					method_params_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched52 = false;
					if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))) && (_tokenSet_16.member(LA(3))))) {
						int _m52 = mark();
						synPredMatched52 = true;
						inputState.guessing++;
						try {
							{
							match(LPAREN);
							expr();
							{
							_loop51:
							do {
								if ((LA(1)==COMMA)) {
									match(COMMA);
									expr();
								}
								else {
									break _loop51;
								}
								
							} while (true);
							}
							match(RPAREN);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched52 = false;
						}
						rewind(_m52);
inputState.guessing--;
					}
					if ( synPredMatched52 ) {
						{
						AST tmp54_AST = null;
						tmp54_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp54_AST);
						match(LPAREN);
						expr();
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop55:
						do {
							if ((LA(1)==COMMA)) {
								AST tmp55_AST = null;
								tmp55_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp55_AST);
								match(COMMA);
								expr();
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop55;
							}
							
						} while (true);
						}
						AST tmp56_AST = null;
						tmp56_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp56_AST);
						match(RPAREN);
						}
						if ( inputState.guessing==0 ) {
							method_params_AST = (AST)currentAST.root;
							method_params_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_params_local,"AST_method_params_local")).add(method_params_AST));
							currentAST.root = method_params_AST;
							currentAST.child = method_params_AST!=null &&method_params_AST.getFirstChild()!=null ?
								method_params_AST.getFirstChild() : method_params_AST;
							currentAST.advanceChildToEnd();
						}
						method_params_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==LPAREN) && (_tokenSet_17.member(LA(2))) && (_tokenSet_16.member(LA(3)))) {
						AST tmp57_AST = null;
						tmp57_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp57_AST);
						match(LPAREN);
						import_arg();
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop57:
						do {
							if ((LA(1)==COMMA)) {
								AST tmp58_AST = null;
								tmp58_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp58_AST);
								match(COMMA);
								import_arg();
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop57;
							}
							
						} while (true);
						}
						AST tmp59_AST = null;
						tmp59_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp59_AST);
						match(RPAREN);
						if ( inputState.guessing==0 ) {
							method_params_AST = (AST)currentAST.root;
							method_params_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_method_params_import,"AST_method_params_import")).add(method_params_AST));
							currentAST.root = method_params_AST;
							currentAST.child = method_params_AST!=null &&method_params_AST.getFirstChild()!=null ?
								method_params_AST.getFirstChild() : method_params_AST;
							currentAST.advanceChildToEnd();
						}
						method_params_AST = (AST)currentAST.root;
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
				}
				catch (RecognitionException ex) {
					if (inputState.guessing==0) {
						reportError(ex);
						recover(ex,_tokenSet_6);
					} else {
					  throw ex;
					}
				}
				returnAST = method_params_AST;
			} finally { // debugging
				traceOut("method_params");
			}
		}
		
	public final void import_arg() throws RecognitionException, TokenStreamException {
		
		traceIn("import_arg");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST import_arg_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case CHARLITERAL:
				case DECIMALLITERAL:
				case HEXLITERAL:
				case RESERVED_TRUE:
				case RESERVED_FALSE:
				case RESERVED_LEN:
				case ID:
				case LPAREN:
				case MINUS:
				case NOT:
				{
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					import_arg_AST = (AST)currentAST.root;
					break;
				}
				case STRINGLITERAL:
				{
					AST tmp60_AST = null;
					tmp60_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp60_AST);
					match(STRINGLITERAL);
					if ( inputState.guessing==0 ) {
						import_arg_AST = (AST)currentAST.root;
						import_arg_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_import_arg,"AST_import_arg")).add(import_arg_AST));
						currentAST.root = import_arg_AST;
						currentAST.child = import_arg_AST!=null &&import_arg_AST.getFirstChild()!=null ?
							import_arg_AST.getFirstChild() : import_arg_AST;
						currentAST.advanceChildToEnd();
					}
					import_arg_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_7);
				} else {
				  throw ex;
				}
			}
			returnAST = import_arg_AST;
		} finally { // debugging
			traceOut("import_arg");
		}
	}
	
	protected final void expr_t() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_t");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_t_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case MINUS:
				{
					AST tmp61_AST = null;
					tmp61_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp61_AST);
					match(MINUS);
					expr_t();
					astFactory.addASTChild(currentAST, returnAST);
					expr_t_AST = (AST)currentAST.root;
					break;
				}
				case NOT:
				{
					AST tmp62_AST = null;
					tmp62_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp62_AST);
					match(NOT);
					expr_t();
					astFactory.addASTChild(currentAST, returnAST);
					expr_t_AST = (AST)currentAST.root;
					break;
				}
				case CHARLITERAL:
				case DECIMALLITERAL:
				case HEXLITERAL:
				case RESERVED_TRUE:
				case RESERVED_FALSE:
				{
					literal();
					astFactory.addASTChild(currentAST, returnAST);
					expr_t_AST = (AST)currentAST.root;
					break;
				}
				case RESERVED_LEN:
				{
					{
					AST tmp63_AST = null;
					tmp63_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp63_AST);
					match(RESERVED_LEN);
					AST tmp64_AST = null;
					tmp64_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp64_AST);
					match(ID);
					}
					expr_t_AST = (AST)currentAST.root;
					break;
				}
				case LPAREN:
				{
					{
					AST tmp65_AST = null;
					tmp65_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp65_AST);
					match(LPAREN);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp66_AST = null;
					tmp66_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp66_AST);
					match(RPAREN);
					}
					expr_t_AST = (AST)currentAST.root;
					break;
				}
				default:
					boolean synPredMatched66 = false;
					if (((LA(1)==ID) && (LA(2)==LPAREN))) {
						int _m66 = mark();
						synPredMatched66 = true;
						inputState.guessing++;
						try {
							{
							method_call();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched66 = false;
						}
						rewind(_m66);
inputState.guessing--;
					}
					if ( synPredMatched66 ) {
						method_call();
						astFactory.addASTChild(currentAST, returnAST);
						expr_t_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==ID) && (_tokenSet_18.member(LA(2)))) {
						location();
						astFactory.addASTChild(currentAST, returnAST);
						expr_t_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = expr_t_AST;
		} finally { // debugging
			traceOut("expr_t");
		}
	}
	
	public final void bin_op() throws RecognitionException, TokenStreamException {
		
		traceIn("bin_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST bin_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case DIV:
				case MULT:
				case PERCENT:
				case PLUS:
				case MINUS:
				{
					arith_op();
					astFactory.addASTChild(currentAST, returnAST);
					bin_op_AST = (AST)currentAST.root;
					break;
				}
				case LEQ:
				case GEQ:
				case LESS:
				case GREATER:
				{
					rel_op();
					astFactory.addASTChild(currentAST, returnAST);
					bin_op_AST = (AST)currentAST.root;
					break;
				}
				case NEQ:
				case EQUALITY:
				{
					eq_op();
					astFactory.addASTChild(currentAST, returnAST);
					bin_op_AST = (AST)currentAST.root;
					break;
				}
				case ANDAND:
				case OROR:
				{
					cond_op();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						bin_op_AST = (AST)currentAST.root;
						bin_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_bin_op,"AST_bin_op")).add(bin_op_AST));
						currentAST.root = bin_op_AST;
						currentAST.child = bin_op_AST!=null &&bin_op_AST.getFirstChild()!=null ?
							bin_op_AST.getFirstChild() : bin_op_AST;
						currentAST.advanceChildToEnd();
					}
					bin_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = bin_op_AST;
		} finally { // debugging
			traceOut("bin_op");
		}
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		traceIn("literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case DECIMALLITERAL:
				case HEXLITERAL:
				{
					int_literal();
					astFactory.addASTChild(currentAST, returnAST);
					literal_AST = (AST)currentAST.root;
					break;
				}
				case CHARLITERAL:
				{
					char_literal();
					astFactory.addASTChild(currentAST, returnAST);
					literal_AST = (AST)currentAST.root;
					break;
				}
				case RESERVED_TRUE:
				case RESERVED_FALSE:
				{
					bool_literal();
					astFactory.addASTChild(currentAST, returnAST);
					literal_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = literal_AST;
		} finally { // debugging
			traceOut("literal");
		}
	}
	
	public final void arith_op() throws RecognitionException, TokenStreamException {
		
		traceIn("arith_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST arith_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp67_AST = null;
					tmp67_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp67_AST);
					match(PLUS);
					arith_op_AST = (AST)currentAST.root;
					break;
				}
				case MINUS:
				{
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp68_AST);
					match(MINUS);
					arith_op_AST = (AST)currentAST.root;
					break;
				}
				case MULT:
				{
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp69_AST);
					match(MULT);
					arith_op_AST = (AST)currentAST.root;
					break;
				}
				case DIV:
				{
					AST tmp70_AST = null;
					tmp70_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp70_AST);
					match(DIV);
					arith_op_AST = (AST)currentAST.root;
					break;
				}
				case PERCENT:
				{
					AST tmp71_AST = null;
					tmp71_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp71_AST);
					match(PERCENT);
					if ( inputState.guessing==0 ) {
						arith_op_AST = (AST)currentAST.root;
						arith_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_arith_op,"AST_arith_op")).add(arith_op_AST));
						currentAST.root = arith_op_AST;
						currentAST.child = arith_op_AST!=null &&arith_op_AST.getFirstChild()!=null ?
							arith_op_AST.getFirstChild() : arith_op_AST;
						currentAST.advanceChildToEnd();
					}
					arith_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = arith_op_AST;
		} finally { // debugging
			traceOut("arith_op");
		}
	}
	
	public final void rel_op() throws RecognitionException, TokenStreamException {
		
		traceIn("rel_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST rel_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case LESS:
				{
					AST tmp72_AST = null;
					tmp72_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp72_AST);
					match(LESS);
					rel_op_AST = (AST)currentAST.root;
					break;
				}
				case GREATER:
				{
					AST tmp73_AST = null;
					tmp73_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp73_AST);
					match(GREATER);
					rel_op_AST = (AST)currentAST.root;
					break;
				}
				case LEQ:
				{
					AST tmp74_AST = null;
					tmp74_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp74_AST);
					match(LEQ);
					rel_op_AST = (AST)currentAST.root;
					break;
				}
				case GEQ:
				{
					AST tmp75_AST = null;
					tmp75_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp75_AST);
					match(GEQ);
					if ( inputState.guessing==0 ) {
						rel_op_AST = (AST)currentAST.root;
						rel_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_rel_op,"AST_rel_op")).add(rel_op_AST));
						currentAST.root = rel_op_AST;
						currentAST.child = rel_op_AST!=null &&rel_op_AST.getFirstChild()!=null ?
							rel_op_AST.getFirstChild() : rel_op_AST;
						currentAST.advanceChildToEnd();
					}
					rel_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = rel_op_AST;
		} finally { // debugging
			traceOut("rel_op");
		}
	}
	
	public final void eq_op() throws RecognitionException, TokenStreamException {
		
		traceIn("eq_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST eq_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case EQUALITY:
				{
					AST tmp76_AST = null;
					tmp76_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp76_AST);
					match(EQUALITY);
					eq_op_AST = (AST)currentAST.root;
					break;
				}
				case NEQ:
				{
					AST tmp77_AST = null;
					tmp77_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp77_AST);
					match(NEQ);
					if ( inputState.guessing==0 ) {
						eq_op_AST = (AST)currentAST.root;
						eq_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_eq_op,"AST_eq_op")).add(eq_op_AST));
						currentAST.root = eq_op_AST;
						currentAST.child = eq_op_AST!=null &&eq_op_AST.getFirstChild()!=null ?
							eq_op_AST.getFirstChild() : eq_op_AST;
						currentAST.advanceChildToEnd();
					}
					eq_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = eq_op_AST;
		} finally { // debugging
			traceOut("eq_op");
		}
	}
	
	public final void cond_op() throws RecognitionException, TokenStreamException {
		
		traceIn("cond_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST cond_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case ANDAND:
				{
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp78_AST);
					match(ANDAND);
					cond_op_AST = (AST)currentAST.root;
					break;
				}
				case OROR:
				{
					AST tmp79_AST = null;
					tmp79_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp79_AST);
					match(OROR);
					if ( inputState.guessing==0 ) {
						cond_op_AST = (AST)currentAST.root;
						cond_op_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_cond_op,"AST_cond_op")).add(cond_op_AST));
						currentAST.root = cond_op_AST;
						currentAST.child = cond_op_AST!=null &&cond_op_AST.getFirstChild()!=null ?
							cond_op_AST.getFirstChild() : cond_op_AST;
						currentAST.advanceChildToEnd();
					}
					cond_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_15);
				} else {
				  throw ex;
				}
			}
			returnAST = cond_op_AST;
		} finally { // debugging
			traceOut("cond_op");
		}
	}
	
	public final void location_array() throws RecognitionException, TokenStreamException {
		
		traceIn("location_array");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST location_array_AST = null;
			
			try {      // for error handling
				AST tmp80_AST = null;
				tmp80_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp80_AST);
				match(ID);
				AST tmp81_AST = null;
				tmp81_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(LBRACKET);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp82_AST = null;
				tmp82_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp82_AST);
				match(RBRACKET);
				if ( inputState.guessing==0 ) {
					location_array_AST = (AST)currentAST.root;
					location_array_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_location_array,"AST_location_array")).add(location_array_AST));
					currentAST.root = location_array_AST;
					currentAST.child = location_array_AST!=null &&location_array_AST.getFirstChild()!=null ?
						location_array_AST.getFirstChild() : location_array_AST;
					currentAST.advanceChildToEnd();
				}
				location_array_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_12);
				} else {
				  throw ex;
				}
			}
			returnAST = location_array_AST;
		} finally { // debugging
			traceOut("location_array");
		}
	}
	
	public final void location_noarray() throws RecognitionException, TokenStreamException {
		
		traceIn("location_noarray");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST location_noarray_AST = null;
			
			try {      // for error handling
				AST tmp83_AST = null;
				tmp83_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp83_AST);
				match(ID);
				if ( inputState.guessing==0 ) {
					location_noarray_AST = (AST)currentAST.root;
					location_noarray_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_location_noarray,"AST_location_noarray")).add(location_noarray_AST));
					currentAST.root = location_noarray_AST;
					currentAST.child = location_noarray_AST!=null &&location_noarray_AST.getFirstChild()!=null ?
						location_noarray_AST.getFirstChild() : location_noarray_AST;
					currentAST.advanceChildToEnd();
				}
				location_noarray_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_12);
				} else {
				  throw ex;
				}
			}
			returnAST = location_noarray_AST;
		} finally { // debugging
			traceOut("location_noarray");
		}
	}
	
	protected final void char_literal() throws RecognitionException, TokenStreamException {
		
		traceIn("char_literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST char_literal_AST = null;
			
			try {      // for error handling
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp84_AST);
				match(CHARLITERAL);
				if ( inputState.guessing==0 ) {
					char_literal_AST = (AST)currentAST.root;
					char_literal_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_char_literal,"AST_char_literal")).add(char_literal_AST));
					currentAST.root = char_literal_AST;
					currentAST.child = char_literal_AST!=null &&char_literal_AST.getFirstChild()!=null ?
						char_literal_AST.getFirstChild() : char_literal_AST;
					currentAST.advanceChildToEnd();
				}
				char_literal_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = char_literal_AST;
		} finally { // debugging
			traceOut("char_literal");
		}
	}
	
	protected final void bool_literal() throws RecognitionException, TokenStreamException {
		
		traceIn("bool_literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST bool_literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case RESERVED_TRUE:
				{
					AST tmp85_AST = null;
					tmp85_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp85_AST);
					match(RESERVED_TRUE);
					bool_literal_AST = (AST)currentAST.root;
					break;
				}
				case RESERVED_FALSE:
				{
					AST tmp86_AST = null;
					tmp86_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp86_AST);
					match(RESERVED_FALSE);
					if ( inputState.guessing==0 ) {
						bool_literal_AST = (AST)currentAST.root;
						bool_literal_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AST_bool_literal,"AST_bool_literal")).add(bool_literal_AST));
						currentAST.root = bool_literal_AST;
						currentAST.child = bool_literal_AST!=null &&bool_literal_AST.getFirstChild()!=null ?
							bool_literal_AST.getFirstChild() : bool_literal_AST;
						currentAST.advanceChildToEnd();
					}
					bool_literal_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_6);
				} else {
				  throw ex;
				}
			}
			returnAST = bool_literal_AST;
		} finally { // debugging
			traceOut("bool_literal");
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"class\"",
		"{",
		"}",
		"WS_",
		"SL_COMMENT",
		"CHARLITERAL",
		"STRINGLITERAL",
		"DECIMALLITERAL",
		"HEXLITERAL",
		"IDSTRING",
		"RESERVED_TRUE",
		"RESERVED_FALSE",
		"RESERVED_BOOL",
		"RESERVED_BREAK",
		"RESERVED_IMPORT",
		"RESERVED_CONTINUE",
		"RESERVED_ELSE",
		"RESERVED_FOR",
		"RESERVED_WHILE",
		"RESERVED_IF",
		"RESERVED_INT",
		"RESERVED_RETURN",
		"RESERVED_LEN",
		"RESERVED_VOID",
		"ID",
		"DIV",
		"MULT",
		"PERCENT",
		"SEMICOLON",
		"LBRACKET",
		"RBRACKET",
		"LPAREN",
		"RPAREN",
		"COMMA",
		"ANDAND",
		"OROR",
		"OPERATOR",
		"PLUSEQUALS",
		"MINUSEQUALS",
		"DECREMENT",
		"INCREMENT",
		"PLUS",
		"MINUS",
		"LEQ",
		"NEQ",
		"EQUALITY",
		"GEQ",
		"LESS",
		"GREATER",
		"ASSIGNMENT",
		"NOT",
		"ESC",
		"CHARINTERNAL",
		"LETTER",
		"NONLETTER",
		"NOTALETTERORDIGIT",
		"DIGIT",
		"HEXDIGIT",
		"SKIPPABLE",
		"BADTOKEN",
		"AST_program",
		"AST_import_decl",
		"AST_field_decl",
		"AST_method_decl",
		"AST_method_param",
		"AST_block",
		"AST_type",
		"AST_statement_assignment",
		"AST_statement_method_call",
		"AST_statement_if",
		"AST_statement_for",
		"AST_statement_while",
		"AST_statement_return",
		"AST_statement_break",
		"AST_statement_continue",
		"AST_assign_expr",
		"AST_assign_op",
		"AST_compound_assign_op",
		"AST_method_call",
		"AST_method_params_none",
		"AST_method_params_local",
		"AST_method_params_import",
		"AST_expr",
		"AST_import_arg",
		"AST_bin_op",
		"AST_arith_op",
		"AST_rel_op",
		"AST_eq_op",
		"AST_cond_op",
		"AST_location_array",
		"AST_location_noarray",
		"AST_char_literal",
		"AST_bool_literal",
		"AST_int_literal"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 151322626L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 468385858L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 151060482L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 268435456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 141733920768L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 8973070907736064L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 206158430208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 317325312L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 469434434L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 9040193193508864L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 317325376L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 18013255511310336L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 4294967296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 227633266688L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 18084801948998144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 26987491227654656L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 18084801948999168L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 8973079497670656L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	
	}
