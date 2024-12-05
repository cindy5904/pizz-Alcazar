package org.example.server.config;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.Environment;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {

//    @Value("${PAYPAL_CLIENT_ID}")
//    private String paypalClientId;
//
//    @Value("${PAYPAL_CLIENT_SECRET}")
//    private String paypalClientSecret;
//
//    @Bean
//    public PaypalServerSdkClient paypalClient() {
//        return new PaypalServerSdkClient.Builder()
//                .environment(Environment.SANDBOX)
//                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
//                        paypalClientId,
//                        paypalClientSecret
//                ).build())
//                .build();
//    }
private final Dotenv dotenv = Dotenv.load();

    @Bean
    public PaypalServerSdkClient paypalClient() {
        System.out.println("Client ID PayPal : " + dotenv.get("PAYPAL_CLIENT_ID"));
        System.out.println("Client Secret PayPal : " + dotenv.get("PAYPAL_CLIENT_SECRET"));
        return new PaypalServerSdkClient.Builder()
                .environment(Environment.SANDBOX) // Environnement Sandbox
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
                        dotenv.get("PAYPAL_CLIENT_ID"), // Lit PAYPAL_CLIENT_ID depuis .env
                        dotenv.get("PAYPAL_CLIENT_SECRET") // Lit PAYPAL_CLIENT_SECRET depuis .env
                ).build())
                .build();
    }
}


