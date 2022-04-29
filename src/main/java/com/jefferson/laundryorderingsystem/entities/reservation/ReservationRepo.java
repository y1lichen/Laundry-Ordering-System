package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM reservations r WHERE r.time=time")
    List<Reservation> findAllByTime(@Param("time") LocalDateTime localDateTime);

    @Query("SELECT r from reservations r WHERE r.time=time AND r.machine=machine")
    Reservation findByTimeAndMachine(@Param("time") LocalDateTime localDateTime, @Param("machine") int machine);
}
