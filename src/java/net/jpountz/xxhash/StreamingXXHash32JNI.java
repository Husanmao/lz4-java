package net.jpountz.xxhash;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


final class StreamingXXHash32JNI extends StreamingXXHash32 {

  private long state;

  StreamingXXHash32JNI(int seed) {
    super(seed);
    state = XXHashJNI.XXH32_init(seed);
  }

  private void checkState() {
    if (state == 0) {
      throw new AssertionError("Already finalized");
    }
  }

  @Override
  public void reset() {
    checkState();
    XXHashJNI.XXH32_result(state);
    state = XXHashJNI.XXH32_init(seed);
  }

  @Override
  public int getValue() {
    checkState();
    return XXHashJNI.XXH32_getIntermediateResult(state);
  }

  @Override
  public void update(byte[] bytes, int off, int len) {
    checkState();
    XXHashJNI.XXH32_feed(state, bytes, off, len);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    // free memory
    if (state != 0) {
      XXHashJNI.XXH32_result(state);
      state = 0;
    }
  }

}
