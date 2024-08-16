package nothing.half_potato.sudoku;

import java.util.Random;
import java.util.Date;
public class sudoku
{
    private int n, m , level , size ;
    private int[][] game , ans , qs; //遊戲中數獨 答案 題目
    //Random rand = new Random(System.currentTimeMillis());

    public sudoku(int n, int m, int level) //初始建構子 (目前只能3*3)
    {
        this.n = n;
        this.m = m;
        this.size = m * n;
        this.level = level;
        //this.game = new int[n * m, m * n];
        do
            this.ans = sudoku_sub();
        while (ans == null);
        game = makegame();
        qs = copyarr(game);
        //*/
    }
    public sudoku(int n, int m, int level , int[][] game , int[][] ans , int[][] qs ) //讀檔用建構子
    {
        this.n = n;
        this.m = m;
        this.size = m * n;
        this.level = level;
        this.game = game;
        this.ans = ans;
        this.qs = qs;
    }
    public boolean inputAns( int x , int y , int n ) //輸入答案 ( 位置x , 位置y , 數字)
    {

        if (n > size || n < 0)	//防呆
            return false;
        if (x > size || x < -1)	//防笨
            return false;
        if (y > size || y < -1)	//防傻
            return false;
        if (qs[x][y] != 0)		//防低能
            return false;
        //*/
        game[x][y] = n;
        return true;
    }
    public int [] chickWin() //回傳各值數量與是否勝利 //第一個值為0即為勝利
    {
        int[] ch = new int[size + 1];
        int ts = size * size;
        for (int t = 0; t != ts; t++)
        {
            ch[game[t / size][ t % size]]++;
            if ( game[t / size][ t % size] != ans[t / size][ t % size])
                ch[ 0 ]++;
        }
        return ch;
    }
    public int[][] readgame() //讀取正在進行的數獨
    {
        return game;
    }
    public int[][] readans() //讀取答案
    {
        return ans;
    }
    public int[][] readqs() //讀取題目
    {
        return qs;
    }
    public boolean[][] EDAC() //偵測錯誤
    {
        boolean[][] ErrorMatrix = new boolean[size][size];
        for ( int t = 0 ; t != size*size ; t ++ )
        {
            int buff = game[t / size][t % size];
            game[t / size][t % size] = 0 ;
            ErrorMatrix[t / size][t % size] = isTrue(game, t, buff);
            game[t / size][t % size] = buff ;
        }
        return ErrorMatrix ;
    }
    public int[] readdata() //讀取資料 ( )
    {
        return new int[] { n, m, level, size };
    }

    //------------------------------------------------後端---------------------------------------------------------
    private boolean isTrue(int[][] arr, int xy, int num) //檢查是否合理
    {
        if (xy >= 81)
            return false ;

        if (arr[xy / size][ xy % size] != 0)
            return false;

        for (int t = 0; t != size; t++)
            if (arr[(xy / size / 3) * 3 + t / 3][ (xy % size / 3) * 3 + t % 3] == num)
                return false;

        for (int t = 0; t != size; t++)
        {
            if (xy % size != t)
                if (arr[xy / size][ t] == num)
            return false;
            if (xy / size != t)
                if (arr[t][ xy % size] == num)
            return false;
        }
        return true;
    }

    private int[][] sudoku_sub() //模組化生成數獨
    {
        int[][] arr = new int[ size ][ size];
        int[] xy = new int[size];
        int xyb, res;
        for (int t = 0; t != size * size; t++)
            arr[t / size][ t % size] = 0;

        for (int t = 0; t != size; t++)
        {
            for (int t2 = 0; t2 != size; t2++)
                xy[t] = -1;

            for (int t2 = 0; t2 != size; t2++)
            {
                res = 0;
                boolean s = false;
                xyb = (int)( Math.random() * (size) );
                do
                {
                    if (res > size)                             //用下面的方法這裡要改大,上面的用9就可
                    {
                        //cout << t << " " << t2 << endl;
                        return null;
                    }
                    xyb++; if (xyb > size - 1) xyb = 0;         //上下兩種二擇一
                    //xyb = rand() % 9;							//此為另一種方法
                    //s = !isTrue(arr, ((xyb / m) + (t2 / n * m)) * size + (xyb % n) + (t2 % m * n), t + 1);
                    s = !isTrue(arr, (xyb / n * size) + (xyb % n) /*內格*/ + (t2 / n * m) * size + (t2 % m * n) /*外格*/, t + 1);
                    res++;
                } while (s);
                xy[t2] = xyb;
                arr[(xyb / m) + (t2 / n * m)][ (xyb % n) + (t2 % m * n)] = t + 1;
            }
        }
        return arr;
    }
    private int[][] makegame() //挖空格
    {
        int[][] arr = new int[n*m][m*n];
        do
        {
            arr = copyarr (ans);
            for (int t = 0; t != level;)
            {
                int r = (int)( Math.random() * 81 );
                if (arr[r / 9][r % 9] != 0)
                {
                    t++;
                    arr[r / 9][r % 9] = 0;
                }
                //cout << t << endl ;
            }
        }
        while ( asSuduku( arr , 0 , 0 ) > 2 );
        return arr;
    }
    private int[][] copyarr( int[][] arr ) //複製陣列
    {
        int[][] newArr = new int[size][ size];
        for ( int t = 0; t != size; t++)
            for ( int t2 = 0; t2 != size; t2 ++ )
            {
                newArr[t][ t2] = arr[t][ t2];
            }
        return newArr;
    }

    private int asSuduku(int[][] arr, int xy, int asn) //檢查唯一解
    {
        //cout << xy << endl ;
        if (xy >= 81)
        {
            //asn += 1;
            //draw(arr, 9, 3);
            return asn + 1;
        }
        if (arr[xy / 9][xy % 9] != 0)
            asn = asSuduku(arr, xy + 1, asn);
        else
            for (int t = 1; t != 10; t++)
            {
                if (isTrue(arr, xy, t))
                {
                    arr[xy / 9][xy % 9] = t;
                    asn = asSuduku(arr, xy + 1, asn);
                    arr[xy / 9][xy % 9] = 0;
                    if (asn > 1)
                        return asn;
                }
            }
        //draw(arr, 9, 3);
        //cout << "error" << endl;
        return asn;
    }
}
