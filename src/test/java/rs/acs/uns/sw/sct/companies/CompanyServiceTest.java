package rs.acs.uns.sw.sct.companies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.CompanyConstants.*;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class CompanyServiceTest {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    private Company newCompany;
    private Company updatedCompany;
    private Company existingCompany;

    /**
     * Asserts equality of two Comments.
     *
     * @param company1 One of the Comments to be compared
     * @param company2 The other Comments to be compared
     */
    private void compareCompanies(Company company1, Company company2) {
        if (company1.getId() != null && company2.getId() != null)
            assertThat(company1.getId()).isEqualTo(company2.getId());
        assertThat(company1.getName()).isEqualTo(company2.getName());
        assertThat(company1.getAddress()).isEqualTo(company2.getAddress());
        assertThat(company1.getPhoneNumber()).isEqualTo(company2.getPhoneNumber());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        existingCompany = new Company()
                .id(ID)
                .name(NAME)
                .address(ADDRESS)
                .phoneNumber(PHONE_NUMBER);
        newCompany = new Company()
                .id(null)
                .name(NEW_NAME)
                .address(NEW_ADDRESS)
                .phoneNumber(NEW_PHONE_NUMBER);
        updatedCompany = new Company()
                .id(null)
                .name(UPDATED_NAME)
                .address(UPDATED_ADDRESS)
                .phoneNumber(UPDATED_PHONE_NUMBER);
    }

    /**
     * Tests pageable retrieval of Companies
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests Companies,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Company> companies = companyRepository.findAll(pageRequest);
        assertThat(companies).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all Companies
     * <p>
     * This test finds all Companies on the repository and asserts
     * that the number of returned results is equal to the number of
     * Companies on the database
     */
    @Test
    public void testFindAll() {
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(DB_COUNT_COMPANIES);
    }

    /**
     * Tests retrieval of a single Company.
     * <p>
     * This test uses the id of an Company that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned Company to an existing Company.
     */
    @Test
    public void testFindOne() {
        Company company = companyService.findOne(ID);
        assertThat(company).isNotNull();

        compareCompanies(company, existingCompany);
    }

    /**
     * Tests addition of Companies
     * <p>
     * This announcement saves a new Company using the CompanyService,
     * then it finds all Companies and asserts that the size of the results
     * has increased by one. It also asserts that the new Company that is on
     * the database equals the Company we added.
     */
    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = companyRepository.findAll().size();

        Company dbComment = companyService.save(newCompany);
        assertThat(dbComment).isNotNull();

        // Validate that new company is in the database
        List<Company> comments = companyRepository.findAll();
        assertThat(comments).hasSize(dbSizeBeforeAdd + 1);

        compareCompanies(dbComment, newCompany);
    }

    /**
     * Tests updating of Companies.
     * <p>
     * This test retrieves a Company using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated Company.
     */
    @Test
    @Transactional
    public void testUpdate() {
        Company dbCompany = companyService.findOne(ID);

        dbCompany.setName(UPDATED_NAME);
        dbCompany.setAddress(UPDATED_ADDRESS);
        dbCompany.setPhoneNumber(UPDATED_PHONE_NUMBER);

        Company updatedDbCompany = companyService.save(dbCompany);
        assertThat(updatedDbCompany).isNotNull();

        compareCompanies(updatedDbCompany, updatedCompany);
    }

    /**
     * Tests removal of Company
     * <p>
     * This test deletes a Company using the service, then
     * asserts that the number of Companies on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted Company's id does not exists on the
     * database.
     */
    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = companyRepository.findAll().size();
        companyService.delete(REMOVE_ID);

        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(dbSizeBeforeRemove - 1);

        Company dbCompany = companyService.findOne(REMOVE_ID);
        assertThat(dbCompany).isNull();
    }


    /*
     * Negative tests
	 */

    /**
     * Tests adding a Company with a null name value
     * <p>
     * This test sets a Company's name to null, then
     * attempts to add it to the database. As name is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullName() {
        newCompany.setName(null);
        companyService.save(newCompany);
        // rollback previous name
        newCompany.setName(NEW_NAME);
    }

    /**
     * Tests adding a Company with a null address value
     * <p>
     * This test sets a Company's address to null, then
     * attempts to add it to the database. As address is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullAddress() {
        newCompany.setAddress(null);
        companyService.save(newCompany);
        // rollback previous address
        newCompany.setAddress(NEW_ADDRESS);
    }

    /**
     * Tests adding a Company with a null telephone number value
     * <p>
     * This test sets a Company's telephone number to null, then
     * attempts to add it to the database. As telephone number is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullTelephoneNo() {
        newCompany.setPhoneNumber(null);
        companyService.save(newCompany);
        // rollback previous telephone no
        newCompany.setPhoneNumber(NEW_PHONE_NUMBER);
    }

    /**
     * Tests search using no arguments
     * <p>
     * This test searches for Companies using no arguments. It then asserts that
     * the number of returned results is equal to the number of Companies on the
     * database or the number of results per page, whichever is smaller.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchCompaniesWithoutAnyQuery() throws Exception {
        final int dbSize = companyRepository.findAll().size();
        final int requiredSize = dbSize < PAGEABLE.getPageSize() ? dbSize : PAGEABLE.getPageSize();

        List<Company> result = companyService.findBySearchTerm(null, null, null, PAGEABLE);
        assertThat(result.size()).isEqualTo(requiredSize);
    }

    /**
     * Tests search using arguments
     * <p>
     * This test takes random substrings of a Company's name, address and
     * phone number and uses them as arguments to perform a search.
     * It then asserts that all of the returned results have
     * values which contain these substrings.
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchCompaniesByNameAndAddressAndPhoneNumber() throws Exception {
        // prepare db data
        companyRepository.save(newCompany);

        final String randomName = getRandomCaseInsensitiveSubstring(newCompany.getName());
        final String randomAddress = getRandomCaseInsensitiveSubstring(newCompany.getAddress());
        final String randomPhoneNumber = getRandomCaseInsensitiveSubstring(newCompany.getPhoneNumber());

        List<Company> result = companyService.findBySearchTerm(randomName, randomAddress, randomPhoneNumber, PAGEABLE);

        for (Company company : result) {
            assertThat(company.getName()).containsIgnoringCase(randomName);
            assertThat(company.getAddress()).containsIgnoringCase(randomAddress);
            assertThat(company.getPhoneNumber()).containsIgnoringCase(randomPhoneNumber);
        }
    }
}
