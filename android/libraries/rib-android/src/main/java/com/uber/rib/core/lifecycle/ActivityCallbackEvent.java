/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.core.lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.Locale;

/** Callback events that can be emitted by Activities. */
public class ActivityCallbackEvent implements ActivityEvent {

  private static final ActivityCallbackEvent LOW_MEMORY_EVENT =
      new ActivityCallbackEvent(Type.LOW_MEMORY);

  private final Type type;

  private ActivityCallbackEvent(Type type) {
    this.type = type;
  }

  /**
   * Creates an event for activity results.
   *
   * @param requestCode the request code
   * @param resultCode the result code
   * @param resultData the result data intent
   * @return the created ActivityEvent.
   */
  public static ActivityCallbackEvent.ActivityResult createOnActivityResultEvent(
      int requestCode, int resultCode, @Nullable Intent resultData) {
    return new ActivityResult(resultData, requestCode, resultCode);
  }

  /**
   * Creates an event for activity new intent.
   *
   * @param intent the new intent
   * @return the created ActivityEvent.
   */
  public static ActivityCallbackEvent.NewIntent createOnActivityNewIntentEvent(
          Intent intent) {
    return new NewIntent(intent);
  }

  /**
   * Creates an event for request permissions result.
   *
   * @param requestCode the request code passed in when request permissions.
   * @param permissions the requested permissions.
   * @param grantResults the grant results for the corresponding permissions.
   * @return the created ActivityEvent.
   */
  public static ActivityCallbackEvent.RequestPermissionsResult createRequestPermissionsResultEvent(
      int requestCode, String[] permissions, int[] grantResults) {
    return new RequestPermissionsResult(requestCode, permissions, grantResults);
  }

  /**
   * Creates an activity event for a given type.
   *
   * @param type The type of event to get.
   * @return The corresponding ActivityEvent.
   */
  public static ActivityCallbackEvent create(Type type) {
    switch (type) {
      case LOW_MEMORY:
        return LOW_MEMORY_EVENT;
      default:
        throw new IllegalArgumentException(
            "Use the createOn"
                + capitalize(type.name().toLowerCase(Locale.US))
                + "Event() method for this type!");
    }
  }

  /**
   * Creates an event for onSaveInstanceState.
   *
   * @param outState the outState bundle.
   * @return the created ActivityEvent.
   */
  public static ActivityCallbackEvent createOnSaveInstanceStateEvent(@Nullable Bundle outState) {
    return new ActivityCallbackEvent.SaveInstanceState(outState);
  }

  /** @return this event's type. */
  @Override
  public Type getType() {
    return this.type;
  }

  private static String capitalize(final String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }

  /** Types of activity events that can occur. */
  public enum Type implements BaseType {
    LOW_MEMORY,
    ACTIVITY_RESULT,
    SAVE_INSTANCE_STATE,
    NEW_INTENT,
    REQUEST_PERMISSION_RESULT
  }

  /**
   * An {@link ActivityCallbackEvent} that encapsulates information from {@link
   * Activity#onActivityResult(int, int, Intent)}.
   */
  public static class ActivityResult extends ActivityCallbackEvent {

    @Nullable private final Intent data;
    private final int requestCode;
    private final int resultCode;

    private ActivityResult(@Nullable Intent data, int requestCode, int resultCode) {
      super(Type.ACTIVITY_RESULT);
      this.data = data;
      this.requestCode = requestCode;
      this.resultCode = resultCode;
    }

    /** @return this event's activity result data intent. */
    @Nullable
    public Intent getData() {
      return data;
    }

    /** @return this event's request code. */
    public int getRequestCode() {
      return requestCode;
    }

    /** @return this event's result code. */
    public int getResultCode() {
      return resultCode;
    }
  }

  /**
   * An {@link ActivityCallbackEvent} that encapsulates information from {@link
   * Activity#onSaveInstanceState(Bundle)}.
   */
  public static class SaveInstanceState extends ActivityCallbackEvent {

    @Nullable private Bundle outState;

    private SaveInstanceState(@Nullable Bundle outState) {
      super(Type.SAVE_INSTANCE_STATE);
      this.outState = outState;
    }

    /** @return this event's outState data. */
    @Nullable
    public Bundle getOutState() {
      return outState;
    }
  }

  /**
   * An {@link ActivityCallbackEvent} that encapsulates information from {@link
   * Activity#onNewIntent(Intent)}.
   */
  public static class NewIntent extends ActivityCallbackEvent {

    private final Intent intent;

    private NewIntent(Intent intent) {
      super(Type.NEW_INTENT);
      this.intent = intent;
    }

    /** @return this event's activity new intent. */
    public Intent getIntent() {
      return intent;
    }
  }

  /**
   * An {@link ActivityCallbackEvent} that encapsulates information from {@link
   * Activity#onRequestPermissionsResult(int, String[], int[])}.
   */
  public static class RequestPermissionsResult extends ActivityCallbackEvent {

    private final int requestCode;
    private final String[] permissions;
    private final int[] grantResults;

    private RequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      super(Type.REQUEST_PERMISSION_RESULT);
      this.requestCode = requestCode;
      this.permissions = permissions;
      this.grantResults = grantResults;
    }

    /** @return the request code passed in when request permissions. */
    public int getRequestCode() {
      return requestCode;
    }

    /** @return the requested permissions. */
    public String[] getPermissions() {
      return permissions;
    }

    /** @return the grant results for the corresponding permissions. */
    public int[] getGrantResults() {
      return grantResults;
    }
  }
}
