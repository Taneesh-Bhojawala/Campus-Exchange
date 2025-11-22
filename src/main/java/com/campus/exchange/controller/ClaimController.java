package com.campus.exchange.controller;

import com.campus.exchange.dto.ClaimRequest;
import com.campus.exchange.model.Claim;
import com.campus.exchange.model.Item;
import com.campus.exchange.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * Create a new claim
     * Body: ClaimRequest { itemId, claimerId, listerId (optional) }
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createClaim(@RequestBody ClaimRequest request) {
        try {
            if (request.getItemId() == null || request.getItemId().isBlank())
                return ResponseEntity.badRequest().body("itemId is required");
            if (request.getClaimerId() == null || request.getClaimerId().isBlank())
                return ResponseEntity.badRequest().body("claimerId is required");

            // call service exactly as provided
            Claim created = claimService.createClaim(request.getItemId(), request.getClaimerId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    /**
     * Accept a claim.
     * Body: Claim (the service accepts a Claim object)
     */
    @PutMapping(path = "/accept", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> acceptClaim(@RequestBody Claim claim) {
        try {
            Claim accepted = claimService.acceptClaim(claim);
            return ResponseEntity.ok(accepted);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    /**
     * Reject a claim.
     * Body: Claim
     */
    @PutMapping(path = "/reject", consumes = "application/json")
    public ResponseEntity<?> rejectClaim(@RequestBody Claim claim) {
        try {
            Claim rejected = claimService.rejectClaim(claim);
            return ResponseEntity.ok(rejected);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    /**
     * Relist a claim / item after failed offline deal (blocks the claimer for 7 days).
     * Body: Claim
     */
    @PutMapping(path = "/relist", consumes = "application/json")
    public ResponseEntity<?> relistClaim(@RequestBody Claim claim) {
        try {
            claimService.relistClaim(claim);
            return ResponseEntity.ok("Item relisted and claimer blocked (if applicable).");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    /**
     * Owner marks a waiting item as completed.
     * Path: /api/claims/item/{itemId}/complete
     */
    @PutMapping(path = "/item/{itemId}/complete", produces = "application/json")
    public ResponseEntity<?> completeDeal(@PathVariable("itemId") String itemId) {
        try {
            claimService.completeDeal(itemId);
            return ResponseEntity.ok("Item marked as completed.");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
}
