package com.huawei.nearbysdk.clone;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.huawei.nearbysdk.HwLog;
import com.huawei.nearbysdk.IInternalConnectionListener.Stub;
import com.huawei.nearbysdk.NearbyConfig;
import com.huawei.nearbysdk.NearbyConfig.BusinessTypeEnum;
import com.huawei.nearbysdk.NearbyConfiguration;
import com.huawei.nearbysdk.NearbyDevice;

public class WifiStatusListenerTransport extends Stub {
    private static final int QUIT_CALLBACK_TIMEOUT_MILLIS = 3500;
    private static final String TAG = "WifiStatusListenerTransport";
    private static final int TYPE_CONNECTION_CHANGE = 2;
    private static final int TYPE_STATE_CHANGE = 1;
    private int mBusinessId;
    private BusinessTypeEnum mBusinessType;
    private boolean mIsClosed;
    private WifiStatusListener mListener;
    private final Handler mListenerHandler;
    private NearbyConfiguration mNearbyConfiguration;
    private NearbyDevice mNearbyDevice;

    WifiStatusListenerTransport(BusinessTypeEnum businessType, int businessId, NearbyConfiguration configuration, WifiStatusListener listener, Looper looper) {
        this(businessType, businessId, configuration, null, listener, looper);
    }

    WifiStatusListenerTransport(BusinessTypeEnum businessType, int businessId, NearbyDevice device, WifiStatusListener listener, Looper looper) {
        this(businessType, businessId, null, device, listener, looper);
    }

    private WifiStatusListenerTransport(BusinessTypeEnum businessType, int businessId, NearbyConfiguration configuration, NearbyDevice device, WifiStatusListener listener, Looper looper) {
        this.mIsClosed = false;
        this.mListener = listener;
        this.mBusinessType = businessType;
        this.mBusinessId = businessId;
        this.mNearbyDevice = device;
        this.mNearbyConfiguration = configuration;
        this.mListenerHandler = new Handler(looper) {
            public void handleMessage(Message msg) {
                WifiStatusListenerTransport.this._handleMessage(msg);
            }
        };
    }

    synchronized void quit() {
        this.mListener = null;
        this.mIsClosed = false;
    }

    synchronized void waitQuit() {
        if (!this.mIsClosed) {
            try {
                wait(3500);
            } catch (InterruptedException e) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("WifiStatusListenerTransport waitQuit:");
                stringBuilder.append(e.getLocalizedMessage());
                HwLog.w(str, stringBuilder.toString());
            }
        }
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("waitQuit done mIsClosed=");
        stringBuilder2.append(this.mIsClosed);
        HwLog.i(str2, stringBuilder2.toString());
    }

    public BusinessTypeEnum getBusinessType() {
        return this.mBusinessType;
    }

    public int getBusinessId() {
        return this.mBusinessId;
    }

    public NearbyDevice getNearbyDevice() {
        return this.mNearbyDevice;
    }

    public void onStatusChange(int state) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onStatusChange state = ");
        stringBuilder.append(state);
        HwLog.d(str, stringBuilder.toString());
        sendMessage(1, null, state, 0);
    }

    public void onConnectionChange(NearbyDevice device, int state) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onConnectionChange device=");
        stringBuilder.append(device);
        stringBuilder.append(" state=");
        stringBuilder.append(state);
        HwLog.d(str, stringBuilder.toString());
        sendMessage(2, device, state, 0);
    }

    public void onReceive(NearbyDevice device, byte[] recvMessage) {
        HwLog.e(TAG, "onReceive error here");
    }

    private void sendMessage(int msgWhat, Object obj, int state, long delayMillis) {
        Message msg = this.mListenerHandler.obtainMessage(msgWhat, obj);
        msg.arg1 = state;
        if (!this.mListenerHandler.sendMessageDelayed(msg, delayMillis)) {
            HwLog.e(TAG, "sendMessage error here");
        }
    }

    private void _handleMessage(Message msg) {
        NearbyDevice device = null;
        if (msg.obj instanceof NearbyDevice) {
            device = msg.obj;
        }
        int arg1 = msg.arg1;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_handleMessage: ");
        stringBuilder.append(msg.toString());
        HwLog.d(str, stringBuilder.toString());
        switch (msg.what) {
            case 1:
                callBackOnStatusChange(arg1);
                return;
            case 2:
                if (device != null) {
                    callBackOnConnectionChange(device, arg1);
                    return;
                }
                return;
            default:
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("_handleMessage: unknown message ");
                stringBuilder.append(msg.what);
                HwLog.e(str, stringBuilder.toString());
                return;
        }
    }

    /* JADX WARNING: Missing block: B:19:0x004b, code skipped:
            return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void callBackOnStatusChange(int status) {
        int state;
        if (status == -1) {
            state = -1;
        } else if (status == 0) {
            state = 0;
            this.mIsClosed = false;
        } else if (status == 1) {
            state = 1;
        } else if (status == 2) {
            state = 3;
            this.mIsClosed = true;
        } else {
            state = 2;
        }
        HwLog.i(TAG, String.format("callBackOnStatusChange: mListener=%s state=%d->%d", new Object[]{this.mListener, Integer.valueOf(status), Integer.valueOf(state)}));
        if (this.mListener != null) {
            this.mListener.onStateChange(state);
        } else if (this.mIsClosed) {
            HwLog.e(TAG, "callBackOnStatusChange: mListener null and closed");
            notifyAll();
        }
    }

    private synchronized void callBackOnConnectionChange(NearbyDevice device, int status) {
        if (this.mListener == null) {
            HwLog.e(TAG, "callBackOnConnectionChange: mListener null");
            return;
        }
        int connectionState;
        if (status == 2) {
            connectionState = 0;
        } else if (status == 3) {
            connectionState = 1;
        } else if (status == 0) {
            connectionState = 3;
        } else {
            connectionState = 2;
        }
        int band = device.getWifiBand();
        String ipAddr = device.getRemoteIp();
        int port = device.getWifiPort();
        HwLog.i(TAG, String.format("callBackOnConnectionChange: mListener=%s state=%d->%d device=%s band=%d ipAddr=%s port=%d", new Object[]{this.mListener, Integer.valueOf(status), Integer.valueOf(connectionState), device, Integer.valueOf(band), NearbyConfig.toSecureString(ipAddr), Integer.valueOf(port)}));
        this.mListener.onConnectionChange(connectionState, band, ipAddr, port);
    }
}
