package org.jpmc.trade.engine.business;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.jpmc.trade.engine.dto.JPMCTradeInfo;
import org.jpmc.trade.engine.dto.TradeType;
import org.jpmc.trade.engine.exception.DataDiscrepencyException;


public class InputToData {
	static final List<String> currenciesWithCustomTradeWeek = Arrays.asList(
			"AED", "SAR");
	static final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E");
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");

	
	public List<JPMCTradeInfo> createSortedListFromExcel(String fileLocation) throws BiffException, IOException, ParseException, DataDiscrepencyException{
		ArrayList<JPMCTradeInfo> lstTradeInfo = new ArrayList<JPMCTradeInfo>();
		JPMCTradeInfo tradeInfoRow = null;
		Workbook workbook = null;
		workbook = Workbook.getWorkbook(new File(fileLocation));
		Calendar calendar = Calendar.getInstance();

		for (int i = 1; i < workbook.getSheet(0).getRows(); i++) {
			tradeInfoRow = new JPMCTradeInfo();
			String entity = workbook.getSheet(0).getCell(0, i)
					.getContents();
			if (entity != null && entity.length() > 0)
				tradeInfoRow.setEntity(entity);
			else throw new DataDiscrepencyException("wrong entity data in row "+i);
			String tradeType = workbook.getSheet(0).getCell(1, i)
					.getContents();
			if (tradeType != null && tradeType.length() > 0 && isInEnum(tradeType, TradeType.class))
				tradeInfoRow.setTradeType(TradeType.valueOf(tradeType
						.trim()));
			else throw new DataDiscrepencyException("wrong trade type data in row "+i);
			String agreedFx = workbook.getSheet(0).getCell(2, i)
					.getContents();
			if (agreedFx != null && agreedFx.length() > 0)
				tradeInfoRow.setAgreedFx(Double.valueOf(agreedFx));
			else throw new DataDiscrepencyException("wrong agreed fx data in row "+i);
			String currency = workbook.getSheet(0).getCell(3, i)
					.getContents();
			if (currency != null && currency.length() > 0)
				tradeInfoRow.setCurrency(currency);
			else throw new DataDiscrepencyException("wrong currency data in row "+i);
			String instructionDate = workbook.getSheet(0).getCell(4, i)
					.getContents();
			if (instructionDate != null && instructionDate.length() > 0) {
				tradeInfoRow.setInstructionDate(dateFormat
						.parse(instructionDate));
			}
			else throw new DataDiscrepencyException("wrong instruction date data in row "+i);
			String settlementDate = workbook.getSheet(0).getCell(5, i)
					.getContents();
			if (settlementDate != null && settlementDate.length() > 0)
				tradeInfoRow.setSettlementDate(dateFormat
						.parse(settlementDate));
			else throw new DataDiscrepencyException("wrong settlement date data in row "+i);
			String units = workbook.getSheet(0).getCell(6, i).getContents();
			if (units != null && units.length() > 0)
				tradeInfoRow.setUnits(Integer.parseInt(units));
			else throw new DataDiscrepencyException("wrong units data in row "+i);
			String pricePerUnit = workbook.getSheet(0).getCell(7, i)
					.getContents();
			if (pricePerUnit != null && pricePerUnit.length() > 0)
				tradeInfoRow.setPricePerUnit(Double.valueOf(pricePerUnit));
			else throw new DataDiscrepencyException("wrong price per unit data in row "+i);
			tradeInfoRow.setTradeAmount(tradeInfoRow.getAgreedFx()
					* tradeInfoRow.getPricePerUnit()
					* tradeInfoRow.getUnits());

			calendar.setTime(tradeInfoRow.getSettlementDate());
			int settlementDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			switch (settlementDayOfWeek) {
			case 1:
				if (!currenciesWithCustomTradeWeek.contains(tradeInfoRow
						.getCurrency())) {
					calendar.setTime(tradeInfoRow.getSettlementDate());
					calendar.add(Calendar.DATE, 1);
					tradeInfoRow
							.setActualSettlementDate(calendar.getTime());
				} else {
					tradeInfoRow.setActualSettlementDate(tradeInfoRow
							.getSettlementDate());
				}
				break;
			case 6:
				if (!currenciesWithCustomTradeWeek.contains(tradeInfoRow
						.getCurrency())) {
					tradeInfoRow.setActualSettlementDate(tradeInfoRow
							.getSettlementDate());
				} else {
					calendar.setTime(tradeInfoRow.getSettlementDate());
					calendar.add(Calendar.DATE, 2);
					tradeInfoRow
							.setActualSettlementDate(calendar.getTime());
				}
				break;
			case 7:
				if (!currenciesWithCustomTradeWeek.contains(tradeInfoRow
						.getCurrency())) {

					calendar.setTime(tradeInfoRow.getSettlementDate());
					calendar.add(Calendar.DATE, 2);

					tradeInfoRow
							.setActualSettlementDate(calendar.getTime());

				} else {
					Calendar calSettlementDate = Calendar.getInstance();
					calSettlementDate.setTime(tradeInfoRow
							.getSettlementDate());
					calSettlementDate.add(Calendar.DATE, 1);

					tradeInfoRow.setActualSettlementDate(calSettlementDate
							.getTime());
				}
				break;
			default:
				tradeInfoRow.setActualSettlementDate(tradeInfoRow
						.getSettlementDate());
			}

			lstTradeInfo.add(tradeInfoRow);
		}
		Collections.sort(lstTradeInfo,
				(trade1, trade2) -> trade1.getActualSettlementDate()
						.compareTo(trade2.getActualSettlementDate()));
		return lstTradeInfo;


	}
	public Map<Date, Double> getSettledAmountGroupedBySettlementDateForTradeType(List<JPMCTradeInfo> allTrades, TradeType tradeType){
		List<JPMCTradeInfo> filteredTrades = allTrades.stream()
				.filter(trade -> (trade.getTradeType() == tradeType))
				.collect(Collectors.toList());
		
		Map<Date, Double> settledAmountByDate = filteredTrades
				.stream()
				.collect(
						Collectors
								.groupingBy(JPMCTradeInfo::getActualSettlementDate
										,
										Collectors
												.summingDouble(JPMCTradeInfo::getTradeAmount)));
										         
		return settledAmountByDate;
		
	}
	
	public Map<String, Double> getSettledAmountGroupedByEntitySortedReverse(List<JPMCTradeInfo> allTrades, TradeType tradeType){
		List<JPMCTradeInfo> filteredTrades = allTrades.stream()
				.filter(trade -> (trade.getTradeType() == tradeType))
				.collect(Collectors.toList());
		Map<String, Double> settledAmountByEntity = filteredTrades
				.stream()
				.collect(
						Collectors
								.groupingBy(
										JPMCTradeInfo::getEntity,
										Collectors
												.summingDouble(JPMCTradeInfo::getTradeAmount)));
		Map<String, Double> tradeEntityRankings = new LinkedHashMap<>();

		settledAmountByEntity
				.entrySet()
				.stream()
				.sorted(Map.Entry.<String, Double> comparingByValue()
						.reversed())
				.forEachOrdered(
						x -> tradeEntityRankings.put(x.getKey(),
								x.getValue()));// order as per ranking
		return tradeEntityRankings;

	}
	
	private <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
		  for (E e : enumClass.getEnumConstants()) {
		    if(e.name().equals(value)) { return true; }
		  }
		  return false;
		}

}
