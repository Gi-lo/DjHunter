package de.giuliopetek.djhunter.data.network.api.residentadviser.parser;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.giuliopetek.djhunter.data.network.api.residentadviser.model.DJSearchResultItem;
import de.giuliopetek.djhunter.helper.retrofitConverter.jsoup.JsoupConverterFactory;

/**
 * Simple parser for a Stream of DJSearchResultItems.
 */
public class DJSearchResultItemStreamParser extends JsoupConverterFactory.JsoupParser<Stream<DJSearchResultItem>> {

    // Constants

    private static final String CLASS_NAME_MUSIC = "music";
    private static final String HEADER_CONTENT_ARTISTS = "Artists";
    private static final int CHILD_SIZE_GENERIC_MUSIC = 2;
    private static final int CHILD_INDEX_GENERIC_MUSIC_HEADER = 0;
    private static final int CHILD_INDEX_GENERIC_MUSIC_LIST = 1;
    private static final int CHILD_INDEX_GENERIC_MUSIC_INNER_LIST = 0;

    // JsoupParser

    @Override
    public Stream<DJSearchResultItem> toModel(Document pDocument) {
        Optional<Element> artistsResultElement = Stream.of(pDocument.getElementsByClass(CLASS_NAME_MUSIC))
                .filter(element -> element.childNodeSize() == CHILD_SIZE_GENERIC_MUSIC)
                .filter(element -> element.child(CHILD_INDEX_GENERIC_MUSIC_HEADER).text().equals(HEADER_CONTENT_ARTISTS))
                .map(element -> element.child(CHILD_INDEX_GENERIC_MUSIC_LIST))
                .filter(element -> element.children().isEmpty() == false)
                .map(elements -> elements.child(CHILD_INDEX_GENERIC_MUSIC_INNER_LIST))
                .findFirst();

        if (artistsResultElement.isPresent()) {
            return artistsResultElement
                    .stream()
                    .flatMap(element -> Stream.of(element.children()))
                    .map(element -> new DJSearchResultItem(element.text()));

        } else {
            throw new RuntimeException("No results found!");
        }
    }
}
