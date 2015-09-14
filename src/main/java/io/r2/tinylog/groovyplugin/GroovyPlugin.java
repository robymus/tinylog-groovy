/*
 * Copyright 2015 Robert Elek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.r2.tinylog.groovyplugin;

import org.codehaus.groovy.runtime.StackTraceUtils;

import org.pmw.tinylog.plugins.ExceptionSanitizer;
import org.pmw.tinylog.plugins.StackTraceProvider;

public class GroovyPlugin implements ExceptionSanitizer, StackTraceProvider
{
    /**
     * Checks if this trace belongs to a closure
     * @param e the stack trace element to check
     * @return true if it's possible inside a closure                                   
     */
    private boolean isClosure(StackTraceElement e) {
        return (e.getMethodName().equals("doCall")	              // method is doCall 
                && e.getClassName().matches(".*_closure[0-9]+")); // class name ends in _closure{number}
    }
    
	public Throwable sanitizeException(final Throwable exception) {
		return StackTraceUtils.sanitize(exception);
	}

	public StackTraceElement getStackTraceElement(final int depth, final boolean onlyClassName) {
		int pos = depth;
		StackTraceElement[] trace = StackTraceUtils.sanitize(new Throwable()).getStackTrace();
		// while we are inside closure, move outside (closure detection is based on names now so it's not perfect)
		while (pos < trace.length-1 && isClosure(trace[pos])) pos++;
		return trace[pos];
	}
}
