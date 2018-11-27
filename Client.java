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
	private GameWindow game;            // UI for user to interact with
	private Socket socket;              // Connection to server
	private DataInputStream in;         // Streaming input from server
	private DataOutputStream out;       // Streaming output to server
	
	/* Default C'tor: 
	 * Preconditions: 
	 * Postconditions: Initializes socket, input, and output streams
	 */
	public Client(String hostname, int port) {
		if(!setupConnections(hostname, port)) { 
			System.out.println("Error setting up connection");
		}
		game = new GameWindow();
	}
	
	/* setupConnections: Helper function used by constructor
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

    /* closeConnections:
     * Preconditions:
     * Postconditions:
     */
	public void closeConnections() throws IOException {
		socket.close();
		in.close();
		out.close();
	}

    /* connected:
     * Preconditions:
     * Postconditions:
     */
	public boolean connected(){
	    if(socket == null) return false;
	    else return socket.isConnected();
    }

    /* getAvailableGames:   JOINING
     * Preconditions:
     * Postconditions: Return list of gameID's to populate UI with.
     */

    /* UiPopulateGameList:  JOINING
     * Preconditions:
     * Postconditions: Populate list with all games ready to be played.
     */

    /* createGame:          HOSTING
     * Preconditions: gameID that doesn't already exist
     * Postconditions: False if gameID already exists, true otherwise.
     */

    /* joinGame:            JOINING
     * Preconditions: gameID that exists in the gameList
     * Postconditions: Send to server the gameID client wishes to join.
     *                 Return true if client recieves ACK, false otherwise.
     */

    /* mainOperation:
     * Preconditions:
     * Postconditions:
     */
    public void mainOperation() throws IOException{

        while (!game.exit) {
            // Handle GUI input
            System.out.print("");                   // Required ??

            // Figure out which page we're located in
            if (game.currentPage().equals("JOIN")) joinOperation();
            else if (game.currentPage().equals("HOST"))
                hostOperation();
            else { } // Main Menu
        }

        out.writeUTF("exit ");

    }

    /* joinOperation:
     * Preconditions:
     * Postconditions:
     */
    public void joinOperation() throws IOException{
//        System.out.println("IN JOIN OPERATION");


        out.writeUTF("JOIN ");

        // ---------------------- Display available games ---------------------
        Thread t = new Thread(new Runnable(){
            public void run(){
                try {
                    int gameListCount = in.readInt();
                    System.out.println(gameListCount);
                    for(int i = 0; i < gameListCount; i++){
                        String gameEntry = in.readUTF();
                        //System.out.println(gameEntry);
                        game.addHost(gameEntry);
                    }

                } catch (IOException e) {
                    System.out.println("IOException in Thread in joinOperation()");
                }
            }
        });
        t.start();

        // ---------------------- Submit or Back clicked ----------------------
        System.out.println("Outside of thread");

        while(!game.joinBackClicked && !game.joinSubmit){ System.out.print("");}

        if(game.joinBackClicked){
            game.joinBackClicked = false;
            game.removeAll();
            out.writeUTF("exit");
            return;
        }

        if(game.joinSubmit && !game.clientList.isSelectionEmpty()){
            System.out.println("Join Submit Clicked");
            game.joinSubmit = false;

            // ---------------------- Obtain selected game --------------------
            String selectedGame = (String)game.clientList.getSelectedValue();
            System.out.println("Selected Game: " + selectedGame);

            // ---------------------- Send request to connect to game ---------
            out.writeUTF(selectedGame);
            String response = in.readUTF();
            System.out.println("response: " + response);
            if(response.equalsIgnoreCase("connected")){
                game.removeAll();
                playGame();

            } else {
                return;
            }
        } else {
            game.removeAll();
            game.joinSubmit = false;
            return;
        }
    }

    /* hostOperation: The user can either go back to main menu or enter a game
     *                name to host, and submit it to the server.
     * Preconditions:
     * Postconditions:
     */
    public void hostOperation() throws IOException{
//        System.out.println("IN HOST OPERATION");
        game.setStatusText("");
        while(!game.hostBackClicked && !game.hostSubmit){ System.out.print("");}

        if(game.hostBackClicked){
            game.hostBackClicked = false;
            game.setGameNameText("");
            return;
        }

        if(game.hostSubmit){
            System.out.println("Host Submit Clicked");
            game.hostSubmit = false;

            // ---------------------- Check host name validity ----------------
            String gameId = game.getGameName();
            System.out.println("GameID: " + gameId);
            if(gameId == null || gameId.length() <= 0 || gameId.isEmpty()){
                game.setStatusText("invalid host name");
                while(!game.hostBackClicked && !game.hostSubmit){
                    System.out.print("");
                    if(game.hostSubmit){
                        gameId = game.getGameName();
                        System.out.println("host submit clicked");
                        System.out.println("GameID: " + gameId);
                        if(gameId == null || gameId.length() <= 0 || gameId.isEmpty()){
                            game.setStatusText("invalid host name");
                            game.hostSubmit = false;
                        } else {
                            game.setStatusText("Valid host name");
                        }
                    }

                    if(game.hostBackClicked){
                        game.hostBackClicked = false;
                        game.setGameNameText("");
                        return;
                    }
                }
                game.hostSubmit = false;
            } else {
                System.out.println("Valid host name first try");
            }

            // ---------------------- Send request to server ------------------
            out.writeUTF("HOST " + gameId);

            // ---------------------- Server check validity -------------------
            String response = in.readUTF();
            System.out.println("Server response: " + response);
            if(response.equals("invalid")) {
                game.setStatusText("Game " + gameId + " already exists");
                return;
            }
            game.setStatusText("Game " + gameId + " created.\nWaiting for opponent.");
            System.out.println(gameId + " is valid");

            // ---------------------- Create new game -------------------------
            waitForGameStart();
        }
    }

    /* playGame:
     * Preconditions:
     * Postconditions:
     */
    public void playGame() throws IOException {
        out.writeUTF("GAME ");
        System.out.println(socket + " in game" );

        // ---------------------- Display game board for both players ---------
        game.displayGameBoard();

        // ---------------------- Modify label text on game board -------------
        String player = in.readUTF();
        game.setGameLabel(player);

        // ---------------------- "your turn" or "wait" -----------------------

        String turn = "";
        System.out.println("Starting gameLoop");
        while(!turn.equalsIgnoreCase("over")) {
            turn = in.readUTF();
            System.out.println("TURN: " + turn);
            game.setTurnLabel(turn);

            if (turn.equalsIgnoreCase("Your Turn")) {
                // ---------------------- Check for updates on board ----------
                updateBoard();

                System.out.println("-Playing- After thread");
                // ---------------------- Mark board --------------------------
                while (!game.TTTButtonClicked) { System.out.print(""); }
                game.TTTButtonClicked = false;
                System.out.println("-Playing- After click");

                // ---------------------- Send update -------------------------
                out.writeInt(game.TTTButton);
                out.flush();
                System.out.println("-Playing- Sent: " + game.TTTButton);
                String response = in.readUTF();
                System.out.println("-Playing- Received: " + response);

                updateBoard();

            } else {
                // Disable board
                game.enableButtons(false);

                String response = in.readUTF();
                System.out.println("Response length: " + response.length());
                System.out.println("Response: " + response);
                System.out.println("Wait for Ready");

                while(!response.equalsIgnoreCase("Ready")) {
                    System.out.print(".");
                    response = in.readUTF();
                    System.out.println("Got response: " + response);
                }

                out.writeUTF("Client Ready");
                out.flush();
                System.out.println("-Waiting- Received: " + response);

            }
        }

        // Got "over"
        // Display "Win" "Lost" "Tie"
        System.out.println("Final Message: " + turn);
    }

    public void updateBoard() throws IOException{
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char buttonStatus = in.readChar();
                System.out.println(buttonStatus);
                if (buttonStatus == 'G') {
                    game.buttons[i][j].setEnabled(true);
                } else if (buttonStatus == 'X') {
                    game.buttons[i][j].setEnabled(false);
                    game.buttons[i][j].setText("X");
                } else if (buttonStatus == 'O') {
                    game.buttons[i][j].setEnabled(false);
                    game.buttons[i][j].setText("O");
                }
            }
        }
    }

    /* waitForGameStart:
     * Preconditions:
     * Postconditions:
     */
    public void waitForGameStart() throws IOException{
        if(game.currentPage().equals("HOST")){
            String response = "";

            while(!game.hostBackClicked && !response.equals("ready")){
                System.out.print("");
                if(in.available() > 0) response = in.readUTF();
            }

            // Clicked back
            if(game.hostBackClicked){
                game.hostBackClicked = false;
                game.setGameNameText("");
                out.writeUTF("exit");
                System.out.println("Clicking back after waiting for game");
                return;
            }

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
