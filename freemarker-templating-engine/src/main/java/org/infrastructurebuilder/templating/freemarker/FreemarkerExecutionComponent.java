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
package org.infrastructurebuilder.templating.freemarker;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.templating.AbstractTemplatingEngine;
import org.infrastructurebuilder.util.core.IBUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerExecutionComponent extends AbstractTemplatingEngine<Configuration> {

  static final Map<String, Object> createContext(final Optional<MavenProject> project, final Map<String,Object> properties) {
    final Map<String, Object> context = new HashMap<>();
    context.put("project", project.orElse(null));

    Properties oo;
    oo = project.isPresent() ? project.get().getProperties() : new Properties();
    for (final Map.Entry<Object, Object> e : oo.entrySet()) {
      final String propName = ((String) e.getKey()).replace('.', '_'), propValue = (String) e.getValue();
      context.put(propName, propValue);
    }

    if (properties != null) {
      properties.entrySet().forEach(e -> context.put(e.getKey(), e.getValue()));
    }
    return context;
  }

  public FreemarkerExecutionComponent(final Path src, final Path sourcePathRoot, final boolean includeDotFiles,
      final Optional<Log> log, final Optional<Collection<String>> sourceExtensions, final Path sourceOutputDir,
      final MavenProject project, final boolean includeHiddenFiles, final boolean caseSensitive,
      final Optional<Path> prefixPath, final Supplier<Map<String,Object>> propsSupplier) {
    super(src, sourcePathRoot, includeDotFiles, log, sourceExtensions, sourceOutputDir, project,
        includeHiddenFiles, caseSensitive, prefixPath, propsSupplier);
  }

  @Override
  public final Configuration createEngine(final Path sourcePathRoot) throws Exception {
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
    // Specify the source where the template files come from. Here I set a
    // plain directory for it, but non-file-system sources are possible too:

    final TemplateLoader tl = new FileTemplateLoader(sourcePathRoot.toRealPath().toFile());
    cfg.setTemplateLoader(tl);

    // Set the preferred charset template files are stored in. UTF-8 is
    // a good choice in most applications:
    cfg.setDefaultEncoding("UTF-8"); // FIXME this should be a parameter

    // Sets how errors will appear.
    // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);

    // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
    cfg.setWrapUncheckedExceptions(true);
    return cfg;
  }

  @Override
  public void writeTemplate(final Configuration engine, final String canoTemplate, final Path outFile)
      throws Exception {
    final Template template = engine.getTemplate(canoTemplate);
    final Map<String, Object> _context = createContext(getProject(), getProperties());
    try (StringWriter out = new StringWriter()) {
      template.process(_context, out);
      Files.createDirectories(outFile.getParent());
      IBUtils.writeString(outFile, unquoteSharpsInComments(out.toString()));
    }

  }

}
