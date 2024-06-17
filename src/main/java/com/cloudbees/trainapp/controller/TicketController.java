package com.cloudbees.trainapp.controller;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody User user) throws Exception {
        try {
            return ResponseEntity.ok(ticketService.purchaseTicket(user));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/receipt/{email}")
    public ResponseEntity<Ticket> getReceipt(@PathVariable String email) throws Exception {
        try {
            return ResponseEntity.ok(ticketService.getReceipt(email));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{email}")
    public ResponseEntity<String> removeUser(@PathVariable String email) throws Exception {
        try {
            return ResponseEntity.ok(ticketService.removeUser(email));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
