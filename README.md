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
