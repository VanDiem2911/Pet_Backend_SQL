package com.petshop.config;

import com.petshop.model.News;
import com.petshop.model.PetService;
import com.petshop.model.Promotion;
import com.petshop.model.PromotionTier;
import com.petshop.repository.NewsRepository;
import com.petshop.repository.PetServiceRepository;
import com.petshop.repository.PromotionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final NewsRepository newsRepository;
    private final PetServiceRepository petServiceRepository;
    private final PromotionRepository promotionRepository;
    private final boolean seedEnabled;

    public DataSeeder(NewsRepository newsRepository,
                      PetServiceRepository petServiceRepository, PromotionRepository promotionRepository,
                      @Value("${app.seed.enabled}") boolean seedEnabled) {
        this.newsRepository = newsRepository;
        this.petServiceRepository = petServiceRepository;
        this.promotionRepository = promotionRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }



        // Seed news if empty
        if (newsRepository.count() == 0) {
            newsRepository.saveAll(List.of(
                    news("Cẩm nang tắm rửa & vệ sinh cho thú cưng mùa nắng nóng",
                            "Mùa hè thời tiết nóng nực dễ khiến thú cưng bị stress và mắc các bệnh về da. Hãy tham khảo ngay hướng dẫn vệ sinh đúng cách từ chuyên gia.",
                            "Mùa hè thời tiết nóng bức, cơ thể thú cưng dễ bị tích tụ bụi bẩn và mồ hôi. Bạn nên tắm cho thú cưng khoảng 1-2 lần mỗi tuần bằng sữa tắm chuyên dụng phù hợp với độ pH da của chúng. Quan trọng nhất là sau khi tắm xong, cần sấy lông thật khô để tránh các bệnh nấm da, viêm kẽ móng hoặc cảm lạnh.",
                            "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=800&auto=format&fit=crop"),
                    news("Kinh nghiệm gửi trông giữ thú cưng khi đi du lịch dài ngày",
                            "Làm thế nào để người bạn nhỏ luôn cảm thấy thoải mái và an tâm khi xa bạn? Cùng điểm qua các bước chuẩn bị quan trọng trước khi gửi bé.",
                            "Khi gửi thú cưng tại khách sạn thú cưng (boarding), việc đầu tiên cần làm là kiểm tra môi trường nuôi nhốt có sạch sẽ và thông thoáng hay không. Hãy chuẩn bị sẵn thức ăn quen thuộc của bé để tránh bị lạ bụng, ghi chú chi tiết lịch ăn uống và thói quen sinh hoạt cho nhân viên. Đừng quên mang theo món đồ chơi yêu thích của bé để giúp giảm cảm giác nhớ nhà nhé.",
                            "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=800&auto=format&fit=crop"),
                    news("Có nên cạo trọc lông chó mèo vào mùa hè để giải nhiệt?",
                            "Nhiều chủ nuôi cho rằng cạo sạch lông giúp chó mèo mát hơn vào mùa hè. Tuy nhiên đây lại là một quan niệm sai lầm phổ biến.",
                            "Thực tế, lớp lông của chó mèo đóng vai trò như một lớp cách nhiệt tự nhiên, bảo vệ da chúng khỏi tác hại của tia UV và côn trùng. Việc cạo trọc lông không giúp chúng mát hơn mà ngược lại còn làm tăng nguy cơ bị cháy nắng và sốc nhiệt. Thay vì cạo lông, bạn hãy chải lông thường xuyên để loại bỏ lông rụng và tắm rửa vệ sinh định kỳ tại các spa uy tín.",
                            "https://images.unsplash.com/photo-1535268647977-a403b69fc756?w=800&auto=format&fit=crop")
            ));
        }

        if (petServiceRepository.count() == 0) {
            petServiceRepository.saveAll(List.of(
                    service("grooming", "Tắm rửa & Vệ sinh", null, "Tùy loại", "lượt",
                            "Dịch vụ tắm rửa, tỉa lông và vệ sinh chuyên nghiệp.", List.of("Tắm & sấy khô", "Cắt tỉa lông", "Vệ sinh tai", "Cắt móng"),
                            List.of("/pet.jpg", "/pet1.jpg", "/pet2.jpg"), 1),
                    service("boarding", "Trông giữ thú cưng", 250000, null, "ngày",
                            "Không gian lưu trú sạch sẽ, an toàn khi bạn vắng nhà.", List.of("Chuồng riêng rộng rãi", "Cho ăn hàng ngày", "Vận động", "Giám sát 24/7"),
                            List.of("/Pethome.jpg", "/Pethome1.jpg", "/Pethome2.jpg"), 2),
                    service("home_service", "Dịch vụ tại nhà", null, "Tùy loại", "lượt",
                            "Chăm sóc và vệ sinh thú cưng ngay tại nhà.", List.of("Tắm tại chỗ", "Vệ sinh tai và móng", "Tỉa lông", "Phục vụ tận nơi"),
                            List.of("/Home_service.png", "/Home_service1.png", "/Home_service2.png"), 3)
            ));
        }

        if (promotionRepository.count() == 0) {
            Promotion promotion = new Promotion();
            promotion.setTitle("Đặt lịch trước — Giảm 10%");
            promotion.setDescription("Đặt lịch trước ít nhất 24 giờ để nhận ưu đãi trên hóa đơn dịch vụ.");
            promotion.setDiscountPercent(10);
            promotion.setServiceCode("grooming");
            promotion.setPromotionType("PREBOOK");
            promotion.setAdvanceHours(24);
            promotion.setActive(true);
            promotionRepository.save(promotion);

        }

        if (!promotionRepository.existsByServiceCode("boarding")) {
            Promotion boarding = new Promotion();
            boarding.setTitle("Ưu đãi trông giữ thú cưng");
            boarding.setDescription("Gửi trông giữ càng lâu, chiết khấu càng cao.");
            boarding.setDiscountPercent(15);
            boarding.setServiceCode("boarding");
            boarding.setPromotionType("LONG_STAY");
            boarding.setTiers(List.of(tier(1, 3, 0), tier(4, 7, 5), tier(8, 14, 10), tier(15, null, 15)));
            boarding.setActive(true);
            promotionRepository.save(boarding);
        }
    }

    private PromotionTier tier(int minDays, Integer maxDays, int discount) {
        PromotionTier tier = new PromotionTier();
        tier.setMinDays(minDays);
        tier.setMaxDays(maxDays);
        tier.setDiscountPercent(discount);
        return tier;
    }

    private PetService service(String code, String title, Integer price, String priceLabel, String unit,
                               String description, List<String> bullets, List<String> images, int sortOrder) {
        PetService service = new PetService();
        service.setCode(code);
        service.setTitle(title);
        service.setPrice(price == null ? null : BigDecimal.valueOf(price));
        service.setPriceLabel(priceLabel);
        service.setUnit(unit);
        service.setDescription(description);
        service.setBulletPoints(bullets);
        service.setImageUrls(images);
        service.setSortOrder(sortOrder);
        service.setActive(true);
        return service;
    }

    private News news(String title, String summary, String content, String imageUrl) {
        News n = new News();
        n.setTitle(title);
        n.setSummary(summary);
        n.setContent(content);
        n.setImageUrl(imageUrl);
        n.setAuthor("Pet Home Spa");
        return n;
    }


}
