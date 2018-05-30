package edu.sysu.showtime.livelist;

import android.hardware.Camera;

import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;

public class HostOperateStatus {
    public boolean isBeautyOn = false;
    public boolean isFlashOn = false;
    public boolean isVoiceOn = true;
    public int cameraId = ILiveConstants.FRONT_CAMERA;

    public void switchBeauty(){
        isBeautyOn = !isBeautyOn;
        if (isBeautyOn) {
            //打开美颜
            ILiveRoomManager.getInstance().enableBeauty(100);
        } else {
            //关闭美颜
            ILiveRoomManager.getInstance().enableBeauty(0);
        }
    }

    public void switchVoice(){
        isVoiceOn = !isVoiceOn;
        ILiveRoomManager.getInstance().enableMic(isVoiceOn);
    }

    public void switchCamera(){
        if (cameraId == ILiveConstants.FRONT_CAMERA) {
            cameraId = ILiveConstants.BACK_CAMERA;
        } else if (cameraId == ILiveConstants.BACK_CAMERA) {
            cameraId = ILiveConstants.FRONT_CAMERA;
            if(isFlashOn){
                isFlashOn = false;
            }
        }
        ILiveRoomManager.getInstance().switchCamera(cameraId);
    }

    public void switchFlash(){
        if(cameraId == ILiveConstants.FRONT_CAMERA){
            isFlashOn = false;
            return;
        }
        Object cameraObj = ILiveLoginManager.getInstance().getAVConext().getVideoCtrl().getCamera();
        if (cameraObj == null || !(cameraObj instanceof Camera)) {
            isFlashOn = false;
            return;
        }

        Camera camera = (Camera) cameraObj;
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            isFlashOn = false;
            return;
        }
        if (isFlashOn) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }

        try {
            camera.setParameters(parameters);
            isFlashOn = !isFlashOn;
        }catch (Exception e){
            isFlashOn = false;
        }
    }
}
