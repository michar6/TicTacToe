import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Game {
    Socket player1;
    Socket player2;
    DataOutputStream p1Out;
    DataInputStream p1In;
    DataOutputStream p2Out;
    DataInputStream p2In;

    private char PLAYER_ONE_MARK = 'X';
    private char PLAYER_TWO_MARK = 'O';

    private int playerTurn;
    private int MAX_MOVES = 9;
    private int movesMade = 0;

    public char board[][] = new char[3][3];

    /* Default Constructor:
     * Preconditions:
     * Postconditions: Create a new game with player1 assigned
     *                 Initialize data output stream and assign socket for P1
     */
    public Game(Socket firstPlayer){
        player1 = firstPlayer;
        try{
            p1Out = new DataOutputStream(firstPlayer.getOutputStream());
            p1In = new DataInputStream(firstPlayer.getInputStream());
        } catch (IOException e){
            System.out.println("IOException in Game constructor");
        }

        for(int i = 0; i < board.length; i++){
            for(int j = 0 ; j < board[i].length; j++){
                board[i][j] = 'G';
            }
        }

    }

    /* setSecondPlayer: Used to establish second player
     * Preconditions: Socket argument that communicates with the second client
     * Postconditions: Initialize data output stream and assign socket for P2
     */
    public void setSecondPlayer(Socket secondPlayer){
        player2 = secondPlayer;
        try{
            p2Out = new DataOutputStream(secondPlayer.getOutputStream());
            p2In = new DataInputStream(secondPlayer.getInputStream());
        } catch (IOException e){
            System.out.println("IOException in Game constructor");
        }
    }

    /* gameReady: Used to decide whether the game is ready to play.
     * Preconditions:
     * Postconditions: True if both player sockets are populated and not null.
     */
    public boolean gameReady(){
        return (player1 != null) && (player2 != null);
    }

    /* gameWonBy: Used to decide whether to send "You Won" or "You Lost" to the
     *            appropriate player.
     * Preconditions: Use a char argument to represent the player.
     * Postconditions: Return true if player (defined by char) won.
     */
    public boolean gameWonBy(char player){
        return (board[0][0] == player && board[0][1] == player && board[0][2] == player) ||  // Horizontal wins
                (board[1][0] == player && board[1][1] == player && board[1][2] == player) ||
                (board[2][0] == player && board[2][1] == player && board[2][2] == player) ||
                (board[0][0] == player && board[1][0] == player && board[2][0] == player) || // Vertical wins
                (board[0][1] == player && board[1][1] == player && board[2][1] == player) ||
                (board[0][2] == player && board[1][2] == player && board[2][2] == player) ||
                (board[0][0] == player && board[1][1] == player && board[2][2] == player) || // Diagonal wins
                (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    /* gameOver: Used to decide if the game is over by checking number of moves
     *           made or if a player has won.
     * Preconditions:
     * Postconditions: Return true if number of moves made equals 9 or either
     *                 player has one.
     */
    public boolean gameOver(){
        return movesMade == MAX_MOVES || gameWonBy(PLAYER_ONE_MARK) || gameWonBy(PLAYER_TWO_MARK);
    }


    
    /**
     * Prompt player for their turn, record their move, update both player's boards.
     * If player exits, end game.
     * 
     * @param player			socket of the associated player
     * @throws IOException
     */
    public void playGame(Socket player) throws IOException {
        try{
            initializeBoard();
//            playerTurn = (int)(Math.random() * 2 + 1);
            playerTurn = 1;

            // ---------------------- Set game labels -------------------------
            if(player == player1) p1Out.writeUTF("GAME PLAYER ONE");
            else if(player == player2) p2Out.writeUTF("GAME PLAYER TWO");

            System.out.println("Starting gameloop");
            boolean playerExit = false;
            // ---------------------- Which player thread ---------------------
            while(!gameOver() && !playerExit) {

                if (player == player1) { // PLAYER ONE
                    System.out.println("-------------PLAYER ONE--------------");
                    // If player 1's turn, prompt for move
                    if (playerTurn == 1) {
                        System.out.println("p1 turn");
                        // Prompt player for turn, break loop if they exit game
                        if(!commWithPlayer(p1Out, p1In, PLAYER_ONE_MARK)) {
                            playerExit = true;
                            break;
                        }
                        System.out.println("    Sending p2 \"Ready\" from p1");
                        movesMade++;
                        p2Out.writeUTF("GAME Ready");
                        p2Out.flush();
                    // If not player 1's turn, wait
                    } else {
                        System.out.println("p1 wait");
                        p1Out.writeUTF("GAME Wait");
                        p1Out.flush();
                        System.out.println( "P1 Receiving..");
                        String response = p1In.readUTF();
                        response = response.substring(response.indexOf(' ')).trim();
                        System.out.println("    P1 Response: " + response);
                        if(response.equalsIgnoreCase("Exit")) playerExit = true;
                    }
                    System.out.println("--end P1---------------------------------");
                } else if (player == player2) {
                    System.out.println("=============PLAYER TWO=============");
                    // If player 2's turn, prompt for move
                    if (playerTurn == 2) {
                        System.out.println("p2 turn");
                        // Prompt player for turn, break loop if they exit game
                        if(!commWithPlayer(p2Out, p2In, PLAYER_TWO_MARK)) {
                            playerExit = true;
                            break;
                        }
                        movesMade++;
                        p1Out.writeUTF("GAME Ready");
                        p1Out.flush();
                    // If not player 2's turn, wait
                    } else {
                        System.out.println("p2 wait");
                        p2Out.writeUTF("GAME Wait");
                        p2Out.flush();

                        System.out.println("    P2 Receiving..");
                        String response = p2In.readUTF();
                        response = response.substring(response.indexOf(' ')).trim();
                        System.out.println("    P2 Recieved: " + response);
                        if(response.equalsIgnoreCase("Exit")) playerExit = true;

                    }
                    System.out.println("==end P2==============================");
                }

            }

            // ---------------------- Let both players know game is over ------
            if(playerExit){
                if(player == player1) {
                    System.out.println("Player 1 exiting");
                    p1Out.writeUTF("GAME Exited");
                    //if(playerTurn == 1){
                        p2Out.writeUTF("GAME Exited");
                    //}

                }
                else {
                    System.out.println("Player 2 exiting");
                    p2Out.writeUTF("GAME Exited");

//                    if(playerTurn == 2){
                        p1Out.writeUTF("GAME Exited");
//                    }
                }
                return;
            }

            // ---------------------- Update both players final boards --------
            if(player == player1) {
                p1Out.writeUTF("GAME Over");
                sendUpdatedBoard(p1Out);
            }
            else {
                p2Out.writeUTF("GAME Over");
                sendUpdatedBoard(p2Out);
            }

            // ---------------------- Send "Tie," "You Won," or "You Lost" ----
            if(movesMade == MAX_MOVES){ // TIE
                if(player == player1) p1Out.writeUTF("GAME Tie");
                else p2Out.writeUTF("GAME Tie");
            } else {
                if(gameWonBy(PLAYER_ONE_MARK)) {
                    if(player == player1) p1Out.writeUTF("GAME You Won");
                    else p2Out.writeUTF("GAME You Lost");
                } else{
                    if(player == player1) p1Out.writeUTF("GAME You Lost");
                    else p2Out.writeUTF("GAME You Won");
                }
            }

            // END OF THE GAME, RETURN

        }catch(IOException e){
            System.out.println("IOException in playGame() in Game");
        }
    }

    
    
    /**
     * Prompt the player for their move, read it in.
     * Mark board with updated move, send updated board, switch which player's turn.
     * 
     * @param out			Data stream for writing to player socket
     * @param in			Data stream for reading from player socket
     * @param mark			Mark associated with the player
     * @return				False if player exits game; else, True
     * @throws IOException
     */
    public boolean commWithPlayer(DataOutputStream out, DataInputStream in, char mark) throws IOException{
        out.writeUTF("GAME Your Turn");

        // ---------------------- Send info about game board ------------------
        sendUpdatedBoard(out);

        // ---------------------- Get new marked button from player -----------
        String indexRequest = in.readUTF();
        System.out.println(indexRequest);
        indexRequest = indexRequest.substring(indexRequest.lastIndexOf(' ')).trim();
        System.out.println(indexRequest);
        if(indexRequest.equalsIgnoreCase("Exit") || indexRequest.equalsIgnoreCase("Exited")) return false;
        int indexToMark = Integer.parseInt(indexRequest);
        System.out.println("indexToMark: " + indexToMark);
        markBoard(indexToMark, mark);

        // ---------------------- Inform player that mark was made ------------
        out.writeUTF("GAME Mark Made");
        out.flush();

        sendUpdatedBoard(out);

        switchPlayerTurn();
        return true;
    }

    
    
    /**
     * Update the player of the board's current configuration.
     * Send every char on the board to the player socket.
     * 
     * @param out			Data stream for writing to player socket
     * @throws IOException
     */
    public void sendUpdatedBoard(DataOutputStream out) throws IOException {
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                out.writeChar((int)board[i][j]);
                System.out.print("," + board[i][j]);
            }
        }
        System.out.println();
    }

    
    
    /**
     * Make a mark on the board at the specified position
     * 
     * @param indexToMark	position in the board to mark
     * @param mark			mark to place at position
     */
    public void markBoard(int indexToMark, char mark){
        System.out.println("Index To Mark: " + indexToMark);
        int row = indexToMark / 10;
        int col = indexToMark % 10;
        System.out.println("ROW/COL: " + row + "/" + col);
        board[row][col] = mark;
    }

    public void switchPlayerTurn(){
        System.out.print("playerTurn : " + playerTurn);
        if(playerTurn == 1) playerTurn = 2;
        else if(playerTurn == 2) playerTurn = 1;
        System.out.println("->" + playerTurn);
    }

    public void initializeBoard(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = 'G';
            }
        }

    }
}
