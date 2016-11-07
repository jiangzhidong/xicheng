package com.qbcbyb.xc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.qbcbyb.libandroid.msg.Msg;
import com.qbcbyb.libandroid.msg.Msg.Which;
import com.qbcbyb.xc.util.ApplicationMain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadUtils {
    private static final String IMG_AND_DB_TEMPNAME = "temp.zip";
    private final Context context;

    public DownloadUtils(Context context) {
        this.context = context;
    }

    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        editText.setHint("请输入资源的URL");
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String url = editText.getText().toString();
                    if (!Patterns.WEB_URL.matcher(url).find()) {
                        Msg.showInfo(context, "URL格式不正确，请重新输入！");
                        return;
                    }
                    startDownLoad(url);
                }
            }
        };
        builder.setTitle("资源同步");
        builder.setPositiveButton("下载", listener);
        builder.setNegativeButton("取消", listener);
        builder.setView(editText);
        builder.show();
    }

    public void startDownLoad(final String url) {
        final MyProgressDialogCustom progressDialog = new MyProgressDialogCustom();
        final File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            IMG_AND_DB_TEMPNAME);
        tempFile.deleteOnExit();
        new AsyncHttpClient().get(url, new RangeFileAsyncHttpResponseHandler(tempFile) {

            @Override
            public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
                boolean canRetry = arg3 != null && arg3.exists() && arg3.delete();
                if (canRetry) {
                    Msg.confirm(context, null, "下载失败，稍后重试！", "重试", "取消", new Msg.MsgCallback() {

                        @Override
                        public void callBack(Which arg0) {
                            if (arg0 == Which.POSITIVE) {
                                startDownLoad(url);
                            }
                        }
                    });
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, File arg2) {
                Msg.showInfo(context, "下载成功！");
                upZipFile(arg2);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                progressDialog.setProgress(bytesWritten * 100 / totalSize);
            }

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show("下载中。。。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }

        });
    }

    public void upZipFile(final File zipFile) {
        final MyProgressDialogCustom progressDialog = new MyProgressDialogCustom();
        new AsyncTask<Void,Integer,Void>() {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show("解压中。。。");
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                Msg.showInfo(context, "解压成功！");
            }

            @Override
            protected Void doInBackground(Void... params) {
                File unZipFold = new File(ApplicationMain.getInstance().getAppPath());
                try {
                    FileInputStream fis = new FileInputStream(zipFile);
                    if (fis != null) {
                        if (fis.available() > 0) {
                            int readCount = 0, totalCount = fis.available();
                            ZipInputStream zipIS = new ZipInputStream(fis);// zip文件
                            ZipEntry entry;
                            File targetFile;
                            File targetParent;
                            while ((entry = zipIS.getNextEntry()) != null) {
                                targetFile = new File(unZipFold, entry.getName());
                                if (entry.isDirectory()) {
                                    targetFile.mkdirs();
                                    continue;
                                }
                                targetParent = targetFile.getParentFile();
                                if (!targetParent.exists()) {
                                    targetParent.mkdirs();
                                }
                                FileOutputStream dbOutputStream = new FileOutputStream(targetFile);
                                int len;
                                byte[] buffer = new byte[2048];
                                // read (len) bytes into buffer
                                while ((len = zipIS.read(buffer)) != -1) {
                                    // write (len) byte from buffer at the
                                    // position 0
                                    dbOutputStream.write(buffer, 0, len);
                                    readCount += len;
                                    // 更新进度条
                                    publishProgress((int) Math.round(100d * readCount / totalCount));
                                }
                                dbOutputStream.flush();
                                dbOutputStream.close();
                                zipIS.closeEntry();
                            }
                            zipIS.close();
                        }
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private class MyProgressDialogCustom extends Dialog {
        private ProgressBar progressBar;
        private TextView textView;

        public MyProgressDialogCustom() {
            super(context, R.style.BasePopupDialogTheme);
            setCancelable(false);
            setContentView(R.layout.layout_dialog_progress);
            progressBar = (ProgressBar) findViewById(R.id.progress);
            textView = (TextView) findViewById(R.id.message);
            progressBar.setMax(100);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public void show(String text) {
            updateText(text);
            show();
        }

        public void updateText(String text) {
            textView.setText(text);
        }

        public void setProgress(int progress) {
            progressBar.setProgress(progress);
        }
    }
}
