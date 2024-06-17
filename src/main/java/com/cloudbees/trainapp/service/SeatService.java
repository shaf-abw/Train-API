package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.constants.ExceptionMessages;
import com.cloudbees.trainapp.exception.SeatAllocationException;
import com.cloudbees.trainapp.exception.TicketBookingException;
import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private final TicketRepository ticketRepository;

    public SeatService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getUsersBySection(String section) {
        Optional<List<Ticket>> optionalList = ticketRepository.findBySeatSection(section);
        if (optionalList.isPresent() && !optionalList.get().isEmpty()) {
            return optionalList.get();
        } else {
            throw new SeatAllocationException(ExceptionMessages.SEATS_NOT_ALLOCATED + ": " + section);
        }
    }

    public Ticket modifySeat(Long id, String email, String newSection) {
        Ticket ticket = ticketRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new TicketBookingException(ExceptionMessages.TICKET_NOT_AVAILABLE + ": " + email));
        ticket.setSeatSection(newSection);
        return ticketRepository.save(ticket);
    }
}
