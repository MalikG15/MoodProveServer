package moodprove.data;


import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.User;

/**
 * @author Malik Graham
 */
public interface UserRepository extends JpaRepository<User, Long> {
	User findByuserid(String userId);
	User findByEmailAndPassword(String email, String password);
	String findfacebookAccessTokenByuserid(String userid);
}
