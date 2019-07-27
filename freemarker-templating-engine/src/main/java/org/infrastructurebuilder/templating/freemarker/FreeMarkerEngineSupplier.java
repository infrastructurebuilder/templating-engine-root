/**
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
package org.infrastructurebuilder.templating.freemarker;

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.templating.AbstractTemplatingEngineSupplier;
import org.infrastructurebuilder.templating.TemplatingEngine;
import org.infrastructurebuilder.templating.TemplatingEngineSupplier;

@Named(org.infrastructurebuilder.templating.freemarker.FreeMarkerEngineSupplier.FREEMARKER)
@Typed(TemplatingEngineSupplier.class)
public class FreeMarkerEngineSupplier extends AbstractTemplatingEngineSupplier<TemplatingEngine> {

  public static final String FREEMARKER = "freemarker";

  @Override
  public TemplatingEngine get() {
    return new FreemarkerExecutionComponent(
        // exec source
        getExecutionSource(),
        // source root path
        getSourcePathRoot(), isIncludeDotFiles(), Optional.empty(), Optional.empty(), getSourcesOutputDirectory(),
        getProject().get(), isIncludeHiddenFiles(), isCaseSensitive(), getPrefixPath());
  }

  @Override
  public String getId() {
    return FREEMARKER;
  }

}
