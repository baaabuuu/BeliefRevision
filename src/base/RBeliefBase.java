package base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import java.util.LinkedHashMap;
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
	HashSet<Belief> beliefs;
	LinkedHashMap<String,HashMap<String,ArrayList<Integer>>> ledger = new LinkedHashMap<String,HashMap<String,ArrayList<Integer>>>();
	
	public RBeliefBase(HashSet<Belief> beliefs){
		this.beliefs = toCNF(beliefs);
		
	}

public static void main(String[] args) {
	Log.debug(true);
	HashSet<Belief> inputBeliefs = new HashSet<Belief>();
	Proposition p = new Proposition("P");
	inputBeliefs.add(p);
	inputBeliefs.add(new Implication(p,new Proposition("Q")));
	inputBeliefs.add(new IfAndOnlyIf(new Proposition("Q"),new Proposition("R")));
	RBeliefBase R = new RBeliefBase(inputBeliefs);
	R.resolve(new Proposition("R"));
}
	
	private HashSet<Belief> toCNF(HashSet<Belief> inputBeliefs){
		HashSet<Belief> tmp = new HashSet<Belief>();
		for (Belief belief : inputBeliefs) {
			tmp.add(belief.getCNF().moveNegationInwards().getCNFAndOverOr());
		}
		int i =0;
		HashSet<Belief> CNF = new HashSet<Belief>();
		for (Belief belief : tmp) 
		{
			
			HashSet<Belief> splitResult =belief.cuptupList(CNF);
			
			//If size is unchanged
			HashMap<String,ArrayList<Integer>> temp = new HashMap<String,ArrayList<Integer>>();
			ArrayList<Integer> tempValue = new ArrayList<Integer>();
			tempValue.add(i);
			temp.put("P", tempValue); //Parent ID
			temp.put("S",tempValue); // Own ID (They are the same, so they must be top level
			if (splitResult.isEmpty())
			{
				CNF.add(belief);
				
				ledger.put(extractLiterals(belief).toString(), temp);
				
				

			}else {
				CNF.addAll(splitResult);
				for(Belief b : splitResult) {
					
					ledger.put(extractLiterals(b).toString(), temp);
				}
				
			}
			i++;
		}
		return CNF;
	}
	
	
	
	public HashSet<HashSet<Belief>> resolve(Belief query) {
		
		HashSet<Belief> resolutionSet = beliefs;
		Belief negatedQuery = (new Negation(query)).getCNF().moveNegationInwards().getCNFAndOverOr();
		resolutionSet.add(negatedQuery);
		HashMap<String,ArrayList<Integer>> temp = new HashMap<String,ArrayList<Integer>>();
		ArrayList<Integer> tempValue = new ArrayList<Integer>();
		tempValue.add(ledger.size());
		temp.put("P", tempValue); //Parent ID
		temp.put("S",tempValue); //Parent ID own ID (Should be same
		ledger.put(extractLiterals(negatedQuery).toString(), temp);
		
		
		
		ArrayList<HashSet<Belief>> setNotation = new ArrayList();
		resolutionSet.forEach(b -> setNotation.add(extractLiterals(b)));
		

		Boolean searching = true;
		Boolean noEmptySets = true;
		while(searching && noEmptySets) {
			setNotation.sort(Comparator.comparing(HashSet::size));
			Log.important(setNotation.toString());
			ArrayList<HashSet<Belief>> result = new ArrayList();
		
		
			for(int i = 0; i <setNotation.size();i++) {
				for(int j = i; j < setNotation.size(); j++) {
				result.addAll(tryResolve(setNotation.get(i),setNotation.get(j)));
				}
			}
			if(result.size() == 0) {
			searching= false;
			}
			setNotation.addAll(result);
		
		
			if(setNotation.stream().filter(s -> s.isEmpty() ).toArray().length !=0) {
				//Successfully resolved empty clause
				noEmptySets = false;
				HashSet<HashSet<Belief>> output = new HashSet<HashSet<Belief>>();
				output.addAll(setNotation);
				Log.log("Resolved: "+output.toString());
				return output;
			}
		
		}
	
		
		return null;	
		
	}
	



	private ArrayList<HashSet<Belief>> tryResolve(HashSet<Belief> clause1, HashSet<Belief> clause2) {
		ArrayList<HashSet<Belief>> result = new ArrayList();
		//Currently not exit efficient
		
			//This seems to be the only way to compare them :(
 List<Belief> hashSet = Arrays.asList(clause1.toArray(new Belief[clause1.size()]));
 List<Belief> hashSet2 = Arrays.asList(clause2.toArray(new Belief[clause2.size()]));
 
 
 	
		
       for(Belief c1 : hashSet) {
    	   for(Belief c2 : hashSet2) {
    		   
    		   if( ((c1 instanceof Proposition) && (c2 instanceof Negation) && c1.toString() == c2.getOGbelief().toString()) ||
    			   ((c1 instanceof Negation) && (c2 instanceof Proposition) && c2.toString() == c1.getOGbelief().toString()) ){
    					
    					HashSet<Belief> resulting = new HashSet<Belief>();
    					
    					Log.log(ledger.toString());
    					resulting.addAll(hashSet);
    					resulting.addAll(hashSet2);
    					resulting.remove(c1);
    					resulting.remove(c2);
    					
    					result.add(resulting);
    					ledger.put(resulting.toString(), createLedgerEntry(clause1,clause2));
    					if(resulting.size() == 0) {
    						return result;
    						
    					} 
    						
    						
    					
    				} 
    		   
    	   }
    	 
       }
		
		
		
			
		
		
		
		
		return result;
	}
private HashMap<String,ArrayList<Integer>> createLedgerEntry( HashSet<Belief> left, HashSet<Belief> right ) {
	HashMap<String,ArrayList<Integer>> temp = new HashMap<String,ArrayList<Integer>>();
	ArrayList<Integer> tempValue = new ArrayList<Integer>();
	ArrayList<Integer> tempValue2 = new ArrayList<Integer>();
	
	String key1 = left.toString();
	String key2= right.toString();
	
	
	tempValue.add(ledger.get(key1).get("S").get(0));
	tempValue.add(ledger.get(key2).get("S").get(0));
	tempValue2.add(ledger.size());
	
	temp.put("P", tempValue); //Parent ID
	temp.put("S",tempValue2);
	return temp;
	
}
	private HashSet<Belief> recursebelief(Belief clause) {
		HashSet<Belief> temp = new HashSet();
		
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
	
	private HashSet<Belief> extractLiterals(Belief clause){
		
		HashSet<Belief>literals = recursebelief(clause);
		Log.log(literals.toString());
		return literals;
		
	}
	
	
	public HashSet<Belief> revise(Belief knowledge){
		//Housekeeping
		HashSet<Belief> knowledgeState = beliefs;
		//Add negated new information
		knowledgeState.add(new Negation(knowledge));
		//Set working set to CNF
		knowledgeState = toCNF(knowledgeState);
		
		
		
		return knowledgeState;
		
	}
	
	private HashSet<Belief> contract(HashSet<Belief> base){
		
		
		
		
		return null;
		
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
