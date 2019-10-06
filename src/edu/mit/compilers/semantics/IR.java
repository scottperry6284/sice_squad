package edu.mit.compilers.semantics;
import edu.mit.compilers.grammar.*;

public class IR {
	private ParseTree parseTree;
	public IR(DecafParser parser) {
		this.parseTree = new ParseTree(parser);
	}
	
	
	public void build()
	{
		parseTree.build();
	}
	
	public interface Node
	{
		
	}
	public class IRExpression implements Node
	{
		
	}
}