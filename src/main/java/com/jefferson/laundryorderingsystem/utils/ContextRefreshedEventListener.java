package com.jefferson.laundryorderingsystem.utils;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ContextRefreshedEventListener {

	@Autowired
    private ReservationService reservationService;

	public void removeExpiredReservations() {
		LocalDateTime today = LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Taipei")), LocalTime.MIDNIGHT);
		List<Reservation> expiredReservations = reservationService.getReservationsBeforeSpecificTime(today);
		for (Reservation reservation : expiredReservations) {
			int id = reservation.getId();
			reservationService.deleteReservationService(id);
		}
	}
}
