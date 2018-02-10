package moodprove.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import moodprove.to.Social;

public interface SocialRepository extends JpaRepository<Social, Long>  {
	@Transactional
    void deleteBysocialid(String socialid);
	
	List<Social> findByday(String day);
}
