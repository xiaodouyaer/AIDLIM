aidl.im.one 和 aidl.im.two这两个module是利用aidl实现的两个可以聊天的app，不过是装在同一个手机上的
    适合超级无聊之人自己和自己聊天使用。

AIDL :Android interface definition language，Android 接口描述语言。
Android studio：新建一个项目，新建两个application  module，目的是用这两个app进行测试通信（以下称
    app1  和  app2 ）。

一、两个module中新建包名和文件名都一样的aidl文件，本项目是IRemoteService.aidl，名称和包名不一样的话
    两边都会找不到服务。
二、如果要通过服务传递自定义对象的话，那么也需要定义该对象的aidl文件，本项目定义了UserMessage.aidl，
    和IRemoteService.aidl一样，在两个module中包名和文件名都一样。
三、两个module 中新建UserMessage.java（也可以是其他语言对象，看你用的是什么语言了，比如可以是UserMe
    ssage.kt）,也需要时一样的包名和文件名。
四、接下来就是写activity和service了，aidl.im.one中新建MainActivity和RemoteService类，位置和名称就随
    便了。
    （1）MainActivity
        1） onCreate方法就可以直接注册本app（app1）接受消息的服务
            Intent intent2 = new Intent().setComponent(new ComponentName(
                            "com.chaoya.aidlimone",
                            "com.chaoya.aidlimone.RemoteService"));
            bindService(intent2, mConnection2, Context.BIND_AUTO_CREATE);

            在发送消息的方法里判断app2的服务有没有连接上，没连接上的话就去连接绑定，之所以没有也在on
            Create方法中绑定是因为同一个手机中没法同时点击打开两个app，若是在onCreate中绑定的话，就会找
            不到服务，导致绑定失败，因为app2还没启动，同样其服务也没启动，所以是连接不成功的；可能有人
            会问，为什么不先把app2启动？如果先把app2启动的话，app1启动时确实可以连接上app2的服务，并且
            app1的接受消息的服务也绑定成功，app1算是没问题了，但是app2呢，app2是没有连接上app1的服务的，
            道理和先启动app1是一样的，不再赘述；这个时候想让app2也连接上app1的服务，就需要app2重启，但是
            问题又来了，若app2重启了，那么app2自己的接受消息的服务也会重新绑定初始化，对象会变哦，内存地
            址都不一样了，这时候app1持有的还是那个一样不存在的服务，如果这时app1调用发送消息服务，那么就
            会报错，什么错呢，就是Android的android.os.DeadObjectException异常。

        2）在发送点击事件中绑定app2的服务
            Intent intent1 = new Intent().setComponent(new ComponentName(
                                "com.chaoya.aidlimtwo",
                                "com.chaoya.aidlimtwo.RemoteService"));
            bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);

            注意：1）和2）中绑定的服务是不一样的，一个是自己的接受消息的服务，一个是调用对方的远程服务。

        3）发送消息的服务具体操作
            private ServiceConnection mConnection1 = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.e(TAG, "onServiceConnected");
                    remoteService = IRemoteService.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            这个对象就是bindService时传入的监听，在发送消息时判断一下remoteService，为null时再bindServi
            ce一下。

            发消息的代码
            if ("".equals(content.getText().toString().trim())) {
                Toast.makeText(this, "请输入信息", Toast.LENGTH_SHORT).show();
                return;
            }
            if (remoteService == null) {
                Toast.makeText(this, "服务正在启动...", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent().setComponent(new ComponentName(
                        "com.chaoya.aidlimtwo",
                        "com.chaoya.aidlimtwo.RemoteService"));
                bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE);
                return;
            }
            try {
                remoteService.sendMessage(new UserMessage(content.getText().toString().trim()));
                list.addFirst("女孩说:" + content.getText().toString().trim());
                content.setText("");
                adapter.notifyDataSetChanged();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            我这里用了一个listview把发送和收到的消息都展示了

         4）接受消息的服务的具体操作
            RemoteService.InnerIBinder innerIBinder = (RemoteService.InnerIBinder) service;
            RemoteService remoteService = (RemoteService) innerIBinder.getService();
            remoteService.setCallBack(new RemoteService.CallBack() {
                @Override
                public void showMessage(UserMessage message) {
                    list.addFirst("男孩说:" + message.messageContent);
                    adapter.notifyDataSetChanged();
                }
            });

            这里通过回调的方式去获取了service收到的消息，我这里是直接加入集合并展示了。
    （2）RemoteService，继承自android.app.Service
        1）定义一个内部类供本app调用
            public class InnerIBinder extends IRemoteService.Stub {
                @Override
                public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

                }
                @Override
                public void sendMessage(UserMessage message) throws RemoteException {
                    //将收到的消息回调给MainActivity，其实就是app2远程调用的，对app1来说就是接受到的
                    callBack.showMessage(message);
                }
                public Service getService() {
                    return RemoteService.this;
                }
            }

        2）onBind方法中直接返回new InnerIBinder();
        3）定义一个回调接口
            public interface CallBack{
                void showMessage(UserMessage message);
            }
        4）设置回调方法
            public void setCallBack(CallBack callBack){
                this.callBack = callBack;
            }

五、app2的代码和app1几乎一样，除了调用服务的包名外。

六、本着真正开源的精神，代码上传GitHub了，特讨厌把代码传C**N，下载还要钱，不喜欢，地址如下：
