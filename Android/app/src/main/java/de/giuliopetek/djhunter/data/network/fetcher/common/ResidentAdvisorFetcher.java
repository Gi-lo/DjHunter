package de.giuliopetek.djhunter.data.network.fetcher.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Abstract fetcher class used to fetch information from Resident Adviser.
 */
public abstract class ResidentAdvisorFetcher<TModel> {

    // Constants

    private final static String ENDPOINT = "https://www.residentadvisor.net/";

    // Members

    private final OkHttpClient mClient;

    // Constructor

    public ResidentAdvisorFetcher(OkHttpClient pClient) {
        super();

        mClient = pClient;
    }

    // Abstract API

    /**
     * Implement this to specify the path to the information.
     */
    public abstract String getPath();

    /**
     * Implement this to parse the information from the given document.
     */
    public abstract TModel onParse(Document pDocument);

    // Public API

    /**
     * Returns a cold observable that will query and parse the information once subscribed.
     */
    public Observable<TModel> fetch() {
        return Observable.create(emitter -> {
            Request request = new Request.Builder()
                    .url(ENDPOINT + getPath())
                    .build();

            String html = mClient.newCall(request)
                    .execute()
                    .body()
                    .string();

            TModel model = onParse(Jsoup.parse(html));

            emitter.onNext(model);
        });
    }
}
