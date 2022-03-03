package com.shorincity.vibin.music_sharing.utils;

public class AppConstants {

    // Prod
    //public static String URL = "https://vibin.pythonanywhere.com//user/login/";
    //public static String BASE_URL = "https://vibin.pythonanywhere.com/";
    public static final boolean LOG_ENABLED = true;

    // Splash
    public static final long SPLASH_DELAY = 2800; //1

    // QA
    public static String URL = "https://harshkant882.pythonanywhere.com/user/login/";
    public static String BASE_URL = "https://staging.vibin.in/";

    public static String LOGIN_SIGNUP_HEADER = "Token 774bf546e87365cade61170bcc9398707994d377";
    public static String KEY_CODE = "uVqLxiHDKIirly75dk9yr5sk097DsmYW1Abj2dh7746dggf";

    // FPRGOT PASS URL
    public static String FORGOT_PASS_URL = BASE_URL + "user/password_reset/";

    public static String API_USER_SIGNUP = "/user/signup/";


    // Youtube Keys
    public static String YOUTUBE_KEY = "";

    public static String LAST_FM_KEY = "";

    // some static Keys
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    // intent Keys
    public static final String INTENT_YOUTUBE_CHANNEL_ID = "intentYoutubeChannelId";
    public static final String INTENT_YOUTUBE_CHANNEL_BANNER_URL = "intentYoutubeChannelBannerUrl";
    public static final String INTENT_YOUTUBE_CHANNEL_DATA = "intentYoutubeChannelData";
    public static final String INTENT_YOUTUBE_PLAYLIST_DATA = "intentYoutubePlaylistData";
    public static final String INTENT_SIGN_UP_METHOD = "intentSignUpMethod";
    public static final String INTENT_EMAIL = "intentEmail";
    public static final String INTENT_PASSWORD = "intentPassword";
    public static final String INTENT_DOB = "intentDob";
    public static final String INTENT_GENDER = "intentGender";
    public static final String INTENT_USER_NAME = "intentUserName";
    public static final String INTENT_FULL_NAME = "intentFullName";
    public static final String INTENT_AVATAR_LINK = "intentAvatarLink";
    public static final String INTENT_USER_ID = "intentUserId";
    public static final String INTENT_GOOGLE_ID = "intentGoogleId";
    public static final String INTENT_SPOTIFY_ID = "intentSpotifyId";
    public static final String INTENT_SPOTIFY_CHANNEL_DATA = "intentSpotifyChannelData";
    public static final String INTENT_USER_PREFERRED_PLATFORM = "intentUserPreferredPlatform";
    public static final String INTENT_NOTIFICATION_TOKEN_UPDATED = "intentNotificationTokenUpdated";
    public static final String INTENT_USER_TOKEN = "intentUserToken";
    public static final String INTENT_USER_API_TOKEN = "intentUserAPIToken";
    public static final String IS_SUFFLEON = "isSuffleOn";
    public static final String IS_REPEATON = "isRepeatOn";
    public static final String INTENT_IS_USER_LOGGEDIN = "intentIsUserLoggedIn";
    public static final String INTENT_UPDATE_PLATFORM = "intentUpdatePlatform";
    public static final String INTENT_USER_DATA_BUNDLE = "intentUserDataBundle";
    public static final String INTENT_TITLE = "intent_title";
    public static final String INTENT_MESSAGE = "intent_message";
    public static final String INTENT_BUTTON_NAME = "intent_button_name";
    public static final String INTENT_CANCELABLE = "intent_cancelable";
    public static final String INTENT_LOGIN_ERROR = "intent_login_error";

    public static final String INTENT_GENDER_AVATAR_LINK = "intentGenderAvatarLink";

    public static final String INTENT_SEARCHED_USER_NAME = "intentSearchedUserName";
    public static final String INTENT_SEARCHED_FULL_NAME = "intentSearchedFullName";
    public static final String INTENT_SEARCHED_USER_ID = "intentSearchedUserId";
    public static final String INTENT_WEBVIEW_URL = "intentWebviewUrl";
    public static final String INTENT_PLAYLIST = "intentPlaylist";
    public static final String INTENT_PLAYLIST_ID = "intentPlaylistId";
    public static final String INTENT_COMING_FROM = "intentComingFrom";
    public static final String INTENT_NOTIFICATION_UNREAD_COUNT = "intent_notification_unread_count";
    public static final String INTENT_LANGUAGE = "intentLanguages";
    public static final String INTENT_GENRES = "intentGenres";
    public static final String INTENT_IS_FROM_GOOGLE = "is_from_google";
    public static final String INTENT_IS_ADMIN = "is_admin";


    public static final String TERMS_COND_KEY = "terms_cond_key";
    public static final String IS_VERIFY = "is_verify";


    // APIs Key
    public static final String SIGNUP_BY_APP = "VibinSignUp";
    public static final String SIGNUP_BY_Google = "GoogleSignUp";
    public static String YOUTUBE = "youtube";
    public static String SPOTIFY = "spotify";
    public static String TOKEN = "Token ";


    public static String MALE = "male";
    public static String FEMALE = "female";
    public static String OTHER = "others";

    // API STATUS KEYS
    public static String STATUS_UPDATED = "Updated";

    public static String GIPHY_API_KEY = "";


    // NOTIFICATION TYPE
    public static String SONG_UPDATED = "song_updated";
    public static String COLLAB_REQUEST = "collab_request";
    public static String COLLAB_INVITE = "collab_invite";
    public static String REAL_TIME_INVITE = "real_time_invite";
    public static String COLLAB_ACCEPTED = "collab_accepted";

    public static String COLLAB_REQUEST_RESPONDED = "collab_request_responded";
    public static String COLLAB_INVITE_RESPONDED = "collab_invite_responded";

    public static String PROFILE_LIKE = "profile_like";

    // REAL_TIME
    public static String INTENT_SESSION_KEY = "intentSessionKey";
    public static String INTENT_USER_KEY = "intentUserKey";
    public static String SESSION = "SESSIONS";
    public static String SESSION_CHILD = "session_child_";
    public static String USER = "USERS";
    public static String USER_CHILD = "user_child_";
    public static String WAIT = "WAIT";
    public static String READY = "READY";
    public static String START = "START";
    public static String STOP = "STOP";
    public static String JOINED = "JOINED";
    public static String ENDED = "ENDED";
    public static String PAUSE = "PAUSE";
    public static String PLAY = "PLAY";
    public static String CHANGED = "CHANGED";
    public static String REPEAT = "REPEAT";
    public static String SUFFLE = "SUFFLE";
    public static String SEEKSONG = "SEEKSONG";

    public static String ACCEPTED = "ACCEPTED";
    public static String REGECTED = "REGECTED";
    public static String GET = "get";
    public static String UPDATE = "update";
    public static String NOTIFICATION = "NOTIFICATION";

    public static String TERMS_COND_TYPE = "terms&conditions";
    public static String PRIVACY_POLICY_TYPE = "privacypolicy";
    public static String USER_SEND_NOTIFICATION = "user_send_notification";

    public static String PREF_YOUTUBE_KEY = "pref_youtube_key";
    public static String PREF_LAST_FM_KEY = "pref_last_fm_key";
    public static String PREF_GIPHY_KEY = "pref_giphy_key";
//    public static String PREF_SHARE_PLAYLIST_RESPONSE = "share_playlist_response";
    public static String SOURCE_TYPE_IN_APP = "IN_APP";
    public static String SOURCE_TYPE_DYNAMIC_LINK = "DYNAMIC_LINK";
    public static String PLAYLIST_UID = "playlist_uid";


}
