public class Move implements MoveInterface{

    private int x;
    private int y;
    private boolean moveIsConceeded;

	public Move(){
        moveIsConceeded = false;
	}

    public boolean setPosition(int x, int y) throws InvalidPositionException{
        boolean check = false;
        if (x < 0 || y < 0){
            // check = false;
            throw new InvalidPositionException();
        } else {
            this.x = x;
            this.y = y;
            check = true;
        }
    	return check;
    }

    public boolean hasConceded()
    {//done
    	return moveIsConceeded;
    }

    public int getXPosition()
    {//done
    	return x;
    }
    public int getYPosition()
    {//done
    	return y;
    }
    public boolean setConceded()
    {//done
        moveIsConceeded = true;
    	return true;
    }
}