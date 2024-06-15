package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> getUsersBySection(String section) {
        return ticketRepository.findBySeatSection(section);
    }

    public Ticket modifySeat(String email, String newSection) {
        Ticket ticket = ticketRepository.findByUserEmail(email);
        if (ticket != null) {
            ticket.setSeatSection(newSection);
            return ticketRepository.save(ticket);
        }
        return null;
    }
}
