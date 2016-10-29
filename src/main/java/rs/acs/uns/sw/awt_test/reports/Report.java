package rs.acs.uns.sw.awt_test.reports;

import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;


@Entity
@Table(name = "reports")
@PrimaryKeyJoinColumn(name="rep_id")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rep_id")
    private Integer id;

    @Column(name = "rep_email")
    private String email;

    @Column(name = "rep_type")
    private String type;

    @Column(name = "rep_content")
    private String content;

    @Column(name = "rep_status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "rep_reporter")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "rep_announcement")
    private Announcement announcement;

    @Column(name = "rep_deleted")
    private Boolean deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }
}