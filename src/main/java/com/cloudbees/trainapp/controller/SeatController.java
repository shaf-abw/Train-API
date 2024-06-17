package com.cloudbees.trainapp.controller;

import com.cloudbees.trainapp.model.Ticket;
import com.cloudbees.trainapp.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/section/{section}")
    public ResponseEntity<List<Ticket>> getUsersBySection(@PathVariable String section) throws Exception {
        try {
            return ResponseEntity.ok(seatService.getUsersBySection(section));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @PutMapping("/modify/{email}")
    public ResponseEntity<Ticket> modifySeat(@PathVariable String email, @RequestParam String newSection) throws Exception {
        try {
            return ResponseEntity.ok(seatService.modifySeat(email, newSection));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
