import java.util.*;

public class GameManager implements GameManagerInterface {
    private Board board;
    private HashMap <Piece, PlayerInterface> players;
    private int[] boardsize;

    public GameManager() {
        players = new HashMap <Piece, PlayerInterface>();
        board = new Board();
        boardsize = new int[2];
    }

    public boolean specifyPlayer(PlayerInterface player, Piece colour) throws ColourAlreadySetException, InvalidColourException { //done
        boolean check = false;
        PlayerInterface value = players.get(colour);
        if (value != null) {
            throw new InvalidColourException();
        } else {
            // Key might be present...
            if (players.containsKey(colour)) {
                // Okay, there's a key but the value is null
                players.put(colour, player);
            } else {
                players.put(colour, player);
                try {
                    check = player.setColour(colour);
                    // System.out.println("check is " + check);
                } catch (ColourAlreadySetException e) {
                    System.out.println("A colour is already set to that player. fix up");
                } catch (InvalidColourException e) {
                    System.out.println("That's an invalid colour choice.");
                }
                // Definitely no such key
            }
        }
        //check = true;

        return check;
    }


    public boolean boardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException { //done
            boolean check = false;
            try {
                check = board.setBoardSize(sizeX, sizeY);
                boardsize[0] = sizeX;
                boardsize[1] = sizeY;
            } catch (InvalidBoardSizeException e) {
                System.out.println("The board is can't be sized with less than 1 for values.");
                check = false;
            } catch (BoardAlreadySizedException e) {
                System.out.println("The board is already sized.");
                check = false;
            }
            return check;
        }
        /**
         * The core of the game manager. This requests each player to make a move and plays these out on the
         * game board.
         */
    public boolean playGame() {
        boolean check = false;
        // check that players have been defined before moving on
        if (players.containsKey(Piece.RED) && players.containsKey(Piece.BLUE) && (players.get(Piece.RED) != players.get(Piece.BLUE) && players.size() == 2)) {

            //establish variables.
            MoveInterface move = new Move();
            boolean flipflop = false; //red starts first
            Piece k;
            Piece conceeder = Piece.UNSET;
            boolean validmove;
            try
            {
                while (board.gameWon() == Piece.UNSET && move.hasConceded() == false && checkIfRemainingMoves(board.getBoardView()) == true)
                {
                    validmove = false;
                    k = CurrentPiece(flipflop);

                    while (validmove == false) { // player makes the move here. loop until a move is put in place or condeeded.
                        System.out.println(k.name() + ": Start Move");
                        try {
                            move = players.get(k).makeMove(board.getBoardView());
                            if (move.hasConceded() == true)
                            {
                                conceeder = k;
                                validmove = true;
                                break;
                            }
                            else
                            { // he hasn't suicided, continue.
                                try
                                {
                                    board.placePiece(k, move);
                                    System.out.println(k.name() + ": The piece is put.");
                                    flipflop = switchBoolean(flipflop);
                                    validmove = true;
                                    // printBoard(board.getBoardView());
                                }
                                catch (PositionAlreadyTakenException e)
                                {
                                    System.out.println("This position is already taken. Try again.");
                                }
                                catch (InvalidPositionException e)
                                {
                                    System.out.println("That's an invalid position. Try again.");
                                }
                            }
                        } catch (NoValidMovesException e) {
                            System.out.println("There's no more valid moves.");
                            System.out.println("Is there a victory?");
                            break;
                        }
                    }
                    if (conceeder != Piece.UNSET) { // the conceeded is found by conceeding
                        break;
                    }
                }
                if (board.gameWon() == Piece.UNSET && checkIfRemainingMoves(board.getBoardView()) == false) {
                    System.out.println("It's a draw! Nobody wins.");
                } else {
                    System.out.println("Winning Board Position:");
                    printBoard(board.getBoardView());
                    String winner;
                    String winner_message = "The winner is ";
                    if (board.gameWon().name() != "UNSET"){
                        //nobody suicided.
                        winner = board.gameWon().name();
                    } else {
                        //clearly someone suicided.
                        winner = flipflop(conceeder).name();
                        winner_message = conceeder + " admits a defeat, " + winner_message;
                    }
                    System.out.println(winner_message + winner);
                }
                check = true;
            } catch (NoBoardDefinedException e) {
                System.out.println("A board isn't defined yet.");
            } catch (InvalidColourException e) {
                System.out.println("That's an invalid colour.");
            }
        } else {
            System.out.println("Players aren't established properly. Make sure that each Player has a colour assigned to them.");
        }
        return check;
    }

    private Piece flipflop(Piece k){
        if (k == Piece.RED){
            return Piece.BLUE;
        } else {
            return Piece.RED;
        }
    }
    private Piece CurrentPiece(boolean k) {
        if (k == true) {
            return Piece.BLUE;
        } else {
            return Piece.RED;
        }
    }
    private boolean switchBoolean(boolean k) {
        if (k == true) {
            return false;
        } else {
            return true;
        }
    }
    private boolean checkIfRemainingMoves(Piece[][] boardView) { //done
        boolean check = true;
        int size = 0;
        int remain = 0;
        for (int i = 0; i < boardView.length; i++) {
            for (int j = 0; j < boardView[i].length; j++) {
                size++;
                if (boardView[i][j] != Piece.UNSET) {
                    remain++;
                }
            }
        }
        if (size == remain) {
            check = false;
        }
        return check;
    }
    public boolean printBoard(Piece[][] b){
        System.out.println("");
        boolean check = false;
        String[] lines = new String[b[1].length];
        String red_indicator1 = "";
        String red_indicator2 = "";

        for (int x = 0; x < lines.length; x++){
            lines[x] = "";
            for (int y = 0; y < x; y++){
                lines[x] += " ";
            }
            lines[x] += "B + ";
        } // initialise

        for (int x = 0; x < b.length*2+5; x++){
            if (x % 2 == 0){
                red_indicator1 += "R";
            } else {
                red_indicator1 += " ";
            }
            if (x % 2 == 1){
                red_indicator2 += "+";
            } else {
                red_indicator2 += " ";
            }
        }

        for(int y=0; y < b[1].length; y++) {
            for(int x=0; x < b.length; x++) {
                // System.out.println(x + ","+ y);
                if (b[x][y] == Piece.UNSET){
                    lines[y] += "_ ";
                } else if (b[x][y] == Piece.BLUE){
                    //blue
                    lines[y] += ""+"\u001B[34m"+"B"+"\u001B[0m"+" ";
                } else {
                    //red
                    lines[y] += ""+"\u001B[31m"+"R"+"\u001B[0m"+" ";
                }
                 lines[y] += "";
            }
        }
        String blankspace = "";
        for (int x = 0; x < lines.length; x++){
            blankspace += " ";
        }

        //PRINT THE STUFF
        System.out.println(red_indicator1);
        System.out.println(red_indicator2);
        for (int x = 0; x < lines.length; x++){
            if (x % 2 == 1){
                     lines[x] += "";
            }
            lines[x] += "+ B";
            System.out.println(lines[x]);
        }
        System.out.println(blankspace + " " + red_indicator2);
        System.out.println(blankspace + "   " + red_indicator1);

        check = true;
        return check;
    }
}