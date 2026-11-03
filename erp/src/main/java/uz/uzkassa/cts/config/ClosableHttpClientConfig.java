package uz.uzkassa.cts.config;

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
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


@Configuration
public class ClosableHttpClientConfig {

    @Bean(name = "defaultPooledHttpClient")
    @Primary
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
                .setValidateAfterInactivity(TimeValue.ofMilliseconds(20500))
                .setConnectTimeout(Timeout.ofMilliseconds(30000))
                .setSocketTimeout(Timeout.ofMilliseconds(30000))
                .build());
        connectionManager.setMaxTotal(40);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("standard")
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000))
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
        connectionManager.setMaxTotal(40);

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
}
