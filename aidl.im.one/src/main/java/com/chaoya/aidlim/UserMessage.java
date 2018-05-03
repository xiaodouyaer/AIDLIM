package com.chaoya.aidlim;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @classDescription: 描述：
 * @author: LiuChaoya
 * @createTime: 2018/5/2 0002 15:58.
 * @email: 1090969255@qq.com
 */

public class UserMessage implements Parcelable{

    public String messageContent;
    public UserMessage(String messageContent){
        this.messageContent = messageContent;
    }
    protected UserMessage(Parcel in) {
        messageContent = in.readString();
    }

    public static final Creator<UserMessage> CREATOR = new Creator<UserMessage>() {
        @Override
        public UserMessage createFromParcel(Parcel in) {
            return new UserMessage(in);
        }

        @Override
        public UserMessage[] newArray(int size) {
            return new UserMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageContent);
    }
}
