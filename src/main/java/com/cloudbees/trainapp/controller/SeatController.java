package com.cloudbees.trainapp.controller;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/section/{section}")
    public List<Ticket> getUsersBySection(@PathVariable String section) {
        return seatService.getUsersBySection(section);
    }

    @PutMapping("/modify/{email}")
    public Ticket modifySeat(@PathVariable String email, @RequestParam String newSection) {
        return seatService.modifySeat(email, newSection);
    }
}
