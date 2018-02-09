package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.Social;

public interface SocialRepository extends JpaRepository<Social, Long>  {

}
