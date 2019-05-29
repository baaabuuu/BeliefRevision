package base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;


import belief.And;
import belief.Belief;
import belief.IfAndOnlyIf;
import belief.Implication;
import belief.Negation;
import belief.Or;
import belief.Proposition;
import logger.Log;

public class RBeliefBase {
	HashSet<Belief> beliefsNCNF; //Non CNF beliefs, needed for resetting the trace
	HashSet<Belief> beliefs;
	HashSet<Belief> workingBeliefs;
	static HashMap<String, ClauseTrace> trace = new HashMap<String,ClauseTrace>();
	public RBeliefBase(HashSet<Belief> beliefs){
		this.beliefs = toCNF(beliefs);
		this.beliefsNCNF = beliefs;
		setBeliefs(this.beliefs);
	}

	private void setBeliefs(HashSet<Belief> beliefs) {
		this.workingBeliefs =beliefs;
	}
public static void main(String[] args) {
	Log.debug(true);
	HashSet<Belief> inputBeliefs = new HashSet<Belief>();
	Proposition p = new Proposition("P");
	Proposition q = new Proposition("Q");
	Proposition s = new Proposition("S");
	inputBeliefs.add(p);
	inputBeliefs.add(s);
	inputBeliefs.add(new Implication(s,q));
	inputBeliefs.add(new Implication(p,q));
	//inputBeliefs.add(new Implication(p,q));
	//inputBeliefs.add(new IfAndOnlyIf(p,q));
	//inputBeliefs.add(new Or(p,q));
	//inputBeliefs.add(new And(p,q));
	
	
	
	//inputBeliefs.add(new Or (new Proposition("Q"),new Negation(new Proposition("Q"))));
	RBeliefBase R = new RBeliefBase(inputBeliefs);
	//R.resolve(R.baseToLiteral(R.beliefs),new Proposition("R"));
	
	Log.log(R.beliefsNCNF.toString());
	R.contract(q);
}
	private HashSet<Belief> toCNF(HashSet<Belief> inputBeliefs){
		HashSet<Belief> tmp = new HashSet<Belief>();
		for (Belief belief : inputBeliefs) {
			tmp.add(belief.getCNF().moveNegationInwards().getCNFAndOverOr());
		}
		HashSet<Belief> CNF = new HashSet<Belief>();
		for (Belief belief : tmp) 
		{
			
			HashSet<Belief> splitResult =belief.cuptupList(CNF);
			
			//If size is unchanged
			// Own ID (They are the same, so they must be top level
			if (splitResult.isEmpty())
			{
				CNF.add(belief);
				trace.put(extractLiterals(belief).toString(), new ClauseTrace(extractLiterals(belief),extractLiterals(belief),null));
		
				
				

			}else {
				CNF.addAll(splitResult);
				for(Belief b : splitResult) {
					
					HashSet<Belief> tempSet = new HashSet<Belief>();
					tempSet.addAll(splitResult);
					tempSet.remove(b);
					ArrayList<HashSet<Belief>> tempList = new ArrayList<HashSet<Belief>>();
					tempList.add(tempSet);
					trace.put(extractLiterals(b).toString(), new ClauseTrace(extractLiterals(b),extractLiterals(b),tempList));
					
				}
				
			}
			
		}
		return CNF;
	}
	

	
	
