package rs.acs.uns.sw.sct.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.acs.uns.sw.sct.security.UserSecurityUtil;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.Constants;
import rs.acs.uns.sw.sct.util.HeaderUtil;
import rs.acs.uns.sw.sct.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Comment.
 */
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserSecurityUtil userSecurityUtil;

    /**
     * POST  /comments : Create a new comment.
     *
     * @param comment the comment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new comment,
     * or with status 400 (Bad Request) if the comment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/comments")
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        if (comment.getId() != null) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMMENT,
                            HeaderUtil.ERROR_CODE_CUSTOM_ID,
                            HeaderUtil.ERROR_MSG_CUSTOM_ID))
                    .body(null);
        }

        final User user = userSecurityUtil.getLoggedUser();
        comment.setAuthor(user);

        Comment result = commentService.save(comment);
        return ResponseEntity.created(new URI("/api/comments/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(
                        Constants.EntityNames.COMMENT,
                        result.getId().toString()))
                .body(result.convertToDto());
    }

    /**
     * PUT  /comments : Updates an existing comment.
     *
     * @param comment the comment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated comment,
     * or with status 400 (Bad Request) if the comment is not valid,
     * or with status 500 (Internal Server Error) if the comment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @PutMapping("/comments")
    public ResponseEntity<CommentDTO> updateComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        if (comment.getId() == null) {
            return createComment(comment);
        }
        // check if user has no rights to update comment
        if (!userSecurityUtil.checkAuthType(AuthorityRoles.ADMIN) &&
                !commentService.findOne(comment.getId()).getAuthor().getUsername()
                        .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMMENT,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }


        Comment result = commentService.save(comment);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(
                        Constants.EntityNames.COMMENT,
                        comment.getId().toString()))
                .body(result.convertToDto());
    }


    /**
     * GET  /comments : get all the comments.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of comments in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("hasAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN)")
    @GetMapping("/comments")
    public ResponseEntity<List<CommentDTO>> getAllComments(Pageable pageable) throws URISyntaxException {
        // TODO 1 - this method should not be allowed for anyone
        Page<CommentDTO> page = commentService.findAll(pageable)
                .map(comment -> comment.convertToDto());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/comments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /comments/:id : get the "id" comment.
     *
     * @param id the id of the comment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the comment, or with status 404 (Not Found)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        Comment comment = commentService.findOne(id);
        return Optional.ofNullable(comment)
                .map(result -> new ResponseEntity<>(
                        result.convertToDto(),
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /comments/announcement/:announcementId : get all comments for one announcement.
     *
     * @param announcementId the id of the announcement
     * @param pageable       the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of comments in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/comments/announcement/{announcementId}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByAnnouncementId(@PathVariable Long announcementId, Pageable pageable)
            throws URISyntaxException {

        Page<CommentDTO> page = commentService.findAllByAnnouncement(announcementId, pageable)
                .map(comment -> comment.convertToDto());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/comments/announcement");

        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /comments/:id : delete the "id" comment.
     *
     * @param id the id of the comment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasAnyAuthority(T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADMIN, T(rs.acs.uns.sw.sct.util.AuthorityRoles).ADVERTISER, T(rs.acs.uns.sw.sct.util.AuthorityRoles).VERIFIER)")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        // check if user has no rights to update
        if (!userSecurityUtil.checkAuthType(AuthorityRoles.ADMIN) &&
                !commentService.findOne(id).getAuthor().getUsername()
                        .equals(userSecurityUtil.getLoggedUserUsername())) {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtil.failure(
                            Constants.EntityNames.COMMENT,
                            HeaderUtil.ERROR_CODE_NOT_OWNER,
                            HeaderUtil.ERROR_MSG_NOT_OWNER))
                    .body(null);
        }

        commentService.delete(id);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityDeletionAlert(
                        Constants.EntityNames.COMMENT,
                        id.toString()))
                .build();
    }

}
