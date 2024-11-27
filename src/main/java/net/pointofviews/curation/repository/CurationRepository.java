package net.pointofviews.curation.repository;

import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.domain.CurationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("SELECT c FROM Curation c " +
            "WHERE (:theme IS NULL OR c.theme LIKE %:theme%) " +
            "AND (:category IS NULL OR c.category = :category)")
    List<Curation> searchCurations(
            @Param("theme") String theme,
            @Param("category") CurationCategory category
    );


}
