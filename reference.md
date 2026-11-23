# Reference

## History

<details><summary><code>client.history.list(requestParameters) -> GetSpeechHistoryResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of your generated audio (e.g. text to speech, speech to speech, Studio, dubbing). Music and SFX generations are not included and cannot currently be retrieved via the API.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.history().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListHistoryRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.history.get(historyItemId, requestParameters) -> SpeechHistoryItemResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves a history item.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.history().get(
    historyItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**historyItemId:** `String` — ID of the history item to be used. You can use the [Get generated items](/docs/api-reference/history/list) endpoint to retrieve a list of history items.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetHistoryRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.history.delete(historyItemId, requestParameters) -> DeleteHistoryItemResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a history item by its ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.history().delete(
    historyItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**historyItemId:** `String` — ID of the history item to be used. You can use the [Get generated items](/docs/api-reference/history/list) endpoint to retrieve a list of history items.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteHistoryRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.history.getAudio(historyItemId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the audio of an history item.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.history().getAudio(
    historyItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**historyItemId:** `String` — ID of the history item to be used. You can use the [Get generated items](/docs/api-reference/history/list) endpoint to retrieve a list of history items.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAudioHistoryRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.history.download(downloadHistoryRequest) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Download one or more history items. If one history item ID is provided, we will return a single audio file. If more than one history item IDs are provided, we will provide the history items packed into a .zip file.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.history().download(downloadHistoryRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**downloadHistoryRequest:** `DownloadHistoryRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## TextToSoundEffects

<details><summary><code>client.textToSoundEffects.convert(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Turn text into sound effects for your videos, voice-overs or video games using the most advanced sound effects models in the world.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToSoundEffects().convert(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `CreateSoundEffectRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## AudioIsolation

<details><summary><code>client.audioIsolation.convert(audioIsolationRequest) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes background noise from audio.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioIsolation().convert(audioIsolationRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**audioIsolationRequest:** `AudioIsolationRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioIsolation.list(requestParameters) -> GetAudioIsolationHistoryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of all your audio isolation generations.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioIsolation().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAudioIsolationRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioIsolation.delete(historyItemId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes a specific audio isolation history item and the associated media files.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioIsolation().delete(
    historyItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**historyItemId:** `String` — Identifier of the audio isolation history item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteAudioIsolationRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioIsolation.stream(audioIsolationStreamRequest) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes background noise from audio.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioIsolation().stream(audioIsolationStreamRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**audioIsolationStreamRequest:** `AudioIsolationStreamRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## AudioEffects

<details><summary><code>client.audioEffects.getCatalog() -> AudioEffectsCatalogResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Lists the filter presets, reverb environments, and background noises that `audio_effects` can reference in Text to Speech, Text to Dialogue, and agent configurations.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioEffects().getCatalog();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Samples

<details><summary><code>client.samples.delete(voiceId, sampleId, requestParameters) -> DeleteSampleResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes a sample by its ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.samples().delete(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — ID of the sample to be used. You can use the [Get voices](/docs/api-reference/voices/get) endpoint list all the available samples for a voice.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteSamplesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices

<details><summary><code>client.voices.get(voiceId, requestParameters) -> Voice</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns metadata about a specific voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().get(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetVoicesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.delete(voiceId, requestParameters) -> DeleteVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes a voice by its ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().delete(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteVoicesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.update(voiceId, editVoiceRequest) -> EditVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Edit a voice created by you.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().update(
    voiceId,
    editVoiceRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**editVoiceRequest:** `EditVoiceRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.search(requestParameters) -> GetVoicesV2Response</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets a list of all available voices for a user with search, filtering and pagination.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().search(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `SearchVoicesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.replicateToIsolatedEnvironment(voiceId, replicateVoiceToIsolatedEnvironmentRequestModel) -> ReplicateVoiceToIsolatedEnvironmentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replicates an Instant Voice Clone or Voice Design voice to a workspace in a different data residency. The target workspace must belong to the same consolidated billing group. The user must have VOICES_WRITE in the source workspace, and be an admin on the source voice. Human users (i.e. not service accounts) must also have VOICES_WRITE in the target workspace. This endpoint is available on the central environment only.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().replicateToIsolatedEnvironment(
    voiceId,
    replicateVoiceToIsolatedEnvironmentRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**replicateVoiceToIsolatedEnvironmentRequestModel:** `ReplicateVoiceToIsolatedEnvironmentRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.share(publicUserId, voiceId, bodyAddSharedVoiceV1VoicesAddPublicUserIdVoiceIdPost) -> AddVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a shared voice to your collection of Voices

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().share(
    publicUserId,
    voiceId,
    bodyAddSharedVoiceV1VoicesAddPublicUserIdVoiceIdPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**publicUserId:** `String` — Public user ID used to publicly identify ElevenLabs users.

</dd>
</dl>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyAddSharedVoiceV1VoicesAddPublicUserIdVoiceIdPost:** `BodyAddSharedVoiceV1VoicesAddPublicUserIdVoiceIdPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.findSimilarVoices(listSimilarVoicesRequest) -> GetLibraryVoicesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of shared voices similar to the provided audio sample. If neither similarity_threshold nor top_k is provided, we will apply default values.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().findSimilarVoices(listSimilarVoicesRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**listSimilarVoicesRequest:** `ListSimilarVoicesRequest` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.getShared(requestParameters) -> GetLibraryVoicesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves a list of shared voices.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().getShared(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetSharedVoicesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc

<details><summary><code>client.voices.pvc.create(createPvcVoiceRequest) -> AddVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new PVC voice with metadata but no samples

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().create(createPvcVoiceRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createPvcVoiceRequest:** `CreatePvcVoiceRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.update(voiceId, bodyEditPvcVoiceV1VoicesPvcVoiceIdPost) -> AddVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Edit PVC voice metadata

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().update(
    voiceId,
    bodyEditPvcVoiceV1VoicesPvcVoiceIdPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyEditPvcVoiceV1VoicesPvcVoiceIdPost:** `BodyEditPvcVoiceV1VoicesPvcVoiceIdPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.train(voiceId, bodyRunPvcTrainingV1VoicesPvcVoiceIdTrainPost) -> StartPvcVoiceTrainingResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start PVC training process for a voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().train(
    voiceId,
    bodyRunPvcTrainingV1VoicesPvcVoiceIdTrainPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyRunPvcTrainingV1VoicesPvcVoiceIdTrainPost:** `BodyRunPvcTrainingV1VoicesPvcVoiceIdTrainPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Samples

<details><summary><code>client.voices.pvc.samples.create(voiceId, bodyAddSamplesToPvcVoiceV1VoicesPvcVoiceIdSamplesPost) -> List&lt;VoiceSample&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add audio samples to a PVC voice

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().create(
    voiceId,
    bodyAddSamplesToPvcVoiceV1VoicesPvcVoiceIdSamplesPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyAddSamplesToPvcVoiceV1VoicesPvcVoiceIdSamplesPost:** `BodyAddSamplesToPvcVoiceV1VoicesPvcVoiceIdSamplesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.samples.update(voiceId, sampleId, bodyUpdatePvcVoiceSampleV1VoicesPvcVoiceIdSamplesSampleIdPost) -> AddVoiceResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a PVC voice sample - apply noise removal, select speaker, change trim times or file name.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().update(
    voiceId,
    sampleId,
    bodyUpdatePvcVoiceSampleV1VoicesPvcVoiceIdSamplesSampleIdPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**bodyUpdatePvcVoiceSampleV1VoicesPvcVoiceIdSamplesSampleIdPost:** `BodyUpdatePvcVoiceSampleV1VoicesPvcVoiceIdSamplesSampleIdPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.samples.delete(voiceId, sampleId, requestParameters) -> DeleteVoiceSampleResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a sample from a PVC voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().delete(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.voices.pvc.samples.requests.DeleteSamplesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Samples Audio

<details><summary><code>client.voices.pvc.samples.audio.get(voiceId, sampleId, requestParameters) -> VoiceSamplePreviewResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the first 30 seconds of voice sample audio with or without noise removal.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().audio().get(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Samples Speakers

<details><summary><code>client.voices.pvc.samples.speakers.get(voiceId, sampleId, requestParameters) -> SpeakerSeparationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the status of the speaker separation process and the list of detected speakers if complete.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().speakers().get(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSpeakersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.samples.speakers.separate(voiceId, sampleId, requestParameters) -> StartSpeakerSeparationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start speaker separation process for a sample

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().speakers().separate(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `SeparateSpeakersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Samples Speakers Audio

<details><summary><code>client.voices.pvc.samples.speakers.audio.get(voiceId, sampleId, speakerId, requestParameters) -> SpeakerAudioResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the separated audio for a specific speaker.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().speakers().audio().get(
    voiceId,
    sampleId,
    speakerId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**speakerId:** `String` — Speaker ID to be used, you can use GET https://api.elevenlabs.io/v1/voices/{voice_id}/samples/{sample_id}/speakers to list all the available speakers for a sample.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.voices.pvc.samples.speakers.audio.requests.GetAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Samples Waveform

<details><summary><code>client.voices.pvc.samples.waveform.get(voiceId, sampleId, requestParameters) -> VoiceSampleVisualWaveformResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the visual waveform of a voice sample.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().samples().waveform().get(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — Sample ID to be used

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetWaveformRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Verification

<details><summary><code>client.voices.pvc.verification.request(voiceId, bodyRequestManualVerificationV1VoicesPvcVoiceIdVerificationPost) -> RequestPvcManualVerificationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Request manual verification for a PVC voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().verification().request(
    voiceId,
    bodyRequestManualVerificationV1VoicesPvcVoiceIdVerificationPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyRequestManualVerificationV1VoicesPvcVoiceIdVerificationPost:** `BodyRequestManualVerificationV1VoicesPvcVoiceIdVerificationPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Pvc Verification Captcha

<details><summary><code>client.voices.pvc.verification.captcha.get(voiceId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get captcha for PVC voice verification.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().verification().captcha().get(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetCaptchaRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.pvc.verification.captcha.verify(voiceId, bodyVerifyPvcVoiceCaptchaV1VoicesPvcVoiceIdCaptchaPost) -> VerifyPvcVoiceCaptchaResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Submit captcha verification for PVC voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().pvc().verification().captcha().verify(
    voiceId,
    bodyVerifyPvcVoiceCaptchaV1VoicesPvcVoiceIdCaptchaPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**bodyVerifyPvcVoiceCaptchaV1VoicesPvcVoiceIdCaptchaPost:** `BodyVerifyPvcVoiceCaptchaV1VoicesPvcVoiceIdCaptchaPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Samples Audio

<details><summary><code>client.voices.samples.audio.get(voiceId, sampleId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the audio corresponding to a sample attached to a voice.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().samples().audio().get(
    voiceId,
    sampleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**sampleId:** `String` — ID of the sample to be used. You can use the [Get voices](/docs/api-reference/voices/get) endpoint list all the available samples for a voice.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.voices.samples.audio.requests.GetAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Collections

<details><summary><code>client.voices.collections.list() -> GetUserVoiceCollectionsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns all the voice collections the user has access to, sorted by title.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.create(bodyCreateVoiceCollectionV1VoicesCollectionsPost) -> UserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new voice collection. The caller becomes its admin.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().create(bodyCreateVoiceCollectionV1VoicesCollectionsPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateVoiceCollectionV1VoicesCollectionsPost:** `BodyCreateVoiceCollectionV1VoicesCollectionsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.get(collectionId, requestParameters) -> UserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a voice collection the user has access to.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().get(
    collectionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**collectionId:** `String` — Collection ID.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetCollectionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.update(collectionId, bodyUpdateVoiceCollectionV1VoicesCollectionsCollectionIdPatch) -> UserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates the title and icon of a voice collection. Fields that are omitted are left unchanged. Requires editor access to the collection.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().update(
    collectionId,
    bodyUpdateVoiceCollectionV1VoicesCollectionsCollectionIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**collectionId:** `String` — Collection ID.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateVoiceCollectionV1VoicesCollectionsCollectionIdPatch:** `BodyUpdateVoiceCollectionV1VoicesCollectionsCollectionIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.delete(collectionId, requestParameters) -> DeleteUserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes a voice collection. The voices in it are not deleted. Requires admin access to the collection.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().delete(
    collectionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**collectionId:** `String` — Collection ID.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteCollectionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.addVoice(collectionId, voiceId, requestParameters) -> AddVoiceToUserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Adds a voice to a voice collection. Adding a voice that is already in the collection succeeds without changes. Requires editor access to the collection.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().addVoice(
    collectionId,
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**collectionId:** `String` — Collection ID.

</dd>
</dl>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `AddVoiceCollectionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.collections.removeVoice(collectionId, voiceId, requestParameters) -> RemoveVoiceFromUserVoiceCollectionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes a voice from a voice collection. The voice itself is not deleted. Requires editor access to the collection.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().collections().removeVoice(
    collectionId,
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**collectionId:** `String` — Collection ID.

</dd>
</dl>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RemoveVoiceCollectionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Settings

<details><summary><code>client.voices.settings.getDefault() -> VoiceSettings</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets the default settings for voices. "similarity_boost" corresponds to"Clarity + Similarity Enhancement" in the web app and "stability" corresponds to "Stability" slider in the web app.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().settings().getDefault();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.settings.get(voiceId, requestParameters) -> VoiceSettings</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the settings for a specific voice. "similarity_boost" corresponds to"Clarity + Similarity Enhancement" in the web app and "stability" corresponds to "Stability" slider in the web app.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().settings().get(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSettingsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.voices.settings.update(voiceId, voiceSettings) -> EditVoiceSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Edit your settings for a specific voice. "similarity_boost" corresponds to "Clarity + Similarity Enhancement" in the web app and "stability" corresponds to "Stability" slider in the web app.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().settings().update(
    voiceId,
    voiceSettings
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. You can use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**voiceSettings:** `VoiceSettings` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Accents

<details><summary><code>client.voices.accents.get(requestParameters) -> GetVoiceAccentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets the list of available accents in the shared voice library.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().accents().get(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetAccentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Voices Ivc

<details><summary><code>client.voices.ivc.create(addVoiceRequest) -> AddVoiceIvcResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a voice clone and add it to your Voices

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.voices().ivc().create(addVoiceRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**addVoiceRequest:** `AddVoiceRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing

<details><summary><code>client.dubbing.list(requestParameters) -> DubbingMetadataPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** List the dubs you have access to.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListDubbingRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.create(dubRequest) -> DoDubbingResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** Dubs a provided audio or video file into given language.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().create(dubRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dubRequest:** `DubRequest` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.get(dubbingId, requestParameters) -> DubbingMetadataResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** Returns metadata about a dubbing project, including whether it's still in progress or not

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().get(
    dubbingId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dubbingId:** `String` — ID of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDubbingRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.delete(dubbingId, requestParameters) -> DeleteDubbingResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** Deletes a dubbing project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().delete(
    dubbingId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dubbingId:** `String` — ID of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteDubbingRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Audio

<details><summary><code>client.dubbing.audio.get(dubbingId, languageCode, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** Returns dub as a streamed MP3 or MP4 file. If this dub has been edited using Dubbing Studio you need to use the resource render endpoint as this endpoint only returns the original automatic dub result.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().audio().get(
    dubbingId,
    languageCode,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dubbingId:** `String` — ID of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageCode:** `String` — ID of the language.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.dubbing.audio.requests.GetAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Project

<details><summary><code>client.dubbing.project.list(requestParameters) -> DubbingProjectListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the dubbing projects in your workspace that you can access, newest first, cursor-paginated. Listed projects carry no `language_ids`; fetch a project, or list its language targets, to see them.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListProjectRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.create(bodyCreateDubbingProjectV1DubbingProjectPost) -> DubbingProjectResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a dubbing project from an uploaded file (`file`) or a source URL (`source_url`). Returns as soon as the project record exists, before the source has been fetched: the project starts `queued` and reaches `ready` once its source has been transcribed. Add a language target to it for each language you want, or pass `target_language` to queue the first one here. Creating a project incurs a minimum charge of one language: you are charged for at least one language's dubbing when the project is created, before any output exists. This charge prepays the first language target — the first target you add, or the one queued via `target_language`, consumes it — and each additional language is charged separately. Preparation can take minutes on a long source, so we recommend passing `webhook_ids` to be notified when the project turns `ready` or `failed`, rather than polling for it.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().create(bodyCreateDubbingProjectV1DubbingProjectPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateDubbingProjectV1DubbingProjectPost:** `BodyCreateDubbingProjectV1DubbingProjectPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.get(projectId, requestParameters) -> DubbingProjectResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Full project detail, including the IDs of every language target under it. To follow a project to `ready`, we recommend a `webhook_ids` subscription rather than polling this endpoint.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().get(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project to fetch.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetProjectRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.delete(projectId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a project, every language target under it, and their stored media and outputs. This cannot be undone, and a dub already running is still billed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().delete(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project to delete.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteProjectRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Project Language

<details><summary><code>client.dubbing.project.language.list(projectId, requestParameters) -> DubbingLanguageListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List a project's language targets, cursor-paginated, each with signed output URLs once it has produced an output.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().list(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the parent dubbing project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListLanguageRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.create(projectId, bodyCreateDubbingLanguageTargetV1DubbingProjectProjectIdLanguagePost) -> DubbingLanguageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a language to dub a project into, and queue the dub. This is the call that produces dubbed audio, and it is billed per generation. The target is created `queued` and starts as soon as the project is `ready`, so it can be added at any point after the project is created. It inherits the project's dubbing model and cannot pick another. A project created with `webhook_ids` sends a `dubbing_language_completed` event carrying the output download URLs, so we recommend subscribing rather than polling this target to completion.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().create(
    projectId,
    bodyCreateDubbingLanguageTargetV1DubbingProjectProjectIdLanguagePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the parent dubbing project.

</dd>
</dl>

<dl>
<dd>

**bodyCreateDubbingLanguageTargetV1DubbingProjectProjectIdLanguagePost:** `BodyCreateDubbingLanguageTargetV1DubbingProjectProjectIdLanguagePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.get(projectId, languageId, requestParameters) -> DubbingLanguageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Full language-target detail. Once the target reports `completed`, `outputs` carries the signed download URLs. To learn when that happens, we recommend the project's `webhook_ids` subscription rather than polling this endpoint; fetch here when a delivered URL has expired, or to reconcile after an edit.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().get(
    projectId,
    languageId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the parent dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target to fetch.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetLanguageRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.delete(projectId, languageId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a language target and its outputs, leaving the project and its other languages intact. This cannot be undone, and a dub already running is still billed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().delete(
    projectId,
    languageId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the parent dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target to delete.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteLanguageRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Project Language Transcript

<details><summary><code>client.dubbing.project.language.transcript.get(projectId, languageId, requestParameters) -> DubbingTargetTranscriptResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

A language target's transcript: source segments with their translations. Available once the target has produced an output. Returns a conflict while the target is still on its first dub, since it has no translations to return yet.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().transcript().get(
    projectId,
    languageId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTranscriptRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.transcript.updateSegment(projectId, languageId, segmentId, dubbingTargetSegmentUpdateRequest) -> DubbingTargetSegmentUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Edit a segment's translation for a language target. Omitted fields are left unchanged; an explicit null clears the field. Bumps the target's `revision` and marks it `stale` if it had already completed. The source transcript and the project's other languages are untouched, and no audio changes until you regenerate the target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().transcript().updateSegment(
    projectId,
    languageId,
    segmentId,
    dubbingTargetSegmentUpdateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target.

</dd>
</dl>

<dl>
<dd>

**segmentId:** `String` — Identifier of the segment to edit.

</dd>
</dl>

<dl>
<dd>

**dubbingTargetSegmentUpdateRequest:** `DubbingTargetSegmentUpdateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.transcript.updateSegments(projectId, languageId, dubbingBulkTargetSegmentUpdateRequest) -> DubbingBulkTargetSegmentUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Edit several segments' translations for a language target in one atomic request: every edit applies or none does. Bumps the target's `revision` and marks it `stale` if it had already completed. The source transcript and the project's other languages are untouched, and no audio changes until you regenerate the target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().transcript().updateSegments(
    projectId,
    languageId,
    dubbingBulkTargetSegmentUpdateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target.

</dd>
</dl>

<dl>
<dd>

**dubbingBulkTargetSegmentUpdateRequest:** `DubbingBulkTargetSegmentUpdateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.language.transcript.regenerate(projectId, languageId, requestParameters) -> DubbingRegenerateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Re-dub a target from its edited transcript, re-synthesizing only the edited regions (charged like a generation, less the free-regeneration allowance). Accepted asynchronously: the target returns to `processing` and sends a `dubbing_language_completed` event to the project's `webhook_ids` when the re-dub lands, carrying the new output URLs. Returns a conflict when the target has no edits to apply — nothing is dispatched and nothing is charged.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().language().transcript().regenerate(
    projectId,
    languageId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageId:** `String` — Identifier of the language target.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RegenerateTranscriptRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Project Transcript

<details><summary><code>client.dubbing.project.transcript.get(projectId, requestParameters) -> DubbingSourceTranscriptResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

The project's source transcript, as editable segments. Available once the project is `ready`.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().transcript().get(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.dubbing.project.transcript.requests.GetTranscriptRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.transcript.updateSegment(projectId, segmentId, dubbingSegmentUpdateRequest) -> DubbingSourceSegmentUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Edit a source segment's text, speaker, or timing. Omitted fields are left unchanged. Bumps the project's `revision`, discards the affected translations in every language target, and marks any target that had already completed `stale`. No audio changes until you regenerate a target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().transcript().updateSegment(
    projectId,
    segmentId,
    dubbingSegmentUpdateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**segmentId:** `String` — Identifier of the segment to edit.

</dd>
</dl>

<dl>
<dd>

**dubbingSegmentUpdateRequest:** `DubbingSegmentUpdateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.transcript.deleteSegment(projectId, segmentId, requestParameters) -> DubbingTranscriptRevisionResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Remove a source segment from the transcript so it is no longer dubbed. Bumps the project's `revision`, discards the affected translations in every language target, and marks any target that had already completed `stale`. No audio changes until you regenerate a target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().transcript().deleteSegment(
    projectId,
    segmentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**segmentId:** `String` — Identifier of the segment to remove.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteSegmentTranscriptRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.transcript.updateSegments(projectId, dubbingBulkSegmentUpdateRequest) -> DubbingBulkSourceSegmentUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Edit several source segments' text, speaker, or timing in one atomic request: every edit applies or none does. Bumps the project's `revision`, discards the affected translations in every language target, and marks any target that had already completed `stale`. No audio changes until you regenerate a target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().transcript().updateSegments(
    projectId,
    dubbingBulkSegmentUpdateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**dubbingBulkSegmentUpdateRequest:** `DubbingBulkSegmentUpdateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.dubbing.project.transcript.createSegment(projectId, dubbingSegmentCreateRequest) -> DubbingSourceSegmentUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Enterprise only. Add a new source segment to the transcript. Its span must lie within the source media, last between 0.1 and 25 seconds, and not overlap another segment by the same speaker. Bumps the project's `revision`, discards the affected translations in every language target, and marks any target that had already completed `stale`. No audio changes until you regenerate a target.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().project().transcript().createSegment(
    projectId,
    dubbingSegmentCreateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — Identifier of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**dubbingSegmentCreateRequest:** `DubbingSegmentCreateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Dubbing Transcripts

<details><summary><code>client.dubbing.transcripts.get(dubbingId, languageCode, formatType, requestParameters) -> DubbingTranscriptsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Deprecated.** Fetch the transcript for one of the languages in a dub.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.dubbing().transcripts().get(
    dubbingId,
    languageCode,
    formatType,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dubbingId:** `String` — ID of the dubbing project.

</dd>
</dl>

<dl>
<dd>

**languageCode:** `String` — ISO-693 language code to retrieve the transcript for. Use 'source' to fetch the transcript of the original media.

</dd>
</dl>

<dl>
<dd>

**formatType:** `GetTranscriptsRequestFormatType` — Format to return transcript in. For subtitles use either 'srt' or 'webvtt', and for a full transcript use 'json'. The 'json' format is not yet supported for Dubbing Studio.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTranscriptsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents

<details><summary><code>client.agents.create(requestParameters) -> CreateAgentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create an agent from a config object

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyCreateAgentV1ConvaiAgentsCreatePost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.get(agentId, requestParameters) -> GetAgentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve config for an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().get(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.update(agentId, requestParameters) -> GetAgentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Patches an Agent settings

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().update(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `UpdateAgentRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.delete(agentId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().delete(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.list(requestParameters) -> GetAgentsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of your agents and their metadata.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.duplicate(agentId, bodyDuplicateAgentV1ConvaiAgentsAgentIdDuplicatePost) -> CreateAgentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new agent by duplicating an existing one. The new agent gets a copy of every unarchived branch of the source agent, each with the same name and description and starting from the latest version on that branch. The live traffic split is copied onto the new branches, with any share held by a branch that was not copied moved to main. Version history, drafts, archived branches, past traffic splits and experiments are not copied.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().duplicate(
    agentId,
    bodyDuplicateAgentV1ConvaiAgentsAgentIdDuplicatePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyDuplicateAgentV1ConvaiAgentsAgentIdDuplicatePost:** `BodyDuplicateAgentV1ConvaiAgentsAgentIdDuplicatePost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.runTests(agentId, runAgentTestsRequestModel) -> GetTestSuiteInvocationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Run selected tests on the agent with provided configuration. If the agent configuration is provided, it will be used to override default agent configuration.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().runTests(
    agentId,
    runAgentTestsRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**runAgentTestsRequestModel:** `RunAgentTestsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.ragIndexOverview() -> RagIndexOverviewResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Provides total size and other information of RAG indexes used by knowledgebase documents

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().ragIndexOverview();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.getDocumentRagIndexes(documentationId, requestParameters) -> RagDocumentIndexesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Provides information about all RAG indexes of the specified knowledgebase document.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().getDocumentRagIndexes(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDocumentRagIndexesAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.deleteDocumentRagIndex(documentationId, ragIndexId, requestParameters) -> RagDocumentIndexResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete RAG index for the knowledgebase document.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().deleteDocumentRagIndex(
    documentationId,
    ragIndexId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**ragIndexId:** `String` — The id of RAG index of document from the knowledge base.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteDocumentRagIndexAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations

<details><summary><code>client.agents.conversations.getSignedUrl(requestParameters) -> ConversationSignedUrlResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a signed url to start a conversation with an agent with an agent that requires authorization

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().getSignedUrl(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetSignedUrlConversationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.getWebrtcToken(requestParameters) -> TokenResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a WebRTC session token for real-time communication.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().getWebrtcToken(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetWebrtcTokenConversationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.list(requestParameters) -> GetConversationsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all conversations of agents that user owns. With option to restrict to a specific agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.create(createAgentConversationRequest) -> CreateAgentConversationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a text conversation with an agent. This does not run the agent, and the agent sends no first message. This endpoint is in alpha and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().create(createAgentConversationRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createAgentConversationRequest:** `CreateAgentConversationRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.resolve(requestParameters) -> GetConversationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Resolve a conversation URL (a Slack message URL or a Zendesk ticket URL) to the deterministic conversation ID for the given agent, then confirm the conversation exists.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().resolve(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ResolveConversationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.get(conversationId, requestParameters) -> GetConversationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the details of a particular conversation

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().get(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.delete(conversationId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a particular conversation

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().delete(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.getSummary(conversationId, requestParameters) -> GetConversationSummaryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a lightweight summary of a conversation: its title, the generated transcript summary, whether the call was successful, and — only when the conversation is short — the plain chat messages. Tool calls, tool results, and contextual updates are omitted so the response stays small. Use this instead of the full conversation endpoint when you only need the gist (e.g. an agent reading many conversations); use GET /v1/convai/conversations/{conversation_id} when you need the full transcript with tool calls and contextual updates.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().getSummary(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSummaryConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.getSipMessages(conversationId, requestParameters) -> GetSipLogMessagesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get SIP messages associated with a conversation's phone call

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().getSipMessages(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSipMessagesConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.sendMessage(conversationId, sendConversationMessageRequest) -> ConversationTurn</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Send input to a conversation, and wait until the agent finishes or waits on client tool results. With `background` set to true, the endpoint returns 202 when the server admits the turn. Then stream the turn's events with `Last-Event-ID` set to the `stream_cursor`. This endpoint is in alpha and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().sendMessage(
    conversationId,
    sendConversationMessageRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The Conversation ID

</dd>
</dl>

<dl>
<dd>

**sendConversationMessageRequest:** `SendConversationMessageRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.sendMessageStream(conversationId, sendConversationMessageStreamRequest) -> SseStream&lt;StreamEnvelope&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Send input to a conversation, and stream the events of the request as server-sent events. The last event is `stream_ended`, which has the outcome of the turn. This endpoint is in alpha and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().sendMessageStream(
    conversationId,
    sendConversationMessageStreamRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The Conversation ID

</dd>
</dl>

<dl>
<dd>

**sendConversationMessageStreamRequest:** `SendConversationMessageStreamRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.getTurn(conversationId, turnId, requestParameters) -> ConversationTurn</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the status and output of a turn. The status comes from the stored transcript, so a turn that failed after the agent started can show as completed. Only the `stream_ended` event has the real outcome. This endpoint is in alpha and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().getTurn(
    conversationId,
    turnId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The Conversation ID

</dd>
</dl>

<dl>
<dd>

**turnId:** `String` — The Turn ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTurnConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.streamTurnEvents(conversationId, turnId, requestParameters) -> SseStream&lt;StreamEnvelope&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream the events of a turn as server-sent events. Send `Last-Event-ID` to resume after an event. The server keeps events for about an hour. This endpoint is in alpha and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().streamTurnEvents(
    conversationId,
    turnId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The Conversation ID

</dd>
</dl>

<dl>
<dd>

**turnId:** `String` — The Turn ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StreamTurnEventsConversationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Audio

<details><summary><code>client.agents.conversations.audio.get(conversationId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the audio recording of a particular conversation

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().audio().get(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.agents.conversations.audio.requests.GetAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Files

<details><summary><code>client.agents.conversations.files.get(conversationId, fileId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Download a file the user attached to a conversation, including while the conversation is still active.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().files().get(
    conversationId,
    fileId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**fileId:** `String` — The file ID returned when it was uploaded.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetFilesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.files.delete(fileId, conversationId, requestParameters) -> ConvAiFileUploadResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a file upload from a conversation. Only possible if the file hasn't already been used in the conversation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().files().delete(
    fileId,
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**fileId:** `String`

</dd>
</dl>

<dl>
<dd>

**conversationId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteFilesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.files.create(conversationId, bodyUploadFileV1ConvaiConversationsConversationIdFilesPost) -> ConvAiFileUploadResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Upload an image, PDF, or text file for a conversation. Returns a unique file ID that can be used to reference the file in the conversation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().files().create(
    conversationId,
    bodyUploadFileV1ConvaiConversationsConversationIdFilesPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String`

</dd>
</dl>

<dl>
<dd>

**bodyUploadFileV1ConvaiConversationsConversationIdFilesPost:** `BodyUploadFileV1ConvaiConversationsConversationIdFilesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Feedback

<details><summary><code>client.agents.conversations.feedback.create(conversationId, conversationFeedbackRequestModel) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Send the feedback for the given conversation

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().feedback().create(
    conversationId,
    conversationFeedbackRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of the conversation you're taking the action on.

</dd>
</dl>

<dl>
<dd>

**conversationFeedbackRequestModel:** `ConversationFeedbackRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Messages

<details><summary><code>client.agents.conversations.messages.textSearch(requestParameters) -> MessagesSearchResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Search through conversation transcript messages by full-text and fuzzy search

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().messages().textSearch(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `TextSearchMessagesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.messages.search(requestParameters) -> MessagesSearchResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Search conversation transcripts by semantic similarity to surface relevant messages based on meaning and intent, rather than exact keyword matches

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().messages().search(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `SearchMessagesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Tags

<details><summary><code>client.agents.conversations.tags.assign(conversationId, assignConversationTagsRequestModel)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Assign one or more conversation tags to a conversation. Tags that are already assigned are ignored. Tags must belong to the same workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().assign(
    conversationId,
    assignConversationTagsRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String`

</dd>
</dl>

<dl>
<dd>

**assignConversationTagsRequestModel:** `AssignConversationTagsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.unassign(conversationId, tagId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a single conversation tag from a conversation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().unassign(
    conversationId,
    tagId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String`

</dd>
</dl>

<dl>
<dd>

**tagId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `UnassignTagsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.list(requestParameters) -> GetConversationTagsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List conversation tags for the workspace, ordered by most recently created first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListTagsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.create(createConversationTagRequestModel) -> ConversationTagResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new conversation tag for the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().create(createConversationTagRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createConversationTagRequestModel:** `CreateConversationTagRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.get(tagId, requestParameters) -> ConversationTagResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a conversation tag by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().get(
    tagId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**tagId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTagsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.update(tagId, patchConversationTagRequestModel) -> ConversationTagResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a conversation tag's title and/or description. Restricted to the tag owner or a workspace admin.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().update(
    tagId,
    patchConversationTagRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**tagId:** `String`

</dd>
</dl>

<dl>
<dd>

**patchConversationTagRequestModel:** `PatchConversationTagRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.tags.delete(tagId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a conversation tag. Restricted to the tag owner or a workspace admin.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().tags().delete(
    tagId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**tagId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteTagsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Topics

<details><summary><code>client.agents.conversations.topics.get(agentId, requestParameters) -> GetAgentTopicsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the latest topic discovery run results for a given agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().topics().get(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the agent

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTopicsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Conversations Analysis

<details><summary><code>client.agents.conversations.analysis.run(conversationId, requestParameters) -> GetConversationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Run the analysis for a conversation using the agent's current evaluation criteria and data collection settings.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().analysis().run(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — ID of the conversation

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RunAnalysisRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.conversations.analysis.runEvaluation(conversationId, runConversationEvaluationsRequest) -> GetConversationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Rerun a specific evaluation for a conversation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().conversations().analysis().runEvaluation(
    conversationId,
    runConversationEvaluationsRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — ID of the conversation

</dd>
</dl>

<dl>
<dd>

**runConversationEvaluationsRequest:** `RunConversationEvaluationsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Settings

<details><summary><code>client.agents.settings.get() -> GetConvAiSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve Convai settings for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().settings().get();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.settings.update(patchConvAiSettingsRequest) -> GetConvAiSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update Convai settings for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().settings().update(patchConvAiSettingsRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**patchConvAiSettingsRequest:** `PatchConvAiSettingsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Dashboard Settings

<details><summary><code>client.agents.dashboard.settings.get() -> GetConvAiDashboardSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve Convai dashboard settings for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().dashboard().settings().get();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.dashboard.settings.update(patchConvAiDashboardSettingsRequest) -> GetConvAiDashboardSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update Convai dashboard settings for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().dashboard().settings().update(patchConvAiDashboardSettingsRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**patchConvAiDashboardSettingsRequest:** `PatchConvAiDashboardSettingsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Twilio

<details><summary><code>client.agents.twilio.outboundCall(bodyHandleAnOutboundCallViaTwilioV1ConvaiTwilioOutboundCallPost) -> TwilioOutboundCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Handle an outbound call via Twilio

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().twilio().outboundCall(bodyHandleAnOutboundCallViaTwilioV1ConvaiTwilioOutboundCallPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyHandleAnOutboundCallViaTwilioV1ConvaiTwilioOutboundCallPost:** `BodyHandleAnOutboundCallViaTwilioV1ConvaiTwilioOutboundCallPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.twilio.registerCall(bodyRegisterATwilioCallAndReturnTwiMlV1ConvaiTwilioRegisterCallPost) -> String</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Register a Twilio call and return TwiML to connect the call

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().twilio().registerCall(bodyRegisterATwilioCallAndReturnTwiMlV1ConvaiTwilioRegisterCallPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyRegisterATwilioCallAndReturnTwiMlV1ConvaiTwilioRegisterCallPost:** `BodyRegisterATwilioCallAndReturnTwiMlV1ConvaiTwilioRegisterCallPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Exotel

<details><summary><code>client.agents.exotel.outboundCall(bodyHandleAnOutboundCallViaExotelV1ConvaiExotelOutboundCallPost) -> ExotelOutboundCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Handle an outbound call via Exotel Connect API

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().exotel().outboundCall(bodyHandleAnOutboundCallViaExotelV1ConvaiExotelOutboundCallPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyHandleAnOutboundCallViaExotelV1ConvaiExotelOutboundCallPost:** `BodyHandleAnOutboundCallViaExotelV1ConvaiExotelOutboundCallPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Whatsapp

<details><summary><code>client.agents.whatsapp.outboundCall(bodyMakeAnOutboundCallViaWhatsAppV1ConvaiWhatsappOutboundCallPost) -> WhatsAppOutboundCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Make an outbound call via WhatsApp

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsapp().outboundCall(bodyMakeAnOutboundCallViaWhatsAppV1ConvaiWhatsappOutboundCallPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyMakeAnOutboundCallViaWhatsAppV1ConvaiWhatsappOutboundCallPost:** `BodyMakeAnOutboundCallViaWhatsAppV1ConvaiWhatsappOutboundCallPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.whatsapp.outboundMessage(bodySendAnOutboundMessageViaWhatsAppV1ConvaiWhatsappOutboundMessagePost) -> WhatsAppOutboundMessageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Send an outbound message via WhatsApp

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsapp().outboundMessage(bodySendAnOutboundMessageViaWhatsAppV1ConvaiWhatsappOutboundMessagePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodySendAnOutboundMessageViaWhatsAppV1ConvaiWhatsappOutboundMessagePost:** `BodySendAnOutboundMessageViaWhatsAppV1ConvaiWhatsappOutboundMessagePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Summaries

<details><summary><code>client.agents.summaries.get(requestParameters) -> Map&lt;String, GetSummariesResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns summaries for the specified agents.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().summaries().get(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetSummariesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase

<details><summary><code>client.agents.knowledgeBase.size(agentId, requestParameters) -> GetAgentKnowledgebaseSizeResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the number of pages in the agent's knowledge base.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().size(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `SizeKnowledgeBaseRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.list(requestParameters) -> GetKnowledgeBaseListResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of available knowledge base documents

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListKnowledgeBaseRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.getOrCreateRagIndexes(bodyComputeRagIndexesInBatchV1ConvaiKnowledgeBaseRagIndexPost) -> Map&lt;String, GetOrCreateRagIndexesKnowledgeBaseResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves and/or creates RAG indexes for multiple knowledge base documents in a single request. Maximum 100 items per request.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().getOrCreateRagIndexes(bodyComputeRagIndexesInBatchV1ConvaiKnowledgeBaseRagIndexPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyComputeRagIndexesInBatchV1ConvaiKnowledgeBaseRagIndexPost:** `BodyComputeRagIndexesInBatchV1ConvaiKnowledgeBaseRagIndexPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.search(requestParameters) -> KnowledgeBaseContentSearchResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Fuzzy text search over knowledge base document content

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().search(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `SearchKnowledgeBaseRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase Documents

<details><summary><code>client.agents.knowledgeBase.documents.createFromUrl(bodyCreateUrlDocumentV1ConvaiKnowledgeBaseUrlPost) -> AddKnowledgeBaseResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a knowledge base document generated by scraping the given webpage.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().createFromUrl(bodyCreateUrlDocumentV1ConvaiKnowledgeBaseUrlPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateUrlDocumentV1ConvaiKnowledgeBaseUrlPost:** `BodyCreateUrlDocumentV1ConvaiKnowledgeBaseUrlPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.createFromFile(bodyCreateFileDocumentV1ConvaiKnowledgeBaseFilePost) -> AddKnowledgeBaseResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a knowledge base document generated form the uploaded file.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().createFromFile(bodyCreateFileDocumentV1ConvaiKnowledgeBaseFilePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateFileDocumentV1ConvaiKnowledgeBaseFilePost:** `BodyCreateFileDocumentV1ConvaiKnowledgeBaseFilePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.createFromText(bodyCreateTextDocumentV1ConvaiKnowledgeBaseTextPost) -> AddKnowledgeBaseResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a knowledge base document containing the provided text.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().createFromText(bodyCreateTextDocumentV1ConvaiKnowledgeBaseTextPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateTextDocumentV1ConvaiKnowledgeBaseTextPost:** `BodyCreateTextDocumentV1ConvaiKnowledgeBaseTextPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.createFolder(bodyCreateFolderV1ConvaiKnowledgeBaseFolderPost) -> AddKnowledgeBaseResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a folder used for grouping documents together.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().createFolder(bodyCreateFolderV1ConvaiKnowledgeBaseFolderPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateFolderV1ConvaiKnowledgeBaseFolderPost:** `BodyCreateFolderV1ConvaiKnowledgeBaseFolderPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.get(documentationId, requestParameters) -> GetDocumentsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get details about a specific documentation making up the agent's knowledge base

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().get(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.update(documentationId, bodyUpdateDocumentV1ConvaiKnowledgeBaseDocumentationIdPatch) -> UpdateDocumentsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the name, content and/or auto-sync settings of a document.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().update(
    documentationId,
    bodyUpdateDocumentV1ConvaiKnowledgeBaseDocumentationIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateDocumentV1ConvaiKnowledgeBaseDocumentationIdPatch:** `BodyUpdateDocumentV1ConvaiKnowledgeBaseDocumentationIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.delete(documentationId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a document or folder from the knowledge base.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().delete(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.getChunkingOverride(documentationId, requestParameters) -> GetDocumentChunkingOverrideResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the stored and effective RAG chunking overrides for a document or folder.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().getChunkingOverride(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetChunkingOverrideDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.updateChunkingOverride(documentationId, updateDocumentChunkingOverrideRequestModel) -> UpdateDocumentChunkingOverrideResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Set or clear the RAG chunking override for a document or folder.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().updateChunkingOverride(
    documentationId,
    updateDocumentChunkingOverrideRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**updateDocumentChunkingOverrideRequestModel:** `UpdateDocumentChunkingOverrideRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.getAgents(documentationId, requestParameters) -> GetKnowledgeBaseDependentAgentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of agents depending on this knowledge base document

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().getAgents(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAgentsDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.getBulkAgents(requestParameters) -> GetKnowledgeBaseDependentAgentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of agents depending on any of the given knowledge base documents.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().getBulkAgents(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyGetDependentAgentsForMultipleDocumentsV1ConvaiKnowledgeBaseDependentAgentsPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.getContent(documentationId, requestParameters) -> String</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the entire content of a document from the knowledge base

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().getContent(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetContentDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.getSourceFileUrl(documentationId, requestParameters) -> KnowledgeBaseSourceFileUrlResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a signed URL to download the original source file of a file-type document from the knowledge base

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().getSourceFileUrl(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSourceFileUrlDocumentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.move(documentId, bodyMoveEntityToFolderV1ConvaiKnowledgeBaseDocumentIdMovePost)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Moves the entity from one folder to another.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().move(
    documentId,
    bodyMoveEntityToFolderV1ConvaiKnowledgeBaseDocumentIdMovePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**bodyMoveEntityToFolderV1ConvaiKnowledgeBaseDocumentIdMovePost:** `BodyMoveEntityToFolderV1ConvaiKnowledgeBaseDocumentIdMovePost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.bulkMove(bodyBulkMoveEntitiesToFolderV1ConvaiKnowledgeBaseBulkMovePost)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Moves multiple entities from one folder to another.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().bulkMove(bodyBulkMoveEntitiesToFolderV1ConvaiKnowledgeBaseBulkMovePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyBulkMoveEntitiesToFolderV1ConvaiKnowledgeBaseBulkMovePost:** `BodyBulkMoveEntitiesToFolderV1ConvaiKnowledgeBaseBulkMovePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.documents.bulkDelete(bodyBulkDeleteKnowledgeBaseDocumentsV1ConvaiKnowledgeBaseBulkDeletePost) -> Map&lt;String, BulkDeleteDocumentsResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete multiple documents or folders from the knowledge base. Each id succeeds or fails independently.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().bulkDelete(bodyBulkDeleteKnowledgeBaseDocumentsV1ConvaiKnowledgeBaseBulkDeletePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyBulkDeleteKnowledgeBaseDocumentsV1ConvaiKnowledgeBaseBulkDeletePost:** `BodyBulkDeleteKnowledgeBaseDocumentsV1ConvaiKnowledgeBaseBulkDeletePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase Documents Summaries

<details><summary><code>client.agents.knowledgeBase.documents.summaries.get(requestParameters) -> Map&lt;String, com.elevenlabs.api.resources.agents.knowledgebase.documents.summaries.types.GetSummariesResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets multiple knowledge base document summaries by their IDs.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().summaries().get(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.agents.knowledgebase.documents.summaries.requests.GetSummariesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase Documents Chunk

<details><summary><code>client.agents.knowledgeBase.documents.chunk.get(documentationId, chunkId, requestParameters) -> KnowledgeBaseDocumentChunkResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get details about a specific documentation part used by RAG.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().chunk().get(
    documentationId,
    chunkId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**chunkId:** `String` — The id of a document RAG chunk from the knowledge base.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetChunkRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase Documents Chunks

<details><summary><code>client.agents.knowledgeBase.documents.chunks.list(documentationId, requestParameters) -> KnowledgeBaseDocumentChunksResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all RAG chunks for a specific knowledge base document.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().documents().chunks().list(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListChunksRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase CrawlJobs

<details><summary><code>client.agents.knowledgeBase.crawlJobs.list(requestParameters) -> ListCrawlJobsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of ongoing and recent crawl jobs for the user.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().crawlJobs().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListCrawlJobsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.crawlJobs.create(bodyCreateCrawlJobV1ConvaiKnowledgeBaseCrawlPost) -> CreateCrawlJobResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a crawl job to crawl the given URL with specified depth and page limits.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().crawlJobs().create(bodyCreateCrawlJobV1ConvaiKnowledgeBaseCrawlPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateCrawlJobV1ConvaiKnowledgeBaseCrawlPost:** `BodyCreateCrawlJobV1ConvaiKnowledgeBaseCrawlPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.crawlJobs.get(crawlJobId, requestParameters) -> GetCrawlJobResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get details about a specific crawl job including status and progress.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().crawlJobs().get(
    crawlJobId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**crawlJobId:** `String` — The id of the crawl job to retrieve

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetCrawlJobsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.crawlJobs.cancel(crawlJobId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Cancel a crawl job and delete all associated documents and folders.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().crawlJobs().cancel(
    crawlJobId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**crawlJobId:** `String` — The id of the crawl job to retrieve

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CancelCrawlJobsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents KnowledgeBase Document

<details><summary><code>client.agents.knowledgeBase.document.updateFile(documentationId, bodyUpdateFileDocumentV1ConvaiKnowledgeBaseDocumentationIdUpdateFilePatch) -> UpdateFileDocumentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the source file of a file document. The document name, content, and metadata are updated to reflect the new file. Any manual content edits will be overwritten.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().document().updateFile(
    documentationId,
    bodyUpdateFileDocumentV1ConvaiKnowledgeBaseDocumentationIdUpdateFilePatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateFileDocumentV1ConvaiKnowledgeBaseDocumentationIdUpdateFilePatch:** `BodyUpdateFileDocumentV1ConvaiKnowledgeBaseDocumentationIdUpdateFilePatch` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.document.refresh(documentationId, requestParameters) -> RefreshDocumentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Manually refresh a URL document by re-fetching its content from the source URL.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().document().refresh(
    documentationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RefreshDocumentRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.knowledgeBase.document.computeRagIndex(documentationId, ragIndexRequestModel) -> RagDocumentIndexResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

In case the document is not RAG indexed, it triggers rag indexing task, otherwise it just returns the current status.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().knowledgeBase().document().computeRagIndex(
    documentationId,
    ragIndexRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**documentationId:** `String` — The id of a document from the knowledge base. This is returned on document addition.

</dd>
</dl>

<dl>
<dd>

**ragIndexRequestModel:** `RagIndexRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Subagents

<details><summary><code>client.agents.subagents.getDependents(agentId, requestParameters) -> GetAgentSubagentDependentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of agents that use this agent as a subagent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().subagents().getDependents(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDependentsSubagentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Widget

<details><summary><code>client.agents.widget.get(agentId, requestParameters) -> GetAgentEmbedResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the widget configuration for an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().widget().get(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetWidgetRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Widget Avatar

<details><summary><code>client.agents.widget.avatar.create(agentId, bodyPostAgentAvatarV1ConvaiAgentsAgentIdAvatarPost) -> PostAgentAvatarResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sets the avatar for an agent displayed in the widget

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().widget().avatar().create(
    agentId,
    bodyPostAgentAvatarV1ConvaiAgentsAgentIdAvatarPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyPostAgentAvatarV1ConvaiAgentsAgentIdAvatarPost:** `BodyPostAgentAvatarV1ConvaiAgentsAgentIdAvatarPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Link

<details><summary><code>client.agents.link.get(agentId, requestParameters) -> GetAgentLinkResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the current link used to share the agent with others

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().link().get(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetLinkRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents HoldAudio

<details><summary><code>client.agents.holdAudio.create(agentId, bodyPostAgentHoldAudioV1ConvaiAgentsAgentIdHoldAudioPost) -> PostAgentHoldAudioResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sets the custom hold audio played on loop to callers waiting in the agent's concurrency wait queue. Replaces any previously uploaded clip.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().holdAudio().create(
    agentId,
    bodyPostAgentHoldAudioV1ConvaiAgentsAgentIdHoldAudioPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyPostAgentHoldAudioV1ConvaiAgentsAgentIdHoldAudioPost:** `BodyPostAgentHoldAudioV1ConvaiAgentsAgentIdHoldAudioPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.holdAudio.delete(agentId, requestParameters) -> DeleteAgentHoldAudioResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes the agent's custom hold audio; queued callers hear the default hold tone again.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().holdAudio().delete(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteHoldAudioRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents LlmUsage

<details><summary><code>client.agents.llmUsage.calculateForAgent(agentId, llmUsageCalculatorRequestModel) -> LlmUsageCalculatorResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Calculates expected number of LLM tokens needed for the specified agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().llmUsage().calculateForAgent(
    agentId,
    llmUsageCalculatorRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String`

</dd>
</dl>

<dl>
<dd>

**llmUsageCalculatorRequestModel:** `LlmUsageCalculatorRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.llmUsage.calculate(llmUsageCalculatorPublicRequestModel) -> LlmUsageCalculatorResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of LLM models and the expected cost for using them based on the provided values.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().llmUsage().calculate(llmUsageCalculatorPublicRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**llmUsageCalculatorPublicRequestModel:** `LlmUsageCalculatorPublicRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Tests

<details><summary><code>client.agents.tests.create(createTestsRequest) -> CreateAgentTestResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new agent response test.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().create(createTestsRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createTestsRequest:** `CreateTestsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.move(bodyBulkMoveTestsToFolderV1ConvaiAgentTestingBulkMovePost) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Moves multiple tests or folders from one folder to another.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().move(bodyBulkMoveTestsToFolderV1ConvaiAgentTestingBulkMovePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyBulkMoveTestsToFolderV1ConvaiAgentTestingBulkMovePost:** `BodyBulkMoveTestsToFolderV1ConvaiAgentTestingBulkMovePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.get(testId, requestParameters) -> GetTestsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets an agent response test by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().get(
    testId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testId:** `String` — The id of a chat response test. This is returned on test creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTestsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.update(testId, updateTestsRequest) -> UpdateTestsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates an agent response test by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().update(
    testId,
    updateTestsRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testId:** `String` — The id of a chat response test. This is returned on test creation.

</dd>
</dl>

<dl>
<dd>

**updateTestsRequest:** `UpdateTestsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.delete(testId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes an agent response test by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().delete(
    testId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testId:** `String` — The id of a chat response test. This is returned on test creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteTestsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.summaries(listTestsByIdsRequestModel) -> GetTestsSummariesByIdsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets agent response test summaries for the requested test IDs.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().summaries(listTestsByIdsRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**listTestsByIdsRequestModel:** `ListTestsByIdsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.list(requestParameters) -> GetTestsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Lists all agent response tests with pagination support and optional search filtering.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListTestsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Tests Folders

<details><summary><code>client.agents.tests.folders.create(bodyCreateAgentTestFolderV1ConvaiAgentTestingFoldersPost) -> CreateAgentTestFolderResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a folder for organizing agent tests.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().folders().create(bodyCreateAgentTestFolderV1ConvaiAgentTestingFoldersPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateAgentTestFolderV1ConvaiAgentTestingFoldersPost:** `BodyCreateAgentTestFolderV1ConvaiAgentTestingFoldersPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.folders.get(folderId, requestParameters) -> GetAgentTestFolderResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets an agent test folder by ID, including its folder path.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().folders().get(
    folderId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**folderId:** `String` — The folder ID.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetFoldersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.folders.update(folderId, bodyUpdateAgentTestFolderV1ConvaiAgentTestingFoldersFolderIdPatch) -> GetAgentTestFolderResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates an agent test folder. Currently only supports updating the folder name.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().folders().update(
    folderId,
    bodyUpdateAgentTestFolderV1ConvaiAgentTestingFoldersFolderIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**folderId:** `String` — The folder ID.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateAgentTestFolderV1ConvaiAgentTestingFoldersFolderIdPatch:** `BodyUpdateAgentTestFolderV1ConvaiAgentTestingFoldersFolderIdPatch` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.folders.delete(folderId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes an agent test folder by ID. Use force=true to delete a non-empty folder and all its contents.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().folders().delete(
    folderId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**folderId:** `String` — The folder ID.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteFoldersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Tests Invocations

<details><summary><code>client.agents.tests.invocations.list(requestParameters) -> GetTestInvocationsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Lists all test invocations with pagination support and optional search filtering.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().invocations().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListInvocationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.invocations.get(testInvocationId, requestParameters) -> GetTestSuiteInvocationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets a test invocation by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().invocations().get(
    testInvocationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testInvocationId:** `String` — The id of a test invocation. This is returned when tests are run.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetInvocationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.invocations.update(testInvocationId, updateTestSuiteInvocationRequestModel) -> GetTestSuiteInvocationResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates the user-provided name of a test invocation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().invocations().update(
    testInvocationId,
    updateTestSuiteInvocationRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testInvocationId:** `String` — The id of a test invocation. This is returned when tests are run.

</dd>
</dl>

<dl>
<dd>

**updateTestSuiteInvocationRequestModel:** `UpdateTestSuiteInvocationRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.invocations.cancel(testInvocationId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Cancels all pending runs in a test invocation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().invocations().cancel(
    testInvocationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testInvocationId:** `String` — The id of a test invocation. This is returned when tests are run.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CancelInvocationsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tests.invocations.resubmit(testInvocationId, resubmitTestsRequestModel) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Resubmits specific test runs from a test invocation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tests().invocations().resubmit(
    testInvocationId,
    resubmitTestsRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**testInvocationId:** `String` — The id of a test invocation. This is returned when tests are run.

</dd>
</dl>

<dl>
<dd>

**resubmitTestsRequestModel:** `ResubmitTestsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Users

<details><summary><code>client.agents.users.list(requestParameters) -> GetConversationUsersPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get distinct users from conversations with pagination.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().users().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListUsersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents TriageTickets

<details><summary><code>client.agents.triageTickets.list(agentId, requestParameters) -> GetAgentConversationTicketsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List an agent's issues, ordered by most recently created first unless sorted by priority. These are issues about the agent's own performance on a conversation (to work on with Architect), not tickets an agent opens for end users.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().list(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListTriageTicketsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.createManual(agentId, createManualTicketRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Manually raise a follow-up issue against an agent, not tied to any conversation (for example a task like 'add the KB about X'). Without a title, one is derived from the comment. Requires viewer access to the agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().createManual(
    agentId,
    createManualTicketRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String`

</dd>
</dl>

<dl>
<dd>

**createManualTicketRequestModel:** `CreateManualTicketRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.listForWorkspace(requestParameters) -> GetAgentConversationTicketsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List agent issues across every agent in the workspace, ordered by most recently created first. Use this to build a workspace-wide view (for example, issues assigned to the caller). Supports the same filters, sorting and search as the per-agent endpoint. Issues for agents the caller cannot access are omitted.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().listForWorkspace(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListForWorkspaceTriageTicketsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.create(createAgentConversationTicketRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Raise an issue about an agent's performance on a conversation, to work on with Architect. Provide an overall comment and/or turn-level comments describing what went wrong.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().create(createAgentConversationTicketRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createAgentConversationTicketRequestModel:** `CreateAgentConversationTicketRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.listAssignableUsers(agentId, requestParameters) -> List&lt;AssignableUserResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

All non-service-account workspace members, each flagged with whether they currently have at least viewer access to the agent. Members without access are included (not filtered out) so the UI can offer them as an assignee and prompt to grant access first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().listAssignableUsers(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListAssignableUsersTriageTicketsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.get(agentqaTicketId, requestParameters) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get an agent issue by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().get(
    agentqaTicketId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTriageTicketsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.update(agentqaTicketId, patchAgentConversationTicketRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an agent issue's title, comment, status, priority, and/or assignee. Requires viewer access to the issue's agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().update(
    agentqaTicketId,
    patchAgentConversationTicketRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**patchAgentConversationTicketRequestModel:** `PatchAgentConversationTicketRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.delete(agentqaTicketId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an agent issue. Restricted to the issue creator or a workspace admin.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().delete(
    agentqaTicketId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteTriageTicketsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.addComment(agentqaTicketId, addTicketCommentRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Append a comment discussing how to resolve the agent issue. Requires viewer access to the issue's agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().addComment(
    agentqaTicketId,
    addTicketCommentRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**addTicketCommentRequestModel:** `AddTicketCommentRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.merge(agentqaTicketId, mergeAgentConversationTicketsRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Merge other open issues of the same agent into this pending, in-progress, or resolved one. Their conversations move onto this issue, its priority is raised to the highest among them, and they are kept with status 'merged'. Requires viewer access to the issue's agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().merge(
    agentqaTicketId,
    mergeAgentConversationTicketsRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**mergeAgentConversationTicketsRequestModel:** `MergeAgentConversationTicketsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.triageTickets.addTurnComment(agentqaTicketId, addTurnCommentRequestModel) -> AgentConversationTicketResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Append a turn-level comment to an agent issue. Requires viewer access to the issue's agent.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().triageTickets().addTurnComment(
    agentqaTicketId,
    addTurnCommentRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentqaTicketId:** `String`

</dd>
</dl>

<dl>
<dd>

**addTurnCommentRequestModel:** `AddTurnCommentRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents PhoneNumbers

<details><summary><code>client.agents.phoneNumbers.list(requestParameters) -> List&lt;ListPhoneNumbersResponseItem&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve all Phone Numbers

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListPhoneNumbersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.create(createPhoneNumbersRequest) -> CreatePhoneNumberResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Import Phone Number from provider configuration (Twilio, Exotel, or SIP trunk)

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().create(createPhoneNumbersRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createPhoneNumbersRequest:** `CreatePhoneNumbersRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.get(phoneNumberId, requestParameters) -> GetPhoneNumbersResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve Phone Number details by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().get(
    phoneNumberId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String` — The phone number ID. This is returned when a phone number is imported.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetPhoneNumbersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.update(phoneNumberId, updatePhoneNumberRequest) -> UpdatePhoneNumbersResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update assigned agent of a phone number

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().update(
    phoneNumberId,
    updatePhoneNumberRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String` — The phone number ID. This is returned when a phone number is imported.

</dd>
</dl>

<dl>
<dd>

**updatePhoneNumberRequest:** `UpdatePhoneNumberRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.delete(phoneNumberId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete Phone Number by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().delete(
    phoneNumberId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String` — The phone number ID. This is returned when a phone number is imported.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeletePhoneNumbersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.listV2(requestParameters) -> GetPhoneNumbersPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a page of Phone Numbers

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().listV2(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListV2PhoneNumbersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.phoneNumbers.getSipMessages(phoneNumberId, requestParameters) -> GetSipLogMessagesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get SIP messages for a phone number

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().phoneNumbers().getSipMessages(
    phoneNumberId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String` — The phone number ID. This is returned when a phone number is imported.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSipMessagesPhoneNumbersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Llm

<details><summary><code>client.agents.llm.list() -> LlmListResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of available LLM models that can be used with agents, including their capabilities and any deprecation status. The response is filtered based on the data residency of the deployment and any compliance requirements (e.g. HIPAA) of the workspace subscription.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().llm().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Analytics LiveCount

<details><summary><code>client.agents.analytics.liveCount.get(requestParameters) -> GetLiveCountResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the live count of the ongoing conversations.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analytics().liveCount().get(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetLiveCountRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Tools

<details><summary><code>client.agents.tools.list(requestParameters) -> ToolsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all available tools in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListToolsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tools.create(toolRequestModel) -> ToolResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a new tool to the available tools in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().create(toolRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolRequestModel:** `ToolRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tools.get(toolId, requestParameters) -> ToolResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get tool that is available in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().get(
    toolId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolId:** `String` — ID of the requested tool.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetToolsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tools.update(toolId, toolRequestModel) -> ToolResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update tool that is available in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().update(
    toolId,
    toolRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolId:** `String` — ID of the requested tool.

</dd>
</dl>

<dl>
<dd>

**toolRequestModel:** `ToolRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tools.delete(toolId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete tool from the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().delete(
    toolId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolId:** `String` — ID of the requested tool.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteToolsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.tools.getDependentAgents(toolId, requestParameters) -> GetToolDependentAgentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of agents depending on this tool

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().getDependentAgents(
    toolId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolId:** `String` — ID of the requested tool.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDependentAgentsToolsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Tools Executions

<details><summary><code>client.agents.tools.executions.get(toolId, requestParameters) -> GetToolExecutionsPageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get paginated list of tool executions for a specific tool.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().tools().executions().get(
    toolId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**toolId:** `String` — ID of the requested tool.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetExecutionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents McpServers

<details><summary><code>client.agents.mcpServers.list() -> McpServersResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve all MCP server configurations available in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.create(mcpServerRequestModel) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new MCP server configuration in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().create(mcpServerRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerRequestModel:** `McpServerRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.get(mcpServerId, requestParameters) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a specific MCP server configuration from the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().get(
    mcpServerId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetMcpServersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.update(mcpServerId, mcpServerConfigUpdateRequestModel) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the configuration settings for an MCP server.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().update(
    mcpServerId,
    mcpServerConfigUpdateRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**mcpServerConfigUpdateRequestModel:** `McpServerConfigUpdateRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.delete(mcpServerId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a specific MCP server configuration from the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().delete(
    mcpServerId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteMcpServersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents McpServers Tools

<details><summary><code>client.agents.mcpServers.tools.list(mcpServerId, requestParameters) -> ListMcpToolsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve all tools available for a specific MCP server configuration.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().tools().list(
    mcpServerId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.agents.mcpservers.tools.requests.ListToolsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents McpServers ToolApprovals

<details><summary><code>client.agents.mcpServers.toolApprovals.create(mcpServerId, mcpToolAddApprovalRequestModel) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add approval for a specific MCP tool when using per-tool approval mode.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolApprovals().create(
    mcpServerId,
    mcpToolAddApprovalRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**mcpToolAddApprovalRequestModel:** `McpToolAddApprovalRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.toolApprovals.delete(mcpServerId, toolName, requestParameters) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove approval for a specific MCP tool when using per-tool approval mode.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolApprovals().delete(
    mcpServerId,
    toolName,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**toolName:** `String` — Name of the MCP tool to remove approval for.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteToolApprovalsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents McpServers ToolConfigs

<details><summary><code>client.agents.mcpServers.toolConfigs.create(mcpServerId, requestParameters) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create configuration overrides for a specific MCP tool.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolConfigs().create(
    mcpServerId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `McpToolConfigOverrideCreateRequestModel` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.toolConfigs.get(mcpServerId, toolName, requestParameters) -> McpToolConfigOverrideOutput</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve configuration overrides for a specific MCP tool.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolConfigs().get(
    mcpServerId,
    toolName,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**toolName:** `String` — Name of the MCP tool to retrieve config overrides for.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetToolConfigsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.toolConfigs.update(mcpServerId, toolName, requestParameters) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update configuration overrides for a specific MCP tool.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolConfigs().update(
    mcpServerId,
    toolName,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**toolName:** `String` — Name of the MCP tool to update config overrides for.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `McpToolConfigOverrideUpdateRequestModel` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mcpServers.toolConfigs.delete(mcpServerId, toolName, requestParameters) -> McpServerResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove configuration overrides for a specific MCP tool.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mcpServers().toolConfigs().delete(
    mcpServerId,
    toolName,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**mcpServerId:** `String` — ID of the MCP Server.

</dd>
</dl>

<dl>
<dd>

**toolName:** `String` — Name of the MCP tool to remove config overrides for.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteToolConfigsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents AnalysisItems

<details><summary><code>client.agents.analysisItems.list(requestParameters) -> AnalysisItemsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List analysis items available in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAnalysisItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.create(createAnalysisItemsRequest) -> AnalysisItemResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new analysis item (evaluation criterion or data collection) in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().create(createAnalysisItemsRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createAnalysisItemsRequest:** `CreateAnalysisItemsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.updateAttachments(analysisItemId, updateAnalysisItemAttachmentsRequestModel) -> UpdateAnalysisItemAttachmentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Attach or detach one analysis item on multiple agent branches.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().updateAttachments(
    analysisItemId,
    updateAnalysisItemAttachmentsRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**updateAnalysisItemAttachmentsRequestModel:** `UpdateAnalysisItemAttachmentsRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.get(analysisItemId, requestParameters) -> AnalysisItemResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get an analysis item available in the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().get(
    analysisItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAnalysisItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.update(analysisItemId, updateAnalysisItemsRequest) -> AnalysisItemResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an analysis item. Publishes a new version when the content changes.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().update(
    analysisItemId,
    updateAnalysisItemsRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**updateAnalysisItemsRequest:** `UpdateAnalysisItemsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.delete(analysisItemId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an analysis item and all of its versions from the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().delete(
    analysisItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteAnalysisItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.listVersions(analysisItemId, requestParameters) -> AnalysisItemVersionsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the published versions of an analysis item, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().listVersions(
    analysisItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListVersionsAnalysisItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.analysisItems.copy(analysisItemId, requestParameters) -> AnalysisItemResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new analysis item from this item's current definition.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().copy(
    analysisItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CopyAnalysisItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents AnalysisItems DependentAgents

<details><summary><code>client.agents.analysisItems.dependentAgents.get(analysisItemId, requestParameters) -> GetAnalysisItemDependentAgentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of agents (and branches) that reference this analysis item.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().analysisItems().dependentAgents().get(
    analysisItemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**analysisItemId:** `String` — ID of the requested analysis item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDependentAgentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Secrets

<details><summary><code>client.agents.secrets.list(requestParameters) -> GetWorkspaceSecretsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all workspace secrets for the user

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListSecretsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.secrets.create(postWorkspaceSecretRequest) -> PostWorkspaceSecretResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new secret for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().create(postWorkspaceSecretRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**postWorkspaceSecretRequest:** `PostWorkspaceSecretRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.secrets.get(secretId, requestParameters) -> ConvAiWorkspaceStoredSecretConfig</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a workspace secret by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().get(
    secretId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**secretId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSecretsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.secrets.update(secretId, patchWorkspaceSecretRequest) -> PostWorkspaceSecretResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing secret for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().update(
    secretId,
    patchWorkspaceSecretRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**secretId:** `String`

</dd>
</dl>

<dl>
<dd>

**patchWorkspaceSecretRequest:** `PatchWorkspaceSecretRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.secrets.delete(secretId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a workspace secret if it's not in use

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().delete(
    secretId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**secretId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteSecretsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.secrets.getDependencies(secretId, resourceType, requestParameters) -> GetSecretDependenciesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get paginated list of resources that depend on a specific secret, filtered by resource type.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().secrets().getDependencies(
    secretId,
    resourceType,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**secretId:** `String`

</dd>
</dl>

<dl>
<dd>

**resourceType:** `SecretDependencyResourceType`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDependenciesSecretsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents BatchCalls

<details><summary><code>client.agents.batchCalls.create(bodySubmitABatchCallRequestV1ConvaiBatchCallingSubmitPost) -> BatchCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Submit a batch call request to schedule calls for multiple recipients.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().create(bodySubmitABatchCallRequestV1ConvaiBatchCallingSubmitPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodySubmitABatchCallRequestV1ConvaiBatchCallingSubmitPost:** `BodySubmitABatchCallRequestV1ConvaiBatchCallingSubmitPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.list(requestParameters) -> WorkspaceBatchCallsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all batch calls for the current workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.get(batchId, requestParameters) -> BatchCallDetailedResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get detailed information about a batch call including all recipients.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().get(
    batchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.delete(batchId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Permanently delete a batch call and all recipient records. Conversations remain in history.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().delete(
    batchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.cancel(batchId, requestParameters) -> BatchCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Cancel a running batch call and set all recipients to cancelled status.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().cancel(
    batchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CancelBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.retry(batchId, requestParameters) -> BatchCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retry a batch call, calling failed and no-response recipients again.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().retry(
    batchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RetryBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.batchCalls.export(batchId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Download all recipients and conversation results for a batch call as CSV, including calls that are still in progress.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().batchCalls().export(
    batchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ExportBatchCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents SipTrunk

<details><summary><code>client.agents.sipTrunk.outboundCall(bodyHandleAnOutboundCallViaSipTrunkV1ConvaiSipTrunkOutboundCallPost) -> SipTrunkOutboundCallResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Handle an outbound call via SIP trunk

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().sipTrunk().outboundCall(bodyHandleAnOutboundCallViaSipTrunkV1ConvaiSipTrunkOutboundCallPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyHandleAnOutboundCallViaSipTrunkV1ConvaiSipTrunkOutboundCallPost:** `BodyHandleAnOutboundCallViaSipTrunkV1ConvaiSipTrunkOutboundCallPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents WhatsappAccounts

<details><summary><code>client.agents.whatsappAccounts.get(phoneNumberId, requestParameters) -> GetWhatsAppAccountResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a WhatsApp account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsappAccounts().get(
    phoneNumberId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetWhatsappAccountsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.whatsappAccounts.update(phoneNumberId, updateWhatsAppAccountRequest) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a WhatsApp account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsappAccounts().update(
    phoneNumberId,
    updateWhatsAppAccountRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String`

</dd>
</dl>

<dl>
<dd>

**updateWhatsAppAccountRequest:** `UpdateWhatsAppAccountRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.whatsappAccounts.delete(phoneNumberId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a WhatsApp account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsappAccounts().delete(
    phoneNumberId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneNumberId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteWhatsappAccountsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.whatsappAccounts.list(requestParameters) -> ListWhatsAppAccountsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all WhatsApp accounts

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().whatsappAccounts().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListWhatsappAccountsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Branches

<details><summary><code>client.agents.branches.list(agentId, requestParameters) -> ListResponseAgentBranchSummary_</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of branches an agent has

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().list(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListBranchesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.create(agentId, bodyCreateANewBranchV1ConvaiAgentsAgentIdBranchesPost) -> CreateAgentBranchResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new branch from a given version of any branch

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().create(
    agentId,
    bodyCreateANewBranchV1ConvaiAgentsAgentIdBranchesPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyCreateANewBranchV1ConvaiAgentsAgentIdBranchesPost:** `BodyCreateANewBranchV1ConvaiAgentsAgentIdBranchesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.get(agentId, branchId, requestParameters) -> AgentBranchResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get information about a single agent branch

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().get(
    agentId,
    branchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Unique identifier for the branch.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetBranchesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.update(agentId, branchId, bodyUpdateAgentBranchV1ConvaiAgentsAgentIdBranchesBranchIdPatch) -> AgentBranchResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update agent branch properties such as name, description, archiving status, protection level, and auto-rebase onto main

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().update(
    agentId,
    branchId,
    bodyUpdateAgentBranchV1ConvaiAgentsAgentIdBranchesBranchIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Unique identifier for the branch.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateAgentBranchV1ConvaiAgentsAgentIdBranchesBranchIdPatch:** `BodyUpdateAgentBranchV1ConvaiAgentsAgentIdBranchesBranchIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.previewMerge(agentId, sourceBranchId, requestParameters) -> MergePreviewResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the result of merging the source branch into the target branch without performing the merge. When `conflicts` is not empty, the merge itself is rejected unless `conflict_resolutions` settles every conflict or `force` is set; until then the returned configuration shows one side of each conflict only as a placeholder.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().previewMerge(
    agentId,
    sourceBranchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**sourceBranchId:** `String` — Unique identifier for the source branch to merge from.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `PreviewMergeBranchesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.merge(agentId, sourceBranchId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Merge a branch into a target branch. If both branches changed the same field, the merge returns 409 unless `conflict_resolutions` settles every conflict the merge preview reports, or `force` is set.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().merge(
    agentId,
    sourceBranchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**sourceBranchId:** `String` — Unique identifier for the source branch to merge from.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodyMergeABranchIntoATargetBranchV1ConvaiAgentsAgentIdBranchesSourceBranchIdMergePost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.previewRebase(agentId, branchId, requestParameters) -> MergePreviewResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the result of pulling changes from a source branch (defaults to main) into the given branch without performing it. When `conflicts` is not empty, the rebase itself is rejected unless `conflict_resolutions` settles every conflict; until then the returned configuration keeps the given branch's side of each conflict.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().previewRebase(
    agentId,
    branchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Unique identifier for the source branch to merge from.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `PreviewRebaseBranchesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.branches.rebase(agentId, branchId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Pull changes from a source branch (defaults to main) into this branch as a new version. If both branches changed the same field, the rebase returns 409 unless `conflict_resolutions` settles every conflict the rebase preview reports.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().branches().rebase(
    agentId,
    branchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Unique identifier for the source branch to merge from.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodyRebaseABranchOntoMainV1ConvaiAgentsAgentIdBranchesBranchIdRebasePost` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Versions

<details><summary><code>client.agents.versions.get(agentId, versionId, requestParameters) -> AgentVersionMetadata</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get metadata for a specific agent version

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().versions().get(
    agentId,
    versionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**versionId:** `String` — Unique identifier for the version.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetVersionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Deployments

<details><summary><code>client.agents.deployments.list(agentId, requestParameters) -> ListResponseAgentDeploymentHistoryItem_</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the traffic split history of an agent, newest first

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().deployments().list(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListDeploymentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.deployments.create(agentId, bodyCreateOrUpdateDeploymentsV1ConvaiAgentsAgentIdDeploymentsPost) -> AgentDeploymentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new deployment for an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().deployments().create(
    agentId,
    bodyCreateOrUpdateDeploymentsV1ConvaiAgentsAgentIdDeploymentsPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyCreateOrUpdateDeploymentsV1ConvaiAgentsAgentIdDeploymentsPost:** `BodyCreateOrUpdateDeploymentsV1ConvaiAgentsAgentIdDeploymentsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Drafts

<details><summary><code>client.agents.drafts.create(agentId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new draft for an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().drafts().create(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodyCreateAgentDraftV1ConvaiAgentsAgentIdDraftsPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.drafts.delete(agentId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a draft for an agent

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().drafts().delete(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteDraftsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Procedures

<details><summary><code>client.agents.procedures.list(agentId, branchId, requestParameters) -> ListProceduresResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the procedures attached to this agent branch. By default, unpublished drafts take precedence over the latest committed version. Pass agent_version_id to list a published snapshot instead. has_draft is true when a procedure has unpublished draft changes on this branch. Procedure content is not included; use Get Procedure to read a procedure's body.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().list(
    agentId,
    branchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListProceduresRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.create(agentId, branchId, createProcedureRequestModel) -> CreateProcedureResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new procedure for the agent on a branch.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().create(
    agentId,
    branchId,
    createProcedureRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**createProcedureRequestModel:** `CreateProcedureRequestModel` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.compile(agentId, branchId, requestParameters) -> CompileProceduresResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Legacy. Do not use. Saving an agent draft (`POST /v1/convai/agents/{agent_id}/drafts`) and publishing an agent (`PATCH /v1/convai/agents/{agent_id}`) compile structured procedures into workflow nodes and edges, save the compiled workflow with the draft or version, and return validation errors, so a separate compile call is no longer needed. This endpoint remains available for the time being so existing callers do not break, as a dry-run that compiles the current procedure drafts into a workflow without persisting anything. It will eventually be deprecated.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().compile(
    agentId,
    branchId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CompileProceduresRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.get(agentId, branchId, procedureId, requestParameters) -> ProcedureAtVersionResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a procedure at a specific version or the current branch HEAD.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().get(
    agentId,
    branchId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — The procedure ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetProceduresRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.remove(agentId, branchId, procedureId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a procedure from the agent's draft working set. Removing a folder cascades to its entire subtree, rejected if any procedure outside the subtree hands off into it.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().remove(
    agentId,
    branchId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — The procedure ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RemoveProceduresRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Procedures Drafts

<details><summary><code>client.agents.procedures.drafts.get(agentId, branchId, procedureId, requestParameters) -> ProcedureDraftResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get user's draft for a procedure

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().drafts().get(
    agentId,
    branchId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — The procedure ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetDraftsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.drafts.update(agentId, branchId, procedureId, updateProcedureDraftRequestModel) -> ProcedureDraftResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create or update user's draft for a procedure

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().drafts().update(
    agentId,
    branchId,
    procedureId,
    updateProcedureDraftRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — The procedure ID

</dd>
</dl>

<dl>
<dd>

**updateProcedureDraftRequestModel:** `UpdateProcedureDraftRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.procedures.drafts.delete(agentId, branchId, procedureId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete user's draft for a procedure, resetting to the committed version

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().procedures().drafts().delete(
    agentId,
    branchId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — Agent ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**branchId:** `String` — Branch ID to get the procedure draft from

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — The procedure ID

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.agents.procedures.drafts.requests.DeleteDraftsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents MergeProposals

<details><summary><code>client.agents.mergeProposals.list(agentId, requestParameters) -> PaginatedResultAgentMergeProposalResponse_</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the proposals for an agent, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().list(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListMergeProposalsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.create(agentId, bodyCreateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsPost) -> CreateAgentMergeProposalResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Record a request to merge a source branch into a target branch. Anyone with edit access can open one; merging it later is gated on write access to the target branch, so this is how a change reaches a protected branch the author cannot merge into themselves.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().create(
    agentId,
    bodyCreateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**bodyCreateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsPost:** `BodyCreateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.get(agentId, mergeProposalId, requestParameters) -> AgentMergeProposalResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a single merge_proposal.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().get(
    agentId,
    mergeProposalId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**mergeProposalId:** `String` — Unique identifier for the merge_proposal.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetMergeProposalsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.update(agentId, mergeProposalId, bodyUpdateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdPatch) -> AgentMergeProposalResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Edit an open merge_proposal's title or description, or close it. The author closing it is recorded as withdrawn; anyone else as rejected.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().update(
    agentId,
    mergeProposalId,
    bodyUpdateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**mergeProposalId:** `String` — Unique identifier for the merge_proposal.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdPatch:** `BodyUpdateAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.submitReview(agentId, mergeProposalId, bodyReviewAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdReviewsPost) -> AgentMergeProposalResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Approve a merge_proposal or request changes on it. A user's latest review replaces their previous one. Non-admins need an approval from another user before the merge is allowed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().submitReview(
    agentId,
    mergeProposalId,
    bodyReviewAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdReviewsPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**mergeProposalId:** `String` — Unique identifier for the merge_proposal.

</dd>
</dl>

<dl>
<dd>

**bodyReviewAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdReviewsPost:** `BodyReviewAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdReviewsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.addComment(agentId, mergeProposalId, bodyCommentOnAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdCommentsPost) -> AgentMergeProposalResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Leave a comment on a merge_proposal without recording a review verdict. Unlike reviews, comments accumulate and can still be added once the merge_proposal is merged or closed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().addComment(
    agentId,
    mergeProposalId,
    bodyCommentOnAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdCommentsPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**mergeProposalId:** `String` — Unique identifier for the merge_proposal.

</dd>
</dl>

<dl>
<dd>

**bodyCommentOnAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdCommentsPost:** `BodyCommentOnAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdCommentsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.mergeProposals.merge(agentId, mergeProposalId, bodyMergeAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdMergePost) -> AgentMergeProposalResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Execute the merge. The caller must have write access to the target branch (admins only, for a protected branch), so this is where a reviewer approves and merges a request opened by someone who could not.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().mergeProposals().merge(
    agentId,
    mergeProposalId,
    bodyMergeAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdMergePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — The id of an agent. This is returned on agent creation.

</dd>
</dl>

<dl>
<dd>

**mergeProposalId:** `String` — Unique identifier for the merge_proposal.

</dd>
</dl>

<dl>
<dd>

**bodyMergeAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdMergePost:** `BodyMergeAMergeProposalV1ConvaiAgentsAgentIdMergeProposalsMergeProposalIdMergePost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents OutboundCampaigns

<details><summary><code>client.agents.outboundCampaigns.list(requestParameters) -> CampaignPageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's outbound campaigns, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.pauseAll() -> PauseAllCampaignsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Pause every running outbound campaign in the workspace. Paused campaigns place no new calls until resumed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().pauseAll();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.stopAll() -> StopAllCampaignsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stop every outbound campaign in the workspace that hasn't finished. Returns `202` while calls already placed are still finishing.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().stopAll();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.create(submitCampaignRequest) -> SubmitCampaignResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create an outbound campaign that calls or messages each recipient with the agent, within the policy's calling windows, retrying and following up as it allows. Returns `201` for a new campaign. A repeated request, matched by `idempotency_key` or else by its body, returns the existing campaign with `created` false.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().create(submitCampaignRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**submitCampaignRequest:** `SubmitCampaignRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.get(campaignId, requestParameters) -> CampaignResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get an outbound campaign and its lead counts.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().get(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.getProgress(campaignId, requestParameters) -> CampaignProgressResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get where an outbound campaign's leads and calls stand, and when its next call is due.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().getProgress(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetProgressOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.getFollowUpRequests(campaignId, requestParameters) -> CampaignFollowUpRequestsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Count the callbacks and redirects contacts asked for during an outbound campaign's calls.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().getFollowUpRequests(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetFollowUpRequestsOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.pause(campaignId, requestParameters) -> CampaignResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Pause an outbound campaign. It places no new calls until resumed; calls already placed carry on.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().pause(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `PauseOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.resume(campaignId, requestParameters) -> CampaignResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Resume a paused outbound campaign, including calls an attention item held back.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().resume(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ResumeOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.stop(campaignId, requestParameters) -> CampaignResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stop an outbound campaign for good: queued calls are cancelled and no lead is called again. Returns `202` with the campaign `stopping` while calls already placed are still finishing.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().stop(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StopOutboundCampaignsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents OutboundCampaigns Config

<details><summary><code>client.agents.outboundCampaigns.configService.get() -> WorkspaceConfigResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the workspace's outbound campaign configuration: the default policy new campaigns take, and per-agent overrides.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().configService().get();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.configService.update(replaceWorkspaceConfigRequest) -> WorkspaceConfigResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create or replace the workspace's outbound campaign configuration. Send the `version` you last read as `expected_version`, or omit it to create the first configuration. A configuration changed since you read it is not replaced. Campaigns keep the policy they started with.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().configService().update(replaceWorkspaceConfigRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**replaceWorkspaceConfigRequest:** `ReplaceWorkspaceConfigRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents OutboundCampaigns Leads

<details><summary><code>client.agents.outboundCampaigns.leads.list(campaignId, requestParameters) -> CampaignLeadsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the leads of an outbound campaign, one per recipient.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().leads().list(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListLeadsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.leads.get(campaignId, leadId, requestParameters) -> LeadDetailResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get one lead of an outbound campaign.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().leads().get(
    campaignId,
    leadId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**leadId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetLeadsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.leads.delete(campaignId, leadId, requestParameters) -> SuppressLeadResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a lead from an outbound campaign so it is never called again. Returns `202` when a call already placed may still go through.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().leads().delete(
    campaignId,
    leadId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**leadId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteLeadsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents OutboundCampaigns Calls

<details><summary><code>client.agents.outboundCampaigns.calls.list(campaignId, requestParameters) -> CampaignCallsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the calls of an outbound campaign, newest first: first contacts, retries, callbacks and redirects, whether queued, under way or done.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().calls().list(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.calls.export(campaignId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Download every call of a campaign, with its lead and conversation results, as CSV.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().calls().export(
    campaignId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**campaignId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ExportCallsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents OutboundCampaigns Attention

<details><summary><code>client.agents.outboundCampaigns.attention.list(requestParameters) -> AttentionPageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List problems in the workspace's outbound campaigns that need you to act, such as an agent or phone number that can no longer be used, newest first. A campaign with an open item holds back the calls it affects.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().attention().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAttentionRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.attention.retry(attentionId, requestParameters) -> AttentionView</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Place the calls a retryable attention item holds back, once its cause is fixed. Resumes the campaign.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().attention().retry(
    attentionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**attentionId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RetryAttentionRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.outboundCampaigns.attention.resolve(attentionId, attentionResolutionRequest) -> AttentionView</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Close an attention item that can't be retried, recording how it was handled.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().outboundCampaigns().attention().resolve(
    attentionId,
    attentionResolutionRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**attentionId:** `String`

</dd>
</dl>

<dl>
<dd>

**attentionResolutionRequest:** `AttentionResolutionRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Agents Realtime

<details><summary><code>client.agents.realtime.listConversationGroups(requestParameters) -> RealtimeConversationGroupsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the realtime conversation groups the current user can view.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().listConversationGroups(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListConversationGroupsRealtimeRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.createConversationGroup(createRealtimeConversationGroupRequestModel) -> RealtimeConversationGroupResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a realtime conversation group owned by the current user.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().createConversationGroup(createRealtimeConversationGroupRequestModel);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createRealtimeConversationGroupRequestModel:** `CreateRealtimeConversationGroupRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.getConversationGroup(conversationGroupId, requestParameters) -> RealtimeConversationGroupResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a realtime conversation group by id.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().getConversationGroup(
    conversationGroupId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationGroupId:** `String` — The id of a realtime conversation group.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetConversationGroupRealtimeRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.updateConversationGroup(conversationGroupId, updateRealtimeConversationGroupRequestModel) -> RealtimeConversationGroupResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Rename or re-describe a realtime conversation group.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().updateConversationGroup(
    conversationGroupId,
    updateRealtimeConversationGroupRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationGroupId:** `String` — The id of a realtime conversation group.

</dd>
</dl>

<dl>
<dd>

**updateRealtimeConversationGroupRequestModel:** `UpdateRealtimeConversationGroupRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.deleteConversationGroup(conversationGroupId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a realtime conversation group. Every conversation in it becomes ungrouped, so access granted through the group is revoked.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().deleteConversationGroup(
    conversationGroupId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationGroupId:** `String` — The id of a realtime conversation group.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteConversationGroupRealtimeRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.setConversationGroup(conversationId, setConversationGroupRequestModel) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Place a realtime conversation in a conversation group, moving it out of its current group if it has one.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().setConversationGroup(
    conversationId,
    setConversationGroupRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of a realtime conversation.

</dd>
</dl>

<dl>
<dd>

**setConversationGroupRequestModel:** `SetConversationGroupRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.clearConversationGroup(conversationId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a realtime conversation from its conversation group.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().clearConversationGroup(
    conversationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**conversationId:** `String` — The id of a realtime conversation.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ClearConversationGroupRealtimeRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.agents.realtime.createClientSecret(createClientSecretRequest) -> ClientSecretResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create an ephemeral client secret for authenticating browser-based Realtime API sessions.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.agents().realtime().createClientSecret(createClientSecretRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createClientSecretRequest:** `CreateClientSecretRequest` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## TextToSpeech

<details><summary><code>client.textToSpeech.convert(voiceId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts text into speech using a voice of your choice and returns audio.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToSpeech().convert(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. Use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodyTextToSpeechFull` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToSpeech.convertWithTimestamps(voiceId, requestParameters) -> AudioWithTimestampsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Generate speech from text with precise character-level timing information for audio-text synchronization.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToSpeech().convertWithTimestamps(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodyTextToSpeechFullWithTimestamps` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToSpeech.stream(voiceId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts text into speech using a voice of your choice and returns audio as an audio stream.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToSpeech().stream(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. Use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StreamTextToSpeechRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToSpeech.streamWithTimestamps(voiceId, requestParameters) -> Iterable&lt;StreamingAudioChunkWithTimestampsResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts text into speech using a voice of your choice and returns a stream of JSONs containing audio as a base64 encoded string together with information on when which character was spoken.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToSpeech().streamWithTimestamps(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. Use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StreamTextToSpeechWithTimestampsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Flows TextToSpeech

<details><summary><code>client.flows.textToSpeech.list(requestParameters) -> MediaGenerationListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the speech generations created through this API, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().textToSpeech().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListTextToSpeechRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.textToSpeech.create(textToSpeechGenerationRequest) -> MediaGenerationCreateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start a speech generation with the selected model. Charged per character via text-to-speech billing. Use this over `/v1/text-to-speech` for the asynchronous generation lifecycle or for models not offered there; for direct, synchronous speech synthesis, prefer `/v1/text-to-speech`.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().textToSpeech().create(textToSpeechGenerationRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**textToSpeechGenerationRequest:** `TextToSpeechGenerationRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.textToSpeech.get(generationId, requestParameters) -> MediaGenerationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the status of a speech generation, and retrieve its output URL once completed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().textToSpeech().get(
    generationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**generationId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTextToSpeechRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Flows Video

<details><summary><code>client.flows.video.list(requestParameters) -> MediaGenerationListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the video generations created through this API, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().video().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListVideoRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.video.create(videoGenerationRequest) -> MediaGenerationCreateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start a video generation with the selected model.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().video().create(videoGenerationRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**videoGenerationRequest:** `VideoGenerationRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.video.get(generationId, requestParameters) -> MediaGenerationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the status of a video generation, and retrieve its output URL once completed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().video().get(
    generationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**generationId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetVideoRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Flows Image

<details><summary><code>client.flows.image.list(requestParameters) -> MediaGenerationListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the image generations created through this API, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().image().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListImageRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.image.create(imageGenerationRequest) -> MediaGenerationCreateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start an image generation with the selected model.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().image().create(imageGenerationRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**imageGenerationRequest:** `ImageGenerationRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.image.get(generationId, requestParameters) -> MediaGenerationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the status of an image generation, and retrieve its output URL once completed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().image().get(
    generationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**generationId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetImageRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Flows Templates

<details><summary><code>client.flows.templates.list(requestParameters) -> TemplateListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the published flows templates in your workspace, together with each runnable version's inputs and outputs. Use the ids here as `template_id` / `version_id` on `POST /v1/flows/templates/{template_id}/runs`. Versions built on models that are not available to you through the API are left out, so `versions` is empty when none of a template's published versions is runnable through this API. Templates shared with you by link, or published to Explore from another workspace, are not listed but can still be fetched and run by `template_id`.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().templates().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListTemplatesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.templates.get(templateId, requestParameters) -> TemplateSummary</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve one flows template, together with each runnable version's inputs and outputs. `versions` is empty when no published version is runnable through this API. Works for any template you can open, including templates shared with you by link or published to Explore from another workspace, which `GET /v1/flows/templates` does not list.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().templates().get(
    templateId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**templateId:** `String` — The ID of the template, as shown in the ElevenLabs app or by `GET /v1/flows/templates`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetTemplatesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Flows Templates Runs

<details><summary><code>client.flows.templates.runs.list(templateId, requestParameters) -> TemplateRunListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List this template's runs created through this API, newest first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().templates().runs().list(
    templateId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**templateId:** `String` — The ID of the template, as shown in the ElevenLabs app or by `GET /v1/flows/templates`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListRunsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.templates.runs.create(templateId, templateRunCreateRequest) -> TemplateRunResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Start a run of a flows template. Pass `version_id` to pin a snapshot, or omit it / pass `latest` to run the latest published version. Set input values under `inputs`, keyed by input port id. The response is the run in its initial state, with every output already listed under its port id in `outputs`. Include `webhook` to receive a `flows_template_run` event carrying the finished run once its `status` is `completed` or `failed`; this is the recommended way to wait. Without one, fetch `GET /v1/flows/templates/{template_id}/runs/{run_id}` at a modest interval until the `status` is terminal.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().templates().runs().create(
    templateId,
    templateRunCreateRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**templateId:** `String` — The ID of the template, as shown in the ElevenLabs app or by `GET /v1/flows/templates`.

</dd>
</dl>

<dl>
<dd>

**templateRunCreateRequest:** `TemplateRunCreateRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.flows.templates.runs.get(templateId, runId, requestParameters) -> TemplateRunResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a template run: its `status`, rolled up from its outputs, and each output's own status and download URL once completed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.flows().templates().runs().get(
    templateId,
    runId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**templateId:** `String` — The ID of the template, as shown in the ElevenLabs app or by `GET /v1/flows/templates`.

</dd>
</dl>

<dl>
<dd>

**runId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetRunsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## TextToDialogue

<details><summary><code>client.textToDialogue.convert(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts a list of text and voice ID pairs into speech (dialogue) and returns audio.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToDialogue().convert(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyTextToDialogueMultiVoiceV1TextToDialoguePost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToDialogue.stream(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts a list of text and voice ID pairs into speech (dialogue) and returns an audio stream.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToDialogue().stream(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyTextToDialogueMultiVoiceStreamingV1TextToDialogueStreamPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToDialogue.streamWithTimestamps(requestParameters) -> Iterable&lt;StreamingAudioChunkWithTimestampsAndVoiceSegmentsResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Converts a list of text and voice ID pairs into speech (dialogue) and returns a stream of JSON blobs containing audio as a base64 encoded string and timestamps

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToDialogue().streamWithTimestamps(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyTextToDialogueStreamWithTimestamps` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToDialogue.convertWithTimestamps(requestParameters) -> AudioWithTimestampsAndVoiceSegmentsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Generate dialogue from text with precise character-level timing information for audio-text synchronization.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToDialogue().convertWithTimestamps(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyTextToDialogueFullWithTimestamps` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## SpeechToSpeech

<details><summary><code>client.speechToSpeech.convert(voiceId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Transform audio from one voice to another. Maintain full control over emotion, timing and delivery.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechToSpeech().convert(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. Use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodySpeechToSpeechV1SpeechToSpeechVoiceIdPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechToSpeech.stream(voiceId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream audio from one voice to another. Maintain full control over emotion, timing and delivery.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechToSpeech().stream(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — ID of the voice to be used. Use the [Get voices](/docs/api-reference/voices/search) endpoint list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodySpeechToSpeechStreamingV1SpeechToSpeechVoiceIdStreamPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## TextToVoice

<details><summary><code>client.textToVoice.create(bodyCreateANewVoiceFromVoicePreviewV1TextToVoicePost) -> Voice</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a voice from previously generated voice preview. This endpoint should be called after you fetched a generated_voice_id using POST /v1/text-to-voice/design or POST /v1/text-to-voice/:voice_id/remix.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToVoice().create(bodyCreateANewVoiceFromVoicePreviewV1TextToVoicePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateANewVoiceFromVoicePreviewV1TextToVoicePost:** `BodyCreateANewVoiceFromVoicePreviewV1TextToVoicePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToVoice.design(requestParameters) -> VoiceDesignPreviewResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Design a voice via a prompt. This method returns a list of voice previews. Each preview has a generated_voice_id and a sample of the voice as base64 encoded mp3 audio. To create a voice use the generated_voice_id of the preferred preview with the /v1/text-to-voice endpoint.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToVoice().design(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `VoiceDesignRequestModel` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.textToVoice.remix(voiceId, requestParameters) -> VoiceDesignPreviewResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remix an existing voice via a prompt. This method returns a list of voice previews. Each preview has a generated_voice_id and a sample of the voice as base64 encoded mp3 audio. To create a voice use the generated_voice_id of the preferred preview with the /v1/text-to-voice endpoint.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToVoice().remix(
    voiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**voiceId:** `String` — Voice ID to be used, you can use https://api.elevenlabs.io/v1/voices to list all the available voices.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `VoiceRemixRequestModel` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## TextToVoice Preview

<details><summary><code>client.textToVoice.preview.stream(generatedVoiceId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream a voice preview that was created via the /v1/text-to-voice/design endpoint.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.textToVoice().preview().stream(
    generatedVoiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**generatedVoiceId:** `String` — The generated_voice_id to stream.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StreamPreviewRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## User

<details><summary><code>client.user.get() -> User</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets information about the user

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.user().get();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## User Subscription

<details><summary><code>client.user.subscription.get() -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets extended information about the users subscription

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.user().subscription().get();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio

<details><summary><code>client.studio.createPodcast(requestParameters) -> PodcastProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create and auto-convert a podcast project. Currently, the LLM cost is covered by us but you will still be charged for the audio generation. In the future, you will be charged for both the LLM and audio generation costs.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().createPodcast(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyCreatePodcastV1StudioPodcastsPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects

<details><summary><code>client.studio.projects.list() -> GetProjectsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of your Studio projects with metadata.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.create(bodyCreateStudioProjectV1StudioProjectsPost) -> AddProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new Studio project, it can be either initialized as blank, from a document or from a URL.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().create(bodyCreateStudioProjectV1StudioProjectsPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateStudioProjectV1StudioProjectsPost:** `BodyCreateStudioProjectV1StudioProjectsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.get(projectId, requestParameters) -> ProjectExtendedResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns information about a specific Studio project. This endpoint returns more detailed information about a project than `GET /v1/studio`.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().get(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetProjectsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.update(projectId, bodyUpdateStudioProjectV1StudioProjectsProjectIdPost) -> EditProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates the specified Studio project by setting the values of the parameters passed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().update(
    projectId,
    bodyUpdateStudioProjectV1StudioProjectsProjectIdPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateStudioProjectV1StudioProjectsProjectIdPost:** `BodyUpdateStudioProjectV1StudioProjectsProjectIdPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.delete(projectId, requestParameters) -> DeleteProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes a Studio project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().delete(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteProjectsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.convert(projectId, requestParameters) -> ConvertProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Starts conversion of a Studio project and all of its chapters.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().convert(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ConvertProjectsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.getMutedTracks(projectId, requestParameters) -> ProjectMutedTracksResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of chapter IDs that have muted tracks in a project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().getMutedTracks(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetMutedTracksProjectsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects PronunciationDictionaries

<details><summary><code>client.studio.projects.pronunciationDictionaries.create(projectId, bodyCreatePronunciationDictionariesV1StudioProjectsProjectIdPronunciationDictionariesPost) -> CreatePronunciationDictionaryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a set of pronunciation dictionaries acting on a project. This will automatically mark text within this project as requiring reconverting where the new dictionary would apply or the old one no longer does.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().pronunciationDictionaries().create(
    projectId,
    bodyCreatePronunciationDictionariesV1StudioProjectsProjectIdPronunciationDictionariesPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**bodyCreatePronunciationDictionariesV1StudioProjectsProjectIdPronunciationDictionariesPost:** `BodyCreatePronunciationDictionariesV1StudioProjectsProjectIdPronunciationDictionariesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects Content

<details><summary><code>client.studio.projects.content.update(projectId, bodyUpdateStudioProjectContentV1StudioProjectsProjectIdContentPost) -> EditProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates Studio project content.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().content().update(
    projectId,
    bodyUpdateStudioProjectContentV1StudioProjectsProjectIdContentPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateStudioProjectContentV1StudioProjectsProjectIdContentPost:** `BodyUpdateStudioProjectContentV1StudioProjectsProjectIdContentPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects Snapshots

<details><summary><code>client.studio.projects.snapshots.list(projectId, requestParameters) -> ProjectSnapshotsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves a list of snapshots for a Studio project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().snapshots().list(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListSnapshotsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.snapshots.get(projectId, projectSnapshotId, requestParameters) -> ProjectSnapshotExtendedResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the project snapshot.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().snapshots().get(
    projectId,
    projectSnapshotId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**projectSnapshotId:** `String` — The ID of the Studio project snapshot.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSnapshotsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.snapshots.stream(projectId, projectSnapshotId, bodyStreamStudioProjectAudioV1StudioProjectsProjectIdSnapshotsProjectSnapshotIdStreamPost) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream the audio from a Studio project snapshot.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().snapshots().stream(
    projectId,
    projectSnapshotId,
    bodyStreamStudioProjectAudioV1StudioProjectsProjectIdSnapshotsProjectSnapshotIdStreamPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**projectSnapshotId:** `String` — The ID of the Studio project snapshot.

</dd>
</dl>

<dl>
<dd>

**bodyStreamStudioProjectAudioV1StudioProjectsProjectIdSnapshotsProjectSnapshotIdStreamPost:** `BodyStreamStudioProjectAudioV1StudioProjectsProjectIdSnapshotsProjectSnapshotIdStreamPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.snapshots.streamArchive(projectId, projectSnapshotId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a compressed archive of the Studio project's audio.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().snapshots().streamArchive(
    projectId,
    projectSnapshotId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**projectSnapshotId:** `String` — The ID of the Studio project snapshot.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `StreamArchiveSnapshotsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects Chapters

<details><summary><code>client.studio.projects.chapters.list(projectId, requestParameters) -> GetChaptersResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of a Studio project's chapters.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().list(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListChaptersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.create(projectId, bodyCreateChapterV1StudioProjectsProjectIdChaptersPost) -> AddChapterResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new chapter either as blank or from a URL.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().create(
    projectId,
    bodyCreateChapterV1StudioProjectsProjectIdChaptersPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**bodyCreateChapterV1StudioProjectsProjectIdChaptersPost:** `BodyCreateChapterV1StudioProjectsProjectIdChaptersPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.get(projectId, chapterId, requestParameters) -> ChapterWithContentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns information about a specific chapter.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().get(
    projectId,
    chapterId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetChaptersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.update(projectId, chapterId, bodyUpdateChapterV1StudioProjectsProjectIdChaptersChapterIdPost) -> EditChapterResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates a chapter.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().update(
    projectId,
    chapterId,
    bodyUpdateChapterV1StudioProjectsProjectIdChaptersChapterIdPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateChapterV1StudioProjectsProjectIdChaptersChapterIdPost:** `BodyUpdateChapterV1StudioProjectsProjectIdChaptersChapterIdPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.delete(projectId, chapterId, requestParameters) -> DeleteChapterResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Deletes a chapter.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().delete(
    projectId,
    chapterId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteChaptersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.convert(projectId, chapterId, requestParameters) -> ConvertChapterResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Starts conversion of a specific chapter.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().convert(
    projectId,
    chapterId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ConvertChaptersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Studio Projects Chapters Snapshots

<details><summary><code>client.studio.projects.chapters.snapshots.list(projectId, chapterId, requestParameters) -> ChapterSnapshotsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets information about all the snapshots of a chapter. Each snapshot can be downloaded as audio. Whenever a chapter is converted a snapshot will automatically be created.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().snapshots().list(
    projectId,
    chapterId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.studio.projects.chapters.snapshots.requests.ListSnapshotsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.snapshots.get(projectId, chapterId, chapterSnapshotId, requestParameters) -> ChapterSnapshotExtendedResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the chapter snapshot.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().snapshots().get(
    projectId,
    chapterId,
    chapterSnapshotId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter.

</dd>
</dl>

<dl>
<dd>

**chapterSnapshotId:** `String` — The ID of the chapter snapshot.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.studio.projects.chapters.snapshots.requests.GetSnapshotsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.studio.projects.chapters.snapshots.stream(projectId, chapterId, chapterSnapshotId, bodyStreamChapterAudioV1StudioProjectsProjectIdChaptersChapterIdSnapshotsChapterSnapshotIdStreamPost) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream the audio from a chapter snapshot. Use `GET /v1/studio/projects/{project_id}/chapters/{chapter_id}/snapshots` to return the snapshots of a chapter.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.studio().projects().chapters().snapshots().stream(
    projectId,
    chapterId,
    chapterSnapshotId,
    bodyStreamChapterAudioV1StudioProjectsProjectIdChaptersChapterIdSnapshotsChapterSnapshotIdStreamPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**chapterId:** `String` — The ID of the chapter to be used. You can use the [List project chapters](/docs/api-reference/studio/get-chapters) endpoint to list all the available chapters.

</dd>
</dl>

<dl>
<dd>

**chapterSnapshotId:** `String` — The ID of the chapter snapshot to be used. You can use the [List project chapter snapshots](/docs/api-reference/studio/get-snapshots) endpoint to list all the available snapshots.

</dd>
</dl>

<dl>
<dd>

**bodyStreamChapterAudioV1StudioProjectsProjectIdChaptersChapterIdSnapshotsChapterSnapshotIdStreamPost:** `BodyStreamChapterAudioV1StudioProjectsProjectIdChaptersChapterIdSnapshotsChapterSnapshotIdStreamPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## PronunciationDictionaries

<details><summary><code>client.pronunciationDictionaries.createFromFile(addPronunciationDictionaryRequest) -> AddPronunciationDictionaryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new pronunciation dictionary from a lexicon .PLS file

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().createFromFile(addPronunciationDictionaryRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**addPronunciationDictionaryRequest:** `AddPronunciationDictionaryRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.createFromRules(bodyAddAPronunciationDictionaryV1PronunciationDictionariesAddFromRulesPost) -> AddPronunciationDictionaryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new pronunciation dictionary from provided rules.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().createFromRules(bodyAddAPronunciationDictionaryV1PronunciationDictionariesAddFromRulesPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyAddAPronunciationDictionaryV1PronunciationDictionariesAddFromRulesPost:** `BodyAddAPronunciationDictionaryV1PronunciationDictionariesAddFromRulesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.get(pronunciationDictionaryId, requestParameters) -> GetPronunciationDictionaryWithRulesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get metadata for a pronunciation dictionary

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().get(
    pronunciationDictionaryId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pronunciationDictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetPronunciationDictionariesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.update(pronunciationDictionaryId, bodyUpdatePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdPatch) -> GetPronunciationDictionaryMetadataResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Partially update the pronunciation dictionary without changing the version

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().update(
    pronunciationDictionaryId,
    bodyUpdatePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pronunciationDictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**bodyUpdatePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdPatch:** `BodyUpdatePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.download(dictionaryId, versionId, requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a PLS file with a pronunciation dictionary version rules

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().download(
    dictionaryId,
    versionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**dictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**versionId:** `String` — The id of the pronunciation dictionary version

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DownloadPronunciationDictionariesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.list(requestParameters) -> GetPronunciationDictionariesMetadataResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a list of the pronunciation dictionaries you have access to and their metadata

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListPronunciationDictionariesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## PronunciationDictionaries Rules

<details><summary><code>client.pronunciationDictionaries.rules.set(pronunciationDictionaryId, bodySetRulesOnThePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdSetRulesPost) -> PronunciationDictionaryRulesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replaces all existing rules on the pronunciation dictionary with the provided ones.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().rules().set(
    pronunciationDictionaryId,
    bodySetRulesOnThePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdSetRulesPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pronunciationDictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**bodySetRulesOnThePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdSetRulesPost:** `BodySetRulesOnThePronunciationDictionaryV1PronunciationDictionariesPronunciationDictionaryIdSetRulesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.rules.add(pronunciationDictionaryId, pronunciationDictionary) -> PronunciationDictionaryRulesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add rules to the pronunciation dictionary. If a rule with the same string_to_replace already exists, it will be replaced.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().rules().add(
    pronunciationDictionaryId,
    pronunciationDictionary
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pronunciationDictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**pronunciationDictionary:** `PronunciationDictionary` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.pronunciationDictionaries.rules.remove(pronunciationDictionaryId, removePronunciationDictionaryRulesRequest) -> PronunciationDictionaryRulesResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove rules from the pronunciation dictionary

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.pronunciationDictionaries().rules().remove(
    pronunciationDictionaryId,
    removePronunciationDictionaryRulesRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pronunciationDictionaryId:** `String` — The id of the pronunciation dictionary

</dd>
</dl>

<dl>
<dd>

**removePronunciationDictionaryRulesRequest:** `RemovePronunciationDictionaryRulesRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## SpeechToText

<details><summary><code>client.speechToText.convert(requestParameters) -> ConvertSpeechToTextResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Transcribe an audio or video file. If webhook is set to true, the request will be processed asynchronously and results sent to configured webhooks. When use_multi_channel is true and the provided audio has multiple channels, a 'transcripts' object with separate transcripts for each channel is returned; set multichannel_output_style='combined' to instead receive a single transcript with all channels merged and sorted by time. Otherwise, returns a single transcript. The optional webhook_metadata parameter allows you to attach custom data that will be included in webhook responses for request correlation and tracking.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechToText().convert(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ConvertSpeechToTextRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## SpeechToText Transcripts

<details><summary><code>client.speechToText.transcripts.get(transcriptionId, requestParameters) -> GetTranscriptsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a previously generated transcript by its ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechToText().transcripts().get(
    transcriptionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**transcriptionId:** `String` — The unique ID of the transcript to retrieve

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.speechtotext.transcripts.requests.GetTranscriptsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechToText.transcripts.delete(transcriptionId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a previously generated transcript by its ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechToText().transcripts().delete(
    transcriptionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**transcriptionId:** `String` — The unique ID of the transcript to delete

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteTranscriptsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Models

<details><summary><code>client.models.list() -> List&lt;Model&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets a list of available models.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.models().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## AudioNative

<details><summary><code>client.audioNative.create(bodyCreatesAudioNativeEnabledProjectV1AudioNativePost) -> AudioNativeCreateProjectResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates Audio Native enabled project, optionally starts conversion and returns project ID and embeddable HTML snippet.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioNative().create(bodyCreatesAudioNativeEnabledProjectV1AudioNativePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreatesAudioNativeEnabledProjectV1AudioNativePost:** `BodyCreatesAudioNativeEnabledProjectV1AudioNativePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioNative.getSettings(projectId, requestParameters) -> GetAudioNativeProjectSettingsResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get player settings for the specific project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioNative().getSettings(
    projectId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the Studio project.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSettingsAudioNativeRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioNative.update(projectId, bodyUpdateAudioNativeProjectContentV1AudioNativeProjectIdContentPost) -> AudioNativeEditContentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates content for the specific AudioNative Project.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioNative().update(
    projectId,
    bodyUpdateAudioNativeProjectContentV1AudioNativeProjectIdContentPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**projectId:** `String` — The ID of the project to be used. You can use the [List projects](/docs/api-reference/studio/get-projects) endpoint to list all the available projects.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateAudioNativeProjectContentV1AudioNativeProjectIdContentPost:** `BodyUpdateAudioNativeProjectContentV1AudioNativeProjectIdContentPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.audioNative.updateContentFromUrl(bodyUpdateAudioNativeContentFromUrlV1AudioNativeContentPost) -> AudioNativeEditContentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Finds an AudioNative project matching the provided URL, extracts content from the URL, updates the project content, and queues it for conversion and auto-publishing.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.audioNative().updateContentFromUrl(bodyUpdateAudioNativeContentFromUrlV1AudioNativeContentPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyUpdateAudioNativeContentFromUrlV1AudioNativeContentPost:** `BodyUpdateAudioNativeContentFromUrlV1AudioNativeContentPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## ContentProvenance

<details><summary><code>client.contentProvenance.watermark(bodyCheckWatermarkV1ContentProvenanceWatermarkPost) -> WatermarkCheck</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Check an audio file for an embedded ElevenLabs watermark. This endpoint is in beta and may change.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.contentProvenance().watermark(bodyCheckWatermarkV1ContentProvenanceWatermarkPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCheckWatermarkV1ContentProvenanceWatermarkPost:** `BodyCheckWatermarkV1ContentProvenanceWatermarkPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Receptionists

<details><summary><code>client.reception.receptionists.list(requestParameters) -> List&lt;ReceptionCustomerFacingAgentResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List every customer-facing receptionist for the workspace.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListReceptionistsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.update(agentId, requestParameters) -> ReceptionCustomerFacingAgentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a receptionist's display name, config and/or widget appearance.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().update(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateCustomerFacingAgentRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.activate(agentId, requestParameters) -> ReceptionCustomerFacingAgentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Re-activate a deactivated receptionist. Fails with `quota_exceeded` when the plan has no free receptionist slot.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().activate(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ActivateReceptionistsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Receptionists Rules

<details><summary><code>client.reception.receptionists.rules.create(agentId, requestParameters) -> AgentRule</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a behavioral rule to the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().rules().create(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionCreateAgentRuleRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.rules.update(agentId, ruleId, requestParameters) -> AgentRule</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replace an existing rule on the named receptionist. Send every field: an omitted `priority` resets to `strict`.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().rules().update(
    agentId,
    ruleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**ruleId:** `String` — ID of the rule. Returned when you add one, and listed in the receptionist's `config.agent_rules`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateAgentRuleRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.rules.delete(agentId, ruleId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a rule from the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().rules().delete(
    agentId,
    ruleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**ruleId:** `String` — ID of the rule. Returned when you add one, and listed in the receptionist's `config.agent_rules`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteRulesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Receptionists Procedures

<details><summary><code>client.reception.receptionists.procedures.list(agentId, requestParameters) -> List&lt;ReceptionProcedureResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the procedures (SOPs) on the named receptionist, with the tools each one is missing.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().procedures().list(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.reception.receptionists.procedures.requests.ListProceduresRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.procedures.create(agentId, requestParameters) -> ReceptionProcedure</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a procedure (SOP) to the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().procedures().create(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionCreateProcedureRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.procedures.update(agentId, procedureId, requestParameters) -> ReceptionProcedure</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replace an existing procedure on the named receptionist. Send every field: an omitted `body` resets to empty.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().procedures().update(
    agentId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — ID of the procedure. Returned when you add one; list them with `GET /v1/reception/customer-facing-agents/{agent_id}/procedures`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateProcedureRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.procedures.delete(agentId, procedureId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a procedure from the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().procedures().delete(
    agentId,
    procedureId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**procedureId:** `String` — ID of the procedure. Returned when you add one; list them with `GET /v1/reception/customer-facing-agents/{agent_id}/procedures`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteProceduresRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Receptionists TransferRules

<details><summary><code>client.reception.receptionists.transferRules.create(agentId, requestParameters) -> ReceptionTransferRule</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a phone forwarding rule to the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().transferRules().create(
    agentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionCreateTransferRuleRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.transferRules.update(agentId, ruleId, requestParameters) -> ReceptionTransferRule</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replace an existing transfer rule on the named receptionist. Send every field: an omitted `post_dial_digits` clears it.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().transferRules().update(
    agentId,
    ruleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**ruleId:** `String` — ID of the transfer rule. Returned when you add one, and listed in the receptionist's `config.transfer_rules`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateTransferRuleRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.receptionists.transferRules.delete(agentId, ruleId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a transfer rule from the named receptionist.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().receptionists().transferRules().delete(
    agentId,
    ruleId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**agentId:** `String` — ID of the receptionist. List them with `GET /v1/reception/customer-facing-agents`.

</dd>
</dl>

<dl>
<dd>

**ruleId:** `String` — ID of the transfer rule. Returned when you add one, and listed in the receptionist's `config.transfer_rules`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteTransferRulesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Team Members

<details><summary><code>client.reception.team.members.list(requestParameters) -> List&lt;ReceptionTeamMemberResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's members, suspended members last.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().members().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListMembersRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.team.members.updateRole(userId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Promote a member to admin or demote an admin to member.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().members().updateRole(
    userId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**userId:** `String` — User ID of the member to update

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `BodySmbUpdateTeamMemberRole` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.team.members.remove(userId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a member from the workspace. Their seat goes to a suspended member, if any.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().members().remove(
    userId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**userId:** `String` — User ID of the member to remove

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RemoveMembersRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Team Invites

<details><summary><code>client.reception.team.invites.list(requestParameters) -> List&lt;ReceptionTeamInviteResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's pending invites.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().invites().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListInvitesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.team.invites.create(requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Email an invite to join the workspace. A pending invite takes a seat, so this is refused once members and invites reach the plan's team limit.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().invites().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `CreateTeamInviteRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.team.invites.cancel(requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Cancel a pending invite, freeing its seat.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().team().invites().cancel(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodySmbCancelTeamInvite` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception PhoneNumbers

<details><summary><code>client.reception.phoneNumbers.list(requestParameters) -> List&lt;ReceptionPhoneAssignment&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's phone numbers and the receptionist each one rings. A number with no `customer_facing_agent_id` is held by the workspace but not attached to a receptionist yet.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().phoneNumbers().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.reception.phonenumbers.requests.ListPhoneNumbersRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.phoneNumbers.create(requestParameters) -> ReceptionPhoneAssignment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Give a receptionist a phone number. A number the workspace already holds but has not attached is used first. Otherwise, in the US and Canada, a number with one of the requested `area_codes` is taken from the pool or bought, falling back to any number in the business's country. In countries that require paperwork, the number is issued right away but takes public calls only once the workspace's own registration is approved (see `accepts_public_calls`). Counts toward the plan's phone number limit, and free plans can take only a few new numbers a day. Returns 404 when no number is available and 403 `telephony_managed_by_reseller` when the reseller supplies the workspace's numbers from its own Twilio account or SIP trunk and none is free.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().phoneNumbers().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `CreatePhoneAssignmentRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.phoneNumbers.update(phoneAssignmentId, requestParameters) -> ReceptionPhoneAssignment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Move a phone number to a different receptionist in the same workspace, which answers its calls from then on. Returns 409 `phone_number_not_attached` for a number no receptionist answers yet (attach it with `POST /v1/reception/phone-numbers/assignments`).

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().phoneNumbers().update(
    phoneAssignmentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneAssignmentId:** `String` — ID of the phone number assignment. Returned when you assign a number; list them with `GET /v1/reception/phone-numbers/assignments`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReassignPhoneRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.phoneNumbers.delete(phoneAssignmentId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a phone number from the workspace. A pool number goes back to the pool it came from. A number the business brought from its own Twilio account, or one issued in a country that requires paperwork, is released at Twilio and cannot be recovered. Returns 403 `telephony_managed_by_reseller` when the reseller supplies the workspace's numbers from its own Twilio account or SIP trunk.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().phoneNumbers().delete(
    phoneAssignmentId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**phoneAssignmentId:** `String` — ID of the phone number assignment. Returned when you assign a number; list them with `GET /v1/reception/phone-numbers/assignments`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.reception.phonenumbers.requests.DeletePhoneNumbersRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Assets

<details><summary><code>client.reception.assets.list(requestParameters) -> List&lt;ReceptionAssetResource&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's bookable assets, such as rooms or equipment.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().assets().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAssetsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.assets.create(requestParameters) -> ReceptionAssetResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a bookable asset, such as a room or a piece of equipment.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().assets().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionCreateAssetRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.assets.get(assetId, requestParameters) -> ReceptionAssetResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get an asset by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().assets().get(
    assetId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**assetId:** `String` — ID of the asset. Returned when you create one; list them with `GET /v1/reception/assets`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetAssetsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.assets.update(assetId, requestParameters) -> ReceptionAssetResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an asset. Only the fields sent are changed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().assets().update(
    assetId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**assetId:** `String` — ID of the asset. Returned when you create one; list them with `GET /v1/reception/assets`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateAssetRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.assets.delete(assetId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an asset.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().assets().delete(
    assetId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**assetId:** `String` — ID of the asset. Returned when you create one; list them with `GET /v1/reception/assets`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteAssetsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Services

<details><summary><code>client.reception.services.list(requestParameters) -> List&lt;ReceptionService&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all services for the current workspace with optional filters

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListServicesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.services.create(requestParameters) -> ReceptionService</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new service

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionCreateServiceRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.services.get(serviceId, requestParameters) -> ReceptionService</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a specific service by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().get(
    serviceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceId:** `String` — ID of the service. Returned when you create one; list them with `GET /v1/reception/services`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetServicesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.services.update(serviceId, requestParameters) -> ReceptionServiceUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing service. Only the fields sent are changed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().update(
    serviceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceId:** `String` — ID of the service. Returned when you create one; list them with `GET /v1/reception/services`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateServiceRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.services.delete(serviceId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a service

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().delete(
    serviceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceId:** `String` — ID of the service. Returned when you create one; list them with `GET /v1/reception/services`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteServicesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.services.confirmUpdate(serviceId, requestParameters) -> ReceptionServiceUpdateResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Save a service update that changes the price of existing bookings. Optionally reprice future bookings.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().services().confirmUpdate(
    serviceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceId:** `String` — ID of the service. Returned when you create one; list them with `GET /v1/reception/services`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionConfirmServiceUpdateRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Products

<details><summary><code>client.reception.products.list(requestParameters) -> List&lt;ReceptionProduct&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace's products, optionally filtered by group or active state.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListProductsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.products.create(requestParameters) -> ReceptionProduct</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a product the receptionist can quote and take orders for. A product created with `track_quantity` starts with nothing on hand; add stock with the stock endpoints.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionCreateProductRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.products.get(productId, requestParameters) -> ReceptionProduct</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a product by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().get(
    productId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**productId:** `String` — ID of the product. Returned when you create one; list them with `GET /v1/reception/products`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetProductsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.products.update(productId, requestParameters) -> ReceptionProduct</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a product's catalog fields. Only the fields sent are changed. On-hand quantities are not part of this call; use the stock endpoints.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().update(
    productId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**productId:** `String` — ID of the product. Returned when you create one; list them with `GET /v1/reception/products`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateProductRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.products.delete(productId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a product.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().delete(
    productId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**productId:** `String` — ID of the product. Returned when you create one; list them with `GET /v1/reception/products`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteProductsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Products Stock

<details><summary><code>client.reception.products.stock.set(productId, requestParameters) -> ReceptionProduct</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Set the on-hand quantity of a counted product at one location. Rejected with a 409 if it changed since `expected_on_hand` was read.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().stock().set(
    productId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**productId:** `String` — ID of the product. Returned when you create one; list them with `GET /v1/reception/products`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateProductStockRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.products.stock.adjust(productId, requestParameters) -> ReceptionProduct</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add to or subtract from the on-hand quantity of a counted product at one location, without overwriting orders taken at the same time.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().products().stock().adjust(
    productId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**productId:** `String` — ID of the product. Returned when you create one; list them with `GET /v1/reception/products`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionAdjustProductStockRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Staff

<details><summary><code>client.reception.staff.list(requestParameters) -> List&lt;ReceptionStaffResource&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all staff for the current workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().staff().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListStaffRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.staff.create(requestParameters) -> ReceptionStaffResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new staff member

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().staff().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionCreateStaffRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.staff.get(staffId, requestParameters) -> ReceptionStaffResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a specific staff member by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().staff().get(
    staffId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**staffId:** `String` — ID of the staff member. Returned when you create one; list them with `GET /v1/reception/staff`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetStaffRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.staff.update(staffId, requestParameters) -> ReceptionStaffResource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing staff member. Only the fields sent are changed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().staff().update(
    staffId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**staffId:** `String` — ID of the staff member. Returned when you create one; list them with `GET /v1/reception/staff`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateStaffRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.staff.delete(staffId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a staff member

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().staff().delete(
    staffId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**staffId:** `String` — ID of the staff member. Returned when you create one; list them with `GET /v1/reception/staff`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteStaffRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Holidays

<details><summary><code>client.reception.holidays.list(requestParameters) -> List&lt;HolidayResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all holidays/time-off entries for the current workspace with optional filters

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().holidays().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListHolidaysRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.holidays.create(requestParameters) -> HolidayResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new holiday

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().holidays().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionCreateHolidayRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.holidays.get(holidayId, requestParameters) -> HolidayResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a specific holiday by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().holidays().get(
    holidayId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**holidayId:** `String` — ID of the holiday or time-off entry. Returned when you create one; list them with `GET /v1/reception/holidays`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetHolidaysRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.holidays.update(holidayId, requestParameters) -> HolidayResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing holiday. Only the fields sent are changed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().holidays().update(
    holidayId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**holidayId:** `String` — ID of the holiday or time-off entry. Returned when you create one; list them with `GET /v1/reception/holidays`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateHolidayRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.holidays.delete(holidayId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a holiday

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().holidays().delete(
    holidayId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**holidayId:** `String` — ID of the holiday or time-off entry. Returned when you create one; list them with `GET /v1/reception/holidays`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteHolidaysRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Locations

<details><summary><code>client.reception.locations.list(requestParameters) -> List&lt;ReceptionLocationResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all business locations.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().locations().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListLocationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.locations.create(requestParameters) -> ReceptionLocationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a new business location.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().locations().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionLocationRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.locations.get(locationId, requestParameters) -> ReceptionLocationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a single location by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().locations().get(
    locationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**locationId:** `String` — ID of the location. Returned when you create one; list them with `GET /v1/reception/locations`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetLocationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.locations.update(locationId, requestParameters) -> ReceptionLocationResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing location. Only the fields sent are changed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().locations().update(
    locationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**locationId:** `String` — ID of the location. Returned when you create one; list them with `GET /v1/reception/locations`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ReceptionLocationRequestPatch` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.locations.delete(locationId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a location.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().locations().delete(
    locationId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**locationId:** `String` — ID of the location. Returned when you create one; list them with `GET /v1/reception/locations`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteLocationsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception BusinessProfile

<details><summary><code>client.reception.businessProfile.get(requestParameters) -> ReceptionAgentInfoResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get the business profile the receptionists answer from, with the current time in the business's timezone.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().businessProfile().get(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `GetBusinessProfileRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.businessProfile.update(requestParameters) -> ReceptionAgentInfoResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the business profile. Every receptionist is updated to match.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().businessProfile().update(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ReceptionUpdateAgentInfoRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception KnowledgeSources

<details><summary><code>client.reception.knowledgeSources.list(requestParameters) -> List&lt;ReceptionKnowledgeSource&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all knowledge sources for the current workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().knowledgeSources().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListKnowledgeSourcesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.knowledgeSources.upload(requestParameters) -> ReceptionKnowledgeSource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Upload a file as a knowledge source

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().knowledgeSources().upload(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `UploadKnowledgeSourcesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.knowledgeSources.get(sourceId, requestParameters) -> ReceptionKnowledgeSource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a specific knowledge source by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().knowledgeSources().get(
    sourceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**sourceId:** `String` — ID of the knowledge source. Returned when you add one; list them with `GET /v1/reception/knowledge-sources`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetKnowledgeSourcesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.knowledgeSources.delete(sourceId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a knowledge source

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().knowledgeSources().delete(
    sourceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**sourceId:** `String` — ID of the knowledge source. Returned when you add one; list them with `GET /v1/reception/knowledge-sources`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteKnowledgeSourcesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.knowledgeSources.addUrl(requestParameters) -> ReceptionKnowledgeSource</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Add a URL as a knowledge source

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().knowledgeSources().addUrl(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `AddUrlRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Workspaces

<details><summary><code>client.reception.workspaces.list(requestParameters) -> ResellerWorkspacesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List workspaces attributed to the caller's reseller, with cursor-based pagination

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().workspaces().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListWorkspacesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.workspaces.create(receptionProvisionResellerWorkspaceRequest) -> ProvisionResellerWorkspaceResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a workspace for one of your customers, optionally with a tier, a phone number and team invites.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().workspaces().create(receptionProvisionResellerWorkspaceRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**receptionProvisionResellerWorkspaceRequest:** `ReceptionProvisionResellerWorkspaceRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.workspaces.completeOnboarding(workspaceId, requestParameters) -> ResellerWorkspaceOnboardingResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Finish setting up the workspace: create its receptionist and assistant from the business profile, reusing a receptionist from a setup already started, and connect your phone number, if you supply one. Safe to retry.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().workspaces().completeOnboarding(
    workspaceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CompleteOnboardingWorkspacesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Subscriptions

<details><summary><code>client.reception.subscriptions.assign(workspaceId, assignResellerTierRequest) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Subscribe a reseller-attributed workspace to one of the reseller's tiers

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().subscriptions().assign(
    workspaceId,
    assignResellerTierRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**assignResellerTierRequest:** `AssignResellerTierRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.subscriptions.change(workspaceId, changeResellerTierRequest) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Move a reseller-attributed workspace between the reseller's tiers. Upgrades apply immediately; downgrades and lateral moves take effect at the end of the billing period.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().subscriptions().change(
    workspaceId,
    changeResellerTierRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**changeResellerTierRequest:** `ChangeResellerTierRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.subscriptions.unassign(workspaceId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

End a reseller-attributed workspace's subscription at the end of the current billing period

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().subscriptions().unassign(
    workspaceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `UnassignSubscriptionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.subscriptions.restore(workspaceId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Undo a pending unassignment so the subscription renews normally

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().subscriptions().restore(
    workspaceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RestoreSubscriptionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.subscriptions.cancelPendingDowngrade(workspaceId, requestParameters) -> Map&lt;String, String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Drop a scheduled downgrade, leaving the workspace on its current tier

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().subscriptions().cancelPendingDowngrade(
    workspaceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**workspaceId:** `String` — ID of the customer workspace. Returned when you provision one; list them with `GET /v1/reception/reseller/workspaces`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CancelPendingDowngradeSubscriptionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Tiers

<details><summary><code>client.reception.tiers.list() -> List&lt;ResellerTierResponse&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

The tiers you can sell, cheapest first. The first is the trial. Retired tiers are left out.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().tiers().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Reception Billing

<details><summary><code>client.reception.billing.listInvoices(requestParameters) -> ResellerInvoicesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the invoices charged to the reseller's card, newest first, one per workspace invoice

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().billing().listInvoices(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListInvoicesBillingRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.billing.getInvoice(invoiceId, requestParameters) -> ResellerInvoiceDetail</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get one invoice charged to the reseller's card, broken down into the workspace invoice's lines

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().billing().getInvoice(
    invoiceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**invoiceId:** `String` — ID of the invoice. List them with `GET /v1/reception/reseller/billing/invoices`.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetInvoiceBillingRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.reception.billing.listUpcomingPayments() -> ResellerUpcomingPaymentsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the next workspace renewals the reseller's card will be charged for, soonest first

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reception().billing().listUpcomingPayments();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspaces ApiKeys

<details><summary><code>client.workspaces.apiKeys.disable(requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Disable the API key used to authenticate this request. Requires the query parameter `api_key_name=self` as an explicit confirmation.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspaces().apiKeys().disable(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `DisableApiKeysRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## ServiceAccounts

<details><summary><code>client.serviceAccounts.list() -> WorkspaceServiceAccountListResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all service accounts in the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.serviceAccounts.create(bodyCreateServiceAccountV1ServiceAccountsPost) -> WorkspaceCreateServiceAccountResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new service account in the workspace. By default, a workspace can have up to 20 service accounts. Enterprise customers may request an increase to this limit.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().create(bodyCreateServiceAccountV1ServiceAccountsPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateServiceAccountV1ServiceAccountsPost:** `BodyCreateServiceAccountV1ServiceAccountsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## ServiceAccounts ApiKeys

<details><summary><code>client.serviceAccounts.apiKeys.list(serviceAccountUserId, requestParameters) -> WorkspaceApiKeyListResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all API keys for a service account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().apiKeys().list(
    serviceAccountUserId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceAccountUserId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListApiKeysRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.serviceAccounts.apiKeys.create(serviceAccountUserId, bodyCreateServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysPost) -> WorkspaceCreateApiKeyResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new API key for a service account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().apiKeys().create(
    serviceAccountUserId,
    bodyCreateServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceAccountUserId:** `String`

</dd>
</dl>

<dl>
<dd>

**bodyCreateServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysPost:** `BodyCreateServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.serviceAccounts.apiKeys.update(serviceAccountUserId, apiKeyId, bodyEditServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysApiKeyIdPatch) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an existing API key for a service account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().apiKeys().update(
    serviceAccountUserId,
    apiKeyId,
    bodyEditServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysApiKeyIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceAccountUserId:** `String`

</dd>
</dl>

<dl>
<dd>

**apiKeyId:** `String`

</dd>
</dl>

<dl>
<dd>

**bodyEditServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysApiKeyIdPatch:** `BodyEditServiceAccountApiKeyV1ServiceAccountsServiceAccountUserIdApiKeysApiKeyIdPatch` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.serviceAccounts.apiKeys.delete(serviceAccountUserId, apiKeyId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an existing API key for a service account

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().apiKeys().delete(
    serviceAccountUserId,
    apiKeyId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceAccountUserId:** `String`

</dd>
</dl>

<dl>
<dd>

**apiKeyId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteApiKeysRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.serviceAccounts.apiKeys.disable(serviceAccountUserId, apiKeyId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Disable an existing API key for a service account without changing its other settings. This stays available after a workspace downgrades off a plan that includes service accounts. Turning the key back on still requires that plan.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.serviceAccounts().apiKeys().disable(
    serviceAccountUserId,
    apiKeyId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serviceAccountUserId:** `String`

</dd>
</dl>

<dl>
<dd>

**apiKeyId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.serviceaccounts.apikeys.requests.DisableApiKeysRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace

<details><summary><code>client.workspace.setThirdPartyDisablingPolicy(bodySetWorkspaceThirdPartyDisablingPolicyV1WorkspacesApiKeysThirdPartyDisablingPost) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Set the workspace-wide Third-Party Disabling policy. When set, it forces, for every API key in the workspace, whether the holder of a key (potentially a third party who found it) may disable it via the self-disable endpoint or when it leaks publicly — overriding each key's own setting. Pass `true` to allow it for all keys, `false` to forbid it for all keys, or `null` to clear the override so per-key values and the plan default apply again. Workspace admins only.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().setThirdPartyDisablingPolicy(bodySetWorkspaceThirdPartyDisablingPolicyV1WorkspacesApiKeysThirdPartyDisablingPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodySetWorkspaceThirdPartyDisablingPolicyV1WorkspacesApiKeysThirdPartyDisablingPost:** `BodySetWorkspaceThirdPartyDisablingPolicyV1WorkspacesApiKeysThirdPartyDisablingPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace AuditLogs

<details><summary><code>client.workspace.auditLogs.list(requestParameters) -> WorkspaceAuditLogsPageResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the audit log for the workspace. Requires enterprise tier and the audit_log_read permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().auditLogs().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListAuditLogsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace AuthConnections

<details><summary><code>client.workspace.authConnections.list() -> ListAuthConnectionsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all auth connections for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().authConnections().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.authConnections.create(createAuthConnectionsRequest) -> CreateAuthConnectionsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new OAuth2 auth connection for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().authConnections().create(createAuthConnectionsRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createAuthConnectionsRequest:** `CreateAuthConnectionsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.authConnections.update(authConnectionId, updateAuthConnectionsRequest) -> UpdateAuthConnectionsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update an auth connection

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().authConnections().update(
    authConnectionId,
    updateAuthConnectionsRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**authConnectionId:** `String`

</dd>
</dl>

<dl>
<dd>

**updateAuthConnectionsRequest:** `UpdateAuthConnectionsRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.authConnections.delete(authConnectionId, requestParameters) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an auth connection

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().authConnections().delete(
    authConnectionId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**authConnectionId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteAuthConnectionsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Groups

<details><summary><code>client.workspace.groups.list() -> Map&lt;String, WorkspaceGroupResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get all groups in the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().groups().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.groups.search(requestParameters) -> List&lt;WorkspaceGroupByNameResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Searches for user groups in the workspace. Multiple or no groups may be returned.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().groups().search(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `SearchGroupsRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Groups Members

<details><summary><code>client.workspace.groups.members.remove(groupId, bodyDeleteMemberFromUserGroupV1WorkspaceGroupsGroupIdMembersRemovePost) -> DeleteWorkspaceGroupMemberResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes a member from the specified group. Requires `group_members_manage` permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().groups().members().remove(
    groupId,
    bodyDeleteMemberFromUserGroupV1WorkspaceGroupsGroupIdMembersRemovePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**groupId:** `String` — The ID of the target group.

</dd>
</dl>

<dl>
<dd>

**bodyDeleteMemberFromUserGroupV1WorkspaceGroupsGroupIdMembersRemovePost:** `BodyDeleteMemberFromUserGroupV1WorkspaceGroupsGroupIdMembersRemovePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.groups.members.add(groupId, addMemberToGroupRequest) -> AddWorkspaceGroupMemberResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Adds a member of your workspace to the specified group. Requires `group_members_manage` permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().groups().members().add(
    groupId,
    addMemberToGroupRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**groupId:** `String` — The ID of the target group.

</dd>
</dl>

<dl>
<dd>

**addMemberToGroupRequest:** `AddMemberToGroupRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Members

<details><summary><code>client.workspace.members.list() -> List&lt;WorkspaceMemberResponseModel&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets a list of all members of the workspace, including locked members. Service accounts are excluded. Requires the workspace_members_read permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().members().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.members.update(updateMemberRequest) -> UpdateWorkspaceMemberResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates attributes of a workspace member. Apart from the email identifier, all parameters will remain unchanged unless specified. This endpoint may only be called by workspace administrators.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().members().update(updateMemberRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**updateMemberRequest:** `UpdateMemberRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.members.updateUsageLimit(userId, updateMemberUsageLimitRequestModel) -> UpdateMemberUsageLimitResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sets or clears the monthly credit usage limit of a workspace member or service account. This endpoint may only be called by workspace administrators.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().members().updateUsageLimit(
    userId,
    updateMemberUsageLimitRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**userId:** `String` — User ID of the target user.

</dd>
</dl>

<dl>
<dd>

**updateMemberUsageLimitRequestModel:** `UpdateMemberUsageLimitRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Invites

<details><summary><code>client.workspace.invites.create(inviteUserRequest) -> AddWorkspaceInviteResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sends an email invitation to join your workspace to the provided email. If the user doesn't have an account they will be prompted to create one. If the user accepts this invite they will be added as a user to your workspace and your subscription using one of your seats. This endpoint may only be called by workspace members with the WORKSPACE_MEMBERS_INVITE permission. If the user is already in the workspace a 400 error will be returned.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().invites().create(inviteUserRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**inviteUserRequest:** `InviteUserRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.invites.createBatch(bodyInviteMultipleUsersV1WorkspaceInvitesAddBulkPost) -> AddWorkspaceInviteResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sends email invitations to join your workspace to the provided emails. Requires all email addresses to be part of a verified domain. If the users don't have an account they will be prompted to create one. If the users accept these invites they will be added as users to your workspace and your subscription using one of your seats. This endpoint may only be called by workspace members with the WORKSPACE_MEMBERS_INVITE permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().invites().createBatch(bodyInviteMultipleUsersV1WorkspaceInvitesAddBulkPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyInviteMultipleUsersV1WorkspaceInvitesAddBulkPost:** `BodyInviteMultipleUsersV1WorkspaceInvitesAddBulkPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.invites.delete(bodyDeleteExistingInvitationV1WorkspaceInvitesDelete) -> DeleteWorkspaceInviteResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Invalidates an existing email invitation. The invitation will still show up in the inbox it has been delivered to, but activating it to join the workspace won't work. This endpoint may only be called by workspace members with the WORKSPACE_MEMBERS_INVITE permission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().invites().delete(bodyDeleteExistingInvitationV1WorkspaceInvitesDelete);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyDeleteExistingInvitationV1WorkspaceInvitesDelete:** `BodyDeleteExistingInvitationV1WorkspaceInvitesDelete` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Resources

<details><summary><code>client.workspace.resources.get(resourceId, requestParameters) -> ResourceMetadataResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Gets the metadata of a resource by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().resources().get(
    resourceId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**resourceId:** `String` — The ID of the target resource.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetResourcesRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.resources.share(resourceId, bodyShareWorkspaceResourceV1WorkspaceResourcesResourceIdSharePost) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Grants a role (one of 'admin', 'editor', 'commenter', or 'viewer') on a workspace resource to a user, group, or workspace (service account) API key. This overrides any existing role the target has on the resource. To target a user or service account, pass only the user email; the user must be in your workspace. To target a group, pass only the group id. To target a workspace (service account) API key, pass the api key id; the resource will be shared with the service account associated with that key. You must have admin access to the resource to share it.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().resources().share(
    resourceId,
    bodyShareWorkspaceResourceV1WorkspaceResourcesResourceIdSharePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**resourceId:** `String` — The ID of the target resource.

</dd>
</dl>

<dl>
<dd>

**bodyShareWorkspaceResourceV1WorkspaceResourcesResourceIdSharePost:** `BodyShareWorkspaceResourceV1WorkspaceResourcesResourceIdSharePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.resources.unshare(resourceId, bodyUnshareWorkspaceResourceV1WorkspaceResourcesResourceIdUnsharePost) -> Object</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes any existing role on a workspace resource from a user, group, or workspace (service account) API key. To target a user or service account, pass only the user email; the user must be in your workspace. To target a group, pass only the group id. To target a workspace (service account) API key, pass the api key id; the resource will be unshared from the service account associated with that key. You must have admin access to the resource to unshare it. You cannot remove permissions from the user who created the resource.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().resources().unshare(
    resourceId,
    bodyUnshareWorkspaceResourceV1WorkspaceResourcesResourceIdUnsharePost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**resourceId:** `String` — The ID of the target resource.

</dd>
</dl>

<dl>
<dd>

**bodyUnshareWorkspaceResourceV1WorkspaceResourcesResourceIdUnsharePost:** `BodyUnshareWorkspaceResourceV1WorkspaceResourcesResourceIdUnsharePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Usage

<details><summary><code>client.workspace.usage.getUsageByProductOverTime(bodyGetWorkspaceUsageV1WorkspaceAnalyticsQueryUsageByProductOverTimePost) -> WorkspaceAnalyticsQueryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns credit usage broken down by product type over time, for workspaces billed in credits. The response is a tabular structure with columns, column_types, column_units, and rows. Some id columns also get a display-name column, such as user_label or voice_name, appended after the existing columns. For workspaces billed by cost, ranges that reach the date the workspace moved to cost billing return an error; use /v1/workspace/analytics/usage instead.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().usage().getUsageByProductOverTime(bodyGetWorkspaceUsageV1WorkspaceAnalyticsQueryUsageByProductOverTimePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyGetWorkspaceUsageV1WorkspaceAnalyticsQueryUsageByProductOverTimePost:** `BodyGetWorkspaceUsageV1WorkspaceAnalyticsQueryUsageByProductOverTimePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.workspace.usage.getUsage(bodyGetWorkspaceCostUsageV1WorkspaceAnalyticsUsagePost) -> WorkspaceAnalyticsQueryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the cost of the workspace's usage over time, for workspaces billed by cost. Usage is bucketed by UTC day. The response is a tabular structure with columns, column_types, column_units, and rows, with one row per group and bucket. `total_cost` is the cost in the workspace's currency and `total_charge_count` the billed quantity. Usage can be grouped or filtered by at most two of `user_id`, `hashed_xi_api_key`, `region`, `reporting_workspace_id`, `billing_group_id`, `product_type`, `surface` and `actor`, and grouping by two of them requires an `eq` or `in` filter on one. `model`, `request_source`, `fiat_charge_type` and `fiat_currency` can always be added. Some id columns also get a display-name column, such as user_label, appended after the existing columns. Workspaces billed in credits should use /v1/workspace/analytics/query/usage-by-product-over-time.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().usage().getUsage(bodyGetWorkspaceCostUsageV1WorkspaceAnalyticsUsagePost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyGetWorkspaceCostUsageV1WorkspaceAnalyticsUsagePost:** `BodyGetWorkspaceCostUsageV1WorkspaceAnalyticsUsagePost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Workspace Analytics Requests

<details><summary><code>client.workspace.analytics.requests.get(bodyListApiRequestsV1WorkspaceAnalyticsRequestsPost) -> WorkspaceAnalyticsQueryResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a list of API requests. Supports filtering by time range, column filters, and search terms. At least one of start_time or end_time must be provided. An optional sort parameter controls timestamp ordering. Results are ordered by timestamp. Descending if end_time is used, ascending if start_time is used. The response is a tabular structure with columns, column_types, column_units, and rows.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.workspace().analytics().requests().get(bodyListApiRequestsV1WorkspaceAnalyticsRequestsPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyListApiRequestsV1WorkspaceAnalyticsRequestsPost:** `BodyListApiRequestsV1WorkspaceAnalyticsRequestsPost` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Webhooks

<details><summary><code>client.webhooks.list(requestParameters) -> WorkspaceWebhookListResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all webhooks for a workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListWebhooksRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.webhooks.create(bodyCreateWorkspaceWebhookV1WorkspaceWebhooksPost) -> WorkspaceCreateWebhookResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new webhook for the workspace with the specified authentication type.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().create(bodyCreateWorkspaceWebhookV1WorkspaceWebhooksPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateWorkspaceWebhookV1WorkspaceWebhooksPost:** `BodyCreateWorkspaceWebhookV1WorkspaceWebhooksPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.webhooks.update(webhookId, bodyUpdateWorkspaceWebhookV1WorkspaceWebhooksWebhookIdPatch) -> PatchWorkspaceWebhookResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the specified workspace webhook

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().update(
    webhookId,
    bodyUpdateWorkspaceWebhookV1WorkspaceWebhooksWebhookIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**webhookId:** `String` — The unique ID for the webhook

</dd>
</dl>

<dl>
<dd>

**bodyUpdateWorkspaceWebhookV1WorkspaceWebhooksWebhookIdPatch:** `BodyUpdateWorkspaceWebhookV1WorkspaceWebhooksWebhookIdPatch` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.webhooks.delete(webhookId, requestParameters) -> DeleteWorkspaceWebhookResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete the specified workspace webhook

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().delete(
    webhookId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**webhookId:** `String` — The unique ID for the webhook

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteWebhooksRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.webhooks.test(webhookId, bodySendTestWebhookEventV1WorkspaceWebhooksWebhookIdTestPost) -> WebhookSendResultResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sends a sample event of the given type to the webhook once and returns the endpoint's status code, response time, content type and response size. Test events are signed like real ones, are never retried, and share a limit of 10 per webhook per minute with redeliveries.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().test(
    webhookId,
    bodySendTestWebhookEventV1WorkspaceWebhooksWebhookIdTestPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**webhookId:** `String` — The ID of the webhook.

</dd>
</dl>

<dl>
<dd>

**bodySendTestWebhookEventV1WorkspaceWebhooksWebhookIdTestPost:** `BodySendTestWebhookEventV1WorkspaceWebhooksWebhookIdTestPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.webhooks.redeliver(webhookId, deliveryId, requestParameters) -> WebhookSendResultResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Sends a stored delivery again, once, with the same delivery ID, a new signature and an ElevenLabs-Webhook-Redelivery header. A retry that is already scheduled is still sent. Redelivering needs permission to manage webhooks and a web session, and the delivery's payload must be stored. Redeliveries and test events share a limit of 10 per webhook per minute.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.webhooks().redeliver(
    webhookId,
    deliveryId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**webhookId:** `String` — The ID of the webhook.

</dd>
</dl>

<dl>
<dd>

**deliveryId:** `String` — The ID of the delivery to send again.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RedeliverWebhooksRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Music

<details><summary><code>client.music.videoToMusic(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Generate background music from one or more video files. Videos are combined in order. Optional description and style tags influence the generated music.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().videoToMusic(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `VideoToMusicMusicRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.compose(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Compose a song from a prompt or a composition plan.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().compose(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyComposeMusicV1MusicPost` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.composeDetailed(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Compose a song from a prompt or a composition plan.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().composeDetailed(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyComposeMusicWithADetailedResponseV1MusicDetailedPost` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.composeDetailedStream(requestParameters) -> SseStream&lt;String&gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream a song and its detailed metadata using Server-Sent Events (SSE).

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().composeDetailedStream(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyStreamComposedMusicWithADetailedResponseV1MusicDetailedStreamPost` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.stream(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Stream a composed song from a prompt or a composition plan.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().stream(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyStreamComposedMusicV1MusicStreamPost` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.upload(bodyUploadMusicV1MusicUploadPost) -> MusicUploadResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Upload a music file to be later used for inpainting. Price for uploading is the same as the one for song generation. All uploaded content gets inspected for copyright infringement. If copyrighted content is detected, half of the request cost is still charged.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().upload(bodyUploadMusicV1MusicUploadPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyUploadMusicV1MusicUploadPost:** `BodyUploadMusicV1MusicUploadPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.separateStems(requestParameters) -> InputStream</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Separate an audio file into individual stems. This endpoint might have high latency, depending on the length of the audio file.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().separateStems(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `SeparateStemsMusicRequest` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Music CompositionPlan

<details><summary><code>client.music.compositionPlan.create(requestParameters) -> CreateCompositionPlanResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a composition plan for music generation. Usage of this endpoint does not cost any credits but is subject to rate limiting depending on your tier.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().compositionPlan().create(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `BodyGenerateCompositionPlanV1MusicPlanPost` — Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Music Finetunes

<details><summary><code>client.music.finetunes.list(requestParameters) -> MusicFinetunePageResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List music finetunes accessible to you (your own, workspace-shared, and ElevenLabs-curated), with optional filtering, sorting, and cursor pagination.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().finetunes().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListFinetunesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.finetunes.create(bodyCreateMusicFinetuneV1MusicFinetunesPost) -> MusicFinetuneResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new music finetune

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().finetunes().create(bodyCreateMusicFinetuneV1MusicFinetunesPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateMusicFinetuneV1MusicFinetunesPost:** `BodyCreateMusicFinetuneV1MusicFinetunesPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.finetunes.get(finetuneId, requestParameters) -> MusicFinetuneResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a music finetune.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().finetunes().get(
    finetuneId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**finetuneId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetFinetunesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.finetunes.update(finetuneId, updateMusicFinetuneRequestModel) -> MusicFinetuneResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a music finetune.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().finetunes().update(
    finetuneId,
    updateMusicFinetuneRequestModel
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**finetuneId:** `String`

</dd>
</dl>

<dl>
<dd>

**updateMusicFinetuneRequestModel:** `UpdateMusicFinetuneRequestModel` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.music.finetunes.delete(finetuneId, requestParameters) -> MusicFinetuneResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a music finetune

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.music().finetunes().delete(
    finetuneId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**finetuneId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteFinetunesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Tokens SingleUse

<details><summary><code>client.tokens.singleUse.create(tokenType, requestParameters) -> SingleUseTokenResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Generate a time limited single-use token with embedded authentication for frontend clients.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokens().singleUse().create(
    tokenType,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**tokenType:** `SingleUseTokenType`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `CreateSingleUseRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## ForcedAlignment

<details><summary><code>client.forcedAlignment.create(bodyCreateForcedAlignmentV1ForcedAlignmentPost) -> ForcedAlignmentResponseModel</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Force align an audio file to text. Use this endpoint to get the timing information for each character and word in an audio file based on a provided text transcript.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.forcedAlignment().create(bodyCreateForcedAlignmentV1ForcedAlignmentPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyCreateForcedAlignmentV1ForcedAlignmentPost:** `BodyCreateForcedAlignmentV1ForcedAlignmentPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## SpeechEngine

<details><summary><code>client.speechEngine.list(requestParameters) -> ListSpeechEnginesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns a paginated list of Speech Engine resources.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListSpeechEngineRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechEngine.create(createSpeechEngineRequest) -> SpeechEngineResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new Speech Engine resource

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().create(createSpeechEngineRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createSpeechEngineRequest:** `CreateSpeechEngineRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechEngine.get(speechEngineId, requestParameters) -> SpeechEngineResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a Speech Engine resource

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().get(
    speechEngineId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**speechEngineId:** `String` — The speech engine ID (accepts seng* or agent* prefix)

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetSpeechEngineRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechEngine.update(speechEngineId, updateSpeechEngineRequest) -> SpeechEngineResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update a Speech Engine resource (partial update)

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().update(
    speechEngineId,
    updateSpeechEngineRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**speechEngineId:** `String` — The speech engine ID (accepts seng* or agent* prefix)

</dd>
</dl>

<dl>
<dd>

**updateSpeechEngineRequest:** `UpdateSpeechEngineRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechEngine.delete(speechEngineId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a Speech Engine resource

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().delete(
    speechEngineId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**speechEngineId:** `String` — The speech engine ID (accepts seng* or agent* prefix)

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteSpeechEngineRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.speechEngine.duplicate(speechEngineId, duplicateSpeechEngineRequest) -> SpeechEngineResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new Speech Engine resource by duplicating an existing one

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.speechEngine().duplicate(
    speechEngineId,
    duplicateSpeechEngineRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**speechEngineId:** `String` — The speech engine ID (accepts seng* or agent* prefix)

</dd>
</dl>

<dl>
<dd>

**duplicateSpeechEngineRequest:** `DuplicateSpeechEngineRequest` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## EnvironmentVariables

<details><summary><code>client.environmentVariables.list(requestParameters) -> EnvironmentVariablesListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List all environment variables for the workspace with optional filtering

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environmentVariables().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListEnvironmentVariablesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environmentVariables.create(createEnvironmentVariablesRequest) -> EnvironmentVariableResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new environment variable for the workspace

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environmentVariables().create(createEnvironmentVariablesRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createEnvironmentVariablesRequest:** `CreateEnvironmentVariablesRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environmentVariables.get(envVarId, requestParameters) -> EnvironmentVariableResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Get a specific environment variable by ID

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environmentVariables().get(
    envVarId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**envVarId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetEnvironmentVariablesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environmentVariables.update(envVarId, updateEnvironmentVariableRequest) -> EnvironmentVariableResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Replace an environment variable's values. Use null to remove its value for an environment (except production); delete an environment with DELETE /v1/convai/environments/{environment}.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environmentVariables().update(
    envVarId,
    updateEnvironmentVariableRequest
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**envVarId:** `String`

</dd>
</dl>

<dl>
<dd>

**updateEnvironmentVariableRequest:** `UpdateEnvironmentVariableRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environmentVariables.delete(envVarId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an environment variable and its values in all environments. The delete is rejected while the latest version of an unarchived agent branch, a tool, an MCP server or the workspace conversation initiation webhook uses the variable. Archived branches and earlier versions are not checked.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environmentVariables().delete(
    envVarId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**envVarId:** `String`

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteEnvironmentVariablesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Environments

<details><summary><code>client.environments.list() -> EnvironmentsListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List the workspace environments, including 'production'.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environments().list();
```

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environments.create(createEnvironmentRequest) -> EnvironmentResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a workspace environment. Variables without a value for it fall back to their production value.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environments().create(createEnvironmentRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createEnvironmentRequest:** `CreateEnvironmentRequest` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.environments.delete(environment, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete a workspace environment and remove its values from all environment variables. 'production' cannot be deleted.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.environments().delete(
    environment,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**environment:** `String` — Name of the environment to delete, e.g. 'staging'.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `DeleteEnvironmentsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Productions Orders

<details><summary><code>client.productions.orders.list(requestParameters) -> ListOrdersResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Lists Productions orders in the workspace. Supports filtering by status and date range, with pagination.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `ListOrdersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.create(createOrderRequest) -> CreateOrderResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Creates a new Productions order in the workspace. The order starts in the open state and can be configured with items before submission.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().create(createOrderRequest);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**createOrderRequest:** `CreateOrderRequest` — _optional_ Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.get(orderId, requestParameters) -> OrderResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves full details for a Productions order. Quote and pricing information may not be available immediately; if you wish to see the quote before submission, you may need to poll the order details until it is ready.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().get(
    orderId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetOrdersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.update(orderId, bodyUpdateOrderV1ProductionsOrdersOrderIdPatch) -> UpdateOrderResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Updates an open order.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().update(
    orderId,
    bodyUpdateOrderV1ProductionsOrdersOrderIdPatch
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**bodyUpdateOrderV1ProductionsOrdersOrderIdPatch:** `BodyUpdateOrderV1ProductionsOrdersOrderIdPatch` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.submit(orderId, requestParameters) -> SubmitOrderResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Submits an open order for processing. The order must have at least one item. Once submitted, items can no longer be modified. Upon submission, the workspace will be charged for the order. The quote is based on information extracted from the uploaded media, such as its duration. The quote may not be available immediately; if you wish to see the quote before submission, you may need to poll the order details until the quote is ready.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().submit(
    orderId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `SubmitOrdersRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Productions Orders Media

<details><summary><code>client.productions.orders.media.register(orderId, bodyRegisterMediaV1ProductionsOrdersOrderIdMediaPost) -> RegisterMediaResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Registers a media file with an order, either by uploading it directly or by providing a URL to fetch it from. Exactly one of `media` or `media_url` must be provided. The registered media can then be referenced when adding order items.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().media().register(
    orderId,
    bodyRegisterMediaV1ProductionsOrdersOrderIdMediaPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order to which this media will be attached.

</dd>
</dl>

<dl>
<dd>

**bodyRegisterMediaV1ProductionsOrdersOrderIdMediaPost:** `BodyRegisterMediaV1ProductionsOrdersOrderIdMediaPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.media.get(orderId, mediaId, requestParameters) -> OrderMediaResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves metadata and a time-limited download URL for a previously uploaded media file.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().media().get(
    orderId,
    mediaId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**mediaId:** `String` — The ID of the media file.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `GetMediaRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Productions Orders Items

<details><summary><code>client.productions.orders.items.upsert(orderId, bodyUpsertOrderItemV1ProductionsOrdersOrderIdItemsPost) -> UpsertOrderItemResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Adds or updates an order item on an open order. Returns the item ID and the quoted price.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().items().upsert(
    orderId,
    bodyUpsertOrderItemV1ProductionsOrdersOrderIdItemsPost
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**bodyUpsertOrderItemV1ProductionsOrdersOrderIdItemsPost:** `BodyUpsertOrderItemV1ProductionsOrdersOrderIdItemsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.productions.orders.items.remove(orderId, itemId, requestParameters) -> RemoveOrderItemResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Removes an order item from an open order.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().items().remove(
    orderId,
    itemId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**itemId:** `String` — The ID of the order item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `RemoveItemsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Productions Orders Deliverables

<details><summary><code>client.productions.orders.deliverables.list(orderId, requestParameters) -> OrderDeliverablesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieves the delivered files for a completed order. Returns an empty list if the order is not yet completed.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().deliverables().list(
    orderId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderId:** `String` — The ID of the order.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListDeliverablesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Productions Orders Languages

<details><summary><code>client.productions.orders.languages.list(orderItemKind, requestParameters) -> LanguagesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Returns the available languages for a given order item kind.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.productions().orders().languages().list(
    orderItemKind,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**orderItemKind:** `OrderItemKind` — The kind of order item.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `ListLanguagesRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

## Assets

<details><summary><code>client.assets.list(requestParameters) -> AssetListResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

List assets in the workspace, most recently created first.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.assets().list(requestParameters);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.assets.requests.ListAssetsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.assets.create(bodyUploadAssetV1AssetsPost) -> AssetResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Upload a new asset.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.assets().create(bodyUploadAssetV1AssetsPost);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**bodyUploadAssetV1AssetsPost:** `BodyUploadAssetV1AssetsPost` — Request Body

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.assets.get(assetId, requestParameters) -> AssetResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a single asset by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.assets().get(
    assetId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**assetId:** `String` — ID of the asset.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.assets.requests.GetAssetsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>

<details><summary><code>client.assets.delete(assetId, requestParameters)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an asset by ID.

</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.assets().delete(
    assetId,
    requestParameters
);
```

</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**assetId:** `String` — ID of the asset.

</dd>
</dl>

<dl>
<dd>

**requestParameters:** `com.elevenlabs.api.resources.assets.requests.DeleteAssetsRequest` — _optional_ Request Parameters Object

</dd>
</dl>
</dd>
</dl>

</dd>
</dl>
</details>
