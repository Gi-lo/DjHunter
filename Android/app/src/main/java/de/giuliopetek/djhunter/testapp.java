package de.giuliopetek.djhunter;

import android.app.Activity;
import android.util.Log;

import de.giuliopetek.djhunter.data.network.api.residentadviser.api.RAApiFactory;
import de.giuliopetek.djhunter.data.network.api.residentadviser.api.SearchApi;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class testapp extends Activity {

    @Override
    protected void onStart() {
        super.onStart();

        RAApiFactory.newApi(new OkHttpClient(), SearchApi.class)
                .search("dixon")
                .subscribeOn(Schedulers.io())
                .subscribe(
                        djStream -> djStream.forEach(dj -> Log.d("Search", "Result: " + dj.name)),
                        throwable -> Log.d("Search", "Error: " + throwable.getMessage())
                );
    }
}
