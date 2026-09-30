package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.client4.{DefaultSyncBackend, SyncBackend}

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, X509TrustManager, HttpsURLConnection}

/**
 * TODO
 */
trait BackendFactory {

  /**
   * TODO
   * @return
   */
  def buildBackend(): SyncBackend
}

/**
 * TODO
 */
object DefaultSyncBackendFactory
  extends BackendFactory {

  override def buildBackend(): SyncBackend = DefaultSyncBackend()
}

/**
 * TODO
 */
object DisabledCertValidationBackendFactory
  extends BackendFactory {

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