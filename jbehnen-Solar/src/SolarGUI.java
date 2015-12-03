import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * A user interface to view the movies, add a new movie and to update an existing movie.
 * The list is a table with all the movie information in it. The TableModelListener listens to
 * any changes to the cells to modify the values for reach movie.
 * @author mmuppa
 *
 */
public class SolarGUI extends JFrame implements ActionListener //, TableModelListener
{
	
	private static final long serialVersionUID = 1779520078061383929L;
	
	private static final Format integerFormat = NumberFormat.getIntegerInstance();
	private static final Format currencyFormat = NumberFormat.getCurrencyInstance();
	
	private JButton btnMyGames, btnAllGames, btnSearch, btnAdd, btnFriends, btnPurchaseHistory, btnAccount, btnSignOut;
	private JPanel pnlLogin, pnlButtons, pnlContent;
	private SolarDB db;
	private List<Game> personalGameList, generalGameList;
	private List<String> genreList;
	private List<String> gameplayTypeList;
	private List<String> osList;
	private List<String> playerGroupingList;
	private List<String> publisherNameList;
	private List<String> friendsList;
	private String[] allGamesColumnNames = {
			"Own",
			"ID",
			"Title",
            "Price",
            "Genre",
            "Gameplay Type",
            "Publisher"};
	private String[] myGamesColumnNames = {
			"ID",
			"Title",
            "Genre",
            "Gameplay Type",
            "Publisher",
            "My Rating"};
	private String[] friendColumnNames = {
			"Friend"};
	private String[] transactionColumnNames = {
			"Player",
			"Game ID",
			"Game Title",
			"Price",
			"Date",
			"Publisher"};
	
	private Object[][] data;
	private JTable table;
	private JScrollPane scrollPane;
	
	private JLabel[] loginLabel = new JLabel[3];
	private JTextField[] loginField = new JTextField[3];
	private JButton btnLogin;
	private JButton btnRegister;
	
	private JPanel pnlAllGamesBtns;
	private JTextField txtAllGamesGameId;
	private JButton btnAllGamesView, btnAllGamesBuy;
	
	private JPanel pnlSearch;
	private JLabel lblTitle;;
	private JTextField txfTitle;
	private JButton btnSearchAction;
	private JComboBox<String> boxSearchFriend;
	private JComboBox<String> boxSearchGenre;
	private JComboBox<String> boxSearchGameplay;
	private JComboBox<String> boxSearchOs;
	private JComboBox<String> boxSearchGrouping;
	
	private JPanel pnlMyGamesBtns;
	private JTextField txtMyGamesGameId;
	private JButton btnMyGamesView, btnMyGamesRate, btnMyGamesPlay;
	private JComboBox<String> boxMyGamesRating;
	
	private JPanel pnlFriendsBtns;
	private JTextField txtFriendsName;
	private JButton btnFriendsAddFriend, btnFriendsRemoveFriend;
	
	private JDialog gameDialog;
	private JLabel[] lblGameViewContent;
	
	private JPanel pnlAdd;
	private JLabel[] txfLabel = new JLabel[7];
	private JTextField[] txfField = new JTextField[7];

	private String username;
	
	/**
	 * Creates the frame and components and launches the GUI.
	 */
	public SolarGUI() {
		super("Solar");
		
		db = new SolarDB();
		updateGeneralData();
		
		// TODO revert
		// logIn();
		
		username = "user";
		createComponents();
		
		setVisible(true);
		setSize(750, 600);
		setResizable(false);
	}
	
	private void logIn() {
		if (pnlLogin == null) {
			pnlLogin = new JPanel();
			pnlLogin.setLayout(new BoxLayout(pnlLogin, BoxLayout.Y_AXIS));
			
			String labelNames[] = {"Username: ", "Password: ", "Email (registration only): "};
			for (int i=0; i<labelNames.length; i++) {
				JPanel panel = new JPanel();
				loginLabel[i] = new JLabel(labelNames[i]);
				loginField[i] = new JTextField(25);
				panel.add(loginLabel[i]);
				panel.add(loginField[i]);
				pnlLogin.add(panel);
			}
			
			JPanel loginButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			btnLogin = new JButton("Log in");
			btnRegister = new JButton("Register");
			btnLogin.addActionListener(this);
			btnRegister.addActionListener(this);
			loginButtonPanel.add(btnLogin);
			loginButtonPanel.add(btnRegister);
			pnlLogin.add(loginButtonPanel);
		}
		getContentPane().removeAll();
		add(pnlLogin, BorderLayout.CENTER);
		validate();
		repaint();
	}
    
