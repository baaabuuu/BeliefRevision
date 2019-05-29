package base;

import java.util.ArrayList;
import java.util.HashSet;

import belief.Belief;

public class ClauseTrace {
	private HashSet<Belief> left;
	private HashSet<Belief> right;
	private ArrayList<HashSet<Belief>> siblings;
	public ClauseTrace(HashSet<Belief> left ,HashSet<Belief> right, ArrayList<HashSet<Belief>> siblings) {
		
		this.left = left;
		this.right = right;
		this.siblings = siblings;
	}

	public HashSet<Belief> getLeft(){
		return this.left;
	}

	public HashSet<Belief> getRight(){
		return this.right;
	}
	
	public ArrayList<HashSet<Belief>> getSiblings(){
		return this.siblings;
	}
}
