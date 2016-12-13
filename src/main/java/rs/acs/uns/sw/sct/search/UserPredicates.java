package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.users.QUser;

public final class UserPredicates {

    private UserPredicates() {
    }

    public static Predicate search(String username, String email, String firstName,
                                   String lastName, String phoneNumber, String companyName) {

        BooleanBuilder where = new BooleanBuilder();
        QUser user = QUser.user;
        if (username != null)
            where.and(user.username.toLowerCase().contains(username.toLowerCase()));
        if (email != null)
            where.and(user.email.toLowerCase().contains(email.toLowerCase()));
        if (firstName != null)
            where.and(user.firstName.toLowerCase().contains(firstName.toLowerCase()));
        if (lastName != null)
            where.and(user.lastName.toLowerCase().contains(lastName.toLowerCase()));
        if (phoneNumber != null)
            where.and(user.phoneNumber.toLowerCase().contains(phoneNumber.toLowerCase()));
        if (companyName != null)
            where.and(user.company.name.toLowerCase().contains(companyName.toLowerCase()));

        return where;
    }

}
