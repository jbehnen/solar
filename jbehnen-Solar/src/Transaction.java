import java.math.BigDecimal;
import java.sql.Date;

public class Transaction {
	private final String username;
	private final int gameId;
	private final Date purchaseDate;
	private final BigDecimal purchasePrice;
	private final boolean isDeleted;
	
	public Transaction(String username, int gameId, Date purchaseDate, BigDecimal purchasePrice, boolean isDeleted) {
		super();
		this.username = username;
		this.gameId = gameId;
		this.purchaseDate = new Date(purchaseDate.getTime());
		this.purchasePrice = purchasePrice;
		this.isDeleted = isDeleted;
	}

	public String getUsername() {
		return username;
	}

	public int getGameId() {
		return gameId;
	}
	
	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	@Override
	public String toString() {
		return "GameTransaction [username=" + username + ", gameId=" + gameId + ", purchaseDate=" + purchaseDate
				+ ", puchasePrice=" + purchasePrice + ", isDeleted=" + isDeleted + "]";
	}
	
}
