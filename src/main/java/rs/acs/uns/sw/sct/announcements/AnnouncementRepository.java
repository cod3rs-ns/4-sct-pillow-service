package rs.acs.uns.sw.sct.announcements;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

}
