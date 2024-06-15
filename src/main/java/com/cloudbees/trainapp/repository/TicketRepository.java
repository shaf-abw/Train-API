package com.cloudbees.trainapp.repository;

import com.cloudbees.trainapp.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByUserEmail(String email);
    List<Ticket> findBySeatSection(String seatSection);
}
