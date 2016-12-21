package net.elost.http_client;

import java.util.Arrays;

public class HttpResponse {
  private final HttpMethod httpMethod;
  private final String url;
  private final String requestBody;

  private final int code;
  private final String responseBody;
  private final byte[] responseBinaryBody;

  public HttpResponse(HttpMethod httpMethod, String url, String requestBody, int code, String responseBody) {
    this.httpMethod = httpMethod;
    this.url = url;
    this.requestBody = requestBody;
    this.code = code;
    this.responseBody = responseBody;
    this.responseBinaryBody = null;
  }

  public HttpResponse(HttpMethod httpMethod, String url, String requestBody, int code, byte[] responseBinaryBody) {
    this.httpMethod = httpMethod;
    this.url = url;
    this.requestBody = requestBody;
    this.code = code;
    this.responseBody = null;
    this.responseBinaryBody = responseBinaryBody;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public String getUrl() {
    return url;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public int getCode() {
    return code;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public byte[] getResponseBinaryBody() {
    return responseBinaryBody;
  }

  @Override
  public String toString() {
    return "request method: " + httpMethod.name() + " \n"
        + "request url: " + url + " \n"
        + "request body: " + requestBody + " \n"
        + "response code: " + code + " \n"
        + "response body: " + responseBody + " \n"
        + "response binary body: " + Arrays.toString(responseBinaryBody);
  }
}
