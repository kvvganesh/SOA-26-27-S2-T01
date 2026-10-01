package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.TrustedLocationRequest;
import com.adaptivemfa.acs.exception.ApiException;
import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustedLocationService {

    private final TrustedLocationRepository repository;
    private final LocationRiskService locationRiskService;

    public TrustedLocationService(
            TrustedLocationRepository repository,
            LocationRiskService locationRiskService) {

        this.repository = repository;
        this.locationRiskService = locationRiskService;
    }

    public TrustedLocation addLocation(
            TrustedLocationRequest request) {

        Double accuracy = request.accuracy();

        double maxAccuracy =
                locationRiskService.getMaxAccuracyMeters();

        /*
         * Refuse to remember a position that could never be
         * matched again, and tell the user why.
         */
        if (accuracy != null && accuracy > maxAccuracy) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Your location is too imprecise to remember (about "
                            + Math.round(accuracy)
                            + " m, limit "
                            + Math.round(maxAccuracy)
                            + " m). Turn on precise location / Wi-Fi "
                            + "and try again."
            );
        }

        /*
         * Already covered by an existing trusted location?
         * Then don't create a duplicate.
         */
        for (TrustedLocation existing :
                repository.findByUsernameAndActiveTrue(
                        request.username())) {

            double distance =
                    LocationRiskService.distanceMeters(
                            request.latitude(),
                            request.longitude(),
                            existing.getLatitude(),
                            existing.getLongitude()
                    );

            if (distance <= existing.getRadiusMeters()) {

                return existing;
            }
        }

        double radius =
                locationRiskService.effectiveRadius(
                        request.radiusMeters(),
                        accuracy
                );

        TrustedLocation location =
                new TrustedLocation(
                        request.username(),
                        request.latitude(),
                        request.longitude(),
                        radius,
                        request.label()
                );

        return repository.save(location);
    }

    public List<TrustedLocation> getLocations(
            String username) {

        return repository.findByUsernameAndActiveTrue(username);
    }

    /**
     * Removes exactly the location the user clicked on.
     * (The old coordinate based removal deactivated the first
     * overlapping location it found, which could be a different
     * one.)
     */
    public void removeLocation(
            String username,
            Long id) {

        repository.findById(id)
                .filter(location ->
                        location.getUsername().equals(username)
                                && location.isActive())
                .ifPresent(location -> {

                    location.setActive(false);
                    repository.save(location);
                });
    }
}