	public HashSet<HashSet<Belief>> resolve(HashSet<HashSet<Belief>> cnfLiterals,Belief query) {
		
		HashSet<HashSet<Belief>> resolutionSet = cnfLiterals;
		HashSet<Belief> querySet = new HashSet<Belief>();
		querySet.add(new Negation(query));
		querySet = toCNF(querySet);
		
		querySet.forEach(s ->resolutionSet.add(extractLiterals(s)));
		
		
		ArrayList<HashSet<Belief>> setNotation = new ArrayList();
		
		
		setNotation.addAll(resolutionSet);

		Boolean searching = true;
		Boolean noEmptySets = true;
		while(searching && noEmptySets) {
			int size = setNotation.size();
			//Sort to prioritize literals
			setNotation.sort(Comparator.comparing(HashSet::size));
			
			HashSet<HashSet<Belief>> result = new HashSet<HashSet<Belief>>();
			HashSet<HashSet<Belief>> setNotationAsSet = new HashSet<HashSet<Belief>>();
			
			
			setNotationAsSet.addAll(setNotation);
			for(int i = 0; i <setNotation.size();i++) {
				for(int j = i; j < setNotation.size(); j++) {
					if(i !=j) {
						ArrayList<HashSet<Belief>> tempResult = tryResolve(setNotation.get(i),setNotation.get(j));
						
						result.addAll(tempResult);
						if(tempResult.size() > 0 && tempResult.get(0).toString().equals("[]")) {
						noEmptySets = false;
						break;}
					}
					
			}
				if(!noEmptySets) break;
			}
			
			//Clearning to make sure we can use the set properties and still access via array
			setNotationAsSet.addAll(result);
			setNotation.clear();
			setNotation.addAll(setNotationAsSet);
			if(setNotation.size() == size) {
				searching= false;
			}
		
			if(!noEmptySets) {
				//Successfully resolved empty clause
				
				HashSet<HashSet<Belief>> output = new HashSet<HashSet<Belief>>();
				output.addAll(setNotation);
				
				return output;
			}
		
		}
	
		
		return null;	
		
	}
	



	private ArrayList<HashSet<Belief>> tryResolve(HashSet<Belief> clause1, HashSet<Belief> clause2) {
		ArrayList<HashSet<Belief>> result = new ArrayList();
		
			//This seems to be the only way to compare them :(
		List<Belief> hashSet = Arrays.asList(clause1.toArray(new Belief[clause1.size()]));
			List<Belief> hashSet2 = Arrays.asList(clause2.toArray(new Belief[clause2.size()]));
 	
 
 	
       for(Belief c1 : hashSet) {
    	   for(Belief c2 : hashSet2) {
    		   
    		   if( ((c1 instanceof Proposition) && (c2 instanceof Negation) && c1.toString() == c2.getOGbelief().toString()) ||
    			   ((c1 instanceof Negation) && (c2 instanceof Proposition) && c2.toString() == c1.getOGbelief().toString()) ){
    					
    					List<Belief> resulting = new ArrayList<Belief>();
    					HashSet<Belief> resultingSet = new HashSet<Belief>();
    					
    					resulting.addAll(hashSet);
    					resulting.addAll(hashSet2);
    				
    					resulting.remove(c1);
    					resulting.remove(c2);
    					if(resulting.toString().equals("["+c1.toString()+"]") ||
    					   resulting.toString().equals("["+c2.toString()+"]")) break; // Dont bother adding duplicates This is a disgusting way of doing this, i am very sorry.
    					
    					resultingSet.addAll(resulting);
    					result.add(resultingSet);
    					trace.put(resulting.toString(), new ClauseTrace(clause1,clause2,null));
    					if(resulting.size() == 0) {
    						return result;
    						
    					} 
    						
    						
    					
    				} 
    		   
    	   }
    	 
       }

		return result;
	}
		//use linked HashSets to maintain insertion order
	private LinkedHashSet<Belief> recursebelief(Belief clause) {
		LinkedHashSet<Belief> temp = new LinkedHashSet();
		
		if(clause instanceof Negation || clause instanceof Proposition) {
			temp.add(clause);
			return temp;
		}
		
		if(clause instanceof Or) {
			temp.addAll(recursebelief(clause.getOGbelief()));
			temp.addAll(recursebelief(clause.getOGbelief2()));
		}
		
		if(clause instanceof And) {
			temp.addAll(recursebelief(clause.getOGbelief()));
			temp.addAll(recursebelief(clause.getOGbelief2()));
		}
		
		
		return temp;
		
	}
	
