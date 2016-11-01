package rs.acs.uns.sw.awt_test.announcements;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

}
