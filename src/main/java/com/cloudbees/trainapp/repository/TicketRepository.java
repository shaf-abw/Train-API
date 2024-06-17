package com.cloudbees.trainapp.repository;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<List<Ticket>> findByUserEmail(String email);
    Optional<Ticket> findByUserAndFromAndTo(User user, String from, String to);
    Optional<Ticket> findByIdAndUserEmail(Long id, String email);
    Optional<List<Ticket>> findBySeatSection(String seatSection);
}
