package com.cloudbees.trainapp.service;

import com.cloudbees.trainapp.constants.ExceptionMessages;
import com.cloudbees.trainapp.constants.TrainConstants;
import com.cloudbees.trainapp.exception.BadRequestException;
import com.cloudbees.trainapp.exception.SeatAllocationException;
import com.cloudbees.trainapp.exception.TicketBookingException;
import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.repository.TicketRepository;
import com.cloudbees.trainapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private final TicketRepository ticketRepository;

    @Autowired
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Ticket purchaseTicket(User user, String from, String to, double price) {
        if (!isValidInput(user, from, to)) {
            throw new BadRequestException(ExceptionMessages.INVALID_INPUT);
        }
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = userRepository.save(user);
        }
        String seatSection = allocateSeatSection();
        String email = user.getEmail();
        if (ticketRepository.findByUserAndFromAndTo(user,from,to).isPresent()) {
            throw new TicketBookingException(ExceptionMessages.TICKET_ALREADY_BOOKED + ": " + email);
        }
        Ticket ticket = new Ticket(from, to, user, price, seatSection);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getReceipt(String email) {
        Optional<List<Ticket>> optionalList = ticketRepository.findByUserEmail(email);
        if (optionalList.isPresent() && !optionalList.get().isEmpty()) {
            return optionalList.get();
        } else {
            throw new TicketBookingException(ExceptionMessages.TICKET_NOT_AVAILABLE + ": " + email);
        }
    }

    public String removeUser(Long id, String email) {
        Ticket ticket = ticketRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new BadRequestException(ExceptionMessages.INVALID_INPUT));
        ticketRepository.delete(ticket);
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

    private boolean isValidInput(User user, String from, String to) {
        return user != null && user.getFirstName() != null && !user.getFirstName().isEmpty()
                && user.getLastName() != null && !user.getLastName().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && from != null && !from.isEmpty() && to != null && !to.isEmpty();
    }
}
