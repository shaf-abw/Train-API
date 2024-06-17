package com.cloudbees.trainapp.controller;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody Ticket requestTicket) {

        return ResponseEntity.ok(ticketService.purchaseTicket(
                requestTicket.getUser(),
                requestTicket.getFrom(),
                requestTicket.getTo(),
                requestTicket.getPrice()));
    }

    @GetMapping("/receipt/{email}")
    public ResponseEntity<List<Ticket>> getReceipt(@PathVariable String email) {
        return ResponseEntity.ok(ticketService.getReceipt(email));
    }

    @DeleteMapping("/remove/{id}/{email}")
    public ResponseEntity<String> removeUser(@PathVariable Long id, @PathVariable String email) {
        return ResponseEntity.ok(ticketService.removeUser(id, email));
    }
}
