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

    /* setSecondPlayer:
     * Preconditions:
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

    /* gameReady:
     * Preconditions:
     * Postconditions:
     */
    public boolean gameReady(){
        return (player1 != null) && (player2 != null);
    }

    public boolean gameOver(){
        return movesMade == MAX_MOVES;
    }


    public void playGame(Socket player) throws IOException {
        try{
//            playerTurn = (int)(Math.random() * 2 + 1);
            playerTurn = 1;

            // ---------------------- Set game labels -------------------------
            if(player == player1) p1Out.writeUTF("PLAYER ONE");
            else if(player == player2) p2Out.writeUTF("PLAYER TWO");

            System.out.println("Starting gameloop");
            // ---------------------- Which player thread ---------------------
            while(!gameOver()) {

                if (player == player1) { // PLAYER ONE
                    System.out.println("-------------PLAYER ONE--------------");

                    if (playerTurn == 1) {
                        System.out.println("p1 turn");
                        commWithPlayer(p1Out, p1In, PLAYER_ONE_MARK);
                        System.out.println("    Sending p2 \"Ready\" from p1");
                        movesMade++;
                        p2Out.writeUTF("Ready");
                        p2Out.flush();
                    } else {
                        System.out.println("p1 wait");
                        p1Out.writeUTF("Wait");
                        p1Out.flush();
                        System.out.println( "P1 Receiving..");
                        String response = p1In.readUTF();

                        System.out.println("    P1 Response: " + response);


                    }

                    System.out.println("--end P1---------------------------------");

                } else if (player == player2) {
                    System.out.println("=============PLAYER TWO=============");

                    if (playerTurn == 2) { // PLAYER TWO
                        System.out.println("p2 turn");
                        commWithPlayer(p2Out, p2In, PLAYER_TWO_MARK);
                        movesMade++;
                        p1Out.writeUTF("Ready");
                        p1Out.flush();

                    } else {
                        System.out.println("p2 wait");
                        p2Out.writeUTF("Wait");
                        p2Out.flush();

                        System.out.println("    P2 Receiving..");
                        String response = p2In.readUTF();
                        System.out.println("    P2 Recieved: " + response);
                    }

                    System.out.println("==end P2==============================");
                }

            }

            p1Out.writeUTF("over");
            p2Out.writeUTF("over");

//            sendUpdatedBoard(p1Out);
//            sendUpdatedBoard(p2Out);
            // Send "Tie," or "You Won"/ "You Lost"


        }catch(IOException e){
            System.out.println("IOException in playGame() in Game");
        }
    }

    public void commWithPlayer(DataOutputStream out, DataInputStream in, char mark) throws IOException{
        out.writeUTF("Your Turn");

        // ---------------------- Send info about game board ------------------
        sendUpdatedBoard(out);

        // ---------------------- Get new marked button from player -----------
        int indexToMark = in.readInt();
        System.out.println("indexToMark: " + indexToMark);
        markBoard(indexToMark, mark);

        // ---------------------- Inform player that mark was made ------------
        out.writeUTF("Mark Made");
        out.flush();

        sendUpdatedBoard(out);

        switchPlayerTurn();
    }

    public void sendUpdatedBoard(DataOutputStream out) throws IOException {
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                out.writeChar((int)board[i][j]);
                System.out.print("," + board[i][j]);
            }
        }
        System.out.println();
    }


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
}
