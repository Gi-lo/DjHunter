package utils

import android.support.test.InstrumentationRegistry

class Fixtures {

    // Loader

    class Loader {

        // Public API

        public static String loadAsset(String pPath) {
            try {
                def context = InstrumentationRegistry.getContext()
                def assetInputStream = context.getAssets().open(pPath)
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetInputStream))

                StringBuilder stringBuilder = new StringBuilder()
                String readString
                while ((readString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(readString)
                }

                bufferedReader.close()

                return stringBuilder.toString()
            } catch (Exception ignored) {
                throw new IllegalStateException("Could not read asset <" + pPath + ">")
            }
        }
    }

    // Fixtures

    class HTML {

        // Constants

        public static String TOP_100_DJS_HTML = Loader.loadAsset("html/Top100DJs.html")
        public static String SIMPLE_HTML = Loader.loadAsset("html/Simple.html")
    }
}
