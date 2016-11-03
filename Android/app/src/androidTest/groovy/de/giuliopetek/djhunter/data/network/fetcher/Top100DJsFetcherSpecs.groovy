package de.giuliopetek.djhunter.data.network.fetcher

import com.andrewreitz.spock.android.AndroidSpecification
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import spock.lang.Title
import utils.Fixtures
import utils.OkHttpMockClient

import static io.reactivex.Observable.empty

@Title("Top100DJsFetcher.fetch()")
class Top100DJsFetcherFetchSpecs extends AndroidSpecification {

    // Scenarios

    def "Sends the correct request"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            mockClient.enqueueException(new Exception("Stub!"))

        and: "a fetcher with that client"
            Top100DJsFetcher fetcher = new Top100DJsFetcher(mockClient)

        when: "asked to fetch"
            fetcher.fetch().onErrorResumeNext(empty()).blockingFirst(null)

        then: "the correct request is queried"
            mockClient.takeRequest().url().toString() == "https://www.residentadvisor.net/dj-100.aspx"
    }

    def "Emits the empty stream when not able to parse"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            Response.Builder responseBuilder = OkHttpMockClient.basicResponseBuilder()
                    .body(ResponseBody.create(MediaType.parse("text/html"), Fixtures.HTML.SIMPLE_HTML))
            mockClient.enqueueResponseBuilder(responseBuilder)

        and: "a fetcher with that client"
            Top100DJsFetcher fetcher = new Top100DJsFetcher(mockClient)

        when: "asked to fetch"
            def djs = fetcher.fetch().blockingFirst()

        then: "an empty stream is returned"
            djs.count() == 0
    }

    def "Emits the correct DJs"() {

        given: "Some fake api client"
            OkHttpMockClient mockClient = new OkHttpMockClient()
            Response.Builder responseBuilder = OkHttpMockClient.basicResponseBuilder()
                    .body(ResponseBody.create(MediaType.parse("text/html"), Fixtures.HTML.TOP_100_DJS_HTML))
            mockClient.enqueueResponseBuilder(responseBuilder)

        and: "a fetcher with that client"
            Top100DJsFetcher fetcher = new Top100DJsFetcher(mockClient)

        when: "asked to fetch"
            def djs = fetcher.fetch().blockingFirst()

        then: "the correct dj stream is returned"
            def firstDJ = djs.findFirst().get()
            firstDJ.name == "Dixon"

            def lastDJ = djs.skip(98).findFirst().get()
            lastDJ.name == "Nicole Moudaber"
    }
}