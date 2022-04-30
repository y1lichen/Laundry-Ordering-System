package com.jefferson.laundryorderingsystem.entities.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    // using service of Reservations
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    private static class ReserveRequestBody {

        private int id;

        private String password;

        private LocalDateTime time;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        public LocalDateTime getTime() {
            return time;
        }
    }

    private static class GetReservationRequestBody {
        private int id;
        private String password;
        private String date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }

    private static class SetCreditRequestBody {
        private int id;
        private String password;
        private boolean isIncrease;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setIncrease(boolean increase) {
            this.isIncrease = increase;
        }

        public boolean isIncrease() {
            return isIncrease;
        }
    }

    private static class ChangePasswordRequestBody {
        private int id;
        private String oldPassword;
        private String newPassword;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }

    /create User @PostMapping(value="/create")

    public ResponseEntity<String> createUser(@Valid @RequestBody User newUser) {
        Optional<User> user = userService.getUserById(newUser.getId());
        if (user.isEmpty()) {
            newUser.setIsLogin(true);
            userService.saveUser(newUser);
            return new ResponseEntity<String>("Successfully create user.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User already exist.", HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody User user) {
        User userInDB = userService.validAndGetUser(user.getId(), user.getPassword());
        if (userInDB != null) {
            user.setIsLogin(true);
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully login.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to login.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody User user) {
        User userInDB = userService.validAndGetUser(user.getId(), user.getPassword());
        if (userInDB != null) {
            user.setIsLogin(false);
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logout.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to logout.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody User body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user != null) {
            userService.deleteUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully delete user.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to delete user.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getOldPassword());
        if (user != null) {
            user.setPassword(body.getNewPassword());
            user.setIsLogin(false);
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ubable to change password.");
    }

    @PostMapping("/set-credit")
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user != null) {
            int originCredit = user.getCredit();
            if (body.isIncrease()) {
                user.setCredit(originCredit + 1);
            } else if (originCredit > 0) {
                user.setCredit(originCredit - 1);
            }
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Credit set!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to set credit.");
    }

    @PostMapping(value = "/get-user-reservations", produces = "application/json")
    public ResponseEntity<?> getUserReservation(@Valid @RequestBody GetReservationRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user != null) {
            // if contains date
            ArrayList<Object> result = new ArrayList<>();
            if (!(body.getDate().isBlank())) {
                LocalDate date = LocalDate.parse(body.getDate());
                for (Reservation reservation : userService.getUserReservationsByDate(user, date)) {
                    Map<Integer, Object> item = new HashMap<Integer,Object>() {
                        {
                            Map<String, Object> machineAndTime = new HashMap<>();
                            machineAndTime.put("machine_num", reservation.getMachine());
                            machineAndTime.put("time", reservation.getTime());
                            put(reservation.getId(), machineAndTime);
                        }
                    };
                }
            } else {
                for (Reservation reservation : user.getReservations()) {
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Unable to find the user.", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/reserve", produces = "application/json")
    public ResponseEntity<?> reserve(@Valid @RequestBody ReserveRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user != null) {
            ArrayList<Reservation> reservationsOfADay = userService.getUserReservationsByDate(user,
                    body.getTime().toLocalDate());
            if (reservationsOfADay.size() < 1) {
                // add reservation
                LocalDateTime time = body.getTime();
                int machineNum = reservationService.getMachineNum(time);
                if (machineNum < 0)
                    return new ResponseEntity<String>("Unable to reserve.", HttpStatus.INTERNAL_SERVER_ERROR);
                Reservation reservation = new Reservation(time, user, machineNum);
                reservationService.saveReservation(reservation);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", HttpStatus.OK);
                map.put("machineNum", machineNum);
                return new ResponseEntity<Object>(map, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("One day one reservations!", HttpStatus.EXPECTATION_FAILED);
            }
        }
        return new ResponseEntity<String>("Unable to correctly operate reservation.", HttpStatus.UNAUTHORIZED);
    }
}
