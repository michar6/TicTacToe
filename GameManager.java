import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GameManager {
    private HashMap<String, Game> availableGames;    // gameID, Game

    /* Default Constructor:
     * Preconditions:
     * Postconditions: Initialize list of available games and their gameID's
     */
    public GameManager(){
        availableGames = new HashMap<String, Game>();
    }

    /* getGameReadyStatus:
     * Preconditions: gameID string to identify which game to check
     * Postconditions:
     */
    public boolean getGameReadyStatus(String gameId){
        return availableGames.get(gameId).gameReady();
    }


    /* contains:
     * Preconditions:
     * Postconditions:
     */
    public boolean contains(String gameId){
        return availableGames.containsKey(gameId);
    }

    /* listGames():
     * Preconditions:
     * Postconditions: Return a set of all the game ID's (Strings)
     */
    public Set<String> listGames(){
        return availableGames.keySet();
    }

    /* getGame: Used to manipulate game object shared by two players
     * Preconditions:
     * Postconditions: Returns Game object referred by gameId
     */
    public Game getGame(String gameId){
        return availableGames.get(gameId);
    }

    /* createGame:
     * Preconditions: Unique gameID
     * Postconditions: Create and add new game to availableGames
     */
    public boolean createGame(String gameId, Socket hostPlayer){
        // Game already exists
        if(availableGames.containsKey(gameId.toLowerCase())) return false;

        // Game can be created
        Game createdGame = new Game(hostPlayer);
        availableGames.put(gameId.toLowerCase().trim(), createdGame);
        return true;
    }

    /* removeGame:
     * Preconditions: Unique gameID
     * Postconditions: Create and add new game to availableGames
     */
    public boolean removeGame(String gameId){
        availableGames.remove(gameId);
        return true;
    }

    /* joinGame:
     * Preconditions: Valid gameID that already exists with list of games
     * Postconditions: True if joined game, false otherwise.
     */
    public boolean joinGame(String gameId, Socket joiningPlayer){
        // Game doesn't exist
        if(!availableGames.containsKey(gameId.toLowerCase())) return false;

        // Game does exist, can be joined
        availableGames.get(gameId).player2 = joiningPlayer;
        return true;
    }
}
