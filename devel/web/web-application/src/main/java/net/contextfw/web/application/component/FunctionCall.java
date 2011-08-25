/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.component;


/**
 * Defines a javascript call invoked from server side.
 * 
 * <p>
 * This class is a convinience class to make calls in with correct convention.
 * </p>
 * <p>
 * The call will be in form: <code>functionName([args,...]);
 * </p>
 */
public class FunctionCall extends Script {

    private final String function;
    private final Object[] args;
    
    public FunctionCall(String function, Object... args) {
        this.function = function;
        this.args = args;
    }

    protected static String toScript(String function, int argCount, boolean isFull) {
        StringBuilder b = new StringBuilder(function).append("(");
        String delim = "";
        for (int i = 0; i < argCount; i++) {
            b.append(delim).append("{").append(i).append("}");
            delim = ",";
        }
        b.append(")");
        if (isFull) {
            b.append(";\n");
        }
        return b.toString();
    }

    @Override
    public String getScript(ScriptContext scriptContext) {
        return getScript(scriptContext, true);
    }
    
    protected String getScript(ScriptContext scriptContext, boolean isFull) {
        return FunctionCall.toScript(getFunctionName(scriptContext),
                args == null ? 0 : args.length, isFull);
    }

    @Override
    public Object[] getArguments(ScriptContext scriptContext) {
        return args; // NOSONAR
    }

    /**
     * Override this, if function name is determined lazily.
     * @return
     */
    protected String getFunctionName(ScriptContext scriptContext) {
        return function;
    }
}