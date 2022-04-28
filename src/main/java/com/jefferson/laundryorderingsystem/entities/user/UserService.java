package com.jefferson.laundryorderingsystem.entities.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepo repo;

	public User validAndGetUser(int id, String password) {
		Optional<User> optUserInDB = repo.findById(id);
		if (optUserInDB.isPresent()) {
			User userInDB = optUserInDB.get();
			if (userInDB.getPassword().equals(password)) {
				return userInDB;
			}
		}
		return null;
	}

	public void saveUser(User user) {
		repo.save(user);
	}

	public Optional<User> getUserById(int id) {
		return repo.findById(id);
	}

	public int deleteUser(int id, String password) {
		User user = validAndGetUser(id, password);
		if (user != null) {
			repo.delete(user);
			return 1;
		}
		return -1;
	}
}