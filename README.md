# AskRedditGenerator
This software generates AskReddit reading videos for you.

## [Example Video](https://www.youtube.com/watch?v=B8DifjET0hc&feature=youtu.be)

## Usage

When you run the .jar file right after downloading, it probably won't work.

If you are on windows, you have to [download ffmpeg](https://ffmpeg.zeranoe.com/builds/) and put the ffmpeg.exe next to the jar file.

If you are on linux, execute `apt-get install ffmpeg`

If you execute it now, it will generate a video with a template post I entered. To properly configure which post to use,
you have to launch it from the command line.

`java -jar AskRedditGenerator.jar <Reddit Post ID>`

If you don't know how to get the Post ID, it's the part of the link selected in the image under this text.
![Post ID](https://i.imgur.com/2Cjlf2M.png)

Which means that if you want to use the post in the image with this generator, you have to run the command like this:

`java -jar AskRedditGenerator.jar c1bhpc`

If you run it like this, the greeting will be a placeholder text. If you want to customize the greeting of the video, just create a text
file named `greeting.txt` next to the .jar and put your greeting in it.
