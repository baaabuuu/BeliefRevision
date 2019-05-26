package belief;

import java.util.HashSet;

import logger.Log;

public class Negation implements Belief
{
	Belief ogBelief;
	public Negation(Belief ogBelief)
	{
		this.ogBelief = ogBelief;
	}
	
	public Belief getOGbelief()
	{
		return ogBelief;
	}
	
	public Belief getOGbelief2()
	{
		return null;
	}
	
	
	public boolean getBelief()
	{
		return !ogBelief.getBelief();
	}
	
	public String toString()
	{
		return "!" + ogBelief.toString() +"";
	}
	
	public boolean convertToCNF()
	{
		return !ogBelief.convertToCNF();
	}

	@Override
	public String toCNFString() {
		return "!" + ogBelief.toCNFString();

	}

	public Belief getCNF()
	{
		if (ogBelief instanceof Negation)
		{
			return ogBelief.getOGbelief().getCNF();
		}


		return new Negation(ogBelief.getCNF());
	}
	
	public Belief getCNFAndOverOr()
	{
		
		return new Negation(ogBelief.getCNFAndOverOr());
	}
	
	@Override
	public void cuptupList(HashSet<Belief> belief)
	{
	}

	@Override
	public Belief moveNegationInwards() {
		if (ogBelief instanceof And)
		{
			Log.important("Do we push in Or?");
			Belief belief1 = ogBelief.getOGbelief();
			Belief belief2 = ogBelief.getOGbelief2();
			return new Or(new Negation(belief1).moveNegationInwards(), new Negation(belief2).moveNegationInwards());
		}
		if (ogBelief instanceof Or)
		{
			Log.important("Do we push in Or?");
			Belief belief1 = ogBelief.getOGbelief();
			Belief belief2 = ogBelief.getOGbelief2();
			return new And(new Negation(belief1).moveNegationInwards(), new Negation(belief2).moveNegationInwards());
		}
		if (ogBelief instanceof Negation)
		{
			return ogBelief.getOGbelief().moveNegationInwards();
		}
		return new Negation(ogBelief.moveNegationInwards());
	}
	
	
}
