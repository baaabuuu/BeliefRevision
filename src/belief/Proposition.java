package belief;

import java.util.HashSet;

public class Proposition implements Belief
{
	public String identifier;
	
	public Proposition(String identifier)
	{
		this.identifier = identifier;
	}
	public Belief getOGbelief()
	{
		return null;
	}
	
	public Belief getOGbelief2()
	{
		return null;
	}
	
	
	public boolean getBelief()
	{
		return true;
	}
	
	public String toString()
	{
		return identifier;
	}
	
	public boolean convertToCNF()
	{
		return true;
	}
	
	public String toCNFString()
	{
		return identifier;
	}
	
	public Belief getCNF()
	{
		return new Proposition(identifier);
	}
	
	public Belief getCNFAndOverOr()
	{
		return new Proposition(identifier);
	}
	@Override
	public HashSet<Belief> cuptupList(HashSet<Belief> belief)
	{
		return new HashSet<Belief>();
	}
	
	@Override
	public Belief moveNegationInwards() {
		return new Proposition(identifier);
	}
	

	
}
