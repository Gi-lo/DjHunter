package de.giuliopetek.djhunter.data.network.api.residentadviser.api;

import com.annimon.stream.Stream;

import de.giuliopetek.djhunter.data.network.api.residentadviser.model.DJSearchResultItem;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Resident Adviser Search API.
 */
public interface SearchApi {

    // GET

    /**
     * Search for djs.
     */
    @GET("search.aspx")
    Single<Stream<DJSearchResultItem>> search(@Query("searchstr") String pSearchTerm);
}
