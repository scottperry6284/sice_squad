// $ANTLR 2.7.7 (2006-11-01): "scanner.g" -> "DecafScanner.java"$

package edu.mit.compilers.grammar;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;
@SuppressWarnings("unchecked")
public class DecafScanner extends antlr.CharScanner implements DecafScannerTokenTypes, TokenStream
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
public DecafScanner(InputStream in) {
	this(new ByteBuffer(in));
}
public DecafScanner(Reader in) {
	this(new CharBuffer(in));
}
public DecafScanner(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public DecafScanner(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("class", this), new Integer(4));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '{':
				{
					mLCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mRCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case ' ':
				{
					mWS_(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mCHARLITERAL(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mSTRINGLITERAL(true);
					theRetToken=_returnToken;
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '_':  case 'a':
				case 'b':  case 'c':  case 'd':  case 'e':
				case 'f':  case 'g':  case 'h':  case 'i':
				case 'j':  case 'k':  case 'l':  case 'm':
				case 'n':  case 'o':  case 'p':  case 'q':
				case 'r':  case 's':  case 't':  case 'u':
				case 'v':  case 'w':  case 'x':  case 'y':
				case 'z':
				{
					mIDSTRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mMULT(true);
					theRetToken=_returnToken;
					break;
				}
				case '%':
				{
					mPERCENT(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSEMICOLON(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mLBRACKET(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mRBRACKET(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '&':
				{
					mANDAND(true);
					theRetToken=_returnToken;
					break;
				}
				case '|':
				{
					mOROR(true);
					theRetToken=_returnToken;
					break;
				}
				case '!':  case '+':  case '-':  case '<':
				case '=':  case '>':
				{
					mOPERATOR(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='/') && (LA(2)=='/')) {
						mSL_COMMENT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='0') && (LA(2)=='x')) {
						mHEXLITERAL(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1) >= '0' && LA(1) <= '9')) && (true)) {
						mDECIMALLITERAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='/') && (true)) {
						mDIV(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLCURLY");
		_ttype = LCURLY;
		int _saveIndex;
		try { // debugging
			
			match("{");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLCURLY");
		}
	}
	
	public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRCURLY");
		_ttype = RCURLY;
		int _saveIndex;
		try { // debugging
			
			match("}");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRCURLY");
		}
	}
	
	public final void mWS_(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mWS_");
		_ttype = WS_;
		int _saveIndex;
		try { // debugging
			
			{
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '\n':
			{
				match('\n');
				if ( inputState.guessing==0 ) {
					newline();
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mWS_");
		}
	}
	
	public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mSL_COMMENT");
		_ttype = SL_COMMENT;
		int _saveIndex;
		try { // debugging
			
			match("//");
			{
			_loop7:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					matchNot('\n');
				}
				else {
					break _loop7;
				}
				
			} while (true);
			}
			match('\n');
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP; newline ();
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mSL_COMMENT");
		}
	}
	
	public final void mCHARLITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mCHARLITERAL");
		_ttype = CHARLITERAL;
		int _saveIndex;
		try { // debugging
			
			match('\'');
			mCHARINTERNAL(false);
			match('\'');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mCHARLITERAL");
		}
	}
	
	protected final void mCHARINTERNAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mCHARINTERNAL");
		_ttype = CHARINTERNAL;
		int _saveIndex;
		try { // debugging
			
			{
			switch ( LA(1)) {
			case '\\':
			{
				mESC(false);
				break;
			}
			case '\u0000':  case '\u0001':  case '\u0002':  case '\u0003':
			case '\u0004':  case '\u0005':  case '\u0006':  case '\u0007':
			case '\u0008':  case '\u000b':  case '\u000c':  case '\r':
			case '\u000e':  case '\u000f':  case '\u0010':  case '\u0011':
			case '\u0012':  case '\u0013':  case '\u0014':  case '\u0015':
			case '\u0016':  case '\u0017':  case '\u0018':  case '\u0019':
			case '\u001a':  case '\u001b':  case '\u001c':  case '\u001d':
			case '\u001e':  case '\u001f':  case ' ':  case '!':
			case '#':  case '$':  case '%':  case '&':
			case '(':  case ')':  case '*':  case '+':
			case ',':  case '-':  case '.':  case '/':
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':  case ':':  case ';':
			case '<':  case '=':  case '>':  case '?':
			case '@':  case 'A':  case 'B':  case 'C':
			case 'D':  case 'E':  case 'F':  case 'G':
			case 'H':  case 'I':  case 'J':  case 'K':
			case 'L':  case 'M':  case 'N':  case 'O':
			case 'P':  case 'Q':  case 'R':  case 'S':
			case 'T':  case 'U':  case 'V':  case 'W':
			case 'X':  case 'Y':  case 'Z':  case '[':
			case ']':  case '^':  case '_':  case '`':
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':  case '{':  case '|':
			case '}':  case '~':  case '\u007f':
			{
				{
				match(_tokenSet_1);
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mCHARINTERNAL");
		}
	}
	
	public final void mSTRINGLITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mSTRINGLITERAL");
		_ttype = STRINGLITERAL;
		int _saveIndex;
		try { // debugging
			
			match('"');
			{
			_loop11:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mCHARINTERNAL(false);
				}
				else {
					break _loop11;
				}
				
			} while (true);
			}
			match('"');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mSTRINGLITERAL");
		}
	}
	
	public final void mDECIMALLITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mDECIMALLITERAL");
		_ttype = DECIMALLITERAL;
		int _saveIndex;
		try { // debugging
			
			{
			int _cnt14=0;
			_loop14:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mDIGIT(false);
				}
				else {
					if ( _cnt14>=1 ) { break _loop14; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt14++;
			} while (true);
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mDECIMALLITERAL");
		}
	}
	
	protected final void mDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mDIGIT");
		_ttype = DIGIT;
		int _saveIndex;
		try { // debugging
			
			matchRange('0','9');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mDIGIT");
		}
	}
	
	public final void mHEXLITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mHEXLITERAL");
		_ttype = HEXLITERAL;
		int _saveIndex;
		try { // debugging
			
			match("0x");
			{
			int _cnt17=0;
			_loop17:
			do {
				if ((_tokenSet_3.member(LA(1)))) {
					mHEXDIGIT(false);
				}
				else {
					if ( _cnt17>=1 ) { break _loop17; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt17++;
			} while (true);
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mHEXLITERAL");
		}
	}
	
	protected final void mHEXDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mHEXDIGIT");
		_ttype = HEXDIGIT;
		int _saveIndex;
		try { // debugging
			
			switch ( LA(1)) {
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				mDIGIT(false);
				break;
			}
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':
			{
				{
				matchRange('a','f');
				}
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':
			{
				{
				matchRange('A','F');
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mHEXDIGIT");
		}
	}
	
	public final void mIDSTRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mIDSTRING");
		_ttype = IDSTRING;
		int _saveIndex;
		try { // debugging
			
			boolean synPredMatched30 = false;
			if (((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='n') && (LA(4)=='t') && (LA(5)=='i') && (LA(6)=='n') && (LA(7)=='u') && (LA(8)=='e') && (true))) {
				int _m30 = mark();
				synPredMatched30 = true;
				inputState.guessing++;
				try {
					{
					mRESERVED_CONTINUE(false);
					mNOTALETTERORDIGIT(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched30 = false;
				}
				rewind(_m30);
inputState.guessing--;
			}
			if ( synPredMatched30 ) {
				mRESERVED_CONTINUE(false);
				if ( inputState.guessing==0 ) {
					_ttype = RESERVED_CONTINUE;
				}
			}
			else {
				boolean synPredMatched28 = false;
				if (((LA(1)=='i') && (LA(2)=='m') && (LA(3)=='p') && (LA(4)=='o') && (LA(5)=='r') && (LA(6)=='t') && (true) && (true) && (true))) {
					int _m28 = mark();
					synPredMatched28 = true;
					inputState.guessing++;
					try {
						{
						mRESERVED_IMPORT(false);
						mNOTALETTERORDIGIT(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched28 = false;
					}
					rewind(_m28);
inputState.guessing--;
				}
				if ( synPredMatched28 ) {
					mRESERVED_IMPORT(false);
					if ( inputState.guessing==0 ) {
						_ttype = RESERVED_IMPORT;
					}
				}
				else {
					boolean synPredMatched42 = false;
					if (((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='t') && (LA(4)=='u') && (LA(5)=='r') && (LA(6)=='n') && (true) && (true) && (true))) {
						int _m42 = mark();
						synPredMatched42 = true;
						inputState.guessing++;
						try {
							{
							mRESERVED_RETURN(false);
							mNOTALETTERORDIGIT(false);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched42 = false;
						}
						rewind(_m42);
inputState.guessing--;
					}
					if ( synPredMatched42 ) {
						mRESERVED_RETURN(false);
						if ( inputState.guessing==0 ) {
							_ttype = RESERVED_RETURN;
						}
					}
					else {
						boolean synPredMatched22 = false;
						if (((LA(1)=='f') && (LA(2)=='a') && (LA(3)=='l') && (LA(4)=='s') && (LA(5)=='e') && (true) && (true) && (true) && (true))) {
							int _m22 = mark();
							synPredMatched22 = true;
							inputState.guessing++;
							try {
								{
								mRESERVED_FALSE(false);
								mNOTALETTERORDIGIT(false);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched22 = false;
							}
							rewind(_m22);
inputState.guessing--;
						}
						if ( synPredMatched22 ) {
							mRESERVED_FALSE(false);
							if ( inputState.guessing==0 ) {
								_ttype = RESERVED_FALSE;
							}
						}
						else {
							boolean synPredMatched26 = false;
							if (((LA(1)=='b') && (LA(2)=='r') && (LA(3)=='e') && (LA(4)=='a') && (LA(5)=='k') && (true) && (true) && (true) && (true))) {
								int _m26 = mark();
								synPredMatched26 = true;
								inputState.guessing++;
								try {
									{
									mRESERVED_BREAK(false);
									mNOTALETTERORDIGIT(false);
									}
								}
								catch (RecognitionException pe) {
									synPredMatched26 = false;
								}
								rewind(_m26);
inputState.guessing--;
							}
							if ( synPredMatched26 ) {
								mRESERVED_BREAK(false);
								if ( inputState.guessing==0 ) {
									_ttype = RESERVED_BREAK;
								}
							}
							else {
								boolean synPredMatched36 = false;
								if (((LA(1)=='w') && (LA(2)=='h') && (LA(3)=='i') && (LA(4)=='l') && (LA(5)=='e') && (true) && (true) && (true) && (true))) {
									int _m36 = mark();
									synPredMatched36 = true;
									inputState.guessing++;
									try {
										{
										mRESERVED_WHILE(false);
										mNOTALETTERORDIGIT(false);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched36 = false;
									}
									rewind(_m36);
inputState.guessing--;
								}
								if ( synPredMatched36 ) {
									mRESERVED_WHILE(false);
									if ( inputState.guessing==0 ) {
										_ttype = RESERVED_WHILE;
									}
								}
								else {
									boolean synPredMatched20 = false;
									if (((LA(1)=='t') && (LA(2)=='r') && (LA(3)=='u') && (LA(4)=='e') && (true) && (true) && (true) && (true) && (true))) {
										int _m20 = mark();
										synPredMatched20 = true;
										inputState.guessing++;
										try {
											{
											mRESERVED_TRUE(false);
											mNOTALETTERORDIGIT(false);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched20 = false;
										}
										rewind(_m20);
inputState.guessing--;
									}
									if ( synPredMatched20 ) {
										mRESERVED_TRUE(false);
										if ( inputState.guessing==0 ) {
											_ttype = RESERVED_TRUE;
										}
									}
									else {
										boolean synPredMatched24 = false;
										if (((LA(1)=='b') && (LA(2)=='o') && (LA(3)=='o') && (LA(4)=='l') && (true) && (true) && (true) && (true) && (true))) {
											int _m24 = mark();
											synPredMatched24 = true;
											inputState.guessing++;
											try {
												{
												mRESERVED_BOOL(false);
												mNOTALETTERORDIGIT(false);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched24 = false;
											}
											rewind(_m24);
inputState.guessing--;
										}
										if ( synPredMatched24 ) {
											mRESERVED_BOOL(false);
											if ( inputState.guessing==0 ) {
												_ttype = RESERVED_BOOL;
											}
										}
										else {
											boolean synPredMatched32 = false;
											if (((LA(1)=='e') && (LA(2)=='l') && (LA(3)=='s') && (LA(4)=='e') && (true) && (true) && (true) && (true) && (true))) {
												int _m32 = mark();
												synPredMatched32 = true;
												inputState.guessing++;
												try {
													{
													mRESERVED_ELSE(false);
													mNOTALETTERORDIGIT(false);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched32 = false;
												}
												rewind(_m32);
inputState.guessing--;
											}
											if ( synPredMatched32 ) {
												mRESERVED_ELSE(false);
												if ( inputState.guessing==0 ) {
													_ttype = RESERVED_ELSE;
												}
											}
											else {
												boolean synPredMatched46 = false;
												if (((LA(1)=='v') && (LA(2)=='o') && (LA(3)=='i') && (LA(4)=='d') && (true) && (true) && (true) && (true) && (true))) {
													int _m46 = mark();
													synPredMatched46 = true;
													inputState.guessing++;
													try {
														{
														mRESERVED_VOID(false);
														mNOTALETTERORDIGIT(false);
														}
													}
													catch (RecognitionException pe) {
														synPredMatched46 = false;
													}
													rewind(_m46);
inputState.guessing--;
												}
												if ( synPredMatched46 ) {
													mRESERVED_VOID(false);
													if ( inputState.guessing==0 ) {
														_ttype = RESERVED_VOID;
													}
												}
												else {
													boolean synPredMatched34 = false;
													if (((LA(1)=='f') && (LA(2)=='o') && (LA(3)=='r') && (true) && (true) && (true) && (true) && (true) && (true))) {
														int _m34 = mark();
														synPredMatched34 = true;
														inputState.guessing++;
														try {
															{
															mRESERVED_FOR(false);
															mNOTALETTERORDIGIT(false);
															}
														}
														catch (RecognitionException pe) {
															synPredMatched34 = false;
														}
														rewind(_m34);
inputState.guessing--;
													}
													if ( synPredMatched34 ) {
														mRESERVED_FOR(false);
														if ( inputState.guessing==0 ) {
															_ttype = RESERVED_FOR;
														}
													}
													else {
														boolean synPredMatched40 = false;
														if (((LA(1)=='i') && (LA(2)=='n') && (LA(3)=='t') && (true) && (true) && (true) && (true) && (true) && (true))) {
															int _m40 = mark();
															synPredMatched40 = true;
															inputState.guessing++;
															try {
																{
																mRESERVED_INT(false);
																mNOTALETTERORDIGIT(false);
																}
															}
															catch (RecognitionException pe) {
																synPredMatched40 = false;
															}
															rewind(_m40);
inputState.guessing--;
														}
														if ( synPredMatched40 ) {
															mRESERVED_INT(false);
															if ( inputState.guessing==0 ) {
																_ttype = RESERVED_INT;
															}
														}
														else {
															boolean synPredMatched44 = false;
															if (((LA(1)=='l') && (LA(2)=='e') && (LA(3)=='n') && (true) && (true) && (true) && (true) && (true) && (true))) {
																int _m44 = mark();
																synPredMatched44 = true;
																inputState.guessing++;
																try {
																	{
																	mRESERVED_LEN(false);
																	mNOTALETTERORDIGIT(false);
																	}
																}
																catch (RecognitionException pe) {
																	synPredMatched44 = false;
																}
																rewind(_m44);
inputState.guessing--;
															}
															if ( synPredMatched44 ) {
																mRESERVED_LEN(false);
																if ( inputState.guessing==0 ) {
																	_ttype = RESERVED_LEN;
																}
															}
															else {
																boolean synPredMatched38 = false;
																if (((LA(1)=='i') && (LA(2)=='f') && (true) && (true) && (true) && (true) && (true) && (true) && (true))) {
																	int _m38 = mark();
																	synPredMatched38 = true;
																	inputState.guessing++;
																	try {
																		{
																		mRESERVED_IF(false);
																		mNOTALETTERORDIGIT(false);
																		}
																	}
																	catch (RecognitionException pe) {
																		synPredMatched38 = false;
																	}
																	rewind(_m38);
inputState.guessing--;
																}
																if ( synPredMatched38 ) {
																	mRESERVED_IF(false);
																	if ( inputState.guessing==0 ) {
																		_ttype = RESERVED_IF;
																	}
																}
																else if ((_tokenSet_4.member(LA(1))) && (true) && (true) && (true) && (true) && (true) && (true) && (true) && (true)) {
																	mID(false);
																	if ( inputState.guessing==0 ) {
																		_ttype = ID;
																	}
																}
																else {
																	throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
																}
																}}}}}}}}}}}}}
																if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
																	_token = makeToken(_ttype);
																	_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
																}
																_returnToken = _token;
															} finally { // debugging
																traceOut("mIDSTRING");
															}
														}
														
	protected final void mRESERVED_TRUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_TRUE");
		_ttype = RESERVED_TRUE;
		int _saveIndex;
		try { // debugging
			
			match("true");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_TRUE");
		}
	}
	
	protected final void mNOTALETTERORDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mNOTALETTERORDIGIT");
		_ttype = NOTALETTERORDIGIT;
		int _saveIndex;
		try { // debugging
			
			switch ( LA(1)) {
			case '\n':
			{
				{
				match('\n');
				}
				break;
			}
			case '\t':
			{
				{
				match('\t');
				}
				break;
			}
			case ' ':  case '!':  case '"':  case '#':
			case '$':  case '%':  case '&':  case '\'':
			case '(':  case ')':  case '*':  case '+':
			case ',':  case '-':  case '.':  case '/':
			{
				{
				matchRange('\040','\057');
				}
				break;
			}
			case ':':  case ';':  case '<':  case '=':
			case '>':  case '?':  case '@':
			{
				{
				matchRange('\072','\100');
				}
				break;
			}
			case '[':  case '\\':  case ']':  case '^':
			{
				{
				matchRange('\133','\136');
				}
				break;
			}
			case '`':
			{
				{
				match('\140');
				}
				break;
			}
			case '{':  case '|':  case '}':  case '~':
			{
				{
				matchRange('\173','\176');
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mNOTALETTERORDIGIT");
		}
	}
	
	protected final void mRESERVED_FALSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_FALSE");
		_ttype = RESERVED_FALSE;
		int _saveIndex;
		try { // debugging
			
			match("false");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_FALSE");
		}
	}
	
	protected final void mRESERVED_BOOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_BOOL");
		_ttype = RESERVED_BOOL;
		int _saveIndex;
		try { // debugging
			
			match("bool");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_BOOL");
		}
	}
	
	protected final void mRESERVED_BREAK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_BREAK");
		_ttype = RESERVED_BREAK;
		int _saveIndex;
		try { // debugging
			
			match("break");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_BREAK");
		}
	}
	
	protected final void mRESERVED_IMPORT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_IMPORT");
		_ttype = RESERVED_IMPORT;
		int _saveIndex;
		try { // debugging
			
			match("import");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_IMPORT");
		}
	}
	
	protected final void mRESERVED_CONTINUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_CONTINUE");
		_ttype = RESERVED_CONTINUE;
		int _saveIndex;
		try { // debugging
			
			match("continue");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_CONTINUE");
		}
	}
	
	protected final void mRESERVED_ELSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_ELSE");
		_ttype = RESERVED_ELSE;
		int _saveIndex;
		try { // debugging
			
			match("else");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_ELSE");
		}
	}
	
	protected final void mRESERVED_FOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_FOR");
		_ttype = RESERVED_FOR;
		int _saveIndex;
		try { // debugging
			
			match("for");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_FOR");
		}
	}
	
	protected final void mRESERVED_WHILE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_WHILE");
		_ttype = RESERVED_WHILE;
		int _saveIndex;
		try { // debugging
			
			match("while");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_WHILE");
		}
	}
	
	protected final void mRESERVED_IF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_IF");
		_ttype = RESERVED_IF;
		int _saveIndex;
		try { // debugging
			
			match("if");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_IF");
		}
	}
	
	protected final void mRESERVED_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_INT");
		_ttype = RESERVED_INT;
		int _saveIndex;
		try { // debugging
			
			match("int");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_INT");
		}
	}
	
	protected final void mRESERVED_RETURN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_RETURN");
		_ttype = RESERVED_RETURN;
		int _saveIndex;
		try { // debugging
			
			match("return");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_RETURN");
		}
	}
	
	protected final void mRESERVED_LEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_LEN");
		_ttype = RESERVED_LEN;
		int _saveIndex;
		try { // debugging
			
			match("len");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_LEN");
		}
	}
	
	protected final void mRESERVED_VOID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRESERVED_VOID");
		_ttype = RESERVED_VOID;
		int _saveIndex;
		try { // debugging
			
			match("void");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRESERVED_VOID");
		}
	}
	
	protected final void mID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mID");
		_ttype = ID;
		int _saveIndex;
		try { // debugging
			
			mLETTER(false);
			{
			_loop63:
			do {
				switch ( LA(1)) {
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '_':  case 'a':
				case 'b':  case 'c':  case 'd':  case 'e':
				case 'f':  case 'g':  case 'h':  case 'i':
				case 'j':  case 'k':  case 'l':  case 'm':
				case 'n':  case 'o':  case 'p':  case 'q':
				case 'r':  case 's':  case 't':  case 'u':
				case 'v':  case 'w':  case 'x':  case 'y':
				case 'z':
				{
					mLETTER(false);
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mDIGIT(false);
					break;
				}
				default:
				{
					break _loop63;
				}
				}
			} while (true);
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mID");
		}
	}
	
	protected final void mLETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLETTER");
		_ttype = LETTER;
		int _saveIndex;
		try { // debugging
			
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				{
				matchRange('a','z');
				}
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				{
				matchRange('A','Z');
				}
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLETTER");
		}
	}
	
	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mDIV");
		_ttype = DIV;
		int _saveIndex;
		try { // debugging
			
			match("/");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mDIV");
		}
	}
	
	public final void mMULT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mMULT");
		_ttype = MULT;
		int _saveIndex;
		try { // debugging
			
			match("*");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mMULT");
		}
	}
	
	public final void mPERCENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mPERCENT");
		_ttype = PERCENT;
		int _saveIndex;
		try { // debugging
			
			match('%');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mPERCENT");
		}
	}
	
	public final void mSEMICOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mSEMICOLON");
		_ttype = SEMICOLON;
		int _saveIndex;
		try { // debugging
			
			match(';');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mSEMICOLON");
		}
	}
	
	public final void mLBRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLBRACKET");
		_ttype = LBRACKET;
		int _saveIndex;
		try { // debugging
			
			match('[');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLBRACKET");
		}
	}
	
	public final void mRBRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRBRACKET");
		_ttype = RBRACKET;
		int _saveIndex;
		try { // debugging
			
			match(']');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRBRACKET");
		}
	}
	
	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLPAREN");
		_ttype = LPAREN;
		int _saveIndex;
		try { // debugging
			
			match('(');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLPAREN");
		}
	}
	
	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mRPAREN");
		_ttype = RPAREN;
		int _saveIndex;
		try { // debugging
			
			match(')');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mRPAREN");
		}
	}
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mCOMMA");
		_ttype = COMMA;
		int _saveIndex;
		try { // debugging
			
			match(',');
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mCOMMA");
		}
	}
	
	public final void mANDAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mANDAND");
		_ttype = ANDAND;
		int _saveIndex;
		try { // debugging
			
			match("&&");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mANDAND");
		}
	}
	
	public final void mOROR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mOROR");
		_ttype = OROR;
		int _saveIndex;
		try { // debugging
			
			match("||");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mOROR");
		}
	}
	
	public final void mOPERATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mOPERATOR");
		_ttype = OPERATOR;
		int _saveIndex;
		try { // debugging
			
			boolean synPredMatched77 = false;
			if (((LA(1)=='+') && (LA(2)=='='))) {
				int _m77 = mark();
				synPredMatched77 = true;
				inputState.guessing++;
				try {
					{
					mPLUSEQUALS(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched77 = false;
				}
				rewind(_m77);
inputState.guessing--;
			}
			if ( synPredMatched77 ) {
				mPLUSEQUALS(false);
				if ( inputState.guessing==0 ) {
					_ttype = PLUSEQUALS;
				}
			}
			else {
				boolean synPredMatched79 = false;
				if (((LA(1)=='-') && (LA(2)=='='))) {
					int _m79 = mark();
					synPredMatched79 = true;
					inputState.guessing++;
					try {
						{
						mMINUSEQUALS(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched79 = false;
					}
					rewind(_m79);
inputState.guessing--;
				}
				if ( synPredMatched79 ) {
					mMINUSEQUALS(false);
					if ( inputState.guessing==0 ) {
						_ttype = MINUSEQUALS;
					}
				}
				else {
					boolean synPredMatched81 = false;
					if (((LA(1)=='-') && (LA(2)=='-'))) {
						int _m81 = mark();
						synPredMatched81 = true;
						inputState.guessing++;
						try {
							{
							mDECREMENT(false);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched81 = false;
						}
						rewind(_m81);
inputState.guessing--;
					}
					if ( synPredMatched81 ) {
						mDECREMENT(false);
						if ( inputState.guessing==0 ) {
							_ttype = DECREMENT;
						}
					}
					else {
						boolean synPredMatched83 = false;
						if (((LA(1)=='+') && (LA(2)=='+'))) {
							int _m83 = mark();
							synPredMatched83 = true;
							inputState.guessing++;
							try {
								{
								mINCREMENT(false);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched83 = false;
							}
							rewind(_m83);
inputState.guessing--;
						}
						if ( synPredMatched83 ) {
							mINCREMENT(false);
							if ( inputState.guessing==0 ) {
								_ttype = INCREMENT;
							}
						}
						else {
							boolean synPredMatched85 = false;
							if (((LA(1)=='<') && (LA(2)=='='))) {
								int _m85 = mark();
								synPredMatched85 = true;
								inputState.guessing++;
								try {
									{
									mLEQ(false);
									}
								}
								catch (RecognitionException pe) {
									synPredMatched85 = false;
								}
								rewind(_m85);
inputState.guessing--;
							}
							if ( synPredMatched85 ) {
								mLEQ(false);
								if ( inputState.guessing==0 ) {
									_ttype = LEQ;
								}
							}
							else {
								boolean synPredMatched87 = false;
								if (((LA(1)=='!') && (LA(2)=='='))) {
									int _m87 = mark();
									synPredMatched87 = true;
									inputState.guessing++;
									try {
										{
										mNEQ(false);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched87 = false;
									}
									rewind(_m87);
inputState.guessing--;
								}
								if ( synPredMatched87 ) {
									mNEQ(false);
									if ( inputState.guessing==0 ) {
										_ttype = NEQ;
									}
								}
								else {
									boolean synPredMatched89 = false;
									if (((LA(1)=='=') && (LA(2)=='='))) {
										int _m89 = mark();
										synPredMatched89 = true;
										inputState.guessing++;
										try {
											{
											mEQUALITY(false);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched89 = false;
										}
										rewind(_m89);
inputState.guessing--;
									}
									if ( synPredMatched89 ) {
										mEQUALITY(false);
										if ( inputState.guessing==0 ) {
											_ttype = EQUALITY;
										}
									}
									else {
										boolean synPredMatched91 = false;
										if (((LA(1)=='>') && (LA(2)=='='))) {
											int _m91 = mark();
											synPredMatched91 = true;
											inputState.guessing++;
											try {
												{
												mGEQ(false);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched91 = false;
											}
											rewind(_m91);
inputState.guessing--;
										}
										if ( synPredMatched91 ) {
											mGEQ(false);
											if ( inputState.guessing==0 ) {
												_ttype = GEQ;
											}
										}
										else {
											boolean synPredMatched93 = false;
											if (((LA(1)=='+') && (true))) {
												int _m93 = mark();
												synPredMatched93 = true;
												inputState.guessing++;
												try {
													{
													mPLUS(false);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched93 = false;
												}
												rewind(_m93);
inputState.guessing--;
											}
											if ( synPredMatched93 ) {
												mPLUS(false);
												if ( inputState.guessing==0 ) {
													_ttype = PLUS;
												}
											}
											else {
												boolean synPredMatched95 = false;
												if (((LA(1)=='-') && (true))) {
													int _m95 = mark();
													synPredMatched95 = true;
													inputState.guessing++;
													try {
														{
														mMINUS(false);
														}
													}
													catch (RecognitionException pe) {
														synPredMatched95 = false;
													}
													rewind(_m95);
inputState.guessing--;
												}
												if ( synPredMatched95 ) {
													mMINUS(false);
													if ( inputState.guessing==0 ) {
														_ttype = MINUS;
													}
												}
												else {
													boolean synPredMatched97 = false;
													if (((LA(1)=='<') && (true))) {
														int _m97 = mark();
														synPredMatched97 = true;
														inputState.guessing++;
														try {
															{
															mLESS(false);
															}
														}
														catch (RecognitionException pe) {
															synPredMatched97 = false;
														}
														rewind(_m97);
inputState.guessing--;
													}
													if ( synPredMatched97 ) {
														mLESS(false);
														if ( inputState.guessing==0 ) {
															_ttype = LESS;
														}
													}
													else {
														boolean synPredMatched99 = false;
														if (((LA(1)=='>') && (true))) {
															int _m99 = mark();
															synPredMatched99 = true;
															inputState.guessing++;
															try {
																{
																mGREATER(false);
																}
															}
															catch (RecognitionException pe) {
																synPredMatched99 = false;
															}
															rewind(_m99);
inputState.guessing--;
														}
														if ( synPredMatched99 ) {
															mGREATER(false);
															if ( inputState.guessing==0 ) {
																_ttype = GREATER;
															}
														}
														else {
															boolean synPredMatched101 = false;
															if (((LA(1)=='=') && (true))) {
																int _m101 = mark();
																synPredMatched101 = true;
																inputState.guessing++;
																try {
																	{
																	mASSIGNMENT(false);
																	}
																}
																catch (RecognitionException pe) {
																	synPredMatched101 = false;
																}
																rewind(_m101);
inputState.guessing--;
															}
															if ( synPredMatched101 ) {
																mASSIGNMENT(false);
																if ( inputState.guessing==0 ) {
																	_ttype = ASSIGNMENT;
																}
															}
															else if ((LA(1)=='!') && (true)) {
																mNOT(false);
																if ( inputState.guessing==0 ) {
																	_ttype = NOT;
																}
															}
															else {
																throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
															}
															}}}}}}}}}}}}
															if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
																_token = makeToken(_ttype);
																_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
															}
															_returnToken = _token;
														} finally { // debugging
															traceOut("mOPERATOR");
														}
													}
													
	protected final void mPLUSEQUALS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mPLUSEQUALS");
		_ttype = PLUSEQUALS;
		int _saveIndex;
		try { // debugging
			
			match("+=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mPLUSEQUALS");
		}
	}
	
	protected final void mMINUSEQUALS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mMINUSEQUALS");
		_ttype = MINUSEQUALS;
		int _saveIndex;
		try { // debugging
			
			match("-=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mMINUSEQUALS");
		}
	}
	
	protected final void mDECREMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mDECREMENT");
		_ttype = DECREMENT;
		int _saveIndex;
		try { // debugging
			
			match("--");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mDECREMENT");
		}
	}
	
	protected final void mINCREMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mINCREMENT");
		_ttype = INCREMENT;
		int _saveIndex;
		try { // debugging
			
			match("++");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mINCREMENT");
		}
	}
	
	protected final void mLEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLEQ");
		_ttype = LEQ;
		int _saveIndex;
		try { // debugging
			
			match("<=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLEQ");
		}
	}
	
	protected final void mNEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mNEQ");
		_ttype = NEQ;
		int _saveIndex;
		try { // debugging
			
			match("!=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mNEQ");
		}
	}
	
	protected final void mEQUALITY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mEQUALITY");
		_ttype = EQUALITY;
		int _saveIndex;
		try { // debugging
			
			match("==");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mEQUALITY");
		}
	}
	
	protected final void mGEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mGEQ");
		_ttype = GEQ;
		int _saveIndex;
		try { // debugging
			
			match(">=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mGEQ");
		}
	}
	
	protected final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mPLUS");
		_ttype = PLUS;
		int _saveIndex;
		try { // debugging
			
			match("+");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mPLUS");
		}
	}
	
	protected final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mMINUS");
		_ttype = MINUS;
		int _saveIndex;
		try { // debugging
			
			match("-");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mMINUS");
		}
	}
	
	protected final void mLESS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mLESS");
		_ttype = LESS;
		int _saveIndex;
		try { // debugging
			
			match("<");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mLESS");
		}
	}
	
	protected final void mGREATER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mGREATER");
		_ttype = GREATER;
		int _saveIndex;
		try { // debugging
			
			match(">");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mGREATER");
		}
	}
	
	protected final void mASSIGNMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mASSIGNMENT");
		_ttype = ASSIGNMENT;
		int _saveIndex;
		try { // debugging
			
			match("=");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mASSIGNMENT");
		}
	}
	
	protected final void mNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mNOT");
		_ttype = NOT;
		int _saveIndex;
		try { // debugging
			
			match("!");
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mNOT");
		}
	}
	
	protected final void mESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mESC");
		_ttype = ESC;
		int _saveIndex;
		try { // debugging
			
			match('\\');
			{
			switch ( LA(1)) {
			case 'n':
			{
				match('n');
				break;
			}
			case '"':
			{
				match('\"');
				break;
			}
			case 't':
			{
				match('t');
				break;
			}
			case '\\':
			{
				match('\\');
				break;
			}
			case '\'':
			{
				match('\'');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mESC");
		}
	}
	
	protected final void mNONLETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mNONLETTER");
		_ttype = NONLETTER;
		int _saveIndex;
		try { // debugging
			
			switch ( LA(1)) {
			case '\n':
			{
				{
				match('\n');
				}
				break;
			}
			case '\t':
			{
				{
				match('\t');
				}
				break;
			}
			case ' ':  case '!':  case '"':  case '#':
			case '$':  case '%':  case '&':  case '\'':
			case '(':  case ')':  case '*':  case '+':
			case ',':  case '-':  case '.':  case '/':
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':  case ':':  case ';':
			case '<':  case '=':  case '>':  case '?':
			case '@':
			{
				{
				matchRange('\040','\100');
				}
				break;
			}
			case '[':  case '\\':  case ']':  case '^':
			{
				{
				matchRange('\133','\136');
				}
				break;
			}
			case '`':
			{
				{
				match('\140');
				}
				break;
			}
			case '{':  case '|':  case '}':  case '~':
			{
				{
				matchRange('\173','\176');
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mNONLETTER");
		}
	}
	
	protected final void mSKIPPABLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mSKIPPABLE");
		_ttype = SKIPPABLE;
		int _saveIndex;
		try { // debugging
			
			switch ( LA(1)) {
			case '\t':  case '\n':  case ' ':
			{
				mWS_(false);
				break;
			}
			case '/':
			{
				mSL_COMMENT(false);
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mSKIPPABLE");
		}
	}
	
	protected final void mBADTOKEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		traceIn("mBADTOKEN");
		_ttype = BADTOKEN;
		int _saveIndex;
		try { // debugging
			
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		} finally { // debugging
			traceOut("mBADTOKEN");
		}
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { -1025L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { -566935684609L, -268435457L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { -566935684609L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 287948901175001088L, 541165879422L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 0L, 576460745995190270L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
