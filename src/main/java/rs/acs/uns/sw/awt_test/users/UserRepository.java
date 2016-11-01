package rs.acs.uns.sw.awt_test.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findOneByEmail(String email);

}
