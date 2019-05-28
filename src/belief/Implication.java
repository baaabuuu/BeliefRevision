package belief;

import java.util.HashSet;

public class Implication implements Belief
{
	Belief ogBelief1, ogBelief2;
	public Implication(Belief ogBelief1, Belief ogBelief2)
	{
		this.ogBelief1 = ogBelief1;
		this.ogBelief2 = ogBelief2;
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
		if (ogBelief1.getBelief())
		{
			return ogBelief2.getBelief();
		}
		return true;		
	}
	
	public String toString()
	{
		return "(" + ogBelief1.toString() + " -> " + ogBelief2.toString() + ")";
	}
	
	public boolean convertToCNF() {
		Belief negation = new Negation(ogBelief1);
		
		return negation.convertToCNF() || ogBelief2.convertToCNF();
	}

	@Override
	public String toCNFString()
	{
		Belief negation = new Negation(ogBelief1);
		
		return negation.toCNFString() + " \\/" + ogBelief2.toCNFString();
	}
	
	public Belief getCNF()
	{
		Belief negation = new Negation(ogBelief1);
		
		return new Or(negation.getCNF(), ogBelief2.getCNF());
	}
	
	public Belief getCNFAndOverOr()
	{
		return null;
	}
	
	@Override
	public HashSet<Belief> cuptupList(HashSet<Belief> belief)
	{
		return new HashSet<Belief>();
	}

	@Override
	public Belief moveNegationInwards() {
		return null;
	}
	
}
