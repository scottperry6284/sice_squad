package edu.mit.compilers;

import java.io.*;
import antlr.Token;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.semantics.IR;
import edu.mit.compilers.semantics.ParseTree;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;
import edu.mit.compilers.semantics.Semantics; 

class Main {
  public static void main(String[] args) {
    try {
      CLI.parse(args, new String[0]);
      InputStream inputStream = args.length == 0 ?
          System.in : new java.io.FileInputStream(CLI.infile);
      PrintStream outputStream = CLI.outfile == null ? System.out : new java.io.PrintStream(new java.io.FileOutputStream(CLI.outfile));
      if (CLI.target == Action.SCAN) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        scanner.setTrace(CLI.debug);
        Token token;
        boolean done = false;
        boolean hasError = false;
        while (!done) {
          try {
            for (token = scanner.nextToken();
token.getType() != DecafParserTokenTypes.EOF;
                 token = scanner.nextToken()) {
              String type = "";
              String text = token.getText();
              switch (token.getType()) {
               // TODO: add strings for the other types here...
               case DecafScannerTokenTypes.ID:
                type = " IDENTIFIER";
                break;
               case DecafScannerTokenTypes.CHARLITERAL:
            	type = " CHARLITERAL";
            	break;
               case DecafScannerTokenTypes.DECIMALLITERAL:
               case DecafScannerTokenTypes.HEXLITERAL:
            	type = " INTLITERAL";
            	break;
               case DecafScannerTokenTypes.RESERVED_TRUE:
               case DecafScannerTokenTypes.RESERVED_FALSE:
            	type = " BOOLEANLITERAL";
            	break;
               case DecafScannerTokenTypes.STRINGLITERAL:
            	type = " STRINGLITERAL";
            	break;
              }
              outputStream.println(token.getLine() + type + " " + text);
            }
            done = true;
          } catch(Exception e) {
            // print the error:
            System.err.println(CLI.infile + " " + e);
            scanner.consume();
            hasError = true;
          }
          if(hasError)
        	  System.exit(1);
        }
      }
	  else if (CLI.target == Action.PARSE || CLI.target == Action.DEFAULT) {
        DecafScanner scanner = new DecafScanner(new DataInputStream(inputStream));
        DecafParser parser = new DecafParser(scanner);
        parser.setTrace(CLI.debug);
        parser.program();
        System.out.println(parser.getError()? "Error": "No error");
        if(parser.getError()) {
          System.exit(1);
        }
      }
	  else if(CLI.target == Action.INTER) {
	      DecafScanner scanner = new DecafScanner(new DataInputStream(inputStream));
	      DecafParser parser = new DecafParser(scanner);
	      parser.setTrace(CLI.debug);
	      parser.program();
	      System.out.println(parser.getError()? "Error": "No error");
	      if(parser.getError()) {
	        System.exit(1);
	      }
	      ParseTree parseTree = new ParseTree(parser);
	      parseTree.build();
	      try {
	    	  IR irbuilder = new IR(parseTree);
	    	  irbuilder.build();
	    	  irbuilder.postprocess();
	    	  irbuilder.root.print();
          Semantics.check4 (irbuilder.root); 
	      }
	      catch(Exception e) {
	    	  e.printStackTrace();
	      }
	  	}
    }
	catch(Exception e) {
      System.err.println(CLI.infile + " " + e);
    }
  }
}