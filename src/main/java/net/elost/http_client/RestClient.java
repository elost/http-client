package net.elost.http_client;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class RestClient {
  private final String endpointUrl;
  private HttpClient httpClient;

  /**
   * <b>IMPORTANT</b>
   * This constructor does not set any timeouts on connection.
   * It's strongly recommended to set global timeouts if you use this constructor.
   * For HotSpot JVM global timeouts can be set with
   * <i>sun.net.client.defaultConnectTimeout</i> and <i>sun.net.client.defaultReadTimeout</i>.
   *
   * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html">Java 8 Oracle Networking Properties</a>.
   */
  private RestClient(String endpointUrl) {
    this.endpointUrl = endpointUrl;
    httpClient = new HttpClientImpl();
  }

  public RestClient(String endpointUrl, int connectTimeoutMillis, int readTimeoutMillis) {
    this.endpointUrl = endpointUrl;
    httpClient = new HttpClientImpl(connectTimeoutMillis, readTimeoutMillis);
  }

  public void get(String path, Object input) {
    sendRequest(HttpMethod.GET, path, input);
  }

  public <T> T get(String path, Object input, Class<T> resultClass) {
    return sendRequest(HttpMethod.GET, path, input, resultClass);
  }

  public <T> T get(String path, Object input, Type typeOfResult) {
    return sendRequest(HttpMethod.GET, path, input, typeOfResult);
  }

  public void post(String path, Object input) {
    sendRequest(HttpMethod.POST, path, input);
  }

  public <T> T post(String path, Object input, Class<T> resultClass) {
    return sendRequest(HttpMethod.POST, path, input, resultClass);
  }

  public <T> T post(String path, Object input, Type typeOfResult) {
    return sendRequest(HttpMethod.POST, path, input, typeOfResult);
  }

  public void put(String path, Object input) {
    sendRequest(HttpMethod.PUT, path, input);
  }

  public <T> T put(String path, Object input, Class<T> resultClass) {
    return sendRequest(HttpMethod.PUT, path, input, resultClass);
  }

  public <T> T put(String path, Object input, Type typeOfResult) {
    return sendRequest(HttpMethod.PUT, path, input, typeOfResult);
  }

  public void delete(String path, Object input) {
    sendRequest(HttpMethod.DELETE, path, input);
  }

  public <T> T delete(String path, Object input, Class<T> resultClass) {
    return sendRequest(HttpMethod.DELETE, path, input, resultClass);
  }

  public <T> T delete(String path, Object input, Type typeOfResult) {
    return sendRequest(HttpMethod.DELETE, path, input, typeOfResult);
  }

  private void sendRequest(HttpMethod method, String path, Object input) {
    String inputJson = new Gson().toJson(input);
    HttpResponse response = httpClient.sendRequest(method, path, inputJson, "application/json");
    checkResponseCode(response);
  }

  private <T> T sendRequest(HttpMethod method, String path, Object input, Class<T> resultClass) {
    String inputJson = new Gson().toJson(input);
    HttpResponse response = httpClient.sendRequest(method, path, inputJson, "application/json");
    checkResponseCode(response);
    return deserializeResult(response, resultClass);
  }

  private <T> T sendRequest(HttpMethod method, String path, Object input, Type typeOfResult) {
    String inputJson = new Gson().toJson(input);
    HttpResponse response = httpClient.sendRequest(method, path, inputJson, "application/json");
    checkResponseCode(response);
    return deserializeResult(response, typeOfResult);
  }

  private String getUrl(String path) {
    String url = endpointUrl;
    if (!url.endsWith("/")) {
      url += '/';
    }

    if (path.startsWith("/") && path.length() > 1) {
      url += path.substring(1);
    }
    else {
      url += path;
    }
    return url;
  }

  private void checkResponseCode(HttpResponse response) {
    if (response.getCode() < 200 || response.getCode() >= 300) {
      throw new HttpCallException(String.format(
          "Failed to call api endpoint %s with status code %s, input: %s",
          response.getUrl(), response.getCode(), response.getRequestBody())
      );
    }
  }

  private <T> T deserializeResult(HttpResponse response, Class<T> resultClass) {
    try {
      return gson().fromJson(response.getResponseBody(), resultClass);
    }
    catch (JsonSyntaxException e) {
      throw new HttpCallException(String.format(
          "Malformed response for api call to [%s] [%s] : [%s]",
          response.getHttpMethod().name(), response.getUrl(), response.getResponseBody()
      ), e);
    }
  }

  private <T> T deserializeResult(HttpResponse response, Type typeOfResult) {
    try {
      return gson().fromJson(response.getResponseBody(), typeOfResult);
    }
    catch (JsonSyntaxException e) {
      throw new HttpCallException(String.format(
          "Malformed response for api call to [%s] [%s] : [%s]",
          response.getHttpMethod().name(), response.getUrl(), response.getResponseBody()
      ), e);
    }
  }

  private Gson gson() {
    GsonBuilder builder = new GsonBuilder();
    builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return builder.create();
  }

}
