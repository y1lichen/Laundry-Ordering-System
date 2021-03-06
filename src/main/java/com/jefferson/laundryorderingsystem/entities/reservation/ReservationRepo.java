package com.jefferson.laundryorderingsystem.entities.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM reservations r WHERE r.time=time")
    List<Reservation> findAllByTime(@Param("time") LocalDateTime localDateTime);

    @Query("SELECT r FROM reservations r WHERE r.time<time")
    List<Reservation> findAllByTimeBefore(@Param("time") LocalDateTime localDateTime);

    Optional<Reservation> findFirstByTimeAndMachine(LocalDateTime time, int machine);
}
