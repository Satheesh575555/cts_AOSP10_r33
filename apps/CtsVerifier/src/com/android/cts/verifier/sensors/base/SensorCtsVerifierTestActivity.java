/*

 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.cts.verifier.sensors.base;

import com.android.cts.verifier.sensors.reporting.SensorTestDetails;
import com.android.cts.verifier.sensors.reporting.SensorTestDetails.ResultCode;

import android.hardware.cts.helpers.reporting.ISensorTestNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An Activity that provides a test execution engine for Sensor CtsVerifier tests. The tests are
 * able to interact with an operator.
 *
 * Sub-classes reuse its own class definition to 'load' tests at runtime through reflection.
 */
public abstract class SensorCtsVerifierTestActivity extends BaseSensorTestActivity {
    private volatile int mTestPassedCounter;
    private volatile int mTestSkippedCounter;
    private volatile int mTestFailedCounter;
    private volatile ISensorTestNode mCurrentTestNode;
    private volatile boolean mEnableRetry = false;

    /**
     * {@inheritDoc}
     */
    protected SensorCtsVerifierTestActivity(
            Class<? extends SensorCtsVerifierTestActivity> testClass) {
        super(testClass);
    }

    /**
     * {@inheritDoc}
     * Constructor to be used by subclasses.
     *
     * @param testClass   The class that contains the tests. It is dependant on test executor
     *                    implemented by subclasses.
     * @param enableRetry Subclass can enable retry mechanism for subtests.
     */
    protected SensorCtsVerifierTestActivity(
        Class<? extends SensorCtsVerifierTestActivity> testClass, boolean enableRetry) {
        super(testClass);
        mEnableRetry = enableRetry;
    }

    /**
     * Executes Semi-automated Sensor tests.
     * Execution is driven by this class, and allows discovery of tests using reflection.
     */
    @Override
    protected SensorTestDetails executeTests() throws InterruptedException {
        // TODO: use reporting to log individual test results
        Iterator<Method> testMethodIt = findTestMethods().iterator();
        while (testMethodIt.hasNext()) {
            Method testMethod = testMethodIt.next();
            boolean isLastSubtest = !testMethodIt.hasNext();
            getTestLogger().logTestStart(testMethod.getName());
            SensorTestDetails testDetails = executeTest(testMethod);
            getTestLogger().logTestDetails(testDetails);

            // If tests enable retry and get failed result, trigger the retry process.
            while (mEnableRetry && testDetails.getResultCode().equals(ResultCode.FAIL)) {
                if (isLastSubtest) {
                    waitForUserToFinish();
                } else {
                    waitForUserToRetry();
                }
                if (!getShouldRetry()) {
                    break;
                }
                mTestFailedCounter--;
                testDetails = executeTest(testMethod);
                getTestLogger().logTestDetails(testDetails);
            }
        }
        return new SensorTestDetails(
                getApplicationContext(),
                getTestClassName(),
                mTestPassedCounter,
                mTestSkippedCounter,
                mTestFailedCounter);
    }

    protected ISensorTestNode getCurrentTestNode() {
        return mCurrentTestNode;
    }

    private List<Method> findTestMethods() {
        ArrayList<Method> testMethods = new ArrayList<>();
        for (Method method : mTestClass.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 0
                    && method.getName().startsWith("test")
                    && method.getReturnType().equals(String.class)) {
                testMethods.add(method);
            }
        }
        return testMethods;
    }

    private SensorTestDetails executeTest(Method testMethod) throws InterruptedException {
        String testMethodName = testMethod.getName();
        String testName = String.format("%s#%s", getTestClassName(), testMethodName);
        mCurrentTestNode = new TestNode(testMethod);

        SensorTestDetails testDetails;
        try {
            String testSummary = (String) testMethod.invoke(this);
            testDetails =
                    new SensorTestDetails(testName, SensorTestDetails.ResultCode.PASS, testSummary);
        } catch (InvocationTargetException e) {
            // get the inner exception, because we use reflection APIs to execute the test
            testDetails = new SensorTestDetails(testName, "TestExecution", e.getCause());
        } catch (Throwable e) {
            testDetails = new SensorTestDetails(testName, "TestInfrastructure", e);
        }

        SensorTestDetails.ResultCode resultCode = testDetails.getResultCode();
        switch(resultCode) {
            case PASS:
                ++mTestPassedCounter;
                break;
            case SKIPPED:
                ++mTestSkippedCounter;
                break;
            case INTERRUPTED:
                throw new InterruptedException();
            case FAIL:
                ++mTestFailedCounter;
                break;
            default:
                throw new IllegalStateException("Unknown ResultCode: " + resultCode);
        }

        return testDetails;
    }

    private class TestNode implements ISensorTestNode {
        private final Method mTestMethod;

        public TestNode(Method testMethod) {
            mTestMethod = testMethod;
        }

        @Override
        public String getName() {
            return mTestClass.getSimpleName() + "_" + mTestMethod.getName();
        }
    }

    /**
     * Show the instruction for the first time execution and wait for user to begin the test.
     *
     * @param descriptionResId The description for the first time execution.
     */
    protected void setFirstExecutionInstruction(int ... descriptionResId) throws Throwable {
        if (!getShouldRetry()) {
            SensorTestLogger logger = getTestLogger();
            for (int id : descriptionResId) {
                logger.logInstructions(id);
            }
            waitForUserToBegin();
        }
    }
}
