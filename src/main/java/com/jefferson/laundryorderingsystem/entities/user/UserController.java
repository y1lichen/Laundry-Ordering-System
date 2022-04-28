package com.jefferson.laundryorderingsystem.entities.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
        private Optional<LocalDate> date;

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

        public Optional<LocalDate> getDate() {
            return date;
        }

        public void setDate(Optional<LocalDate> date) {
            this.date = date;
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

    private static class CreateUserResponseBody {
        private int replyCode;
        private String description;

        public CreateUserResponseBody(int replyCode, String description) {
            this.replyCode = replyCode;
            this.description = description;
        }

        public void setReplyCode(int replyCode) {
            this.replyCode = replyCode;
        }

        public int getReplyCode() {
            return replyCode;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class GetReservationResponseBody {
        private HashMap<Integer, LocalDateTime> reservationHasMap = new HashMap<>();

        public void addReservation(int id, LocalDateTime time) {
            reservationHasMap.put(id, time);
        }

        public HashMap<Integer, LocalDateTime> getReservationHasMap() {
            return reservationHasMap;
        }
    }

    // create User
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createUser(@Valid @RequestBody User newUser) {
        Optional<User> user = userService.getUserById(newUser.getId());
        CreateUserResponseBody responseBody = new CreateUserResponseBody(-1, "Unable to create user.");
        try {
            if (user.isEmpty()) {
                newUser.setIsLogin(true);
                userService.saveUser(newUser);
                responseBody.setReplyCode(1);
                responseBody.setDescription("Successfully create user.");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.setReplyCode(0);
                responseBody.setDescription("User existed.");
                return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
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
        return ResponseEntity.badRequest().body("Unable to login.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody User user) {
        User userInDB = userService.validAndGetUser(user.getId(), user.getPassword());
        if (userInDB != null) {
            user.setIsLogin(false);
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logout.");
        }
        return ResponseEntity.badRequest().body("Unable to logout.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody User user) {
        int result = userService.deleteUser(user.getId(), user.getPassword());
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.OK).body("Successfully to delete user.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to delete user.");
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ubable to change password.");
    }

    @PostMapping("/set-credit")
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), user.getPassword());
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to set credit.");
    }

    private ArrayList<Reservation> getUserReservationsByDate(User user, LocalDate date) {
        ArrayList<Reservation> result = new ArrayList<>();
        for (Reservation reservation : user.getReservations()) {
            if (reservation.getTime().toLocalDate() == date) {
                result.add(reservation);
            }
        }
        return result;
    }

    @PostMapping(value = "/get-user-reservations", produces = "application/json")
    public ResponseEntity<?> getUserReservation(@Valid @RequestBody GetReservationRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user != null) {
            GetReservationResponseBody response = new GetReservationResponseBody();
            // if contains date
            if (body.getDate().isPresent()) {
                LocalDate date = body.getDate().get();
                for (Reservation reservation : getUserReservationsByDate(user, date)) {
                    response.addReservation(reservation.getId(), reservation.getTime());
                }
            } else {
                for (Reservation reservation : user.getReservations()) {
                    response.addReservation(reservation.getId(), reservation.getTime());
                }
            }
            return new ResponseEntity<GetReservationResponseBody>(response, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Unable to find the user.", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@Valid @RequestBody ReserveRequestBody body) {
        User user = userService.validAndGetUser(body.getId(), body.getPassword());
        if (user.getPassword().equals(body.getPassword())) {
            ArrayList<Reservation> reservationsOfADay = getUserReservationsByDate(user,
                    body.getTime().toLocalDate());
            if (reservationsOfADay.size() > 1) {
                return new ResponseEntity<String>("One day one reservations!", HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<String>("reserved!", HttpStatus.OK);
            }
        }
        return ResponseEntity.badRequest().body("Unable to correctly operate reservation.");
    }
}
