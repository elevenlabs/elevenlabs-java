package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * A tool's definition as this SDK models it, to tell whether two tools are the same: its request model without the
 * fields the generated models don't declare. The tool builder keeps such fields from a loaded config, but a tool
 * written in code can't have them, so they don't count here; {@code push} checks them separately, against the
 * server's copy.
 */
final class ToolDefinition {

  /** The SDK's mapper, writing only declared properties: every generated model's extra fields are left out. */
  private static final ObjectMapper MODELED = ObjectMappers.JSON_MAPPER.copy()
    .setAnnotationIntrospector(
      new JacksonAnnotationIntrospector() {
        private static final long serialVersionUID = 1L;

        @Override
        public Boolean hasAnyGetter(Annotated annotated) {
          return annotated.hasAnnotation(JsonAnyGetter.class) ? null : super.hasAnyGetter(annotated);
        }

        @Override
        public boolean hasIgnoreMarker(AnnotatedMember member) {
          return member.hasAnnotation(JsonAnyGetter.class) || super.hasIgnoreMarker(member);
        }
      }
    );

  private ToolDefinition() {}

  /** The definition as a string to compare, or {@code null} if the tool is invalid. */
  static String of(Tool tool) {
    try {
      Object config = tool.build().getToolConfig();
      return tool.getKind().wireName() + ":" + CanonicalJson.toCanonicalString(Wire.withoutNulls(MODELED.valueToTree(config)));
    } catch (RuntimeException e) {
      return null;
    }
  }
}
