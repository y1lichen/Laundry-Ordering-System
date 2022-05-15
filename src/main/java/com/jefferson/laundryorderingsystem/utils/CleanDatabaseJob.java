package com.jefferson.laundryorderingsystem.utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;
import com.jefferson.laundryorderingsystem.entities.reservation.ReservationRepo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class CleanDatabaseJob implements Job {
	@Autowired
    ReservationRepo repo;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LocalDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay();
		List<Reservation> reservations = repo.findAllByTimeBefore(yesterday);
		for (Reservation reservation : reservations) {
			int id = reservation.getId();
			repo.deleteById(id);
		}
	}
}
