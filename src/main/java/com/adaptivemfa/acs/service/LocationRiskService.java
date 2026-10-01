package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.model.TrustedLocation;
import com.adaptivemfa.acs.repository.TrustedLocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Single place that decides whether a position is "trusted".
 *
 * A login position is trusted when
 *   1. the browser reported a usable fix (accuracy no worse than
 *      {@code mfa.location.max-accuracy-meters}), and
 *   2. it lies inside the radius of one of the user's active
 *      trusted locations.
 *
 * The old rule ("accuracy must be <= the radius, i.e. <= 100 m")
 * rejected almost every desktop / laptop fix, so a location the
 * user had just remembered was never recognised again.
 */
@Service
public class LocationRiskService {

    private static final double EARTH_RADIUS_METERS = 6371000;

    private static final double DEFAULT_RADIUS_METERS = 100;

    private static final double MIN_RADIUS_METERS = 50;

    private final TrustedLocationRepository repository;

    private final double maxAccuracyMeters;


    public LocationRiskService(
            TrustedLocationRepository repository,
            @Value("${mfa.location.max-accuracy-meters:1000}")
            double maxAccuracyMeters) {

        this.repository = repository;
        this.maxAccuracyMeters = maxAccuracyMeters;
    }


    public boolean isTrustedLocation(
            String username,
            Double latitude,
            Double longitude,
            Double accuracy) {

        /*
         * Location was denied / unavailable on the client:
         * it simply cannot be trusted.
         */
        if (latitude == null
                || longitude == null
                || accuracy == null) {

            return false;
        }

        /*
         * A very coarse fix (for example an IP based one) says
         * nothing reliable about where the user really is.
         */
        if (accuracy > maxAccuracyMeters) {

            return false;
        }

        for (TrustedLocation trusted :
                repository.findByUsernameAndActiveTrue(username)) {

            double distance =
                    distanceMeters(
                            latitude,
                            longitude,
                            trusted.getLatitude(),
                            trusted.getLongitude()
                    );

            if (distance <= trusted.getRadiusMeters()) {

                return true;
            }
        }

        return false;
    }


    public double getMaxAccuracyMeters() {

        return maxAccuracyMeters;
    }


    /**
     * Radius to store for a new trusted location: at least what
     * the client asked for and at least as large as the accuracy
     * of the fix it was taken from (otherwise normal GPS jitter
     * would immediately fall outside the circle).
     */
    public double effectiveRadius(
            Double requestedRadius,
            Double accuracy) {

        double radius =
                requestedRadius == null
                        ? DEFAULT_RADIUS_METERS
                        : requestedRadius;

        if (accuracy != null) {

            radius = Math.max(radius, accuracy);
        }

        return Math.min(
                Math.max(radius, MIN_RADIUS_METERS),
                maxAccuracyMeters
        );
    }


    public static double distanceMeters(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

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
