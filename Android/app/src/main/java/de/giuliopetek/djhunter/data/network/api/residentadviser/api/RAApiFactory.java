package de.giuliopetek.djhunter.data.network.api.residentadviser.api;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import de.giuliopetek.djhunter.data.network.api.residentadviser.parser.DJSearchResultItemStreamParser;
import de.giuliopetek.djhunter.helper.retrofitConverter.jsoup.JsoupConverterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Class to generate ResidentAdviser API services.
 */
public final class RAApiFactory {

    // Constants

    private static final String BASE_URL = "https://www.residentadvisor.net/";

    // Public API

    /**
     * Generates a new service instance for the given service class.
     */
    public static <TApiService> TApiService newApi(OkHttpClient pClient, Class<TApiService> pServiceClass) {
        JsoupConverterFactory converterFactory = JsoupConverterFactory.create();
        converterFactory.registerParser(new DJSearchResultItemStreamParser());

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .client(pClient)
                .build();

        return retrofit.create(pServiceClass);
    }

}
