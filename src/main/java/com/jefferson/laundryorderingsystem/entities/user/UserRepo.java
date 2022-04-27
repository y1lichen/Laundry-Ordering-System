package com.jefferson.laundryorderingsystem.entities.user;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepo extends JpaRepository<User , Integer> {
}