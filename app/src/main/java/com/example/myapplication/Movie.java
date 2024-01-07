package com.example.myapplication;
    public class Movie {
        private String title;
        private String imageUrl;
        private String videoUrl;

        // Constructor mặc định cần cho Firebase
        public Movie() {}

        // Constructor với tất cả các thuộc tính
        public Movie(String title, String imageUrl, String videoUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.videoUrl = videoUrl;
        }

        // Getter và Setter
        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }

