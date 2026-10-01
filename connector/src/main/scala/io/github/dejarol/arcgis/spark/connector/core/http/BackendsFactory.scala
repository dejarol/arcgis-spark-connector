package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.client4.{DefaultSyncBackend, SyncBackend}

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, X509TrustManager, HttpsURLConnection}

/**
 * Creates an STTP synchronous backend used to send HTTP requests.
 *
 * @since 0.2.0
 */
trait BackendFactory {

  /**
   * Builds a synchronous STTP backend.
   *
   * @return a backend ready to send requests
   * @since 0.2.0
   */
  def buildBackend(): SyncBackend
}

/**
 * Factory that creates STTP's default synchronous backend.
 *
 * @since 0.2.0
 */
object DefaultSyncBackendFactory
  extends BackendFactory {

  /**
   * Builds STTP's default synchronous backend.
   *
   * @return a default [[sttp.client4.DefaultSyncBackend]]
   * @since 0.2.0
   */
  override def buildBackend(): SyncBackend = DefaultSyncBackend()
}

/**
 * Factory that creates a backend which does not validate TLS certificates.
 *
 * @since 0.2.0
 */
object DisabledCertValidationBackendFactory
  extends BackendFactory {

  /**
   * Builds a backend that trusts all TLS certificates.
   *
   * @return an HTTP backend with certificate validation disabled
   * @since 0.2.0
   */
  override def buildBackend(): SyncBackend = {

    // [1] Create a trust manager that trusts all certificates
    val trustAllCerts: X509TrustManager = new X509TrustManager() {
      override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
      override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()
      override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()
    }

    // [2] Link this manager to the SSLContext
    val ssl: SSLContext = SSLContext.getInstance("TLS")
    ssl.init(Array.empty, Array(trustAllCerts), new SecureRandom)

    // [3] Create a backend that uses such context
    HttpURLConnectionBackend(
      customizeConnection = {
        case connection: HttpsURLConnection =>
          connection.setSSLSocketFactory(ssl.getSocketFactory)
      }
    )
  }
}