package uz.uzkassa.smartpossupply.config.httpclient;

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

@Slf4j
@Configuration
public class HttpClientBeanConfig {

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
        connectionManager.setMaxTotal(100);

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
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error(e.getMessage(), e);
        }
        final PoolingHttpClientConnectionManager connectionManager = socketFactoryRegistry != null
                ? new PoolingHttpClientConnectionManager(socketFactoryRegistry)
                : new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                // Revalidate any connection idle > 5s before reuse, so a socket the peer
                // silently closed is detected and replaced instead of yielding "Connection reset".
                .setValidateAfterInactivity(TimeValue.ofSeconds(5))
                .setConnectTimeout(Timeout.ofMilliseconds(30000))
                .setSocketTimeout(Timeout.ofMilliseconds(30000))
                // Cap how long a pooled connection may live, bounding exposure to server-side idle drops.
                .setTimeToLive(TimeValue.ofSeconds(60))
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
                // Background eviction of expired / long-idle connections keeps the pool from
                // handing out stale keep-alive sockets that the peer has already reset.
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(10))
                .setDefaultRequestConfig(requestConfig);

        return builder.build();
    }

    @Bean
    public DefaultUriBuilderFactory uriBuilderFactory() {
        return new DefaultUriBuilderFactory();
    }
}
