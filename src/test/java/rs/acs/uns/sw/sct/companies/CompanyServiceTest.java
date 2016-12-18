package rs.acs.uns.sw.sct.companies;

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

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.CompanyConstants.*;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class CompanyServiceTest {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    private Company newCompany;
    private Company updatedCompany;
    private Company existingCompany;

    private void compareCompanies(Company company1, Company company2) {
        if (company1.getId() != null && company2.getId() != null)
            assertThat(company1.getId()).isEqualTo(company2.getId());
        assertThat(company1.getName()).isEqualTo(company2.getName());
        assertThat(company1.getAddress()).isEqualTo(company2.getAddress());
        assertThat(company1.getPhoneNumber()).isEqualTo(company2.getPhoneNumber());
    }


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

    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Company> companies = companyRepository.findAll(pageRequest);
        assertThat(companies).hasSize(PAGE_SIZE);
    }

    @Test
    public void testFindAll() {
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).hasSize(DB_COUNT_COMPANIES);
    }

    @Test
    public void testFindOne() {
        Company company = companyService.findOne(ID);
        assertThat(company).isNotNull();

        compareCompanies(company, existingCompany);
    }

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

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullName() {
        newCompany.setName(null);
        companyService.save(newCompany);
        // rollback previous name
        newCompany.setName(NEW_NAME);
    }


    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullAddress() {
        newCompany.setAddress(null);
        companyService.save(newCompany);
        // rollback previous address
        newCompany.setAddress(NEW_ADDRESS);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullTelephoneNo() {
        newCompany.setPhoneNumber(null);
        companyService.save(newCompany);
        // rollback previous telephone no
        newCompany.setPhoneNumber(NEW_PHONE_NUMBER);
    }


    @Test
    @Transactional
    public void searchCompaniesWithoutAnyQuery() throws Exception {
        final int dbSize = companyRepository.findAll().size();
        final int requiredSize = dbSize < PAGEABLE.getPageSize() ? dbSize : PAGEABLE.getPageSize();

        List<Company> result = companyService.findBySearchTerm(null, null, null, PAGEABLE);
        assertThat(result.size()).isEqualTo(requiredSize);
    }

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
