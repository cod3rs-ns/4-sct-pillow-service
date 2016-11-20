package rs.acs.uns.sw.sct.reports;

import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A report.
 */
@Entity
@Table(name = "reports")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    private String content;

    @NotNull
    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn()
    private User reporter;

    @ManyToOne
    @JoinColumn()
    private Announcement announcement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id report identifier
     * @return Report (this)
     */
    public Report id(Long id) {
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
     * @param email email of the user that posted report
     * @return Report (this)
     */
    public Report email(String email) {
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
     * @param type report type
     * @return Report (this)
     */
    public Report type(String type) {
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
     * @param content report content - reason for report
     * @return Report (this)
     */
    public Report content(String content) {
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
     * @param status report status
     * @return Report (this)
     */
    public Report status(String status) {
        this.status = status;
        return this;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param reporter user that created report
     * @return Report (this)
     */
    public Report reporter(User reporter) {
        this.reporter = reporter;
        return this;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param announcement subject of report
     * @return Report (this)
     */
    public Report announcement(Announcement announcement) {
        this.announcement = announcement;
        return this;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", reporter=" + reporter +
                ", announcement=" + announcement +
                '}';
    }
}