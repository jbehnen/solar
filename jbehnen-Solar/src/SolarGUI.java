import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A user interface to view the movies, add a new movie and to update an existing movie.
 * The list is a table with all the movie information in it. The TableModelListener listens to
 * any changes to the cells to modify the values for reach movie.
 * @author mmuppa
 *
 */
public class SolarGUI extends JFrame implements ActionListener, TableModelListener
{
	
	private static final long serialVersionUID = 1779520078061383929L;
	private JButton btnList, btnSearch, btnAdd;
	private JPanel pnlLogin, pnlButtons, pnlContent;
	private SolarDB db;
	private List<Game> gameList;
	private List<String> genreList;
	private List<String> gameplayTypeList;
	private List<String> osList;
	private List<String> playerGroupingList;
	private List<String> publisherNameList;
	private String[] columnNames = {
			"gameTitle",
            "gamePrice",
            "genreId",
            "gameplayTypeId",
            "publisherId"};
	
	private Object[][] data;
	private JTable table;
	private JScrollPane scrollPane;
	private JPanel pnlSearch;
	private JLabel lblTitle;;
	private JTextField txfTitle;
	private JButton btnTitleSearch;
	
	private JPanel pnlAdd;
	private JLabel[] txfLabel = new JLabel[7];
	private JTextField[] txfField = new JTextField[7];
	private JButton btnAddMovie;
	
	private JButton btnLogin;
	private JButton btnRegister;
	
	/**
	 * Creates the frame and components and launches the GUI.
	 */
	public SolarGUI() {
		super("Movie Store");
		
		db = new SolarDB();
		try
		{
			gameList = db.getGames();
			genreList = db.getGenres();
			gameplayTypeList = db.getGameplayTypes();
			osList = db.getOperatingSystems();
			playerGroupingList = db.getPlayerGroupingTypes();
			publisherNameList = db.getPublisherNames();
			for (int i = 0; i < playerGroupingList.size(); i++) {
				System.out.println(playerGroupingList.get(i));
			}
			fillGameGrid(gameList);
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		//logIn();
		createComponents();
		setVisible(true);
		setSize(1000, 1000);
	}
	
	private void logIn() {
		pnlLogin = new JPanel();
		pnlLogin.setLayout(new GridLayout(5, 0));
		
		String labelNames[] = {"Username: ", "Password: ", "Email (registration only): "};
		for (int i=0; i<labelNames.length; i++) {
			JPanel panel = new JPanel();
			txfLabel[i] = new JLabel(labelNames[i]);
			txfField[i] = new JTextField(25);
			panel.add(txfLabel[i]);
			panel.add(txfField[i]);
			pnlLogin.add(panel);
		}
		
		JPanel loginTypePanel = new JPanel();
		
		ButtonGroup loginTypeChoice = new ButtonGroup();
		List<JRadioButton> radioButtons = new ArrayList<JRadioButton>();
		radioButtons.add(new JRadioButton ("Player"));
		radioButtons.add(new JRadioButton ("Publisher"));
		for (JRadioButton button : radioButtons) {
			loginTypeChoice.add(button);
			loginTypePanel.add(button);
		}
		pnlLogin.add(loginTypePanel);
		
		JPanel loginButtonPanel = new JPanel();
		btnLogin = new JButton("Log in");
		btnRegister = new JButton("Register");
		btnLogin.addActionListener(this);
		btnRegister.addActionListener(this);
		loginButtonPanel.add(btnLogin);
		loginButtonPanel.add(btnRegister);
		pnlLogin.add(loginButtonPanel);
		
		add(pnlLogin, BorderLayout.CENTER);
	}
    
	/**
	 * Creates panels for Movie list, search, add and adds the corresponding 
	 * components to each panel.
	 */
	private void createComponents()
	{
		pnlButtons = new JPanel();
		btnList = new JButton("Movie List");
		btnList.addActionListener(this);
		
		btnSearch = new JButton("Movie Search");
		btnSearch.addActionListener(this);
		
		btnAdd = new JButton("Add Movie");
		btnAdd.addActionListener(this);
		
		pnlButtons.add(btnList);
		pnlButtons.add(btnSearch);
		pnlButtons.add(btnAdd);
		add(pnlButtons, BorderLayout.NORTH);
		
		//List Panel
		pnlContent = new JPanel();
		table = new JTable(data, columnNames);
		scrollPane = new JScrollPane(table);
		pnlContent.add(scrollPane);
		table.getModel().addTableModelListener(this);
		
		//Search Panel
		pnlSearch = new JPanel();
		lblTitle = new JLabel("Enter Title: ");
		txfTitle = new JTextField(25);
		btnTitleSearch = new JButton("Search");
		btnTitleSearch.addActionListener(this);
		pnlSearch.add(lblTitle);
		pnlSearch.add(txfTitle);
		pnlSearch.add(btnTitleSearch);
		
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
		
		add(pnlContent, BorderLayout.CENTER);
		
		
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
		if (e.getSource() == btnList) {
			try {
				gameList = db.getGames();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fillGameGrid(gameList);
			pnlContent.removeAll();
			table = new JTable(data, columnNames);
			table.getModel().addTableModelListener(this);
			scrollPane = new JScrollPane(table);
			pnlContent.add(scrollPane);
			pnlContent.revalidate();
			this.repaint();
			
		} else if (e.getSource() == btnLogin) {
			System.out.println("login");
		} else if (e.getSource() == btnRegister) {
			System.out.println("register");
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
				gameList = db.getGames(title);
				fillGameGrid(gameList);
				pnlContent.removeAll();
				table = new JTable(data, columnNames);
				table.getModel().addTableModelListener(this);
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
		}
		
	}

	/**
	 * Event handling for any cell being changed in the table.
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        
        db.updateGame(row, columnName, data);
		
	}
	
	private void fillGameGrid(List<Game> gameList) {
		data = new Object[gameList.size()][columnNames.length];
		for (int i=0; i<gameList.size(); i++) {
			data[i][0] = gameList.get(i).getTitle();
			data[i][1] = gameList.get(i).getPrice();
			data[i][2] = genreList.get(gameList.get(i).getGenreId());
			data[i][3] = gameplayTypeList.get(gameList.get(i).getGameplayTypeId());
			data[i][4] = publisherNameList.get(gameList.get(i).getPublisherId());
		}
	}

}
