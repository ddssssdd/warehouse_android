package com.stevenfu.warehouse.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.stevenfu.warehouse.MainActivity;
import com.stevenfu.warehouse.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateAppService extends Service {
    private Context context;
    private Notification notification;
    private NotificationManager nManager;
    private PendingIntent pendingIntent;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        CreateInform();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    public void SendNotification(String title, String message)
    {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.side_nav_bar)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }
    //创建通知
    public void CreateInform() {
        SendNotification("Download","Begin to download...");
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程
    }
    class updateRunnable implements Runnable{
        int downnum = 0;//已下载的大小
        int downcount= 0;//下载百分比
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                DownLoadApp("http://10.4.30.60:8000/warehouse/uploads/new_version.apk");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public void DownLoadApp(String urlString) throws Exception{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int length = urlConnection.getContentLength();
            InputStream inputStream = urlConnection.getInputStream();
            File apkFile = getFile();
            OutputStream outputStream = new FileOutputStream(apkFile);

            byte buffer[] = new byte[1024*3];
            int readsize = 0;
            while((readsize = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, readsize);
                downnum += readsize;
                if((downcount == 0)||(int) (downnum*100/length)-1>downcount){
                    downcount += 1;
                    SendNotification("正在下载", "已下载了"+(int)downnum*100/length+"%");

                }
                if (downnum==length) {
                    SendNotification("下载结束", "开始安装");

                }
            }
            inputStream.close();
            outputStream.close();
            InstallApk(apkFile);
        }
        //获取文件的保存路径
        public File getFile() throws Exception{
            String SavePath = getSDCardPath() + "/App";
            File path = new File(SavePath);
            File file = new File(SavePath + "/Warehouse_android.apk");
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        }
        //获取SDCard的目录路径功能
        private String getSDCardPath() {
            File sdcardDir = null;
            // 判断SDCard是否存在
            boolean sdcardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (sdcardExist) {
                sdcardDir = Environment.getExternalStorageDirectory();
            }
            return sdcardDir.toString();
        }
        private void InstallApk(File file)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
            // android.os.Process.killProcess(android.os.Process.myPid());
            startActivity(intent);
        }
    }
}
