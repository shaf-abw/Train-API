package com.cloudbees.trainapp;

import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TrainTicketApiApplicationTests {

	@Autowired
	private TicketService ticketService;

	@Test
	public void testPurchaseTicket() {
		User user = new User("John", "Doe", "john.doe@example.com");
		assertNotNull(ticketService.purchaseTicket(user));
	}

	@Test
	public void testGetReceipt() {
		User user = new User("Jane", "Doe", "jane.doe@example.com");
		ticketService.purchaseTicket(user);
		assertNotNull(ticketService.getReceipt("jane.doe@example.com"));
	}

}
