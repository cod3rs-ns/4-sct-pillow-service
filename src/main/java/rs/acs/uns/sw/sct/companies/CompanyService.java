package rs.acs.uns.sw.sct.companies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
}
