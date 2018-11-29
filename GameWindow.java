import java.awt.EventQueue;

import javax.swing.*;
import java.awt.CardLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.border.EtchedBorder;
import java.awt.SystemColor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class GameWindow{

	private JFrame frmGameWindow;
	private JTextField gameNameTextField;
	private JTextField statusText;
	
	public JList clientList;
	private DefaultListModel<String> clientListModel;
    private JPanel gameplayPanel;
    private JPanel mainMenuPanel;
    private JPanel hostPanel;
    private JPanel joinPanel;
    private JPanel gamePanel;
    private JLabel gameLabel;
    private JLabel turnLabel;

    public boolean exit = false;
    public boolean hostSubmit = false;
    public boolean hostBackClicked = false;
    public boolean joinSubmit = false;
    public boolean joinBackClicked = false;

    public JButton btn00 = new JButton();
    public JButton btn01 = new JButton();
    public JButton btn02 = new JButton();
    public JButton btn10 = new JButton();
    public JButton btn11 = new JButton();
    public JButton btn12 = new JButton();
    public JButton btn20 = new JButton();
    public JButton btn21 = new JButton();
    public JButton btn22 = new JButton();
    private JButton btnS;

    public JButton[][] buttons;

    public boolean TTTButtonClicked = false;
    public int TTTButton = -1;

    public HashMap<Integer, JButton> board = new HashMap<>();

    /**
	 * Create the application.
	 */
	public GameWindow() {
		initialize();
        frmGameWindow.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGameWindow = new JFrame();
		frmGameWindow.setResizable(false);
		frmGameWindow.setTitle("Game Window");
		frmGameWindow.setBounds(100, 100, 420, 390);
		frmGameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGameWindow.getContentPane().setLayout(new CardLayout(0, 0));

		frmGameWindow.addWindowListener(new WindowAdapter() {
		    @Override
            public void windowClosing(WindowEvent windowEvent){
                exit = true;
            }
        });

		// ----------------- MAIN MENU -----------------------
		mainMenuPanel = new JPanel();
		frmGameWindow.getContentPane().add(mainMenuPanel, "name_1194666943908356");
		mainMenuPanel.setLayout(new CardLayout(0, 0));
		
		JPanel mainMenuContent = new JPanel();
		mainMenuPanel.add(mainMenuContent, "name_1194691835227743");
		
		JLabel TTTLabel = new JLabel("Tic Tac Toe");
		TTTLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		JButton hostButton = new JButton("Host");
		hostButton.setForeground(new Color(139, 0, 0));
		hostButton.setBackground(Color.WHITE);
		
		JButton joinButton = new JButton("Join");
		joinButton.setForeground(new Color(139, 0, 0));
		joinButton.setBackground(Color.WHITE);
		
		
		JButton exitButton = new JButton("Exit");
		exitButton.setBackground(Color.LIGHT_GRAY);
		exitButton.setForeground(Color.BLACK);
		
		GroupLayout gl_mainMenuContent = new GroupLayout(mainMenuContent);
		gl_mainMenuContent.setHorizontalGroup(
			gl_mainMenuContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainMenuContent.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mainMenuContent.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_mainMenuContent.createSequentialGroup()
							.addComponent(TTTLabel)
							.addContainerGap(273, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_mainMenuContent.createSequentialGroup()
							.addGroup(gl_mainMenuContent.createParallelGroup(Alignment.TRAILING)
								.addComponent(exitButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
								.addComponent(hostButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
								.addComponent(joinButton, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
							.addGap(295))))
		);
		gl_mainMenuContent.setVerticalGroup(
			gl_mainMenuContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainMenuContent.createSequentialGroup()
					.addContainerGap()
					.addComponent(TTTLabel)
					.addGap(52)
					.addComponent(hostButton)
					.addGap(26)
					.addComponent(joinButton)
					.addPreferredGap(ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
					.addComponent(exitButton)
					.addContainerGap())
		);
		mainMenuContent.setLayout(gl_mainMenuContent);

        // ----------------- HOSTING -----------------------
		hostPanel = new JPanel();
		frmGameWindow.getContentPane().add(hostPanel, "name_1194677590844328");
		hostPanel.setLayout(new CardLayout(0, 0));
		
		JPanel hostContent = new JPanel();
		hostPanel.add(hostContent, "name_1194720999782655");
		
		JLabel hostLabel = new JLabel("Host");
		hostLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		JLabel hostnameLabel = new JLabel("Host name:");

        gameNameTextField = new JTextField();
        gameNameTextField.setColumns(10);
		
		btnS = new JButton("Submit");
		btnS.setBackground(Color.WHITE);
		btnS.setForeground(new Color(139, 0, 0));
		
		JButton hostBackBtn = new JButton("Back");
        hostBackBtn.setBackground(Color.WHITE);
        hostBackBtn.setForeground(new Color(139, 0, 0));
		
		statusText = new JTextField();
		statusText.setEditable(false);
		statusText.setColumns(10);
		
		JLabel statusLabel = new JLabel("Status:");
		GroupLayout gl_hostContent = new GroupLayout(hostContent);
		gl_hostContent.setHorizontalGroup(
			gl_hostContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_hostContent.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_hostContent.createParallelGroup(Alignment.LEADING)
						.addComponent(hostLabel)
						.addGroup(gl_hostContent.createParallelGroup(Alignment.TRAILING)
							.addComponent(hostBackBtn)
							.addGroup(gl_hostContent.createSequentialGroup()
								.addGroup(gl_hostContent.createParallelGroup(Alignment.LEADING)
									.addComponent(hostnameLabel)
									.addComponent(statusLabel))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_hostContent.createParallelGroup(Alignment.LEADING, false)
									.addComponent(statusText)
									.addGroup(Alignment.TRAILING, gl_hostContent.createSequentialGroup()
										.addComponent(gameNameTextField, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnS))))))
					.addContainerGap(28, Short.MAX_VALUE))
		);
		gl_hostContent.setVerticalGroup(
			gl_hostContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_hostContent.createSequentialGroup()
					.addContainerGap()
					.addComponent(hostLabel)
					.addGap(18)
					.addGroup(gl_hostContent.createParallelGroup(Alignment.BASELINE)
						.addComponent(hostnameLabel)
						.addComponent(gameNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnS))
					.addGap(18)
					.addGroup(gl_hostContent.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_hostContent.createSequentialGroup()
							.addComponent(statusText, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(hostBackBtn))
						.addComponent(statusLabel))
					.addContainerGap())
		);
		hostContent.setLayout(gl_hostContent);

        // ----------------- JOINING -----------------------
		joinPanel = new JPanel();
		frmGameWindow.getContentPane().add(joinPanel, "name_1196085011998434");
		joinPanel.setLayout(new CardLayout(0, 0));
		
		JPanel joinContent = new JPanel();
		joinPanel.add(joinContent, "name_1196097197647064");
		
		JLabel joinLabel = new JLabel("Join");
		joinLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		JPanel clientPanel = new JPanel();
		
		JButton backBtn = new JButton("Back");
		backBtn.setBackground(Color.WHITE);
		backBtn.setForeground(new Color(139, 0, 0));
		
		JButton joinSubmitButton = new JButton("Submit");
		joinSubmitButton.setBackground(Color.WHITE);
		joinSubmitButton.setForeground(new Color(139, 0, 0));
		GroupLayout gl_joinContent = new GroupLayout(joinContent);
		gl_joinContent.setHorizontalGroup(
			gl_joinContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_joinContent.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_joinContent.createParallelGroup(Alignment.LEADING)
						.addComponent(clientPanel, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
						.addComponent(joinLabel)
						.addGroup(Alignment.TRAILING, gl_joinContent.createSequentialGroup()
							.addComponent(joinSubmitButton)
							.addPreferredGap(ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
							.addComponent(backBtn)))
					.addContainerGap())
		);
		gl_joinContent.setVerticalGroup(
			gl_joinContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_joinContent.createSequentialGroup()
					.addContainerGap()
					.addComponent(joinLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(clientPanel, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
					.addGroup(gl_joinContent.createParallelGroup(Alignment.BASELINE)
						.addComponent(backBtn)
						.addComponent(joinSubmitButton))
					.addContainerGap())
		);

		    // List of games to join
        clientListModel = new DefaultListModel<String>();
        clientListModel.addElement("USA");

		clientList = new JList();
        clientList.setModel(clientListModel);

		clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GroupLayout gl_clientPanel = new GroupLayout(clientPanel);
		gl_clientPanel.setHorizontalGroup(
			gl_clientPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clientPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(clientList, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_clientPanel.setVerticalGroup(
			gl_clientPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clientPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(clientList, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
					.addContainerGap())
		);
		clientPanel.setLayout(gl_clientPanel);
		joinContent.setLayout(gl_joinContent);

		// --------------------- GAME PAGE --------------------------
		gamePanel = new JPanel();
		frmGameWindow.getContentPane().add(gamePanel, "name_1196374056271062");
		gamePanel.setLayout(new CardLayout(0, 0));
		
		JPanel gameContent = new JPanel();
		gamePanel.add(gameContent, "name_1196388902534141");
		
		gameLabel = new JLabel("Game");
		gameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		gameplayPanel = new JPanel();
		gameplayPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		turnLabel = new JLabel("[Wait/YourTurn]");
		turnLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JButton gameExitButton = new JButton("Exit");
		GroupLayout gl_gameContent = new GroupLayout(gameContent);
		gl_gameContent.setHorizontalGroup(
			gl_gameContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_gameContent.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_gameContent.createParallelGroup(Alignment.LEADING)
						.addComponent(gameplayPanel, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
						.addGroup(gl_gameContent.createSequentialGroup()
							.addComponent(gameLabel)
							.addPreferredGap(ComponentPlacement.RELATED, 266, Short.MAX_VALUE)
							.addComponent(turnLabel))
						.addComponent(gameExitButton, Alignment.TRAILING))
					.addContainerGap())
		);
		gl_gameContent.setVerticalGroup(
			gl_gameContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_gameContent.createSequentialGroup()
					.addGap(11)
					.addGroup(gl_gameContent.createParallelGroup(Alignment.BASELINE)
						.addComponent(gameLabel)
						.addComponent(turnLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(gameplayPanel, GroupLayout.PREFERRED_SIZE, 257, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(gameExitButton)
					.addContainerGap(16, Short.MAX_VALUE))
		);
		gameplayPanel.setLayout(new GridLayout(3, 3, 3, 3));
        gameplayPanel.setBackground(new Color(255, 255, 255));

        gameExitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
            	exit = true;
            }
        });

		btn00 = new JButton();
		gameplayPanel.add(btn00);

        btn00.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 0;
            }
        });

        btn01 = new JButton();
		gameplayPanel.add(btn01);

        btn01.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 1;
            }
        });

        btn02 = new JButton();
		gameplayPanel.add(btn02);

        btn02.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 2;
            }
        });

        btn10 = new JButton();
		gameplayPanel.add(btn10);

        btn10.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 10;
            }
        });

        btn11 = new JButton();
		gameplayPanel.add(btn11);

        btn11.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 11;
            }
        });

        btn12 = new JButton();
		gameplayPanel.add(btn12);

        btn12.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 12;
            }
        });

        btn20 = new JButton();
		gameplayPanel.add(btn20);

        btn20.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 20;
            }
        });

        btn21 = new JButton();
		gameplayPanel.add(btn21);

        btn21.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                TTTButtonClicked = true;
                TTTButton = 21;
            }
        });

        btn22 = new JButton();
		gameplayPanel.add(btn22);
		gameContent.setLayout(gl_gameContent);

		btn22.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                TTTButtonClicked = true;
                TTTButton = 22;
            }
        });

        buttons = new JButton[][] {{btn00, btn01, btn02},
                                    {btn10, btn11, btn12},
                                    {btn20, btn21, btn22}};
		
		
		// ================ Action Listeners to buttons =======================
		hostButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				mainMenuPanel.setVisible(false);
				joinPanel.setVisible(false);
				hostPanel.setVisible(true);
			}
		});
		
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainMenuPanel.setVisible(false);
				joinPanel.setVisible(true);
				hostPanel.setVisible(false);
			}
		});

        hostBackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainMenuPanel.setVisible(true);
				joinPanel.setVisible(false);
				hostPanel.setVisible(false);
                hostBackClicked = true;
			}
		});
		
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainMenuPanel.setVisible(true);
				joinPanel.setVisible(false);
				hostPanel.setVisible(false);
                joinBackClicked = true;
			}
		});
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmGameWindow.dispose();
                exit = true;
			}

		});

        btnS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                hostSubmit = true;
            }
        });

        joinSubmitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                joinSubmit = true;
            }
        });
	}


	public void addHost(String gameName){
	    clientListModel.addElement(gameName);
	    clientList.setModel(clientListModel);
    }

    public void removeHost(String gameName){
	    clientListModel.removeElement(gameName);
	    clientList.setModel(clientListModel);
    }

    public void removeAll(){
	    clientListModel.removeAllElements();
	    clientList.setModel(clientListModel);
    }

    
    
    /**
     * Return which page the GUI is displaying.
     * 
     * @return	name of the page which GUI is displaying: MAIN, JOIN, or HOST
     */
    public String currentPage(){
	    if(mainMenuPanel.isVisible()) return "MAIN";
	    else if(joinPanel.isVisible()) return "JOIN";
	    else if(hostPanel.isVisible()) return "HOST";
	    return "";
    }

    public String getGameName(){
	    return gameNameTextField.getText();
    }

    public void setGameNameText(String input) {gameNameTextField.setText(input);};

    public void setStatusText(String status){
	    statusText.setText(status);
    }

    public void setGameLabel(String label){
        gameLabel.setText(label);
    }

    public void setTurnLabel(String label){
        turnLabel.setText(label);
    }

    public void enableButtons(boolean enable){
        btn00.setEnabled(enable);
        btn01.setEnabled(enable);
        btn02.setEnabled(enable);
        btn10.setEnabled(enable);
        btn11.setEnabled(enable);
        btn12.setEnabled(enable);
        btn20.setEnabled(enable);
        btn21.setEnabled(enable);
        btn22.setEnabled(enable);
    }

    public void clearButtons(){
        btn00.setText("");
        btn01.setText("");
        btn02.setText("");
        btn10.setText("");
        btn11.setText("");
        btn12.setText("");
        btn20.setText("");
        btn21.setText("");
        btn22.setText("");
    }

    public void enableSubmitButton(boolean enable){
        btnS.setEnabled(enable);
    }

    public void displayGameBoard(){
        clearButtons();
        mainMenuPanel.setVisible(false);
        hostPanel.setVisible(false);
        joinPanel.setVisible(false);
        gameplayPanel.setVisible(true);
        gamePanel.setVisible(true);
    }

    public void displayMainMenu(){
        mainMenuPanel.setVisible(true);
        hostPanel.setVisible(false);
        joinPanel.setVisible(false);
        gamePanel.setVisible(false);
    }


}
