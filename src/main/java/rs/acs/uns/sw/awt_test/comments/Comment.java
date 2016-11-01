package rs.acs.uns.sw.awt_test.comments;

import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "comments")
@PrimaryKeyJoinColumn(name = "co_id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "co_id")
    private Long id;

    @Column(name = "co_content")
    private String content;

    @Column(name = "co_date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "co_announcement")
    private Announcement announcement;

    @ManyToOne
    @JoinColumn(name = "co_author")
    private User author;

    public Comment() {
    }

    public Comment(String content, Date date, Announcement announcement, User author) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
