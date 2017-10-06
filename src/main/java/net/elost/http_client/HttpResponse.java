package net.elost.http_client;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponse {
  private HttpMethod httpMethod;
  private String url;
  private String requestBody;

  private int code;
  private String responseBody;
  private byte[] responseBinaryBody;

  private Map<String, List<String>> headers;

  public static class Builder {

    private HttpResponse response = new HttpResponse();

    public Builder(HttpMethod httpMethod, HttpURLConnection connection, String requestBody, int responseCode) {
      response.httpMethod = httpMethod;
      response.url = connection.getURL().toString();
      response.requestBody = requestBody;
      response.code = responseCode;
      response.headers = connection.getHeaderFields();
    }

    public Builder responseBody(String responseBody) {
      response.responseBody = responseBody;
      return this;
    }

    public Builder responseBinaryBody(byte[] responseBinaryBody) {
      response.responseBinaryBody = responseBinaryBody;
      return this;
    }

    public HttpResponse build() {
      return response;
    }
  }

  private HttpResponse() {
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

  public Map<String, List<String>> getHeaders() {
    return headers;
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
