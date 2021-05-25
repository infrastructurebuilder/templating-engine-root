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

import static java.util.Objects.requireNonNull;
import static org.infrastructurebuilder.templating.TemplatingEngine.getPropertyNames;
import static org.infrastructurebuilder.templating.TemplatingEngine.mergeProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.infrastructurebuilder.util.core.IBDirScannerSupplier;
import org.infrastructurebuilder.util.core.StringListSupplier;
import org.infrastructurebuilder.util.filescanner.DefaultIBDirScannerSupplier;

/**
 * @author mykel.alvis
 *
 * @param <T>
 */
abstract public class AbstractTemplatingEngine<T> implements TemplatingEngine {

  public final static List<String>        SCM_NAMES           = Arrays.asList(".svn", ".git", "cvs");

  private static final Collection<String> DEFAULT_SOURCE_LIST = new ArrayList<>(
      java.util.Arrays.asList("java", "kt", "scala", "groovy", "clj", "go", "js", "rust"));

  public static final boolean endsWith(final File f, final Collection<String> extensions) {
    final String s = f.getAbsolutePath().toLowerCase();
    if (extensions != null) {
      for (final String e : extensions) {
        if (s.endsWith("." + e))
          return true;
      }
    }
    return false;
  }

  public static String getDisplayedContext(final Optional<MavenProject> project, final Map<String, Object> properties) {
    final Map<String, Object> p = new HashMap<>();
    project.map(MavenProject::getProperties).ifPresent(pproj -> {
      pproj.stringPropertyNames().forEach(key -> p.put(key, pproj.getProperty(key)));
    });
    final Map<String, Object>     gp = mergeProperties(p, properties);
    final TreeMap<String, Object> m  = new TreeMap<>();
    getPropertyNames(gp).stream().sorted().forEach(n -> m.put(n, gp.get(n)));

    return String.join("\n", m.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.toList()).toArray(new String[0]));
  }

  /**
   * @param templateRelPath path to the template file, relative to sourcePathRoot
   * @param executionSource
   * @param outputDirectory root to target output path
   * */
  public static final Path getOutputFile(final Path templateRelPath, final Path executionSource,
      final Path outputDirectory) throws IOException {

    String rel    = templateRelPath.toString();
    String relLow = rel.toLowerCase();
    for (final String suf : new String[] { ".vm", ".velo", ".velocity" }) {
      if (relLow.endsWith(suf)) {
        rel = rel.substring(0, rel.length() - suf.length());
        break;
      }
    }
    return outputDirectory.resolve(rel);
  }

  public static final boolean isSCMDir(final File f) {
    return SCM_NAMES.contains(Optional.ofNullable(f).map(File::getName).map(String::toLowerCase).orElse("_FALSE"));
  }

  // public static void listVeloFiles(final File f, final Set<File> out, final
  // boolean includeDotFiles,
  // final boolean includeHidden) throws IOException {
  // if (f.isHidden() && !includeHidden)
  // return;
  //
  // final String n = f.getName().toLowerCase();
  // if (f.isDirectory()) {
  // if (isSCMDir(f))
  // return;
  // for (final File ff : f.listFiles()) {
  // listVeloFiles(ff.getAbsoluteFile(), out, includeDotFiles, includeHidden);
  // }
  // } else if (f.isFile()) {
  // if (includeDotFiles || !n.startsWith(".")) {
  // out.add(f);
  // }
  // }
  // }

  public static final String prependDot(String s) {
    if (s != null && !s.startsWith(".")) {
      s = "." + s;
    }
    return s;
  }

  public static final String processComments(final String source, final Function<String, String> processor) {
    final Pattern      p  = Pattern.compile("(?m)(?s)/\\*\\*?.*?\\*/");
    final Matcher      m  = p.matcher(source);
    final StringBuffer sb = new StringBuffer();
    while (m.find()) {
      final String comment     = m.group();
      final String replacement = processor.apply(comment);
      m.appendReplacement(sb, "");
      sb.append(replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static final String quoteSharpsInComments(final String source) {
    return processComments(source, f -> f.replaceAll("(?<=\\w)#", "\\\\#"));
  }

  public static final String unquoteSharpsInComments(final String source) {
    return processComments(source, f -> f.replaceAll("\\\\#", "#"));
  }

  private final Path                executionSource;

  private final Path                sourcePathRoot;

  private final boolean             includeDotFiles;

  private final Log                 log;

  private final Collection<String>  _sourceExtensions;

  private final Path                sourcesOutputDirectory;

  private final MavenProject        project;

  private final Map<String, Object> properties;

  private final boolean             includeHiddenFiles;

  private final boolean             caseSensitive;

  private final Optional<Path>      prefixPath;

  /**
   * @param src
   * @param sourcePathRoot
   * @param includeDotFiles
   * @param log
   * @param sourceExtensions
   * @param sourceOutputDir
   * @param project
   * @param includeHiddenFiles
   * @param caseSensitive
   * @param prefixPath
   */
  public AbstractTemplatingEngine(
      //
      final Path src,
      //
      final Path sourcePathRoot,
      // Include dot files
      final boolean includeDotFiles,
      // Logging
      final Optional<Log> log,
      // Files with these extensions are called "sources"
      final Optional<Collection<String>> sourceExtensions,
      // Target location
      final Path sourceOutputDir,
      // Maven project for properties
      final MavenProject project,
      // ALSO include hidden?
      final boolean includeHiddenFiles,
      // Match case sensitive
      final boolean caseSensitive,
      // Prepend this to the output path
      final Optional<Path> prefixPath,
      // "the" properties
      final Supplier<Map<String, Object>> propertiesSupplier) {
    super();
    this.executionSource = requireNonNull(src);
    this.sourcePathRoot = requireNonNull(sourcePathRoot);
    this.includeDotFiles = includeDotFiles;
    this.log = requireNonNull(log).orElse(new DefaultLog(new ConsoleLogger(0, "name")));
    this._sourceExtensions = requireNonNull(sourceExtensions).orElse(DEFAULT_SOURCE_LIST).stream()
        .map(AbstractTemplatingEngine::prependDot).collect(Collectors.toSet());
    this.sourcesOutputDirectory = requireNonNull(sourceOutputDir);
    this.project = requireNonNull(project);
    this.includeHiddenFiles = includeHiddenFiles;
    this.caseSensitive = caseSensitive;
    this.prefixPath = requireNonNull(prefixPath);
    this.properties = requireNonNull(propertiesSupplier).get();
    this.prefixPath.ifPresent(pp -> {
      if (pp.isAbsolute())
        throw new TemplatingEngineException("Prefix path " + pp + " is not relative");
    });
  }

  // FIXME make encoding a string parameter?
  abstract public T createEngine(Path sourcePathRoot) throws Exception;

  @Override
  public Optional<String> execute() throws TemplatingEngineException {
    final StringListSupplier   includes = () -> Arrays.asList("**/*");
    final StringListSupplier   excludes = () -> new ArrayList<>(SCM_NAMES);
    final IBDirScannerSupplier ss       = new DefaultIBDirScannerSupplier(
        //
        () -> getExecutionSource(),
        //
        includes,
        //
        excludes,
        // You can't process directories, so ignore them at all times
        () -> true,
        // Add the other stuff
        () -> !isIncludeHiddenFiles(),
        //
        () -> !isIncludeDotFiles(),
        //
        () -> isCaseSensitive());
    final List<Path>           paths    = ss.get().scan()
        // Fetch the "included" files
        .get(true)
        //
        .stream()
        //
        .filter(f -> !f.endsWith("~"))
        //
        .filter(f -> !f.endsWith(".bak"))
        //
        .map(Path::toAbsolutePath)
        //
        .map(getSourcePathRoot()::relativize)
        //
        .distinct()
        //
        .collect(Collectors.toList());
    getLog().debug("Found " + paths.size() + " files in '" + getExecutionSource() + "'...");
    Map<String, Object> p = getProperties();

    if (getProject().isPresent()) {
      Properties pprops = getProject().get().getProperties();
      pprops.entrySet().stream().forEach(e -> p.put(e.getKey().toString(), e.getValue()));
    }
    p.put("LB", "${");
    p.put("RB", "}");
    getLog().debug("Got Maven getProperties() : " + p);
    getLog().debug("Got getProperties() : " + getProperties());

    if (paths.isEmpty())
      return Optional.empty();

    final String displayedContext = getDisplayedContext(getProject(), getProperties());

    for (final Path file : paths) {
      try {
        final Path outputDirectory = getOutputDirectoryForFile(file);
        final Path outFile         = getOutputFile(file, getExecutionSource(), outputDirectory);
        String     originalFile    = getSourcePathRoot().resolve(file).toString();
        getLog().debug("Executing template '" + originalFile + "'...");
        getLog().info("Writing " + originalFile + " to " + outFile);
        final T engine = createEngine(getSourcePathRoot());
        writeTemplate(engine, file.toString(), outFile);

      } catch (final Exception ex) {
        throw new TemplatingEngineException("Failed to execute template '" + file + "'", ex);
      }
    }

    return Optional.ofNullable(displayedContext);
  }

  @Override
  public Path getExecutionSource() {
    return this.executionSource;
  }

  @Override
  public Optional<Path> getPrefixPath() {
    return this.prefixPath;
  }

  @Override
  public Optional<MavenProject> getProject() {
    return Optional.ofNullable(project);
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }

  public Collection<String> getSourceExtensions() {
    return this._sourceExtensions;
  }

  @Override
  public Path getSourcePathRoot() {
    return sourcePathRoot;
  }

  public Path getSourcesOutputDirectory() {
    return sourcesOutputDirectory;
  }

  @Override
  public boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  @Override
  public boolean isIncludeDotFiles() {
    return this.includeDotFiles;
  }

  @Override
  public boolean isIncludeHiddenFiles() {
    return this.includeHiddenFiles;
  }

  abstract public void writeTemplate(T engine, String canoTemplate, Path outFile) throws Exception;

  protected Log getLog() {
    return this.log;
  }

  protected Path getOutputDirectoryForFile(final Path file) {
    return this.sourcesOutputDirectory;
  }

}