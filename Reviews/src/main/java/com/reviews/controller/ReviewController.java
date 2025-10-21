package com.reviews.controller;

import com.reviews.entity.Review;
import com.reviews.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        if (!model.containsAttribute("review")) {
            model.addAttribute("review", new Review());
        }
        return "reviews";
    }

    @PostMapping("/add")
    public String addReview(@ModelAttribute Review review, Model model) {
        System.out.println("=== ДОБАВЛЕНИЕ ОТЗЫВА ===");
        System.out.println("Пользователь: " + review.getUserName());
        System.out.println("Продукт: " + review.getProduct());
        System.out.println("Рейтинг: " + review.getRating());
        System.out.println("Комментарий: " + review.getComment());

        try {
            reviewService.saveReview(review);
            return "redirect:/reviews";
        } catch (Exception e) {
            System.out.println("ОШИБКА: " + e.getMessage());
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("reviews", reviewService.getAllReviews());
            return "reviews";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Review> review = reviewService.getReviewById(id);
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            model.addAttribute("reviews", reviewService.getAllReviews());
            return "reviews";
        }
        return "redirect:/reviews";
    }

    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute Review review, Model model) {
        try {
            review.setId(id);
            reviewService.saveReview(review);
            return "redirect:/reviews";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении: " + e.getMessage());
            model.addAttribute("reviews", reviewService.getAllReviews());
            return "reviews";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }
}