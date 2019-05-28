package base;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

import belief.Belief;
import belief.Implication;
import belief.Negation;
import belief.Or;
import belief.Proposition;
import logger.Log;

public class RBeliefBase {
	HashSet<Belief> beliefs;
	
	public RBeliefBase(HashSet<Belief> beliefs){
		this.beliefs = toCNF(beliefs);
		
	}
	
public static void main(String[] args) {
	Log.debug(true);
	HashSet<Belief> inputBeliefs = new HashSet<Belief>();
	Proposition p = new Proposition("P");
	inputBeliefs.add(p);
	inputBeliefs.add(new Implication(p,new Proposition("Q")));
	RBeliefBase R = new RBeliefBase(inputBeliefs);
	
	R.SpookyResolution(R.beliefs);
}
	
	private HashSet<Belief> toCNF(HashSet<Belief> inputBeliefs){
		HashSet<Belief> tmp = new HashSet<Belief>();
		for (Belief belief : inputBeliefs) {
			tmp.add(belief.getCNF().moveNegationInwards().getCNFAndOverOr());
		}
		HashSet<Belief> CNF = new HashSet<Belief>();
		for (Belief belief : tmp) 
		{
			int size = CNF.size();
			belief.cuptupList(CNF);
			//If size is unchanged
			if (CNF.size() == size)
			{
				CNF.add(belief);

			}
		}
		return CNF;
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
