package com.jefferson.laundryorderingsystem.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DataCleaner implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
    private ReservationService reservationService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LocalDateTime today = LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Taipei")), LocalTime.MIDNIGHT);
		List<Reservation> expiredReservations = reservationService.getReservationsBeforeSpecificTime(today);
		for (Reservation reservation : expiredReservations) {
			int id = reservation.getId();
			reservationService.deleteReservationService(id);
		}
	}
}
