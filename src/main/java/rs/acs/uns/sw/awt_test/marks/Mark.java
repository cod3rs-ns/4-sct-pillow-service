package rs.acs.uns.sw.awt_test.marks;


import rs.acs.uns.sw.awt_test.announcements.Announcement;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "marks")
@PrimaryKeyJoinColumn(name="m_id")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "m_id")
    private Integer id;

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
}
