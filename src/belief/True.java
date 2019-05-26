package belief;

import java.util.HashSet;

public class True implements Belief
{
	public boolean getBelief()
	{
		return true;
	}
	
	public boolean convertToCNF()
	{
		return true;
	}
	
	public String toString()
	{
		return "true";
	}

	@Override
	public String toCNFString() {
		return "true";
	}
	
	public Belief getOGbelief()
	{
		return null;
	}
	
	public Belief getOGbelief2()
	{
		return null;
	}
	

	@Override
	public Belief getCNF() {
		return new True();
	}
	
	public Belief getCNFAndOverOr()
	{
		return new True();
	}
	
	@Override
	public void cuptupList(HashSet<Belief> belief)
	{
	}

	@Override
	public Belief moveNegationInwards() {
		return new True();
	}
	
	
}
