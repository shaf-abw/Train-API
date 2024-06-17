package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.constants.ExceptionMessages;
import com.cloudbees.trainapp.constants.TrainConstants;
import com.cloudbees.trainapp.exception.SeatAllocationException;
import com.cloudbees.trainapp.exception.TicketBookingException;
import com.cloudbees.trainapp.exception.UserNotFoundException;
import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.repository.TicketRepository;
import com.cloudbees.trainapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public Ticket purchaseTicket(User user) {
        if (!isValidUser(user)) {
            throw new TicketBookingException(ExceptionMessages.INVALID_INPUT);
        }
        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new TicketBookingException(ExceptionMessages.TICKET_ALREADY_BOOKED +": "+ user.getEmail()));
        savedUser = userRepository.save(savedUser);
        String seatSection = allocateSeatSection();
        Ticket ticket = new Ticket("London", "France", user, 5.0, seatSection);
        return ticketRepository.save(ticket);
    }

    public Ticket getReceipt(String email) {
        return ticketRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND + ": " + email));
    }

    public String removeUser(String email) {
        Ticket ticket = ticketRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND + ": " + email));
        ticketRepository.delete(ticket);
        userRepository.delete(ticket.getUser());
        return "User removed successfully";
    }

    private String allocateSeatSection() {
        int seatsInSectionA = ticketRepository.findBySeatSection("A").get().size();
        int seatsInSectionB = ticketRepository.findBySeatSection("B").get().size();

        if (seatsInSectionA < TrainConstants.MAX_SEATS_SECTION_A) {
            return "A";
        } else if (seatsInSectionB < TrainConstants.MAX_SEATS_SECTION_B) {
            return "B";
        } else {
            throw new SeatAllocationException(ExceptionMessages.SEATS_NOT_AVAILABLE);
        }
    }

    private boolean isValidUser(User user) {
        return user != null && user.getFirstName() != null && !user.getFirstName().isEmpty()
                && user.getLastName() != null && !user.getLastName().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty();
    }
}
