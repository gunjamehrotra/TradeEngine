Objective of application: It takes as input an excel representing the instructions sent by various clients to JP Morgan to execute in the international market.


Required Environment/Tools Java 1.8 maven 3
The class org.jpmc.trade.engine.app.CreateReport contains main methodand takes as argument the path of excel file with sample data. Sample excels are provided in testData folder inside test.
The program outputs following in the report
Amount in USD settled incoming everyday
Amount in USD settled outgoing everyday
Ranking of entities based on incoming and outgoing amount. 