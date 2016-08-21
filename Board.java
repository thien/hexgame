import java.util.*;
public class Board implements BoardInterface{

    private int sizeX;
    private int sizeY;
    private Piece[][] theboard;
    private Piece thrownloser;
    private Piece brutewinner;

    public Board(){
        sizeX = 0;
        sizeY = 0;
        thrownloser = Piece.UNSET;
    }

    public boolean setBoardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException
    { //done
        boolean check = true;

        if (sizeX < 1 || sizeY < 1)
        {
            check = false;
            throw new InvalidBoardSizeException();
        }
        if (this.sizeX != 0 || this.sizeY != 0){
            check = false;
            throw new BoardAlreadySizedException();
        }
        if (check == true){
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            theboard = new Piece[this.sizeX][this.sizeY];
            for (int i = 0; i<this.sizeX; i++){
                for (int j = 0; j<this.sizeY; j++){
                    theboard[i][j] = Piece.UNSET;
                }
            }
        }
        return check;
    }

    public Piece[][] getBoardView() throws NoBoardDefinedException
    {//done
        if (this.sizeX == 0 || this.sizeY == 0){
            throw new NoBoardDefinedException();
        } else {
            return theboard;
        }
    }

    public boolean placePiece(Piece colour, MoveInterface move) throws PositionAlreadyTakenException, InvalidPositionException, InvalidColourException, NoBoardDefinedException
    {
        boolean check = true;
        int x = move.getXPosition();
        int y = move.getYPosition();
        if (sizeX == 0 || sizeY == 0){
            check = false;
            throw new NoBoardDefinedException();
        }
        if (move.getXPosition() > sizeX-1 || move.getXPosition() <= -1 || move.getYPosition() > sizeY-1 || move.getYPosition() <= -1){
            check = false;
            throw new InvalidPositionException();
        }
        if (colour != Piece.RED && colour != Piece.BLUE){
            check = false;
            throw new InvalidColourException();
        }
        if (theboard[x][y] != Piece.UNSET){
            check = false;
            throw new PositionAlreadyTakenException();
        }
        if (move.hasConceded() == true)
        {
            if (colour == Piece.RED)
            {
                thrownloser = Piece.RED;
                // winner is blue.
            } else {
                thrownloser = Piece.BLUE;
                // winner is red.
            }
        }
        theboard[x][y] = colour;
        return check;
    }

    public Piece gameWon() throws NoBoardDefinedException
    {
        if (this.sizeX == 0 || this.sizeY == 0){
            throw new NoBoardDefinedException();
        } else {
            if (isGameOver(Piece.RED) == true){
                // boolean k = printBoard(theboard);
                return Piece.RED;
            }
            else if (isGameOver(Piece.BLUE) == true){
                // boolean k = printBoard(theboard);
                return Piece.BLUE;
            }
            else {
                return Piece.UNSET;
            }
        }
    }

    public boolean isGameOver(Piece colour)
    {
        boolean check = false;
        if (thrownloser == colour){
            check = true;
        } else {
            check = doTheThing(colour);
            //check = true;
        }
        return check;
    }

    private List<int[]> pieceStartingPositions(Piece colour){
        List<int[]> pos = new ArrayList<int[]>();
        int[] coordinate = new int[2];

        if (colour == Piece.BLUE){
                coordinate[0] = 0;
                for (int i = 0; i < sizeY; i++){ //for x coordinate
                if (theboard[0][i] == colour){
                    coordinate[1] = i;
                    pos.add(coordinate);
                }
            }
        } else if (colour == Piece.RED){
                coordinate[1] = 0;
                for (int i = 0; i < sizeX; i++){ //for x coordinate
                if (theboard[i][0] == colour){
                    coordinate[0] = i;
                    pos.add(coordinate);
                }
            }
        }
        return pos;
    }

