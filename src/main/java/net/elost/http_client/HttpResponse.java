package net.elost.http_client;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponse {
  private final HttpMethod httpMethod;
  private final String url;
  private final String requestBody;

  private final int code;
  private final String responseBody;
  private final byte[] responseBinaryBody;
  private final Map<String, List<String>> headers;

  private HttpResponse(HttpMethod httpMethod, HttpURLConnection connection, String requestBody, int code,
                       String responseBody, byte[] responseBinaryBody) {
    this.httpMethod = httpMethod;
    this.url = connection.getURL().toString();
    this.requestBody = requestBody;

    this.code = code;
    this.headers = connection.getHeaderFields();

    this.responseBody = responseBody;
    this.responseBinaryBody = responseBinaryBody;
  }

  public HttpResponse(HttpMethod httpMethod, HttpURLConnection connection, String requestBody, int code, byte[] responseBinaryBody) {
    this(httpMethod, connection, requestBody, code, null, responseBinaryBody);
  }

  public HttpResponse(HttpMethod httpMethod, HttpURLConnection connection, String requestBody, int code, String responseBody) {
    this(httpMethod, connection, requestBody, code, responseBody, null);
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

  public Map<String, List<String>> getHeaders() {
    return headers;
  }
}
