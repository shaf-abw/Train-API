package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    @Autowired
    private TicketService ticketService;

    public List<Ticket> getUsersBySection(String section) {
        return ticketService.getTickets().stream()
                .filter(ticket -> ticket.getSeatSection().equalsIgnoreCase(section))
                .collect(Collectors.toList());
    }

    public Ticket modifySeat(String email, String newSection) {
        Ticket ticket = ticketService.getReceipt(email);
        if (ticket != null) {
            ticket.setSeatSection(newSection);
        }
        return ticket;
    }
}
