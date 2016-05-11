/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package fr.pjthin.vertx.client.data;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link fr.pjthin.vertx.client.data.User}.
 *
 * NOTE: This class has been automatically generated from the {@link fr.pjthin.vertx.client.data.User} original class using Vert.x codegen.
 */
public class UserConverter {

  public static void fromJson(JsonObject json, User obj) {
    if (json.getValue("cryptedPasswd") instanceof String) {
      obj.setCryptedPasswd((String)json.getValue("cryptedPasswd"));
    }
    if (json.getValue("gender") instanceof String) {
      obj.setGender(fr.pjthin.vertx.client.data.Gender.valueOf((String)json.getValue("gender")));
    }
    if (json.getValue("id") instanceof Number) {
      obj.setId(((Number)json.getValue("id")).intValue());
    }
    if (json.getValue("login") instanceof String) {
      obj.setLogin((String)json.getValue("login"));
    }
  }

  public static void toJson(User obj, JsonObject json) {
    if (obj.getCryptedPasswd() != null) {
      json.put("cryptedPasswd", obj.getCryptedPasswd());
    }
    if (obj.getGender() != null) {
      json.put("gender", obj.getGender().name());
    }
    json.put("id", obj.getId());
    if (obj.getLogin() != null) {
      json.put("login", obj.getLogin());
    }
  }
}