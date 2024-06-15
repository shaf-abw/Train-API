package com.cloudbees.trainapp.controller;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.model.User;
import com.cloudbees.trainapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public Ticket purchaseTicket(@RequestBody User user) {
        return ticketService.purchaseTicket(user);
    }

    @GetMapping("/receipt/{email}")
    public Ticket getReceipt(@PathVariable String email) {
        return ticketService.getReceipt(email);
    }

    @DeleteMapping("/remove/{email}")
    public void removeUser(@PathVariable String email) {
        ticketService.removeUser(email);
    }
}
