package com.jefferson.laundryorderingsystem.entities.reservation;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Integer> {
    
    @Query("SELECT * FROM reservation r where r.time >= time")
    List<Reservation> findAllByTimeAfter(@Param("time") String time);
}
