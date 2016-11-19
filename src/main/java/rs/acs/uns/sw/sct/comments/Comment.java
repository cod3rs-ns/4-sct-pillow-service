package rs.acs.uns.sw.sct.comments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "co_id")
    private Long id;

    @NotNull
    @Column(name = "co_content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "co_date", nullable = false)
    private Date date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "co_announcement")
    private Announcement announcement;

    @ManyToOne
    @JoinColumn(name = "co_author")
    private User author;

    public Comment() {
    }

    public Comment(Long id, String content, Date date, Announcement announcement, User author) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.announcement = announcement;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
