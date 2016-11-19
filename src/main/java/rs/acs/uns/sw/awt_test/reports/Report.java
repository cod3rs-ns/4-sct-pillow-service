package rs.acs.uns.sw.awt_test.reports;

import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "reports")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rep_id")
    private Long id;

    @NotNull
    @Column(name = "rep_email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "rep_type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "rep_content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "rep_status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "rep_reporter")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "rep_announcement")
    private Announcement announcement;

    public Report() {
    }

    public Report(Long id, String email, String type, String content, String status, User reporter, Announcement announcement) {
        this.id = id;
        this.email = email;
        this.type = type;
        this.content = content;
        this.status = status;
        this.reporter = reporter;
        this.announcement = announcement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Report content(String content) {
        this.content = content;
        return this;
    }

    public Report status(String status) {
        this.status = status;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
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