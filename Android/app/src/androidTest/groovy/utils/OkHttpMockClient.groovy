package utils

import okhttp3.*

class OkHttpMockClient extends OkHttpClient {

    // Call

    public class MockCall implements Call {

        // Members

        private final Request mRequest;
        private final Object mAnswer;

        // Constructor

        MockCall(Request pRequest, Object pAnswer) {
            mRequest = pRequest
            mAnswer = pAnswer;
        }

        // Call

        @Override
        Request request() {
            return mRequest
        }

        @Override
        Response execute() throws IOException {
            if (mAnswer instanceof Exception) {
                throw mAnswer
            }

            return mAnswer
        }

        @Override
        void enqueue(Callback responseCallback) {
            try {
                Response response = execute()
                responseCallback.onResponse(this, response)
            } catch (Exception exception) {
                responseCallback.onFailure(this, exception)
            }
        }

        @Override
        void cancel() {
            // EMPTY!
        }

        @Override
        boolean isExecuted() {
            return false
        }

        @Override
        boolean isCanceled() {
            return false
        }
    }

    // Members

    private final Deque<Request> mTapedRequests = new ArrayDeque<>()
    private final Deque<Object> mAnswers = new ArrayDeque<Object>()

    // Public API

    public static Response.Builder basicResponseBuilder(Request pRequest) {
        return new Response.Builder()
                .request(pRequest)
                .protocol(Protocol.HTTP_1_0)
                .code(HttpURLConnection.HTTP_OK);
    }

    public void enqueueResponseBuilder(Response.Builder pResponseBuilder) {
        mAnswers.addLast(pResponseBuilder)
    }

    public void enqueueException(Exception pException) {
        mAnswers.addLast(pException)
    }

    public Request takeRequest() {
        return mTapedRequests.removeFirst()
    }

    // OkHttpClient

    @Override
    Call newCall(Request request) {
        mTapedRequests.add(request);

        if (mAnswers.isEmpty()) {
            enqueueResponse(basicResponseBuilder())
        }

        Object answer = mAnswers.removeFirst()
        if (answer instanceof Response.Builder) {
            return new MockCall(request, answer.request(request).build())
        } else {
            return new MockCall(request, answer)
        }
    }
}