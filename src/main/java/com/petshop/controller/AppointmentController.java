package com.petshop.controller;

import com.petshop.model.Appointment;
import com.petshop.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@Valid @RequestBody Appointment appointment) {
        return appointmentService.book(appointment);
    }

    @GetMapping
    public List<Appointment> list() {
        return appointmentService.findAll();
    }

    @GetMapping("/track")
    public List<Appointment> track(@RequestParam String phone) {
        String digits = phone == null ? "" : phone.replaceAll("\\D", "");
        if (digits.length() < 9 || digits.length() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không hợp lệ");
        }
        return appointmentService.findByPhone(phone);
    }

    /**
     * GET /api/appointments/booked-slots?date=YYYY-MM-DD&serviceType=grooming
     * Returns the set of time slot strings that are already booked on that date.
     */
    @GetMapping("/booked-slots")
    public Set<String> bookedSlots(
            @RequestParam String date,
            @RequestParam(defaultValue = "grooming") String serviceType
    ) {
        if (!StringUtils.hasText(date)) return Set.of();
        return appointmentService.getBookedSlots(date, serviceType);
    }
}
