package fetch.rewards.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fetch.rewards.model.PointBalance;
import fetch.rewards.model.PointTransaction;
import fetch.rewards.repository.PointsRepository;
import fetch.rewards.request.AddTransactionRequest;
import fetch.rewards.request.SpendRequest;

@RestController
@CrossOrigin
public class PointsController {
	
	@Autowired
	PointsRepository repository;

	@PostMapping(path = "/add")
	public ResponseEntity<?> addTransaction(@RequestBody AddTransactionRequest request) {
		if (request == null) return ResponseEntity.badRequest().body("The request is empty.");
		if (request.getPayer() == null) return ResponseEntity.badRequest().body(
				"The request must specify a number of a payer, in the parameter 'payer'.");
		if (request.getPoints() == null) return ResponseEntity.badRequest().body(
				"The request must specify a number of points to spend, in the parameter 'points'.");
		Date timestamp = request.getTimestamp() == null ? new Date() : request.getTimestamp();
		PointTransaction action = new PointTransaction(request.getPayer(), request.getPoints(), timestamp);
		if (repository.saveTransaction(action)) {
			return ResponseEntity.ok("Transaction added.");
		} else {
			return ResponseEntity.badRequest().body("Not enough points to complete the transaction.");
		}
	}
	
	@PostMapping(path = "/spend")
	public ResponseEntity<?> spend(@RequestBody SpendRequest request) {
		if (request == null) return ResponseEntity.badRequest().body("The request is empty.");
		if (request.getPoints() == null) return ResponseEntity.badRequest().body(
				"The request must specify a number of points to spend, in the parameter 'points'.");
		List<PointBalance> listOfChanges = repository.spendOverall(request.getPoints());
		for (PointBalance pointChange: listOfChanges) {
			PointTransaction action = new PointTransaction(pointChange.getPayer(), 
					pointChange.getPoints(), new Date());
			repository.transactionList.add(action);
		}
		return ResponseEntity.ok(listOfChanges);
	}
	
	@GetMapping(path = "/balance")
	public ResponseEntity<?> displayBalance() {
		return ResponseEntity.ok(repository.balanceListJson());
	}
}
