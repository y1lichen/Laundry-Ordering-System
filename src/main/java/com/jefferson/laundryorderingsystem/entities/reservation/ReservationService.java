package com.jefferson.laundryorderingsystem.entities.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    ReservationRepo repo;

	public List<Reservation> getReservationsOfSpecificTime(LocalDateTime time) {
		return repo.findAllByTime(time);
	}

	public List<Reservation> getReservationsBeforeSpecificTime(LocalDateTime time) {
		return repo.findAllByTimeBefore(time);
	}

	public int getMachineNum(LocalDateTime time) {
		for(int i=1; i<Reservation.totalMachine+1; i++) {
			if (repo.findFirstByTimeAndMachine(time, i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	public void saveReservation(Reservation reservation) {
		repo.save(reservation);
	}

	public void deleteReservationService(int id) {
		repo.deleteById(id);
	}
}