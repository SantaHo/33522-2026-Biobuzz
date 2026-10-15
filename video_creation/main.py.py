import asyncio
from pathlib import Path
 
import edge_tts
from moviepy import (
    VideoFileClip,
    AudioFileClip,
    CompositeVideoClip,
)
 
ROOT = Path(__file__).parent
STORY_FILE = ROOT / "story.txt"
BACKGROUND_FILE = ROOT / "backgrounds" / "gameplay.mp4"
OUTPUT_DIR = ROOT / "output"
 
OUTPUT_DIR.mkdir(exist_ok=True)
 
VOICE = "en-US-AndrewNeural"
WIDTH = 1080
HEIGHT = 1920
 
 
async def generate_voice(text, output_path):
    communicate = edge_tts.Communicate(
        text=text,
        voice=VOICE,
        rate="+0%",
    )
    await communicate.save(str(output_path))
 
 
def create_video():
    story = STORY_FILE.read_text(encoding="utf-8").strip()
 
    if not story:
        raise ValueError("story.txt is empty")
 
    audio_path = OUTPUT_DIR / "narration.mp3"
    video_path = OUTPUT_DIR / "reddit_story.mp4"
 
    asyncio.run(generate_voice(story, audio_path))
 
    audio = AudioFileClip(str(audio_path))
    background = VideoFileClip(str(BACKGROUND_FILE))
 
    try:
        # Fill the vertical frame without stretching.
        scale = max(
            WIDTH / background.w,
            HEIGHT / background.h,
        )
 
        background = background.resized(scale)
 
        background = background.cropped(
            x_center=background.w / 2,
            y_center=background.h / 2,
            width=WIDTH,
            height=HEIGHT,
        )
 
        # Loop the background if it is too short.
        if background.duration < audio.duration:
            from moviepy import concatenate_videoclips
 
            repeats = int(
                audio.duration / background.duration
            ) + 1
 
            background = concatenate_videoclips(
                [background] * repeats,
                method="compose",
            )
 
        background = background.subclipped(
            0, audio.duration
        )
 
        final = background.with_audio(audio)
 
        final.write_videofile(
            str(video_path),
            fps=30,
            codec="libx264",
            audio_codec="aac",
            preset="medium",
        )
 
        final.close()
 
    finally:
        audio.close()
        background.close()
 
    print(f"Video saved to: {video_path}")
 
 
if __name__ == "__main__":
    create_video()
