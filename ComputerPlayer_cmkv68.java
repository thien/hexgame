import java.util.*;
public class ComputerPlayer_cmkv68 implements PlayerInterface
{

    Piece colour;
    Piece enemyCol;
    List<int[]> botMovesList;
    List<int[]> enemyMovesList;
    int enemyMoveDepth;
    Piece[][] oldBoardState;
    int pivot;
    int[] botPreviousMove;
    int[] enemyPreviousMove;
    int[] boardSize;
    int[] boardCenter;
    boolean debug; //option to display bot messages
    boolean hasInitialised;
    boolean direction_boolean;

    public ComputerPlayer_cmkv68()
    {
        botMovesList = new ArrayList<int[]>();
        enemyMovesList = new ArrayList<int[]>();
        colour = Piece.UNSET;
        enemyCol = Piece.UNSET;
        enemyMoveDepth = 0;
        pivot = 1;
        boardSize = new int[2];
        hasInitialised = false;
        direction_boolean = true;
        debug = false;
    }

    public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException{
        Move k = new Move();
        initialise(boardView);

        if (checkIfRemainingMoves(boardView) == true){
            try {
                enemyPreviousMove = enemyLastMove(boardSize, boardView);
                int[] themove = findTheCoordinates(boardView); //find the coordinates that the bot is going to make.
                k.setPosition(themove[0],themove[1]); //assign the coordinates to the Move Class.
                botMovesList.add(themove); //update list of bot moves.
                oldBoardState = boardView; //save the state of the board.
            } catch (InvalidPositionException e){
                // robots don't fail, the programmer does.
            } catch (NumberFormatException e){
                // robots don't fail, the programmer does.
            }
        } else {
            throw new NoValidMovesException();
        }
        return k;
    }
    private void verboosePrint(String k){
        if (debug == true){
            System.out.println(k);
        }
    }

    public void setDebug(boolean k){
        debug = k;
    }
    private void initialise(Piece[][] board){
        if (hasInitialised == false){
            //start initialising variables
            // board related
            boardSize = findBoardSize(board);
            oldBoardState = board;
            //establish board size.
            boardCenter = theCenter(board);
            //find the center coordinate of the board. we wanna start there for maximum options for the bot.
            //---------------------

            //enemy player related
            enemyCol = enemyColour(colour); //define the enemy colour first.
            enemyMovesList = enemyMoves(board);

            //bot related
            direction_boolean = northOrEast(colour); //true is North to South, false is East to West!

            hasInitialised = true;
        }
        enemyMovesList = enemyMoves(board);
        enemyPreviousMove = enemyLastMove(boardSize, board);
    }
    private int[] theCenter(Piece[][] board){
        int x = Integer.valueOf(Math.round(board.length / 2));
        int y = Integer.valueOf(Math.round(board[0].length /2));
        int[] k = new int[2];
        k[0] = x;
        k[1] = y;
        return k;
    }
    private List<int[]> enemyMoves(Piece[][] board){
        List<int[]> moves = new ArrayList<int[]>();
        for (int x = 0; x<board.length; x++){
                for (int y = 0; y<board[0].length; y++){
                    if (board[x][y] != Piece.UNSET && board[x][y] != colour){
                        int[] k = new int[2];
                        k[0] = x;
                        k[1] = y;
                        moves.add(k);
                    }
                }
            }
        return moves;
    }
    private int[] enemyLastMove(int[] boardSize, Piece[][] board){
        int[] j = new int[2];
        List<int[]> newEnemyMoves = enemyMoves(board);
        for (int k = 0; k < newEnemyMoves.size(); k++){
            if (newEnemyMoves.get(k) != enemyMovesList.get(k)){
                j = newEnemyMoves.get(k);
            }
        }
        return j;
    }
    private int[] findBoardSize(Piece[][] board){
        int[] bs = new int[2];
        bs[0] = board.length;
        bs[1] = board[0].length;
        return bs;
    }
    private boolean northOrEast(Piece c){
        boolean check;
        if (c == Piece.RED){
            //you wanna go from north to south
            check = true;
            verboosePrint("Bot is going from up to down");
        } else {
            //you wanna go from west to east
            check = false;
            verboosePrint("Bot is going from left to right");
        }

        return check;
    }
    private Piece enemyColour(Piece c){//works
        if (enemyCol != Piece.UNSET){
            if (c == Piece.RED){
                return Piece.BLUE;
            } else {
                return Piece.RED;
            }
        } else {
            return enemyCol;
        }
    }
    private String printCoordsList(List<int[]> coords){
        String l = "";
        for (int[] i : coords){
            l = l + "(" + i[0] + "," + i[1] + "), ";
        }
        return l;
    }
    //finding the move
    private int[] findTheCoordinates(Piece[][] board){
        int[] coordinates = new int[2];
        verboosePrint("-------------------------------------------");
        verboosePrint("Enemy moves: " + printCoordsList(enemyMovesList));
        verboosePrint("Enemy last move: " + enemyPreviousMove[0] + "," + enemyPreviousMove[1]);

        List<int[]> possibles;
        boolean strategy = true; // false is defend, true is attack;
        List<int[]> visitednodes = new ArrayList<int[]>();
        enemyMoveDepth = 0;


        if (botMovesList.size() == 0){
            //first move will try and be a center move
            if (board[boardCenter[0]][boardCenter[1]] == Piece.UNSET){
                coordinates = boardCenter;
            } else {
                // find moves close to the center
                possibles = putListIntoMembers(board,boardCenter,Piece.UNSET, colour);
                coordinates = ChooseBestMove(possibles,board,direction_boolean);
            }
        } else {
            // moves have been done..
            if (enemyMovesList.size() > 2){ //bot should start being worried.
                for (int[] i : enemyPosLine(board)){
                    dfs(board,enemyPreviousMove,enemyMovesList,0); //use dfs to determine the move depth
                }
                verboosePrint("Enemy move depth:" + enemyMoveDepth);
                if (enemyMoveDepth > Math.floor(boardSize[1]/2)){
                    strategy = false;
                } else {
                    strategy = true;
                }

            }
            if (strategy == false){
                //defend moves!
                //find most recent move, block!
                verboosePrint("Defending");
                verboosePrint("Bot: Enemy recent move: " + enemyPreviousMove[0] + "," + enemyPreviousMove[1]);
                possibles = putListIntoMembers(board,enemyPreviousMove,Piece.UNSET, colour);
                coordinates = ChooseBestMove(possibles,board,direction_boolean);
            } else {
                //attack moves!
                verboosePrint("Bot: Attacking");
                int[] botLastMove = botMovesList.get(botMovesList.size() - 1);
                verboosePrint("Bot last move: " + botLastMove[0] + "," + botLastMove[1]);
                verboosePrint("calculating good moves");
                List<int[]> goodmoves_base = closestToEdgePiece(board, pivot);
                verboosePrint("calculated good moves, move size: " + goodmoves_base.size());
                verboosePrint("Good moves: " + printCoordsList(goodmoves_base));
                if (goodmoves_base.size() == 0){
                    //no moves.
                    verboosePrint("Bot's going to have to try a leap of faith");
                    coordinates = randomMove(board);
                } else {
                    verboosePrint("Bot has moves, calculating!");
                    // thank god theres moves in the good moves list, lets continue
                    possibles = putListIntoMembers(board, goodmoves_base.get(0),Piece.UNSET, colour);
                    if (possibles.size() == 0){
                    //no moves.
                        verboosePrint("Bot's going to have to try a leap of faith");
                        coordinates = randomMove(board);
                    } else {
                        verboosePrint("Possible moves: " + printCoordsList(possibles));
                        coordinates = ChooseBestMove(possibles,board,direction_boolean);
                    }
                }
            }
        }
        verboosePrint("Bot does: " + coordinates[0] + "," + coordinates[1]);
        return coordinates;
    }
    private int[] randomMove(Piece[][] board){
        int[] move = new int[2];
        Random generator = new Random();
        boolean doesthiswork = false;
            while (doesthiswork == false){
                int x = generator.nextInt(boardSize[0]);
                int y = generator.nextInt(boardSize[1]);
                if (board[x][y] == Piece.UNSET){
                    doesthiswork = true;
                    move[0]=x;
                    move[1]=y;
                    break;
                }
            }
        return move;
    }
    private List<int[]> closestToEdgePiece(Piece[][] board, int pivot){
        List<int[]> pos = new ArrayList<int[]>();
        int[] coordinate = new int[2];
        boolean check = false;
        int k;

        if (colour == Piece.BLUE){
            if (pivot == 0){
                k = 0;
            } else {
                k = boardSize[0]-1;
            }
            while (check == false){
                for (int i = 0; i < boardSize[1]; i++){ //for x coordinate
                    if (board[k][i] == colour){
                        coordinate[0] = k;
                        coordinate[1] = i;
                        if(!pos.contains(coordinate)){
                            pos.add(coordinate);
                            check = true;
                        }
                    }
                }
                if (pivot == 0){
                    k++;
                } else {
                    k--;
                }
            }
        } else if (colour == Piece.RED){
            if (pivot == 0){
                k = 0;
            } else {
                k = boardSize[1]-1;
            }
            while (check == false){
                for (int i = 0; i < boardSize[0]; i++){ //for x coordinate
                    if (board[i][k] == colour){
                        coordinate[0] = i;
                        coordinate[1] = k;
                        if(!pos.contains(coordinate)){
                            pos.add(coordinate);
                            check = true;
                        // gMap.add(user).add(new ArrayList(user));
                        }
                        check = true;
                    }
                }
                if (pivot == 0){
                    k++;
                } else {
                    k--;
                }
            }
        }
        return pos;
    }
    private List<int[]> enemyPosLine(Piece[][] board){
        List<int[]> pos = new ArrayList<int[]>();
        int[] coordinate = new int[2];
        boolean check = false;

        if (enemyCol == Piece.BLUE){
            coordinate[0] = 0;
            while (check == false){
                for (int i = 0; i < boardSize[1]; i++){ //for x coordinate
                    if (board[coordinate[1]][i] == colour){
                        coordinate[1] = i;
                        pos.add(coordinate);
                        check = true;
                    }
                }
                coordinate[0]++;
            }
        } else if (enemyCol == Piece.RED){
            coordinate[1] = 0;
            while (check == false){
                for (int i = 0; i < boardSize[0]; i++){ //for x coordinate
                    if (board[i][coordinate[1]] == colour){
                        coordinate[0] = i;
                        pos.add(coordinate);
                        check = true;
                    }
                }
                coordinate[1]++;
            }
        }
        return pos;
    }
    private int[] ChooseBestMove(List<int[]> possibles, Piece[][] board, boolean direction_boolean){
        int[] k = new int[2];
        verboosePrint("Before Sort: " + printIntList(possibles) + possibles.size());
        possibles = bubbleSort(possibles,direction_boolean);
        verboosePrint("After Sort: " + printIntList(possibles));
        for (int[] i : possibles){
            if (board[i[0]][i[1]] == Piece.UNSET){
                k = i;
                break;
            }
        }
        return k;
    }
    private String printIntList(List<int[]> p){
        String ss= "";
        for (int[] i : p){
            ss += "("+i[0]+","+i[1]+"),";
            // pivot = 0;
        }
        return ss;
    }
    private List<int[]> bubbleSort(List<int[]> p, boolean direction_boolean){
        int k = 0;
        if (direction_boolean == true) {
           k = 0;
            // deal with x axis.
        } else {
           k = 1;
            // deal with y axis.
        }
        //true is North to South, false is East to West!
        if (pivot == 1){
            for (int i = 1; i < p.size()-1; i++){
                for (int j = 0; i < p.size()-1; i++){
                    if (p.get(j)[k] > p.get(j+1)[k]){
                        Collections.swap(p,j,j+1);
                    }
                }
            }
            pivot = 0;
        }
        else {
            for (int i = 0; i < p.size()-1; i++){
                for (int j = 0; i < p.size()-1; i++){
                    if (p.get(j)[k] < p.get(j+1)[k]){
                        Collections.swap(p,j,j+1);
                    }
                }
            }
            pivot = 1;
        }
        return p;
    }
    public List<int[]> putListIntoMembers(Piece[][] bo, int[] co, Piece colour, Piece forwho)
    {//works
        // verboosePrint("Bot: Considering moves around (" + co[0] + "," + co[1] + ")");
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
    private void dfs(Piece[][] board, int[] currentCoord, List<int[]> vi, int count){ //find the depth of the enemy's line.
        verboosePrint("DFS: "+currentCoord[0]+","+currentCoord[1]+" Depth = " + count + " EnemyColour: " + enemyCol);
        count++;
        List<int[]> neighbours = putListIntoMembers(board,currentCoord,enemyCol,enemyCol);
        verboosePrint("neighbours: " + printIntList(neighbours));
        vi.add(currentCoord);
        for (int[] v : neighbours){
            boolean check = true;
            for (int[] j : vi){
                if (Arrays.equals(v,j)){
                    check = false;
                }
            }
            if (check == true){
                    dfs(board,v,vi,count);
            } else {
                setEnemyMoveDepth(count);
            }
        }
    }
    private void setEnemyMoveDepth(int count){
        if (count > enemyMoveDepth){
            enemyMoveDepth = count;
        }
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
            verboosePrint("You won!");
        }
        else if (state == GameState.LOST){
            verboosePrint("You lost!");
        }
		return true;
    }
}