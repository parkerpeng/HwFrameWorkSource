package com.huawei.nearbysdk.DTCP;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.huawei.nearbysdk.NearbyDevice;

public interface IDTCPReceiver extends IInterface {

    public static abstract class Stub extends Binder implements IDTCPReceiver {
        private static final String DESCRIPTOR = "com.huawei.nearbysdk.DTCP.IDTCPReceiver";
        static final int TRANSACTION_cancelReceive = 2;
        static final int TRANSACTION_confirm = 1;
        static final int TRANSACTION_getSenderDevice = 4;
        static final int TRANSACTION_reject = 3;

        private static class Proxy implements IDTCPReceiver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public int confirm(String saveDir, ITransmitCallback transCallback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(saveDir);
                    _data.writeStrongBinder(transCallback != null ? transCallback.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int cancelReceive() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int reject() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NearbyDevice getSenderDevice() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NearbyDevice _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NearbyDevice) NearbyDevice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDTCPReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IDTCPReceiver)) {
                return new Proxy(obj);
            }
            return (IDTCPReceiver) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code != 1598968902) {
                int _result;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        int _result2 = confirm(data.readString(), com.huawei.nearbysdk.DTCP.ITransmitCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        _result = cancelReceive();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        _result = reject();
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        NearbyDevice _result3 = getSenderDevice();
                        reply.writeNoException();
                        if (_result3 != null) {
                            reply.writeInt(1);
                            _result3.writeToParcel(reply, 1);
                        } else {
                            reply.writeInt(0);
                        }
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }
    }

    int cancelReceive() throws RemoteException;

    int confirm(String str, ITransmitCallback iTransmitCallback) throws RemoteException;

    NearbyDevice getSenderDevice() throws RemoteException;

    int reject() throws RemoteException;
}
