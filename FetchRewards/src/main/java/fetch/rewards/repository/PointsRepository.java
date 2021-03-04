package fetch.rewards.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.text.StringContent;

import org.springframework.stereotype.Repository;

import fetch.rewards.model.PointBalance;
import fetch.rewards.model.PointTransaction;

@Repository
public class PointsRepository {
	public Map<String, Integer> balanceMap = new HashMap<>();
	public List<PointTransaction> transactionList = new ArrayList<>();
	public List<PointTransaction> evenedOutList = new ArrayList<>();
	int pointsToSpend = 0;
	
	public boolean saveTransaction(PointTransaction action) {
		String payer = action.getPayer();
		int pointChange = action.getPoints();
		int balance = balanceMap.containsKey(payer) ? balanceMap.get(payer) : 0;
		if (balance + pointChange >= 0) {
			balanceMap.put(payer, balance + pointChange);
			transactionList.add(action);
			return true;
		}
		return false;
	}
	
	private PointBalance spendUntilZero(PointTransaction action) {
		int balance = balanceMap.get(action.getPayer());
		int pointsSpent = Math.min(balance, pointsToSpend);
		balance -= pointsSpent;
		pointsToSpend -= pointsSpent;
		balanceMap.put(action.getPayer(), balance);
		return new PointBalance(action.getPayer(), -pointsSpent);
	}
	
	private List<PointTransaction> getTransactions(String payer) {
		return evenedOutList.stream().filter(action -> 
			Objects.equals(payer, action.getPayer()))
			.collect(Collectors.toList());
	}
	
	
	/* This method takes points from negative transactions and 
	 * deducts them from positive transactions, to make sure the 
	 * oldest points get spent first.
	 */
	private void evenOut() {
		
		// Start by copying the transaction list to a new list
		evenedOutList = new ArrayList<>();
		for (PointTransaction action: transactionList) {
			evenedOutList.add(new PointTransaction(action.getPayer(), 
					action.getPoints(), action.getTimestamp()));
		}
		for (PointTransaction action: evenedOutList) {
			int pointsLeft = action.getPoints();
			if (pointsLeft >= 0) continue;
			List<PointTransaction> partialList = getTransactions(action.getPayer());
			for (PointTransaction matchingAction: partialList) {
				int matchingPoints = matchingAction.getPoints();
				if (matchingPoints > 0) {
					int sum = matchingPoints + pointsLeft;
					
					/* The negative points are fully distributed
					 to the positive points, so this loop ends */
					if (matchingPoints >= -pointsLeft) {
						matchingAction.setPoints(sum);
						action.setPoints(0);
						break;
						
					/* The positive points are not enough to absorb all
					the negative points, so the loop continues. */
					} else {
						matchingAction.setPoints(0);
						action.setPoints(sum);
					}
				}
			}
		}
	}
	
	public List<PointBalance> spendOverall(int points) {
		// Sorts from oldest transaction to newest.
		Collections.sort(transactionList);
		evenOut();
		pointsToSpend = points;
		List<PointBalance> listOfChanges = new ArrayList<>();
		for (PointTransaction action: evenedOutList) {
			if (pointsToSpend <= 0) break;
			if (action.getPoints() > 0) {
				listOfChanges.add(spendUntilZero(action));
			}
		}
		return listOfChanges;
	}
	
	private List<PointBalance> balanceList() {
		return balanceMap.entrySet().parallelStream().map(entry -> 
			new PointBalance(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
	}
	
	/* Format the balance list as a string, like this: 
	 * {
	 * Payer_1: 100,
	 * Payer_2: 200,
	 * Payer_3: 300
	 * }*/
	public String balanceListJson() {
		List<PointBalance> list = balanceList();
		StringBuilder builder = new StringBuilder("{\n");
		for (int index = 0; index < list.size(); index++) {
			PointBalance entry = list.get(index);
			builder.append(String.format("\"%s\": %d", 
					entry.getPayer(), entry.getPoints()));
			if (index < list.size() - 1) builder.append(",");
			builder.append("\n");
		}
		builder.append("}");
		return builder.toString();
	}
}
