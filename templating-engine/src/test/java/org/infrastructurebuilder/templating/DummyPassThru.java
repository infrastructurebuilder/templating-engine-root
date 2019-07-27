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
package org.infrastructurebuilder.templating;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class DummyPassThru extends AbstractTemplatingEngine<DummyPassThru> {

  public DummyPassThru(final File src, final String sourcePathRoot, final boolean includeDotFiles,
      final Optional<Log> log, final Optional<Collection<String>> sourceExtensions, final File sourceOutputDir,
      final MavenProject project, final boolean includeHiddenFiles, final boolean caseSensitive,
      final Optional<Path> prefixPath) {
    super(src, sourcePathRoot, includeDotFiles, log, sourceExtensions, sourceOutputDir.toPath(), project,
        includeHiddenFiles, caseSensitive, prefixPath);
  }

  @Override
  public DummyPassThru createEngine(final String sourcePathRoot) throws Exception {
    return this;
  }

  @Override
  public void writeTemplate(final DummyPassThru engine, final String canoTemplate, final File outFile)
      throws Exception {

  }

}
