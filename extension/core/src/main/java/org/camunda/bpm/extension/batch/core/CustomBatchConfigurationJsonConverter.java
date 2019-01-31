/*
 * Copyright © 2012 - 2018 camunda services GmbH and various authors (info@camunda.com)
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
package org.camunda.bpm.extension.batch.core;

import org.apache.commons.lang3.SerializationUtils;
import org.camunda.bpm.engine.impl.json.JsonObjectConverter;
import org.camunda.bpm.engine.impl.util.JsonUtil;
import org.camunda.bpm.engine.impl.util.json.JSONArray;
import org.camunda.bpm.engine.impl.util.json.JSONObject;

import java.io.Serializable;

public class CustomBatchConfigurationJsonConverter<T> extends JsonObjectConverter<CustomBatchConfiguration<T>> {

  static final String EXCLUSIVE = "exclusive";
  static final String DATA_SERIALIZED = "data_serialized";

  public static <T> CustomBatchConfigurationJsonConverter<T> of() {
    return new CustomBatchConfigurationJsonConverter<>();
  }

  @Override
  public JSONObject toJsonObject(final CustomBatchConfiguration<T> customBatchConfiguration) {
    final JSONObject json = new JSONObject();

    JsonUtil.addField(json, EXCLUSIVE, customBatchConfiguration.isExclusive());
    JsonUtil.addField(json, DATA_SERIALIZED,
      new JSONArray(SerializationUtils.serialize((Serializable) customBatchConfiguration.getData())));

    return json;
  }

  @Override
  public CustomBatchConfiguration<T> toObject(final JSONObject json) {
    final JSONArray jsonArray = json.getJSONArray(DATA_SERIALIZED);
    final byte[] byteArray = new byte[jsonArray.length()];
    for (int n = 0; n < jsonArray.length(); n++) {
      byteArray[n] = ((Integer)jsonArray.get(n)).byteValue();
    }

    return CustomBatchConfiguration.of(
      SerializationUtils.deserialize(byteArray),
      json.getBoolean(EXCLUSIVE));
  }

}
