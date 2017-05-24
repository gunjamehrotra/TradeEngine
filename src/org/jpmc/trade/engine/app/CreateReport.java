package org.jpmc.trade.engine.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jpmc.trade.engine.business.InputToData;
import org.jpmc.trade.engine.dto.JPMCTradeInfo;
import org.jpmc.trade.engine.dto.TradeType;

public class CreateReport {

	public static void main(String[] args) {

		try {
			InputToData inputToData = new InputToData();
			List<JPMCTradeInfo> lstTradeInfo = inputToData
					.createSortedListFromExcel(args[0]);
			
			Map<Date, Double> incomingSettledAmountByDate=inputToData.getSettledAmountGroupedBySettlementDateForTradeType(lstTradeInfo, TradeType.B);
			
		
			System.out.println("Amount in USD settled incoming everyday:");
			System.out.println("Date\t\t\t\tAmount(USD)");
			incomingSettledAmountByDate.forEach((k, v) -> System.out
					.println(new SimpleDateFormat("dd-MMM-yy").format(k)
							+ "\t\t\t" + String.format("%.2f", v)));
			System.out.println("\nAmount in USD settled outgoing everyday:");
			System.out.println("Date\t\t\t\tAmount(USD)");
			Map<Date, Double> outgoingSettledAmountByDate = inputToData.getSettledAmountGroupedBySettlementDateForTradeType(lstTradeInfo, TradeType.S);
			
			outgoingSettledAmountByDate.forEach((k, v) -> System.out
					.println(new SimpleDateFormat("dd-MMM-yy").format(k)
							+ "\t\t\t" + String.format("%.2f", v)));

			Map<String, Double> outgoingEntityRankings = inputToData.getSettledAmountGroupedByEntitySortedReverse(lstTradeInfo, TradeType.S);
			Map<String, Double> incomingEntityRankings = inputToData.getSettledAmountGroupedByEntitySortedReverse(lstTradeInfo, TradeType.B);
			

			System.out
					.println("\nRanking of entities based on incoming amount: ");
			System.out.println("Rank\t\t\t\tEntity\t\t\t\tAmount(USD)");
			int rank = 1;
			for (Entry<String, Double> entry : incomingEntityRankings
					.entrySet()) {
				System.out
						.println(rank + " \t\t\t\t" + entry.getKey()
								+ " \t\t\t\t"
								+ String.format("%.2f", entry.getValue()));
				rank++;
			}
			rank = 1;

			System.out
					.println("\nRanking of entities based on outgoing amount: ");
			System.out.println("Rank\t\t\t\tEntity\t\t\t\tAmount(USD)");

			for (Entry<String, Double> entry : outgoingEntityRankings
					.entrySet()) {
				System.out
						.println(rank + " \t\t\t\t" + entry.getKey()
								+ " \t\t\t\t"
								+ String.format("%.2f", entry.getValue()));
				rank++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
