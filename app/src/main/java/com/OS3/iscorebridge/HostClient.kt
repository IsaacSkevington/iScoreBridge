package com.OS3.iscorebridge

import android.os.Handler

class HostWifiClient(handler: Handler, var hostClient: HostClient) : WifiClient(handler){
    override fun send(purpose : Int, msg : String){
        hostClient.clientHandler.obtainMessage(MESSAGE_READ, Communication(myInfo.deviceName, purpose, msg)).sendToTarget()
    }

}

class HostClient(number : Int, handler : Handler) : Client(0, number, handler) {

    override fun send(c : Communication){
        wifiClient.connectionHandler.obtainMessage(MESSAGE_READ,c).sendToTarget()
    }
    override fun getAddress() : String{
        return "Me"
    }
}