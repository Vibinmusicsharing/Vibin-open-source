package com.shorincity.vibin.music_sharing.model;

public abstract class Resource<T> {
    public static class Loading<T> extends Resource<T> {
    }

    public static class Success<T> extends Resource<T> {

        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class Error<T> extends Resource<T> {

        private String errorMsg;

        public Error(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
