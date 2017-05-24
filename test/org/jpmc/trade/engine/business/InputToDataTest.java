package org.jpmc.trade.engine.business;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jpmc.trade.engine.dto.JPMCTradeInfo;
import org.jpmc.trade.engine.dto.TradeType;
import org.jpmc.trade.engine.exception.DataDiscrepencyException;
import org.junit.Before;
import org.junit.Test;

public class InputToDataTest {
	 InputToData inputToData;
	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy");
	private List<JPMCTradeInfo> listFromExcel;
	 @Before
	    public void init() {
		  inputToData= new InputToData();
	    }


	@Test
	public void testCreateSortedListFromExcelWhenNoData() throws Exception {
	File fileWithNoData =  new File(getClass().getClassLoader().getResource("testData/SampleTradeDataEmpty.xls").getFile());
		listFromExcel = inputToData.createSortedListFromExcel(fileWithNoData.getAbsolutePath());
		assertTrue(listFromExcel.isEmpty());
	}
	
	@Test(expected=DataDiscrepencyException.class)
	public void testCreateSortedListFromExcelWhenWrongDataEntity() throws Exception {
	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeDataWrongEntity.xls").getFile());
		listFromExcel = inputToData.createSortedListFromExcel(file.getAbsolutePath());
	}
	
	@Test(expected=DataDiscrepencyException.class)
	public void testCreateSortedListFromExcelWhenWrongDataTradeType() throws Exception {
	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeDataWrongTradeType.xls").getFile());
		listFromExcel = inputToData.createSortedListFromExcel(file.getAbsolutePath());
	}
	
	
	@Test
	public void testCreateSortedListFromExcelWithSpecialCurrency() throws Exception {
		
		JPMCTradeInfo trade1= new JPMCTradeInfo("bar2",TradeType.S,Double.valueOf(0.32),	"SAR",
				simpleDateFormat.parse("05-Feb-16"),Double.valueOf(0.32*900*15.5), 
				simpleDateFormat.parse("19-May-17"),
				simpleDateFormat.parse("21-May-2017"),900,	Double.valueOf(15.5));
		
		JPMCTradeInfo trade2= new JPMCTradeInfo("bar22",TradeType.B,Double.valueOf(0.78),	"AED",
				simpleDateFormat.parse("05-Mar-16"),Double.valueOf(0.78*300*10), 
				simpleDateFormat.parse("20-May-17"),
				simpleDateFormat.parse("21-May-2017"),300,	Double.valueOf(10));
		
		JPMCTradeInfo trade3= new JPMCTradeInfo("bar222",TradeType.S,Double.valueOf(0.99),	"SAR",
				simpleDateFormat.parse("05-Apr-16"),Double.valueOf(0.99*200*20), 
				simpleDateFormat.parse("18-May-17"),
				simpleDateFormat.parse("18-May-2017"),200,	Double.valueOf(20));



	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeDataSpecialCurrency.xls").getFile());
		listFromExcel = inputToData.createSortedListFromExcel(file.getAbsolutePath());
		assertEquals(3, listFromExcel.size());
		assertThat(listFromExcel.get(0), samePropertyValuesAs(trade3));
		assertThat(listFromExcel.get(1), samePropertyValuesAs(trade1));
		assertThat(listFromExcel.get(2), samePropertyValuesAs(trade2));
	}
	
	@Test
	public void testCreateSortedListFromExcelWithStandardCurrency() throws Exception {
	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeDataRegularCurrency.xls").getFile());
	JPMCTradeInfo trade1= new JPMCTradeInfo("bar2",TradeType.S,Double.valueOf(0.32),	"ABC",
			simpleDateFormat.parse("05-Feb-16"),Double.valueOf(0.32*900*15.5), 
			simpleDateFormat.parse("29-Apr-17"),
			simpleDateFormat.parse("01-May-2017"),900,	Double.valueOf(15.5));
	
	JPMCTradeInfo trade2= new JPMCTradeInfo("bar222",TradeType.B,Double.valueOf(0.98),	"ABC",
			simpleDateFormat.parse("05-Mar-16"),Double.valueOf(0.98*600*12.8), 
			simpleDateFormat.parse("30-Apr-17"),
			simpleDateFormat.parse("01-May-2017"),600,	Double.valueOf(12.8));
	
	JPMCTradeInfo trade3= new JPMCTradeInfo("bar22",TradeType.B,Double.valueOf(0.78),	"ABC",
			simpleDateFormat.parse("05-Apr-16"),Double.valueOf(0.78*300*14.6), 
			simpleDateFormat.parse("28-Apr-17"),
			simpleDateFormat.parse("28-Apr-17"),300,	Double.valueOf(14.6));
		listFromExcel = inputToData.createSortedListFromExcel(file.getAbsolutePath());
		
		assertEquals(3, listFromExcel.size());
		assertThat(listFromExcel.get(0), samePropertyValuesAs(trade3));
		assertThat(listFromExcel.get(1), samePropertyValuesAs(trade1));
		assertThat(listFromExcel.get(2), samePropertyValuesAs(trade2));
	}

	
	@Test
	public void testGetSettledAmountGroupedBySettlementDateForOutgoing() throws Exception {
	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeData.xls").getFile());

		Map<Date, Double> settledAmountGroupedBySettlementDateForOutgoing = inputToData.getSettledAmountGroupedBySettlementDateForTradeType(inputToData.createSortedListFromExcel(file.getAbsolutePath()), TradeType.S);
		assertEquals(6, settledAmountGroupedBySettlementDateForOutgoing.size());
		assertEquals("14899.50",  new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("07-Jan-16"))));
		assertEquals("4464.00",  new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("07-Feb-16"))));
		assertEquals("231.00",  new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("18-Jan-16"))));
		assertEquals("195219.00", new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("19-Jan-16"))));
		assertEquals("462.00",  new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("09-Mar-16"))));
		assertEquals("1760.22",  new DecimalFormat("####0.00").format(settledAmountGroupedBySettlementDateForOutgoing.get(simpleDateFormat.parse("08-Feb-16"))));
		
		
	}
	

	
	@Test
	public void testGetSettledAmountGroupedByEntitySortedReverseForOutgoing() throws Exception {
	File file =  new File(getClass().getClassLoader().getResource("testData/SampleTradeData.xls").getFile());

		Map<String, Double> settledAmountGroupedByEntitySortedReverseForOutgoing = inputToData.getSettledAmountGroupedByEntitySortedReverse(inputToData.createSortedListFromExcel(file.getAbsolutePath()), TradeType.S);
		assertEquals(3, settledAmountGroupedByEntitySortedReverseForOutgoing.size());
		assertEquals("210349.50",  new DecimalFormat("####0.00").format(settledAmountGroupedByEntitySortedReverseForOutgoing.get("bar")));
		assertEquals("4464.00",  new DecimalFormat("####0.00").format(settledAmountGroupedByEntitySortedReverseForOutgoing.get("bar2")));
		assertEquals("2222.22",  new DecimalFormat("####0.00").format(settledAmountGroupedByEntitySortedReverseForOutgoing.get("bar3")));
		
		
	}

}
