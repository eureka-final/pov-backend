package net.pointofviews.premiere.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.premiere.dto.request.PremiereRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Premiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String thumbnail;

    private String eventImage;

    private Integer price;

    private boolean isPaymentRequired;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Builder
    private Premiere(
            String title,
            String thumbnail,
            String eventImage,
            Integer price,
            boolean isPaymentRequired,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.eventImage = eventImage;
        this.price = price;
        this.isPaymentRequired = isPaymentRequired;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updatePremiere(PremiereRequest request) {
        this.title = request.title();
        this.price = request.price();
        this.isPaymentRequired = request.isPaymentRequired();
        this.startAt = request.startAt();
        this.endAt = request.endAt();
    }

    public void updateEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
