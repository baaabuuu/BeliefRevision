package base;

import java.util.ArrayList;


public class LookupNode
{
	String identifier;
	ArrayList<Integer> idList = new ArrayList<Integer>();
	Boolean hasCompared = false;
	boolean type;
	
	public LookupNode(String identifier, boolean type)
	{
		this.identifier = identifier;
		this.type = type;
	}
	
	public void addPos(Integer value)
	{
		idList.add(value);
	}
	
	
	public String toString()
	{
		String temp = (type) ? "" : "!";
		return "[" + temp  + identifier  + ", "+ idList.toString() + "]";
	}
	
	public void setCompare()
	{
		hasCompared = true;
	}
	
	

}
