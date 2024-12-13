package net.pointofviews.premiere.repository;

import net.pointofviews.premiere.domain.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Long countEntriesByPremiereId(Long premiereId);

    boolean existsEntryByMemberIdAndPremiereId(UUID memberId, Long premiereId);

    Optional<Entry> findEntryByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}
