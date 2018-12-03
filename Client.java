import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
 *
 * Will get list of hosting clients from server and format that in "Join" panel
 * Will register current client whenever they "Submit" in "Host" panel
 */
public class Client {
	private GameWindow gameWindow;            // UI for user to interact with
	private Socket socket;              // Connection to server
	private DataInputStream in;         // Streaming input from server
	private DataOutputStream out;       // Streaming output to server
	
	/**
     * Default C'tor:
	 * Preconditions: 
	 * Postconditions: Initializes socket, input, and output streams
	 */
	public Client(String hostname, int port) {
		if(!setupConnections(hostname, port)) { 
			System.out.println("Error setting up connection");
		}
		gameWindow = new GameWindow();
	}
	
	/**
     * setupConnections: Helper function used by constructor
	 * Preconditions:
	 * Postconditions:
	 */
	private boolean setupConnections(String hostname, int port) {
		try{
			// Connect socket
			socket = new Socket(hostname, port);
			
			// Connect communicating streams
			if(socket.isConnected()) {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} else {
			    return false;
            }
			
			return true;
		} catch (IOException e) {
			e.getStackTrace();
			
			return false;
		}
	}

    /**
     *  closeConnections:
     * Preconditions:
     * Postconditions:
     */
	public void closeConnections() throws IOException {
		socket.close();
		in.close();
		out.close();
	}

    /**
     * Check whether the Client's socket is connected
     * 
     * @return	False if socket is connected; else, True
     */
	public boolean connected(){
	    if(socket == null) return false;
	    else return socket.isConnected();
    }

    /**
     * Check which page GUI is on, execute appropriate function calls.
     * Loop until GUI is on exit screen.
     * 
     * @throws IOException
     */
    public void mainOperation() throws IOException{

        while (!gameWindow.exit) {
            // Handle GUI input
            System.out.print("");                   // Required ??

            // Figure out which page we're located in
            if (gameWindow.currentPage().equals("JOIN")) joinOperation();
            else if (gameWindow.currentPage().equals("HOST"))
                hostOperation();
            else { } // Main Menu
        }

        out.writeUTF("exit ");

    }

