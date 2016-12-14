package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.companies.QCompany;

public final class CompanyPredicates {

    private CompanyPredicates() {
    }

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
