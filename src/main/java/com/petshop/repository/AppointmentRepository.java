package com.petshop.repository;

import com.petshop.model.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByDateAndServiceType(String date, String serviceType);
    boolean existsByServiceType(String serviceType);
}
