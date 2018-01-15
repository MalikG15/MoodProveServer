package moodprove.to;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Malik Graham
 */
@Entity
@Table(name = "User")
public class User {
	
	  @Id
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  @GeneratedValue(generator = "uuid")
	  @Column(name = "userid")
	  private String userid;
	  @Column(name = "username")
	  private String username;
	  @Column(name = "password")
	  private String password;
	  @Column(name = "facebookaccesstoken")
	  private String facebookAccessToken;
	  @Column(name = "googleaccesstoken")
	  private String googleAccessToken;
	  

}
