package com.shorincity.vibin.music_sharing.service;


import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.AddSongLogModel;
import com.shorincity.vibin.music_sharing.model.AddTrackResponse;
import com.shorincity.vibin.music_sharing.model.AdditionalSignUpModel;
import com.shorincity.vibin.music_sharing.model.CollabsList;
import com.shorincity.vibin.music_sharing.model.CreateSessionModel;
import com.shorincity.vibin.music_sharing.model.GetNotifications;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.model.LogoutModel;
import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.PlaylistLikeModel;
import com.shorincity.vibin.music_sharing.model.PlaylistSongCollabDeleteModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.model.SignUpResponse;
import com.shorincity.vibin.music_sharing.model.SignUpUserNameCheckModel;
import com.shorincity.vibin.music_sharing.model.SongLikeModel;
import com.shorincity.vibin.music_sharing.model.TermsAndConditionsModel;
import com.shorincity.vibin.music_sharing.model.UpdateLikeStatusModel;
import com.shorincity.vibin.music_sharing.model.UpdateNotificationModel;
import com.shorincity.vibin.music_sharing.model.UpdatePreferPlatformModel;
import com.shorincity.vibin.music_sharing.model.UserLikeList;
import com.shorincity.vibin.music_sharing.model.UserProfileModel;
import com.shorincity.vibin.music_sharing.model.UserSearchModel;
import com.shorincity.vibin.music_sharing.model.YoutubeChannelModel;
import com.shorincity.vibin.music_sharing.model.YoutubeGuideCategoryModel;
import com.shorincity.vibin.music_sharing.model.YoutubePlaylistItemModel;
import com.shorincity.vibin.music_sharing.model.YoutubeTrendingModel;
import com.shorincity.vibin.music_sharing.model.avatar.AvatarDetails;
import com.shorincity.vibin.music_sharing.model.coverart.CoverArtImageResponse;
import com.shorincity.vibin.music_sharing.model.lastfm.LastFMSearchResponse;
import com.shorincity.vibin.music_sharing.model.lastfm.trackinfo.TrackInfoResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// youtube Data Api

public interface DataAPI {
    @GET("search")
    Call<ModelData> getResurt(@Query("part") String part,
                              @Query("q") String q,
                              @Query("maxResults") String maxResults,
                              @Query("type") String type,
                              @Query("videoCategoryId") String id,
                              @Query("videoSyndicated") String vSyndicated,
                              @Query("key") String key);

    @GET("videos")
    Call<YoutubeTrendingModel> getYoutubeVideosList(@Query("part") String part,
                                                    @Query("chart") String mostPopular,
                                                    @Query("regionCode") String regionCode,
                                                    @Query("maxResults") String maxResults,
                                                    @Query("videoCategoryId") String videoCategory,
                                                    @Query("key") String key,
                                                    @Query("pageToken") String pageToken);

    @GET("search")
    Call<ModelData> getYoutubeSearchList(@Query("part") String part,
                                         @Query("chart") String mostPopular,
                                         @Query("q") String q,
                                         @Query("maxResults") String maxResults,
                                         @Query("type") String type,
                                         @Query("key") String key);


    //https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId={ChannelID}&key={API key} & maxResults=50
//    @GET("playlists")
//    Call<YoutubeTrendingModel> getYoutubeChannelsPlayList(@Query("part") String part,
//                                                    @Query("chart") String mostPopular,
//                                                    @Query("regionCode") String regionCode,
//                                                    @Query("channelId") String channelId,
//                                                    @Query("key") String key);

    @GET("playlists")
    Call<YoutubeTrendingModel> getYoutubeChannelsPlayList(@Query("part") String part,
                                                          @Query("channelId") String channelId,
                                                          @Query("key") String key,
                                                          @Query("maxResults") String maxResults);

    //https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&key= {API key} &maxResults=50 &playlistId = {id}
//    @GET("playlistItems")
//    Call<YoutubePlaylistItemModel> getYoutubePlayListItem(@Query("part") String part,
//                                                          @Query("maxResults") String maxResults,
//                                                          @Query("playlistId") String playlistId,
//                                                          @Query("videoId") String videoId,
//                                                          @Query("key") String key);


