
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("@attr")
    private Attr mAttr;
    @SerializedName("opensearch:itemsPerPage")
    private String mOpensearchItemsPerPage;
    @SerializedName("opensearch:Query")
    private OpensearchQuery mOpensearchQuery;
    @SerializedName("opensearch:startIndex")
    private String mOpensearchStartIndex;
    @SerializedName("opensearch:totalResults")
    private String mOpensearchTotalResults;
    @SerializedName("trackmatches")
    private Trackmatches mTrackmatches;

    public Attr getAttr() {
        return mAttr;
    }

    public void setAttr(Attr attr) {
        mAttr = attr;
    }

    public String getOpensearchItemsPerPage() {
        return mOpensearchItemsPerPage;
    }

    public void setOpensearchItemsPerPage(String opensearchItemsPerPage) {
        mOpensearchItemsPerPage = opensearchItemsPerPage;
    }

    public OpensearchQuery getOpensearchQuery() {
        return mOpensearchQuery;
    }

    public void setOpensearchQuery(OpensearchQuery opensearchQuery) {
        mOpensearchQuery = opensearchQuery;
    }

    public String getOpensearchStartIndex() {
        return mOpensearchStartIndex;
    }

    public void setOpensearchStartIndex(String opensearchStartIndex) {
        mOpensearchStartIndex = opensearchStartIndex;
    }

    public String getOpensearchTotalResults() {
        return mOpensearchTotalResults;
    }

    public void setOpensearchTotalResults(String opensearchTotalResults) {
        mOpensearchTotalResults = opensearchTotalResults;
    }

    public Trackmatches getTrackmatches() {
        return mTrackmatches;
    }

    public void setTrackmatches(Trackmatches trackmatches) {
        mTrackmatches = trackmatches;
    }

}
