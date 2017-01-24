package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.users.QUser;
import rs.acs.uns.sw.sct.util.Constants;

/**
 * Allows functionality of searching for users based on provided predicates.
 */
public final class UserPredicates {

    private UserPredicates() {
    }

    /**
     * Creates Predicate object based on provided parameters.
     *
     * @param username    user's unique username
     * @param email       email of the user
     * @param firstName   first name of the user
     * @param lastName    last name of the user
     * @param phoneNumber phone number of the user
     * @param companyName company name which member user is
     * @return Predicate object used for searching for user
     */
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


    /**
     * Creates Predicate object based on provided parameters.
     *
     * @param firstName first name of the user
     * @param lastName  last name of the user
     * @return Predicate object used for searching for user
     */
    public static Predicate searchOR(String firstName, String lastName) {

        BooleanBuilder where = new BooleanBuilder();
        QUser user = QUser.user;
        if (firstName != null && !"".equals(firstName) && (lastName == null || "".equals(lastName))) {
            where.or(user.firstName.containsIgnoreCase(firstName));
            where.or(user.lastName.containsIgnoreCase(firstName));
        } else {
            where.or(user.firstName.containsIgnoreCase(firstName).and(user.lastName.containsIgnoreCase(lastName)));
            where.or(user.firstName.containsIgnoreCase(lastName).and(user.lastName.containsIgnoreCase(firstName)));
        }

        where.and(user.companyVerified.isNull().or(user.companyVerified.ne(Constants.CompanyStatus.ACCEPTED)));
        where.and(user.deleted.eq(false));

        return where;
    }
}
