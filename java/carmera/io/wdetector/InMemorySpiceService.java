package carmera.io.wdetector;

import android.app.Application;
import android.util.Log;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.memory.LruCacheStringObjectPersister;
import com.octo.android.robospice.persistence.springandroid.json.gson.GsonObjectPersister;

import models.Classifications;


public class InMemorySpiceService extends SpiceService {

    @Override
    public CacheManager createCacheManager(Application application) {
        CacheManager manager = new CacheManager();
        try {
            GsonObjectPersister genDataPersister = new GsonObjectPersister(application, Classifications.class);

            manager.addPersister(genDataPersister);
        } catch (CacheCreationException e) {
            Log.i("Create cache manager: ", e.getMessage());
        }
        LruCacheStringObjectPersister memoryPersister = new LruCacheStringObjectPersister(1024 * 1024);
        manager.addPersister(memoryPersister);
        return manager;
    }
}
