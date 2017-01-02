package rs.acs.uns.sw.sct.comments;

import rs.acs.uns.sw.sct.announcements.AnnouncementDTO;
import rs.acs.uns.sw.sct.users.UserDTO;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Comment Data Transfer Object.
 *
 * @see Comment
 */
public class CommentDTO {

    private Long id;

    @NotNull
    private String content;

    @NotNull
    private Date date;

    private AnnouncementDTO announcement;

    private UserDTO author;

    /**
     * Converts DTO to Comment entity
     *
     * @return announcement for further use
     */
    public Comment convertToComment() {
        return new Comment()
                .id(id)
                .content(content)
                .date(date)
                .announcement(announcement.convertToAnnouncement(announcement.getAuthor().convertToUser()))
                .author((author != null) ? author.convertToUser() : null);

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
     * @param id    Comment id
     * @return CommentDTO (this)
     */
    public CommentDTO id(Long id) {
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
     * @param content    Comment content
     * @return CommentDTO (this)
     */
    public CommentDTO content(String content) {
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
     * @param date    Comment date
     * @return CommentDTO (this)
     */
    public CommentDTO date(Date date) {
        this.date = date;
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
     * @param announcement    Comment announcement
     * @return CommentDTO (this)
     */
    public CommentDTO announcement(AnnouncementDTO announcement) {
        this.announcement = announcement;
        return this;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param author    Comment author
     * @return CommentDTO (this)
     */
    public CommentDTO author(UserDTO author) {
        this.author = author;
        return this;
    }
}
