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
package org.apache.beam.sdk.extensions.euphoria.executor.local;

import java.util.Collections;
import org.apache.beam.sdk.extensions.euphoria.core.client.dataset.windowing.Window;
import org.apache.beam.sdk.extensions.euphoria.core.client.dataset.windowing.WindowedElement;
import org.apache.beam.sdk.extensions.euphoria.core.client.dataset.windowing.Windowing;
import org.apache.beam.sdk.extensions.euphoria.core.client.triggers.NoopTrigger;
import org.apache.beam.sdk.extensions.euphoria.core.client.triggers.Trigger;

class AttachedWindowing<T, W extends Window<W>> implements Windowing<T, W> {

  static final AttachedWindowing INSTANCE = new AttachedWindowing();

  private AttachedWindowing() {}

  @SuppressWarnings("unchecked")
  @Override
  public Iterable<W> assignWindowsToElement(WindowedElement<?, T> input) {
    return Collections.singleton((W) input.getWindow());
  }

  @Override
  public Trigger<W> getTrigger() {
    return NoopTrigger.get();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AttachedWindowing;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
