package jec.CM12sekine.packetflower;
import java.io.File;

import jec.CM12sekine.packetflower.FileSelectDialog.OnFileSelectDialogListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class MainActivity extends Activity { 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        getMenuInflater().inflate(R.menu.main, menu);
 
        return true;
    }
    public void onStartButton(View view){
        Intent intent = new Intent(MainActivity.this, LoadActivity.class);
        // 次画面のアクティビティ起動
        startActivity(intent);
    }
}