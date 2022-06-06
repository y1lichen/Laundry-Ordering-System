package com.jefferson.laundryorderingsystem.entities.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationService;
import com.jefferson.laundryorderingsystem.utils.ApplicationPasswordEncoder;
import com.jefferson.laundryorderingsystem.utils.TokenGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepo repo;
	// using service of Reservations
	@Autowired
	private ReservationService reservationService;


	private final ApplicationPasswordEncoder passwordEncoder = new ApplicationPasswordEncoder();

	private boolean tokenIsMatched(int id, String token) {
		return token.equals(repo.getById(id).getTokens());
	}

	public void removeToken(int id, String token) {
		Optional<User> optUserInDB = repo.findById(id);
		optUserInDB.ifPresent(user -> user.setTokens(null));
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

	public int changePassword(int id, String oldPassword, String newPassword) {
		User user = validAndGetUser(id, oldPassword);
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
			if (user.getTokens() != null) {
				return user.getTokens();
			}
			result = passwordEncoder.encode(TokenGenerator.generate());
			user.setTokens(result);
			saveUser(user);
		}
		return result;
	}

	public Map<String, Object> reserve(int id, String token, LocalDateTime time){
		User user = validByIdAndToken(id, token);
		if (user != null && user.getIsLogin()) {
			ArrayList<Reservation> reservationsOfADay = getUserReservationsByDate(user, time.toLocalDate());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", HttpStatus.EXPECTATION_FAILED);
			if (reservationsOfADay.size() < 1) {
				// add reservation
				int machineNum = reservationService.getMachineNum(time);
				if (machineNum < 0)
					return null;
				Reservation reservation = new Reservation(time, user, machineNum);
				reservationService.saveReservation(reservation);
				map.put("status", HttpStatus.OK);
				map.put("machineNum", machineNum);
			}
			return map;
		}
		return null;
	}
}