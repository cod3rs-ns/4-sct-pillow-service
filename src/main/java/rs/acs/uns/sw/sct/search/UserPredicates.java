package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.users.QUser;
import rs.acs.uns.sw.sct.util.Constants;

public final class UserPredicates {

    private UserPredicates() {
    }

    public static Predicate search(String username, String email, String firstName,
                                   String lastName, String phoneNumber, String companyName) {

        BooleanBuilder where = new BooleanBuilder();
        QUser user = QUser.user;
        if (username != null)
            where.and(user.username.containsIgnoreCase(username));
        if (email != null)
            where.and(user.email.containsIgnoreCase(email));
        if (firstName != null)
            where.and(user.firstName.containsIgnoreCase(firstName));
        if (lastName != null)
            where.and(user.lastName.containsIgnoreCase(lastName));
        if (phoneNumber != null)
            where.and(user.phoneNumber.containsIgnoreCase(phoneNumber));
        if (companyName != null)
            where.and(user.company.name.containsIgnoreCase(companyName))
                    .and(user.companyVerified.contains(Constants.CompanyStatus.ACCEPTED));

        where.and(user.deleted.eq(false));

        return where;
    }

}
