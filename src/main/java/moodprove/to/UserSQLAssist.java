package moodprove.to;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author malikg
 * Created only to execute SQL requests for User authentication
 */
public class UserSQLAssist {
	
	private static final String DELETE_LINK_STATEMENT = "UPDATE mood.User SET googleoauthlink = null WHERE userid = ?";
	private static final String UPDATE_LINK_STATEMENT = "UPDATE mood.User SET googleoauthlink = ? WHERE userid = ?";
	private static final String GET_LINK_STATEMENT = "SELECT googleoauthlink FROM mood.User WHERE userid = ?";
	private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/mood";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "abc";
	private static final String TEST_ID = "9c433741-317a-4489-95d8-87f55d02aa6b";
	
	
	
	public static void deleteLink(String userId) {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
		    PreparedStatement stmt = connection.prepareStatement(DELETE_LINK_STATEMENT);
		    stmt.setString(1, userId);
		    stmt.execute();
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
	
	public static void addLink(String link, String userId) {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
		    PreparedStatement stmt = connection.prepareStatement(UPDATE_LINK_STATEMENT);
		    stmt.setString(1, link);
		    stmt.setString(2, userId);
		    stmt.execute();
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
	
	public static String getLink(String userId) {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
		    PreparedStatement stmt = connection.prepareStatement(GET_LINK_STATEMENT);
		    stmt.setString(1, userId);
		    ResultSet result = stmt.executeQuery();
		    while (result.next()) {
		    	return result.getString(1);
		    }
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		addLink("hello", TEST_ID);
		System.out.println(getLink(TEST_ID));
		deleteLink(TEST_ID);
	}
}
