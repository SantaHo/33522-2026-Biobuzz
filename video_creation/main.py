import asyncio
import random
import os
from pathlib import Path

import edge_tts

from moviepy.editor import (
    AudioFileClip,
    VideoFileClip,
    concatenate_videoclips,
)

from proglog import ProgressBarLogger


# ============================================================
# SETTINGS
# ============================================================

ROOT = Path(__file__).parent

STORY_FILE = ROOT / "stories" / "story.txt"
BACKGROUND_DIR = ROOT / "backgrounds" / "videos.gitignore"
OUTPUT_DIR = ROOT / "output"

OUTPUT_DIR.mkdir(exist_ok=True)

VOICE = "en-US-AndrewNeural"

# ============================================================
# VIDEO FORMAT
# ============================================================

# 720p 9:16 vertical
WIDTH = 720
HEIGHT = 1280

FPS = 30

# ============================================================
# AUDIO
# ============================================================

NARRATION_VOLUME = 5.0

# ============================================================
# BACKGROUND
# ============================================================

# Final background changes every 10 seconds
CLIP_LENGTH = 10

# Background videos play at 2x speed
BACKGROUND_SPEED = 2.0

# ============================================================
# ENCODING
# ============================================================

# Fastest x264 preset
ENCODER_PRESET = "ultrafast"

# Use ALL available logical CPU threads
THREADS = os.cpu_count() or 1


# ============================================================
# CUSTOM PROGRESS LOGGER
# ============================================================

class NewLineProgressLogger(ProgressBarLogger):

    def bars_callback(
        self,
        bar,
        attr,
        value,
        old_value=None
    ):

        if attr != "index":
            return

        total = self.bars[bar]["total"]

        if total is None or total <= 0:
            return

        percentage = (value / total) * 100

        current_percent = int(percentage)

        previous_percent = getattr(
            self,
            "_last_percent",
            -1
        )

        if current_percent == previous_percent:
            return

        self._last_percent = current_percent

        bar_length = 30

        filled = int(
            bar_length * percentage / 100
        )

        progress_bar = (
            "#" * filled
            +
            "-" * (bar_length - filled)
        )

        print(
            f"[{progress_bar}] "
            f"{percentage:6.2f}%"
        )


# ============================================================
# TEXT TO SPEECH
# ============================================================

async def generate_voice(text, output_path):

    communicate = edge_tts.Communicate(
        text=text,
        voice=VOICE,
        rate="+0%",
    )

    await communicate.save(
        str(output_path)
    )


# ============================================================
# FORMAT VIDEO TO 720x1280
# ============================================================

def format_video(clip):

    # Already exactly 720x1280
    if clip.w == WIDTH and clip.h == HEIGHT:

        print(
            "      Already 720x1280 "
            "- skipping resize."
        )

        return clip

    # Scale enough to completely cover
    # the vertical frame.
    scale = max(
        WIDTH / clip.w,
        HEIGHT / clip.h,
    )

    print(
        f"      Resizing "
        f"{int(clip.w)}x{int(clip.h)} "
        f"by {scale:.3f}x"
    )

    clip = clip.resize(scale)

    # Center crop
    clip = clip.crop(
        x_center=clip.w / 2,
        y_center=clip.h / 2,
        width=WIDTH,
        height=HEIGHT,
    )

    return clip


# ============================================================
# CREATE RANDOM BACKGROUND
# ============================================================

