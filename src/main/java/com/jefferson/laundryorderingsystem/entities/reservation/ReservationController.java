package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jefferson.laundryorderingsystem.entities.user.User;
import com.jefferson.laundryorderingsystem.entities.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "reservations")
public class ReservationController {

    private static class DeleteReservationRequest {
        int userId;
        String token;
        int reservationId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public int getReservationId() {
            return reservationId;
        }

        public void setReservationId(int reservationId) {
            this.reservationId = reservationId;
        }
    }

    @Autowired
    private ReservationService service;
    @Autowired
    private UserService userService;

    private static final String[] possibleTime = { "16:00:00", "16:40:00", "17:20:00", "18:00:00", "18:40:00",
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

    @PostMapping("delete")
    public ResponseEntity<?> deleteReservation(@RequestBody DeleteReservationRequest body) {
        User user = userService.validByIdAndToken(body.getUserId(), body.getToken());
        if (user == null) return new ResponseEntity<String>("Unauthorized, unable to delete reservation", HttpStatus.UNAUTHORIZED);
        service.deleteReservationService(body.getReservationId());
        return new ResponseEntity<String>("Successfully delete the reservation.", HttpStatus.OK);
    }
}