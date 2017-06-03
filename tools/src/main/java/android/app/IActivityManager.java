package android.app;

/**
 * Created by ruiqianqi on 2016/11/10 0010.
 */
import android.content.res.Configuration;
import android.os.RemoteException;

public interface IActivityManager {
    Configuration getConfiguration() throws RemoteException;
    void updateConfiguration(Configuration paramConfiguration) throws RemoteException;
}

