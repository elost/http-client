package net.elost.http_client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
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
    HttpURLConnection connection = connect(method, url, contentType, headers);

    try {
      return trySendRequest(method, connection, input);
    }
    finally {
      connection.disconnect();
    }
  }

  private HttpResponse trySendRequest(HttpMethod method, HttpURLConnection connection, String input) {
    sendRequest(connection, input);

    int status = getResponseCode(connection);
    if (isOctetStream(connection)) {
      byte[] result = tryReadBinaryResult(connection);
      return new HttpResponse(method, connection.getURL().toString(), input, status, result);
    }
    else {
      String result = tryReadResultString(connection);
      return new HttpResponse(method, connection.getURL().toString(), input, status, result);
    }
  }

  private HttpURLConnection connect(HttpMethod method, String url, String contentType, Map<String, String> headers) {
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

  private void sendRequest(HttpURLConnection connection, String inputJson) {
    try {
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
      writer.write(inputJson);
      writer.flush();
      writer.close();
    }
    catch (UnknownHostException connectionException) {
      throw new HttpCallException(String.format(
          "Failed to connect to url: %s", connection.getURL()
      ), connectionException);
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

  private String tryReadResultString(HttpURLConnection connection) {
    try {
      return readResultString(connection);
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

  private String readResultString(HttpURLConnection connection) throws IOException {
    InputStream stream = connection.getInputStream();
    InputStreamReader isReader = new InputStreamReader(stream);
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
    int responseCode = getResponseCode(connection);
    String responseBody;
    if (isOctetStream(connection)) {
      responseBody = "Binary Content";
    }
    else {
      responseBody = tryReadResultString(connection);
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
