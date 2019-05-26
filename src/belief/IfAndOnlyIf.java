package belief;

import java.util.HashSet;

public class IfAndOnlyIf implements Belief
{
	Belief ogBelief1, ogBelief2;
	public IfAndOnlyIf(Belief ogBelief1, Belief ogBelief2)
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
		return ogBelief1.getBelief() == ogBelief2.getBelief();
	}
	
	public String toString()
	{
		return ogBelief1.toString() + " <-> " + ogBelief2.toString();
	}
	
	public boolean convertToCNF()
	{
		Belief imp1 = new Implication(ogBelief1, ogBelief2);
		Belief imp2 = new Implication(ogBelief2, ogBelief1);
		return imp1.convertToCNF() && imp2.convertToCNF();
	}
	
	public String toCNFString()
	{
		Belief imp1 = new Implication(ogBelief1, ogBelief2);
		Belief imp2 = new Implication(ogBelief2, ogBelief1);
		return imp1.toCNFString() + " /\\ " + imp2.toCNFString();
	}
	
	public Belief getCNF()
	{
		Belief imp1 = new Implication(ogBelief1, ogBelief2);
		Belief imp2 = new Implication(ogBelief2, ogBelief1);
		return new And(imp1.getCNF(), imp2.getCNF());
	}
	
	public Belief getCNFAndOverOr()
	{
		return null;
	}
	
	@Override
	public void cuptupList(HashSet<Belief> belief)
	{
	}

	@Override
	public Belief moveNegationInwards() {
		return null;
	}
	
	
	
	
}
