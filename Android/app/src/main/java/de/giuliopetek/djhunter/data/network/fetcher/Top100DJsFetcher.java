package de.giuliopetek.djhunter.data.network.fetcher;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.giuliopetek.djhunter.data.network.fetcher.common.ResidentAdvisorFetcher;
import de.giuliopetek.djhunter.data.network.model.DJ;
import okhttp3.OkHttpClient;

/**
 * Fetcher that is used to fetch the top 100 DJ list.
 */
public class Top100DJsFetcher extends ResidentAdvisorFetcher<Stream<DJ>> {

    // Constants

    private static final String PATH = "dj-100.aspx";
    private static final String CLASS_NAME_DJ_LIST = "ul.messages.polls.clearfix";
    private static final String CLASS_NAME_INFO_ARTIST = "li.info.artist";

    // Constructor

    public Top100DJsFetcher(OkHttpClient pClient) {
        super(pClient);
    }

    // Abstract API

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public Stream<DJ> onParse(Document pDocument) {
        return Optional.of(pDocument.select(CLASS_NAME_DJ_LIST))
                .filter(value -> value.isEmpty() == false)
                .map(Elements::first)
                .stream()
                .flatMap(element -> Stream.of(element.childNodes()))
                .map(node -> {
                    Element element = (Element) node;

                    // Title
                    String title = Optional.of(element.select(CLASS_NAME_INFO_ARTIST))
                            .filter(value -> value.isEmpty() == false)
                            .map(artistInfoElements -> artistInfoElements.get(0))
                            .map(Element::text)
                            .get();

                    return new DJ(title);
                });
    }
}
