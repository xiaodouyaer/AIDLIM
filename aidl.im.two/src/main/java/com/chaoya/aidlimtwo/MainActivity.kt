package com.chaoya.aidlimtwo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.chaoya.aidlim.IRemoteService
import com.chaoya.aidlim.UserMessage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private var list: LinkedList<String> = LinkedList()
    private var adapter: ArrayAdapter<String>? = null;
    private var remoteService: IRemoteService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClick()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        list_view?.adapter = adapter;

        val intent2 = Intent().setComponent(ComponentName(
                "com.chaoya.aidlimtwo",
                "com.chaoya.aidlimtwo.RemoteService"))
        bindService(intent2, mConnection2, Context.BIND_AUTO_CREATE)
    }

    private fun setClick() {
        send.setOnClickListener { v ->
            if ("" == content?.getText().toString().trim { it <= ' ' }) {
                Toast.makeText(this, "请输入信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (remoteService == null) {
                Toast.makeText(this, "服务正在启动...", Toast.LENGTH_SHORT).show()
                val intent1 = Intent().setComponent(ComponentName(
                        "com.chaoya.aidlimone",
                        "com.chaoya.aidlimone.RemoteService"))
                bindService(intent1, mConnection1, Context.BIND_AUTO_CREATE)
                return@setOnClickListener
            }

            try {
                remoteService?.sendMessage(UserMessage(content?.getText().toString().trim { it <= ' ' }))
                list.addFirst("男孩说:" + content?.getText().toString().trim { it <= ' ' })
                content?.setText("")
                adapter?.notifyDataSetChanged()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
         }
    }

    private val mConnection1 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected")
            remoteService = IRemoteService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    private val mConnection2 = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected")
            val innerIBinder = service as RemoteService.InnerIBinder
            val remoteService = innerIBinder.service as RemoteService
            remoteService.setCallBack { message ->
                list.addFirst("女孩说:" + message.messageContent)
                adapter?.notifyDataSetChanged()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }
}
