package belief;

import java.util.HashSet;

public class And implements Belief
{
	Belief ogBelief1, ogBelief2;
	public And(Belief ogBelief1, Belief ogBelief2)
	{
		this.ogBelief1 = ogBelief1;
		this.ogBelief2 = ogBelief2;
	}
	
	public Belief getOGbelief()
	{
		return ogBelief1;
	}
	
	public Belief getOGbelief2()
	{
		return ogBelief2;
	}
	
	
	public boolean getBelief()
	{
		return ogBelief1.getBelief() && ogBelief2.getBelief();
	}
	
	public String toString()
	{
		return "(" + ogBelief1.toString() + " /\\ " + ogBelief2.toString() + ")";
	}

	public boolean convertToCNF() {
		return ogBelief1.convertToCNF() && ogBelief2.convertToCNF();
	}
	
	public String toCNFString()
	{
		return "(" + ogBelief1.toCNFString() + ") /\\ (" + ogBelief2.toCNFString() + ")";
	}
	
	public Belief getCNF()
	{
		return new And(ogBelief1.getCNF(), ogBelief2.getCNF());
	}
	
	public Belief getCNFAndOverOr()
	{
		return new And(ogBelief1.getCNFAndOverOr(), ogBelief2.getCNFAndOverOr());
	}

	@Override
	public HashSet<Belief> cuptupList(HashSet<Belief> belief)
	{
		HashSet<Belief> result = new HashSet<Belief>();
		if (ogBelief1 instanceof And)
		{
			result.addAll(ogBelief1.cuptupList(belief));

		}
		else
		{
			result.add(ogBelief1);
		}
		if (ogBelief2 instanceof And)
		{
			result.addAll(ogBelief2.cuptupList(belief));

		}
		else
		{
			result.add(ogBelief2);
		}
		return result;
	}
	
	

	public Belief moveNegationInwards() {
		return new And(ogBelief1.moveNegationInwards(), ogBelief2.moveNegationInwards());
	}
	
}
