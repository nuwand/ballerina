/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.siddhi.core.executor.condition;

import org.ballerinalang.siddhi.core.event.ComplexEvent;
import org.ballerinalang.siddhi.core.exception.OperationNotSupportedException;
import org.ballerinalang.siddhi.core.executor.ExpressionExecutor;
import org.ballerinalang.siddhi.query.api.definition.Attribute;

/**
 * Executor class for And condition. Condition evaluation logic is implemented within executor.
 */
public class AndConditionExpressionExecutor extends ConditionExpressionExecutor {

    protected ExpressionExecutor leftConditionExecutor;
    protected ExpressionExecutor rightConditionExecutor;

    public AndConditionExpressionExecutor(ExpressionExecutor leftConditionExecutor,
                                          ExpressionExecutor rightConditionExecutor) {
        if (leftConditionExecutor.getReturnType().equals(Attribute.Type.BOOL)
                && rightConditionExecutor.getReturnType().equals(Attribute.Type.BOOL)) {

            this.leftConditionExecutor = leftConditionExecutor;
            this.rightConditionExecutor = rightConditionExecutor;
        } else {
            if (!leftConditionExecutor.getReturnType().equals(Attribute.Type.BOOL)) {
                throw new OperationNotSupportedException("Return type of condition executor " +
                        leftConditionExecutor.toString() +
                        " should be of type BOOL. Actual Type: " +
                        leftConditionExecutor.getReturnType().toString());
            } else if (!rightConditionExecutor.getReturnType().equals(Attribute.Type.BOOL)) {
                throw new OperationNotSupportedException("Return type of condition executor " +
                        rightConditionExecutor.toString() + " should be of "
                        + "type BOOL. Actual Type: " +
                        rightConditionExecutor.getReturnType().toString());
            } else {
                throw new OperationNotSupportedException("Return type of condition executor " +
                        leftConditionExecutor.toString() +
                        " and condition executor" +
                        rightConditionExecutor.toString() +
                        "should be of type BOOL. Left executor: " +
                        leftConditionExecutor.getReturnType().toString() +
                        " Right executor: " +
                        rightConditionExecutor.getReturnType().toString());
            }
        }

    }

    public Boolean execute(ComplexEvent event) {
        Object leftResult = leftConditionExecutor.execute(event);
        if (leftResult != null && (Boolean) leftResult) {
            Object rightResult = rightConditionExecutor.execute(event);
            if (rightResult != null && (Boolean) rightResult) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public ExpressionExecutor cloneExecutor(String key) {
        return new AndConditionExpressionExecutor(leftConditionExecutor.cloneExecutor(key), rightConditionExecutor
                .cloneExecutor(key));
    }

}
