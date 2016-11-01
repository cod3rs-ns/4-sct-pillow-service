package rs.acs.uns.sw.awt_test.marks;


import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;


@Entity
@Table(name = "marks")
@PrimaryKeyJoinColumn(name = "m_id")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "m_id")
    private Long id;

    @Column(name = "m_value")
    private Double value;

    @ManyToOne
    @JoinColumn(name = "m_grader")
    private User grader;

    @ManyToOne
    @JoinColumn(name = "m_graded_announcer")
    private User graded_announcer;

    @ManyToOne
    @JoinColumn(name = "m_announcement")
    private Announcement announcement;

    @Column(name = "re_deleted")
    private Boolean deleted;

    public Mark() {
    }

    public Mark(Double value, User grader, User graded_announcer, Announcement announcement, Boolean deleted) {
        this.value = value;
        this.grader = grader;
        this.graded_announcer = graded_announcer;
        this.announcement = announcement;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public User getGrader() {
        return grader;
    }

    public void setGrader(User grader) {
        this.grader = grader;
    }

    public User getGraded_announcer() {
        return graded_announcer;
    }

    public void setGraded_announcer(User graded_announcer) {
        this.graded_announcer = graded_announcer;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
