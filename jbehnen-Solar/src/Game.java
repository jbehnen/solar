import java.math.BigDecimal;

/**
 * Represents a game with an ID, title, description, price, genre ID, gameplay type ID,
 * and publisher ID.
 * @author Julia Behnen
 * @version December 2015
 *
 */
public class Game {
	private int id;
	private String title;
	private String description;
	private BigDecimal price;
	private int genreId;
	private int gameplayTypeId;
	private int publisherId;
	
	/**
	 * Initialize the game parameters.
	 * @param id
	 * @param title
	 * @param description
	 * @param price
	 * @param genreId
	 * @param gameplayTypeId
	 * @param publisherId
	 * @throws IllegalArgumentException if title is null or empty, price is null,
	 * id < 0, price < 0.0, genreId < 0, gameplayTypeId < 0, publisherId < 0.
	 */
	public Game(int id, String title, String description, BigDecimal price,
			int genreId, int gameplayTypeId, int publisherId) {
		setId(id);
		setTitle(title);
		setDescription(description);
		setPrice(price);
		setGenreId(genreId);
		setGameplayTypeId(gameplayTypeId);
		setPublisherId(publisherId);
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", title=" + title + ", description=" + description + ", price=" + price
				+ ", genreId=" + genreId + ", gameplayTypeId=" + gameplayTypeId + ", publisherId=" + publisherId + "]";
	}

	
	/**
	 * Returns the ID of the game.
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifies the id of the game.
	 * @param id
	 * @throws IllegalArgumentException if id < 0.
	 */
	public final void setId(int id) {
		if (id < 0) {
			throw new IllegalArgumentException("Please supply a valid game ID (greater than or equal to zero).");
		}
		this.id = id;
	}

	/**
	 * Returns the title of the game.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Modifies the title of the game.
	 * @param title
	 * @throws IllegalArgumentException if title is null or empty,
	 */
	public final void setTitle(String title) {
		if (title == null || title.length() == 0) {
			throw new IllegalArgumentException("Please supply a valid title.");
		}
		this.title = title;
	}

	/**
	 * Returns the description of the game.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifies the description of the game.
	 * @param description
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the price of the game.
	 * @return price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * Modifies the price of the game.
	 * @param price
	 * @throws IllegalArgumentException if price is null or less than 0.0.
	 */
	public final void setPrice(BigDecimal price) {
		if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Please supply a valid price (greater than or equal to zero).");
		}
		this.price = price;
	}

	/**
	 * Returns the genre ID of the game.
	 * @return genreId
	 */
	public int getGenreId() {
		return genreId;
	}

	/**
	 * Modifies the genre ID of the game.
	 * @param genreId
	 * @throws IllegalArgumentException if genreId < 0.
	 */
	public final void setGenreId(int genreId) {
		if (genreId < 0) {
			throw new IllegalArgumentException("Please supply a valid genre ID (greater than or equal to zero)");
		}
		this.genreId = genreId;
	}

	/**
	 * Returns the gameplay type ID of the game.
	 * @return gameplayTypeId
	 */
	public int getGameplayTypeId() {
		return gameplayTypeId;
	}

	/**
	 * Modifies the gameplay type ID of the game.
	 * @param gameplayTypeId
	 * @throws IllegalArgumentException if gameplayTypeId < 0.
	 */
	public final void setGameplayTypeId(int gameplayTypeId) {
		if (gameplayTypeId < 0) {
			throw new IllegalArgumentException("Please supply a valid gameplay type ID (greater than or equal to zero)");
		}
		this.gameplayTypeId = gameplayTypeId;
	}

	/**
	 * Returns the publisher ID of the game.
	 * @return publisherId
	 */
	public int getPublisherId() {
		return publisherId;
	}

	/**
	 * Modifies the publisher ID of the game.
	 * @param publisherId
	 * @throws IllegalArgumentException if publisherId < 0.
	 */
	public final void setPublisherId(int publisherId) {
		if (publisherId < 0) {
			throw new IllegalArgumentException("Please supply a valid publisher ID (greater than or equal to zero)");
		}
		this.publisherId = publisherId;
	}
	
}
