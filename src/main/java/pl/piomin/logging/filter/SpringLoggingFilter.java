package pl.piomin.logging.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.piomin.logging.util.UniqueIDGenerator;
import pl.piomin.logging.wrapper.SpringRequestWrapper;
import pl.piomin.logging.wrapper.SpringResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SpringLoggingFilter extends OncePerRequestFilter {
    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(LoggingMessage.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLoggingFilter.class);
    private UniqueIDGenerator generator;

    @Value("${spring.logging.ignorePatterns:null}")
    String ignorePatterns;
    @Value("${spring.logging.includeHeaders:false}")
    boolean logHeaders;

    public SpringLoggingFilter(UniqueIDGenerator generator) {
        this.generator = generator;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (ignorePatterns != null && request.getRequestURI().matches(ignorePatterns)) {
            chain.doFilter(request, response);
        } else {
            generator.generateAndSetMDC(request);
            final long startTime = System.currentTimeMillis();
            final SpringRequestWrapper wrappedRequest = new SpringRequestWrapper(request);
            final SpringResponseWrapper wrappedResponse = new SpringResponseWrapper(response);
            wrappedResponse.setHeader("X-Request-ID", MDC.get("X-Request-ID"));
            wrappedResponse.setHeader("X-Correlation-ID", MDC.get("X-Correlation-ID"));
            Exception error = null;
            try {
                chain.doFilter(wrappedRequest, wrappedResponse);
            } catch (Exception e) {
                error = e;
                throw e;
            } finally {
                log(startTime, wrappedRequest, wrappedResponse, error);
            }
        }
    }

    private String prepareLoggingMessage(long startTime, SpringRequestWrapper request, SpringResponseWrapper response,
                                         Exception exception) throws IOException {
        LoggingMessage message = new LoggingMessage()
                .withRemoteIp(request.getRemoteAddr())
                .withMethod(request.getMethod())
                .withPath(request.getRequestURI())
                .withRequestId(MDC.get("X-Request-ID"))
                .withRequestParams(request.getQueryString())
                .withRequestBody(IOUtils.toString(request.getInputStream(), request.getCharacterEncoding()))
                .withResponseStatus(response.getStatus())
                .withResponseBody(IOUtils.toString(response.getContentAsByteArray(), response.getCharacterEncoding()))
                .withDuration(System.currentTimeMillis() - startTime)
                .withException(exception);
        if (logHeaders) {
            message.setRequestHeaders(request.getAllHeaders());
            message.setResponseHeaders(response.getAllHeaders());
        }
        return MESSAGE_WRITER.writeValueAsString(message);
    }

    private void log(long startTime, SpringRequestWrapper requestWrapper, SpringResponseWrapper wrappedResponse,
                     Exception exception) throws IOException {
        LOGGER.info(prepareLoggingMessage(startTime, requestWrapper, wrappedResponse, exception));
    }

}
