package com.android.ims;

import android.telephony.ims.ImsServiceProxy.INotifyStatusChanged;
import java.util.function.Consumer;

final /* synthetic */ class -$Lambda$gK80XnH6tW5tPey07NHzZCneFoE implements Consumer {
    public static final /* synthetic */ -$Lambda$gK80XnH6tW5tPey07NHzZCneFoE $INST$0 = new -$Lambda$gK80XnH6tW5tPey07NHzZCneFoE();

    /* renamed from: com.android.ims.-$Lambda$gK80XnH6tW5tPey07NHzZCneFoE$1 */
    final /* synthetic */ class AnonymousClass1 implements INotifyStatusChanged {
        private final /* synthetic */ Object -$f0;

        private final /* synthetic */ void $m$0() {
            ((ImsManager) this.-$f0).getStatusCallbacks().forEach(-$Lambda$gK80XnH6tW5tPey07NHzZCneFoE.$INST$0);
        }

        public /* synthetic */ AnonymousClass1(Object obj) {
            this.-$f0 = obj;
        }

        public final void notifyStatusChanged() {
            $m$0();
        }
    }

    private final /* synthetic */ void $m$0(Object arg0) {
        ((INotifyStatusChanged) arg0).notifyStatusChanged();
    }

    private /* synthetic */ -$Lambda$gK80XnH6tW5tPey07NHzZCneFoE() {
    }

    public final void accept(Object obj) {
        $m$0(obj);
    }
}
