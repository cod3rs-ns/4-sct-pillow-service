package rs.acs.uns.sw.sct.marks;

import rs.acs.uns.sw.sct.announcements.AnnouncementDTO;
import rs.acs.uns.sw.sct.users.UserDTO;

import javax.validation.constraints.NotNull;

/**
 * Mark Data Transfer Object.
 *
 * @see Mark
 */
public class MarkDTO {

    private Long id;

    @NotNull
    private Integer value;

    @NotNull
    private UserDTO grader;

    private UserDTO gradedAnnouncer;

    private AnnouncementDTO announcement;

    /**
     * Converts DTO to Mark entity
     *
     * @return Mark for further use
     */
    public Mark convertToMark() {
        return new Mark()
                .id(id)
                .value(value)
                .grader((grader != null) ? grader.convertToUser() : null)
                .gradedAnnouncer((gradedAnnouncer != null) ? gradedAnnouncer.convertToUser() : null)
                .announcement((announcement != null) ? announcement.convertToAnnouncement() : null);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id    Mark id
     * @return MarkDTO (this)
     */
    public MarkDTO id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param value    Mark value
     * @return MarkDTO (this)
     */
    public MarkDTO value(Integer value) {
        this.value = value;
        return this;
    }

    public UserDTO getGrader() {
        return grader;
    }

    public void setGrader(UserDTO grader) {
        this.grader = grader;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param grader    Mark grader
     * @return MarkDTO (this)
     */
    public MarkDTO grader(UserDTO grader) {
        this.grader = grader;
        return this;
    }

    public UserDTO getGradedAnnouncer() {
        return gradedAnnouncer;
    }

    public void setGradedAnnouncer(UserDTO gradedAnnouncer) {
        this.gradedAnnouncer = gradedAnnouncer;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param gradedAnnouncer    Mark graded
     * @return MarkDTO (this)
     */
    public MarkDTO gradedAnnouncer(UserDTO gradedAnnouncer) {
        this.gradedAnnouncer = gradedAnnouncer;
        return this;
    }

    public AnnouncementDTO getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(AnnouncementDTO announcement) {
        this.announcement = announcement;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param announcement    Mark announcement
     * @return MarkDTO (this)
     */
    public MarkDTO announcement(AnnouncementDTO announcement) {
        this.announcement = announcement;
        return this;
    }
}
