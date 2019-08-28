package pl.piomin.logging.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class LoggingMessage {
    private String remoteIp;
    private String method;
    private String path;
    private String requestId;
    private String requestParams;
    private Map<String, String> requestHeaders;
    private String requestBody;
    private int responseStatus;
    private Map<String, String> responseHeaders;
    private String responseBody;
    private long duration;
    private Exception exception;

    @JsonProperty("remote_ip")
    public String getRemoteIp() {
        return remoteIp;
    }

    public LoggingMessage withRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        return this;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    public LoggingMessage withMethod(String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    public LoggingMessage withPath(String path) {
        this.path = path;
        return this;
    }

    @JsonProperty("request_id")
    public String getRequestId() {
        return requestId;
    }

    public LoggingMessage withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    @JsonProperty("request_params")
    public String getRequestParams() {
        return requestParams;
    }

    public LoggingMessage withRequestParams(String requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    @JsonProperty("request_headers")
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    @JsonProperty("request_body")
    public String getRequestBody() {
        return requestBody;
    }

    public LoggingMessage withRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    @JsonProperty("response_status")
    public int getResponseStatus() {
        return responseStatus;
    }

    public LoggingMessage withResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    @JsonProperty("response_headers")
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @JsonProperty("response_body")
    public String getResponseBody() {
        return responseBody;
    }

    public LoggingMessage withResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    @JsonProperty("duration")
    public long getDuration() {
        return duration;
    }

    public LoggingMessage withDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @JsonProperty("exception")
    public String getExceptionStr() {
        if (exception == null) {
            return null;
        } else {
            return exception.getMessage();
        }
    }

    @JsonProperty("stack_trace")
    public String getStackTraceStr() {
        if (exception == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            exception.printStackTrace(pw);
            pw.flush();
            return baos.toString();
        }
    }

    @JsonIgnore
    public Exception getException() {
        return exception;
    }

    public LoggingMessage withException(Exception exception) {
        this.exception = exception;
        return this;
    }
}
