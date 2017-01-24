package rs.acs.uns.sw.sct.reports;

import rs.acs.uns.sw.sct.announcements.AnnouncementDTO;
import rs.acs.uns.sw.sct.users.UserDTO;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Comment Data Transfer Object.
 *
 * @see Report
 */
public class ReportDTO {

    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String type;

    @NotNull
    private String content;

    @NotNull
    private String status;

    @NotNull
    private Date createdAt;

    private UserDTO reporter;

    @NotNull
    private AnnouncementDTO announcement;

    /**
     * Converts DTO to Report entity
     *
     * @return report for further use
     */
    public Report convertToReport() {
        return new Report()
                .id(id)
                .email(email)
                .type(type)
                .content(content)
                .status(status)
                .reporter((reporter != null) ? reporter.convertToUser() : null)
                .announcement(announcement.convertToAnnouncement())
                .createdAt(createdAt);
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
     * @param id Report id
     * @return ReportDTO (this)
     */
    public ReportDTO id(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param email Report email
     * @return ReportDTO (this)
     */
    public ReportDTO email(String email) {
        this.email = email;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param type Report type
     * @return ReportDTO (this)
     */
    public ReportDTO type(String type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param content Report content
     * @return ReportDTO (this)
     */
    public ReportDTO content(String content) {
        this.content = content;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param status Report status
     * @return ReportDTO (this)
     */
    public ReportDTO status(String status) {
        this.status = status;
        return this;
    }

    public UserDTO getReporter() {
        return reporter;
    }

    public void setReporter(UserDTO reporter) {
        this.reporter = reporter;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param reporter Report author
     * @return ReportDTO (this)
     */
    public ReportDTO reporter(UserDTO reporter) {
        this.reporter = reporter;
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
     * @param announcement Report announcement
     * @return ReportDTO (this)
     */
    public ReportDTO announcement(AnnouncementDTO announcement) {
        this.announcement = announcement;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param createdAt date when report is created
     * @return ReportDTO (this)
     */
    public ReportDTO createdAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
