package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.repository.TicketRepository;
import com.cloudbees.trainapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public Ticket purchaseTicket(User user) {
        User savedUser = userRepository.findByEmail(user.getEmail());
        if (savedUser == null) {
            savedUser = userRepository.save(user);
        }
        String seatSection = allocateSeat();
        Ticket ticket = new Ticket("London", "France", savedUser, 5.0, seatSection);
        return ticketRepository.save(ticket);
    }

    public Ticket getReceipt(String email) {
        return ticketRepository.findByUserEmail(email);
    }

    public void removeUser(String email) {
        Ticket ticket = ticketRepository.findByUserEmail(email);
        if (ticket != null) {
            ticketRepository.delete(ticket);
            userRepository.delete(ticket.getUser());
        }
    }

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    private String allocateSeat() {
        long count = ticketRepository.count();
        return (count % 2 == 0) ? "A" : "B";
    }
}
