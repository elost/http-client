package net.elost.http_client;

import java.util.List;
import java.util.Map;

public class HttpResponse {
  private HttpMethod httpMethod;
  private String url;
  private String requestBody;

  private int code;
  private String responseBody;
  private byte[] responseBinaryBody;

  private Map<String, List<String>> responseHeaders;

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public HttpResponse httpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public HttpResponse url(String url) {
    this.url = url;
    return this;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public HttpResponse requestBody(String requestBody) {
    this.requestBody = requestBody;
    return this;
  }

  public int getCode() {
    return code;
  }

  public HttpResponse code(int code) {
    this.code = code;
    return this;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public HttpResponse responseBody(String responseBody) {
    this.responseBody = responseBody;
    return this;
  }

  public byte[] getResponseBinaryBody() {
    return responseBinaryBody;
  }

  public HttpResponse responseBinaryBody(byte[] responseBinaryBody) {
    this.responseBinaryBody = responseBinaryBody;
    return this;
  }

  public Map<String, List<String>> getResponseHeaders() {
    return responseHeaders;
  }

  public HttpResponse responseHeaders(Map<String, List<String>> headers) {
    this.responseHeaders = headers;
    return this;
  }

  @Override
  public String toString() {
    String sizeUnit = (responseBinaryBody != null && responseBinaryBody.length == 1) ? "byte" : "bytes";
    int binaryBodyLength = responseBinaryBody == null ? 0 : responseBinaryBody.length;
    return "request method: " + httpMethod.name() + " \n"
        + "request url: " + url + " \n"
        + "request body: " + requestBody + " \n"
        + "response code: " + code + " \n"
        + "response body: " + responseBody + " \n"
        + "response binary body length: " + String.valueOf(binaryBodyLength)
        + " " + sizeUnit;
  }
}
