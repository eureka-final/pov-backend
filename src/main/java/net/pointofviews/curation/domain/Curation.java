package net.pointofviews.curation.domain;

import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String theme;

    @Enumerated(EnumType.STRING)
    private CurationCategory category;

    private String title;

    private String description;

    @Builder
    public Curation(Member member, String theme, CurationCategory category, String title, String description) {
        this.member = member;
        this.theme = theme;
        this.category = category;
        this.title = title;
        this.description = description;
    }

    public void updateCuration(String theme, CurationCategory category, String title, String description) {
        this.theme = theme;
        this.category = category;
        this.title = title;
        this.description = description;
    }
}
