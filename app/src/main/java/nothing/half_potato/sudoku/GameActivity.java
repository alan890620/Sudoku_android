package nothing.half_potato.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class GameActivity extends AppCompatActivity
{
    private sudoku game ;
    private Button[] nums = new Button[81] , inputBt = new Button[9];
    private Button winButton ;
    private TextView[] Count = new TextView[9] ;
    private TextView winText ;
    private int select = -1;
    private AppCompatActivity main ;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) //初始化
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int vWidth = dm.widthPixels;
        int ButtonSize = vWidth / 9 ;
        setResult( RESULT_OK , new Intent());

        for ( int t = 0 ; t != 81 ; t ++ ) //匯入盤面
        {
            nums[t] = findViewById(getResources().getIdentifier("button" + t, "id", this.getPackageName()));
            ViewGroup.LayoutParams size = nums[t].getLayoutParams();
            size.height = ButtonSize;
            size.width  = ButtonSize;
            nums[t].setLayoutParams( size );
        }
        for ( int t = 0 ; t != 9 ; t ++ ) //匯入按鈕與計數器
        {
            inputBt[t] = findViewById(getResources().getIdentifier("button_num" + (t + 1), "id", this.getPackageName()));
            Count[t] = findViewById(getResources().getIdentifier("textView" + (t + 1), "id", this.getPackageName()));
        }
        winButton = findViewById( R.id.button_win);
        winText = findViewById( R.id.winText) ;

        int level = getIntent().getIntExtra( "level" , 30 );
        if ( level > 0 )
        {
            long timest = System.nanoTime();
            game = creat(level);
            long timeend = System.nanoTime();
            Toast.makeText(this ,"生成完成，挖空了" +  game.readdata()[2]  + "格\n共花費" + ( timeend - timest ) + "ns" , Toast.LENGTH_LONG).show();
        }
        else
        {
            game = read();
            //Toast.makeText(this ,"已讀取進度", Toast.LENGTH_LONG).show();
        }
        show ( );
    }
    private void show ( ) //顯示
    {
        boolean [][] Error = game.EDAC() ;
        int[][] qs = game.readqs() , rgame = game.readgame();
        int[] count = game.chickWin();
        for ( int t = 0 ; t != 81 ; t ++ )
        {
            if ( rgame[t / 9][t % 9] != 0 )
            {
                nums[t].setText(Integer.toString(rgame[t / 9][t % 9]));
                if ( qs[t / 9][t % 9] != 0 ) //顯示題目區域
                    nums[t].setTypeface( nums[t].getTypeface() , Typeface.BOLD);
                else
                    if ( !Error[t / 9][ t % 9 ] ) //顯示錯誤
                        nums[t].setTextColor( Color.parseColor("#FF0000")  );
                    else
                        nums[t].setTextColor( Color.parseColor("#000000")  );
            }
            else
                nums[t].setText("");
        }
        for ( int t = 0 ; t != 9 ; t++ )
        {
            Count[t].setText( Integer.toString( count[t+1] ));
            if ( count[t+1] > 9 )
                Count[t].setTextColor( Color.parseColor( "#FF0000" ));
            else
                Count[t].setTextColor( Color.parseColor( "#000000" ));
        }
        if ( count[0] == 0 )
        {
            winButton.setVisibility( View.VISIBLE );
            winText.setVisibility( View.VISIBLE );
        }
        else
        {
            winButton.setVisibility( View.INVISIBLE );
            winText.setVisibility( View.INVISIBLE );
        }

    }
    public void bt_G ( View view) //選取方格
    {
        for (int t = 0; t != 81; t++)
            if (view.getId() == nums[t].getId())
            {
                nums[t].setBackgroundColor(Color.parseColor("#E9E9E9"));
                select = t;
            }
            else
            {
                if (((t / 9 / 3) + (t % 9 / 3)) % 2 == 0)
                    nums[t].setBackgroundColor(Color.parseColor("#FFFFFF"));
                else
                    nums[t].setBackgroundColor(Color.parseColor("#E0FFFF"));
            }
    }
    public void bt_num ( View view ) //輸入數字
    {
        int inNum = 0 ;
        if ( select > -1 && select < 81 )
        {
            for (int t = 0; t != 9; t++)
                if (view.getId() == inputBt[t].getId())
                {
                    inNum = t + 1;
                    break;
                }
            if ( game.inputAns(select / 9, select % 9, inNum ))
                show();
        }
    }
    @Override
    protected void onDestroy() //結束 記得存檔(贏了就不用)
    {
        super.onDestroy();
        if ( game.chickWin()[0] == 0 )
            deleteFile();
        else
            save();
        //setResult( RESULT_OK , new Intent());
        //finish();
    }
    private void save () //存檔
    {
        int[] data = game.readdata();
        int[][][] matrix = new int [][][] { game.readgame() , game.readans() , game.readqs() } ;//遊戲中數獨 答案 題目
        try
        {
            FileOutputStream fout = openFileOutput( "Sudoku_Save.txt" , MODE_PRIVATE  );
            BufferedOutputStream buffout = new BufferedOutputStream( fout ) ;
            for ( int t = 0 ; t != data.length ; t++ )
                buffout.write( (data[t] + "\n" ).getBytes() );
            for ( int t = 0 ; t != 3 ; t++ )
            {
                for ( int t2 = 0 ; t2 != 81 ; t2 ++ )
                    buffout.write((matrix[t][t2 / 9][t2 % 9] + " ").getBytes());
                buffout.write(("\n").getBytes());
            }
            buffout.write( ( "_" ).getBytes() ); //代表結束
            buffout.close();
        }
        catch ( Exception e )
        {
            Toast.makeText(this ,"寫入失敗", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }
    private sudoku read() //讀檔
    {
        int flag = 0 ;
        int data[] = new int[4] ;
        int[][][] matrix = new int[3][9][9];
        String buffer = "" , buff = "" ;

        try
        {
            FileInputStream fin = openFileInput("Sudoku_Save.txt");
            BufferedInputStream buffin = new BufferedInputStream(fin);
            for ( char t = (char)buffin.read(); t != '_' ; t = (char)buffin.read() ) //轉錄至占存
                buffer += t ;
            buffin.close();
            for ( int t = 0 ; t != 4 ; t ++ )
            {
                buff = "" ;
                while ( buffer.charAt(flag++) != '\n')
                    buff+= buffer.charAt(flag-1);
                data[t] = Integer.parseInt( buff ) ;
            }
            for ( int t = 0 ; t != 3 ; t ++)
            {
                for (int t2 = 0; t2 != data[3] * data[3]; t2++)
                {
                    buff = "";
                    while (buffer.charAt(flag) != '\n' && buffer.charAt(flag++) != ' ')
                        buff += buffer.charAt(flag - 1);
                    matrix[t][t2 / data[3]][t2 % data[3]] = Integer.parseInt(buff);
                }
                flag ++ ;
            }
            Toast.makeText(this ,"已讀取進度", Toast.LENGTH_LONG).show();
        }
        catch ( Exception e )
        {
            //Toast.makeText(this ,"讀取失敗\n" + buffer + "\n" + flag, Toast.LENGTH_LONG).show();
            Toast.makeText(this ,"讀取失敗", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        return new sudoku( data[0] , data[1] , data[2] , matrix[0] , matrix[1] , matrix[2] );
    }
    private sudoku creat( int level ) //正常生成
    {
        return new sudoku( 3 , 3 , level ) ;
    }
    private void deleteFile () //刪除存檔
    {
        try
        {
            FileOutputStream fout = openFileOutput("Sudoku_Save.txt", MODE_PRIVATE);
            BufferedOutputStream buffout = new BufferedOutputStream( fout ) ;
            buffout.write( ( "_" ).getBytes() );
            buffout.close();
        }
        catch ( Exception e) {}
    }
    public void bt_Win(View view)
    {
        onBackPressed();
    }
}