def create_random_background(duration):

    background_files = list(
        BACKGROUND_DIR.glob("*.mp4")
    )

    if not background_files:

        raise FileNotFoundError(
            f"No .mp4 videos found in:\n"
            f"{BACKGROUND_DIR}"
        )

    print()
    print(
        f"Found {len(background_files)} "
        f"background videos."
    )

    clips = []
    source_videos = []

    elapsed = 0
    last_file = None
    clip_number = 1

    while elapsed < duration:

        # ----------------------------------------------------
        # Pick random video.
        # Avoid same video twice in a row.
        # ----------------------------------------------------

        possible_files = [
            file
            for file in background_files
            if file != last_file
        ]

        if not possible_files:

            possible_files = background_files

        selected_file = random.choice(
            possible_files
        )

        last_file = selected_file

        print()
        print(
            f"  [{clip_number}] "
            f"Selected: "
            f"{selected_file.name}"
        )

        # ----------------------------------------------------
        # Load source
        # ----------------------------------------------------

        source = VideoFileClip(
            str(selected_file)
        )

        source_videos.append(
            source
        )

        if source.duration <= 0:

            print(
                "      Video has no "
                "usable duration."
            )

            continue

        # ----------------------------------------------------
        # Because the video is 2x speed,
        # we need twice as much source footage.
        #
        # Example:
        #
        # 20 seconds source
        #       ↓ 2x
        # 10 seconds final
        # ----------------------------------------------------

        remaining = duration - elapsed

        final_clip_length = min(
            CLIP_LENGTH,
            remaining,
        )

        source_clip_length = (
            final_clip_length *
            BACKGROUND_SPEED
        )

        # Don't request more footage than exists
        source_clip_length = min(
            source_clip_length,
            source.duration,
        )

        # Calculate actual final duration
        actual_final_duration = (
            source_clip_length /
            BACKGROUND_SPEED
        )

        if actual_final_duration <= 0:
            continue

        # ----------------------------------------------------
        # Pick random section
        # ----------------------------------------------------

        max_start = max(
            0,
            source.duration -
            source_clip_length,
        )

        start_time = random.uniform(
            0,
            max_start,
        )

        end_time = (
            start_time +
            source_clip_length
        )

        print(
            f"      Source section: "
            f"{start_time:.2f}s -> "
            f"{end_time:.2f}s"
        )

        print(
            f"      Speed: "
            f"{BACKGROUND_SPEED}x"
        )

        # ----------------------------------------------------
        # Create subclip
        # ----------------------------------------------------

        clip = source.subclip(
            start_time,
            end_time,
        )

        # ----------------------------------------------------
        # Speed up background
        # ----------------------------------------------------

        clip = clip.speedx(
            BACKGROUND_SPEED
        )

        # ----------------------------------------------------
        # Resize/crop
        # ----------------------------------------------------

        clip = format_video(
            clip
        )

        clips.append(
            clip
        )

        elapsed += actual_final_duration

        print(
            f"      Added: "
            f"{actual_final_duration:.2f}s"
        )

        print(
            f"      Background progress: "
            f"{elapsed:.2f}s / "
            f"{duration:.2f}s"
        )

        clip_number += 1

    # --------------------------------------------------------
    # Combine clips
    # --------------------------------------------------------

    print()
    print(
        "Combining background clips..."
    )

    # All clips are already 720x1280,
    # so chain is faster than compose.
    background = concatenate_videoclips(
        clips,
        method="chain",
    )

    # Make exactly narration length
    background = background.subclip(
        0,
        duration,
    )

    return (
        background,
        source_videos,
    )


# ============================================================
# CREATE FINAL VIDEO
# ============================================================

