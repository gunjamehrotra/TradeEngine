package org.jpmc.trade.engine.dto;
import java.util.Date;


public class JPMCTradeInfo {
	
	public JPMCTradeInfo(){}
	
	public JPMCTradeInfo(String entity, TradeType tradeType, Double agreedFx,
			String currency, Date instructionDate, Double tradeAmount,
			Date settlementDate, Date actualSettlementDate, Integer units,
			Double pricePerUnit) {
		super();
		this.entity = entity;
		this.tradeType = tradeType;
		this.agreedFx = agreedFx;
		this.currency = currency;
		this.instructionDate = instructionDate;
		this.tradeAmount = tradeAmount;
		this.settlementDate = settlementDate;
		this.actualSettlementDate = actualSettlementDate;
		this.units = units;
		this.pricePerUnit = pricePerUnit;
	}
	private String entity;
	private TradeType tradeType;
	private Double agreedFx;
	private String currency;
	private Date instructionDate;
	private Double tradeAmount;
	public Double getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(Double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public Double getAgreedFx() {
		return agreedFx;
	}
	public void setAgreedFx(Double agreedFx) {
		this.agreedFx = agreedFx;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getInstructionDate() {
		return instructionDate;
	}
	public void setInstructionDate(Date instructionDate) {
		this.instructionDate = instructionDate;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	public Integer getUnits() {
		return units;
	}
	public void setUnits(Integer units) {
		this.units = units;
	}
	public double getPricePerUnit() {
		return pricePerUnit;
	}
	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
	private Date settlementDate;
	private Date actualSettlementDate;
	
	public Date getActualSettlementDate() {
		return actualSettlementDate;
	}
	public void setActualSettlementDate(Date actualSettlementDate) {
		this.actualSettlementDate = actualSettlementDate;
	}
	private Integer units;
	private Double pricePerUnit;
	

}
