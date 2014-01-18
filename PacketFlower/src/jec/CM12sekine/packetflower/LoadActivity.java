package jec.CM12sekine.packetflower;

import java.io.File;

import jec.CM12sekine.packetflower.FileSelectDialog.OnFileSelectDialogListener;
import jec.CM12sekine.packetflower.packet.PacketDataController;
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
 
public class LoadActivity extends Activity implements OnFileSelectDialogListener {
	private Button button_Load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_load);
    	button_Load = ((Button)findViewById(R.id.button_Load)) ;
    	button_Load.setEnabled(false);

    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        getMenuInflater().inflate(R.menu.main, menu);
 
        return true;
    }
 
    /**
     * ファイル選択イベント
     */
    public void onClickFileSelectButton(View view) {
 
        // ファイル選択ダイアログを表示
        FileSelectDialog dialog = new FileSelectDialog(this,true,"");
        dialog.setOnFileSelectDialogListener(this);
 
        // 表示
        dialog.show(Environment.getExternalStorageDirectory().getPath());
    }
 
	public void onClickLoadButton(View view) {
		String filePath = ((EditText) findViewById(R.id.editText_filePath))
				.getText().toString();
		if (!new File(filePath).exists()||filePath.indexOf(".dump")==-1) {
			((TextView) findViewById(R.id.errorMessage)).setText("不正なファイルです");
		} else {
			PacketDataController p = PacketDataController.getInstance();
			p.setFile(filePath);
			try {
				p.update();
				Intent intent = new Intent(LoadActivity.this, GLActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
					
			} catch (Exception e) {
				
			}
		}

	}
    /**
     * ファイル選択完了イベント
     */
    @Override
    public void fileSelected(File file) {
 
        if (file != null) {
 
            // 選択ファイルを設定

            ((EditText)findViewById(R.id.editText_filePath)).setText (file.getPath() );
            button_Load.setEnabled(true);
        }else{
        	button_Load.setEnabled(false);
        }

    }
}