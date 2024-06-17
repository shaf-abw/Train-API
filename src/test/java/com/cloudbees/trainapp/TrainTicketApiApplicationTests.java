package com.cloudbees.trainapp;

import com.cloudbees.trainapp.constants.ExceptionMessages;
import com.cloudbees.trainapp.constants.TrainConstants;
import com.cloudbees.trainapp.model.*;
import com.cloudbees.trainapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TrainTicketApiApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private TestRestTemplate restTemplate;

	private String baseUrl;
	private User testUser;

	@BeforeEach
	public void setUp() {
		this.baseUrl = "http://localhost:" + port + "/v1/api";
		testUser = new User("John", "Doe", "john.doe@example.com");
	}

	@Test
	public void testPurchaseTicket() {
		Ticket ticketRequest = new Ticket("London", "France", testUser, 5.0, "A");

		ResponseEntity<Ticket> response = restTemplate.postForEntity(baseUrl + "/tickets/purchase", ticketRequest, Ticket.class);

		assertEquals(200, response.getStatusCodeValue());
		assertNotNull(response.getBody());
		assertEquals("London", response.getBody().getFrom());
		assertEquals("France", response.getBody().getTo());
		assertEquals("john.doe@example.com", response.getBody().getUser().getEmail());
		assertEquals(5.0, response.getBody().getPrice());
	}

	@Test
	public void testBookDuplicateTicket() {
		// Book a ticket first
		Ticket bookedTicket = ticketService.purchaseTicket(testUser, "London", "France", 5.0);

		// Try to book the same ticket again
		ResponseEntity<Ticket> response = restTemplate.postForEntity(baseUrl + "/tickets/purchase", bookedTicket, Ticket.class);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	public void testGetReceipt() {
		String email = "john.doe@example.com";

		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/tickets/receipt/" + email, String.class);

		assertEquals(409, response.getStatusCodeValue());
		assertNotNull(response.getBody());
	}

	@Test
	public void testRemoveUser() {

		// Book a ticket first
		Ticket bookedTicket = ticketService.purchaseTicket(testUser, "London", "France", 5.0);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/tickets/remove/" + bookedTicket.getId() + "/" + bookedTicket.getUser().getEmail(), HttpMethod.DELETE, entity, String.class);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("User removed successfully", response.getBody());
	}

	@Test
	public void testInvalidTicketBooking() {
		// Try to book a ticket with invalid data
		ResponseEntity<Ticket> response = restTemplate.postForEntity(baseUrl + "/tickets/purchase", new Ticket(), Ticket.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testPurchaseTicketWithInvalidInput() {
		User user = new User("", "Doe", "john.doe@example.com"); // Invalid first name
		Ticket ticketRequest = new Ticket("London", "France", user, 5.0, "A");

		ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/tickets/purchase", ticketRequest, String.class);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.INVALID_INPUT));
	}

	@Test
	public void testSeatAllocationOverflow() {
		// Simulate filling all seats
		for (int i = 0; i < TrainConstants.MAX_SEATS_SECTION_A + TrainConstants.MAX_SEATS_SECTION_B; i++) {
			User user = new User("User" + i, "LastName" + i, "user" + i + "@example.com");
			Ticket ticketRequest = new Ticket("London", "France", user, 5.0, "A");
			restTemplate.postForEntity(baseUrl + "/tickets/purchase", ticketRequest, Ticket.class);
		}

		// Attempt to book another ticket
		User user = new User("John", "Doe", "john.doe@example.com");
		Ticket ticketRequest = new Ticket("London", "France", user, 5.0, "A");

		ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/tickets/purchase", ticketRequest, String.class);

		assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.SEATS_NOT_AVAILABLE));
	}

	@Test
	public void testGetReceiptForNonExistentUser() {
		String email = "non.existent@example.com";

		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/tickets/receipt/" + email, String.class);

		assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.TICKET_NOT_AVAILABLE));
	}

	@Test
	public void testRemoveNonExistentTicket() {
		String email = "john.doe@example.com";
		Long ticketId = 999L; // Non-existent ticket ID

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/tickets/remove/" + ticketId + "/" + email, HttpMethod.DELETE, entity, String.class);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.INVALID_INPUT));
	}

	@Test
	public void testGetUsersBySection() {
		String section = "A";

		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/seats/section/" + section, String.class);

		assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
		assertNotNull(response.getBody());
	}

	@Test
	public void testModifySeat() {
		// First, create a ticket
		User user = new User("John", "Doe", "john.doe@example.com");
		Ticket ticketRequest = new Ticket("London", "France", user, 5.0, "A");
		ResponseEntity<Ticket> createResponse = restTemplate.postForEntity(baseUrl + "/tickets/purchase", ticketRequest, Ticket.class);
		Ticket createdTicket = createResponse.getBody();

		// Modify the seat section
		String newSection = "B";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Ticket> response = restTemplate.exchange(
				baseUrl + "/seats/modify/" + createdTicket.getId() + "/" + user.getEmail() + "?newSection=" + newSection,
				HttpMethod.PUT,
				entity,
				Ticket.class
		);

		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertNotNull(response.getBody());
		assertEquals(newSection, response.getBody().getSeatSection());
	}

	@Test
	public void testGetUsersBySectionSeatAllocationException() {
		String section = "C"; // Assuming section "C" does not exist

		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/seats/section/" + section, String.class);

		assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.SEATS_NOT_ALLOCATED));
	}

	@Test
	public void testModifySeatTicketNotFoundException() {
		Long ticketId = 999L; // Non-existent ticket ID
		String email = "non.existent@example.com";
		String newSection = "B";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				baseUrl + "/seats/modify/" + ticketId + "/" + email + "?newSection=" + newSection,
				HttpMethod.PUT,
				entity,
				String.class
		);

		assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
		assertTrue(response.getBody().contains(ExceptionMessages.TICKET_NOT_AVAILABLE));
	}

}
