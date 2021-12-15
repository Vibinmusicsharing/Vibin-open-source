
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastFMSearchResponse {

    @SerializedName("results")
    private Results mResults;

    public Results getResults() {
        return mResults;
    }

    public void setResults(Results results) {
        mResults = results;
    }

}
