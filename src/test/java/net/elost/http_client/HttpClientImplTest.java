package net.elost.http_client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Urmancheev
 */
public class HttpClientImplTest {

  @Rule
  public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

  @ClassRule
  public static WireMockClassRule server = new WireMockClassRule(
      wireMockConfig().dynamicPort()
  );

  private HttpClientImpl client = new HttpClientImpl(20, 1000);

  @Before
  public void init() {
    for (int code = 200; code < 600; code++) {
      server.stubFor(WireMock
          .get("/api/" + code)
          .willReturn(aResponse()
              .withHeader("Content-Type", "application/text")
              .withStatus(code)
              .withBody("get-" + code)
          )
      );
      server.stubFor(WireMock
          .post("/api/" + code)
          .willReturn(aResponse()
              .withHeader("Content-Type", "application/text")
              .withStatus(code)
              .withBody("post-" + code)
          )
      );
      server.stubFor(WireMock
          .put("/api/" + code)
          .willReturn(aResponse()
              .withHeader("Content-Type", "application/text")
              .withStatus(code)
              .withBody("put-" + code)
          )
      );
      server.stubFor(WireMock
          .delete("/api/" + code)
          .willReturn(aResponse()
              .withHeader("Content-Type", "application/text")
              .withStatus(code)
              .withBody("delete-" + code)
          )
      );
    }
  }

  @Test
  public void basicGetRequest() {
    String baseUrl = baseUrl();
    HttpResponse response = client.sendRequest(
        HttpMethod.GET, baseUrl + 200, "", ""
    );

    assertEquals(200, response.getCode());
    assertEquals("get-200", response.getResponseBody().trim());
  }

  @Test
  public void basicPostRequest() {
    String baseUrl = baseUrl();
    HttpResponse response = client.sendRequest(
        HttpMethod.POST, baseUrl + 200, "", ""
    );

    assertEquals(200, response.getCode());
    assertEquals("post-200", response.getResponseBody().trim());
  }

  @Test
  public void basicPutRequest() {
    String baseUrl = baseUrl();
    HttpResponse response = client.sendRequest(
        HttpMethod.PUT, baseUrl + 200, "", ""
    );

    assertEquals(200, response.getCode());
    assertEquals("put-200", response.getResponseBody().trim());
  }

  @Test
  public void basicDeleteRequest() {
    String baseUrl = baseUrl();
    HttpResponse response = client.sendRequest(
        HttpMethod.DELETE, baseUrl + 200, "", ""
    );

    assertEquals(200, response.getCode());
    assertEquals("delete-200", response.getResponseBody().trim());
  }

  @Test
  public void readsResponseOnAllHttpStatuses() {
    Set<Integer> httpStatusesThatDoesNotHaveResponseBody = new HashSet<>(Arrays.asList(
        204, 304
    ));
    String baseUrl = baseUrl();

    for (int code = 200; code < 600; code++) {
      HttpResponse response = client.sendRequest(
          HttpMethod.GET, baseUrl + code, null, "application/json"
      );

      assertEquals(code, response.getCode());
      if (!httpStatusesThatDoesNotHaveResponseBody.contains(code)) {
        assertEquals("get-" + code, response.getResponseBody().trim());
      }
    }
  }

  private String baseUrl() {
    return "http://localhost:" + server.port() + "/api/";
  }
}