    /**
     * joinOperation:
     * Preconditions:
     * Postconditions:
     * @throws IOException
     */
    public void joinOperation() throws IOException{
//        System.out.println("IN JOIN OPERATION");


        out.writeUTF("JOIN ");

        // ---------------------- Display available games ---------------------
        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                	// Read in available games
                    String gameEntry = in.readUTF();
                    System.out.println("Updating Join List");
                    System.out.println("GameEntry: " + gameEntry);
                    while(!gameEntry.contains("/.Done")){
                        System.out.println(gameEntry);
                        gameEntry = gameEntry.substring(gameEntry.lastIndexOf(' ')).trim();
                        System.out.println(gameEntry);
                        // update GUI with available game
                        gameWindow.addHost(gameEntry);
                        gameEntry = in.readUTF();
                    }
                    System.out.println("Done listing all games");

                } catch (IOException e) {
                    System.out.println("IOException in Thread in joinOperation()");
                }
            }
        });
        t.start();

        // ---------------------- Submit or Back clicked ----------------------
        System.out.println("Outside of thread");

        while(!gameWindow.joinBackClicked && !gameWindow.joinSubmit){ System.out.print("");}

        if(gameWindow.joinBackClicked){
            gameWindow.joinBackClicked = false;
            gameWindow.removeAll();
            out.writeUTF("exit");
            return;
        }

        if(gameWindow.joinSubmit && !gameWindow.clientList.isSelectionEmpty()){
            System.out.println("Join Submit Clicked");
            gameWindow.joinSubmit = false;

            // ---------------------- Obtain selected game --------------------
            String selectedGame = (String)gameWindow.clientList.getSelectedValue();
            System.out.println("Selected Game: " + selectedGame);

            // ---------------------- Send request to connect to game ---------
            out.writeUTF(selectedGame);
            String response = in.readUTF();
            System.out.println("response: " + response);
            if(response.equalsIgnoreCase("connected")){
                gameWindow.removeAll();
                playGame();

            } else {
                return;
            }
        } else {
            gameWindow.removeAll();
            gameWindow.joinSubmit = false;
            return;
        }
    }

    /** hostOperation: The user can either go back to main menu or enter a game
     *                name to host, and submit it to the server.
     * Preconditions:
     * Postconditions:
     */
    public void hostOperation() throws IOException{
//        System.out.println("IN HOST OPERATION");
        gameWindow.setStatusText("");
        while(!gameWindow.hostBackClicked && !gameWindow.hostSubmit){ System.out.print("");}

        if(gameWindow.hostBackClicked){
            gameWindow.hostBackClicked = false;
            gameWindow.setGameNameText("");
            return;
        }

        if(gameWindow.hostSubmit){
            System.out.println("Host Submit Clicked");
            gameWindow.hostSubmit = false;

            // ---------------------- Check host name validity ----------------
            String gameId = gameWindow.getGameName();
            System.out.println("GameID: " + gameId);
            if(gameId == null || gameId.length() <= 0 || gameId.isEmpty()){
                gameWindow.setStatusText("invalid host name");
                while(!gameWindow.hostBackClicked && !gameWindow.hostSubmit){
                    System.out.print("");
                    if(gameWindow.hostSubmit){
                        gameId = gameWindow.getGameName();
                        System.out.println("host submit clicked");
                        System.out.println("GameID: " + gameId);
                        if(gameId == null || gameId.length() <= 0 || gameId.isEmpty()){
                            gameWindow.setStatusText("invalid host name");
                            gameWindow.hostSubmit = false;
                        } else {
                            gameWindow.setStatusText("Valid host name");
                        }
                    }

                    if(gameWindow.hostBackClicked){
                        gameWindow.hostBackClicked = false;
                        gameWindow.setGameNameText("");
                        return;
                    }
                }
                gameWindow.hostSubmit = false;
            } else {
                System.out.println("Valid host name first try");
            }

            // ---------------------- Send request to server ------------------
            out.writeUTF("HOST " + gameId.toLowerCase().trim());

            // ---------------------- Server check validity -------------------
            String response = in.readUTF();
            System.out.println("Server response: " + response);
            if(response.equals("invalid")) {
                gameWindow.setStatusText("Game (" + gameId.toLowerCase().trim() + ") already exists");
                return;
            }
            gameWindow.setStatusText("Game " + gameId.toLowerCase().trim() + " created.\nWaiting for opponent.");
            System.out.println(gameId + " is valid");

            // ---------------------- Create new game -------------------------
            waitForGameStart(gameId);
        }
    }

    /**
     * playGame:
     * Preconditions:
     * Postconditions:
     */
    public void playGame() throws IOException {
        out.writeUTF("GAME ");
        System.out.println(socket + " in game" );

        // ---------------------- Display game board for both players ---------
        gameWindow.displayGameBoard();

        // ---------------------- Modify label text on game board -------------
        String player = in.readUTF();
        player = player.substring(player.indexOf(' ')).trim();
        gameWindow.setGameLabel(player);

        // ---------------------- "your turn" or "wait" -----------------------

        String turn = "";
        System.out.println("Starting gameLoop");
        turn = in.readUTF();
        turn = turn.substring(turn.indexOf(' ')).trim(); // GAME' 'Your Turn or GAME' 'Wait
        System.out.println("TURN: " + turn);
        gameWindow.setTurnLabel(turn);
        gameWindow.exit = false;
        while(!turn.equalsIgnoreCase("Over") && !gameWindow.exit) { // Also check if "Exit" is clicked

            if (turn.equalsIgnoreCase("Your Turn")) {
                // ---------------------- Check for updates on board ----------
                updateBoard();

                System.out.println("-Playing- After thread");
                String response = "";
                if(in.available() > 0) {
                    response = in.readUTF();
                    response = response.substring(response.indexOf(' ')).trim();
                }
                // ---------------------- Mark board --------------------------
                while (!gameWindow.TTTButtonClicked && !gameWindow.exit && !response.equalsIgnoreCase("Exited")) {
                    System.out.print("");
                    if(in.available() > 0) {
                        response = in.readUTF();
                        response = response.substring(response.indexOf(' ')).trim();
                        System.out.println("Response: " + response);
                    }
                }

                if(response.equalsIgnoreCase("Exited")){
                    gameWindow.exit = true;
                }

                if(gameWindow.exit) {
                    System.out.println("WAS ABOUT TO MAKE MOVE BUT EXITED");
                    break;
                }
                gameWindow.TTTButtonClicked = false;
                System.out.println("-Playing- After click");

                // ---------------------- Send update -------------------------
                out.writeUTF("GAME mark " + gameWindow.TTTButton);
                out.flush();
                System.out.println("-Playing- Sent: " + gameWindow.TTTButton);
                response = in.readUTF();
                System.out.println("-Playing- Received: " + response);

                updateBoard();

            } else {
                // Disable board
                gameWindow.enableButtons(false);

                System.out.println("Waiting and waiting for response");
                //String response = in.readUTF();
                String response = "";
                if(in.available() > 0) {
                    response = in.readUTF();
                    response = response.substring(response.indexOf(' ')).trim(); // GAME' 'Ready
                    System.out.println("Response length: " + response.length());
                    System.out.println("Response: " + response);
                }

                System.out.println("Wait for Ready");

                while(!response.equalsIgnoreCase("Ready") && !response.equalsIgnoreCase("Exited") && !gameWindow.exit) {
//                    System.out.print(".");
                    System.out.println(gameWindow.exit + " and Response : " + response);
                    if(in.available() > 0) {
                        response = in.readUTF();
                        response = response.substring(response.indexOf(' ')).trim(); // GAME' 'Ready
                    }
//                    System.out.println("Got response: " + response);
                }

                if(response.equalsIgnoreCase("Exited")) {
                    System.out.println("WAS WAITING NOW EXITED");
                    gameWindow.exit = true;
                    break;
                }

                if(!gameWindow.exit) {
                    out.writeUTF("GAME CReady"); // Client ready
                    out.flush();
                    System.out.println("-Waiting- Received: " + response);
                } else{
                    System.out.println("Game.exit : " + gameWindow.exit);
                }

            }

            if(!gameWindow.exit) {
                turn = in.readUTF();
                turn = turn.substring(turn.indexOf(' ')).trim(); // GAME' 'Your Turn or GAME' 'Wait
                System.out.println("TURN: " + turn);
                gameWindow.setTurnLabel(turn);
            }
        }

        // Client has exited their game
        if(gameWindow.exit){
            System.out.println("Within game.exit block");
            gameWindow.exit = false;
            out.writeUTF("GAME Exit");
            System.out.println("Reading response...");
            String response = in.readUTF();
            response = response.substring(response.indexOf(' ')).trim();
            System.out.println("response: " + response);
            gameWindow.setTurnLabel(response);
            gameWindow.displayMainMenu();
            if(in.available() > 0) System.out.println("Read: " + in.readUTF());
            return;
        }

        // Got "over"
        // Display "Win" "Lost" "Tie"
        System.out.println("Final Message: " + turn);

        // Ensure all updates to board are made so they match both players
        updateBoard();

        // Get "TIE" / "WON" / "LOST" message
        String gameEnd = in.readUTF();
        gameEnd = gameEnd.substring(gameEnd.indexOf(' ')).trim();
        gameWindow.setTurnLabel(gameEnd);

        // Disable game
        gameWindow.enableButtons(false);

        // Wait for exit button to be clicked
        while(!gameWindow.exit){System.out.print("");}

        // Return to main menu
        if(gameWindow.exit){
            gameWindow.exit = false;
            gameWindow.displayMainMenu();
            gameWindow.enableButtons(true);
            return;
        }
    }

    /**
     *  updateBoard:
     * Preconditions:
     * Postconditions:
     */
    public void updateBoard() throws IOException{
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char buttonStatus = in.readChar();
                System.out.println(buttonStatus);
                if (buttonStatus == 'G') {
                    gameWindow.buttons[i][j].setEnabled(true);
                } else if (buttonStatus == 'X') {
                    gameWindow.buttons[i][j].setEnabled(false);
                    gameWindow.buttons[i][j].setText("X");
                } else if (buttonStatus == 'O') {
                    gameWindow.buttons[i][j].setEnabled(false);
                    gameWindow.buttons[i][j].setText("O");
                }
            }
        }
    }

    /** waitForGameStart:
     * Preconditions:
     * Postconditions:
     */
    public void waitForGameStart(String gameId) throws IOException{
        if(gameWindow.currentPage().equals("HOST")){
            gameWindow.enableSubmitButton(false);
            String response = "";

            while(!gameWindow.hostBackClicked && !response.equals("ready")){
                System.out.print("");
                if(in.available() > 0) response = in.readUTF();
            }

            // Clicked back
            if(gameWindow.hostBackClicked){
                gameWindow.hostBackClicked = false;
                gameWindow.setGameNameText("");
                out.writeUTF("exit");
                System.out.println("Clicking back after waiting for game");
                gameWindow.enableSubmitButton(true);
                return;
            }

            // Allow client to submit other games again
            gameWindow.enableSubmitButton(true);

            // Remove game from list
            gameWindow.removeHost(gameId);
            gameWindow.setGameNameText("");

            // Game starting
            playGame();





        } else { // Current page is JOIN

        }
    }

    /* MAIN */
	public static void main(String args[]) {
        // Connect to Server
	    Client c = new Client("localhost", 8564);
        if(!c.connected()) return;

		try {
		    System.out.println("Connected to Server");

		    c.mainOperation();

			c.closeConnections();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
}