    private boolean doTheThing(Piece colour){
        boolean check = false;
        brutewinner = Piece.UNSET;
        if (colour == Piece.RED){
            for (int[] u : pieceStartingPositions(Piece.RED)){ //u is a beginning piece.
                List<int[]> visitedNodes = new ArrayList<int[]>();
                dfs(1,sizeY-1,u,Piece.RED,visitedNodes);
            }
        }
        else if (colour == Piece.BLUE){
            for (int[] u : pieceStartingPositions(Piece.BLUE)){
                List<int[]> visitedNodes = new ArrayList<int[]>();
                dfs(0,sizeX-1,u,Piece.BLUE,visitedNodes);
            }
        }
        if (brutewinner == colour){
            check = true;
        } else {
            // System.out.println("No winner found, carry on");
        }
        return check;
    }

    public List<int[]> findNeighbours(Piece[][] bo, int[] co, Piece colour){
        int limx = bo.length;
        int limy = bo[0].length;
        int x = co[0];
        int y = co[1];
        List<int[]> al = new ArrayList<int[]>();
        int[] m = new int[2];
        if (y-1 >= 0){
            m[1] = y-1;

            if(x+1 < limx && bo[x+1][y-1] == colour){ //northeast
                int[] k = {x+1,y-1};
                al.add(k);
            }
            if (bo[x][y-1] == colour){ //north
                int[] k = {x,y-1};
                al.add(k);
            }
        }
        if (x-1 >= 0 && bo[x-1][y] == colour){ //west
                int[] k = {x-1,y};
                al.add(k);
        }
        if (x+1 < limx && bo[x+1][y] == colour){ //east
                int[] k = {x+1,y};
                al.add(k);
        }
        if (y+1 < limy){
            m[1] = y+1;
            if (x-1 >= 0 && bo[x-1][y+1] == colour){ //southwest
                int[] k = {x-1,y+1};
                al.add(k);
            }
            if (bo[x][y+1] == colour){ //south
                int[] k = {x,y+1};
                al.add(k);
            }
        }
        return al;
    }

    private void dfs(int flipflop,int b,int[] u, Piece colour, List<int[]> visited){
        List<int[]> neighbours = findNeighbours(theboard,u,colour);
        visited.add(u);
        if (u[flipflop] != b || brutewinner != Piece.UNSET){ //is this the end row/col?
            for (int[] v : neighbours){
                boolean check = true;
                for (int[] j : visited){
                    if (Arrays.equals(v,j)){
                        check = false;
                    }
                }
                if (check == true){
                    dfs(flipflop,b,v,colour,visited);
                }
            }
        }
        else {
            brutewinner = colour;
        }
    }

    public boolean printBoard(Piece[][] b){
        System.out.println("");
        boolean check = false;
        String[] lines = new String[b[0].length];
        String line = "";
        for (int x = 0; x < lines.length; x++){
            lines[x] = "    B|";
            if (x % 2 == 0){
                 lines[x] += "--";
            }
        } // initialise
        for(int x=0; x < b.length; x++) {
            for(int y=0; y < b[0].length; y++) {
                // lines[y] = lines[y] + "("+x+","+y+")"; //deals with showing coords

                if (b[x][y] == Piece.UNSET){
                    lines[y] += "- -";
                } else if (b[x][y] == Piece.BLUE){
                    lines[y] += "-0-";
                } else {
                    lines[y] += "-1-";
                }
                 lines[y] += "";
            }
        }
        String topline = "";
        for (int x = 0; x < lines[0].length(); x++){
            if (x > 5){
                if (x % 3 == 0){
                    topline += "R";
                } else {
                    topline += " ";
                }
                line += "-";
            } else {
                topline += " ";
                line += " ";
            }
        }

        //PRINT THE STUFF
        System.out.println(topline);
        System.out.println(line);
        for (int x = 0; x < lines.length; x++){
            if (x % 2 == 1){
                     lines[x] += "--";
            }
            lines[x] += "|B";
            // if (x == ceil(linex.length/2)){
            //     lines[x] += "B";
            // }
            System.out.println(lines[x]);
        }
        System.out.println(line);
        System.out.println(topline);

        check = true;
        return check;
    }
}