	private LinkedHashSet<Belief> extractLiterals(Belief clause){
		
		LinkedHashSet<Belief>literals = recursebelief(clause);
		return literals;
		
	}
	
	
	public HashSet<Belief> contract(Belief knowledge){
		//Housekeeping
		HashSet<Belief> knowledgeState = new HashSet<Belief>();
		knowledgeState.addAll(beliefsNCNF);
		HashSet<Belief> negatedQuery = new HashSet<Belief>();
		negatedQuery.add(new Negation(knowledge));
		
		// Load non CNF to rebuild the trace
		trace.clear(); //Clear old trace
		knowledgeState = toCNF(knowledgeState); // Done For side effect of updating the trace
		negatedQuery = toCNF(negatedQuery);
		
		Log.log("CNF: "+knowledgeState.toString());
		HashSet<HashSet<Belief>> literalSet = new HashSet<HashSet<Belief>>();
		negatedQuery.forEach(c -> literalSet.add(extractLiterals(c)));
		
		
		//Resolve Updates trace
		HashSet<HashSet<Belief>> resolved = resolve(baseToLiteral(beliefs),knowledge); // This updates the resolution trace
							//Now we can follow the trace and get a list of root clauses
		
		if(resolved == null) {
			//This means we have nothing contradicting in the beliefbase
			
			return knowledgeState;
		}else {
			//Do the magic
			
			
			
			ArrayList<HashSet<Belief>> rootClauses = getRootClauses();
			HashSet<HashSet<Belief>> clauseSet = new HashSet<HashSet<Belief>>();
			
			clauseSet.addAll(rootClauses);
			clauseSet = subtractSets(clauseSet,literalSet);
	
			//We need the beliefbase in literalform to modify, so we get that and then remove each clause
			ArrayList<HashSet<HashSet<Belief>>> potential = new ArrayList();
			HashSet<HashSet<Belief>> literalBase = baseToLiteral(knowledgeState);
			ArrayList<HashSet<HashSet<Belief>>> remainders = recursiveContract(knowledge, literalBase,literalSet);
			
			Log.log("Remainders: "+remainders.toString());
			
			HashSet<HashSet<Belief>> newBeliefBase = gamma(remainders);
		
			
		
		return knowledgeState;
		
	}
	}
	private HashSet<HashSet<Belief>> gamma(ArrayList<HashSet<HashSet<Belief>>> remainders) {
		// TODO Auto-generated method stub
		
		ArrayList<ArrayList<HashSet<HashSet<Belief>>>> intersector = powerSet(remainders);
		
		for(ArrayList<HashSet<HashSet<Belief>>> combination : intersector) {
			HashSet<HashSet<Belief>> temp = new HashSet<>();
			
			
			
			
		}
		
			
			int max =0;
			HashSet<HashSet<Belief>> best = new HashSet<HashSet<Belief>>() ;
			for(ArrayList<HashSet<HashSet<Belief>>> a : intersector) {
				int score = 0;
				if(a.size()> 0) {
					
					HashSet<HashSet<Belief>> intersection = new HashSet();
					
					intersection.addAll(a.get(0));
				
					for(HashSet<HashSet<Belief>> b : a) {
						
						intersection.retainAll(b);
						
					}
					for(HashSet<Belief> b : intersection) {
						score+= b.size();
					}
					if(score>max) {
						max=score;
						best = intersection;
					}
					
					
				}
				
				
			}
			Log.log("New BeliefBase: "+best.toString());
		return best;
	}
	//Algorithm from
	//https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
	   public static ArrayList<ArrayList<HashSet<HashSet<Belief>>>> powerSet(ArrayList<HashSet<HashSet<Belief>>> originalSet) {
	        
		   ArrayList<ArrayList<HashSet<HashSet<Belief>>>> sets = new ArrayList<>();
	        if (originalSet.isEmpty()) {
	            sets.add(new ArrayList<HashSet<HashSet<Belief>>>());
	            return sets;
	        }
	        
	        HashSet<HashSet<Belief>> head = originalSet.get(0);
	       
	        ArrayList<HashSet<HashSet<Belief>>> rest = new ArrayList<HashSet<HashSet<Belief>>>(originalSet.subList(1, originalSet.size()));
	        for (ArrayList<HashSet<HashSet<Belief>>> set : powerSet(rest)) {
	        	ArrayList<HashSet<HashSet<Belief>>> newSet = new ArrayList<>();
	            newSet.add(head);
	            newSet.addAll(set);
	            sets.add(newSet);
	            sets.add(set);
	         
	        }
	        return sets;
	    }
	private ArrayList<HashSet<HashSet<Belief>>> recursiveContract(Belief knowledge, HashSet<HashSet<Belief>> literalBase, HashSet<HashSet<Belief>> literalSet) {
		ArrayList<HashSet<HashSet<Belief>>> potential = new ArrayList<HashSet<HashSet<Belief>>>();
		ArrayList<HashSet<HashSet<Belief>>> valid = new ArrayList();
		ArrayList<HashSet<Belief>> rootClauses = getRootClauses();
		HashSet<HashSet<Belief>> cSet = new HashSet<HashSet<Belief>>();
		cSet.addAll(rootClauses);
		////////THIS IS WHAT HAPPENS WHEN JAVA CAN'T .equals :( //////
		cSet = subtractSets(cSet,literalSet);
		for(HashSet<Belief> c : cSet) {
			
			HashSet<HashSet<Belief>> wrapper = new HashSet<HashSet<Belief>>();
			wrapper.add(c);
			//Log.log("Step:"+c.toString());
			ArrayList<HashSet<Belief>> linkedBeliefs = trace.get(c.toString()).getSiblings();
			if(linkedBeliefs != null) {
			//	Log.log("Sibling found");
				linkedBeliefs.forEach(b -> b.forEach(s -> wrapper.add(extractLiterals(s))));
				
			}
		//	Log.log("wrapper: "+wrapper.toString());
			HashSet<HashSet<Belief>> baseCopy = new HashSet<HashSet<Belief>>();
			baseCopy.addAll(literalBase);
			baseCopy = subtractSets(baseCopy,wrapper);
			
			potential.add(baseCopy);
			//Log.important("BaseCopy: "+ baseCopy.toString());
			
		}
		
		for(HashSet<HashSet<Belief>> base : potential) {
			trace.clear();	/////////////////////////////IMPORTANT ////////////////////////// Clearing trace is really important, otherwise baby will die
		
			base.forEach(b -> trace.put(b.toString(), new ClauseTrace(b,b,null)));
			
			HashSet<HashSet<Belief>> output = resolve(base,knowledge);
			if(output != null) {
				valid.addAll(recursiveContract(knowledge,base,literalSet));
				
			} 
			else {
				valid.add(subtractSets(base,literalSet));
			}

			
			
		}
		
		// TODO Auto-generated method stub
		return valid;
	}

