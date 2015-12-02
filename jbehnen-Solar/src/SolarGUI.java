import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
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
	
	private JButton btnMyGames, btnAllGames, btnSearch, btnAdd, btnFriends, btnPurchaseHistory, btnSignOut;
	private JPanel pnlLogin, pnlButtons, pnlContent;
	private SolarDB db;
	private List<Game> personalGameList, generalGameList;
	private List<String> genreList;
	private List<String> gameplayTypeList;
	private List<String> osList;
	private List<String> playerGroupingList;
	private List<String> publisherNameList;
	private List<String> friendsList;
	private String[] gameColumnNames = {
			"ID",
			"Title",
            "Price",
            "Genre",
            "Gameplay Type",
            "Publisher"};
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
	private JPanel pnlSearch;
	private JLabel lblTitle;;
	private JTextField txfTitle;
	private JButton btnTitleSearch;
	
	private JLabel[] loginLabel = new JLabel[3];
	private JTextField[] loginField = new JTextField[3];
	List<JRadioButton> loginRadioButtons;
	private JButton btnLogin;
	private JButton btnRegister;
	
	private JPanel pnlMyGamesBtns;
	private JTextField txtMyGamesGameId;
	private JButton btnMyGamesView, btnMyGamesRate, btnMyGamesPlay;
	
	private JPanel pnlAllGamesBtns;
	private JTextField txtAllGamesGameId;
	private JButton btnAllGamesView, btnAllGamesBuy;
	
	private JPanel pnlFriendsBtns;
	private JTextField txtFriendsName;
	private JButton btnFriendsAddFriend, btnFriendsRemoveFriend;
	
	private JPanel pnlAdd;
	private JLabel[] txfLabel = new JLabel[7];
	private JTextField[] txfField = new JTextField[7];
	
	private JButton btnAddMovie;

	private String username;
	private boolean isPlayer;
	
	/**
	 * Creates the frame and components and launches the GUI.
	 */
	public SolarGUI() {
		super("Movie Store");
		
		db = new SolarDB();
		try
		{
			generalGameList = db.getGames();
			genreList = db.getGenres();
			gameplayTypeList = db.getGameplayTypes();
			osList = db.getOperatingSystems();
			playerGroupingList = db.getPlayerGroupingTypes();
			publisherNameList = db.getPublisherNames();
			
			fillGameGrid(generalGameList);
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		// TODO revert
		// logIn();
		
		username = "user";
		isPlayer = true;
		createComponents();
		
		setVisible(true);
		setSize(700, 600);
	}
	
	private void logIn() {
		if (pnlLogin == null) {
			pnlLogin = new JPanel();
			
			String labelNames[] = {"Username: ", "Password: ", "Email (registration only): "};
			for (int i=0; i<labelNames.length; i++) {
				JPanel panel = new JPanel();
				loginLabel[i] = new JLabel(labelNames[i]);
				loginField[i] = new JTextField(25);
				panel.add(loginLabel[i]);
				panel.add(loginField[i]);
				pnlLogin.add(panel);
			}
			
			JPanel loginTypePanel = new JPanel();
			
			ButtonGroup loginTypeChoice = new ButtonGroup();
			loginRadioButtons = new ArrayList<JRadioButton>();
			loginRadioButtons.add(new JRadioButton ("Player"));
			loginRadioButtons.add(new JRadioButton ("Publisher"));
			for (JRadioButton button : loginRadioButtons) {
				loginTypeChoice.add(button);
				loginTypePanel.add(button);
			}
			loginRadioButtons.get(0).setSelected(true);
			pnlLogin.add(loginTypePanel);
			
			JPanel loginButtonPanel = new JPanel();
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
		initializePersonalGameList();
		if (isPlayer) {
			createPlayerComponents();
		} else {
			createPublisherComponents();
		}
	}
	
	private void createPlayerComponents() {
		pnlButtons = new JPanel();
		
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
		
		btnSignOut = new JButton("Log Out");
		btnSignOut.addActionListener(this);
		
		pnlButtons.add(btnAllGames);
		pnlButtons.add(btnSearch);
		pnlButtons.add(btnMyGames);
		pnlButtons.add(btnFriends);
		pnlButtons.add(btnPurchaseHistory);
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
		lblTitle = new JLabel("Enter Title: ");
		txfTitle = new JTextField(25);
		btnTitleSearch = new JButton("Search");
		btnTitleSearch.addActionListener(this);
		pnlSearch.add(lblTitle);
		pnlSearch.add(txfTitle);
		pnlSearch.add(btnTitleSearch);
		
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
		
		pnlMyGamesBtns.add(lblMyGamesGameId);
		pnlMyGamesBtns.add(txtMyGamesGameId);
		pnlMyGamesBtns.add(btnMyGamesPlay);
		pnlMyGamesBtns.add(btnMyGamesView);
		pnlMyGamesBtns.add(btnMyGamesRate);

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
		
		pnlContent = new JPanel();
		generateAllGamesPanel();
		
		getContentPane().removeAll();
		add(pnlButtons, BorderLayout.NORTH);
		add(pnlContent, BorderLayout.CENTER);
		
		validate();
		repaint();
	}
	
	private void createPublisherComponents() {	
		pnlButtons = new JPanel();
		
		btnAdd = new JButton("Add Movie");
		btnAdd.addActionListener(this);
		
		pnlButtons.add(btnAdd);
		add(pnlButtons, BorderLayout.NORTH);
		
		//Add Panel
		pnlAdd = new JPanel();
		pnlAdd.setLayout(new GridLayout(6, 0));
		String labelNames[] = {"Enter Title: ", "Enter Year: ", "Enter Length: ", "Enter Genre: ", "Enter Studio Name: "};
		for (int i=0; i<labelNames.length; i++) {
			JPanel panel = new JPanel();
			txfLabel[i] = new JLabel(labelNames[i]);
			txfField[i] = new JTextField(25);
			panel.add(txfLabel[i]);
			panel.add(txfField[i]);
			pnlAdd.add(panel);
		}
		JPanel panel = new JPanel();
		btnAddMovie = new JButton("Add");
		btnAddMovie.addActionListener(this);
		panel.add(btnAddMovie);
		pnlAdd.add(panel);
		
		// TODO switch to new mode
		add(pnlAdd, BorderLayout.CENTER);
		
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
			
		} else if (e.getSource() == btnMyGamesRate) {
			
		} else if (e.getSource() == btnAllGames) {
			generateAllGamesPanel();
		} else if (e.getSource() == btnAllGamesView) {
			
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
			pnlContent.removeAll();
			pnlContent.add(pnlSearch);
			pnlContent.revalidate();
			this.repaint();
		} else if (e.getSource() == btnAdd) {
			pnlContent.removeAll();
			pnlContent.add(pnlAdd);
			pnlContent.revalidate();
			this.repaint();
		} else if (e.getSource() == btnTitleSearch) {
			String title = txfTitle.getText();
			if (title.length() > 0) {
				generalGameList = db.getGames(title);
				fillGameGrid(generalGameList);
				pnlContent.removeAll();
				table = new JTable(data, gameColumnNames);
				scrollPane = new JScrollPane(table);
				pnlContent.add(scrollPane);
				pnlContent.revalidate();
				this.repaint();
			}
		} else if (e.getSource() == btnAddMovie) {
			// TODO: check for safe BigDecimal
			Game game = new Game(Integer.parseInt(txfField[0].getText()), txfField[1].getText(),
					txfField[2].getText(), new BigDecimal(txfField[3].getText()), 
					Integer.parseInt(txfField[4].getText()),
					Integer.parseInt(txfField[5].getText()), 
					Integer.parseInt(txfField[6].getText()));
			db.addGame(game);
			JOptionPane.showMessageDialog(null, "Added Successfully!");
			for (int i=0; i<txfField.length; i++) {
				txfField[i].setText("");
			}
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
	
	private void fillGameGrid(List<Game> gameList) {
		data = new Object[gameList.size()][gameColumnNames.length];
		for (int i=0; i<gameList.size(); i++) {
			data[i][0] = gameList.get(i).getId();
			data[i][1] = gameList.get(i).getTitle();
			data[i][2] = gameList.get(i).getPrice();
			data[i][3] = genreList.get(gameList.get(i).getGenreId());
			data[i][4] = gameplayTypeList.get(gameList.get(i).getGameplayTypeId());
			data[i][5] = publisherNameList.get(gameList.get(i).getPublisherId());
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
		for (JRadioButton button: loginRadioButtons) {
			if (button.isSelected()) {
				isPlayer = button.getText().equals("Player");
			}
		}
		username = loginField[0].getText();
		String password = Helper.encryptString(loginField[1].getText());
		String email = loginField[2].getText();
		boolean success;
		if (isLogin) {
			success = db.checkUser(username, password, isPlayer);
		} else {
			success = db.addUser(username, password, email, isPlayer);
		}
		if (success) {
			createComponents();
		} else {
			String action = isLogin ? "Login" : "Registration";
			JOptionPane.showMessageDialog(this, action + " failed.");
		}
	}
	
	private void initializePersonalGameList() {
		if (isPlayer) {
			personalGameList = db.getOwnedGames(username);
		} else {
			// TODO
			personalGameList = null;
		}
	}
	
	private void generateMyGamesPanel() {
		personalGameList = db.getOwnedGames(username);
		fillGameGrid(personalGameList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlMyGamesBtns, BorderLayout.NORTH);
		
		table = new JTable(data, gameColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane, BorderLayout.CENTER);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private void generateAllGamesPanel() {
		try {
			generalGameList = db.getGames();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			generalGameList = new ArrayList<>();
		}
		fillGameGrid(generalGameList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlAllGamesBtns, BorderLayout.NORTH);
		
		table = new JTable(data, gameColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane, BorderLayout.CENTER);
		
		pnlContent.revalidate();
		this.repaint();
	}
	
	private void generateFriendsPanel() {
		friendsList = db.getFriends(username);
		fillFriendsGrid(friendsList);
		
		pnlContent.removeAll();
		pnlContent.add(pnlFriendsBtns, BorderLayout.NORTH);
		
		table = new JTable(data, friendColumnNames);
		scrollPane = new JScrollPane(table);
		table.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		pnlContent.add(scrollPane, BorderLayout.CENTER);
		
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
		pnlContent.add(scrollPane, BorderLayout.CENTER);
		
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

}
