#!/usr/bin/env python3
"""Renders the gallery image from the client game test result.
Usage: render-gallery.py <boss-sounds-result.csv> <out.png>"""
import csv, sys
from PIL import Image, ImageDraw, ImageFont

src, out = sys.argv[1], sys.argv[2]
FONT = '/System/Library/Fonts/Menlo.ttc'
SURF, INK, INK2, GRID = (26, 26, 25), (240, 239, 236), (195, 194, 183), (60, 60, 58)
VAN, MOD, OFF = (217, 89, 38), (57, 135, 229), (70, 70, 68)
f = lambda n: ImageFont.truetype(FONT, n)
rows = list(csv.DictReader(open(src)))
cases = []
for r in rows:
    if r['case'] not in cases:
        cases.append(r['case'])
res = {(r['mode'], r['case']): r for r in rows}
W, H = 1600, 900
im = Image.new('RGB', (W, H), SURF)
d = ImageDraw.Draw(im)
d.text((60, 40), 'Where you hear each boss sound', font=f(36), fill=INK)
d.text((60, 92), 'Real Fabric 26.3 client on a dedicated server. The server fires each event at the distance shown.', font=f(22), fill=INK2)
x0, y0, cw, rh = 60, 214, 470, 82
heads = ('Event', 'Vanilla', 'With Local Boss Sounds')
cols = [x0, x0 + 400, x0 + 400 + cw + 20]
for i, h in enumerate(heads):
    d.text((cols[i], y0 - 44), h, font=f(24), fill=INK)
for k, c in enumerate(cases):
    y = y0 + k * rh
    d.line([(x0, y - 8), (W - 60, y - 8)], fill=GRID, width=1)
    d.text((cols[0], y + 18), c.replace('in nether', 'in the Nether'), font=f(24), fill=INK)
    for j, mode in enumerate(('everywhere', 'nearby')):
        r = res[(mode, c)]
        cx = cols[j + 1]
        if r['heard'] == 'true':
            col = VAN if mode == 'everywhere' else MOD
            dist = float(r['offset_from_player'])
            label = f'heard, {dist:.1f} blocks from you' if mode == 'everywhere' else f'heard from the event, {dist:.0f} blocks away'
        else:
            col, label = OFF, 'silent'
        d.rounded_rectangle([cx, y + 10, cx + 28, y + 38], 5, fill=col)
        d.text((cx + 44, y + 12), label, font=f(22), fill=INK if r['heard'] == 'true' else INK2)
d.text((60, H - 56), 'Vanilla plays far and other-dimension events right next to your head. The mod plays near ones from where they happened.', font=f(19), fill=INK2)
im.save(out)