    @GET("playlistItems")
    Call<YoutubePlaylistItemModel> getYoutubePlayListItem(@Query("part") String part,
                                                          @Query("key") String key,
                                                          @Query("maxResults") String maxResults,
                                                          @Query("playlistId") String playlistId

    );

    @GET("guideCategories")
    Call<YoutubeGuideCategoryModel> getYoutubeGuideCategoryModel(@Query("part") String part,
                                                                 @Query("regionCode") String regionCode,
                                                                 @Query("key") String key);

    @GET("channels")
    Call<YoutubeChannelModel> getYoutubeChannelList(@Query("part") String part,
                                                    @Query("categoryId") String categoryId,
                                                    @Query("maxResults") String maxResults,
                                                    @Query("key") String key);

    // VIBIN APIS.............
    @FormUrlEncoded
    @POST("/user/login/")
    Call<AdditionalSignUpModel> login(@Header("Authorization") String loginSignUpHeader,
                                      @Field("email") String email,
                                      @Field("password") String password,
                                      @Field("loginType") String loginType);

    @FormUrlEncoded
    @POST("/user/signup/additionals/")
    Call<AdditionalSignUpModel> postAdditionalFields(@Header("Authorization") String loginSignUpHeader,
                                                     @Field("customer") int customer,
                                                     @Field("sex") String sex,
                                                     @Field("preferredPlatform") String platform,
                                                     @Field("dobUser") String dobUser);

    @FormUrlEncoded
    @POST("/user/logout/")
    Call<LogoutModel> logout(@Header("Authorization") String apiToken,
                             @Field("token") String email);

    @FormUrlEncoded
    @POST("/user/update/user_prefer_platform/")
    Call<UpdatePreferPlatformModel> callUpdatePreferredPlatform(@Header("Authorization") String token,
                                                                @Field("customer_id") int customer,
                                                                @Field("new_platform") String platform);

    @FormUrlEncoded
    @POST("/user/signup/")
    Call<SignUpResponse> postSignUpFields(@Header("Authorization") String loginSignUpHeader,
                                          @Field("email") String email,
                                          @Field("password") String password,
                                          @Field("username") String username,
                                          @Field("fullname") String fullname,
                                          @Field("typeOfRegistration") String typeOfRegistration,
                                          @Field("timeOfRegistration") String timeOfRegistration,
                                          @Field("pushNotifications") String pushNotifications,
                                          @Field("avatar_link") String avatarLink,
                                          @Field("sex") String sex,
                                          @Field("dobUser") String dobUser,
                                          @Field("preferredPlatform") String platform,
                                          @Field("preferredLanguage") String languages,
                                          @Field("preferredGenre") String genres);

    @FormUrlEncoded
    @POST("/user/signup_google/")
    Call<SignUpResponse> postGoogleSignup(@Header("Authorization") String loginSignUpHeader,
                                          @Field("email") String email,
                                          @Field("name") String name,
                                          @Field("picture") String picture);

    @FormUrlEncoded
    @POST("/user/add_preferred_lang_genres/")
    Call<APIResponse> postUpdateProfile(@Header("Authorization") String token,
                                        @Field("token") String token1,
                                        @Field("preferredLanguage") String languages,
                                        @Field("preferredGenre") String genres);

    @FormUrlEncoded
    @POST("/user/load_user_details/")
    Call<SignUpResponse> getUserDetail(@Header("Authorization") String token,
                                       @Field("token") String token1);

    @FormUrlEncoded
    @POST("/user/signup/usernamechecker/")
    Call<SignUpUserNameCheckModel> signupUsernameChecker(@Header("Authorization") String loginSignUpHeader, @Field("username") String username);

    @FormUrlEncoded
    @POST("/user/signup/emailchecker/")
    Call<SignUpUserNameCheckModel> signupEmailChecker(@Header("Authorization") String loginSignUpHeader, @Field("email") String email);

    @GET("/user/getusers")
    Call<ArrayList<UserSearchModel>> searchUsers(@Header("Authorization") String token, @Query("search") String search);

