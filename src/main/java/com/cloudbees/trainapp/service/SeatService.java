package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.constants.ExceptionMessages;
import com.cloudbees.trainapp.exception.SeatAllocationException;
import com.cloudbees.trainapp.exception.UserNotFoundException;
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
        return ticketRepository.findBySeatSection(section)
                .orElseThrow(() -> new SeatAllocationException(ExceptionMessages.SEATS_NOT_ALLOCATED + ": " + section));
    }

    public Ticket modifySeat(String email, String newSection) {
        Ticket ticket = ticketRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND + ": " + email));
        ticket.setSeatSection(newSection);
        return ticketRepository.save(ticket);
    }
}
