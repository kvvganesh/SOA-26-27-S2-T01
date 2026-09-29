package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    /*
     * Maximum number of requests allowed
     * during one time window.
     */
    private static final int MAX_ATTEMPTS = 5;

    /*
     * Length of the rate-limit window.
     */
    private static final int WINDOW_MINUTES = 1;


    /*
     * Stores a separate counter for every key.
     *
     * Example:
     *
     * LOGIN:ganesh
     * MFA:ganesh
     * RESET:ganesh
     */
    private final Map<String, RequestCounter> requestStore =
            new ConcurrentHashMap<>();


    public boolean isAllowed(String key) {

        LocalDateTime now =
                LocalDateTime.now();


        /*
         * Create a new counter if this is
         * the first request from this key.
         */
        RequestCounter counter =
                requestStore.computeIfAbsent(
                        key,
                        k -> new RequestCounter(now)
                );


        /*
         * Check whether the current
         * one-minute window has expired.
         */
        if (now.isAfter(
                counter.getWindowStart()
                        .plusMinutes(WINDOW_MINUTES))) {

            /*
             * Start a completely new window.
             */
            RequestCounter newCounter =
                    new RequestCounter(now);

            requestStore.put(
                    key,
                    newCounter
            );

            return true;
        }


        /*
         * Atomically increase the request count.
         *
         * This prevents multiple simultaneous
         * requests from incorrectly using the
         * same counter value.
         */
        int currentCount =
                counter.incrementAndGet();


        /*
         * Check whether the limit was exceeded.
         */
        if (currentCount > MAX_ATTEMPTS) {

            /*
             * Don't allow more requests.
             */
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }


        return true;
    }


    /*
     * Represents one rate-limit window.
     */
    private static class RequestCounter {

        private final LocalDateTime windowStart;

        private final AtomicInteger count;


        public RequestCounter(
                LocalDateTime windowStart) {

            this.windowStart = windowStart;

            /*
             * The first request is counted immediately.
             */
            this.count =
                    new AtomicInteger(1);
        }


        public LocalDateTime getWindowStart() {

            return windowStart;
        }


        public int incrementAndGet() {

            return count.incrementAndGet();
        }
    }
}

