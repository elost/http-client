package net.elost.http_client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClientImpl implements HttpClient {
  private int connectTimeoutMillis;
  private int readTimeoutMillis;

  /**
   * <b>IMPORTANT</b>
   * This constructor does not set any timeouts on connection.
   * It's strongly recommended to set global timeouts if you use this constructor.
   * For HotSpot JVM global timeouts can be set with
   * <i>sun.net.client.defaultConnectTimeout</i> and <i>sun.net.client.defaultReadTimeout</i>.
   *
   * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html">Java 8 Oracle Networking Properties</a>.
   */
  public HttpClientImpl() {
  }

  public HttpClientImpl(int connectTimeoutMillis, int readTimeoutMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.readTimeoutMillis = readTimeoutMillis;
  }

  @Override
  public HttpResponse sendRequest(HttpMethod method, String url, String input, String contentType) {
    return this.sendRequest(method, url, input, contentType, new HashMap<>());
  }

  @Override
  public HttpResponse sendRequest(
      HttpMethod method,
      String url,
      String input,
      String contentType,
      Map<String, String> headers
  ) {
    HttpURLConnection connection = prepareConnection(method, url, contentType, headers);

    try {
      connect(connection);
      return trySendRequest(method, connection, input);
    }
    finally {
      connection.disconnect();
    }
  }

  private void connect(HttpURLConnection connection) {
    try {
      connection.connect();
    }
    catch (IOException e) {
      throw new HttpCallException(String.format(
          "Failed to connect to url: %s. Reason: %s",
          connection.getURL(), e.getMessage()
      ));
    }
  }

  private HttpResponse trySendRequest(HttpMethod method, HttpURLConnection connection, String input) {
    if (method != HttpMethod.GET) {
      sendRequestBody(connection, input);
    }

    int status = getResponseCode(connection);

    HttpResponse response = new HttpResponse()
        .httpMethod(method)
        .url(connection.getURL().toString())
        .requestBody(input)
        .code(status)
        .responseHeaders(connection.getHeaderFields());

    boolean successStatus = status < 400;

    if (successStatus) {
      if (isOctetStream(connection)) {
        byte[] result = tryReadBinaryResult(connection);
        response.responseBinaryBody(result);
      }
      else {
        String result = tryReadResultString(connection, true);
        response.responseBody(result);
      }
    }
    else {
      String result = tryReadResultString(connection, false);
      response.responseBody(result);
    }

    return response;
  }

  private HttpURLConnection prepareConnection(HttpMethod method, String url, String contentType, Map<String, String> headers) {
    try {
      URL endpoint = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
      connection.setRequestMethod(method.name());
      connection.setRequestProperty("Content-Type", contentType);
      headers.forEach(connection::setRequestProperty);
      connection.setDoOutput(true);
      connection.setConnectTimeout(connectTimeoutMillis);
      connection.setReadTimeout(readTimeoutMillis);
      return connection;
    }
    catch (IOException connectionException) {
      throw new HttpCallException(String.format("Failed to connect to url: %s", url), connectionException);
    }
  }

  private void sendRequestBody(HttpURLConnection connection, String inputJson) {
    try {
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
      writer.write(inputJson);
      writer.flush();
      writer.close();
    }
    catch (IOException ioe) {
      logSendRequestIOException(connection, ioe, inputJson);
    }
  }

  private int getResponseCode(HttpURLConnection connection) {
    try {
      return connection.getResponseCode();
    }
    catch (IOException e) {
      throw new HttpCallException("Can't get response code, assuming failure", e);
    }
  }

  private String tryReadResultString(HttpURLConnection connection, boolean success) {
    try {
      return readResultString(connection, success);
    }
    catch (IOException e) {
      throw new HttpCallException(String.format(
          "Can't read response from api call to %s",
          connection.getURL()
      ), e);
    }
  }

  private byte[] tryReadBinaryResult(HttpURLConnection connection) {
    try {
      return readBinaryResult(connection);
    }
    catch (IOException e) {
      throw new HttpCallException(String.format(
          "Can't read response from api call to %s",
          connection.getURL()
      ), e);
    }
  }

  private String readResultString(HttpURLConnection connection, boolean success) throws IOException {
    InputStream stream;
    if (success) {
      stream = connection.getInputStream();
    }
    else {
      stream = connection.getErrorStream();
    }
    InputStreamReader isReader = new InputStreamReader(stream, "UTF-8");
    BufferedReader reader = new BufferedReader(isReader);

    StringBuilder result = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      result.append(line).append("\n");
    }

    return result.toString();
  }

  private byte[] readBinaryResult(HttpURLConnection connection) throws IOException {
    BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
    return StreamUtil.toByteArray(stream);
  }

  private boolean isOctetStream(HttpURLConnection connection) {
    return connection.getHeaderField("Content-Type").toLowerCase().contains("application/octet-stream");
  }

  private void logSendRequestIOException(HttpURLConnection connection, IOException ioe, String inputJson) {
    int responseCode = -1;
    try {
      responseCode = getResponseCode(connection);
    }
    catch (Exception e) {
    }

    String responseBody;
    if (isOctetStream(connection)) {
      responseBody = "Binary Content";
    }
    else {
      responseBody = tryReadResultString(connection, false);
    }
    throw new HttpCallException(String.format(
        "Failed to call api endpoint [%s], input: [%s], status: [%s], response: %s",
        connection.getURL(),
        inputJson,
        responseCode,
        responseBody
    ), ioe);
  }
}
