import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

// Handle the connection to all the clients
class ClientHandler extends Thread{
    private Server server;              // Connects with the main server
    private Socket clientSocket;        // Connect to client
    private DataInputStream in;         // Take input from client
    private DataOutputStream out;       // Send output to client
    private String currentGameId;

    /* Default Constructor:
     * Preconditions:
     * Postconditions:
     */
    public ClientHandler(Socket clientSocket, Server server){
        this.clientSocket = clientSocket;
        this.server = server;
        try{
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        }catch(IOException e){
            e.getStackTrace();
        }
    }

    /* closeConnection: Helper to deleting all communicating resources
     * Precondition:
     * Postconditions:
     */
    private void closeConnections() throws IOException{
        System.out.println("Goodbye " + clientSocket);
        clientSocket.close();
        in.close();
        out.close();
    }

    /* hostOperation:
     * Precondition: String request as argument containing gameID
     * Postconditions:
     */
    private void hostOperation(String gameId) throws IOException {
        System.out.println("HOST OPERATION");

        System.out.println("Request: " + gameId);

        // Inform client of gameId validity
        if(server.contains(gameId)) {
            out.writeUTF("invalid");
            return;
        }
        out.writeUTF("valid");
        server.createGame(gameId, clientSocket);
        System.out.println("Server contains " + gameId + ": " + server.contains(gameId));

        // Wait for game object to obtain second player or until "exit" request
        String request = "";
        while(!server.getGameReadyStatus(gameId) && !request.equals("exit")) {
            System.out.print("");
            if(in.available() > 0) request = in.readUTF();
        }

        if(server.getGameReadyStatus(gameId)){
            // Game Starting
            System.out.println("Player 2 connected");
            out.writeUTF("ready");
            currentGameId = gameId;
            System.out.println("CURRENT GAME ID IN HOST: " + currentGameId);
        } else {
            server.removeGame(gameId);
            System.out.println("Removed game created: " + gameId);
        }


        return;
    }

    /* joinOperation:
     * Preconditions:
     * Postconditions:
     */
    private void joinOperation() throws IOException{
        System.out.println("JOIN OPERATION");

        // ---------------------- Send over available games -------------------
        Thread t = new Thread(new Runnable(){
            String gameList[] = Arrays.copyOf(server.listGames().toArray(), server.listGames().toArray().length, String[].class);
            public void run(){
                try {
                    out.writeInt(gameList.length);
                    System.out.println(gameList.length);
                    for(int i = 0; i < gameList.length; i++){
                        System.out.println(gameList[i]);
                        out.writeUTF(gameList[i]);
                    }
                } catch (IOException e) {
                    System.out.println("IOException in joinOperation()");
                }
            }
        });
        t.start();

        // ---------------------- Read "exit" or "connect" --------------------
        String requestedGame = in.readUTF();
        if(requestedGame.equalsIgnoreCase("exit")) {
            System.out.println("Leaving joinOperation()");
            return;
        }

        // ---------------------- Send socket to Game object ------------------

        if(server.contains(requestedGame)){
            System.out.println(requestedGame + " is " + server.getGameReadyStatus(requestedGame));
            out.writeUTF("connected");
            currentGameId = requestedGame;
            server.getGame(requestedGame).setSecondPlayer(clientSocket);
        } else {
            out.writeUTF("error");
        }

        System.out.println("Outside of thread server");

    }

    /* gameOperation:
     * Preconditions:
     * Postconditions:
     */
    public void gameOperation() throws IOException{
        System.out.println("GAME OPERATION IN CLIENT HANDLER FOR " + clientSocket);
        System.out.println("Current game ID: " + currentGameId);

        // Play game
        server.getGame(currentGameId).playGame(clientSocket);

        // Remove game from list since two players are already in
        server.removeGame(currentGameId);
    }

    /* run:
     * Preconditions:
     * Postconditions:
     */
    public void run(){
        try{
            System.out.println("Connected " + clientSocket);
            boolean keepRunning = true;

            // Handle input from Client
            String clientRequest;

            while(keepRunning) {
                clientRequest = in.readUTF();
                System.out.println(clientRequest);
                String requestType = clientRequest.substring(0, clientRequest.indexOf(' '));
                System.out.println(requestType);

                switch (requestType) {
                    case "HOST":
                        hostOperation(clientRequest.substring(clientRequest.indexOf(' ')).trim());
                        break;
                    case "JOIN":
                        joinOperation();
                        break;
                    case "GAME":
                        gameOperation();
                        break;
                    case "exit":
                        keepRunning = false;
                        break;
                }

            }

            closeConnections();
        }catch(IOException e){
            e.getStackTrace();
        }

    }

}