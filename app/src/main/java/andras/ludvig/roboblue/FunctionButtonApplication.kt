package andras.ludvig.roboblue

import andras.ludvig.roboblue.database.FunctionButtonsDbLoader
import android.app.Application

class FunctionButtonApplication : Application() {

    companion object {
        lateinit var functionButtonsLoader: FunctionButtonsDbLoader
            private set
    }

    override fun onCreate() {
        super.onCreate()

        functionButtonsLoader = FunctionButtonsDbLoader(this)
        functionButtonsLoader.open()
    }

    override fun onTerminate() {
        functionButtonsLoader.close()
        super.onTerminate()
    }

}