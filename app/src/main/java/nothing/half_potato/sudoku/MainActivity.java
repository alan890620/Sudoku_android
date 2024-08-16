package nothing.half_potato.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences preference ;
    public EditText num ;
    public Button read ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num = findViewById( R.id.editTextPhone);
        read = findViewById( R.id.button_read );

        preference = getSharedPreferences( "LastNum" , MODE_PRIVATE);
        ReadLastNum();
        updateButten();
    }
    private void updateButten()
    {
        if ( !checkFile() )
            read.setVisibility( View.INVISIBLE );
        else
            read.setVisibility( View.VISIBLE );
    }

    private void ReadLastNum ()
    {
        int buff = preference.getInt( "Num" , -1 ) ;
        if ( buff >= 1 && buff <= 55 )
            num.setText(  Integer.toString( buff ) );
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode , Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        updateButten();
    }

    public void bt_click(View view)
    {
        preference.edit().clear().putInt( "Num" ,  Integer.valueOf( num.getText().toString() ) ).commit() ;
        if ( checkFile() )
            checkMessenge();
        else
            creatGame();
    }
    private void creatGame ()
    {
        Intent intent = new Intent();
        intent.setClass( MainActivity.this , GameActivity.class );
        intent.putExtra( "level" , Integer.valueOf(num.getText().toString()) );
        startActivities(new Intent[]{intent});
    }
    private void checkMessenge()
    {
        boolean cm ;
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle("確定要繼續?");
        builder.setMessage("偵測到還有正在進行的遊戲，如果繼續將會覆蓋紀錄") ;
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                creatGame();
            }
        });
        builder.show();
    }

    public void bt2_click(View view)
    {
        if ( checkFile() )
        {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GameActivity.class);
            intent.putExtra("level", -1);
            startActivities(new Intent[]{intent});
        }
    }

    private boolean checkFile() //確定存檔完整性
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
        }
        catch ( Exception e )
        {
            return false ;
        }
        return true;
    }
}