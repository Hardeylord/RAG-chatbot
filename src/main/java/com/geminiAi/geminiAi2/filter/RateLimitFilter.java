package com.geminiAi.geminiAi2.filter;

import com.geminiAi.geminiAi2.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bucketKey = getClientIpAddress(request);
        Bucket userBucket = rateLimitService.rateLimit(bucketKey);
        ConsumptionProbe consumptionProbe = userBucket.tryConsumeAndReturnRemaining(1);
        if (consumptionProbe.isConsumed()) {

            filterChain.doFilter(request, response);
            return;
        };

        var refillTime = consumptionProbe.getNanosToWaitForRefill()/1_000_000_000;

        response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
        response.setContentType("application/json");
        String jsonResponse = """
                "status": %s,
                "error": "Too Many Requests.",
                "message": "",
                "retryAfterSeconds": %s
                """.formatted(HttpStatus.SC_TOO_MANY_REQUESTS, refillTime);
        response.getWriter().write(jsonResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }
}
