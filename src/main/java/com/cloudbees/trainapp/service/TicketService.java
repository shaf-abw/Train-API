package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TicketService {

    private List<Ticket> tickets = new ArrayList<>();

    public Ticket purchaseTicket(User user) {
        Ticket ticket = new Ticket("London", "France", user, 5.0, allocateSeat());
        tickets.add(ticket);
        return ticket;
    }

    public Ticket getReceipt(String email) {
        return tickets.stream()
                .filter(ticket -> ticket.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public void removeUser(String email) {
        tickets.removeIf(ticket -> ticket.getUser().getEmail().equals(email));
    }

    private String allocateSeat() {
        // Simple allocation logic alternating between sections
        return (tickets.size() % 2 == 0) ? "A" : "B";
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
