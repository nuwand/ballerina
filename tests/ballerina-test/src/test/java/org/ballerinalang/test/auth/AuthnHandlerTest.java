/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/

package org.ballerinalang.test.auth;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AuthnHandlerTest {
    private static final String BALLERINA_CONF = "ballerina.conf";
    private CompileResult compileResult;
    private String resourceRoot;
    private Path ballerinaConfCopyPath;

    @BeforeClass
    public void setup() throws Exception {
        resourceRoot = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        Path sourceRoot = Paths.get(resourceRoot, "test-src", "auth");
        Path ballerinaConfPath = Paths
                .get(resourceRoot, "datafiles", "config", "auth", "basicauth", "userstore", BALLERINA_CONF);
        ballerinaConfCopyPath = sourceRoot.resolve(BALLERINA_CONF);

        // Copy the ballerina.conf to the source root before starting the tests
        Files.copy(ballerinaConfPath, ballerinaConfCopyPath, new CopyOption[] { REPLACE_EXISTING });
        compileResult = BCompileUtil.compile(sourceRoot.resolve("authn-handler-test.bal").toString());

        // load configs
        ConfigRegistry registry = ConfigRegistry.getInstance();
        registry.initRegistry(getRuntimeProperties(), ballerinaConfCopyPath);
        registry.loadConfigurations();
    }

    private Map<String, String> getRuntimeProperties() {
        Map<String, String> runtimeConfigs = new HashMap<>();
        runtimeConfigs.put(BALLERINA_CONF,
                Paths.get(resourceRoot, "datafiles", "config", "auth", "basicauth", "userstore", BALLERINA_CONF)
                        .toString());
        return runtimeConfigs;
    }

    @Test(description = "Test case for basic auth interceptor canHandle method, without the basic auth header")
    public void testCanHandleHttpBasicAuthWithoutHeader() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testCanHandleHttpBasicAuthWithoutHeader");
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for basic auth interceptor canHandle method")
    public void testCanHandleHttpBasicAuth() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testCanHandleHttpBasicAuth");
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for basic auth interceptor authentication failure")
    public void testHandleHttpBasicAuthFailure() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testHandleHttpBasicAuthFailure");
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertFalse(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for basic auth interceptor authentication success")
    public void testHandleHttpBasicAuth() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testHandleHttpBasicAuth");
        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test case for extracting invalid basic auth header value")
    public void testExtractInvalidBasicAuthHeaderValue() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testExtractInvalidBasicAuthHeaderValue");
        Assert.assertTrue(returns != null);
        // basic auth header should be null
        Assert.assertTrue(returns[0].stringValue() == null);
        // an error should be returned
        Assert.assertTrue(returns[1] != null);
    }

    @Test(description = "Test case for extracting basic auth header value")
    public void testExtractBasicAuthHeaderValue() {
        BValue[] returns = BRunUtil.invoke(compileResult, "testExtractBasicAuthHeaderValue");
        Assert.assertTrue(returns != null);
        // basic auth header should not be null
        Assert.assertTrue(returns[0].stringValue() != null);
        // no error should be returned
        Assert.assertTrue(returns[1] == null);
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.deleteIfExists(ballerinaConfCopyPath);
    }
}
