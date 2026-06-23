package com.petshop.controller;

import com.petshop.model.Appointment;
import com.petshop.model.Contact;
import com.petshop.model.News;
import com.petshop.model.PetService;
import com.petshop.model.Promotion;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.ContactRepository;
import com.petshop.repository.NewsRepository;
import com.petshop.repository.PetServiceRepository;
import com.petshop.repository.PromotionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final String adminUser;
    private final String adminPass;
    private final AppointmentRepository appointmentRepository;
    private final ContactRepository contactRepository;
    private final NewsRepository newsRepository;
    private final PetServiceRepository petServiceRepository;
    private final PromotionRepository promotionRepository;

    // Simple in-memory token store (sufficient for single-admin use)
    private String currentToken = null;

    public AdminController(
            @Value("${app.admin.username:admin}") String adminUser,
            @Value("${app.admin.password:petshop@2024}") String adminPass,
            AppointmentRepository appointmentRepository,
            ContactRepository contactRepository,
            NewsRepository newsRepository,
            PetServiceRepository petServiceRepository,
            PromotionRepository promotionRepository
    ) {
        this.adminUser = adminUser;
        this.adminPass = adminPass;
        this.appointmentRepository = appointmentRepository;
        this.contactRepository = contactRepository;
        this.newsRepository = newsRepository;
        this.petServiceRepository = petServiceRepository;
        this.promotionRepository = promotionRepository;
    }

    // ── AUTH ──────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String u = body.getOrDefault("username", "");
        String p = body.getOrDefault("password", "");
        if (!adminUser.equals(u) || !adminPass.equals(p)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai tài khoản hoặc mật khẩu");
        }
        currentToken = UUID.randomUUID().toString();
        return Map.of("token", currentToken);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        currentToken = null;
        return Map.of("message", "Đã đăng xuất");
    }

    // ── GUARD ─────────────────────────────────────────────────────────────

    private void requireAuth(String token) {
        if (token == null || !token.equals(currentToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }
    }

    // ── STATS ─────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        long totalAppointments = appointmentRepository.count();
        long pendingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();
        long totalContacts = contactRepository.count();
        long totalNews = newsRepository.count();
        return Map.of(
                "totalAppointments", totalAppointments,
                "pendingAppointments", pendingAppointments,
                "totalContacts", totalContacts,
                "totalNews", totalNews,
                "totalServices", petServiceRepository.count(),
                "totalPromotions", promotionRepository.count()
        );
    }

    // SERVICES

    @GetMapping("/services")
    public List<PetService> services(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        return petServiceRepository.findAll();
    }

    @PostMapping("/services")
    @ResponseStatus(HttpStatus.CREATED)
    public PetService createService(@Valid @RequestBody PetService service,
                                    @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        petServiceRepository.findByCode(service.getCode()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã dịch vụ đã tồn tại");
        });
        return petServiceRepository.save(service);
    }

    @PutMapping("/services/{id}")
    public PetService updateService(@PathVariable String id, @Valid @RequestBody PetService service,
                                    @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        PetService existing = petServiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"));
        service.setId(existing.getId());
        return petServiceRepository.save(service);
    }

    @DeleteMapping("/services/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable String id,
                              @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        PetService service = petServiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"));
        if (appointmentRepository.existsByServiceType(service.getCode()) || promotionRepository.existsByServiceCode(service.getCode())) {
            service.setActive(false);
            petServiceRepository.save(service);
            return;
        }
        petServiceRepository.delete(service);
    }

    // PROMOTIONS

    @GetMapping("/promotions")
    public List<Promotion> promotions(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        return promotionRepository.findAll();
    }

    @PostMapping("/promotions")
    @ResponseStatus(HttpStatus.CREATED)
    public Promotion createPromotion(@Valid @RequestBody Promotion promotion,
                                     @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        validatePromotion(promotion);
        return promotionRepository.save(promotion);
    }

    @PutMapping("/promotions/{id}")
    public Promotion updatePromotion(@PathVariable String id, @Valid @RequestBody Promotion promotion,
                                     @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        validatePromotion(promotion);
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));
        promotion.setId(existing.getId());
        return promotionRepository.save(promotion);
    }

    @DeleteMapping("/promotions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@PathVariable String id,
                                @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        promotionRepository.deleteById(id);
    }

    private void validatePromotion(Promotion promotion) {
        String type = promotion.getPromotionType() == null ? "PREBOOK" : promotion.getPromotionType();
        if (!Set.of("PREBOOK", "LONG_STAY", "TIME_SLOT", "SPECIFIC_DATE").contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại khuyến mãi không hợp lệ");
        }
        try {
            if (promotion.getStartDate() != null && !promotion.getStartDate().isBlank()
                    && promotion.getEndDate() != null && !promotion.getEndDate().isBlank()
                    && LocalDate.parse(promotion.getStartDate()).isAfter(LocalDate.parse(promotion.getEndDate()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu");
            }
            if ("TIME_SLOT".equals(type)) {
                if (promotion.getStartTime() == null || promotion.getEndTime() == null
                        || !LocalTime.parse(promotion.getStartTime()).isBefore(LocalTime.parse(promotion.getEndTime()))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khung giờ khuyến mãi không hợp lệ");
                }
            }
        } catch (DateTimeParseException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày hoặc giờ không đúng định dạng");
        }
        if ("LONG_STAY".equals(type)) {
            if (promotion.getTiers() == null || promotion.getTiers().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại ưu đãi Ở càng lâu yêu cầu phải có ít nhất một mốc giảm giá.");
            }
            var tiers = promotion.getTiers().stream()
                    .sorted(java.util.Comparator.comparing(com.petshop.model.PromotionTier::getMinDays))
                    .toList();
            int previousMax = 0;
            for (var tier : tiers) {
                if (tier.getMinDays() == null || tier.getMinDays() <= previousMax
                        || (tier.getMaxDays() != null && tier.getMaxDays() < tier.getMinDays())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Các mốc ngày bị trùng hoặc không hợp lệ");
                }
                previousMax = tier.getMaxDays() == null ? Integer.MAX_VALUE : tier.getMaxDays();
            }
        }
        if ("SPECIFIC_DATE".equals(type)) {
            if (promotion.getDiscountAmount() == null || promotion.getDiscountAmount() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại ưu đãi theo ngày cụ thể yêu cầu số tiền giảm lớn hơn 0.");
            }
        }
    }

    // ── CONTACTS ──────────────────────────────────────────────────────────

    @GetMapping("/contacts")
    public List<Contact> contacts(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        return contactRepository.findAll();
    }

    @PatchMapping("/contacts/{id}/status")
    public Contact updateContactStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        Contact c = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy liên hệ"));
        c.setStatus(body.getOrDefault("status", c.getStatus()));
        return contactRepository.save(c);
    }

    @DeleteMapping("/contacts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(
            @PathVariable String id,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        contactRepository.deleteById(id);
    }

    // ── NEWS ──────────────────────────────────────────────────────────────

    @GetMapping("/news")
    public List<News> news(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/news")
    @ResponseStatus(HttpStatus.CREATED)
    public News createNews(
            @Valid @RequestBody News news,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        return newsRepository.save(news);
    }

    @PutMapping("/news/{id}")
    public News updateNews(
            @PathVariable String id,
            @Valid @RequestBody News news,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tin tức"));
        existing.setTitle(news.getTitle());
        existing.setSummary(news.getSummary());
        existing.setContent(news.getContent());
        existing.setImageUrl(news.getImageUrl());
        existing.setAuthor(news.getAuthor());
        return newsRepository.save(existing);
    }

    @DeleteMapping("/news/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(
            @PathVariable String id,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        newsRepository.deleteById(id);
    }

    // ── APPOINTMENTS ──────────────────────────────────────────────────────

    @GetMapping("/appointments")
    public List<Appointment> appointments(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        requireAuth(token);
        return appointmentRepository.findAll();
    }

    @PatchMapping("/appointments/{id}/status")
    public Appointment updateAppointmentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));
        a.setStatus(body.getOrDefault("status", a.getStatus()));
        return appointmentRepository.save(a);
    }

    @DeleteMapping("/appointments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(
            @PathVariable String id,
            @RequestHeader(value = "X-Admin-Token", required = false) String token
    ) {
        requireAuth(token);
        appointmentRepository.deleteById(id);
    }

}
