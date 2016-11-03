package de.giuliopetek.djhunter.data.network.fetcher.common

import com.andrewreitz.spock.android.AndroidSpecification
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import spock.lang.Title
import utils.Fixtures
import utils.OkHttpMockClient

import static io.reactivex.Observable.empty

// Classes

class MockFetcher extends ResidentAdvisorFetcher<String> {

    // Members

    private final String mPath;

    // Constructor

    MockFetcher(OkHttpClient pClient, String pPath) {
        super(pClient)

        mPath = pPath;
    }

    // ResidentAdvisorFetcher

    @Override
    String getPath() {
        return mPath;
    }

    @Override
    String onParse(Document pDocument) {
        if (pDocument != null) {
            return pDocument.title()
        }

        return null
    }
}

@Title("ResidentAdvisorFetcher.fetch()")
class ResidentAdvisorFetcherFetchSpecs extends AndroidSpecification {

    // Scenarios

    def "Sends the correct request"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            mockClient.enqueueException(new Exception("Stub!"))

        and: "a fetcher with that client"
            MockFetcher fetcher = new MockFetcher(mockClient, "SomePath")

        when: "asked to fetch"
            fetcher.fetch().onErrorResumeNext(empty()).blockingFirst(null)

        then: "the correct request is queried"
            mockClient.takeRequest().url().toString() == "https://www.residentadvisor.net/SomePath"
    }

    def "Emits the parsed title"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            Response.Builder responseBuilder = OkHttpMockClient.basicResponseBuilder()
                    .body(ResponseBody.create(MediaType.parse("text/html"), Fixtures.HTML.SIMPLE_HTML))
            mockClient.enqueueResponseBuilder(responseBuilder)

        and: "a fetcher with that client"
            MockFetcher fetcher = new MockFetcher(mockClient, "SomePath")

        when: "asked to fetch the title"
            def title = fetcher.fetch().blockingFirst()

        then: "the correct title is returned"
            title == "Some title"
    }

    def "emits an error if an error occurs"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            mockClient.enqueueException(new IllegalStateException("Illegal state!"))

        and: "a fetcher with that client"
            MockFetcher fetcher = new MockFetcher(mockClient, "SomePath")

        when: "asked to fetch"
            fetcher.fetch().blockingFirst()

        then: "am IllegalStateException is thrown"
            thrown(IllegalStateException)
    }
}