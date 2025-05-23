---
title: Video
type: tags
order: 310
meta_title: Video Tag for Video Labeling
meta_description: Customize Label Studio with the Video tag for basic video annotation tasks for machine learning and data science projects.
---

Video tag plays a simple video file. Use for video annotation tasks such as classification and transcription.

Use with the following data types: video

### Video format

Label Studio relies on your web browser to play videos, so it's essential that your videos use a format and codecs that are universally supported. To ensure maximum compatibility, we recommend using an MP4 container with video encoded using the H.264 (AVC) codec and audio encoded with AAC. This combination is widely supported across all modern browsers and minimizes issues like incorrect total duration detection or problems with playback. In addition, it's important to convert your videos to a constant frame rate (CFR), ideally around 30 fps, to avoid discrepancies in frame counts and issues with duplicated or missing frames.

Converting your videos to this recommended format will help ensure that they play smoothly in Label Studio and that the frame rate and duration are correctly recognized for accurate annotations. To convert any video to this format, you can use FFmpeg. For example, the following command converts an input video to MP4 with H.264 video, AAC audio, and a constant frame rate of 30 fps:

```bash
ffmpeg -i input_video.mp4 -c:v libx264 -profile:v high -level 4.0 -pix_fmt yuv420p -r 30 -c:a aac -b:a 128k output_video.mp4
```

In this command:
- `-i input_video.mp4` specifies your source video.
- `-c:v libx264` uses the H.264 codec for video encoding.
- `-profile:v high -level 4.0` sets compatibility parameters for a broad range of devices.
- `-pix_fmt yuv420p` ensures the pixel format is compatible with most browsers.
- `-r 30` forces a constant frame rate of 30 fps. You can also omit the -r option, ffmpeg will save your current frame rate. This is fine if you are 100% certain that your video has a constant frame rate.
- `-c:a aac -b:a 128k` encodes the audio in AAC at 128 kbps.
- `output_video.mp4` is the converted video file ready for use in Label Studio.

Using this FFmpeg command to re-encode your videos will help eliminate playback issues and ensure that Label Studio detects the total video duration  accurately, providing a smooth annotation experience.

It is a good idea to check all parameters of your video using this command:
```bash
ffprobe -v error -show_format -show_streams -print_format json input.mp4
```

### Parameters

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| name | <code>string</code> |  | Name of the element |
| value | <code>string</code> |  | URL of the video |
| [frameRate] | <code>number</code> | <code>24</code> | video frame rate per second; default is 24; can use task data like `$fps` |
| [sync] | <code>string</code> |  | object name to sync with |
| [muted] | <code>boolean</code> | <code>false</code> | muted video |
| [height] | <code>number</code> | <code>600</code> | height of the video player |
| [timelineHeight] | <code>number</code> | <code>64</code> | height of the timeline with regions |

### Example

Labeling configuration to display a video on the labeling interface

```html
<View>
  <Video name="video" value="$video" />
</View>
```
### Example

Video classification

```html
<View>
  <Video name="video" value="$video" />
  <Choices name="ch" toName="video">
    <Choice value="Positive" />
    <Choice value="Negative" />
  </Choices>
</View>
```
### Example

Video transcription

```html
<View>
  <Video name="video" value="$video" />
  <TextArea name="ta" toName="video" />
</View>
```
