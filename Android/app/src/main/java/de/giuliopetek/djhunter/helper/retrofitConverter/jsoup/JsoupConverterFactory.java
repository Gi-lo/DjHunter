package de.giuliopetek.djhunter.helper.retrofitConverter.jsoup;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.jsoup.nodes.Document;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Converter factory using Jsoup.
 */
public class JsoupConverterFactory extends Converter.Factory {

    // JsoupParser

    /**
     * A JsoupParser is class that converts a document to the specified model
     */
    public static abstract class JsoupParser<TModel> {

        /**
         * Called when the mode should be parsed.
         */
        public abstract TModel toModel(Document pDocument);
    }

    // Factory methods

    /**
     * Creates a new JsoupConverterFactory instance.
     */
    public static JsoupConverterFactory create() {
        return new JsoupConverterFactory();
    }

    // Members

    private final List<JsoupParser<?>> mParser = new ArrayList<>();

    // Public Api

    /**
     * Registers a new JsoupParser to the given type.
     */
    public <TModel> void registerParser(JsoupParser<TModel> pParser) {
        mParser.add(pParser);
    }

    // Converter.Factory

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type pType, Annotation[] pAnnotations, Retrofit pRetrofit) {
        Type rawInputType = getRawType(pType);
        Type actualInputType = getActualType(pType);

        return Stream.of(mParser)
                .filter(value -> {

                    // We check the raw and actual type of the parser to find the correct one
                    Type parserType = ((ParameterizedType) value.getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0];
                    Type parserRawType = getRawType(parserType);
                    Type parserActualType = getActualType(parserType);

                    // If it's not a generic container class.
                    if (parserRawType == parserActualType) {
                        return parserRawType == rawInputType;
                    } else {
                        return parserRawType == rawInputType && parserActualType == actualInputType;
                    }
                })
                .findFirst()
                .map((Function<JsoupParser<?>, JsoupResponseBodyConverter<?>>) JsoupResponseBodyConverter::new)
                .executeIfAbsent(() -> {
                    throw new IllegalStateException("No parser for type <" + pType + "> specified.");
                })
                .get();
    }

    // Private Api

    private Type getActualType(Type pType) {
        if (pType instanceof ParameterizedType) {
            return ((ParameterizedType) pType).getActualTypeArguments()[0];
        } else {
            return pType;
        }
    }

    private Type getRawType(Type pType) {
        if (pType instanceof ParameterizedType) {
            return ((ParameterizedType) pType).getRawType();
        } else {
            return pType;
        }
    }
}
