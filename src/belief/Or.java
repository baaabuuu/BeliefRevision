package belief;

import java.util.HashSet;

import logger.Log;

public class Or implements Belief {
	Belief ogBelief1, ogBelief2;

	public Or(Belief ogBelief1, Belief ogBelief2) {
		this.ogBelief1 = ogBelief1;
		this.ogBelief2 = ogBelief2;
	}

	public Belief getOGbelief() {
		return ogBelief1;
	}

	public Belief getOGbelief2() {
		return ogBelief2;
	}

	public boolean getBelief() {
		return ogBelief1.getBelief() || ogBelief2.getBelief();
	}

	public String toString() {
		return "(" + ogBelief1.toString() + " \\/ " + ogBelief2.toString() + ")";
	}

	public boolean convertToCNF() {
		return ogBelief1.convertToCNF() || ogBelief2.convertToCNF();
	}

	@Override
	public String toCNFString() {
		return ogBelief1.toCNFString() + " \\/ " + ogBelief2.toCNFString();
	}

	public Belief getCNF() {
		return new Or(ogBelief1.getCNF(), ogBelief2.getCNF());
	}

	@Override
	public Belief getCNFAndOverOr() {
		if (ogBelief1 instanceof And) {
			Log.important("Was it this part?");
			Belief and1split = ogBelief1.getOGbelief();
			Belief and2split = ogBelief1.getOGbelief2();

			Belief or1 = new Or(and1split, ogBelief2);
			Belief or2 = new Or(and2split, ogBelief2);
			return new And(or1.getCNFAndOverOr(), or2.getCNFAndOverOr());
		} else if (ogBelief2 instanceof And && !(ogBelief1 instanceof And)) {
			Log.important("No this?");

			Belief and1split = ogBelief2.getOGbelief();
			Belief and2split = ogBelief2.getOGbelief2();

			Belief or1 = new Or(and1split, ogBelief1);
			Belief or2 = new Or(and2split, ogBelief1);
			return new And(or1.getCNFAndOverOr(), or2.getCNFAndOverOr());
		} 

		return new Or(ogBelief1.getCNFAndOverOr(), ogBelief2.getCNFAndOverOr());
	}
	
	@Override
	public HashSet<Belief> cuptupList(HashSet<Belief> belief)
	{
		return new HashSet<Belief>();
	}

	@Override
	public Belief moveNegationInwards() {
		return new Or(ogBelief1.moveNegationInwards(), ogBelief2.moveNegationInwards());
	}

}