	/**
	 * Creates panels for Movie list, search, add and adds the corresponding 
	 * components to each panel.
	 */
	private void createComponents() {
		pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		btnAllGames = new JButton("Game List");
		btnAllGames.addActionListener(this);
		
		btnSearch = new JButton("Game Search");
		btnSearch.addActionListener(this);
		
		btnMyGames = new JButton("My Games");
		btnMyGames.addActionListener(this);
		
		btnFriends = new JButton("Friends");
		btnFriends.addActionListener(this);
		
		btnPurchaseHistory = new JButton("Purchase History");
		btnPurchaseHistory.addActionListener(this);
		
		btnAccount = new JButton("Account");
		btnAccount.addActionListener(this);
		
		btnSignOut = new JButton("Log Out");
		btnSignOut.addActionListener(this);
		
		pnlButtons.add(btnAllGames);
		pnlButtons.add(btnSearch);
		pnlButtons.add(btnMyGames);
		pnlButtons.add(btnFriends);
		pnlButtons.add(btnPurchaseHistory);
		pnlButtons.add(btnAccount);
		pnlButtons.add(btnSignOut);

		//All Games Panel
		pnlAllGamesBtns = new JPanel();
		JLabel lblAllGamesGameId = new JLabel("Game ID: ");
		txtAllGamesGameId = new JFormattedTextField(integerFormat);
		txtAllGamesGameId.setColumns(10);
		btnAllGamesView = new JButton("View");
		btnAllGamesView.addActionListener(this);
		btnAllGamesBuy = new JButton("Buy");
		btnAllGamesBuy.addActionListener(this);
		
		pnlAllGamesBtns.add(lblAllGamesGameId);
		pnlAllGamesBtns.add(txtAllGamesGameId);
		pnlAllGamesBtns.add(btnAllGamesView);
		pnlAllGamesBtns.add(btnAllGamesBuy);
		
		//Search Panel
		pnlSearch = new JPanel();
		JPanel pnlSearchInternal = new JPanel();
		pnlSearchInternal.setLayout(new BoxLayout(pnlSearchInternal, BoxLayout.Y_AXIS));
		pnlSearch.add(pnlSearchInternal);
		lblTitle = new JLabel("Enter Title: ");
		txfTitle = new JTextField(25);
		btnSearchAction = new JButton("Search");
		btnSearchAction.addActionListener(this);
		boxSearchFriend = new JComboBox<>();
		boxSearchGenre = new JComboBox<>();
		boxSearchGameplay = new JComboBox<>();
		boxSearchOs = new JComboBox<>();
		boxSearchGrouping = new JComboBox<>();
		
		pnlSearchInternal.add(lblTitle);
		pnlSearchInternal.add(txfTitle);
		pnlSearchInternal.add(btnSearchAction);
		pnlSearchInternal.add(boxSearchFriend);
		pnlSearchInternal.add(boxSearchGenre);
		pnlSearchInternal.add(boxSearchGameplay);
		pnlSearchInternal.add(boxSearchOs);
		pnlSearchInternal.add(boxSearchGrouping);
		
		//My Games Panel
		pnlMyGamesBtns = new JPanel();
		JLabel lblMyGamesGameId = new JLabel("Game ID: ");
		txtMyGamesGameId = new JFormattedTextField(integerFormat);
		txtMyGamesGameId.setColumns(10);
		btnMyGamesPlay = new JButton("Play");
		btnMyGamesPlay.addActionListener(this);
		btnMyGamesView = new JButton("View");
		btnMyGamesView.addActionListener(this);
		btnMyGamesRate = new JButton("Rate: ");
		btnMyGamesRate.addActionListener(this);
		boxMyGamesRating = new JComboBox<>();
		boxMyGamesRating.addItem("");
		for (int i = 0; i <= 5; i++) {
			boxMyGamesRating.addItem(Integer.toString(i));
		}
		
		pnlMyGamesBtns.add(lblMyGamesGameId);
		pnlMyGamesBtns.add(txtMyGamesGameId);
		pnlMyGamesBtns.add(btnMyGamesPlay);
		pnlMyGamesBtns.add(btnMyGamesView);
		pnlMyGamesBtns.add(btnMyGamesRate);
		pnlMyGamesBtns.add(boxMyGamesRating);

		//Friends Panel
		pnlFriendsBtns = new JPanel();
		txtFriendsName = new JTextField(20);
		btnFriendsAddFriend = new JButton("Add Friend");
		btnFriendsAddFriend.addActionListener(this);
		btnFriendsRemoveFriend = new JButton("Remove Friend");
		btnFriendsRemoveFriend.addActionListener(this);
		
		pnlFriendsBtns.add(txtFriendsName);
		pnlFriendsBtns.add(btnFriendsAddFriend);
		pnlFriendsBtns.add(btnFriendsRemoveFriend);
		
		// Game Overview Dialog
		gameDialog = new JDialog(this);
		Container gameDialogContent = gameDialog.getContentPane();
		gameDialogContent.setLayout(new BoxLayout(gameDialogContent, BoxLayout.Y_AXIS));
		
		String gameViewLabelNames[] = {"Game ID: ", "Title: ", "Description: ", 
				"Average Rating: ", "Price: ", "Genre: ", "Gameplay Type: ", 
				"Publisher: ", "Operating Systems: ", "Number of Players: "};
		Font labelFont = new Font("Dialog", Font.BOLD, 12);
		Font descriptionFont = new Font("Dialog", Font.ITALIC, 12);
		lblGameViewContent = new JLabel[gameViewLabelNames.length];
		for (int i=0; i<gameViewLabelNames.length; i++) {
			//JPanel panel = new JPanel();
			JLabel lblGameViewLabel = new JLabel(gameViewLabelNames[i]);
			lblGameViewLabel.setFont(labelFont);
			lblGameViewContent[i] = new JLabel();
			lblGameViewContent[i].setFont(descriptionFont);
			gameDialogContent.add(lblGameViewLabel);
			gameDialogContent.add(lblGameViewContent[i]);
			//gameDialogContent.add(panel);
		}
			
		// Final initialization
		pnlContent = new JPanel();
		pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
		generateAllGamesPanel();

		getContentPane().removeAll();
		add(pnlButtons, BorderLayout.NORTH);
		add(pnlContent, BorderLayout.CENTER);
		
		validate();
		repaint();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SolarGUI movieGUI = new SolarGUI();
		movieGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Event handling to change the panels when different tabs are clicked,
	 * add and search buttons are clicked on the corresponding add and search panels.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnLogin) {
			loginOrRegister(true);
		} else if (e.getSource() == btnRegister) {
			loginOrRegister(false);
		} else if (e.getSource() == btnMyGames) {
			generateMyGamesPanel();
		} else if (e.getSource() == btnMyGamesPlay) {
			int gameId = getIntFromTextField(txtMyGamesGameId);
			if (gameId != -1) {
				Game ownedGame = ownedGame(gameId);
				if (ownedGame == null) {
					JOptionPane.showMessageDialog(null, 
							"You do not own this game. If you believe this "
							+ "is incorrect, please refresh the screen and try again.");
				} else {
					JOptionPane.showMessageDialog(null, 
							"If this was a real system, you would be playing "
							+ ownedGame.getTitle() + " right now.");
				}
			}
		} else if (e.getSource() == btnMyGamesView) {
			int gameId = getIntFromTextField(txtMyGamesGameId);
			if (gameId != -1) {
				Game ownedGame = ownedGame(gameId);
				if (ownedGame == null) {
					JOptionPane.showMessageDialog(null, 
							"You do not own this game. If you believe this "
							+ "is incorrect, please refresh the screen and try again.");
				} else {
					launchGameOverview(gameId);
				}
			}
		} else if (e.getSource() == btnMyGamesRate) {
			int gameId = getIntFromTextField(txtMyGamesGameId);
			if (gameId != -1) {
				Game ownedGame = ownedGame(gameId);
				if (ownedGame == null) {
					JOptionPane.showMessageDialog(null, 
							"You do not own this game. If you believe this "
							+ "is incorrect, please refresh the screen and try again.");
				} else {
					adjustRating(gameId);;
				}
			}
		} else if (e.getSource() == btnAllGames) {
			generateAllGamesPanel();
		} else if (e.getSource() == btnAllGamesView) {
			int gameId = getIntFromTextField(txtAllGamesGameId);
			if (gameId != -1) {
				Game game = db.getGame(gameId);
				if (game == null) {
					JOptionPane.showMessageDialog(null, "Game does not exist.");
				} else {
					launchGameOverview(gameId);
				}
			}
		} else if (e.getSource() == btnAllGamesBuy) {
			int gameId = getIntFromTextField(txtAllGamesGameId);
			if (gameId != -1) {
				Game game = db.getGame(gameId);
				if (game == null) {
					JOptionPane.showMessageDialog(null, "Game does not exist.");
				} else {
					// TODO prompt for confirmation
					boolean success = db.buyGame(username, game);
					if (success) {
						JOptionPane.showMessageDialog(null, "Purchase successful.");
					} else {
						JOptionPane.showMessageDialog(null, "Purchase failed. You "
								+ "may already own this game.");
					}
				}
			}
		} else if (e.getSource() == btnSearch) {
			updateSearchPanel();
			pnlContent.removeAll();
			pnlContent.add(pnlSearch);
			pnlContent.revalidate();
			this.repaint();
		} else if (e.getSource() == btnAdd) {
			pnlContent.removeAll();
			pnlContent.add(pnlAdd);
			pnlContent.revalidate();
			this.repaint();
		} else if (e.getSource() == btnSearchAction) {
			String title = txfTitle.getText();
			String friend = (String) boxSearchFriend.getSelectedItem();
			int genreId = boxSearchGenre.getSelectedIndex() - 1;
			int gameplayId = boxSearchGameplay.getSelectedIndex() - 1;
			int osId = boxSearchOs.getSelectedIndex() - 1;
			int groupingId = boxSearchGrouping.getSelectedIndex() - 1;
			
			generalGameList = db.getGameSearchResults(title, friend, genreId,
					gameplayId, osId, groupingId);
			fillAllGamesGrid(generalGameList);
			pnlContent.removeAll();
			table = new JTable(data, allGamesColumnNames);
			scrollPane = new JScrollPane(table);
			pnlContent.add(scrollPane);
			pnlContent.revalidate();
			this.repaint();
		} else if (e.getSource() == btnFriends) {
			generateFriendsPanel();
		} else if (e.getSource() == btnFriendsAddFriend) {
			// TODO check empty string
			String friendName = txtFriendsName.getText();
			if (username.equals(friendName)) {
				JOptionPane.showMessageDialog(null, "Cannot friend yourself.");
			} else {
				if (db.userExists(friendName)) {
					if (db.addFriend(username, friendName)) {
						JOptionPane.showMessageDialog(null, "Friend added.");
						generateFriendsPanel();
					} else {
						JOptionPane.showMessageDialog(null, "Failed. May already be a friend.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "User does not exist.");
				}
			}
		} else if (e.getSource() == btnFriendsRemoveFriend) {
			// TODO check empty string
			String friendName = txtFriendsName.getText();
			if (friendsList.contains(friendName)) {
				if (db.removeFriend(username, friendName)) {
					JOptionPane.showMessageDialog(null, "Friend removed.");
					generateFriendsPanel();
				} else {
					JOptionPane.showMessageDialog(null, "Failed.");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Can't remove a friend who isn't your friend.");
			}
		} else if (e.getSource() == btnPurchaseHistory) {
			generatePurchaseHistoryPanel();
		} else if (e.getSource() == btnSignOut) {
			logIn();
		}
		
	}
	
	private void updateGeneralData() {
		try
		{
			generalGameList = db.getGames();
			genreList = db.getGenres();
			gameplayTypeList = db.getGameplayTypes();
			osList = db.getOperatingSystems();
			playerGroupingList = db.getPlayerGroupingTypes();
			publisherNameList = db.getPublisherNames();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void fillAllGamesGrid(List<Game> gameList) {
		data = new Object[gameList.size()][allGamesColumnNames.length];
		for (int i=0; i<gameList.size(); i++) {
			Game game = gameList.get(i);
			data[i][0] = ownedGame(game.getId()) != null ? "X" : "";
			data[i][1] = game.getId();
			data[i][2] = game.getTitle();
			data[i][3] = game.getPrice();
			data[i][4] = genreList.get(game.getGenreId());
			data[i][5] = gameplayTypeList.get(game.getGameplayTypeId());
			data[i][6] = publisherNameList.get(game.getPublisherId());
		}
	}
	
	private void fillMyGamesGrid(List<Game> gameList) {
		data = new Object[gameList.size()][myGamesColumnNames.length];
		for (int i=0; i<gameList.size(); i++) {
			Game game = gameList.get(i);
			int gameRating = db.getUserRating(username, game.getId());
			data[i][0] = game.getId();
			data[i][1] = game.getTitle();
			data[i][2] = genreList.get(game.getGenreId());
			data[i][3] = gameplayTypeList.get(game.getGameplayTypeId());
			data[i][4] = publisherNameList.get(game.getPublisherId());
			data[i][5] = gameRating == -1 ? "" : gameRating;
		}
	}
	
	private void fillFriendsGrid(List<String> friendList) {
		data = new Object[friendList.size()][friendColumnNames.length];
		for (int i=0; i<friendList.size(); i++) {
			data[i][0] = friendList.get(i);
		}
	}
	
	private void fillTransactionsGrid(List<Transaction> transactionsList) {
		// make sure owned games list up to date
		personalGameList = db.getOwnedGames(username);
		
		data = new Object[transactionsList.size()][transactionColumnNames.length];
		for (int i=0; i<transactionsList.size(); i++) {
			int gameId = transactionsList.get(i).getGameId();
			Game game = ownedGame(gameId);
			data[i][0] = transactionsList.get(i).getUsername();
			data[i][1] = gameId;
			data[i][2] = game != null ? game.getTitle() : "[Error]";
			data[i][3] = transactionsList.get(i).getPurchasePrice();
			data[i][4] = transactionsList.get(i).getPurchaseDate();
			data[i][5] = game != null ? 
					publisherNameList.get(game.getPublisherId()) : "[Error]";
		}
	}
	
	private void loginOrRegister(boolean isLogin) {
		// TODO reorder
		username = loginField[0].getText();
		String password = Helper.encryptString(loginField[1].getText());
		String email = loginField[2].getText();
		boolean success;
		if (isLogin) {
			success = db.checkUser(username, password);
		} else {
			success = db.addUser(username, password, email);
		}
		if (success) {
			personalGameList = db.getOwnedGames(username);
			createComponents();
		} else {
			String action = isLogin ? "Login" : "Registration";
			JOptionPane.showMessageDialog(this, action + " failed.");
		}
	}
	
	private void generateAllGamesPanel() {
		try {
			generalGameList = db.getGames();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			generalGameList = new ArrayList<>();
		}
		personalGameList = db.getOwnedGames(username);
		fillAllGamesGrid(generalGameList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlAllGamesBtns);
		
		table = new JTable(data, allGamesColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private void updateSearchPanel() {
		updateGeneralData();
		friendsList = db.getFriends(username);
		initializeComboBox(boxSearchFriend, friendsList);
		initializeComboBox(boxSearchGenre, genreList);
		initializeComboBox(boxSearchGameplay, gameplayTypeList);
		initializeComboBox(boxSearchOs, osList);
		initializeComboBox(boxSearchGrouping, playerGroupingList);
	}
	
	private void generateMyGamesPanel() {
		personalGameList = db.getOwnedGames(username);
		fillMyGamesGrid(personalGameList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlMyGamesBtns);
		
		table = new JTable(data, myGamesColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private void generateFriendsPanel() {
		friendsList = db.getFriends(username);
		fillFriendsGrid(friendsList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlFriendsBtns);
		
		table = new JTable(data, friendColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private void generatePurchaseHistoryPanel() {
		List<Transaction> transactionsList = db.getPlayerTransactions(username);
		fillTransactionsGrid(transactionsList);
		
		pnlContent.removeAll();
		
		table = new JTable(data, transactionColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private Game ownedGame(int gameId) {
		if (gameId <= -1) return null;
		Game ownedGame = null;
		for (Game game: personalGameList) {
			if (game.getId() == gameId) {
				ownedGame = game;
				break;
			}
		}
		return ownedGame;
	}
	
	private int getIntFromTextField(JTextField textField) {
		int value = -1;
		try {
			value = Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please enter a number");
		}
		return value;
	}
	
	private void initializeComboBox(JComboBox<String> comboBox, List<String> strings) {
		comboBox.removeAllItems();
		comboBox.addItem("");
		for (String string : strings) {
			comboBox.addItem(string);
		}
	}
	
	private void launchGameOverview(int gameId) {
		updateGeneralData();
		Game game = db.getGame(gameId);
		if (game == null) {
			JOptionPane.showMessageDialog(null, "Please enter a number");
			return;
		}
		
		String ratingString;
		
		int ratingCount = db.getNumOfRatings(gameId);
		if (ratingCount == -1) {
			ratingString = "Error";
		} else if (ratingCount == 0) {
			ratingString = "No ratings"; 
		} else {
			double avgRating = db.getAverageRating(gameId);
			ratingString = avgRating > -0.5 ? 
					String.format("%.1f / 5.0", avgRating) : "Error";
		}
		
		List<String> operatingSystems = db.getOperatingSystems(gameId);
		// All games must have at least one operating system
		if (operatingSystems.size() == 0) {
			operatingSystems.add("[Error]");
		}
		StringBuilder osListStringBuilder = new StringBuilder(operatingSystems.get(0));
		for (int i = 1; i < operatingSystems.size(); i++) {
			osListStringBuilder.append(", " + operatingSystems.get(i));
		}
		
		List<String> groupingTypes = db.getPlayerGroupingTypes(gameId);
		// All games must have at least one player grouping type
		if (groupingTypes.size() == 0) {
			groupingTypes.add("[Error]");
		}
		StringBuilder groupingListStringBuilder = new StringBuilder(groupingTypes.get(0));
		for (int i = 1; i < groupingTypes.size(); i++) {
			groupingListStringBuilder.append(", " + groupingTypes.get(i));
		}
		
		lblGameViewContent[0].setText(Integer.toString(gameId));
		lblGameViewContent[1].setText("<html><p>" + game.getTitle() + "</p></html>");
		lblGameViewContent[2].setText("<html><p>" + game.getDescription() + "</p></html>");
		lblGameViewContent[3].setText(ratingString);
		lblGameViewContent[4].setText(game.getPrice().toString());
		lblGameViewContent[5].setText(genreList.get(game.getGenreId()));
		lblGameViewContent[6].setText(gameplayTypeList.get(game.getGameplayTypeId()));
		lblGameViewContent[7].setText(publisherNameList.get(game.getPublisherId()));
		lblGameViewContent[8].setText(osListStringBuilder.toString());
		lblGameViewContent[9].setText(groupingListStringBuilder.toString());
		
		gameDialog.pack();
		gameDialog.setVisible(true);
	}
	
	public void adjustRating(int gameId) {
		int rating = boxMyGamesRating.getSelectedIndex() - 1;
		// if selected blank entry, remove rating if exists;
		
		boolean success;
		if (rating < 0) {
			success = db.deleteUserRating(username, gameId);
		} else {
			int currentRating = db.getUserRating(username, gameId);
			
			// if no current rating, add new entry; else, update existing
			if (currentRating == -1) {
				success = db.addUserRating(username, gameId, rating);
			} else {
				success = db.updateUserRating(username, gameId, rating);
			}
		}
		
		if (success) {
			JOptionPane.showMessageDialog(null, "Rating saved.");
			// TODO replace with targeted update
			generateMyGamesPanel();
		} else {
			JOptionPane.showMessageDialog(null, "Rating failed. Please try again.");
		}
	}

}
