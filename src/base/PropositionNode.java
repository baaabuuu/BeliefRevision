package base;

import belief.Belief;

public class PropositionNode implements Comparable< PropositionNode >
{
	String identifier;
	Boolean status = false;
	Boolean checked = false;
	
	public PropositionNode(String identifier, boolean status)
	{
		this.identifier = identifier;
		this.status = status;
	}
	
	public String toString()
	{
		return (status) ? identifier : "!"+identifier;
	}
	
    @Override
    public int compareTo(PropositionNode o) {
    	toString();
        return toString().compareTo(o.toString());
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PropositionNode && ((PropositionNode) obj).status == this.status && ((PropositionNode) obj).identifier == this.identifier);

    }
    
    
    @Override 
    public int hashCode()
    { 
      return toString().hashCode();
    }

}
