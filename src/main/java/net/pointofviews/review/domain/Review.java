package net.pointofviews.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.SoftDeleteEntity;
import net.pointofviews.member.domain.Member;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    private boolean isSpoiler;

    private boolean disabled;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    private Review(Member member, String title, String contents, boolean isSpoiler) {
        Assert.notNull(title, "Title must not be null");

        this.member = member;
        this.title = title;
        this.contents = contents;
        this.isSpoiler = isSpoiler;
        this.disabled = false;
    }
}
