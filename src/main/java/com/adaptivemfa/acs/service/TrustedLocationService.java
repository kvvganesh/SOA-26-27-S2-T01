package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.TrustedLocationRequest;
import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustedLocationService {

    private final TrustedLocationRepository repository;

    public TrustedLocationService(
            TrustedLocationRepository repository) {

        this.repository = repository;
    }

    public TrustedLocation addLocation(
            TrustedLocationRequest request) {

        TrustedLocation location =
                new TrustedLocation(
                        request.username(),
                        request.latitude(),
                        request.longitude(),
                        request.radiusMeters(),
                        request.label()
                );

        return repository.save(location);
    }

    public List<TrustedLocation> getLocations(
            String username) {

        return repository.findByUsernameAndActiveTrue(username);
    }

    public boolean isTrusted(
            String username,
            Double latitude,
            Double longitude,
            Double accuracy) {

        List<TrustedLocation> locations =
                repository.findByUsernameAndActiveTrue(username);

        for (TrustedLocation location : locations) {

            double distance =
                    calculateDistance(
                            latitude,
                            longitude,
                            location.getLatitude(),
                            location.getLongitude()
                    );

            if (distance <= location.getRadiusMeters()
                    && accuracy <= location.getRadiusMeters()) {

                return true;
            }
        }

        return false;
    }

    public void removeLocation(
            String username,
            Double latitude,
            Double longitude) {

        List<TrustedLocation> locations =
                repository.findByUsernameAndActiveTrue(username);

        for (TrustedLocation location : locations) {

            double distance =
                    calculateDistance(
                            latitude,
                            longitude,
                            location.getLatitude(),
                            location.getLongitude()
                    );

            if (distance <= location.getRadiusMeters()) {

                location.setActive(false);
                repository.save(location);
                return;
            }
        }
    }

    private double calculateDistance(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        final double EARTH_RADIUS_METERS = 6371000;

        double latitudeDifference =
                Math.toRadians(latitude2 - latitude1);

        double longitudeDifference =
                Math.toRadians(longitude2 - longitude1);

        double a =
                Math.sin(latitudeDifference / 2)
                        * Math.sin(latitudeDifference / 2)
                        +
                        Math.cos(Math.toRadians(latitude1))
                                * Math.cos(Math.toRadians(latitude2))
                                * Math.sin(longitudeDifference / 2)
                                * Math.sin(longitudeDifference / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS_METERS * c;
    }
}