package moodprove.data;


import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.User;

/**
 * @author Malik Graham
 */
public interface UserRepository extends JpaRepository<User, Long> {

}