	private HashSet<HashSet<Belief>> baseToLiteral(HashSet<Belief> base){
		
		HashSet<HashSet<Belief>> output = new HashSet<HashSet<Belief>>();
		
		base.forEach(s -> output.add(extractLiterals(s)));
		
		return output;
	}
	private HashSet<HashSet<Belief>> subtractSets(HashSet<HashSet<Belief>> s1,HashSet<HashSet<Belief>> s2){
		HashSet<HashSet<Belief>> output = new HashSet<HashSet<Belief>>();
		output.addAll(s1);
		for(HashSet<Belief> hs : s1) {
			for(HashSet<Belief> bs : s2) {
				if(hs.toString().equals(bs.toString())) {
					output.remove(hs);
			}
			
		}
		}
		
		
		return output;
	}
	private ArrayList<HashSet<Belief>> getRootClauses(){
		
		String p1 = trace.get("[]").getLeft().toString();
		String p2 = trace.get("[]").getRight().toString();
		
		
		
		return getParents(trace.get("[]"));
	}
	
	private ArrayList<HashSet<Belief>> getParents(ClauseTrace c){
		if(c == null) return null;
		ArrayList<HashSet<Belief>> result = new ArrayList<HashSet<Belief>>();
		String key1 = c.getLeft().toString();
		String key2 = c.getRight().toString();
		if(key1.equals( key2)) { // If both parents are the same then we are at the top and we add ourselves ( which is also our parent)
			result.add(c.getLeft());
			return result;
		} 
		ClauseTrace parentKey1 = trace.get(c.getLeft().toString());
		ClauseTrace parentKey2 = trace.get(c.getRight().toString());
		result.addAll(getParents(parentKey1));
		result.addAll(getParents(parentKey2));
		return result;
	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	public void SpookyResolution(HashSet<Belief> input) {
		
		ArrayList<ArrayList<PropositionNode>> listOfElems = new ArrayList<ArrayList<PropositionNode>>();
		ArrayList<LookupNode> lookups = new ArrayList<LookupNode>();
		ArrayList<Belief> list = new ArrayList<Belief>(input);
		for (int i = 0; i < list.size(); i++)
		{
			Belief belief = list.get(i);
			ArrayList<PropositionNode> generatedList = new ArrayList<PropositionNode>();
			handleProposition(belief, generatedList, i, lookups);
			generatedList.sort(Comparator.comparing(Object::toString));
			listOfElems.add(generatedList);
		}
		
	HashSet<ArrayList<PropositionNode>> newListElems = new HashSet<ArrayList<PropositionNode>>(listOfElems);
		
		Log.important("our nodes are:");
		
		for (LookupNode node : lookups)
		{
			if (node.hasCompared != true)
			{
				Optional<LookupNode> similiarSymbol = lookups.parallelStream().filter(compNode ->
						node.identifier.equals(compNode.identifier)
						&& node != compNode).findAny();
				if (similiarSymbol.isPresent())
				{
					LookupNode otherNode = similiarSymbol.get();
					otherNode.hasCompared = true;
					for (Integer nodeElem : node.idList)
					{
						for (Integer otherNodeElem : otherNode.idList)
						{
							ArrayList<PropositionNode> tempList = new ArrayList<PropositionNode> (listOfElems.get(nodeElem));
							ArrayList<PropositionNode> tempNewList = new ArrayList<PropositionNode> ();
							for(PropositionNode compNode : tempList)
							{
								if (!(compNode.identifier.equals(node.identifier) && compNode.status == node.type))
								{
									tempNewList.add(compNode);
								}
							}
							tempList = new ArrayList<PropositionNode> (listOfElems.get(otherNodeElem));
							for(PropositionNode compNode : tempList)
							{
								if (!(compNode.identifier.equals(node.identifier) && compNode.status == otherNode.type))
								{
									tempNewList.add(compNode);
								}
							}
							tempNewList.sort(Comparator.comparing(Object::toString));
							newListElems.add(tempNewList);
						}
					}
					
				}

			}
		}
		for(ArrayList<PropositionNode> p : newListElems) {
			Log.log(p.toString());
		}
		Log.important("Then we know that:");
		
	}
	
	private void handleProposition(Belief belief, ArrayList<PropositionNode> generatedList, int i, ArrayList<LookupNode> lookups) {
		if (belief instanceof Or) {
			Belief beliefChild1 = belief.getOGbelief();
			Belief beliefChild2 = belief.getOGbelief2();
			
			handleProposition(beliefChild1, generatedList, i, lookups);
			handleProposition(beliefChild2, generatedList, i, lookups);
		} else if (belief instanceof Negation)
		{
			PropositionNode node = new PropositionNode(belief.getOGbelief().toString(), false);
			generatedList.add(node);
			boolean notFound = true;
			for (LookupNode lookupNode : lookups)
			{
				if(lookupNode.identifier.equals(node.identifier) && lookupNode.type == node.status)
				{
					notFound = false;
					lookupNode.addPos(i);
					break;
				}
			}
			if (notFound)
			{

				LookupNode newNode = new LookupNode(node.identifier, node.status);
				newNode.addPos(i);
				lookups.add(newNode);
			}		
		} else if (belief instanceof Proposition) {
			PropositionNode node = new PropositionNode(belief.toString(), true);
			generatedList.add(node);
			boolean notFound = true;
			for (LookupNode lookupNode : lookups)
			{
				if(lookupNode.identifier.equals(node.identifier)  && lookupNode.type == node.status)
				{
					notFound = false;
					lookupNode.addPos(i);
					break;
				}
			}
			if (notFound)
			{
				LookupNode newNode = new LookupNode(node.identifier, node.status);
				newNode.addPos(i);
				lookups.add(newNode);


			}
		}
	}
	
	
}
