package com.cloudbees.trainapp;

import com.cloudbees.trainapp.model.*;
import com.cloudbees.trainapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TrainTicketApiApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private SeatService seatService;

	private User testUser;

	@BeforeEach
	public void setup() {
		// Initialize a test user
		testUser = new User("John", "Doe", "john.doe@example.com");
	}

	@Test
	public void testBookTicket() {
		// Perform ticket booking
		ResponseEntity<Ticket> response = restTemplate.postForEntity("/api/tickets/purchase", testUser, Ticket.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Ticket bookedTicket = response.getBody();
		assertNotNull(bookedTicket);
		assertEquals(testUser.getId(), bookedTicket.getUser().getId());
		// Add additional assertions as per your application logic
	}

	@Test
	public void testBookDuplicateTicket() {
		// Book a ticket first
		Ticket bookedTicket = ticketService.purchaseTicket(testUser);

		// Try to book the same ticket again
		ResponseEntity<Ticket> response = restTemplate.postForEntity("/api/tickets/purchase", testUser, Ticket.class);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

		// Verify error message or additional assertions as per your application logic
	}

	@Test
	public void testInvalidTicketBooking() {
		// Try to book a ticket with invalid data
		ResponseEntity<Ticket> response = restTemplate.postForEntity("/api/tickets/purchase", new User(), Ticket.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		// Verify error message or additional assertions as per your application logic
	}

	@Test
	public void testSeatAllocationWhenFull() {
		// Allocate all seats in section A
		ResponseEntity<Ticket> response = null;
		for (int i = 0; i < 50; i++) { // Assuming 50 seats in section A
			response = restTemplate.postForEntity("/api/tickets/purchase", testUser, Ticket.class);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		// Verify error message or additional assertions as per your application logic
	}

}
