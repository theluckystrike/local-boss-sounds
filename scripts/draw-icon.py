#!/usr/bin/env python3
"""Draws the 512 px icon: a speaker whose sound waves stop at a dashed ring (heard only nearby).
Usage: draw-icon.py <out.png>   (also writes <out>-128.png)"""
import math, sys
from PIL import Image, ImageDraw

out = sys.argv[1]
S = 512
im = Image.new('RGBA', (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(im)
d.rounded_rectangle([0, 0, S - 1, S - 1], 96, fill=(28, 24, 40))
cx, cy = 230, 256
# dashed range ring
R = 190
for a in range(0, 360, 12):
    a0, a1 = math.radians(a), math.radians(a + 7)
    d.line([(cx + R * math.cos(a0), cy + R * math.sin(a0)), (cx + R * math.cos(a1), cy + R * math.sin(a1))], fill=(150, 140, 190), width=10)
# speaker
d.rectangle([cx - 80, cy - 34, cx - 36, cy + 34], fill=(236, 232, 245))
d.polygon([(cx - 36, cy - 34), (cx + 14, cy - 82), (cx + 14, cy + 82), (cx - 36, cy + 34)], fill=(236, 232, 245))
# waves inside the ring
for r, col in ((50, (170, 120, 255)), (92, (150, 100, 235)), (134, (120, 80, 200))):
    d.arc([cx + 14 - r, cy - r, cx + 14 + r, cy + r], -40, 40, fill=col, width=16)
# muted far wave outside the ring
d.arc([cx + 14 - 214, cy - 214, cx + 14 + 214, cy + 214], -18, 18, fill=(80, 70, 110), width=12)
xx, yy = 452, 256
d.line([(xx - 22, yy - 22), (xx + 22, yy + 22)], fill=(230, 80, 80), width=12)
d.line([(xx - 22, yy + 22), (xx + 22, yy - 22)], fill=(230, 80, 80), width=12)
im.save(out)
im.resize((128, 128), Image.LANCZOS).save(out.replace('.png', '-128.png'))
