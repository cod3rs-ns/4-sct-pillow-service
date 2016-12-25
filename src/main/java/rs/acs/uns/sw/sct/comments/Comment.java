package rs.acs.uns.sw.sct.comments;

import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * A user comment.
 */
@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String content;

    @NotNull
    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn()
    private Announcement announcement;

    @ManyToOne
    @JoinColumn()
    private User author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id comment identifier
     * @return Comment (this)
     */
    public Comment id(Long id) {
        this.id = id;
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
     * @param content comment content
     * @return Comment (this)
     */
    public Comment content(String content) {
        this.content = content;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param date date on which comment is posted
     * @return Comment (this)
     */
    public Comment date(Date date) {
        this.date = date;
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
     * @param announcement announcement on which comment is posted
     * @return Comment (this)
     */
    public Comment announcement(Announcement announcement) {
        this.announcement = announcement;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param author author of the comment
     * @return Comment (this)
     */
    public Comment author(User author) {
        this.author = author;
        return this;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", announcement=" + announcement +
                ", author=" + author +
                '}';
    }
}
