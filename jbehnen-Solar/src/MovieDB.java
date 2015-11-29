import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A class that consists of the database operations to insert and update the Movie information.
 * @author mmuppa
 *
 */

public class MovieDB {
	private static String userName = "jbehnen"; //Change to yours
	private static String password = "BefPaf4";
	private static String serverName = "cssgate.insttech.washington.edu";
	private static Connection conn;
	private List<Movie> list;

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

	/**
	 * Returns a list of movie objects from the database.
	 * @return list of movies
	 * @throws SQLException
	 */
	public List<Movie> getMovies() throws SQLException {
		if (conn == null) {
			createConnection();
		}
		Statement stmt = null;
		String query = "select title, year, length, genre, studioName "
				+ "from jbehnen.Movies ";

		list = new ArrayList<Movie>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String title = rs.getString("title");
				int year = rs.getInt("year");
				int length = rs.getInt("length");
				String genre = rs.getString("genre");
				String studioName = rs.getString("studioName");
				Movie movie = new Movie(title, year, length, genre, studioName);
				list.add(movie);
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
	 * Filters the movie list to find the given title. Returns a list with the
	 * movie objects that match the title provided.
	 * @param title
	 * @return list of movies that contain the title.
	 */
	public List<Movie> getMovies(String title) {
		List<Movie> filterList = new ArrayList<Movie>();
		try {
			list = getMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (Movie movie : list) {
			if (movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
				filterList.add(movie);
			}
		}
		return filterList;
	}

	/**
	 * Adds a new movie to the table.
	 * @param movie 
	 */
	public void addMovie(Movie movie) {
		String sql = "insert into jbehnen.Movies values " + "(?, ?, ?, ?, ?, null); ";

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, movie.getTitle());
			preparedStatement.setInt(2, movie.getYear());
			preparedStatement.setInt(3, movie.getLength());
			preparedStatement.setString(4, movie.getGenre());
			preparedStatement.setString(5, movie.getStudioName());
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
	public void updateMovie(int row, String columnName, Object data) {
		
		Movie movie = list.get(row);
		String title = movie.getTitle();
		int year = movie.getYear();
		String sql = "update jbehnen.Movies set " + columnName + " = ?  where title= ? and year = ? ";
		System.out.println(sql);
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			if (data instanceof String)
				preparedStatement.setString(1, (String) data);
			else if (data instanceof Integer)
				preparedStatement.setInt(1, (Integer) data);
			preparedStatement.setString(2, title);
			preparedStatement.setInt(3, year);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		
	}
}
