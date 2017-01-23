package net.elost.http_client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {
  /**
   * Reads all bytes from an input stream into a byte array. Does not close the stream.
   * The method is adopted from
   * <a href='https://github.com/google/guava/blob/master/guava/src/com/google/common/io/ByteStreams.java'>
   *   com.google.common.io.ByteStreams</a>
   *
   * @param in the input stream to read from
   * @return a byte array containing all the bytes from the stream
   * @throws IOException if an I/O error occurs
   */
  public static byte[] toByteArray(InputStream in) throws IOException {
    // Presize the ByteArrayOutputStream since we know how large it will need
    // to be, unless that value is less than the default ByteArrayOutputStream
    // size (32).
    ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
    copy(in, out);
    return out.toByteArray();
  }

  /**
   * Copies all bytes from the input stream to the output stream. Does not close or flush either
   * stream.
   * The method is adopted from
   * <a href='https://github.com/google/guava/blob/master/guava/src/com/google/common/io/ByteStreams.java'>
   *   com.google.common.io.ByteStreams</a>
   *
   * @param from the input stream to read from
   * @param to   the output stream to write to
   * @return the number of bytes copied
   * @throws IOException if an I/O error occurs
   */
  private static long copy(InputStream from, OutputStream to) throws IOException {
    validateArguments(from, to);
    byte[] buf = new byte[8192];
    long total = 0;
    while (true) {
      int r = from.read(buf);
      if (r == -1) {
        break;
      }
      to.write(buf, 0, r);
      total += r;
    }
    return total;
  }

  private static void validateArguments(InputStream from, OutputStream to) {
    String fromNullErrorMessage = "";
    String toNullErrorMessage = "";

    if (from == null) {
      fromNullErrorMessage = "\"from\" argument is null";
    }
    if (to == null) {
      toNullErrorMessage = "\"to\" argument is null";
    }

    String concatenator = (!fromNullErrorMessage.isEmpty() && !toNullErrorMessage.isEmpty()) ? "and" : "";
    if (!fromNullErrorMessage.isEmpty() || !toNullErrorMessage.isEmpty()) {
      throw new IllegalArgumentException(fromNullErrorMessage + concatenator + toNullErrorMessage);
    }
  }
}