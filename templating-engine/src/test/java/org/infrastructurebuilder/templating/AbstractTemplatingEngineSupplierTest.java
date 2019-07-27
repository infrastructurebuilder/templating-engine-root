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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.maven.project.MavenProject;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractTemplatingEngineSupplierTest {

  private static WorkingPathSupplier wps;
  private static Path target;

  @BeforeClass
  public static void setupBC() {
    wps = new WorkingPathSupplier();
    target = wps.getRoot();

  }

  @AfterClass
  public static void teardownClass() {
    wps.finalize();
  }

  private DummyTemplatingEngineSupplier engineSupplier;

  private Path testClasses;
  private MavenProject p;

  @Before
  public void setUp() throws Exception {
    p = new MavenProject();
    engineSupplier = new DummyTemplatingEngineSupplier();
    engineSupplier.setProject(p);
    testClasses = target.resolve("test-classes");
    engineSupplier.setSourcePathRoot(testClasses.toFile());
    engineSupplier.setExecutionSource(testClasses.resolve("execFiles").toFile());
    final Path generated = target.resolve("generated-sources");
    Files.createDirectories(generated);
    final Path generatedResources = target.resolve("generated-resources");
    Files.createDirectories(generatedResources);
    //    engineSupplier.setResourcesOutputDirectory(generatedResources.toFile());
    engineSupplier.setSourcesOutputDirectory(generated.toFile());

  }

  @Test
  public void testExecuteNoLogger() throws TemplatingEngineException, IOException {
    final Path empty = testClasses.resolve("execFiles").resolve("empty");
    Files.createDirectories(empty);
    engineSupplier.setExecutionSource(empty.toFile());
    assertFalse(engineSupplier.get().execute().isPresent()); // False when no files
  }

  @Test
  public void testExecuteNoLoggerWithFile() throws Exception {
    engineSupplier.setExecutionSource(testClasses.resolve("execFiles").toFile());
    final Optional<String> s = engineSupplier.get().execute();
    assertTrue(s.isPresent()); // False when no files
  }

  @Test
  public void testGet() {
    assertNotNull(engineSupplier.get());
  }

}
