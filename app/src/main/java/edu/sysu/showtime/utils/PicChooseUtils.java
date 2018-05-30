package edu.sysu.showtime.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;

import java.io.File;
import java.io.IOException;

import edu.sysu.showtime.widget.PicChooseDialog;

public class PicChooseUtils {
    public static final int FROM_ALBUM = 1;
    public static final int FROM_CAMERA = 2;
    public static final int DO_CROP = 3;
    public PicChooseDialog dialog = null;

    private Activity activity = null;
    private android.support.v4.app.Fragment fragment = null;

    private String picType;         //图片的类型，是个人信息页的头像图片，还是直播时的封面图片
    private Uri storagePathUri;     //剪裁后的图片保存到的SD卡中的uri路径
    private Uri cameraUri;          //相机拍照完后获取的图片存放到的Uri

    private int chooseType = 2;     //默认选择了相册

    public PicChooseUtils(Activity activity, String picType){
        this.activity = activity;
        this.picType = picType;
    }

    public PicChooseUtils(android.support.v4.app.Fragment fragment, String picType){
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.picType = picType;
    }

    //打开相册对话框工具类
    public void showPicChooseDialog(){
        if(dialog == null){
            dialog = new PicChooseDialog(activity);
        }
        dialog.setOnDialogClickListener(new PicChooseDialog.OnDialogClickListener() {
            @Override
            public void onAlbum() {     //选择从相册获取
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if(fragment != null){
                    fragment.startActivityForResult(intent, FROM_ALBUM);
                } else if(activity != null){
                    activity.startActivityForResult(intent, FROM_ALBUM);
                }
                chooseType = 2;
            }
            @Override
            public void onCamera() {    //选择拍照获取
                choosePicFromCamera();
                chooseType = 1;
            }
        });
        dialog.showDialogOnBottom();
    }

    //创建用于保存图片到的File
    private File createImageFile() throws IOException {
        String fileName = "";
        if(picType == "headPic"){
            fileName = System.currentTimeMillis() + "_headPic";
        } else if(picType == "coverPic"){
            fileName = System.currentTimeMillis() + "_coverPic";
        }
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        return image;
    }

    private void choosePicFromCamera() {
        cameraUri = getOutputUriPath("camera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {   //大于7.0的版本
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    cameraUri = FileProvider.getUriForFile(activity, "edu.sysu.showtime.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(fragment != null){
            fragment.startActivityForResult(intent, FROM_CAMERA);
        } else if(activity != null){
            activity.startActivityForResult(intent, FROM_CAMERA);
        }
    }

    public void getReturnResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CAMERA) {
            //从拍照完成返回。
            if (resultCode == Activity.RESULT_OK) {
                startCrop(cameraUri);
            }
        } else if (requestCode == FROM_ALBUM) {
            //从相册选择返回。
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                startCrop(uri);     //选择了某张图片之后，打开剪裁图片界面对图片做剪裁
            }
        } else if (requestCode == DO_CROP) {
            //裁剪结束
            if (resultCode == Activity.RESULT_OK) {
                //剪裁成功后，通知系统图库更新，
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(storagePathUri.getPath()))));
                //并上传剪裁后的图片到七牛云服务器保存起来
                uploadTo7Niu(storagePathUri.getPath());
            }
        } else{
            Log.i("test", "返回结果失败");
        }
    }

    //用于给EditProfileActivity设置监听器，
    //用于监听该监听器中的onSuccess或者onFail方法被调用时回调
    public interface OnChooseResultListener {
        void onSuccess(String url);
        void onFail(String msg);
    }
    private OnChooseResultListener listener;

    public void setOnChooseResultListener(OnChooseResultListener l) {
        listener = l;
    }

    private void uploadTo7Niu(String path) {
        String name = System.currentTimeMillis() + "_headPic.jpg";
        QiNiuUpLoadUtils.uploadPic(path, name, new QiNiuUpLoadUtils.UploadCallBack() {
            @Override
            public void success(String url) {    //上传成功，回调告诉EditProfileActivity
                if (listener != null) {
                    listener.onSuccess(url);
                }
            }
            @Override
            public void fail(String key, ResponseInfo info) {   //上传失败，回调告诉EditProfileActivity
                if (listener != null) {
                    listener.onFail(info.error);
                }
            }
        });
    }

    //剪裁后图片输出到的Uri或者拍照后保存图片到的Uri
    private Uri getOutputUriPath(String type) {
        String fileName = "";
        if(type.equals("crop")){
            fileName = System.currentTimeMillis() +  "_crop.jpg";
        } else if(type.equals("camera")){
            fileName = System.currentTimeMillis() +  ".jpg";
        }
        String dirPath = Environment.getExternalStorageDirectory() + "/"
                + activity.getApplication().getApplicationInfo().packageName;
        File dir = new File(dirPath);
        if (!dir.exists() || dir.isFile()) {
            dir.mkdirs();
        }
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();       //先删除后创建
        }
        try {
            picFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(picFile);
    }

    //打开图片剪裁界面，并对uri路径下的图片做剪裁，剪裁后的图片保存在全局变量storagePathUri中
    private void startCrop(Uri uri) {
        storagePathUri = getOutputUriPath("crop");
        //1. 设置Action为action.CROP，隐式跳转到系统剪裁页面
        //2. 设置剪裁
        //3. 设置不获取返回数据，而是自己在后面输出到某个SD卡路径
        //4. 设置输出的图片格式
        //5. 剪裁完图片后，输出图片到Uri路径storagePathUri中
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, storagePathUri);

        int width = 300;
        int height = 300;
        if(chooseType == 2){    //选择了从相册选取照片
            //获取图片uri路径所对应的存储路径，进而获取原图的尺寸大小，供剪切时参考
            Cursor cursor = activity.getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String photoPath = cursor.getString(index);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        //如果是头像图片，则输入输出都是选择300像素
        if(picType == "headPic") {
            intent.putExtra("aspectX", 300);              //这里也能用width
            intent.putExtra("aspectY", 300);             //这里也能用height
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
        }//如果是直播封面图片，则输入输出都是选择500像素
        else if(picType == "coverPic"){
            intent.putExtra("aspectX", 500);
            intent.putExtra("aspectY", 300);
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 300);
        }

        intent.setDataAndType(uri, "image/*");
        if(fragment != null){
            Log.i("test", "从fragment开始跳转到crop");
            fragment.startActivityForResult(intent, DO_CROP);
        } else if(activity != null){
            Log.i("test", "从activity开始跳转到crop");
            activity.startActivityForResult(intent, DO_CROP);
        }
    }

    //释放activity资源
    public void dismiss(){
        dialog.dismiss();
    }
}
