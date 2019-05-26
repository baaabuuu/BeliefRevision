package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import belief.Belief;
import belief.IfAndOnlyIf;
import belief.Implication;
import belief.And;
import belief.Negation;
import belief.Or;
import belief.Proposition;
import belief.True;
import logger.Log;

public class BeliefBase {

	HashSet<Belief> beliefs = new HashSet<Belief>();

	public BeliefBase() {
		/*
		Belief propR = new Proposition("r");
		Belief propP = new Proposition("p");
		Belief propS = new Proposition("s");
		
		Belief propRNegated = new Negation(propR);
		Belief propPNegated = new Negation(propP);
		Belief propSNegated = new Negation(propS);
		
		Belief or1 = new Or(propRNegated, propP);
		or1 = new Or(or1, propS);
		Belief or2 = new Or(propPNegated, propR);
		Belief or3 = new Or(propSNegated, propR);
		
		Belief and1 = new And(or1, or2);
		Belief and2 = new And(and1, or3);
		Belief and3 = new And(and2, propRNegated);
		
		
		Belief propOK = new Proposition("OK");
		Belief propNO = new Proposition("NO");
		
		Belief implicationOKNO = new Implication(propOK, propNO);
		
		beliefs.add(propOK);
		beliefs.add(implicationOKNO);
		
		
		

		
		
		beliefs.add(and3);
		*/
		Belief prop1 = new Proposition("P");
		Belief prop2 = new Proposition("Q");
		
		
		Belief tempOr = new Implication(prop1, prop2);
		beliefs.add(prop1);
		
		
		beliefs.add(tempOr);
		


	}

	public static void main(String[] args) {
		Log.debug(true);
		new BeliefBase().checkBeliefs();
	}

	private void checkBeliefs() {
		HashSet<Belief> CNFBeliefs = new HashSet<Belief>();
		Log.important("Starting a new BeliefCheck on the model: ");
		for (Belief belief : beliefs) {
			Log.log(belief.toString());
		}

		//Removing implcation etc.
		Log.important("Starting a new CNF BeliefCheck on the model: ");
		for (Belief belief : beliefs) {
			Log.log("Coverted to CNF belief model: " + belief.getCNF().toString());
			CNFBeliefs.add(belief.getCNF());
		}
		
		//Moving negation inwards (demorgan)
		HashSet<Belief> InwardsNegation = new HashSet<Belief>();
		Log.important("Moving Negation inwards: ");
		for (Belief belief : CNFBeliefs) {
			Log.important("before: " + belief.toString());

			Belief tempBelief = belief.moveNegationInwards();
			Log.important(" after: " + tempBelief.toString());
			InwardsNegation.add(tempBelief);
		}
		
		


		HashSet<Belief> AndOverOrBeliefs = new HashSet<Belief>();
		Log.important("Distributing And over Or: ");

		for (Belief belief : InwardsNegation) {
			Belief tempBelief = belief.getCNFAndOverOr();
			Log.log("Coverted result: " + tempBelief.toString());
			AndOverOrBeliefs.add(tempBelief);
		}
		
		
		HashSet<Belief> NoAndBeliefs = new HashSet<Belief>();
		for (Belief belief : AndOverOrBeliefs) 
		{
			int size = NoAndBeliefs.size();
			belief.cuptupList(NoAndBeliefs);
			//If size is unchanged
			if (NoAndBeliefs.size() == size)
			{
				NoAndBeliefs.add(belief);

			}
		}


		Log.important("No more And: ");
		for (Belief belief : NoAndBeliefs) {
			Log.log(belief.toString());
		}

		ArrayList<ArrayList<PropositionNode>> listOfElems = new ArrayList<ArrayList<PropositionNode>>();
		ArrayList<LookupNode> lookups = new ArrayList<LookupNode>();
		ArrayList<Belief> list = new ArrayList<Belief>(NoAndBeliefs);
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
		Log.important("Then we know that:");
	}

	public void handleProposition(Belief belief, ArrayList<PropositionNode> generatedList, int i, ArrayList<LookupNode> lookups) {
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