package com.jefferson.laundryorderingsystem.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;

public class DataCleaner {

	@Autowired
    private ReservationService reservationService;

	public void removeExpiredReservation() {
		LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Taipei"));
		List<Reservation> expiredReservations = reservationService.getReservationsBeforeSpecificTime(today);
		for (Reservation reservation : expiredReservations) {
			int id = reservation.getId();
			reservationService.deleteReservationService(id);
		}
	}
}
