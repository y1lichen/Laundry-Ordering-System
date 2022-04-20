package com.jefferson.laundryorderingsystem.entities.reservation;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reservations")
public class ReservationController {

    private static String[] possibleTime = { "16:00", "16:40", "17:20", "18:00", "18:40",
            "19:20", "20:00", "20:40", "21:20", "22:00", "22:40", "23:20" };

    private static class GetAvaliableReservationsResponse {
        private int replyCode;
    }

    @Autowired
    ReservationRepo repo;

    @GetMapping(value = "/get-avaliable-reservations", produces = "application/json")
    public ResponseEntity<?> getAvaliableReservation(@RequestParam String time) {
        GetAvaliableReservationsResponse responseBody = new GetAvaliableReservationsResponse();
        List<Reservation> unavaliableReservations = repo.findAllByTimeAfter(time);
        System.out.println(unavaliableReservations.toString());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}