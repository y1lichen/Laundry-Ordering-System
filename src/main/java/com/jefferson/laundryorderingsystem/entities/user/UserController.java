package com.jefferson.laundryorderingsystem.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    // testing
    @GetMapping(value = "/test")
    public String test() {
        return "Hello";
    }

    // create User
    @PostMapping("/create")
    public Status createUser(@Valid @RequestBody User newUser) {
        Optional<User> user = userRepo.findById(newUser.getId());
        if (user.isEmpty()) {
            userRepo.save(newUser);
            return Status.SUCCESS;
        }
        return Status.USER_ALREADY_EXISTS;
    }

    @PostMapping("/login")
    public Status loginUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(true);
            userRepo.save(user);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }

    @PostMapping("/logout")
    public Status logoutUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.equals(userInDB.get())) {
            user.setIsLogin(false);
            userRepo.save(user);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }

    @PostMapping("/delete")
    public Status deleteUser(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent()) {
            userRepo.delete(userInDB.get());
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }

    @PostMapping("/add-credit")
    public Status addCredit(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.getIsLogin()) {
            user.setCredit(userInDB.get().getCredit() + 1);
            userRepo.save(user);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }

    @PostMapping("/sub-credit")
    public Status subCredit(@Valid @RequestBody User user) {
        Optional<User> userInDB = userRepo.findById(user.getId());
        if (userInDB.isPresent() && user.getIsLogin()) {
            user.setCredit(userInDB.get().getCredit() - 1);
            userRepo.save(user);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }

    @DeleteMapping("/deleteall")
    public Status deleteAllUsers() {
        userRepo.deleteAll();
        return Status.SUCCESS;
    }
}
