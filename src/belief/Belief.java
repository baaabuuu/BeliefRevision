package belief;

import java.util.HashSet;

public interface Belief
{
	public boolean getBelief();
	public String toString();
	public boolean convertToCNF();
	public String toCNFString();
	public Belief getCNF();
	public Belief moveNegationInwards();
	public Belief getCNFAndOverOr();
	public Belief getOGbelief();
	public Belief getOGbelief2();
	public void cuptupList(HashSet<Belief> belief);
}
