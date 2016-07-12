package net.elost.http_client;

/**
 * @author Timur Urmancheev
 */
public class HttpCallException extends RuntimeException {
  private static final long serialVersionUID = 4901565145984329597L;

  public HttpCallException(String message) {
    super(message);
  }

  public HttpCallException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpCallException(Exception e) {
    super(e);
  }
}
