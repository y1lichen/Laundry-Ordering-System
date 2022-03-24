package com.jefferson.laundryorderingsystem.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static class SetCreditResponseBody {
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

        public boolean isIncrease() {
            return isIncrease;
        }

        public void setIncrease(boolean increase) {
            isIncrease = increase;
        }
    }

    private static class LoginResponse {
        private int returnCode;
        public void setReturnCode(int returnCode) {
            this.returnCode = returnCode;
        }
        public int getReturnCode() {
            return returnCode;
        }
    }

    @Autowired
    private UserRepo userRepo;

    // testing
    @GetMapping(value = "/test")
    public String test() {
        return "Hello";
    }

    // create User
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody User newUser) {
        Optional<User> user = userRepo.findById(newUser.getId());
        if (user.isEmpty()) {
            userRepo.save(newUser);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully create user.");
        }
        return ResponseEntity.badRequest().body("User already exist.");
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> loginUser(@Valid @RequestBody User user) {
        LoginResponse response = new LoginResponse();
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(true);
            userRepo.save(user);
            response.setReturnCode(1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setReturnCode(-1);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(false);
            userRepo.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logout.");
        }
        return ResponseEntity.badRequest().body("Unable to logout.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent()) {
            userRepo.delete(userInDB.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully to delete user.");
        }
        return ResponseEntity.badRequest().body("Unable to delete user.");
    }

    @PostMapping("/set-credit")
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditResponseBody body) {
        User requestUser = new User(body.getId(), body.getPassword());
        Optional<User> optUserInDB = userRepo.findById(body.getId());
        if (optUserInDB.isPresent()) {
            User userInDB = optUserInDB.get();
            if (userInDB.equals(requestUser) && userInDB.getIsLogin()) {
                int originCredit = userInDB.getCredit();
                if (body.isIncrease()) {
                    userInDB.setCredit(originCredit + 1);
                } else if (originCredit > 0) {
                    userInDB.setCredit(originCredit - 1);
                }
                userRepo.save(userInDB);
                return ResponseEntity.status(HttpStatus.OK).body("Credit set!");
            }
        }
        return ResponseEntity.badRequest().body("Unable to correctly set credit.");
    }

    // for testing
    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAllUsers() {
        userRepo.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Request accepted.");
    }
}
