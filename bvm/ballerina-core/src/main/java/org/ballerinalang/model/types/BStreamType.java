/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.model.types;

import org.ballerinalang.model.values.BStream;
import org.ballerinalang.model.values.BValue;

/**
 * {@code BStreamType} represents streaming data in Ballerina.
 *
 * @since 0.965.0
 */
public class BStreamType extends BType {

    private BType constraint;

    /**
     * Creates a {@code BStreamType} which represents the stream type.
     *
     * @param typeName  string name of the type
     * @param pkgPath   package path
     */
    BStreamType(String typeName, String pkgPath) {
        super(typeName, pkgPath, BStream.class);
    }

    public BStreamType(BType constraint) {
        super(TypeConstants.STREAM_TNAME, null, BStream.class);
        this.constraint = constraint;
    }

    public BType getConstrainedType() {
        return constraint;
    }

    @Override
    public <V extends BValue> V getZeroValue() {
        return null;
    }

    @Override
    public <V extends BValue> V getEmptyValue() {
        return null; //TODO:check - return (V) new BStream();
    }

    @Override
    public TypeSignature getSig() {
        if (constraint == null) {
            return new TypeSignature(TypeSignature.SIG_STREAM);
        } else {
            return new TypeSignature(TypeSignature.SIG_STREAM, constraint.getPackagePath(), constraint.getName());
        }
    }

    @Override
    public int getTag() {
        return TypeTags.STREAM_TAG;
    }
}
