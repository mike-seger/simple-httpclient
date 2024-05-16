package com.example.demo;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;

@Configuration
public class RestClientConfig {

    @Value("${client.ssl.trust-store:null}")
    private Resource trustStore;

    @Value("${client.ssl.trust-store-password:}")
    private String trustStorePassword;

    @Value("${client.ssl.key-store:null}")
    private Resource keyStore;

    @Value("${client.ssl.key-store-password:}")
    private String keyStorePassword;

    @Bean
    public RestTemplate restTemplate() throws Exception {
        SSLContext sslContext = createSslContext();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new DefaultHostnameVerifier());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(200); // Set max total connections
        connectionManager.setDefaultMaxPerRoute(20); // Set max connections per route

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }

    private SSLContext createSslContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (var keyStoreInputStream = this.keyStore.getInputStream()) {
            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
        }

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (var trustStoreInputStream = this.trustStore.getInputStream()) {
            trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

        return sslContext;
    }
}
