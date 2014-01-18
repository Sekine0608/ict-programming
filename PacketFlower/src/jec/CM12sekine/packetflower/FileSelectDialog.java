package jec.CM12sekine.packetflower;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
 
/**
 * ファイル選択ダイアログ
 */
public class FileSelectDialog extends Activity implements OnClickListener {
 
    private Activity activity = null;
    private OnFileSelectDialogListener listener = null;
    private String extension = "";
    private boolean systemFileBlock = true; 
    private List<File> viewFileDataList = null;
    private List<String> viewPathHistory = null;
 
    public FileSelectDialog(Activity activity, boolean systemFileBlock, String extension) {
        this.activity = activity;
        this.extension = extension;
        this.systemFileBlock = systemFileBlock ;
        this.viewPathHistory = new ArrayList<String>();
    }
 
 
    @Override
    public void onClick(DialogInterface dialog, int which) {
 
        File file = this.viewFileDataList.get(which);
 
        // ディレクトリの場合
        if (file.isDirectory()) {
            show(file.getAbsolutePath() + "/");
        } else {
            this.listener.fileSelected(file);
        }
    }
 
    public void show(final String dirPath) {
 
        if (this.viewPathHistory.size() == 0 || !dirPath.equals(this.viewPathHistory.get(this.viewPathHistory.size() - 1))) {
            this.viewPathHistory.add(dirPath);
        }
 
        File[] fileArray = new File(dirPath).listFiles();
        List<String> nameList = new ArrayList<String>();
        if (fileArray != null) {
 
            Map<String, File> map = new HashMap<String, File>();
            for (File file : fileArray) {
            	if (systemFileBlock && file.getName().substring(0,1).equals(".") ){
            		continue ;
            	}
                if (file.isDirectory()) {
                	if(isEmptyDirectory(file)) continue ;
                    nameList.add(file.getName() + "/");
                    map.put(nameList.get(map.size()), file);
                } else if ("".equals(this.extension) || file.getName().matches("^.*" + this.extension + "$")) {
                    nameList.add(file.getName());
                    map.put(nameList.get(map.size()), file);
                }
            }
            Collections.sort(nameList);
            this.viewFileDataList = new ArrayList<File>();
            for (String name : nameList) {
                this.viewFileDataList.add(map.get(name));
            }
        }
 
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
        dialog.setTitle(dirPath);
        dialog.setItems(nameList.toArray(new String[0]), this);
 
        dialog.setPositiveButton("上 へ", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
 
                if (!"/".equals(dirPath)) {
                    String dirPathNew = dirPath.substring(0, dirPath.length() - 1);
                    dirPathNew = dirPathNew.substring(0, dirPathNew.lastIndexOf("/") + 1);
                    FileSelectDialog.this.viewPathHistory.add(dirPathNew);
                    show(dirPathNew);
                } else {
                    show(dirPath);
                }
            }
        });
        
        
        dialog.setNeutralButton("戻 る", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
                int index = FileSelectDialog.this.viewPathHistory.size() - 1;
                if (index > 0) {
                    FileSelectDialog.this.viewPathHistory.remove(index);
                    show(FileSelectDialog.this.viewPathHistory.get(index - 1));
 
                } else {
                    show(dirPath);
                }
            }
        });
 
        dialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
 
                FileSelectDialog.this.listener.fileSelected(null);
            }
        });
 
        dialog.show();
    }
    private boolean isEmptyDirectory(File filePath){
    	File[] targetFileArray = filePath.listFiles() ;
    	if(targetFileArray!=null){
                    if(targetFileArray.length == 0){
                            return true;
                    }else{
                        if(systemFileBlock){
                                            int systemFileCount = 0 ;
                            for(File file: targetFileArray){
                                    if (file.getName().substring(0,1).equals(".") ){
                                            systemFileCount++ ;
                                    }
                            }
                            if((targetFileArray.length - systemFileCount) == 0){
                                    return true ;
                            }
                        }
                    }
    	}
                            return false ;
    }
 
    public void setOnFileSelectDialogListener(OnFileSelectDialogListener listener) {
        this.listener = listener;
    }
 
    public interface OnFileSelectDialogListener {
        public void fileSelected(File file);
    }
}