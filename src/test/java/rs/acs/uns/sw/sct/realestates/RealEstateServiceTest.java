package rs.acs.uns.sw.sct.realestates;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.RealEstateConstants;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.RealEstateConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class RealEstateServiceTest {

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private RealEstateRepository realEstateRepository;

    private RealEstate newRealEstate;
    private RealEstate updatedRealEstate;
    private RealEstate existingRealEstate;

    /**
     * Asserts equality of two RealEstates.
     *
     * @param re1 One of the RealEstates to be compared
     * @param re2 The other RealEstates to be compared
     */
    private void compareRealEstate(RealEstate re1, RealEstate re2) {
        if (re1.getId() != null && re2.getId() != null)
            assertThat(re1.getId()).isEqualTo(re2.getId());
        assertThat(re1.getArea()).isEqualTo(re2.getArea());
        assertThat(re1.getHeatingType()).isEqualTo(re2.getHeatingType());
        assertThat(re1.getName()).isEqualTo(re2.getName());
        assertThat(re1.getType()).isEqualTo(re2.getType());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        existingRealEstate = new RealEstate()
                .id(ID)
                .name(NAME)
                .type(TYPE)
                .area(AREA)
                .heatingType(HEATING_TYPE)
                .deleted(DEFAULT_DELETED);
        newRealEstate = new RealEstate()
                .id(null)
                .name(NEW_NAME)
                .type(NEW_TYPE)
                .area(NEW_AREA)
                .heatingType(NEW_HEATING_TYPE)
                .deleted(DEFAULT_DELETED);
        updatedRealEstate = new RealEstate()
                .id(null)
                .name(UPDATED_NAME)
                .type(UPDATED_TYPE)
                .area(UPDATED_AREA)
                .heatingType(UPDATED_HEATING_TYPE)
                .deleted(DEFAULT_DELETED);
    }

    /**
     * Tests pageable retrieval of RealEstates
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests RealEstates,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<RealEstate> realEstates = realEstateService.findAll(pageRequest);
        assertThat(realEstates).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all RealEstates
     * <p>
     * This test finds all RealEstates on the repository and asserts
     * that the number of returned results is equal to the number of
     * RealEstates on the database
     */
    @Test
    public void testFindAll() {
        List<RealEstate> realEstateList = realEstateRepository.findAll();
        assertThat(realEstateList).hasSize(DB_COUNT_REAL_ESTATES);
    }

    /**
     * Tests retrieval of a single RealEstate.
     * <p>
     * This test uses the id of an RealEstate that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned RealEstate to an existing RealEstate.
     */
    @Test
    public void testFindOne() {
        RealEstate realEstate = realEstateService.findOne(ID);
        assertThat(realEstate).isNotNull();

        compareRealEstate(realEstate, existingRealEstate);
    }

    /**
     * Tests addition of RealEstates
     * <p>
     * This announcement saves a new RealEstate using the RealEstateService,
     * then it finds all RealEstates and asserts that the size of the results
     * has increased by one. It also asserts that the new RealEstate that is on
     * the database equals the RealEstate we added.
     */
    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = realEstateRepository.findAll().size();

        RealEstate dbRealEstate = realEstateService.save(newRealEstate);
        assertThat(dbRealEstate).isNotNull();

        // Validate that new real estate is in the database
        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(dbSizeBeforeAdd + 1);

        compareRealEstate(dbRealEstate, newRealEstate);
    }

    /**
     * Tests updating of RealEstates.
     * <p>
     * This test retrieves a RealEstate using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated RealEstate.
     */
    @Test
    @Transactional
    public void testUpdate() {
        RealEstate dbRealEstate = realEstateService.findOne(ID);

        dbRealEstate.setArea(UPDATED_AREA);
        dbRealEstate.setHeatingType(UPDATED_HEATING_TYPE);
        dbRealEstate.setName(UPDATED_NAME);
        dbRealEstate.setType(UPDATED_TYPE);

        RealEstate updatedDbRealEstate = realEstateService.save(dbRealEstate);
        assertThat(updatedDbRealEstate).isNotNull();

        compareRealEstate(updatedDbRealEstate, updatedRealEstate);
    }

    /**
     * Tests removal of RealEstates
     * <p>
     * This test deletes a RealEstate using the service, then
     * asserts that the number of RealEstates on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted RealEstate's id does not exists on the
     * database.
     */
    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = realEstateRepository.findAll().size();
        realEstateService.delete(REMOVE_ID);

        List<RealEstate> realEstates = realEstateRepository.findAll();
        assertThat(realEstates).hasSize(dbSizeBeforeRemove - 1);

        RealEstate dbRealEstate = realEstateService.findOne(REMOVE_ID);
        assertThat(dbRealEstate).isNull();
    }

    /*
     * Negative tests
	 */

    /**
     * Tests adding a RealEstate with a null name value
     * <p>
     * This test sets a RealEstate's name to null, then
     * attempts to add it to the database. As name is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullName() {
        newRealEstate.setName(null);
        realEstateService.save(newRealEstate);
        // rollback previous name
        newRealEstate.setName(NEW_NAME);
    }

    /**
     * Tests adding a RealEstate with a null type value
     * <p>
     * This test sets a RealEstate's type to null, then
     * attempts to add it to the database. As type is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newRealEstate.setType(null);
        realEstateService.save(newRealEstate);
        // rollback previous type
        newRealEstate.setType(NEW_TYPE);
    }

    /**
     * Tests adding a RealEstate with a null area value
     * <p>
     * This test sets a RealEstate's area to null, then
     * attempts to add it to the database. As area is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullArea() {
        newRealEstate.setArea(null);
        realEstateService.save(newRealEstate);
        // rollback previous area
        newRealEstate.setArea(NEW_AREA);
    }

    /**
     * Tests adding a RealEstate with a null heatingType value
     * <p>
     * This test sets a RealEstate's heatingType to null, then
     * attempts to add it to the database. As heatingType is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullHeatingType() {
        newRealEstate.setHeatingType(null);
        realEstateService.save(newRealEstate);
        // rollback previous heating type
        newRealEstate.setHeatingType(NEW_HEATING_TYPE);
    }

    /**
     * Tests searching for a deleted RealEstates
     * <p>
     * This tests sets an RealEstate's deleted value to true,
     * then saves it to the database. Then it searches the database
     * for deleted RealEstates and asserts that the number of results
     * matches the expected number and that every one of the results
     * has a "deleted" value of true.
     */
    @Test
    public void testFindAllByStatusDeletedTrue() {
        final Boolean status = true;

        final Page<RealEstate> realEstates = realEstateService.findAllByStatus(status, PAGEABLE);

        assertThat(realEstates.getTotalElements()).isEqualTo(DB_COUNT_REAL_ESTATES_DELETED_TRUE);

        for (final RealEstate realEstate: realEstates) {
            assertThat(realEstate.isDeleted()).isEqualTo(status);
        }
    }

    /**
     * Tests searching for a deleted RealEstates
     * <p>
     * This tests sets an RealEstate's deleted value to false,
     * then saves it to the database. Then it searches the database
     * for undeleted RealEstates and asserts that the number of results
     * matches the expected number and that every one of the results
     * has a "deleted" value of false.
     */
    @Test
    public void testFindAllByStatusDeletedFalse() {
        final Boolean status = false;

        final Page<RealEstate> realEstates = realEstateService.findAllByStatus(status, PAGEABLE);

        assertThat(realEstates.getTotalElements()).isEqualTo(DB_COUNT_REAL_ESTATES_DELETED_FALSE);

        for (final RealEstate realEstate: realEstates) {
            assertThat(realEstate.isDeleted()).isEqualTo(status);
        }
    }

    @Test
    public void testFindAllSimilar() {
        Location location = new Location()
                .country(RealEstateConstants.SIMILAR_COUNTRY)
                .city(RealEstateConstants.SIMILAR_CITY)
                .cityRegion(RealEstateConstants.SIMILAR_REGION)
                .street(RealEstateConstants.SIMILAR_STREET)
                .streetNumber(RealEstateConstants.SIMILAR_STREET_NO);

        final RealEstateSimilarDTO similar = new RealEstateSimilarDTO(location, Double.parseDouble(RealEstateConstants.SIMILAR_AREA));

        final Page<RealEstate> realEstates = realEstateService.findAllSimilar(similar, RealEstateConstants.PAGEABLE);

        assertThat(realEstates.getTotalElements()).isEqualTo(DB_COUNT_REAL_ESTATES_SIMILAR_NON_DELETED);

        for (final RealEstate realEstate: realEstates) {
            assertThat(realEstate.similar(similar)).isEqualTo(true);
        }
    }

}
