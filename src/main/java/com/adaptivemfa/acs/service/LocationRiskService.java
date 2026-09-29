package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationRiskService {

    private final TrustedLocationRepository repository;

    public LocationRiskService(TrustedLocationRepository repository) {
        this.repository = repository;
    }

    public boolean isTrustedLocation(
            String username,
            Double latitude,
            Double longitude,
            Double accuracy) {

        List<TrustedLocation> trustedLocations =
                repository.findByUsernameAndActiveTrue(username);

        for (TrustedLocation trustedLocation : trustedLocations) {

            double distance = calculateDistance(
                    latitude,
                    longitude,
                    trustedLocation.getLatitude(),
                    trustedLocation.getLongitude()
            );

            if (distance <= trustedLocation.getRadiusMeters()) {

                if (accuracy <= trustedLocation.getRadiusMeters()) {
                    return true;
                }
            }
        }

        return false;
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