    @FormUrlEncoded
    @POST("/playlist/add_song_log/")
    Call<AddSongLogModel> addSongLogAPI(@Header("Authorization") String token, @Field("customer_id") int customer_id,
                                        @Field("song_type") String song_type,
                                        @Field("song_name") String song_name,
                                        @Field("song_id") String song_id,
                                        @Field("song_uri") String song_uri,
                                        @Field("song_thumbnail") String song_thumbnail,
                                        @Field("song_details") String song_details,
                                        @Field("artist_name") String artistName,
                                        @Field("song_duration") String duration);

    @FormUrlEncoded
    @POST("/playlist/get_song_like_status/")
    Call<SongLikeModel> getSongLikeStatus(@Header("Authorization") String token, @Field("customer_id") int customer_id,
                                          @Field("song_id") String song_id);

    @FormUrlEncoded
    @POST("/playlist/put_song_like_status/")
    Call<AddSongLogModel> putSongLikeStatus(@Header("Authorization") String token, @Field("customer_id") int customer_id,
                                            @Field("song_id") String song_id,
                                            @Field("liked") String isLiked);

    @GET("/playlist/get_recent_all/")
    Call<ArrayList<RecentSongModel>> getRecentAllSong(@Header("Authorization") String token, @Query("search") int customer_id);

    @GET("/playlist/get_recent_songs/")
    Call<ArrayList<RecentSongModel>> getRecentSongs(@Header("Authorization") String token, @Query("search") int customer_id);

    @GET("/playlist/liked_songs_list/")
    Call<ArrayList<RecentSongModel>> getLikedSongs(@Header("Authorization") String token, @Query("search") int customer_id);

    @FormUrlEncoded
    @POST("/playlist/my_playlists/")
    Call<ArrayList<MyPlaylistModel>> getMyPlaylist(@Header("Authorization") String token, @Field("token") String token1);

    @FormUrlEncoded
    @POST("/playlist/search_playlist/")
    Call<ArrayList<MyPlaylistModel>> getSearchedPlaylist(@Header("Authorization") String token, @Field("token") String token1, @Field("search_term") String searchedTerm);

    @FormUrlEncoded
    @POST("/playlist/get_public_playlists/")
    Call<ArrayList<MyPlaylistModel>> getPublicPlaylist(@Header("Authorization") String token, @Field("customer_id") int userId);

    @FormUrlEncoded
    @POST("/playlist/playlist_detail/")
    Call<ArrayList<PlaylistDetailModel>> getPublicPlaylistDetail(@Header("Authorization") String token, @Field("token") String token1, @Field("playlist") String id);

    @FormUrlEncoded
    @POST("/playlist/put_playlist_likes/")
    Call<PlaylistLikeModel> putPlaylistLike(@Header("Authorization") String token, @Field("token") String token1, @Field("playlist") int id);

    @FormUrlEncoded
    @POST("/playlist/put_playlist_likes/")
    Call<MyPlaylistModel> putPlaylistLikeNew(@Header("Authorization") String token, @Field("token") String token1, @Field("playlist") int id);

    @FormUrlEncoded
    @POST("/playlist/add_collab/")
    Call<APIResponse> addCollab(@Header("Authorization") String token, @Field("playlist_id") int playlistId,
                                @Field("customer") int customerId);

    @FormUrlEncoded
    @POST("/user/update_collab_notify_status/")
    Call<UpdateNotificationModel> updateCollabNotifyStatus(@Header("Authorization") String token,
                                                           @Field("playlist_id") int playlistId,
                                                           @Field("admin_id") int adminId,
                                                           @Field("user_ids") int userId,
                                                           @Field("notify_id") int notifyId,
                                                           @Field("status") String status,
                                                           @Field("type") String type);


    @FormUrlEncoded
    @POST("/user/delete_realtime_info/")
    Call<APIResponse> deleteRealTimeSession(@Header("Authorization") String token, @Field("session_key") String sessionKey);


    @FormUrlEncoded
    @POST("/user/liked_disliked/userdata_get/")
    Call<UserProfileModel> getUserProfile(@Header("Authorization") String token, @Field("user_profile") int userId,
                                          @Field("checking_profile") int customerId);

