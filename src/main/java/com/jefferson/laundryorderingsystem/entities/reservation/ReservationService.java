package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    ReservationRepo repo;

    private class ReservationResponse {
    	int machineId;
    	String response;
    }

	public List<Reservation> getReservationsOfSpecificTime(LocalDateTime time) {
		return repo.findAllByTime(time);
	}

	public int getMachineNum(LocalDateTime time) {
		for(int i=1; i<Reservation.totalMachine+1; i++) {
			if (repo.findByTimeAndMachine(time, i) == null) {
				return i;
			}
		}
		return -1;
	}

	public void saveReservation(Reservation reservation) {
		repo.save(reservation);
	}

}