package com.huawei.nb.searchmanager.client.connect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import com.huawei.nb.utils.logger.DSLog;

public class RemoteServiceConnection {
    private static final String COMMAND_TYPE_APP = "APP";
    private static final String TAG = "RemoteServiceConnection";
    private IBinder binder;
    private OnConnectListener connectListener;
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            RemoteServiceConnection.this.binder = iBinder;
            if (RemoteServiceConnection.this.connectListener != null) {
                RemoteServiceConnection.this.connectListener.onConnect(RemoteServiceConnection.this.binder);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (RemoteServiceConnection.this.binder != null) {
                RemoteServiceConnection.this.binder = null;
                if (RemoteServiceConnection.this.connectListener != null) {
                    RemoteServiceConnection.this.connectListener.onDisconnect();
                }
                DSLog.e("RemoteServiceConnection Connection to data service is disconnected unexpectedly.", new Object[0]);
            }
        }
    };
    private final Context context;
    private final String serviceAction;
    private final String serviceClassName;
    private final String servicePackageName;

    public interface OnConnectListener {
        void onConnect(IBinder iBinder);

        void onDisconnect();
    }

    public RemoteServiceConnection(Context base, String servicePackageName, String serviceClassName) {
        this.context = base;
        this.serviceAction = null;
        this.servicePackageName = servicePackageName;
        this.serviceClassName = serviceClassName;
        this.binder = null;
        DSLog.i("RemoteServiceConnection RemoteServiceConnection", new Object[0]);
    }

    public RemoteServiceConnection(Context base, String serviceAction) {
        this.context = base;
        this.serviceAction = serviceAction;
        this.servicePackageName = null;
        this.serviceClassName = null;
        this.binder = null;
        DSLog.i("RemoteServiceConnection RemoteServiceConnection", new Object[0]);
    }

    public boolean open(OnConnectListener listener) {
        Intent remoteServiceIntent;
        this.connectListener = listener;
        if (this.servicePackageName == null || this.serviceClassName == null) {
            remoteServiceIntent = createImplicitIntent(this.serviceAction);
        } else {
            remoteServiceIntent = createExplicitIntent(this.servicePackageName, this.serviceClassName);
        }
        if (remoteServiceIntent == null) {
            DSLog.e("RemoteServiceConnection Failed to find the given data service action.", new Object[0]);
            return false;
        }
        remoteServiceIntent.setType(COMMAND_TYPE_APP);
        try {
            if (this.context.bindService(remoteServiceIntent, this.connection, 1)) {
                return true;
            }
            DSLog.e("RemoteServiceConnection Failed to connect to data service.", new Object[0]);
            return false;
        } catch (SecurityException e) {
            DSLog.e("RemoteServiceConnection Failed to do bind service, error: %s.", new Object[0]);
            return false;
        }
    }

    public boolean close() {
        this.context.unbindService(this.connection);
        this.binder = null;
        this.connectListener = null;
        return true;
    }

    private Intent createImplicitIntent(String action) {
        PackageManager pm = this.context.getPackageManager();
        if (pm == null) {
            return null;
        }
        Intent intent = new Intent(action);
        ResolveInfo info = pm.resolveService(intent, 131072);
        if (info == null) {
            return null;
        }
        intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        return intent;
    }

    private Intent createExplicitIntent(String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        return intent;
    }
}
