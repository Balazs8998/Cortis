package com.cortis.core.security.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Instant;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRateLimitStore {

    private final ConcurrentHashMap<String, RateLimitWindow> windows = new ConcurrentHashMap<>();

    // Utána átnézzük, és csak azután alakítjuk át compute(...)-os, szebb verzióra.

  public RateLimitResult check(String key, RateLimitRule rule){
      Instant now = Instant.now();

      RateLimitWindow window = windows.get(key);

      if(window == null){
         RateLimitWindow newWindow = new RateLimitWindow(1, now);
          windows.put(key, newWindow);

          return new RateLimitResult(
                  true,
                  rule.getMaxRequests(),
                  rule.getMaxRequests() - 1,
                  now.plusSeconds(rule.getWindowSeconds())
          );
      }

      boolean expired = now.isAfter(
              window.getWindowStart().plusSeconds(rule.getWindowSeconds())
      );

      if(expired){
          RateLimitWindow newWindow = new RateLimitWindow(1, now);
          windows.put(key, newWindow);

          return new RateLimitResult(
                  true,
                  rule.getMaxRequests(),
                  rule.getMaxRequests() - 1,
                  now.plusSeconds(rule.getWindowSeconds())
          );
      }

      if (window.getRequestCount() >= rule.getMaxRequests()){
          return new RateLimitResult(
                  false,
                  rule.getMaxRequests(),
                  0,
                  window.getWindowStart().plusSeconds(rule.getWindowSeconds())
          );
      }

      window.setRequestCount(window.getRequestCount() + 1);

      return new RateLimitResult(
              true,
              rule.getMaxRequests(),
              rule.getMaxRequests() - window.getRequestCount(),
              window.getWindowStart().plusSeconds(rule.getWindowSeconds())
      );
  }

}
