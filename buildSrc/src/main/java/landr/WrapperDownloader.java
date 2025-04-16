/*
 * Inspired from Lucene project.
 */
package landr;

import static java.nio.file.StandardOpenOption.DELETE_ON_CLOSE;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;


public class WrapperDownloader {

  private static final String URL_PATTERN = "https://raw.githubusercontent.com/gradle/gradle/v%s/gradle/wrapper/gradle-wrapper.jar";

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("Usage: java WrapperDownloader.java <destination>");
      System.exit(1);
    }

    Path destination = Path.of(args[0]);
    if (!checkDestination(destination)) {
      System.err.println("Cannot write to destination: " + destination);
      System.exit(1);
    }

    // Read version number
    String versionName = destination.getFileName().toString().replace(".jar", ".version");
    Path versionPath = destination.resolveSibling(versionName);
    String version = Files.readString(versionPath).trim();

    // Read validation checksum
    String checksumName = destination.getFileName().toString().replace(".jar", ".sha256");
    Path checksumPath = destination.resolveSibling(checksumName);
    String checksum = Files.readString(checksumPath).trim();

    // Download the wrapper to a temporary file, so we're sure to not leave a corrupted
    // file in place of the final destination
    Path tmpDir = Files.createTempDirectory("wrapper-downloader");
    Path tempDestination = tmpDir.resolve("gradle-wrapper.tmp");
    download(tempDestination, version);

    String actualChecksum = checksum(tempDestination);
    if (!actualChecksum.equals(checksum)) {
      System.err.println("Checksum validation failed: expected " + checksum + ", was " + actualChecksum);
      System.exit(1);
    }

    try (InputStream input = Files.newInputStream(tempDestination, DELETE_ON_CLOSE)) {
      Files.copy(input, destination);
    }
  }

  /**
   * Check we can download to the destination file.
   */
  private static boolean checkDestination(Path destination) {
    if (Files.exists(destination)) {
        return Files.isWritable(destination);
    } else {
      Path parent = destination.getParent();
      return Files.isDirectory(parent) && Files.isWritable(parent);
    }
  }

  private static void download(Path destination, String version) throws Exception {

    URL url = new URI(String.format(URL_PATTERN, version)).toURL();
    System.err.println("Downloading wrapper from " + url);

    try (InputStream input = url.openStream()) {
      Files.copy(input, destination);
    }
  }

  /**
   * Calculate the SHA-256 checksum of a file.
   */
  private static String checksum(Path path) throws Exception {

    MessageDigest digester = MessageDigest.getInstance("SHA-256");
    byte[] sha = digester.digest(Files.readAllBytes(path));

    StringBuilder result = new StringBuilder();
    for (byte b : sha) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        result.append('0');
      }
      result.append(hex);
    }
    return result.toString();
  }

}
