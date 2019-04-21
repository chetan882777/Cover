package com.chetan.projects.cover.Network;

public class JsonRequestConstants {

    public static final String PARAM_QUERY = "q";
    public static final String PARAM_SCHEME = "https";

    public static String SEND_LOADING_TYPE = "loadingType";
    public static String LOADING_TYPE_PEXELS = "pexels";
    public static String LOADING_TYPE_PIXABAY = "pixabay";


    public static class PixabayAPI{

        public static final int CATEGORY_IMAGE_LOADING = 0;
        public static final int POPULAR_IMAGE_LOADING = 1;
        public static final int LATEST_IMAGE_LOADING = 2;

        public static final String PIXABAY_BASE = "pixabay.com";
        public static final String API_PATH = "api";

        public static final String KEY = "key";
        public static final String KEY_VALUE = "10823773-d6c71e4d3fa8511d3245df63a";


        public static final String IMAGE_TYPE = "image_type";
        public static final String IMAGE_TYPE_PHOTO = "photo";
        public static final String IMAGE_TYPE_ILLUSTRATION = "illustration";
        public static final String IMAGE_TYPE_VECTOR = "vector";


        // image orientation
        public static final String IMAGE_ORIENTATION = "orientation";
        public static final String IMAGE_ORIENTATION_VERTICAL = "vertical";
        public static final String IMAGE_ORIENTATION_HORIZONTAL = "horizontal";

        public static final String IMAGE_CATEGORY = "category";
        public static final String IMAGE_CATEGORY_FASHION = "fashion";
        public static final String IMAGE_CATEGORY_NATURE = "nature";
        public static final String IMAGE_CATEGORY_BACKGROUNDS = "backgrounds";
        public static final String IMAGE_CATEGORY_SCIENCE = "science";
        public static final String IMAGE_CATEGORY_RELIGION = "religion";

        public static final String IMAGE_MIN_WIDTH = "min_width";
        public static final String IMAGE_MIN_HEIGH = "min_height";
        public static final int IMAGE_MIN_WIDTH_320 = 320;
        public static final int IMAGE_MIN_HEIGHT_480 = 480;


        public static final String IMAGE_PREVIEW_URL = "previewURL";
        public static final String IMAGE_PER_PAGE = "per_page";
        public static final String IMAGE_PER_PAGE_RESULTS = "200";
        public static final String PAGE_NO = "page";
        // result best images
        public static final String IMAGE_EDITORS_CHOICE = "editors_choice";
        public static final boolean IMAGE_EDITORS_CHOICE_TRUE = true;

        public static final String REQUEST_CALLBACK_FUNCTION = "callback";
        public static final String REQUEST_CALLBACK_FUNCTION_JSONP = "JSONP";


        public static final String JSON_OUTPUT_INDENTATION = "pretty";
        // only in development
        public static final boolean JSON_OUTPUT_INDENTATION_TRUE = true;


        public static final String JSON_READ_IMAGE_PREVIEW_URL = "previewURL";
        public static final String JSON_READ_IMAGE_HITS = "hits";
        public static final String JSON_READ_IMAGE_PAGE_URL = "pageURL";
        public static final String JSON_READ_IMAGE_LARGE_URL = "largeImageURL";
        public static final String JSON_READ_IMAGE_USER_NAME = "user";
        public static final String JSON_READ_IMAGE_USER_ID = "user_id";
        public static final String JSON_READ_IMAGE_TYPE = "type";



        public static final String IMAGE_ORDER = "order";
        public static final String IMAGE_ORDER_POPULAR = "popular";
        public static final String IMAGE_ORDER_LATEST = "latest";

    }

    public static class PexelsAPI{

        public static final int CATEGORY_IMAGE_LOADING = 0;
        public static final int POPULAR_IMAGE_LOADING = 1;
        public static final int LATEST_IMAGE_LOADING = 2;

        public static final String PEXELS_BASE = "api.pexels.com/v1/search/";

        public static final String BEARER_TOKEN = "Token";
        public static final String TOKEN_VALUE = "563492ad6f9170000100000154cbf2e77016482d882a032891e77894";


        public static final String IMAGE_PREVIEW_URL = "small";
        public static final String IMAGE_MEDIUM_URL = "large";
        public static final String IMAGE_LARGE_URL = "original";


        public static final String JSON_READ_IMAGE_URLS = "src";
        public static final String JSON_READ_IMAGES = "photos";
        public static final String JSON_READ_IMAGE_PAGE_URL = "url";
        public static final String JSON_READ_IMAGE_USER_NAME = "photographer";
        public static final String JSON_READ_IMAGE_USER_ID = "photographer_url";

    }
}
