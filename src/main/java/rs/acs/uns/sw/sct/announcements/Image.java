package rs.acs.uns.sw.sct.announcements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * An image - image path
 */
@Entity
@Table(name = "images")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String imagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id image identifier
     * @return Image (this)
     */
    public Image id(Long id) {
        this.id = id;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param imagePath path to the image (file or URL)
     * @return Image (this)
     */
    public Image imagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }
}
