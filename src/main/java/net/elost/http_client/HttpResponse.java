package net.elost.http_client;

public class HttpResponse {
  private final HttpMethod httpMethod;
  private final String url;
  private final String requestBody;

  private final int code;
  private final String responseBody;

  public HttpResponse(HttpMethod httpMethod, String url, String requestBody, int code, String responseBody) {
    this.httpMethod = httpMethod;
    this.url = url;
    this.requestBody = requestBody;
    this.code = code;
    this.responseBody = responseBody;
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

  @Override
  public String toString() {
    String sizeUnit = (responseBinaryBody != null && responseBinaryBody.length == 1) ? "byte" : "bytes";
    int binaryBodyLength = responseBinaryBody == null ? 0 : responseBinaryBody.length;
    return "request method: " + httpMethod.name() + " \n"
        + "request url: " + url + " \n"
        + "request body: " + requestBody + " \n"
        + "response code: " + code + " \n"
        + "response body: " + responseBody;
  }
}
