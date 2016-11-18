package rs.acs.uns.sw.awt_test.marks;


import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "marks")
@PrimaryKeyJoinColumn(name = "m_id")
public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "m_id")
    private Long id;

    @NotNull
    @Column(name = "m_value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "m_grader")
    private User grader;

    @ManyToOne
    @JoinColumn(name = "m_graded_announcer")
    private User graded_announcer;

    @ManyToOne
    @JoinColumn(name = "m_announcement")
    private Announcement announcement;

    public Mark() {
    }

    public Mark(Long id, Integer value, User grader, User graded_announcer, Announcement announcement) {
        this.id = id;
        this.value = value;
        this.grader = grader;
        this.graded_announcer = graded_announcer;
        this.announcement = announcement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Mark value(Integer value) {
        this.value = value;
        return this;
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

}
