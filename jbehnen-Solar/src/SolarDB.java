import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

/**
 * A class that consists of the database operations to insert, update, and get the Game information.
 * @author mmuppa
 *
 */

public class SolarDB {
	private static String userName = "jbehnen"; //Change to yours
	private static String password = "BefPaf4";
	private static String serverName = "cssgate.insttech.washington.edu";
	private static Connection conn;
	private List<Game> gameList;

	/**
	 * Creates a sql connection to MySQL using the properties for
	 * userid, password and server information.
	 * @throws SQLException
	 */
	public static void createConnection() throws SQLException {
		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", password);

		conn = DriverManager.getConnection("jdbc:" + "mysql" + "://"
				+ serverName + "/", connectionProps);

		System.out.println("Connected to database");
	}
	
	public boolean checkUser(String username, String password, boolean isPlayer) {
		String sql;
		if (isPlayer) {
			sql = "SELECT * FROM jbehnen.PlayerAccount WHERE playerUsername = ? AND playerPassword = ?;";
		} else {
			sql = "SELECT * FROM jbehnen.PublisherAccount WHERE publisherName = ? AND publisherPassword = ?;";
		}
		
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			ResultSet rs = preparedStatement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addUser(String username, String password, String email, boolean isPlayer) {
		String sql;
		boolean success = true;
		 if (isPlayer) {
			 sql = "insert into jbehnen.PlayerAccount values (?, ?, ?); ";
		 } else {
			// Insert subquery from http://stackoverflow.com/questions/10644149/insert-into-with-subquery-mysql
			sql = "insert into jbehnen.PublisherAccount"
					+ " SELECT nextId, ? as publisherName, ? as publisherEmail, ? as publisherPasswword"
					+ " FROM (SELECT MAX(publisherId) + 1 'nextId' FROM jbehnen.PublisherAccount) MaximumId; ";
		 }
		 PreparedStatement preparedStatement = null;
		 try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, password);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public Game getGame(int gameId) {
		String sql = "select gameId, gameTitle, gameDescription, gamePrice, genreId, "
				+ "gameplayTypeId, publisherId from jbehnen.Game where gameId = ?";

		Game game = null;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("gameId");
				String title = rs.getString("gameTitle");
				String description = rs.getString("gameDescription");
				BigDecimal gamePrice = rs.getBigDecimal("gamePrice");
				int genreId = rs.getInt("genreId");
				int gameplayTypeId = rs.getInt("gameplayTypeId");
				int publisherId = rs.getInt("genreId");
				game = new Game(id, title, description, gamePrice, genreId,
						gameplayTypeId, publisherId);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return game;
	}

	/**
	 * Returns a list of movie objects from the database.
	 * @return list of movies
	 * @throws SQLException
	 */
	public List<Game> getGames() throws SQLException {
		if (conn == null) {
			createConnection();
		}
		Statement stmt = null;
		String query = "select gameId, gameTitle, gameDescription, gamePrice, genreId, "
				+ "gameplayTypeId, publisherId from jbehnen.Game ";

		gameList = new ArrayList<Game>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				int id = rs.getInt("gameId");
				String title = rs.getString("gameTitle");
				String description = rs.getString("gameDescription");
				BigDecimal gamePrice = rs.getBigDecimal("gamePrice");
				int genreId = rs.getInt("genreId");
				int gameplayTypeId = rs.getInt("gameplayTypeId");
				int publisherId = rs.getInt("genreId");
				Game game = new Game(id, title, description, gamePrice, genreId,
						gameplayTypeId, publisherId);
				gameList.add(game);
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return gameList;
	}
	
	public List<Game> getOwnedGames(String username) {
		String sql = "select gameId, gameTitle, gameDescription, gamePrice, genreId, "
				+ "gameplayTypeId, publisherId from jbehnen.Game where gameId in "
				+ "(SELECT gameId FROM jbehnen.PlayerOwnsGame WHERE playerUsername = ?);";

		gameList = new ArrayList<Game>();
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("gameId");
				String title = rs.getString("gameTitle");
				String description = rs.getString("gameDescription");
				BigDecimal gamePrice = rs.getBigDecimal("gamePrice");
				int genreId = rs.getInt("genreId");
				int gameplayTypeId = rs.getInt("gameplayTypeId");
				int publisherId = rs.getInt("genreId");
				Game game = new Game(id, title, description, gamePrice, genreId,
						gameplayTypeId, publisherId);
				gameList.add(game);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return gameList;
	}

	/**
	 * Filters the movie list to find the given title. Returns a list with the
	 * movie objects that match the title provided.
	 * @param title
	 * @return list of movies that contain the title.
	 */
	public List<Game> getGames(String title) {
		List<Game> filterList = new ArrayList<Game>();
		try {
			gameList = getGames();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (Game game : gameList) {
			if (game.getTitle().toLowerCase().contains(title.toLowerCase())) {
				filterList.add(game);
			}
		}
		return filterList;
	}
	
	public List<String> getGenres() throws SQLException {
		return getIdDescriptorList("Genre", "genreId", "genreName");
	}
	
	public List<String> getGameplayTypes() throws SQLException {
		return getIdDescriptorList("GameplayType", "gameplayTypeId", "gameplayTypeName");
	}
	
	public List<String> getOperatingSystems() throws SQLException {
		return getIdDescriptorList("OperatingSystem", "operatingSystemId", "operatingSystemName");
	}
	
	public List<String> getPlayerGroupingTypes() throws SQLException {
		return getIdDescriptorList("PlayerGroupingType", "playerGroupingTypeId", "playerGroupingTypeName");
	}
	
	public List<String> getPublisherNames() throws SQLException {
		return getIdDescriptorList("PublisherAccount", "publisherId", "publisherName");
	}
	
	private List<String> getIdDescriptorList(String table, String idName, String descriptorName) throws SQLException {
		if (conn == null) {
			createConnection();
		}
		Statement stmt = null;
		String query = "select " + descriptorName + " from jbehnen." + table + " order by " + idName;

		List<String> list = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				list.add(rs.getString(descriptorName));
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return list;
	}

	/**
	 * Adds a new movie to the table.
	 * @param movie 
	 */
	public void addGame(Game game) {
		String sql = "insert into jbehnen.Game values " + "(?, ?, ?, ?, ?, ?, ?); ";

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, game.getId());
			preparedStatement.setString(2, game.getTitle());
			preparedStatement.setString(3, game.getDescription());
			preparedStatement.setBigDecimal(4, game.getPrice());
			preparedStatement.setInt(5, game.getGenreId());
			preparedStatement.setInt(6, game.getGameplayTypeId());
			preparedStatement.setInt(7, game.getPublisherId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
	}

	/**
	 * Modifies the movie information corresponding to the index in the list.
	 * @param row index of the element in the list
	 * @param columnName attribute to modify
	 * @param data value to supply
	 */
	public void updateGame(int row, String columnName, Object data) {
		Game game = gameList.get(row);
		int gameId = game.getId();
		String sql = "update jbehnen.Game set " + columnName + " = ?  where gameId = ? ";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			if (data instanceof String)
				preparedStatement.setString(1, (String) data);
			else if (data instanceof Integer)
				preparedStatement.setInt(1, (Integer) data);
			preparedStatement.setInt(2, gameId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
	}
	
	public boolean buyGame(String username, Game game) {
		String sql = "insert into jbehnen.PlayerOwnsGame values (?, ?, ?, ?, ?)";

		boolean success = true;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, game.getId());
			preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
			preparedStatement.setBigDecimal(4, game.getPrice());
			preparedStatement.setBoolean(5, false);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		} 
		return success;
	}
	
	public boolean userExists(String username) {
		String sql = "select playerUsername from jbehnen.PlayerAccount where playerUsername = ?";

		boolean exists = false;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			exists = rs.next();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		return exists;
	}
	
	public boolean addFriend(String username, String friendUsername) {
		String sql = "insert into jbehnen.Friends values (?, ?)";

		boolean success = true;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, friendUsername);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		} 
		return success;
	}
	
	public boolean removeFriend(String username, String friendUsername) {
		String sql = "delete from jbehnen.Friends where playerUsername = ? AND friendUsername = ?";

		boolean success = true;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, friendUsername);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		} 
		return success;
	}
	
	public List<String> getFriends(String username) {
		String sql = "select friendUsername from jbehnen.Friends where playerUsername = ?";
		List<String> friends = new ArrayList<>();
		
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				friends.add(rs.getString("friendUsername"));
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		return friends;
	}
	
	public List<Transaction> getPlayerTransactions(String username) {
		String sql = "select gameId, purchaseDate, purchasePrice, isDeleted"
				+ " from jbehnen.PlayerOwnsGame where playerUsername = ?";
		List<Transaction> transactions = new ArrayList<>();
		
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("gameId");
				Date date = rs.getDate("purchaseDate");
				BigDecimal price = rs.getBigDecimal("purchasePrice");
				boolean deleted = rs.getBoolean("isDeleted");
				transactions.add(new Transaction(username, id, date, price, deleted));
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		return transactions;
	}
	
}
