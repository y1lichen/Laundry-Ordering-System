package com.jefferson.laundryorderingsystem.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private static class SetCreditRequest {
        private int id;
        private String password;
        private int credit;

        public int getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        public int getCredit() {
            return credit;
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

    @PostMapping("/login")
    public ResponseEntity<Integer> loginUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(true);
            userRepo.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(1);
        }
        return ResponseEntity.badRequest().body(-1);
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
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditRequest request) {
        Optional<User> optUserInDB = userRepo.findById(request.getId());
        if (optUserInDB.isPresent()) {
            User userInDB = optUserInDB.get();
            userInDB.setCredit(request.getCredit());
            userRepo.save(userInDB);
            return ResponseEntity.status(HttpStatus.OK).body("Credit set!");
        }
        return ResponseEntity.badRequest().body("Unable correctly to set credit.");
    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAllUsers() {
        try {
            userRepo.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).body("Successfully delete all user");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }
}
