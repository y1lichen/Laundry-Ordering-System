package com.jefferson.laundryorderingsystem.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        List<User> users = userRepo.findAll();
        for (User user: users) {
            if (user.equals(newUser)) {
                return Status.USER_ALREADY_EXISTS;
            }
        }
        userRepo.save(newUser);
        return Status.SUCCESS;
    }

    @PostMapping("/login")
    public Status loginUser(@Valid @RequestBody User user) {
        List<User> users = userRepo.findAll();
        for (User other: users) {
            if (other.equals(user)) {
                user.setIsLogin(true);
                userRepo.save(user);
                return Status.SUCCESS;
            }
        }
        return Status.FAILURE;
    }

    @PostMapping("/logout")
    public Status logoutUser(@Valid @RequestBody User user) {
        List<User> users = userRepo.findAll();
        for (User other: users) {
            if (other.equals(user)) {
                user.setIsLogin(false);
                userRepo.save(user);
                return Status.SUCCESS;
            }
        }
        return Status.FAILURE;
    }

    @PostMapping("/delete")
    public Status deleteUser(@Valid @RequestBody User user) {
        List<User> users = userRepo.findAll();
        for (User other: users) {
            if (other.equals(user)) {
                userRepo.delete(other);
                return Status.SUCCESS;
            }
        }
        return Status.FAILURE;
    }

    @DeleteMapping("/deleteall")
    public Status deleteAllUsers() {
        userRepo.deleteAll();
        return Status.SUCCESS;
    }
}
