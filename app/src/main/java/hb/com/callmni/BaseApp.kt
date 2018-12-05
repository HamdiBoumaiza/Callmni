package hb.com.callmni

import android.app.Application
import timber.log.Timber


class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
