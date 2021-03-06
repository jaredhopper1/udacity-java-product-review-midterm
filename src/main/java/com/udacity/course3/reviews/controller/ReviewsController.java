package com.udacity.course3.reviews.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import com.udacity.course3.reviews.entities.ReviewDocument;
import com.udacity.course3.reviews.repos.ReviewDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.udacity.course3.reviews.entities.Product;
import com.udacity.course3.reviews.entities.Review;
import com.udacity.course3.reviews.repos.ProductRepository;
import com.udacity.course3.reviews.repos.ReviewRepository;

/**
 * Spring REST controller for working with review entity.
 */
@RestController
public class ReviewsController {

    // Completed: Wire JPA repositories here
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewDocumentRepository reviewDocumentRepository;

    /**
     * Creates a review for a product.
     * <p>
     * 1. Add argument for review entity. Use {@link RequestBody} annotation.
     * 2. Check for existence of product.
     * 3. If product not found, return NOT_FOUND.
     * 4. If found, save review.
     *
     * @param productId The id of the product.
     * @return The created review or 404 if product id is not found.
     */
    @RequestMapping(value = "/reviews/products/{productId}", method = RequestMethod.POST)
    public ResponseEntity<?> createReviewForProduct(@PathVariable("productId") Integer productId,
                                                         @Valid @RequestBody Review review) {
        Optional<Product> optional = productRepository.findById(productId);
        if (optional.isPresent()) {
            Product product = optional.get();
            review.setProduct(product);

            // save to mysql
            Review mysqlReview = reviewRepository.save(review);

            // save to mongo
            ReviewDocument mongoReview =
                    reviewDocumentRepository.save(new ReviewDocument(mysqlReview));
            return ResponseEntity.ok(mongoReview);
        }
        else {
            return new ResponseEntity<Review>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Lists reviews by product.
     *
     * @param productId The id of the product.
     * @return The list of reviews.
     */
    @RequestMapping(value = "/reviews/products/{productId}", method = RequestMethod.GET)
    public ResponseEntity<List<?>> listReviewsForProduct(@PathVariable(
            "productId") Integer productId) {

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {

            List<ReviewDocument> reviewDocs =
                    reviewDocumentRepository.findAllByProductId(productId);
            return new ResponseEntity<>(reviewDocs, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}