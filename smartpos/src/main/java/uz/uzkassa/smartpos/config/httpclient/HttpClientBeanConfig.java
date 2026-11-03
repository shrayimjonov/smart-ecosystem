package uz.uzkassa.smartpos.config.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Spring configuration class providing any {@link org.apache.hc.client5.http.classic.HttpClient} pools
 * used within the application.
 *
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HttpClientBeanConfig {

    /**
     * Default HttpClient configurations. Uses the default
     * values for the HttpClientConfig (32 connects per host/64 total outgoing)
     * with overrides shown.
     */
    @Bean(name = "defaultPooledHttpClient")
    public CloseableHttpClient getDefaultHttpClient() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
        try {
            final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            final SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            final SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ignored) {
        }
        final PoolingHttpClientConnectionManager connectionManager = socketFactoryRegistry != null
                ? new PoolingHttpClientConnectionManager(socketFactoryRegistry)
                : new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setValidateAfterInactivity(TimeValue.ofSeconds(20))
                .setConnectTimeout(Timeout.ofSeconds(20))
                .setSocketTimeout(Timeout.ofSeconds(20))
                .build());
        connectionManager.setMaxTotal(40);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("standard")
                .setConnectionRequestTimeout(Timeout.ofSeconds(20))
                .setConnectionKeepAlive(Timeout.ofSeconds(20))
                .build();

        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig);

        return builder.build();
    }

    @Bean(name = "pooledIntegrationHttpClient")
    public CloseableHttpClient pooledIntegrationHttpClient() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
        try {
            final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            final SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            final SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException ignored) {
        }
        final PoolingHttpClientConnectionManager connectionManager = socketFactoryRegistry != null
                ? new PoolingHttpClientConnectionManager(socketFactoryRegistry)
                : new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setValidateAfterInactivity(TimeValue.ofMilliseconds(20500))
                .setConnectTimeout(Timeout.ofMilliseconds(30000))
                .setSocketTimeout(Timeout.ofMilliseconds(30000))
                .build());
        connectionManager.setMaxTotal(500);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("standard")
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000))
                .build();

        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .addResponseInterceptorLast((response, entity, context) -> response.removeHeaders(HttpHeaders.TRANSFER_ENCODING.toLowerCase()))
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig);

        return builder.build();
    }

    @Bean
    public DefaultUriBuilderFactory uriBuilderFactory() {
        return new DefaultUriBuilderFactory();
    }
}
