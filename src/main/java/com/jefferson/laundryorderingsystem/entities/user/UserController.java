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

    private static class ReserveRequestBody {
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

    @Autowired
    private UserRepo userRepo;

    // create User
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createUser(@Valid @RequestBody User newUser) {
        Optional<User> user = userRepo.findById(newUser.getId());
        CreateUserResponseBody responseBody = new CreateUserResponseBody(-1, "Unable to create user.");
        try {
            if (user.isEmpty()) {
                userRepo.save(newUser);
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
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(true);
            userRepo.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully login.");
        }
        return ResponseEntity.badRequest().body("Unable to login.");
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

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent()) {
            userRepo.delete(userInDB.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully to delete user.");
        }
        return ResponseEntity.badRequest().body("Unable to delete user.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestBody body) {
        Optional<User> optionalUserInDB = userRepo.findById(body.getId());
        if (optionalUserInDB.isPresent()) {
            User userInDB = optionalUserInDB.get();
            if (userInDB.getPassword().equals(body.getOldPassword())) {
                userInDB.setPassword(body.getNewPassword());
            }
            userRepo.save(userInDB);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ubable to change password.");
    }

    @PostMapping("/set-credit")
    public ResponseEntity<String> addCredit(@Valid @RequestBody SetCreditRequestBody body) {
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

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@Valid @RequestBody ReserveRequestBody body) {
        return ResponseEntity.badRequest().body("Unable to correctly operate reservation.");
    }

    // for testing
    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAllUsers() {
        userRepo.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Request accepted.");
    }
}
