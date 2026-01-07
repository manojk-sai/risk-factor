package com.manoj.risk.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@SpringBootTest
class CorrelationFilterTest {
    private CorrelationIdFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    void shouldAddCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        String correlationId = response.getHeader("X-Correlation-ID");
        assertThat(correlationId).isNotBlank();

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // Ensure MDC is cleared after the filter
    }

    @Test
    void shouldUseExistingCorrelationIdFromHeader() throws ServletException, IOException {
        String existingCorrelationId = "test-correlation-id";
        request.addHeader("X-Correlation-ID", existingCorrelationId);

        filter.doFilterInternal(request, response, filterChain);

        String correlationId = response.getHeader("X-Correlation-ID");
        assertThat(correlationId).isEqualTo(existingCorrelationId);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // Ensure MDC is cleared after the filter
    }
}