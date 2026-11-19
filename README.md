# Elevenlabs Java Library

[![fern shield](https://img.shields.io/badge/%F0%9F%8C%BF-Built%20with%20Fern-brightgreen)](https://buildwithfern.com?utm_source=github&utm_medium=github&utm_campaign=readme&utm_source=Elevenlabs%2FJava)
[![Maven Central](https://img.shields.io/maven-central/v/io.elevenlabs/elevenlabs-java)](https://central.sonatype.com/artifact/io.elevenlabs/elevenlabs-java)

The Elevenlabs Java library provides convenient access to the Elevenlabs APIs from Java.

## Table of Contents

- [Documentation](#documentation)
- [Installation](#installation)
- [Reference](#reference)
- [Usage](#usage)
- [Environments](#environments)
- [Base Url](#base-url)
- [Exception Handling](#exception-handling)
- [Websocket](#websocket)
- [Advanced](#advanced)
  - [Custom Client](#custom-client)
  - [Retries](#retries)
  - [Timeouts](#timeouts)
  - [Custom Headers](#custom-headers)
  - [Access Raw Response Data](#access-raw-response-data)
- [Contributing](#contributing)

## Documentation

API reference documentation is available [here](https://github.com/elevenlabs/elevenlabs-java).

## Installation

### Gradle

Add the dependency in your `build.gradle` file:

```groovy
dependencies {
  implementation 'io.elevenlabs:elevenlabs-java:1.0.0'
}
```

### Maven

Add the dependency in your `pom.xml` file:

```xml
<dependency>
  <groupId>io.elevenlabs</groupId>
  <artifactId>elevenlabs-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Reference

A full reference for this library is available [here](./reference.md).

## Usage

Instantiate and use the client with the following:

```java
package com.example.usage;

import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.resources.history.requests.DownloadHistoryRequest;
import java.util.Arrays;

public class Example {

  public static void main(String[] args) {
    ElevenlabsApiClient client = ElevenlabsApiClient.builder().build();

    client
      .history()
      .download(
        DownloadHistoryRequest.builder().historyItemIds(Arrays.asList("history_item_ids")).build()
      );
  }
}

```

## Environments

This SDK allows you to configure different environments for API requests.

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.Environment;

ElevenlabsApiClient client = ElevenlabsApiClient.builder().environment(Environment.DEFAULT).build();

```

## Base Url

You can set a custom base URL when constructing the client.

```java
import com.elevenlabs.api.ElevenlabsApiClient;

ElevenlabsApiClient client = ElevenlabsApiClient.builder().url("https://example.com").build();

```

## Exception Handling

When the API returns a non-success status code (4xx or 5xx response), an API exception will be thrown.

```java
import com.elevenlabs.api.core.ElevenlabsApiApiException;

try {
    client.history().download(...);
} catch (ElevenlabsApiApiException e) {
    // Do something with the API exception...
}
```

## Websocket

The SDK provides WebSocket support for real-time bidirectional communication. You can connect to a WebSocket,
register message handlers to receive server messages, send messages to the server, and close the connection when done.

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.resources.dubbing.websocket.DubbingWebSocketClient;

ElevenlabsApiClient client = ElevenlabsApiClient
    .builder()
    .build();

// Create the WebSocket client and connect
DubbingWebSocketClient ws = client.dubbing().dubbingWebSocket();
ws.live(DubbingConnectOptions.builder()...build()).join();

// Register message handlers to receive server messages
ws.onSubscribe(message -> {
    System.out.println("Received: " + message);
});

// Send messages to the server
ws.sendPublish(...);

// Close the connection when done
ws.disconnect();
```

## Advanced

### Custom Client

This SDK is built to work with any instance of `OkHttpClient`. By default, if no client is provided, the SDK will construct one.
However, you can pass your own client like so:

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import okhttp3.OkHttpClient;

OkHttpClient customClient = ...;

ElevenlabsApiClient client = ElevenlabsApiClient
    .builder()
    .httpClient(customClient)
    .build();
```

### Retries

The SDK is instrumented with automatic retries with exponential backoff. A request will be retried as long
as the request is deemed retryable and the number of retry attempts has not grown larger than the configured
retry limit (default: 2). Before defaulting to exponential backoff, the SDK will first attempt to respect
the `Retry-After` header (as either in seconds or as an HTTP date), and then the `X-RateLimit-Reset` header
(as a Unix timestamp in epoch seconds); failing both of those, it will fall back to exponential backoff.

Which status codes are retried depends on the `retry-status-codes` generator configuration:

**`legacy`** (current default): retries on

- [408](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/408) (Timeout)
- [429](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429) (Too Many Requests)
- [5XX](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#server_error_responses) (All server errors, including 500)

**`recommended`**: retries on

- [408](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/408) (Timeout)
- [429](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429) (Too Many Requests)
- [502](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/502) (Bad Gateway)
- [503](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/503) (Service Unavailable)
- [504](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/504) (Gateway Timeout)

Use the `maxRetries` client option to configure this behavior.

```java
import com.elevenlabs.api.ElevenlabsApiClient;

ElevenlabsApiClient client = ElevenlabsApiClient.builder().maxRetries(1).build();

```

### Timeouts

The SDK defaults to a 240 second timeout. You can configure this with a timeout option at the client or request level.

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.RequestOptions;

// Client level
ElevenlabsApiClient client = ElevenlabsApiClient
    .builder()
    .timeout(60)
    .build();

// Request level
client.history().download(
    ...,
    RequestOptions
        .builder()
        .timeout(60)
        .build()
);
```

### Custom Headers

The SDK allows you to add custom headers to requests. You can configure headers at the client level or at the request level.

```java
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.RequestOptions;

// Client level
ElevenlabsApiClient client = ElevenlabsApiClient
    .builder()
    .addHeader("X-Custom-Header", "custom-value")
    .addHeader("X-Request-Id", "abc-123")
    .build();

// Request level
client.history().download(
    ...,
    RequestOptions
        .builder()
        .addHeader("X-Request-Header", "request-value")
        .build()
);
```

### Access Raw Response Data

The SDK provides access to raw response data, including headers, through the `withRawResponse()` method.
The `withRawResponse()` method returns a raw client that wraps all responses with `getData()` and `getMetadata()` methods.
(A normal client's `response` is identical to a raw client's `response.getData()`.)

```java
ElevenlabsApiHttpResponse<GetSpeechHistoryResponse> response =
    client.history().withRawResponse().list();

response.getData();
response.getMetadata().getStatusCode();
response.getMetadata().getHeaders();
response.getRaw();
```

## Contributing

While we value open-source contributions to this SDK, this library is generated programmatically.
Additions made directly to this library would have to be moved over to our generation code,
otherwise they would be overwritten upon the next generated release. Feel free to open a PR as
a proof of concept, but know that we will not be able to merge it as-is. We suggest opening
an issue first to discuss with us!

On the other hand, contributions to the README are always very welcome!
