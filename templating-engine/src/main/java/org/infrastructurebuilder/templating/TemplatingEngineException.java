/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.templating;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class TemplatingEngineException extends RuntimeException {

  private static final long serialVersionUID = -4074934259001259947L;
  public static ExceptionTranslator et = ET.newConfiguration().translate(Exception.class).to(TemplatingEngineException.class).done();

  public TemplatingEngineException() {
  }

  public TemplatingEngineException(final String message) {
    super(message);
  }

  public TemplatingEngineException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public TemplatingEngineException(final Throwable cause) {
    super(cause);
  }

}
