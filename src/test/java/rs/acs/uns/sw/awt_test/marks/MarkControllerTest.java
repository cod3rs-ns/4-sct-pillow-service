package rs.acs.uns.sw.awt_test.marks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;
import rs.acs.uns.sw.awt_test.util.TestUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MarkResource REST controller.
 *
 * @see MarkController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class MarkControllerTest {

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private MarkService markService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMarkMockMvc;

    private Mark mark;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mark createEntity() {
        return new Mark()
                .value(DEFAULT_VALUE);
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MarkController markCtrl = new MarkController();
        ReflectionTestUtils.setField(markCtrl, "markService", markService);
        this.restMarkMockMvc = MockMvcBuilders.standaloneSetup(markCtrl)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        mark = createEntity();
    }

    @Test
    @Transactional
    public void createMark() throws Exception {
        int databaseSizeBeforeCreate = markRepository.findAll().size();

        // Create the Mark

        restMarkMockMvc.perform(post("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mark)))
                .andExpect(status().isCreated());

        // Validate the Mark in the database
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeCreate + 1);
        Mark testMark = marks.get(marks.size() - 1);
        assertThat(testMark.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = markRepository.findAll().size();
        // set the field null
        mark.setValue(null);

        // Create the Mark, which fails.

        restMarkMockMvc.perform(post("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mark)))
                .andExpect(status().isBadRequest());

        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMarks() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get all the marks
        restMarkMockMvc.perform(get("/api/marks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(mark.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void getMark() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", mark.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(mark.getId().intValue()))
                .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingMark() throws Exception {
        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMark() throws Exception {
        // Initialize the database
        markService.save(mark);

        int databaseSizeBeforeUpdate = markRepository.findAll().size();

        // Update the mark
        Mark updatedMark = markRepository.findOne(mark.getId());
        updatedMark
                .value(UPDATED_VALUE);

        restMarkMockMvc.perform(put("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMark)))
                .andExpect(status().isOk());

        // Validate the Mark in the database
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeUpdate);
        Mark testMark = marks.get(marks.size() - 1);
        assertThat(testMark.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteMark() throws Exception {
        // Initialize the database
        markService.save(mark);

        int databaseSizeBeforeDelete = markRepository.findAll().size();

        // Get the mark
        restMarkMockMvc.perform(delete("/api/marks/{id}", mark.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
