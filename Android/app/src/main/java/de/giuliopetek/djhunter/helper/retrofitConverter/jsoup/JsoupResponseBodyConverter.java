package de.giuliopetek.djhunter.helper.retrofitConverter.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * A convert that converts a ResponseBody to a the specified model using the passed JsoupParser.
 */
public class JsoupResponseBodyConverter<TModel> implements Converter<ResponseBody, TModel> {

    // Members

    private final JsoupConverterFactory.JsoupParser<TModel> mParser;

    // Constructor

    public JsoupResponseBodyConverter(JsoupConverterFactory.JsoupParser<TModel> pParser) {
        super();

        mParser = pParser;
    }

    // Converter

    @Override
    public TModel convert(ResponseBody value) throws IOException {
        String html = value.string();
        Document document = Jsoup.parse(html);
        TModel model = mParser.toModel(document);

        return model;
    }
}