    @FormUrlEncoded
    @POST("/user/liked_disliked/userdata_update/")
    Call<UpdateLikeStatusModel> updateLikeUserProfile(@Header("Authorization") String token,
                                                      @Field("liked_by_user") int userId,
                                                      @Field("liked_to_user") int customerId,
                                                      @Field("current_like_status") String likeStatus);


    @FormUrlEncoded
    @POST("/playlist/get_youtube_home_playlists/")
    Call<HomeYoutubeModel> getYoutubePlaylistAtHome(@Header("Authorization") String token,
                                                    @Field("platform") String notifyToken);

    // RealTime Session API...............................................
    @FormUrlEncoded
    @POST("/user/add_realtime_info/")
    Call<APIResponse> addRealTimeInfo(@Header("Authorization") String token,
                                      @Field("playlist_id") int playlistId,
                                      @Field("admin_id") int adminId,
                                      @Field("session_key") String sessionKey,
                                      @Field("session_token") String sessionToken,
                                      @Field("user_ids") String userIds,
                                      @Field("user_session_keys") String userSessionKeys);

    // Notification API....................................................
    @FormUrlEncoded
    @POST("/user/add_notification_token/")
    Call<APIResponse> addNotificationToken(@Header("Authorization") String token,
                                           @Field("login_token") String loginToken,
                                           @Field("notify_token") String notifyToken);

    /*@FormUrlEncoded
    @POST("/user/send_notification/")
    Call<APIResponse> sendNotification(@Header("Authorization") String token,
                                           @Field("sender") int senderId,
                                           @Field("receiver") int receiverId,
                                           @Field("type") String type);*/

    @FormUrlEncoded
    @POST("/user/send_notification/")
    Call<APIResponse> sendNotification(@Header("Authorization") String token,
                                       @Field("sender") int senderId,
                                       @Field("receiver") int receiverId,
                                       @Field("playlist") int playlist,
                                       @Field("type") String type);

    /*@FormUrlEncoded
    @POST("/user/send_notification_song_update/")
    Call<APIResponse> sendNotificationSongUpdate(@Header("Authorization") String token,
                                       @Field("sender") int senderId,
                                       @Field("playlist") int playlist);*/

    @FormUrlEncoded
    @POST("/user/send_notification_song_update/")
    Call<APIResponse> sendNotificationSongUpdate(@Header("Authorization") String token,
                                                 @Field("sender") int senderId,
                                                 @Field("playlist") int playlist,
                                                 @Field("type") String type);


    @FormUrlEncoded
    @POST("/user/send_notification_song_update/")
    Call<APIResponse> sendNotificationRealTimeUpdate(@Header("Authorization") String token,
                                                     @Field("sender") int senderId,
                                                     @Field("playlist") int playlist,
                                                     @Field("type") String type);

    @FormUrlEncoded
    @POST("/user/mark_all_notification_read/")
    Call<APIResponse> markAllNotificationRead(@Header("Authorization") String token,
                                              @Field("customer") int userId);

    @FormUrlEncoded
    @POST("/user/mark_notification_read/")
    Call<APIResponse> markNotificationRead(@Header("Authorization") String token,
                                           @Field("notify_id") int notifyID);

    @FormUrlEncoded
    @POST("/user/get_unread_notification_count/")
    Call<APIResponse> getUnreadCount(@Header("Authorization") String token,
                                     @Field("customer_id") int userId);

    @GET("/user/get_notifications/")
    Call<ArrayList<GetNotifications>> getNotifications(@Header("Authorization") String token,
                                                       @Query("search") int userId);

    @GET("/user/get_policies/")
    Call<TermsAndConditionsModel> getTermsAndConditions(@Header("Authorization") String token,
                                                        @Query("type") String type);


    @FormUrlEncoded
    @POST("/user/get_avatars/")
    Call<AvatarDetails> getAvatars(@Header("Authorization") String token,
                                   @Field("gender") String gender);
// Yash's DevelopMent


