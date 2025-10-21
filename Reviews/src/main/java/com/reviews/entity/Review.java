package com.reviews.entity;

public class Review {
    private Long id;
    private String userName;
    private String product;
    private Integer rating;
    private String comment;

    public Review() {}

    public Review(String userName, String product, Integer rating, String comment) {
        this.userName = userName;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getRatingStars() {
        if (rating == null) return "☆☆☆☆☆";
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    @Override
    public String toString() {
        return String.format("Review{id=%d, user='%s', product='%s', rating=%d}",
                id, userName, product, rating);
    }
}