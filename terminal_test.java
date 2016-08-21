public class terminal_test
{
    public static void main(String[] args)
    {
        HumanPlayer p2 = new HumanPlayer();
        // HumanPlayer p1 = new HumanPlayer();
        // ComputerPlayer_cmkv68 p2 = new ComputerPlayer_cmkv68();
        ComputerPlayer_cmkv68 p1 = new ComputerPlayer_cmkv68();
        // // ComputerPlayer_cmkv68 p3 = new ComputerPlayer_cmkv68();
        // p2.setDebug(true); // verboose mode for bot
        // p1.setDebug(true); // verboose mode for bot
        GameManager hex = new GameManager();
        try {
            hex.boardSize(8,8);
            // hex.boardSize(1,1);
        }catch (InvalidBoardSizeException e){
            System.out.println("The board is can't be sized with less than 1 for values.");
        }
        catch (BoardAlreadySizedException e){
            System.out.println("The board is already sized.");
        }
        try {
            hex.specifyPlayer(p1, Piece.RED);
        }
        catch (ColourAlreadySetException e){
            System.out.println("A colour is already set to that player.");
        }
        catch(InvalidColourException e){
            System.out.println("That's an invalid colour choice.");
        }
        try {
            hex.specifyPlayer(p2, Piece.BLUE);
        }
        catch (ColourAlreadySetException e){
            System.out.println("A colour is already set to that player.");
        }
        catch(InvalidColourException e){
            System.out.println("That's an invalid colour choice.");
        }
        hex.playGame();
    }
}

