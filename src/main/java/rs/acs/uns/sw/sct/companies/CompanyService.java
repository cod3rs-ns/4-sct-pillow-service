package rs.acs.uns.sw.sct.companies;

import com.querydsl.core.types.Predicate;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static rs.acs.uns.sw.sct.search.CompanyPredicates.search;


/**
 * Service Implementation for managing Company.
 */
@Service
@Transactional
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Save a company.
     *
     * @param company the entity to save
     * @return the persisted entity
     */
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Company findOne(Long id) {
        return companyRepository.findOne(id);
    }

    /**
     * Delete the  company by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        companyRepository.delete(id);
    }

    /**
     * Find all companies that satisfied criteria defined by query params.
     *
     * @param name company name
     * @param address company address
     * @param phoneNumber company phone numver
     */
    @Transactional(readOnly = true)
    public List<Company> findBySearchTerm(String name, String address, String phoneNumber) {
        Predicate searchPredicate = search(name, address, phoneNumber);
        Iterable<Company> searchResults = companyRepository.findAll(searchPredicate);
        return IteratorUtils.toList(searchResults.iterator());
    }
}
