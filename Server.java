import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class Server {
	private ServerSocket welcomeSocket;
    private GameManager gameManager;

	/* Default C'tor: Setup welcome socket for new clients
	 * Preconditions:
	 * Postconditions:
	 */
	public Server(int port) throws IOException {
	    welcomeSocket = new ServerSocket(port);
	    gameManager = new GameManager();
	}

    /* contains:
     * Preconditions:
     * Postconditions:
     */
	public boolean contains(String gameId){
	    return gameManager.contains(gameId.toLowerCase().trim());
    }

    /* createGame:
     * Preconditions:
     * Postconditions:
     */
    public boolean createGame(String gameId, Socket player){
	    return gameManager.createGame(gameId, player);
    }

    /* createGame:
     * Preconditions:
     * Postconditions:
     */
    public boolean removeGame(String gameId){
        return gameManager.removeGame(gameId);
    }

    /* getGameReadyStatus:
     * Preconditions:
     * Postconditions:
     */
    public boolean getGameReadyStatus(String gameId){
        return gameManager.getGameReadyStatus(gameId);
    }

    /* getGame: Used to manipulate game object
     * Preconditions:
     * Postconditions:
     */
    public Game getGame(String gameId){
        return gameManager.getGame(gameId);
    }

    /* listGames:
     * Preconditions:
     * Postconditions: Return a set of games available to play
     */
    public Set<String> listGames(){
        return gameManager.listGames();
    }

	public static void main(String args[]) {
		try {
		    // Create server
            Server server = new Server(8564);

            // Continuously check for clients and assign em new socket/thread
		    while(true){
                Socket clientSocket = server.welcomeSocket.accept();
                Thread clientThread = new ClientHandler(clientSocket, server);
                clientThread.start();
            }

		} catch(IOException e) {
			e.getStackTrace();
		}
	}


}


