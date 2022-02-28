package org.mockserver.netty.integration.mock.tls.inbound.bouncycastle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockserver.cli.Main;
import org.mockserver.client.MockServerClient;
import org.mockserver.netty.integration.mock.tls.inbound.AbstractClientAuthenticationMockingIntegrationTest;
import org.mockserver.socket.PortFactory;
import org.mockserver.uuid.UUIDService;

import java.io.File;

import static org.mockserver.configuration.ConfigurationProperties.*;
import static org.mockserver.configuration.ConfigurationProperties.useBouncyCastleForKeyAndCertificateGeneration;
import static org.mockserver.stop.Stop.stopQuietly;

/**
 * @author jamesdbloom
 */
public class ClientAuthenticationDynamicCAMockingIntegrationTest extends AbstractClientAuthenticationMockingIntegrationTest {

    private static final int severHttpPort = PortFactory.findFreePort();
    private static boolean originalUseBouncyCastleForKeyAndCertificateGeneration;
    private static boolean originalTLSMutualAuthenticationRequired;
    private static boolean originalDynamicallyCreateCertificateAuthorityCertificate;
    private static String originalDirectoryToSaveDynamicSSLCertificate;

    @BeforeClass
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void startServer() throws Exception {
        // save original value
        originalUseBouncyCastleForKeyAndCertificateGeneration = useBouncyCastleForKeyAndCertificateGeneration();
        originalTLSMutualAuthenticationRequired = tlsMutualAuthenticationRequired();
        originalDynamicallyCreateCertificateAuthorityCertificate = dynamicallyCreateCertificateAuthorityCertificate();
        originalDirectoryToSaveDynamicSSLCertificate = directoryToSaveDynamicSSLCertificate();

        // temporary directory
        File temporaryDirectory = new File(File.createTempFile("random", "temp").getParent(), UUIDService.getUUID());
        temporaryDirectory.mkdirs();

        useBouncyCastleForKeyAndCertificateGeneration(true);
        tlsMutualAuthenticationRequired(true);
        dynamicallyCreateCertificateAuthorityCertificate(true);
        directoryToSaveDynamicSSLCertificate(temporaryDirectory.getAbsolutePath());

        Main.main("-serverPort", "" + severHttpPort);

        mockServerClient = new MockServerClient("localhost", severHttpPort).withSecure(true);
    }

    @AfterClass
    public static void stopServer() {
        stopQuietly(mockServerClient);

        // set back to original value
        useBouncyCastleForKeyAndCertificateGeneration(originalUseBouncyCastleForKeyAndCertificateGeneration);
        tlsMutualAuthenticationRequired(originalTLSMutualAuthenticationRequired);
        dynamicallyCreateCertificateAuthorityCertificate(originalDynamicallyCreateCertificateAuthorityCertificate);
        directoryToSaveDynamicSSLCertificate(originalDirectoryToSaveDynamicSSLCertificate);
    }

    @Override
    public int getServerPort() {
        return severHttpPort;
    }

}
