package com.reviews.service;

import com.reviews.entity.Review;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReviewService {
    private final List<Review> reviews = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Добавляем тестовые данные
    public ReviewService() {
        reviews.add(new Review("Анна Петрова", "iPhone 14", 5, "Отличный телефон!"));
        reviews.add(new Review("Иван Сидоров", "Samsung Galaxy", 4, "Хороший телефон"));
        reviews.add(new Review("Мария Иванова", "MacBook Pro", 5, "Лучший ноутбук"));

        // Устанавливаем ID для тестовых данных
        for (int i = 0; i < reviews.size(); i++) {
            reviews.get(i).setId((long) (i + 1));
        }
        idCounter.set(reviews.size() + 1);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviews.stream()
                .filter(review -> review.getId().equals(id))
                .findFirst();
    }

    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setId(idCounter.getAndIncrement());
            reviews.add(review);
        } else {
            // Обновление существующего отзыва
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getId().equals(review.getId())) {
                    reviews.set(i, review);
                    break;
                }
            }
        }
        return review;
    }

    public void deleteReview(Long id) {
        reviews.removeIf(review -> review.getId().equals(id));
    }
}