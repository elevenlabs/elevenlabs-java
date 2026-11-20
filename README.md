# ElevenLabs Java Library

![LOGO](https://github.com/elevenlabs/elevenlabs-python/assets/12028621/21267d89-5e82-4e7e-9c81-caf30b237683)

[![fern shield](https://img.shields.io/badge/%F0%9F%8C%BF-SDK%20generated%20by%20Fern-brightgreen)](https://buildwithfern.com/?utm_source=fern-elevenlabs/elevenlabs-java/readme)
[![Discord](https://badgen.net/badge/black/ElevenLabs/icon?icon=discord&label)](https://discord.gg/elevenlabs)
[![Twitter](https://badgen.net/badge/black/elevenlabsio/icon?icon=twitter&label)](https://twitter.com/elevenlabsio)
[![Maven Central](https://img.shields.io/maven-central/v/io.elevenlabs/elevenlabs-java)](https://central.sonatype.com/artifact/io.elevenlabs/elevenlabs-java)

The official Java SDK for [ElevenLabs](https://elevenlabs.io/). ElevenLabs brings the most compelling, rich and lifelike voices to creators and developers in just a few lines of code.

## 📖 API & Docs

Check out the [HTTP API documentation](https://elevenlabs.io/docs/api-reference). A full reference for this library is available [here](./reference.md).

## Requirements

This SDK requires Java 8 or later.

## Install

### Gradle

Add the dependency to your `build.gradle` file:

```groovy
dependencies {
  implementation 'io.elevenlabs:elevenlabs-java:1.0.0'
}
```

### Maven

Add the dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.elevenlabs</groupId>
  <artifactId>elevenlabs-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

### Main Models

1. **Eleven v4** (`eleven_v4`)
    - Highest quality and most expressive speech, with reliable voice cloning across long generations
    - 90+ languages with consistent voice quality across language switches
    - Up to 10,000 characters (~10 minutes of audio) per request
    - Used via the [Text to Dialogue API](https://elevenlabs.io/docs/api-reference/text-to-dialogue/convert)

2. **Eleven v4 Turbo** (`eleven_v4_turbo`)
    - State-of-the-art real-time speech, with a median latency of ~100ms
    - 90+ languages
    - Ideal for voice agents and other latency-sensitive use cases

3. **Eleven Multilingual v2** (`eleven_multilingual_v2`)
    - Excels in stability, language diversity, and accent accuracy
    - The most stable choice for long-form Text to Speech
    - Supports 29 languages

4. **Eleven Flash v2.5** (`eleven_flash_v2_5`)
    - Ultra-low latency and lowest cost
    - Supports 32 languages

For more detailed information about these models and others, visit the [ElevenLabs Models documentation](https://elevenlabs.io/docs/models).

Instantiate and use the client with the following:

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.resources.texttospeech.requests.BodyTextToSpeechFull;
import com.elevenlabs.api.resources.texttospeech.types.ConvertTextToSpeechRequestOutputFormat;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Example {

  public static void main(String[] args) throws Exception {
    // Reads the API key from the ELEVENLABS_API_KEY environment variable.
    // Pass it explicitly with ElevenlabsApiClient.builder().apiKey("YOUR_API_KEY") if you prefer.
    ElevenlabsApiClient client = ElevenlabsApiClient.builder().build();

    // The returned InputStream owns the HTTP response: close it, ideally with try-with-resources.
    try (
      InputStream audio = client
        .textToSpeech()
        .convert(
          "JBFqnCBsd6RMkjVDRZzb",
          BodyTextToSpeechFull.builder()
            .text("The first move is what sets everything in motion.")
            .modelId("eleven_multilingual_v2")
            .outputFormat(ConvertTextToSpeechRequestOutputFormat.MP_3_44100_128)
            .build()
        )
    ) {
      Files.copy(audio, Paths.get("output.mp3"), StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
```

The builder reads `ELEVENLABS_API_KEY` once, when `builder()` is called, and sends it as the
`xi-api-key` header. If the variable is unset and you don't call `apiKey(...)`, the client is still
built, but requests go out unauthenticated and the API answers with a 401 (`UnauthorizedError`).

### Text to Dialogue (Eleven v4)

Eleven v4 and Eleven v4 Turbo are served through the Text to Dialogue API, which turns a list of speaker turns into a single expressive audio stream.

```java
import com.elevenlabs.api.resources.texttodialogue.requests.BodyTextToDialogueMultiVoiceV1TextToDialoguePost;
import com.elevenlabs.api.resources.texttodialogue.types.ConvertTextToDialogueRequestOutputFormat;
import com.elevenlabs.api.types.DialogueInput;

try (
  InputStream audio = client
    .textToDialogue()
    .convert(
      BodyTextToDialogueMultiVoiceV1TextToDialoguePost.builder()
        .modelId("eleven_v4")
        .outputFormat(ConvertTextToDialogueRequestOutputFormat.MP_3_44100_128)
        .addInputs(
          DialogueInput.builder()
            .text("[giggling] Knock knock")
            .voiceId("JBFqnCBsd6RMkjVDRZzb")
            .build()
        )
        .addInputs(
          DialogueInput.builder()
            .text("[curious] Who is there?")
            .voiceId("Aw4FAjKCGjjNkVhN1Xmq")
            .build()
        )
        .build()
    );
  OutputStream file = new FileOutputStream("dialogue.mp3")
) {
  // Write each chunk as it arrives.
  byte[] buffer = new byte[8192];
  int read;
  while ((read = audio.read(buffer)) != -1) {
    file.write(buffer, 0, read);
  }
}
```

## Voices

List all your available voices with `search`.

```java
import com.elevenlabs.api.types.GetVoicesV2Response;
import com.elevenlabs.api.types.Voice;

GetVoicesV2Response response = client.voices().search();
for (Voice voice : response.getVoices()) {
  System.out.println(voice.getName().orElse("(unnamed)"));
}
```

Optional fields are returned as `java.util.Optional`, so `getName()` is an `Optional<String>`. For
information about the structure of the voices output, please refer to the
[official ElevenLabs API documentation for Get Voices](https://elevenlabs.io/docs/api-reference/get-voices).

## Agent workflows as code

`com.elevenlabs.api.agentworkflows` builds an agent's workflow in code. The `Workflow` is the start
node; each verb (`converse`, `tool`, `transfer`, `end`, `goTo`) adds a node, connects it, and
returns the new node, so a flow reads as one chain. `build()` returns the generated
`AgentWorkflowRequestModel` that agent create and update requests take.

```java
import com.elevenlabs.api.agentworkflows.Destination;
import com.elevenlabs.api.agentworkflows.ResultBranches;
import com.elevenlabs.api.agentworkflows.Workflow;
import com.elevenlabs.api.resources.agents.requests.UpdateAgentRequest;
import java.util.Arrays;

Workflow wf = new Workflow();
wf.converse("triage", "Find out what the caller needs")
  .when("Caller gave an order number")
  .tool("lookup_order", Arrays.asList("tool_get_order"))
  .onResult(new ResultBranches()
    .success(ok -> ok.converse("resolve", "Explain the order status").end())
    .failure(failed -> failed.goTo("triage")))
  .up()
  .when("Caller wants a human")
  .transfer("human", Destination.phone("+15551234567"));

client.agents().update("agent_id", UpdateAgentRequest.builder().workflow(wf.build()).build());
```

Chaining never throws. `build()` checks the whole graph and throws a
`WorkflowValidationException` listing every problem; `build(issue -> ...)` also receives
warnings such as `unreachable_node`. To edit an existing workflow, load it with
`Workflow.fromAgent(client.agents().get("agent_id"))` or `Workflow.fromJson(json)`, then use
`node(id).update(...)`, `insertBetween(...)` and `remove(...)`. Loaded node ids, edge ids,
positions and edge order are kept; new nodes are laid out automatically.

`toJsonString()` writes the API wire format with sorted keys, 2-space indentation and a trailing
newline, byte-identical to the other ElevenLabs SDKs, so the file can be stored in git next to
an agent config. A node type this SDK version doesn't model is kept as loaded by `toJson()`, but
`build()` rejects it with an `invalid_model` error, because the generated request model can't
represent it. To send such a workflow, pass `toJson()` instead of `build()`:
`UpdateAgentRequest.builder().additionalProperty("workflow", wf.toJson()).build()`.

## Agent tools as code

`com.elevenlabs.api.agenttools` builds agent tools in code: webhook tools (the server calls an HTTP
endpoint), client tools (the agent sends an event to the user's client) and system tools (built-in
ones such as ending or transferring the call). Each method returns the tool, so a tool reads as one
chain. `build()` returns the generated `ToolRequestModel` that tool create and update requests take.

```java
import com.elevenlabs.api.agenttools.Header;
import com.elevenlabs.api.agenttools.Schema;
import com.elevenlabs.api.agenttools.SystemTool;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agenttools.ToolOptions;
import com.elevenlabs.api.agenttools.ToolTransferOptions;
import com.elevenlabs.api.agenttools.WebhookTool;
import com.elevenlabs.api.agentworkflows.Destination;
import com.elevenlabs.api.types.PreToolSpeechMode;
import com.elevenlabs.api.types.TransferTypeEnum;

WebhookTool getOrder = Tool.webhook("get_order", "Look up an order by the number the caller gives")
  .get("https://api.example.com/orders/{order_id}")
  .pathParam("order_id", Schema.string().describe("The order number, e.g. A-1042"))
  .queryParam("include", Schema.string().constant("items"))
  .header("Authorization", Header.secret("sec_orders_api"))
  .assign("order_status", "order.status")
  .options(new ToolOptions().responseTimeoutSecs(10).preToolSpeech(PreToolSpeechMode.FORCE));

WebhookTool refund = Tool.webhook("issue_refund", "Refund an order the caller asked to return")
  .post("https://api.example.com/refunds")
  .body(Schema.object()
    .property("order_id", Schema.string().fromVariable("order_id"))
    .property("reason", Schema.string().describe("Why the caller wants a refund").choices("damaged", "late", "other"))
    .property("note", Schema.string().describe("Anything else the caller said").optional()));

SystemTool transfer = SystemTool.transferToNumber()
  .transfer("Caller asks for billing", Destination.phone("+15551234567"))
  .transfer("Caller asks for a manager", Destination.sipVariable("manager_sip"),
    new ToolTransferOptions().transferType(TransferTypeEnum.SIP_REFER));

String toolId = client.agents().tools().create(getOrder.build()).getId();
Files.write(Paths.get("tool_configs/issue_refund.json"), refund.toJsonString().getBytes(StandardCharsets.UTF_8));
```

Every literal param needs exactly one value source: `describe()` (the LLM provides it),
`fromVariable()`, `constant()`, `systemProvided()` or `omit()`. Schemas are immutable values, so one
schema can be reused across tools. Webhook and client tools are created with `agents().tools().create()`
and attached to an agent by id in `prompt.tool_ids`; a system tool goes in the agent's
`prompt.built_in_tools`, via `SystemTool.buildBuiltInConfig()`.

Chaining never throws. `build()`, `toJson()` and `toJsonString()` check the whole tool and throw a
`ToolValidationException` listing every problem; `build(issue -> ...)` also receives warnings. To
edit an existing tool, load it with `Tool.fromResponse(client.agents().tools().get("tool_id"))` or
`Tool.fromJson(json)`, narrow it with `asWebhook()`, `asClient()` or `asSystem()`, and use the same
methods (`pathParam`, `removeQueryParam`, `header`, ...). Loaded fields the builder doesn't model, and
the API's defaults for unset fields, are written back unchanged. A tool of a type this SDK can't edit
(such as `mcp`) is kept as loaded and reported as an `unknown_tool_type` warning.

`toJsonString()` writes the API wire format with sorted keys, 2-space indentation and a trailing
newline, byte-identical to the other ElevenLabs SDKs and to the CLI's `tool_configs/*.json`. The
generated request model holds a literal's constant as a `String`, `int`, `double` or `boolean`, so a
constant it would change (such as an integer outside the `int` range, or a nullable literal's
`null`) makes `build()` report `constant_type_mismatch` rather than send a different value; send the
`toJson()` output directly for such a tool.

## Agents as code

`com.elevenlabs.api.agentbuilder` puts an agent's settings, its tools and its workflow in one
definition. `client.agents().push(agent)` creates or updates the webhook and client tools the agent
uses, then creates or updates the agent with their ids filled in. Workflow tool nodes and subagents
take `Tool` objects as well as tool ids, so a workflow can use tools that don't exist yet.

```java
import com.elevenlabs.api.agentbuilder.Agent;
import com.elevenlabs.api.agentbuilder.AgentOptions;
import com.elevenlabs.api.agentbuilder.AgentProject;
import com.elevenlabs.api.agentbuilder.AgentPullOptions;
import com.elevenlabs.api.agentbuilder.AgentPushOptions;
import com.elevenlabs.api.agentbuilder.AgentPushResult;
import com.elevenlabs.api.agenttools.SystemTool;
import com.elevenlabs.api.agentworkflows.Workflow;

Workflow wf = new Workflow();
wf.converse("triage", "Find out what the caller needs")
  .when("Caller gave an order number")
  .tool("lookup", getOrder)              // a Tool object: push fills in its id
  .onSuccess()
  .converse("resolve", "Explain the order status");

Agent agent = new Agent("Support line", new AgentOptions()
  .prompt("You are a support agent for Acme.")
  .firstMessage("Hi, how can I help?")
  .tools(SystemTool.endCall(), refund)
  .workflow(wf));

AgentPushResult result = client.agents().push(agent);              // creates the tools, then the agent
client.agents().push(agent, new AgentPushOptions().agentId(result.getAgentId())); // later: updates them

AgentProject project = AgentProject.open(".");                     // the ElevenLabs CLI's layout
client.agents().push(agent, new AgentPushOptions().project(project));
Agent pulled = client.agents().pull("agent_123", new AgentPullOptions().project(project));
```

`agent.validate()` checks everything that needs no ids, so CI can run it without an API key; it
throws an `AgentValidationException` listing every problem. `push` validates first, so an invalid
agent writes nothing. Without a project, the tools an agent already has are found by name among the
tools that agent uses (on `branchId`'s config, when given), so a same-named tool another agent uses is
never touched. A tool the server already has as defined is left `UNCHANGED`; one the agent stopped
using is reported as `DETACHED` and isn't deleted. If a request fails partway, `push` throws an
`AgentPushException` whose `getResult()` holds what was already written, and a project opened from a
directory is saved with those ids.

`AgentProject` reads and writes the CLI's `agents.json`, `tools.json`, `agent_configs/` and
`tool_configs/` byte for byte, so a repo works with both the SDK and the CLI. Files are written only
by `push` and `pull`, owner-only (`0600`), never through a symlink and never outside the project; a
file nothing changed keeps its exact text. `AgentProject.fromFiles(map)` does the same without I/O.
`Agent.fromJson(json)` loads a single config, keeping the fields this SDK doesn't model, and
`agent.toJson(toolIds)` writes one back with sorted keys. YAML and TOML configs aren't supported yet.

## Streaming

Stream audio in real-time, as it's being generated. `convert` and `stream` both return an
`InputStream` read straight from the open HTTP response, so playback or a file write can start
before the server has finished. The stream holds the connection until it is closed.

```java
import com.elevenlabs.api.resources.texttospeech.requests.StreamTextToSpeechRequest;
import com.elevenlabs.api.resources.texttospeech.types.StreamTextToSpeechRequestOutputFormat;

try (
  InputStream audio = client
    .textToSpeech()
    .stream(
      "JBFqnCBsd6RMkjVDRZzb",
      StreamTextToSpeechRequest.builder()
        .text("This is a... streaming voice.")
        .modelId("eleven_multilingual_v2")
        .outputFormat(StreamTextToSpeechRequestOutputFormat.MP_3_44100_128)
        .build()
    )
) {
  byte[] buffer = new byte[8192];
  int read;
  while ((read = audio.read(buffer)) != -1) {
    // Process each chunk as it arrives.
    System.out.write(buffer, 0, read);
  }
}
```

## Music with metadata

`POST /v1/music/detailed` returns a song together with its metadata (composition plan, title,
genres and, if requested, word timestamps) as a `multipart/mixed` body. The generated
`client.music().composeDetailed(...)` can't decode that body, so use `MusicDetailed`, which sends the
same request through the client's configuration and splits the response into its parts.

```java
import com.elevenlabs.api.resources.music.ComposeDetailedResponse;
import com.elevenlabs.api.resources.music.MusicDetailed;
import com.elevenlabs.api.resources.music.requests.BodyComposeMusicWithADetailedResponseV1MusicDetailedPost;

MusicDetailed music = new MusicDetailed(client);
ComposeDetailedResponse song = music.composeDetailed(
  BodyComposeMusicWithADetailedResponseV1MusicDetailedPost.builder()
    .prompt("Epic orchestral music with dramatic strings")
    .musicLengthMs(60000)
    .build()
);

System.out.println(song.getJson().path("song_metadata").path("title").asText());
Files.write(Paths.get(song.getFilename()), song.getAudio());
```

`getJson()` is the metadata part as a Jackson `JsonNode`, with the API's snake_case keys.
`getAudio()` is the audio file's bytes, `getFilename()` the name the API suggests for it (or
`generated_music.mp3`), and `getSongId()` the `song-id` response header, which is sent when
`storeForInpainting(true)` is set. The whole response is held in memory. Per-call `RequestOptions`
apply as they do to generated methods. API errors throw the usual exceptions; a response that isn't a
complete multipart body with a JSON part and an audio part throws `ElevenlabsApiException`. For a
client constructed with `new ElevenlabsApiClient(config)`, pass that config: `new MusicDetailed(config)`.

## Waiting for Flows generations

Image, video and text-to-speech generations and template runs under `client.flows()` run
asynchronously: `create` returns an ID and `get` reports the status. `FlowsWaiter` adds
`createAndWait`, which does both, polling until the generation is `completed` or `failed`.

```java
import com.elevenlabs.api.flows.wait.FlowsWaitOptions;
import com.elevenlabs.api.flows.wait.FlowsWaitTimeoutException;
import com.elevenlabs.api.flows.wait.FlowsWaiter;
import com.elevenlabs.api.types.GptImage1Request;
import com.elevenlabs.api.types.ImageGenerationRequest;
import com.elevenlabs.api.types.MediaGenerationResponse;
import java.time.Duration;

FlowsWaiter flows = new FlowsWaiter(client);

MediaGenerationResponse generation;
try {
  generation = flows
    .image()
    .createAndWait(
      ImageGenerationRequest.gptImage1(
        GptImage1Request.builder().prompt("A lighthouse at dusk").build()
      ),
      FlowsWaitOptions.builder().waitTimeout(Duration.ofMinutes(10)).build()
    );
} catch (FlowsWaitTimeoutException e) {
  // Still running on the server: resume with client.flows().image().get(e.getId()).
  throw e;
}

if (generation.isCompleted()) {
  System.out.println(generation.getCompleted().get().getContentUrl());
} else {
  System.out.println(generation.getFailed().get().getErrorMessage());
}
```

`flows.video()` and `flows.textToSpeech()` work the same way, and
`flows.templates().runs().createAndWait(templateId, request)` waits for a template run.

- Between status checks it waits for the server's `Retry-After` header (seconds or an HTTP date),
  never less than `minPollInterval` (default 3 seconds, minimum 0.5), plus up to 20% jitter.
- `waitTimeout` bounds the whole wait. It defaults to none, which waits indefinitely. When it runs
  out, `FlowsWaitTimeoutException` carries the `id` (and `templateId` for a run) and the last
  status.
- A `failed` generation or run is returned, not thrown.
- A third `RequestOptions` argument applies to the create call and every status check; the status
  checks are GET requests, so `bodyProperties` reach only the create call.
- The calling thread blocks while it waits. Interrupting it stops the wait.

This is meant for scripts and quickstarts. In production, set a `webhook` on the request and
handle the webhook event instead of holding a thread open.

## Environments

This SDK allows you to configure different environments for API requests. It defaults to the production environment.

```java
import com.elevenlabs.api.core.Environment;

ElevenlabsApiClient client = ElevenlabsApiClient.builder()
  .environment(Environment.PRODUCTION_US)
  .build();
```

Available environments:
- `Environment.PRODUCTION` (the default, also available as `Environment.DEFAULT`)
- `Environment.PRODUCTION_US`
- `Environment.PRODUCTION_EU`
- `Environment.PRODUCTION_INDIA`
- `Environment.PRODUCTION_SINGAPORE`

To point the client somewhere else, such as a proxy, pass a base URL with
`builder().url("https://proxy.example.com")` or `environment(Environment.custom("https://proxy.example.com"))`.
Calling `environment(...)` clears a URL set earlier with `url(...)`.

## Exception Handling

When the API returns a non-success status code (4xx or 5xx), the SDK throws a
`com.elevenlabs.api.core.ElevenlabsApiApiException`. The HTTP status code is available via
`statusCode()`, the response body via `body()` and the response headers via `headers()`. Common
status codes throw named subclasses from `com.elevenlabs.api.errors`, so you can catch the one you care
about: `BadRequestError` (400), `UnauthorizedError` (401), `ForbiddenError` (403), `NotFoundError`
(404), `MethodNotAllowedError` (405), `ConflictError` (409) and `TooManyRequestsError` (429).
Endpoints that declare a validation error also throw `UnprocessableEntityError` (422), whose `body()`
is a typed `HttpValidationError`.

```java
import com.elevenlabs.api.core.ElevenlabsApiApiException;
import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.errors.NotFoundError;

try (InputStream audio = client.textToSpeech().convert(...)) {
  // ...
} catch (NotFoundError e) {
  // Handle a 404 specifically.
} catch (ElevenlabsApiApiException e) {
  System.out.println("API error occurred: " + e.getMessage());
  System.out.println("Status code: " + e.statusCode());
  System.out.println("Response body: " + e.body());
  // Optionally, rethrow the exception or handle accordingly.
} catch (ElevenlabsApiException e) {
  // No response: the request never reached the server (DNS failure, refused connection,
  // timeout). The transport exception is on e.getCause().
}
```

`ElevenlabsApiApiException` extends `ElevenlabsApiException`, which extends `RuntimeException`, so
none of these need to be declared.

## Webhooks

ElevenLabs signs every webhook it sends with an `ElevenLabs-Signature` header. `WebhookVerifier`
checks that signature against your webhook secret and returns the decoded event:

```java
import com.elevenlabs.api.webhooks.WebhookVerificationException;
import com.elevenlabs.api.webhooks.WebhookVerifier;
import com.fasterxml.jackson.databind.JsonNode;

// rawBody must be the request body exactly as received (a String or byte[]), not a parsed and
// re-serialized copy: any change to the bytes breaks the signature.
try {
  JsonNode event = WebhookVerifier.constructEvent(
    rawBody,
    request.getHeader("ElevenLabs-Signature"),
    System.getenv("ELEVENLABS_WEBHOOK_SECRET")
  );
  if ("post_call_transcription".equals(event.path("type").asText())) {
    String agentId = event.path("data").path("agent_id").asText();
    // ...
  }
} catch (WebhookVerificationException e) {
  // e.reason() says which check failed: MISSING_SIGNATURE_HEADER, MISSING_SECRET,
  // MISSING_SIGNATURE_SCHEME, TIMESTAMP_OUTSIDE_TOLERANCE, SIGNATURE_MISMATCH or INVALID_PAYLOAD.
  response.sendError(e.statusCode(), e.getMessage()); // always 400
}
```

Webhooks with a timestamp more than 30 minutes old are rejected as possible replays.
`WebhookVerificationException` extends `ElevenlabsApiApiException`, so an existing
`catch (ElevenlabsApiApiException e)` also catches it.

## WebSockets

Each realtime channel has its own WebSocket client. Register handlers for incoming messages, open the
connection, send typed messages with `sendPublish`, and call `close()` (or `disconnect()`) when done.
Connecting and sending return a `CompletableFuture<Void>`; handlers are called on OkHttp's WebSocket
thread, so hand long-running work off to your own executor.

```java
import com.elevenlabs.api.resources.texttospeech.websocket.TextToSpeechConnectOptions;
import com.elevenlabs.api.resources.texttospeech.websocket.TextToSpeechWebSocketClient;
import com.elevenlabs.api.types.CloseConnection;
import com.elevenlabs.api.types.InitializeConnection;
import com.elevenlabs.api.types.SendMessage;
import com.elevenlabs.api.types.SendText;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

ByteArrayOutputStream audio = new ByteArrayOutputStream();
CountDownLatch closed = new CountDownLatch(1);

TextToSpeechWebSocketClient socket = client
  .textToSpeech()
  .textToSpeechWebSocket("JBFqnCBsd6RMkjVDRZzb");

// Handlers run on OkHttp's WebSocket thread. Register them before connecting.
socket.onSubscribe(message ->
  message
    .getAudioOutput()
    .ifPresent(chunk -> {
      byte[] bytes = Base64.getDecoder().decode(chunk.getAudio());
      audio.write(bytes, 0, bytes.length);
    })
);
socket.onError(error -> error.printStackTrace());
socket.onDisconnected(reason -> closed.countDown());

// realtime(...) opens the connection; join() waits until it is open.
socket
  .realtime(
    TextToSpeechConnectOptions.builder()
      .modelId("eleven_multilingual_v2")
      .outputFormat("mp3_44100_128")
      .build()
  )
  .join();

socket.sendPublish(SendMessage.of(InitializeConnection.builder().build())).join();
socket.sendPublish(SendMessage.of(SendText.builder().text("Hello from a WebSocket. ").build())).join();
// An empty text message ends the stream; the server sends the remaining audio, then closes.
socket.sendPublish(SendMessage.of(CloseConnection.builder().build())).join();

closed.await(30, TimeUnit.SECONDS);
socket.close();
Files.write(Paths.get("realtime.mp3"), audio.toByteArray());
```

The SDK has one WebSocket client per channel:

- `client.textToSpeech().textToSpeechWebSocket(voiceId)` returns a `TextToSpeechWebSocketClient`; connect with `realtime(...)`
- `client.textToSpeechMultiContext().textToSpeechMultiContextWebSocket(voiceId)` returns a `TextToSpeechMultiContextWebSocketClient`; connect with `realtime(...)`
- `client.textToDialogue().textToDialogueWebSocket()` returns a `TextToDialogueWebSocketClient`; connect with `realtime(...)`
- `client.textToDialogueMultiContext().textToDialogueMultiContextWebSocket()` returns a `TextToDialogueMultiContextWebSocketClient`; connect with `realtime(...)`
- `client.speechToText().speechToTextWebSocket()` returns a `SpeechToTextWebSocketClient`; connect with `realtime(...)`
- `client.translation().translationWebSocket()` returns a `TranslationWebSocketClient`; connect with `realtime(TranslationConnectOptions)`
- `client.dubbing().dubbingWebSocket()` returns a `DubbingWebSocketClient`; connect with `live(DubbingConnectOptions)`
- `client.speechEngine().speechEngineWebSocket()` returns a `SpeechEngineWebSocketClient`; connect with `upstream()`

Every client also has `onConnected`, `onDisconnected`, `onError` and `onMessage` (the raw JSON text)
handlers, and `getReadyState()`. A connection that drops with a code other than 1000 is reopened
automatically; tune that with `reconnectOptions(...)`.

## Speech Engine

With [Speech Engine](https://elevenlabs.io/docs/overview/capabilities/speech-engine) you run the
conversation logic (your own LLM, tools and state) and ElevenLabs handles speech-to-text and
text-to-speech. Your server hosts a WebSocket endpoint that the ElevenLabs API dials into: it sends
you user transcripts and you reply with text to speak.

Use `ElevenLabsClient` (a subclass of `ElevenlabsApiClient`) for this. Its `speechEngine()` returns
the generated client with `create`, `get`, `update` and `duplicate` returning a
`SpeechEngineResource`. The resource is still a `SpeechEngineResponse`, so all of its getters work,
and it carries the client's API key, which verifies that inbound connections come from ElevenLabs.

```java
import com.elevenlabs.api.ElevenLabsClient;
import com.elevenlabs.api.resources.speechengine.requests.CreateSpeechEngineRequest;
import com.elevenlabs.api.speechengine.SpeechEngineCallbacks;
import com.elevenlabs.api.speechengine.SpeechEngineResource;
import com.elevenlabs.api.types.SpeechEngineConfig;

ElevenLabsClient client = ElevenLabsClient.builder().build();

SpeechEngineResource engine = client
  .speechEngine()
  .create(
    CreateSpeechEngineRequest.builder()
      .speechEngine(SpeechEngineConfig.builder().wsUrl("wss://your-server.example.com/ws").build())
      .name("My engine")
      .build()
  );
// Or fetch an existing one: client.speechEngine().get("seng_123")

engine.serve(
  3001,
  "/ws",
  new SpeechEngineCallbacks()
    .onTranscript((transcript, signal, session) -> {
      String reply = myLlm.reply(transcript); // List<ConversationMessage>, oldest first
      session.sendResponse(reply);
    })
    .onError((error, session) -> error.printStackTrace())
);
```

`serve` blocks until the server is stopped; `start(port, path, callbacks)` returns a running
`SpeechEngineServer` instead. Each connection is checked during the WebSocket handshake: the
`X-ElevenLabs-Speech-Engine-Authorization` header must hold a JWT signed with your API key, or the
upgrade is refused. `disableAuth(true)` turns this off, which is only safe behind an IP allowlist.

The standalone server uses [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket), an
optional dependency of this SDK, so add it to your build:

```groovy
implementation 'org.java-websocket:Java-WebSocket:1.5.7'
```

### Responses and interruptions

Transcript handlers run on their own thread, so they can block on an LLM call. `sendResponse`
takes a `String`, or an `Iterable` or `Stream` of chunks for a streamed reply. A chunk is either
text or an OpenAI, Anthropic or Gemini stream event (as a Jackson `JsonNode`, a `Map`, or any object
Jackson can serialise), and the text is extracted from it.

When the user speaks again, a new transcript arrives and the previous turn's `AbortSignal` fires.
Use it to cancel the LLM call (`signal.onAbort(call::cancel)`) or check `signal.isAborted()`. A
streamed reply stops by itself once its turn is interrupted.

### Serving from an existing servlet container

To serve Speech Engine from Tomcat 10+, Jetty 11+, Undertow or Spring Boot 3, attach it to the
servlet context while the application starts. It registers a filter that answers `401` to
unverified requests before the upgrade, and a Jakarta WebSocket endpoint on the path:

```java
import com.elevenlabs.api.speechengine.SpeechEngineJakarta;
import org.springframework.boot.web.servlet.ServletContextInitializer;

@Bean
ServletContextInitializer speechEngine(ElevenLabsClient client) {
  return servletContext ->
    SpeechEngineJakarta.attach(servletContext, client.speechEngine(), "/ws", callbacks);
}
```

The container provides the Jakarta Servlet and WebSocket APIs. For any other transport, verify the
upgrade request with `engine.verifyRequest(headers)` and drive the connection with
`engine.createSession(send, close, callbacks)`, passing each inbound text frame to
`session.handleRawMessage(frame)` and calling `session.handleDisconnect()` when it closes.

`AsyncElevenlabsApiClient` is not wired: its `speechEngine()` methods return the plain
`CompletableFuture<SpeechEngineResponse>`. Wrap a response with
`new SpeechEngineResource(response, apiKey)` to serve it.

## Advanced

### Retries

The SDK is instrumented with automatic retries with exponential backoff. A request will be retried as
long as the request is deemed retryable and the number of retry attempts has not grown larger than the
configured retry limit (default: 2). Before backing off, the SDK honours the `Retry-After` header (in
seconds or as an HTTP date) and then `X-RateLimit-Reset` (a Unix timestamp in seconds).

A request is deemed retryable when any of the following HTTP status codes is returned:

- [408](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/408) (Timeout)
- [429](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429) (Too Many Requests)
- [5XX](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#server_error_responses) (Internal Server Error)

Connection failures and socket timeouts are retried as well.

Configure the limit and the backoff on the client, or the limit for a single request:

```java
import com.elevenlabs.api.core.RequestOptions;

ElevenlabsApiClient client = ElevenlabsApiClient.builder()
  .maxRetries(3)
  .initialRetryDelayMillis(500)
  .maxRetryDelayMillis(10_000)
  .build();

client.textToSpeech().convert(..., RequestOptions.builder().maxRetries(0).build());
```

### Timeouts

The connect, read and write timeout defaults to 240 seconds. Set it in seconds on the client, or per
request:

```java
// Client level
ElevenlabsApiClient client = ElevenlabsApiClient.builder().timeout(60).build();

// Request level
client.textToSpeech().convert(..., RequestOptions.builder().timeout(10).build());
```

`RequestOptions.builder().timeout(value, TimeUnit)` takes another unit.

### Custom Headers

Add headers to every request on the client, or to a single request. `RequestOptions` can add query
parameters too:

```java
// Client level
ElevenlabsApiClient client = ElevenlabsApiClient.builder()
  .addHeader("X-Custom-Header", "custom-value")
  .build();

// Request level
client
  .textToSpeech()
  .convert(
    ...,
    RequestOptions.builder()
      .addHeader("X-Request-Header", "request-value")
      .addQueryParameter("trace", "true")
      .build()
  );
```

### Custom HTTP Client

This SDK is built to work with any instance of `OkHttpClient`. By default, if no client is provided,
the SDK constructs one. You can pass your own instead:

```java
import okhttp3.OkHttpClient;

OkHttpClient httpClient = new OkHttpClient.Builder()
  .readTimeout(120, TimeUnit.SECONDS)
  .build();

ElevenlabsApiClient client = ElevenlabsApiClient.builder().httpClient(httpClient).build();
```

The SDK layers its own interceptors (authentication, retries, logging) on a copy of your client, so
your interceptors, proxy and connection settings are kept. A client you pass in keeps its own
timeouts (the client-level `timeout(...)` is not applied to it, while per-request timeouts still
are), and closing the SDK client does not shut it down.

### Access Raw Response Data

`withRawResponse()` returns a client whose methods wrap each result in an `ElevenlabsApiHttpResponse`,
with the parsed data and the response's status code and headers:

```java
import com.elevenlabs.api.core.ElevenlabsApiHttpResponse;

ElevenlabsApiHttpResponse<GetVoicesV2Response> response = client
  .voices()
  .withRawResponse()
  .search();

GetVoicesV2Response voices = response.getData();
int status = response.getMetadata().getStatusCode();
Map<String, List<String>> headers = response.getMetadata().getHeaders();
```

### Logging

Logging is off by default. Turn it on with a `LogConfig`. At `DEBUG` level the request headers are
logged, and the `xi-api-key` header is currently written in plain text, so don't enable it where logs
are shared or retained:

```java
import com.elevenlabs.api.core.LogConfig;
import com.elevenlabs.api.core.LogLevel;

ElevenlabsApiClient client = ElevenlabsApiClient.builder()
  .logging(LogConfig.builder().level(LogLevel.DEBUG).silent(false).build())
  .build();
```

### Async Client

`AsyncElevenlabsApiClient` has the same builder and resources, with every method returning a
`CompletableFuture`:

```java
import com.elevenlabs.api.AsyncElevenlabsApiClient;

AsyncElevenlabsApiClient client = AsyncElevenlabsApiClient.builder().build();

client
  .voices()
  .search()
  .thenAccept(response -> System.out.println(response.getTotalCount() + " voices"))
  .join();
```

### Closing the Client

Both clients implement `AutoCloseable`. `close()` disconnects any WebSocket clients still open
through it and shuts down the `OkHttpClient` the SDK created:

```java
try (ElevenlabsApiClient client = ElevenlabsApiClient.builder().build()) {
  // ...
}
```

## Languages Supported

Explore [all models & languages](https://elevenlabs.io/docs/models).

## Contributing

While we value open-source contributions to this SDK, this library is generated programmatically.
Additions made directly to this library would have to be moved over to our generation code,
otherwise they would be overwritten upon the next generated release. Feel free to open a PR as
a proof of concept, but know that we will not be able to merge it as-is. We suggest opening
an issue first to discuss with us!

On the other hand, contributions to the README are always very welcome!
