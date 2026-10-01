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
     * When the store grows beyond this size, expired windows are
     * purged so that random usernames cannot fill the memory.
     */
    private static final int PURGE_THRESHOLD = 10_000;


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

        if (requestStore.size() > PURGE_THRESHOLD) {

            requestStore.entrySet().removeIf(entry ->
                    isExpired(entry.getValue(), now));
        }


        /*
         * compute() is atomic for a key, so simultaneous requests
         * can't use the same counter value. A new window starts
         * (with count 1) when there is no counter yet or the old
         * window has expired; otherwise the counter is incremented.
         *
         * (The previous implementation counted the very first
         * request twice.)
         */
        RequestCounter counter =
                requestStore.compute(
                        key,
                        (k, existing) -> {

                            if (existing == null
                                    || isExpired(existing, now)) {

                                return new RequestCounter(now);
                            }

                            existing.increment();

                            return existing;
                        }
                );


        if (counter.get() > MAX_ATTEMPTS) {

            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }

        return true;
    }


    private boolean isExpired(
            RequestCounter counter,
            LocalDateTime now) {

        return now.isAfter(
                counter.getWindowStart()
                        .plusMinutes(WINDOW_MINUTES)
        );
    }


    /*
     * Represents one rate-limit window.
     */
    private static class RequestCounter {

        private final LocalDateTime windowStart;

        private final AtomicInteger count;


        RequestCounter(LocalDateTime windowStart) {

            this.windowStart = windowStart;

            /*
             * The first request is counted immediately.
             */
            this.count = new AtomicInteger(1);
        }


        LocalDateTime getWindowStart() {

            return windowStart;
        }


        void increment() {

            count.incrementAndGet();
        }


        int get() {

            return count.get();
        }
    }
}
