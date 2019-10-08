package edu.mit.compilers.semantics;
import edu.mit.compilers.grammar.*;

public class IR {
	private ParseTree parseTree;
	public IR(ParseTree parseTree) {
		this.parseTree = parseTree;
	}
	
	
	public void build()
	{
		
	}
	
	public interface Node
	{
		
	}
	public class IRExpression implements Node
	{
		
	}
}