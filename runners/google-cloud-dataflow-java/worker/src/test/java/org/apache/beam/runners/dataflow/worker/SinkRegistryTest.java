/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker;

import static org.apache.beam.runners.dataflow.util.Structs.addString;

import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link SinkRegistry}. */
@RunWith(JUnit4.class)
public class SinkRegistryTest {

  private PipelineOptions options = PipelineOptionsFactory.create();

  @Test
  public void testCreatePredefinedSink() throws Exception {
    CloudObject spec = CloudObject.forClassName("AvroSink");
    addString(spec, "filename", "/path/to/file.txt");

    SizeReportingSinkWrapper<?> sink =
        SinkRegistry.defaultRegistry()
            .create(
                spec,
                StringUtf8Coder.of(),
                options,
                BatchModeExecutionContext.forTesting(options, "testStage"),
                TestOperationContext.create());
    Assert.assertThat(sink.getUnderlyingSink(), new IsInstanceOf(AvroByteSink.class));
  }

  @Test
  public void testCreateUnknownSink() throws Exception {
    CloudObject spec = CloudObject.forClassName("UnknownSink");
    com.google.api.services.dataflow.model.Sink cloudSink =
        new com.google.api.services.dataflow.model.Sink();
    cloudSink.setSpec(spec);
    cloudSink.setCodec(CloudObjects.asCloudObject(StringUtf8Coder.of()));
    try {
      SinkRegistry.defaultRegistry()
          .create(
              spec,
              StringUtf8Coder.of(),
              options,
              BatchModeExecutionContext.forTesting(options, "testStage"),
              TestOperationContext.create());
      Assert.fail("should have thrown an exception");
    } catch (Exception exn) {
      Assert.assertThat(exn.toString(), CoreMatchers.containsString("Unable to create a Sink"));
    }
  }
}