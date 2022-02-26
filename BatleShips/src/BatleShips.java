import java.io.File;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class BatleShips 
{
    public static void main(String[] args) throws Exception
    {
        gameManager();
    }
    //====================================================================================================================
    public static void gameManager() throws Exception
    {
        intro();
        char [][] player1Board = new char[10][10];
        init(player1Board);
        char [][] player2Board = new char[10][10];
        init(player2Board);
        int[] dif = difficultyPicker();
        LinkedList <Ship> playerShips = new LinkedList<Ship>(playerShipPlacement(player1Board, dif[0]));
        LinkedList <Ship> enemyShips = new LinkedList<Ship>(enemyShipPlacement(dif[0], dif[1]));
        byte[][] enemyMoves = enemyMoves();
        byte gameSituation;
        int round = 0;
        while(true)
        {
            if(round>=100)
            {
                System.out.println("the game has gone for too long...\nyou lose by default...");
                break;
            }
            gameSituation = roundCheck(playerShips, enemyShips);
            if(gameSituation==0)
            {
                StandbyPhase(player1Board, player2Board);
                playerTurn(player2Board, enemyShips);
            }
            gameSituation = roundCheck(playerShips, enemyShips);
            if(gameSituation == 1)
            {
                System.out.println("you destroyed all of the enemy ships...\nyou win...");
                break;
            }
            else if(gameSituation == -1)
            {
                System.out.println("all of your ships got destroyed by the enemy...\nyou lose...");
                break;
            }
            round = enemyTurn(player1Board, playerShips, enemyMoves, round);
        }
    }
    //====================================================================================================================
    public static void intro()
    {
        System.out.println("Welcome to battleships...");
        System.out.println("your goal is to find and sink the enemy ships before they do...");
        System.out.println("right now its only built to handle singleplayer gameplay...");
        System.out.println("though do not expect me to update it, it was already a pain to make it like that with whatever resources available XD...");
        System.out.println("enjoy the game...");
        System.out.println("PS: not all of out of range values have been handled in the code so read carefully before you input...");
        System.out.println("");
        System.out.println("");
    }
    //====================================================================================================================
    public static void init(char[][] board)
    {
        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 10; j++) 
            {
                board[i][j]='-';
            }
        }
    }
    //====================================================================================================================
    public static int[] difficultyPicker()
    {
        int[] values = new int[2];
        Scanner input = new Scanner(System.in);
        while(true)
        {
            System.out.println("how many ships you plan to play with/against(2-5)?");
            values[0] = input.nextInt();
            if(values[0]>=2 && values[0]<=5)
                break;
            System.out.println("ship count is invalid, pick from 2-5...");
        }
        while(true)
        {
            System.out.println("how hard is the enemy on a scale from 1-4? (effects the length of enemy ships)"); 
            values[1] = input.nextInt();
            if(values[1]>=1 && values[1]<=4)
                break;
            System.out.println("enemy difficuly is invalid, pick from 1-4...");
        }
        return values;
    }
    //====================================================================================================================
    public static LinkedList<Ship> playerShipPlacement(char[][]board, int shipcount)
    {
        LinkedList <Ship> ships = new LinkedList<Ship>();
        Scanner input = new Scanner(System.in);
        System.out.println("place your ships...");
        System.out.println("input x,y,alignment(as hor OR ver) of each ship in a space seperated manner...");
        System.out.println("PS: all ships have a fixed length of 3");
        int posx,posy;
        String align;
        for(int i =0;i<shipcount;i++)
        {
            System.out.printf("%d ships are still in need of placement...\n",shipcount-i);
            posx = input.nextInt();
            posy = input.nextInt();
            align = input.next();
            if(align.equals("hor"))
            {
                if(posx<0||posx>9||posy<0||posy>7)
                {
                    System.out.println("wrong placement");
                    i--;
                    continue;
                }
                if(board[posx][posy]!='%'&&board[posx][posy+1]!='%'&&board[posx][posy+2]!='%')
                {
                    board[posx][posy++] = '%'; 
                    board[posx][posy++] = '%'; 
                    board[posx][posy] = '%';
                    ships.addFirst(new Ship(posx,posy-2,align,3));
                }
                else
                {
                    System.out.println("place taken...");
                    i--;
                }
            }
            else if(align.equals("ver"))
            {
                if(posx<0||posx>7||posy<0||posy>9)
                {
                    System.out.println("wrong placement");
                    i--;
                    continue;
                }
                if(board[posx][posy]!='%'&&board[posx+1][posy]!='%'&&board[posx+2][posy]!='%'){
                    board[posx++][posy] = '%'; 
                    board[posx++][posy] = '%'; 
                    board[posx][posy] = '%';
                    ships.addFirst(new Ship(posx-2,posy,align,3));
                }
                else
                {
                    System.out.println("place taken...");
                    i--;
                }
            }
            else
                {
                    System.out.println("wrong alignment");
                    i--;
                }
        }
        return ships;
    }
    //====================================================================================================================
    public static LinkedList<Ship> enemyShipPlacement(int shipcount, int length)throws Exception
    {
        LinkedList<Ship> ships = new LinkedList<>();
        String path = String.format("enemyDifficulties%c%d%c%d.in", File.separatorChar, shipcount, File.separatorChar, length);
        Scanner input = new Scanner(new File(path));
        int x,y;
        String alignment;
        for (int i = 0; i < shipcount; i++) 
        {
            x = input.nextInt();
            y = input.nextInt();
            alignment = input.next();
            ships.addFirst(new Ship(x, y, alignment, 6 - length));
        }
        input.close();
        return ships;
    }
    //====================================================================================================================
    public static void StandbyPhase(char[][]playerBoard, char[][]enemyBoard)
    {
        System.out.println("  0 1 2 3 4 5 6 7 8 9 \t  0 1 2 3 4 5 6 7 8 9");
        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 11; j++) 
            {
                if(j == 0)
                    System.out.print(i);
                else
                    System.out.print(" "+playerBoard[i][j-1]);
            }
            System.out.print("\t");
            for (int j = 0; j < 11; j++) 
            {
                if(j == 0)
                    System.out.print(i);
                else
                    System.out.print(" "+enemyBoard[i][j-1]);
            }
            System.out.println();
        }
    }
    //====================================================================================================================
    public static byte[][] enemyMoves()
    {
        ArrayList<Byte> moves = new ArrayList<Byte>(100);
        for (byte i = 0; i < 100; i++)
            moves.add(i);
        Random r = new Random(System.currentTimeMillis()/2521);
        Collections.shuffle(moves,r);
        byte[][] shuffled = new byte[2][100];
        int x,y;
        for (byte i = 0; i < 100; i++) 
        {
            if(moves.get(i)>=10)
            {
                x = moves.get(i)/10;
                y = moves.get(i)%10;
            }
            else
            {
                x = 0;
                y = moves.get(i);
            }
            shuffled[0][i] = (byte)x;
            shuffled[1][i] = (byte)y;
        }
        return shuffled;
    }
    //====================================================================================================================
    public static void playerTurn(char[][] enemyBoard, LinkedList<Ship> enemyShips)
    {   
        Scanner input = new Scanner(System.in);
        System.out.println("choose a place to attack in \"X Y\" on the enemy board...");
        int x,y;
        while(true)
        {
            x = input.nextInt();
            y = input.nextInt();
            if(x<0||x>9||y<0||y>9)
            {
                System.out.println("chosen plcae is not on the board, try again...");
                continue;
            }
            if(enemyBoard[x][y]=='-')
            {
                enemyBoard[x][y]='X';
                for(Ship s:enemyShips)
                {
                    if(s.isAlive==0)
                        continue;
                    for (int i = 0; i < s.xPositions.length; i++) 
                    {
                        if(x==s.xPositions[i]&&y==s.yPositions[i])
                        {
                            System.out.println("enemy ship was hit and destroyed...");
                            s.isAlive=0;
                            for (int j = 0; j < s.xPositions.length; j++) 
                                enemyBoard[s.xPositions[j]][s.yPositions[j]]='#';
                        }
                    }
                }
                return;
            }
            else if(enemyBoard[x][y]=='X')
            {
                System.out.println("place has already been hit, try somewhere else...");    
            }
            else
                System.out.println("that ship has already been destroyed, try somewhere else...");
        }
    }
    //====================================================================================================================
    public static int enemyTurn(char[][] playerBoard, LinkedList<Ship> playerShips, byte[][] moves, int round)
    {
        byte x,y;
        while(true)
        {
            x = moves[0][round];
            y = moves[1][round];
            if(playerBoard[x][y]=='-' || playerBoard[x][y]=='%')
            {
                playerBoard[x][y]='X';
                round++;
                for(Ship s:playerShips)
                {
                    if(s.isAlive==0)
                        continue;
                    for (int i = 0; i < s.xPositions.length; i++) 
                    {
                        if(x==s.xPositions[i]&&y==s.yPositions[i])
                        {
                            System.out.println("player ship was hit and destroyed...");
                            s.isAlive=0;
                            for (int j = 0; j < s.xPositions.length; j++) 
                                playerBoard[s.xPositions[j]][s.yPositions[j]]='#';
                        }
                    }
                }
                return round;
            }
            else
                round++;
        }
    }
    //====================================================================================================================
    public static byte roundCheck(LinkedList<Ship> playerShips, LinkedList<Ship> enemyShips)
    {
        int p = playerShips.size(), e = enemyShips.size();
        for(Ship s: playerShips)
            if(s.isAlive==0)
                p--;
        if(p==0)
            return -1;
        
        for(Ship s: enemyShips)
            if(s.isAlive==0)
                e--;
        if(e==0)
            return 1;
        
        return 0;
    }
}