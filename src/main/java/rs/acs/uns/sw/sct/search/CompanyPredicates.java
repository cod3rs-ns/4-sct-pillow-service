package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.companies.QCompany;

/**
 * Allows functionality of searching for company based on provided predicates.
 */
public final class CompanyPredicates {

    private CompanyPredicates() {
    }

    /**
     * Creates Predicate object based on provided parameters.
     *
     * @param name        name of the company
     * @param address     address of the company
     * @param phoneNumber phone number of the company
     * @return Predicate used for searching for companies
     */
    public static Predicate search(String name, String address, String phoneNumber) {
        BooleanBuilder where = new BooleanBuilder();

        QCompany company = QCompany.company;
        if (name != null)
            where.and(company.name.containsIgnoreCase(name));
        if (address != null)
            where.and(company.address.containsIgnoreCase(address));
        if (phoneNumber != null)
            where.and(company.phoneNumber.containsIgnoreCase(phoneNumber));

        return where;
    }
}