    @FormUrlEncoded
    @POST("/user/liked_disliked/get_profiles/")
    Call<UserLikeList> getLikes_DisLikesListing(@Header("Authorization") String token,
                                                @Field("token") String usertoken);

    @FormUrlEncoded
    @POST("/user/send_verification_email/")
    Call<APIResponse> sendVerificationOtp(@Header("Authorization") String token,
                                          @Field("email") String email);


    @FormUrlEncoded
    @POST("/user/verify_email/")
    Call<APIResponse> sendVerifyEmail(@Header("Authorization") String token,
                                      @Field("email") String email,
                                      @Field("code_entered") String code);


    @FormUrlEncoded
    @POST("/playlist/get_collaborators_profiles/")
    Call<CollabsList> getCollabsListing(@Header("Authorization") String token,
                                        @Field("token") String usertoken);


    @FormUrlEncoded
    @POST("/playlist/delete_playlist/")
    Call<PlayListDeleteModel> getDeletePlaylist(
            @Header("Authorization") String token, @Field("token") String usertoken,
            @Field("playlist") int playlist);

    @FormUrlEncoded
    @POST("/playlist/edit_playlist/")
    Call<PlayListDeleteModel> getEditPlaylist(
            @Header("Authorization") String token, @Field("token") String usertoken,
            @Field("playlist") int playlist,
            @Field("songs_list") String songs_list,
            @Field("collabs_list") String collabs_list);

    @FormUrlEncoded
    @POST("/playlist/edit_playlist/")
    Call<PlaylistSongCollabDeleteModel> callDeleteSongsOrCollabApi(
            @Header("Authorization") String token, @Field("token") String usertoken,
            @Field("playlist") int playlist,
            @Field("songs_list") String songs_list,
            @Field("collabs_list") String collabs_list);

    @FormUrlEncoded
    @POST("/playlist/view_collab/")
    Call<List<ViewCollab>> getCollaboratorsList(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("playlist_id") String playListId
    );

    @FormUrlEncoded
    @POST("/playlist/like_playlist_song/")
    Call<PlaylistDetailModel> callPlaylistLike(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("playlist") Integer playListId,
            @Field("track") Integer trackId,
            @Field("liked") String liked

    );

    @FormUrlEncoded
    @POST("/playlist/create_new_playlist/")
    Call<MyPlaylistModel> callCreatePlayList(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("name") String name,
            @Field("description") String description,
            @Field("gif_link") String gif_link,
            @Field("private") String isPrivate,
            @Field("password") String password,
            @Field("playlist_tags") String playlistTags
    );

    @FormUrlEncoded
    @POST("/playlist/edit_playlist_basics/")
    Call<MyPlaylistModel> callEditPlaylistBasics(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("update_name") String name,
            @Field("playlist") Integer playListId,
            @Field("update_description") String description,
            @Field("update_gif_link") String gif_link,
            @Field("update_tags") String playlistTags,
            @Field("update_password") String password,
            @Field("update_private") String isPrivate
    );

    @FormUrlEncoded
    @POST("/playlist/add_trak_to_playlist/")
    Call<ResponseBody> callAddTrackApi(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("playlist") String playlist,
            @Field("type") String type,
            @Field("track_id") String trackId,
            @Field("name") String name,
            @Field("image") String image,
            @Field("artist_name") String artistName,
            @Field("song_duration") String songDuration
    );


    @GET("2.0?method=track.search")
    Call<LastFMSearchResponse> callTrackSearch(
            @Query("track") String track,
            @Query("api_key") String key,
            @Query("format") String format,
            @Query("limit") int limit
    );


    @GET("2.0/?method=track.getInfo")
    Call<TrackInfoResponse> callTrackInfoApi(
            @Query("api_key") String key,
            @Query("track") String track,
            @Query("artist") String artist,
            @Query("format") String format
    );

    @GET("/release/{mbid}")
    Call<CoverArtImageResponse> callImagesApi(@Path("mbid") String mbid);

    @FormUrlEncoded
    @POST("/user/create_real_time_session/")
    Call<CreateSessionModel> callCreateRTSession(
            @Header("Authorization") String token,
            @Field("token") String usertoken,
            @Field("playlist") String playlist
    );
}
