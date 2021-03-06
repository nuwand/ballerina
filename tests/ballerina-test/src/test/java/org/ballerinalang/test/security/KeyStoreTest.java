/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.security;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.nativeimpl.security.KeyStore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyStoreTest {

    private static final String KEY_STORE_CONFIG = "keyStore";
    private static final String KEY_STORE_LOCATION = "location";
    private static final String KEY_STORE_TYPE = "type";
    private static final String KEY_STORE_PASSWORD = "keyStorePassword";
    private static final String TRUST_STORE_CONFIG = "trustStore";
    private static final String TRUST_STORE_LOCATION = "location";
    private static final String TRUST_STORE_TYPE = "type";
    private static final String TRUST_STORE_PASSWORD = "trustStorePassword";

    @Test(description = "Test loading keystore")
    public void testGetKeyStore() throws Exception {
        Assert.assertNotNull(loadKeyStore());
    }

    @Test(description = "Test getting trusted public key from truststore")
    public void testGetTrustedPublicKey() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Assert.assertTrue(keyStore.getTrustedPublicKey("ballerina") instanceof PublicKey);
    }

    @Test(description = "Test getting trusted certificate from truststore")
    public void testGetTrustedCertificate() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Assert.assertTrue(keyStore.getTrustedCertificate("ballerina") instanceof Certificate);
    }

    @Test(description = "Test getting private from keystore")
    public void testGetPrivateKey() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Assert.assertTrue(keyStore.getPrivateKey("ballerina", "ballerina".toCharArray()) instanceof PrivateKey);
    }

    @Test(description = "Test getting public key from keystore")
    public void testGetPublicKey() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Assert.assertTrue(keyStore.getPublicKey("ballerina") instanceof PublicKey);
    }

    @Test(description = "Test getting certificate from keystore")
    public void testGetCertificate() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Assert.assertTrue(keyStore.getCertificate("ballerina") instanceof Certificate);
    }

    private KeyStore loadKeyStore() throws Exception {
        ConfigRegistry defaultConfigRegistry = ConfigRegistry.getInstance();
        KeyStore keyStore;

        ConfigRegistry configRegistry = mock(ConfigRegistry.class);
        when(configRegistry.getInstanceConfigValue(KEY_STORE_CONFIG, KEY_STORE_LOCATION))
                .thenReturn(getClass().getClassLoader().getResource(
                        "datafiles/security/keyStore/ballerinaKeystore.p12").getPath());
        when(configRegistry.getInstanceConfigValue(KEY_STORE_CONFIG, KEY_STORE_PASSWORD))
                .thenReturn("ballerina");
        when(configRegistry.getInstanceConfigValue(KEY_STORE_CONFIG, KEY_STORE_TYPE))
                .thenReturn("pkcs12");
        when(configRegistry.getInstanceConfigValue(TRUST_STORE_CONFIG, TRUST_STORE_LOCATION))
                .thenReturn(getClass().getClassLoader().getResource(
                        "datafiles/security/keyStore/ballerinaTruststore.p12").getPath());
        when(configRegistry.getInstanceConfigValue(TRUST_STORE_CONFIG, TRUST_STORE_PASSWORD))
                .thenReturn("ballerina");
        when(configRegistry.getInstanceConfigValue(TRUST_STORE_CONFIG, TRUST_STORE_TYPE))
                .thenReturn("pkcs12");

        Field field = ConfigRegistry.class.getDeclaredField("configRegistry");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(ConfigRegistry.class, configRegistry);
        keyStore = KeyStore.getKeyStore();

        field.set(ConfigRegistry.class, defaultConfigRegistry);
        modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
        modifiersField.setAccessible(false);
        field.setAccessible(false);

        return keyStore;
    }
}
