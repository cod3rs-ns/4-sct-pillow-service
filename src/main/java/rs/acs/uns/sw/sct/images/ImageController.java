package rs.acs.uns.sw.sct.images;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rs.acs.uns.sw.sct.util.Constants.FilePaths.IMAGES_URL;


/**
 * REST controller for working with images.
 */

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class ImageController {

    @Value("${sct.file_upload.path}")
    private String uploadPath;


    /**
     * GET  /images/{entity}/{imageName}
     *
     * @param entity    name of entity
     * @param imageName path of image
     * @return the ResponseEntity with status 200 (OK) and the image path if fileExists
     * or with status 404 (OK) if image does not exists
     * @throws IOException if there is an error to read image from path
     */
    @GetMapping("/images/{entity}/{imageName:.+}")
    public ResponseEntity<byte[]> realEstateImage(@PathVariable String imageName, @PathVariable String entity) throws IOException {
        File f = new File(uploadPath + File.separator + entity + File.separator + imageName);
        if (!f.exists()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        InputStream in = new FileInputStream(f);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        byte[] result = IOUtils.toByteArray(in);
        in.close();

        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * POST  /images/:entity : upload file for announcement.
     *
     * @param entity the name of entity
     * @param file   the file to be upload
     * @return the ResponseEntity with status 201 (Created) and with body the new file name,
     * or with status 400 (Bad Request) if the upload failed, or with status 204 (No content)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PostMapping("/images/{entity}")
    public ResponseEntity<String> handleFileUpload(@PathVariable String entity, @RequestParam("file") MultipartFile file) {
        // TODO check if entity is real

        if (!file.isEmpty()) {
            try {
                String originalFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
                String originalFileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
                String newFilename = originalFileName + UUID.randomUUID().toString() + originalFileExtension;

                // transfer to upload folder
                File dir = new File(uploadPath + File.separator + entity + File.separator);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File newFile = new File(dir + File.separator + newFilename);
                file.transferTo(newFile);

                return new ResponseEntity<>(IMAGES_URL + entity + "/" + newFilename, HttpStatus.OK);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Unable to create folders.", e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}