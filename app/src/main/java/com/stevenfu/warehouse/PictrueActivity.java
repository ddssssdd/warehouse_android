package com.stevenfu.warehouse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stevenfu.warehouse.network.UploadFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictrueActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REQUEST_CODE_PICK_PIC_KITKAT = 1;
    public static final int REQUEST_CODE_PICK_PIC = 2;
    public static final int REQUEST_CODE_CAMERA = 3;
    private ImageView mImageView;
    private Button mButtonSelectPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictrue);
        mButtonSelectPicture = (Button) findViewById(R.id.buttonSelectPicture);
        mButtonSelectPicture.setOnClickListener(this);

        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_PIC) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                //mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                mImageView.setImageBitmap(BitmapFactory.decodeByteArray(UploadFile.decodeBitmap(picturePath), 0, UploadFile.decodeBitmap(picturePath).length));
                mImageView.setTag(picturePath);
                doUpload(picturePath);
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                if (mPicUri != null) {
                    mImageView.setImageURI(mPicUri);
                    mImageView.setTag(mPicUri.getPath());
                    doUpload(mPicUri.getPath());
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private  void doUpload(String file){
        UploadFile uf = new UploadFile();
        uf.DoTask(file);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelectPicture:
                showPickPictureDialog();
                break;
        }
    }

    private void showPickPictureDialog() {
        String[] items = { "Camera" , "Photo Library", "Clear" };
        AlertDialog dialog = new AlertDialog.Builder(this).
                setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickPictureByCamera();
                                break;
                            case 1:
                                pickPicture();
                                break;
                            case 2:
                                clearPicture();
                                break;
                        }
                    }
                }).create();
        dialog.show();
    }

    private void pickPicture() {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        Intent intent = new Intent();
//        if (isKitKat) {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            startActivityForResult(intent, REQUEST_CODE_PICK_PIC_KITKAT);
//        } else {
        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_PIC);
//        }
    }

    private Uri mPicUri;

    private void pickPictureByCamera() {
        String strImgPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
        String strImgName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + ".jpg";
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, strImgName);
        mPicUri = Uri.fromFile(out);
        System.out.println("uri:" + mPicUri);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void clearPicture() {
        mImageView.setImageBitmap(null);
        mImageView.setTag("");
    }
}
