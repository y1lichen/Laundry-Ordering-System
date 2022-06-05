package com.jefferson.laundryorderingsystem.entities.user;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController {


    @Autowired
    private UserService userService;

    private static class ReserveRequestBody {

        private int id;

        private String token;

        private LocalDateTime time;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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
        private String token;
        private String date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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
        private String token;
        private boolean isIncrease;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setIncrease(boolean increase) {
            this.isIncrease = increase;
        }

        public boolean isIncrease() {
            return isIncrease;
        }
    }

    private static class LogoutRequestBody {
        private int id;
        private String token;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
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

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }

    private static class LoginResponse {
       private String message = "Successfully login!";
       private String token = "";

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody User newUser) {
        int result = userService.register(newUser.getId(), newUser.getPassword());
        if (result > 0) {
            return new ResponseEntity<String>("Successfully create user.", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("User already exist.", HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> loginUser(@Valid @RequestBody User user) {
        LoginResponse response = new LoginResponse();
        String token = userService.login(user.getId(), user.getPassword());
        if (token != null) {
            response.setToken(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Unable to login", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody LogoutRequestBody body) {
        User userInDB = userService.validByIdAndToken(body.getId(), body.getToken());
        if (userInDB != null) {
            userInDB.setIsLogin(false);
            userService.removeToken(body.getId(), body.getToken());
            userService.saveUser(userInDB);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logout.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to logout.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody LogoutRequestBody body) {
        User user = userService.validByIdAndToken(body.getId(), body.getToken());
        if (user != null && user.getIsLogin()) {
            userService.deleteUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully delete user.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to delete user.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestBody body) {
        if (userService.changePassword(body.getId(), body.getOldPassword(), body.getNewPassword()) > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Password changed!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to change password.");
    }

    @PostMapping("/set-credit")
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditRequestBody body) {
        User user = userService.validByIdAndToken(body.getId(), body.getToken());
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
        User user = userService.validByIdAndToken(body.getId(), body.getToken());
        if (user != null && user.getIsLogin()) {
            // if contains date
            ArrayList<Object> result = new ArrayList<>();
            ArrayList<Reservation> reservations;
            if (!(body.getDate().isBlank())) {
                LocalDate date = LocalDate.parse(body.getDate());
                reservations = userService.getUserReservationsByDate(user, date);
            } else {
                reservations = new ArrayList<>(user.getReservations());
            }
            for (Reservation reservation : reservations) {
                Map<String, Object> item = new HashMap<String,Object>() {
                    {
                        Map<String, Object> machineAndTime = new HashMap<>();
                        machineAndTime.put("machine_num", reservation.getMachine());
                        machineAndTime.put("time", reservation.getTime());
                        put("id", reservation.getId());
                        put("info", machineAndTime);
                    }
                };
                result.add(item);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Unable to find the user.", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/reserve", produces = "application/json")
    public ResponseEntity<?> reserve(@Valid @RequestBody ReserveRequestBody body) {
        Map<String, Object> map = userService.reserve(body.getId(), body.getToken(), body.getTime());
        int status = (int) map.get("status");
        if (status == HttpStatus.OK.value()) {
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } else if (status == HttpStatus.EXPECTATION_FAILED.value()) {
            return new ResponseEntity<String>("One day one reservations!", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<String>("Unable to correctly operate reservation.", HttpStatus.UNAUTHORIZED);
    }

}
