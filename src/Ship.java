public class Ship 
{
    public int [] xPositions;
    public int [] yPositions;
    private int length;
    public int isAlive;
    public Ship(int Xstart,int Ystart,String alignment,int length)
    {
        isAlive=1;
        xPositions=new int[length];
        yPositions = new int[length];
        for (int i=0;i<length;i++) 
        {
            if(alignment.equals("hor"))
            {
                xPositions[i] = Xstart;
                yPositions[i] = Ystart + i;
            }
            else
            {
                xPositions[i] = Xstart + i;
                yPositions[i] = Ystart;
            }
        }
    }
}
