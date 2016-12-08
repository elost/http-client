package net.elost.http_client;

import java.io.InputStream;
import java.util.Map;

public interface HttpClient {

  HttpResponse sendRequest(HttpMethod method, String path, String input, String contentType);

  HttpResponse sendRequest(HttpMethod method, String url, String input, String contentType, Map<String, String> headers);

  InputStream sendRequestGetStream(HttpMethod method, String url, String input, Map<String, String> headers);
}
