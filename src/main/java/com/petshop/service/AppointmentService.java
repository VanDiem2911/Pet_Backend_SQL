package com.petshop.service;

import com.petshop.model.Appointment;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.PromotionRepository;
import com.petshop.repository.PetServiceRepository;
import com.petshop.model.Promotion;
import com.petshop.model.PromotionTier;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PromotionRepository promotionRepository;
    private final PetServiceRepository petServiceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PromotionRepository promotionRepository,
                              PetServiceRepository petServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.promotionRepository = promotionRepository;
        this.petServiceRepository = petServiceRepository;
    }

    public synchronized Appointment book(Appointment appointment) {
        if (appointment.getDate() == null || appointment.getDate().isBlank()) {
            throw new IllegalArgumentException("Ngày đặt lịch không được để trống.");
        }
        
        // Validate phone number format (must be 10-digit Vietnamese mobile number)
        String normalized = normalizePhone(appointment.getPhone());
        if (!normalized.matches("^0[35789]\\d{8}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải là số di động Việt Nam gồm 10 chữ số).");
        }

        // Validate stay dates for boarding
        if ("boarding".equals(appointment.getServiceType())) {
            if (appointment.getCheckoutDate() == null || appointment.getCheckoutDate().isBlank()) {
                throw new IllegalArgumentException("Ngày trả thú cưng không được để trống đối với dịch vụ gửi trông giữ.");
            }
            try {
                LocalDate checkin = LocalDate.parse(appointment.getDate());
                LocalDate checkout = LocalDate.parse(appointment.getCheckoutDate());
                if (checkout.isBefore(checkin)) {
                    throw new IllegalArgumentException("Ngày trả thú cưng không được trước ngày gửi.");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Định dạng ngày gửi hoặc ngày trả thú cưng không hợp lệ.");
            }
        }

        // Validate timeSlot for grooming or home_service
        if (("grooming".equals(appointment.getServiceType()) || "home_service".equals(appointment.getServiceType()))
                && (appointment.getTimeSlot() == null || appointment.getTimeSlot().isBlank())) {
            throw new IllegalArgumentException("Vui lòng chọn khung giờ cho dịch vụ này.");
        }

        if (appointment.getTimeSlot() != null && !appointment.getTimeSlot().isBlank()) {
            List<Appointment> existing = appointmentRepository.findByDateAndServiceType(
                    appointment.getDate(), appointment.getServiceType());
            boolean alreadyBooked = existing.stream()
                    .anyMatch(a -> (appointment.getId() == null || !a.getId().equals(appointment.getId()))
                            && !"CANCELLED".equalsIgnoreCase(a.getStatus())
                            && appointment.getTimeSlot().equals(a.getTimeSlot()));
            if (alreadyBooked) {
                throw new IllegalArgumentException("Khung giờ này đã được đặt trước. Vui lòng chọn khung giờ khác.");
            }
        }
        appointment.setStatus("PENDING");
        applyBestPromotion(appointment);
        return appointmentRepository.save(appointment);
    }

    private int getBookingPrice(Appointment appointment) {
        String serviceType = appointment.getServiceType();
        int defaultPrice = "boarding".equals(serviceType) ? 250000 : 200000;
        int priceFromDb = defaultPrice;
        
        var serviceOpt = petServiceRepository.findByCode(serviceType);
        if (serviceOpt.isPresent() && serviceOpt.get().getPrice() != null) {
            priceFromDb = serviceOpt.get().getPrice().intValue();
        }

        if ("boarding".equals(serviceType) && appointment.getCheckoutDate() != null) {
            try {
                LocalDate checkin = LocalDate.parse(appointment.getDate());
                LocalDate checkout = LocalDate.parse(appointment.getCheckoutDate());
                long days = ChronoUnit.DAYS.between(checkin, checkout) + 1;
                return (int) (days * priceFromDb);
            } catch (Exception e) {
                // ignore
            }
        }
        return priceFromDb;
    }

    private void applyBestPromotion(Appointment appointment) {
        PromotionChoice best = promotionRepository.findByActiveTrue().stream()
                .map(promotion -> evaluate(promotion, appointment))
                .filter(java.util.Objects::nonNull)
                .max(Comparator.comparingInt(PromotionChoice::monetaryDiscount))
                .orElse(null);

        String backendPromoId = best == null ? null : best.promotion().getId();
        int backendDiscountPercent = best == null ? 0 : best.discountPercent();
        int backendDiscountAmount = best == null ? 0 : best.discountAmount();

        String frontendPromoId = appointment.getAppliedPromotionId();
        int frontendDiscountPercent = appointment.getDiscountPercent() == null ? 0 : appointment.getDiscountPercent();
        int frontendDiscountAmount = appointment.getDiscountAmount() == null ? 0 : appointment.getDiscountAmount();

        if (frontendPromoId == null && backendPromoId == null) {
            appointment.setAppliedPromotionId(null);
            appointment.setAppliedPromotionTitle(null);
            appointment.setDiscountPercent(0);
            appointment.setDiscountAmount(0);
            return;
        }

        if (!java.util.Objects.equals(frontendPromoId, backendPromoId)
                || frontendDiscountPercent != backendDiscountPercent
                || frontendDiscountAmount != backendDiscountAmount) {
            throw new IllegalArgumentException("Thông tin ưu đãi đã thay đổi (nhận được giảm "
                    + (backendDiscountPercent > 0 ? backendDiscountPercent + "%" : backendDiscountAmount + "đ")
                    + " từ " + (best != null ? best.promotion().getTitle() : "không áp dụng")
                    + " thay vì " + (frontendDiscountPercent > 0 ? frontendDiscountPercent + "%" : frontendDiscountAmount + "đ")
                    + "). Vui lòng tải lại trang và đặt lịch lại.");
        }

        if (best == null) {
            appointment.setAppliedPromotionId(null);
            appointment.setAppliedPromotionTitle(null);
            appointment.setDiscountPercent(0);
            appointment.setDiscountAmount(0);
        } else {
            appointment.setAppliedPromotionId(best.promotion().getId());
            appointment.setAppliedPromotionTitle(best.promotion().getTitle());
            appointment.setDiscountPercent(best.discountPercent());
            appointment.setDiscountAmount(best.discountAmount());
        }
    }

    private PromotionChoice evaluate(Promotion promotion, Appointment appointment) {
        try {
            if (appointment.getDate() == null)
                return null;
            LocalDate date = LocalDate.parse(appointment.getDate());
            if (promotion.getServiceCode() != null && !promotion.getServiceCode().isBlank()
                    && !promotion.getServiceCode().equals(appointment.getServiceType()))
                return null;

            if (promotion.getStartDate() != null && !promotion.getStartDate().isBlank()) {
                try {
                    if (date.isBefore(LocalDate.parse(promotion.getStartDate())))
                        return null;
                } catch (Exception e) {
                    return null;
                }
            }
            if (promotion.getEndDate() != null && !promotion.getEndDate().isBlank()) {
                try {
                    if (date.isAfter(LocalDate.parse(promotion.getEndDate())))
                        return null;
                } catch (Exception e) {
                    return null;
                }
            }

            String type = promotion.getPromotionType();
            if (promotion.getTiers() != null && !promotion.getTiers().isEmpty())
                type = "LONG_STAY";
            if (type == null)
                type = "PREBOOK";

            int discountPercent = 0;
            int discountAmount = 0;

            if ("TIME_SLOT".equals(type)) {
                if (appointment.getTimeSlot() == null || appointment.getTimeSlot().length() < 5
                        || promotion.getStartTime() == null || promotion.getEndTime() == null)
                    return null;
                LocalTime slot;
                try {
                    slot = LocalTime.parse(appointment.getTimeSlot().substring(0, 5));
                } catch (Exception e) {
                    return null;
                }
                LocalTime promoStart;
                LocalTime promoEnd;
                try {
                    promoStart = LocalTime.parse(promotion.getStartTime());
                    promoEnd = LocalTime.parse(promotion.getEndTime());
                } catch (Exception e) {
                    return null;
                }
                if (slot.isBefore(promoStart) || !slot.isBefore(promoEnd))
                    return null;
                discountPercent = promotion.getDiscountPercent() == null ? 0 : promotion.getDiscountPercent();
            } else if ("PREBOOK".equals(type)) {
                LocalTime time;
                try {
                    time = appointment.getTimeSlot() != null && appointment.getTimeSlot().length() >= 5
                            ? LocalTime.parse(appointment.getTimeSlot().substring(0, 5))
                            : LocalTime.MAX;
                } catch (Exception e) {
                    time = LocalTime.MAX;
                }
                long hoursAhead = Duration
                        .between(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), LocalDateTime.of(date, time))
                        .toHours();
                if (hoursAhead < (promotion.getAdvanceHours() == null ? 24 : promotion.getAdvanceHours()))
                    return null;
                discountPercent = promotion.getDiscountPercent() == null ? 0 : promotion.getDiscountPercent();
            } else if ("LONG_STAY".equals(type)) {
                if (appointment.getCheckoutDate() == null)
                    return null;
                LocalDate checkoutDate;
                try {
                    checkoutDate = LocalDate.parse(appointment.getCheckoutDate());
                } catch (Exception e) {
                    return null;
                }
                long days = ChronoUnit.DAYS.between(date, checkoutDate) + 1;
                PromotionTier tier = promotion.getTiers().stream()
                        .filter(item -> days >= item.getMinDays()
                                && (item.getMaxDays() == null || days <= item.getMaxDays()))
                        .findFirst().orElse(null);
                if (tier == null)
                    return null;
                discountPercent = tier.getDiscountPercent();
            } else if ("SPECIFIC_DATE".equals(type)) {
                discountAmount = promotion.getDiscountAmount() == null ? 0 : promotion.getDiscountAmount();
            }

            int bookingPrice = getBookingPrice(appointment);
            int monetaryDiscount = 0;
            if (discountPercent > 0) {
                monetaryDiscount = bookingPrice * discountPercent / 100;
            } else if (discountAmount > 0) {
                monetaryDiscount = discountAmount;
            }

            return new PromotionChoice(promotion, discountPercent, discountAmount, monetaryDiscount);
        } catch (Exception e) {
            System.err.println("Error evaluating promotion " + promotion.getId() + ": " + e.getMessage());
            return null;
        }
    }

    private record PromotionChoice(Promotion promotion, int discountPercent, int discountAmount, int monetaryDiscount) {
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPhone(String phone) {
        String normalizedPhone = normalizePhone(phone);
        return appointmentRepository.findAll().stream()
                .filter(appointment -> normalizedPhone.equals(normalizePhone(appointment.getPhone())))
                .sorted(Comparator.comparing(Appointment::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private String normalizePhone(String phone) {
        if (phone == null)
            return "";
        String digits = phone.replaceAll("\\D", "");
        if (digits.startsWith("84") && digits.length() > 9) {
            return "0" + digits.substring(2);
        }
        return digits;
    }

    /**
     * Returns the set of booked timeSlots for a given date and serviceType (e.g.
     * "grooming").
     * Only active (non-CANCELLED) bookings are counted.
     */
    public Set<String> getBookedSlots(String date, String serviceType) {
        return appointmentRepository.findByDateAndServiceType(date, serviceType)
                .stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .map(Appointment::getTimeSlot)
                .filter(slot -> slot != null && !slot.isBlank())
                .collect(Collectors.toSet());
    }
}
