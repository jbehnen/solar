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
	
	public boolean checkUser(String username, String password) {
		String sql;
		sql = "SELECT * FROM jbehnen.PlayerAccount WHERE playerUsername = ? AND playerPassword = ?;";
		
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
	
	public boolean addUser(String username, String password, String email) {
		String sql;
		boolean success = true;
		sql = "insert into jbehnen.PlayerAccount values (?, ?, ?); ";
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
		Statement stmt = null;
		String query = "select gameId, gameTitle, gameDescription, gamePrice, genreId, "
				+ "gameplayTypeId, publisherId from jbehnen.Game ";

		gameList = new ArrayList<Game>();
		try {
			if (conn == null) {
				createConnection();
			}
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

	public List<Game> getGameSearchResults(String inputTitle, String inputFriend, 
			int inputGenreId, int inputGameplayId, int inputOsId, int inputGroupingId) {
		StringBuilder sqlBuilder = new StringBuilder("select gameId, "
				+ "gameTitle, gameDescription, gamePrice, genreId, "
				+ "gameplayTypeId, publisherId from jbehnen.Game where ");
		
		boolean anyCondition = false;
		boolean[] conditions = new boolean[6];
		
		if (inputTitle.length() > 0) {
			sqlBuilder.append("gameTitle LIKE ? ");
			conditions[0] = true;
			anyCondition = true;
		}
		
		if (inputFriend.length() > 0) {
			if (anyCondition) sqlBuilder.append("AND ");
			sqlBuilder.append("gameId IN (SELECT gameId FROM "
					+ "jbehnen.PlayerOwnsGame WHERE playerUsername = ?) ");
			conditions[1] = true;
			anyCondition = true;
		}
		
		if (inputGenreId >= 0) {
			if (anyCondition) sqlBuilder.append("AND ");
			sqlBuilder.append("genreId = ? ");
			conditions[2] = true;
			anyCondition = true;
		}
		
		if (inputGameplayId >= 0) {
			if (anyCondition) sqlBuilder.append("AND ");
			sqlBuilder.append("gameplayTypeId = ? ");
			conditions[3] = true;
			anyCondition = true;
		}
		
		if (inputOsId >= 0) {
			if (anyCondition) sqlBuilder.append("AND ");
			sqlBuilder.append("gameId IN (SELECT gameId FROM "
					+ "jbehnen.GameSupportsOS WHERE "
					+ "operatingSystemId = ? ");
			conditions[4] = true;
			anyCondition = true;
		}
		
		if (inputGroupingId >= 0) {
			if (anyCondition) sqlBuilder.append("AND ");
			sqlBuilder.append("gameId IN (SELECT gameId FROM "
					+ "jbehnen.GameSupportsGrouping WHERE "
					+ "playerGroupingTypeId = ? ");
			conditions[5] = true;
			anyCondition = true;
		}
		
		// If no conditions have been added, add ending to statement
		boolean condition = false;
		for (int i = 0; i < conditions.length; i++) {
			condition |= conditions[i];
		}
		if (!condition) {
			sqlBuilder.append("1 = 1");
		}
		
		sqlBuilder.append(";");
		String sql = sqlBuilder.toString();

		gameList = new ArrayList<Game>();
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			int currentBinding = 1;
			if (conditions[0]) {
				preparedStatement.setString(currentBinding++, "%" + inputTitle + "%");
			}
			if (conditions[1]) {
				preparedStatement.setString(currentBinding++, inputFriend);
			}
			if (conditions[2]) {
				preparedStatement.setInt(currentBinding++, inputGenreId);
			}
			if (conditions[3]) {
				preparedStatement.setInt(currentBinding++, inputGameplayId);
			}
			if (conditions[4]) {
				preparedStatement.setInt(currentBinding++, inputOsId);
			}
			if (conditions[5]) {
				preparedStatement.setInt(currentBinding++, inputGroupingId);
			}
			System.out.println(preparedStatement.toString());
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
	
	public List<String> getOperatingSystems(int gameId) {
		String sql = "select operatingSystemName from jbehnen.OperatingSystem "
				+ "NATURAL JOIN jbehnen.GameSupportsOS where gameId = ?";

		List<String> operatingSystems = new ArrayList<String>();
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String os = rs.getString("operatingSystemName");
				operatingSystems.add(os);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return operatingSystems;
	}
	
	public List<String> getPlayerGroupingTypes(int gameId) {
		String sql = "select playerGroupingTypeName from jbehnen.PlayerGroupingType "
				+ "NATURAL JOIN jbehnen.GameSupportsGrouping where gameId = ?";

		List<String> groupingTypes = new ArrayList<String>();
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String type = rs.getString("playerGroupingTypeName");
				groupingTypes.add(type);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return groupingTypes;
	}
	
	public double getAverageRating(int gameId) {
		String sql = "select AVG(gameRating) `avgRating` from jbehnen.PlayerRatesGame "
				+ "where gameId = ?";

		double rating = -1;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				rating = rs.getInt("avgRating");
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return rating;
	}
	
	public int getNumOfRatings(int gameId) {
		String sql = "select COUNT(*) `ratingCount` from jbehnen.PlayerRatesGame "
				+ "where gameId = ?";

		int count = -1;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				count = rs.getInt("ratingCount");
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return count;
	}
	
	public int getUserRating(String username, int gameId) {
		String sql = "select gameRating from jbehnen.PlayerRatesGame "
				+ "where playerUsername = ? AND gameId = ?;";

		int rating = -1;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, gameId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				rating = rs.getInt("gameRating");
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return rating;
	}
	
	public boolean addUserRating(String username, int gameId, int rating) {
		String sql = "insert into jbehnen.PlayerRatesGame values (?, ?, ?);";

		boolean success = true;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, gameId);
			preparedStatement.setInt(3, rating);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public boolean updateUserRating(String username, int gameId, int rating) {
		String sql = "update jbehnen.PlayerRatesGame set gameRating = ? "
				+ "where playerUsername = ? and gameId = ?;";

		boolean success = true;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, rating);
			preparedStatement.setString(2, username);
			preparedStatement.setInt(3, gameId);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public boolean deleteUserRating(String username, int gameId) {
		String sql = "delete from jbehnen.PlayerRatesGame "
				+ "where playerUsername = ? and gameId = ?;";

		boolean success = true;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, gameId);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public boolean updatePassword(String username, String password) {
		String sql = "update jbehnen.PlayerAccount set playerPassword = ? "
				+ "where playerUsername = ?;";

		boolean success = true;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, password);
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	public boolean updateEmail(String username, String email) {
		String sql = "update jbehnen.PlayerAccount set playerEmail = ? "
				+ "where playerUsername = ?;";

		boolean success = true;
		try {
			if (conn == null) {
				createConnection();
			}
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
}
