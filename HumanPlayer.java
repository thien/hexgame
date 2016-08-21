import java.util.Scanner;

public class HumanPlayer implements PlayerInterface
{
    Piece colour;
    boolean suicide;
    private int[] boardsize;

    public HumanPlayer()
    {
        suicide = false;
        colour = Piece.UNSET;
        boardsize = new int[2];
	}

    private void setBoardSize(Piece[][] k){
        boardsize[0] = k.length;
        boardsize[1] = k[0].length;
    }
    public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException{
        Move k = new Move();
        printBoard(boardView);
        if (checkIfRemainingMoves(boardView) == true){
            Scanner in = new Scanner(System.in);
            String s = "lol";
            boolean check = false;
            while (check == false)
            {
                System.out.println("Do you concede defeat? type y or n:");
                s = in.nextLine();
                if (s.equals("y") || s.equals("n")){
                    check = true;
                } else if (s.equals("q")){
                    check = true;
                    break;
                } else {
                    System.out.println("Please type in y or n.");
                }
            }
            if (s.equals("y")){
                k.setConceded();
            } else {
                boolean verify = false;
                while (verify == false){
                    System.out.println("enter your x and y coordinates in the form x,y:");
                    String[] q = in.nextLine().split(",");

                    //make your move.
                    //get coords of x and y
                    try {
                        k.setPosition(Integer.parseInt(q[0]),Integer.parseInt(q[1]));
                        verify = true;
                    } catch (InvalidPositionException e){
                        System.out.println("That's a invalid position. Try again.");
                    } catch (NumberFormatException e){
                        System.out.println("I need it in 'x,y' form. Try again.");
                    } catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("I need it in 'x,y' form. Try again.");
                    }
                }
            }
            //check if conceeded

        } else {
            throw new NoValidMovesException();
        }
    	return k;
    }

    //shitty MakeMove one for submission in the test
    public MoveInterface doPutMove(boolean isconceeded, int x, int y, Piece[][] boardView) throws NoValidMovesException
    {
        Move k = new Move();
        if (checkIfRemainingMoves(boardView) == true){
            if (isconceeded == true){
                k.setConceded();
            } else {
                    //make your move.
                    //get coords of x and y
                    try {
                        k.setPosition(x,y);
                       // verify = true;
                    } catch (InvalidPositionException e){
                        System.out.println("That's a invalid position. Try again.");
                    } catch (NumberFormatException e){
                        System.out.println("I need it in 'x,y' form. Try again.");
                    } catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The coordinates are outsize the board. Try again.");
                    }
            }
        } else {
            throw new NoValidMovesException();
        }
        return k;
    }


    public boolean checkIfRemainingMoves(Piece[][] boardView)
    {//done
        boolean check = true;
        int size = 0;
        int remain = 0;
        for(int i=0; i < boardView.length; i++) {
            for(int j=0; j < boardView[i].length; j++) {
            size++;
                if (boardView[i][j] != Piece.UNSET){
                    remain++;
                }
            }
        }
        if (size == remain){
            check = false;
        }
        return check;
    }

    public boolean setColour(Piece colour) throws InvalidColourException, ColourAlreadySetException
    {//done
        boolean check = true;
        if (this.colour != Piece.UNSET){
           check = false;
           throw new ColourAlreadySetException();
        }
        if (colour != Piece.RED && colour != Piece.BLUE){
            check = false;
            throw new InvalidColourException();
        }
        if (check = true){
            this.colour = colour;
        }
    	return check;
    }

    public boolean finalGameState(GameState state)
    {//done
        boolean check = false;
        if (state == GameState.WON){
            System.out.println("You won!");
            check = true;
        }
        else {
            System.out.println("You lost!");
            check = true;
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
            // if (x % 2 == 0){
            //      lines[x] += "";
            // }
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