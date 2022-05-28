package com.jefferson.laundryorderingsystem;

import javax.annotation.PostConstruct;

import com.jefferson.laundryorderingsystem.utils.DataCleaner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LaundryOrderingSystemApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LaundryOrderingSystemApplication.class, args);
	}

	@PostConstruct
	public void init() {
		DataCleaner dataCleaner = new DataCleaner();
		dataCleaner.removeExpiredReservation();
	}

}