def create_video():

    print()
    print(
        "===================================="
    )
    print(
        "       BioBuzz Video Generator"
    )
    print(
        "===================================="
    )
    print()

    # ========================================================
    # 0. READ STORY
    # ========================================================

    print(
        "[0/6] Reading story..."
    )

    if not STORY_FILE.exists():

        raise FileNotFoundError(
            f"Story file not found:\n"
            f"{STORY_FILE}"
        )

    story = (
        STORY_FILE
        .read_text(
            encoding="utf-8"
        )
        .strip()
    )

    if not story:

        raise ValueError(
            "story.txt is empty."
        )

    print(
        f"Story length: "
        f"{len(story)} characters"
    )

    # ========================================================
    # FIND BACKGROUND VIDEOS
    # ========================================================

    if not BACKGROUND_DIR.exists():

        raise FileNotFoundError(
            f"Background folder not found:\n"
            f"{BACKGROUND_DIR}"
        )

    background_files = list(
        BACKGROUND_DIR.glob("*.mp4")
    )

    if not background_files:

        raise FileNotFoundError(
            "No .mp4 background videos found."
        )

    print()

    print(
        f"Found "
        f"{len(background_files)} "
        f"background video(s)."
    )

    # ========================================================
    # 1. GENERATE NARRATION
    # ========================================================

    print()
    print(
        "[1/6] Generating narration..."
    )

    audio_path = (
        OUTPUT_DIR /
        "narration.mp3"
    )

    asyncio.run(
        generate_voice(
            story,
            audio_path,
        )
    )

    print(
        f"Narration created:\n"
        f"{audio_path}"
    )

    # ========================================================
    # 2. LOAD NARRATION
    # ========================================================

    print()
    print(
        "[2/6] Loading narration..."
    )

    audio = AudioFileClip(
        str(audio_path)
    )

    print(
        f"Audio duration: "
        f"{audio.duration:.2f} seconds"
    )

    # ========================================================
    # BOOST NARRATION
    # ========================================================

    print()

    print(
        f"Boosting narration to "
        f"{NARRATION_VOLUME}x..."
    )

    audio = audio.volumex(
        NARRATION_VOLUME
    )

    print(
        "Narration boosted."
    )

    # ========================================================
    # VARIABLES
    # ========================================================

    background = None
    source_videos = []
    final = None

    try:

        # ====================================================
        # 3. CREATE RANDOM BACKGROUND
        # ====================================================

        print()
        print(
            "[3/6] Creating random "
            "background..."
        )

        (
            background,
            source_videos,
        ) = create_random_background(
            audio.duration
        )

        print()

        print(
            f"Background duration: "
            f"{background.duration:.2f}s"
        )

        # ====================================================
        # 4. ADD NARRATION
        # ====================================================

        print()
        print(
            "[4/6] Adding narration..."
        )

        final = background.set_audio(
            audio
        )

        print(
            "Narration attached."
        )

        # ====================================================
        # 5. RENDER
        # ====================================================

        print()
        print(
            "[5/6] Rendering video..."
        )

        print()

        print(
            f"Resolution: "
            f"{WIDTH}x{HEIGHT}"
        )

        print(
            "Aspect ratio: 9:16"
        )

        print(
            "Quality: 720p"
        )

        print(
            f"Background speed: "
            f"{BACKGROUND_SPEED}x"
        )

        print(
            f"Narration volume: "
            f"{NARRATION_VOLUME}x"
        )

        print(
            f"Using ALL available CPU "
            f"threads: {THREADS}"
        )

        print(
            f"Encoding preset: "
            f"{ENCODER_PRESET}"
        )

        print()

        print(
            "Rendering progress:"
        )

        print()

        video_path = (
            OUTPUT_DIR /
            "reddit_story.mp4"
        )

        progress_logger = (
            NewLineProgressLogger()
        )

        final.write_videofile(

            str(video_path),

            fps=FPS,

            codec="libx264",

            audio_codec="aac",

            preset=ENCODER_PRESET,

            threads=THREADS,

            logger=progress_logger,
        )

        # ====================================================
        # 6. FINISHED
        # ====================================================

        print()

        print(
            "[6/6] DONE!"
        )

        print()

        print(
            "===================================="
        )

        print(
            "     VIDEO CREATED SUCCESSFULLY"
        )

        print(
            "===================================="
        )

        print()

        print(
            f"Video saved to:\n"
            f"{video_path}"
        )

    finally:

        print()

        print(
            "Closing video files..."
        )

        if final is not None:

            try:
                final.close()
            except:
                pass

        if background is not None:

            try:
                background.close()
            except:
                pass

        for source in source_videos:

            try:
                source.close()
            except:
                pass

        try:
            audio.close()
        except:
            pass

        print(
            "Finished."
        )


# ============================================================
# RUN PROGRAM
# ============================================================

if __name__ == "__main__":

    create_video()
