package rs.acs.uns.sw.sct.marks;


import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A mark - rating.
 */
@Entity
@Table(name = "marks")
public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer value;

    @NotNull
    @ManyToOne(cascade = CascadeType.REFRESH)
    private User grader;

    @ManyToOne
    private User gradedAnnouncer;

    @ManyToOne
    private Announcement announcement;

    /**
     * Converts DTO to Mark entity
     *
     * @return MarkDTO for further use
     */
    public MarkDTO convertToDTO() {
        return new MarkDTO()
                .id(id)
                .value(value)
                .grader(grader.convertToDTO())
                .gradedAnnouncer((gradedAnnouncer != null) ? gradedAnnouncer.convertToDTO() : null)
                .announcement((announcement != null) ? announcement.convertToDTO() : null);
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
     * @param id mark identifier
     * @return Mark (this)
     */
    public Mark id(Long id) {
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
     * @param value mark value
     * @return Mark (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param grader user who posted mark
     * @return Mark (this)
     */
    public Mark grader(User grader) {
        this.grader = grader;
        return this;
    }

    public User getGradedAnnouncer() {
        return gradedAnnouncer;
    }

    public void setGradedAnnouncer(User gradedAnnouncer) {
        this.gradedAnnouncer = gradedAnnouncer;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param gradedAnnouncer user who is graded
     * @return Mark (this)
     */
    public Mark gradedAnnouncer(User gradedAnnouncer) {
        this.gradedAnnouncer = gradedAnnouncer;
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
     * @param announcement announcement which is graded
     * @return Mark (this)
     */
    public Mark announcement(Announcement announcement) {
        this.announcement = announcement;
        return this;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "id=" + id +
                ", value=" + value +
                ", grader=" + grader +
                ", gradedAnnouncer=" + gradedAnnouncer +
                ", announcement=" + announcement +
                '}';
    }
}
