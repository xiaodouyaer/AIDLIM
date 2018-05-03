package com.chaoya.aidlimtwo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.chaoya.aidlim.IRemoteService;
import com.chaoya.aidlim.UserMessage;

/**
 * @classDescription: 描述：
 * @author: LiuChaoya
 * @createTime: 2018/5/2 0002 16:10.
 * @email: 1090969255@qq.com
 */

public class RemoteService extends Service {

    private CallBack callBack;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new InnerIBinder();
    }

    public class InnerIBinder extends IRemoteService.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void sendMessage(UserMessage message) throws RemoteException {
            callBack.showMessage(message);
        }

        public Service getService() {
            return RemoteService.this;
        }
    }

    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    public interface CallBack{
        void showMessage(UserMessage message);
    }
}
