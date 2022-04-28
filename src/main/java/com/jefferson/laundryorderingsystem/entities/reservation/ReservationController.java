package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;

    private static String[] possibleTime = { "16:00:00", "16:40:00", "17:20:00", "18:00:00", "18:40:00",
            "19:20:00", "20:00:00", "20:40:00", "21:20:00", "22:00:00", "22:40:00", "23:20:00" };

    private static class GetAvailableReservationsResponse {
        private int replyCode = -1;
        private ArrayList<String> availableTimeList = new ArrayList<>();

        public void setReplyCode(int replyCode) {
            this.replyCode = replyCode;
        }

        public int getReplyCode() {
            return replyCode;
        }

        public void setAvailableTimeList(ArrayList<String> availableTimeList) {
            this.availableTimeList = availableTimeList;
        }

        public ArrayList<String> getAvailableTimeList() {
            return availableTimeList;
        }
    }


    @GetMapping(value = "/get-available-reservations", produces = "application/json")
    public ResponseEntity<?> getAvaliableReservation(@RequestParam String date) {
        GetAvailableReservationsResponse responseBody = new GetAvailableReservationsResponse();
        ArrayList<String> availableTimeList = new ArrayList<>();
        for (String element : possibleTime) {
            LocalDateTime localDateTime = LocalDateTime.parse(String.format("%sT%s", date, element));
            List<Reservation> reservationsOfSpecificTime = service.getReservationsOfSpecificTime(localDateTime);
            if (reservationsOfSpecificTime.size() < Reservation.totalMachine) {
                availableTimeList.add(String.format("%s %s", date, element));
            }
        }
        responseBody.setAvailableTimeList(availableTimeList);
        if (availableTimeList.size() == 0) {
            // unavaliable
            responseBody.setReplyCode(-1);
            return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
        } else {
            // avaliable
            responseBody.setReplyCode(1);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
    }

}