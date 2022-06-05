package com.jefferson.laundryorderingsystem.entities.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.utils.ApplicationPasswordEncoder;

import com.jefferson.laundryorderingsystem.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepo repo;

	private final ApplicationPasswordEncoder passwordEncoder = new ApplicationPasswordEncoder();

	private boolean tokenIsMatched(int id, String token) {
		Optional<User> optUserInDB = repo.findById(id);
		return optUserInDB.map(user -> user.getTokens().contains(token)).orElse(false);
	}

	public void removeToken(int id, String token) {
		Optional<User> optUserInDB = repo.findById(id);
		optUserInDB.ifPresent(user -> user.getTokens().remove(token));
	}

	public User validByIdAndToken(int id, String token) {
		Optional<User> optUserInDB = repo.findById(id);
		if (optUserInDB.isPresent()) {
			User userInDB = optUserInDB.get();
			if (tokenIsMatched(id, token)) {
				return userInDB;
			}
		}
		return null;
	}

	public User validAndGetUser(int id, String password) {
		Optional<User> optUserInDB = repo.findById(id);
		if (optUserInDB.isPresent()) {
			User userInDB = optUserInDB.get();
			if (passwordEncoder.matches(password, userInDB.getPassword())) {
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

	public void deleteUser(User user) {
		repo.delete(user);
	}

	public ArrayList<Reservation> getUserReservationsByDate(User user, LocalDate date) {
		ArrayList<Reservation> result = new ArrayList<>();
		for (Reservation reservation : user.getReservations()) {
			if (reservation.getTime().toLocalDate().equals(date)) {
				result.add(reservation);
			}
		}
		return result;
	}

	public int changePassword(int id, String token, String newPassword) {
		User user = validByIdAndToken(id, token);
		if (user != null) {
			user.setPassword(passwordEncoder.encode(newPassword));
			saveUser(user);
			return 1;
		}
		return -1;
	}

	public int register(int id, String password) {
        Optional<User> user = getUserById(id);
        if (user.isEmpty()) {
        	User newUser = new User(id, passwordEncoder.encode(password));
            newUser.setIsLogin(false);
            saveUser(newUser);
            return 1;
        } else {
            return -1;
        }
	}

	public String login(int id, String password) {
		String result = null;
		User user = validAndGetUser(id, password);
		if (user != null) {
			user.setIsLogin(true);
			result = TokenGenerator.generate();
			user.getTokens().add(result);
			saveUser(user);
		}
		return result;
	}
}