package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // total amount of laundry machine
    private static int totalMachine = 18;

    Logger logger = LoggerFactory.getLogger(ReservationController.class);

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

        public void addToAvaliableTimeList(String time) {
            availableTimeList.add(time);
        }

        public ArrayList<String> getAvailableTimeList() {
            return availableTimeList;
        }
    }

    @Autowired
    ReservationRepo repo;

    @GetMapping(value = "/get-avaliable-reservations", produces = "application/json")
    public ResponseEntity<?> getAvaliableReservation(@RequestParam String time) {
        GetAvailableReservationsResponse responseBody = new GetAvailableReservationsResponse();
        for (String element : possibleTime) {
            LocalDateTime localDateTime = LocalDateTime.parse(String.format("%sT%s", time, element));
            List<Reservation> reservationsOfSpecificTime = repo.findAllByTime(localDateTime);
            if (reservationsOfSpecificTime.size() < totalMachine) {
                // avaliable
                responseBody.setReplyCode(1);
                responseBody.addToAvaliableTimeList(reservationsOfSpecificTime.get(0).getTime().toString());
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                // unavaliable
                responseBody.setReplyCode(-1);
                return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

}