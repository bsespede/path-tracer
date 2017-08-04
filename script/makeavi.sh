cat frames*.png | ffmpeg -f image2pipe -r 1 -vcodec mjpeg -i - -vcodec libx264 out.mp4

ffmpeg -i frame-%d.png -y -f avi -b 1150 -s 1024x768 -r 29.97 -g 12 -qmin 3 -qmax 13 -ab 224 -ar 44100 -ac 1 test.avi

