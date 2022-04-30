package com.jefferson.laundryorderingsystem.entities.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;

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

}