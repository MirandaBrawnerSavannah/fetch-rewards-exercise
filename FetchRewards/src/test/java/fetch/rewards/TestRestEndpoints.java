package fetch.rewards;


import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;

import fetch.rewards.controller.PointsController;
import fetch.rewards.model.PointBalance;
import fetch.rewards.request.AddTransactionRequest;
import fetch.rewards.request.SpendRequest;

class TestRestEndpoints {
	
	static ApplicationContext context;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		context = SpringApplication.run(FetchRewardsApplication.class, new String[]{});
	}

	// Transactions to add for test
	/* {"payer": "DANNON", "points": 1000, "timestamp": "2020-11-02T14:00:00Z"}
{"payer": "UNILEVER", "points": 200, "timestamp": "2020-10-31T11:00:00Z"}
{"payer": "DANNON", "points": -200, "timestamp": "2020-10-31T15:00:00Z"}
{"payer": "MILLER COORS", "points": 10000, "timestamp": "2020-11-01T14:00:00Z"}
{"payer": "DANNON", "points": 300, "timestamp": "2020-10-31T10:00:00Z"} */
	
	@Test
	void test() {
		String[] payerArray = {"DANNON", "UNILEVER", "DANNON", "MILLER COORS", "DANNON"};
		int[] pointsArray = {1000, 200, -200, 10000, 300};
		Date earliestDate = new Date(1);
		Date earlyDate = new Date(2);
		Date middleDate = new Date(3);
		Date lateDate = new Date(4);
		Date latestDate = new Date(5);
		Date[] dateArray = {latestDate, earlyDate, middleDate, lateDate, earliestDate};
		PointsController controller = context.getBean(PointsController.class);
		for (int index = 0; index < payerArray.length; index++) {
			AddTransactionRequest request = new AddTransactionRequest(
					payerArray[index], pointsArray[index], dateArray[index]);
			ResponseEntity<?> response = controller.addTransaction(request);
			Assertions.assertEquals(200, response.getStatusCodeValue(), 
					"Each transaction should return a status code of 200.");
		}
		// Send a request to spend 5000 points
		SpendRequest spendRequest = new SpendRequest(5000);
		ResponseEntity<?> response = controller.spend(spendRequest);
		Assertions.assertEquals(200, response.getStatusCodeValue(), 
				"The spend method should return a status code of 200.");
		Object responseBody = response.getBody();
		Assertions.assertTrue(responseBody instanceof List<?>, "The spend method should "
				+ "return a response that contains a list.");
		List<PointBalance> spendList = (List<PointBalance>) response.getBody();
		Assertions.assertEquals(3, spendList.size(), "The list returned by the spend method "
				+ "should have three items.");
		
		// Expected response from spend method
		payerArray = new String[] {"DANNON", "UNILEVER", "MILLER COORS"};
		pointsArray = new int[] {-100, -200, -4700};
		for (int index = 0; index < 3; index++) {
			PointBalance balance = spendList.get(index);
			Assertions.assertEquals(payerArray[index], balance.getPayer(), 
					"One of the payers in the 'spend' results was incorrect.");
			Assertions.assertEquals(pointsArray[index], balance.getPoints(),
					"One of the point values in the 'spend' results was incorrect.");
		}
		
		// Test the display balance method
		response = controller.displayBalance();
		Assertions.assertTrue(response.getBody() instanceof String, "The response "
				+ "to the displayBalance method should contain a string.");
		String balanceString = (String) response.getBody();
		Assertions.assertEquals("{\n"
				+ "\"DANNON\": 1000,\n"
				+ "\"UNILEVER\": 0,\n"
				+ "\"MILLER COORS\": 5300\n"
				+ "}", balanceString, "The string returned by the "
						+ "displayBalance method is incorrect.");
	}

}
