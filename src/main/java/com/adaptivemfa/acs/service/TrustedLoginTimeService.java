package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.RememberLoginTimeRequest;
import com.adaptivemfa.acs.dto.TrustedLoginTimeRequest;
import com.adaptivemfa.acs.model.TrustedLoginTime;
import com.adaptivemfa.acs.repository.TrustedLoginTimeRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustedLoginTimeService {

    private final TrustedLoginTimeRepository repository;


    public TrustedLoginTimeService(
            TrustedLoginTimeRepository repository) {

        this.repository = repository;
    }


    // =========================================================
    // REGISTRATION / SECURITY ENROLLMENT
    // =========================================================

    public TrustedLoginTime addTime(
            TrustedLoginTimeRequest request) {

        validateHours(
                request.startHour(),
                request.endHour()
        );


        TrustedLoginTime time =
                new TrustedLoginTime(
                        request.username(),
                        request.startHour(),
                        request.endHour()
                );


        return repository.save(time);
    }


    // =========================================================
    // REMEMBER LOGIN TIME AFTER AUTHENTICATION
    // =========================================================

    public TrustedLoginTime rememberTime(
            String username,
            RememberLoginTimeRequest request) {


        validateHours(
                request.startHour(),
                request.endHour()
        );


        /*
         * Avoid creating an exact duplicate.
         */

        List<TrustedLoginTime> existingTimes =
                repository
                        .findByUsernameAndActiveTrue(username);


        for (TrustedLoginTime existing :
                existingTimes) {


            if (existing.getStartHour()
                    == request.startHour()
                    &&
                    existing.getEndHour()
                            == request.endHour()) {


                return existing;
            }
        }


        TrustedLoginTime time =
                new TrustedLoginTime(
                        username,
                        request.startHour(),
                        request.endHour()
                );


        return repository.save(time);
    }


    // =========================================================
    // GET TRUSTED LOGIN TIMES
    // =========================================================

    public List<TrustedLoginTime> getTimes(
            String username) {

        return repository
                .findByUsernameAndActiveTrue(username);
    }


    // =========================================================
    // CHECK TRUSTED LOGIN TIME
    // =========================================================

    public boolean isTrustedTime(
            String username,
            int loginHour) {


        List<TrustedLoginTime> trustedTimes =
                repository
                        .findByUsernameAndActiveTrue(
                                username
                        );


        for (TrustedLoginTime time :
                trustedTimes) {


            if (isHourInsideRange(
                    loginHour,
                    time.getStartHour(),
                    time.getEndHour())) {

                return true;
            }
        }


        return false;
    }


    // =========================================================
    // REMOVE TRUSTED LOGIN TIME
    // =========================================================

    public void removeTime(
            String username,
            Long id) {


        repository.findById(id)
                .filter(time ->
                        time.getUsername()
                                .equals(username)
                                &&
                                time.isActive()
                )
                .ifPresent(time -> {

                    time.setActive(false);

                    repository.save(time);
                });
    }


    // =========================================================
    // VALIDATE HOURS
    // =========================================================

    private void validateHours(
            int startHour,
            int endHour) {


        if (startHour < 0 ||
                startHour > 23 ||
                endHour < 0 ||
                endHour > 23) {


            throw new IllegalArgumentException(
                    "Login hour must be between 0 and 23"
            );
        }
    }


    // =========================================================
    // CHECK HOUR RANGE
    // =========================================================

    private boolean isHourInsideRange(
            int hour,
            int start,
            int end) {


        if (start <= end) {

            return hour >= start &&
                    hour <= end;

        } else {

            /*
             * Handles ranges such as:
             *
             * 22 → 02
             *
             * which means:
             *
             * 22, 23, 00, 01, 02
             */

            return hour >= start ||
                    hour <= end;
        }
    }
}