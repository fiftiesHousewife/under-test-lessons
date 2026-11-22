"""Writes a lesson's video script (.tape) from a short JSON spec, in the house style of the existing lessons.

    python3 tools/new-video-script.py spec.json lessons/<id>/media/<id>.tape

The spec (see new-video-script.example.json) names the lesson and lists its scenes. Each scene has a title, the seconds
it stays on screen after its last step ("sleep": "15s"), and steps, each one of:
    ["note", "caption text, with *emphasis*"]
    ["show", "<file or part of its name>", "<first line>", "<last line>", "<label>"]   or   ["show", "<file>", "/pattern/", "<label>"]
    ["live", "<command>", "<regex the screen shows when it finishes>", "<seconds to hold the result>"]
    ["callout", "what the result means"]
A scene's on-screen time must cover its narration: about (words / 2.6) * 1.35 + 1.5 seconds.
"""
import sys

HEAD = '''# Terminal video for the {title} lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson={id}
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/{id}/media/{id}.gif
Output lessons/{id}/media/{id}.silent.mp4

Require java
Require git

Set Shell "bash"
Set FontSize 20
Set Width 1920
Set Height 1080
Set Padding 24
Set Theme "Builtin Dark"
Set TypingSpeed 40ms
Set WindowBar Colorful
Set CursorBlink false

Env LESSON "{id}"
Source lessons/_shared/setup.tape
'''

def typed(cmd):
    q = '`' if '"' in cmd else '"'
    return f'Type@1ms {q}{cmd}{q}\nEnter\nSleep 300ms\n'

def tape(lesson_id, title, scenes):
    out = [HEAD.format(id=lesson_id, title=title)]
    for n, sc in enumerate(scenes, 1):
        out.append(f'\n# ---- Scene {n}: {sc["title"]} ----\nHide\n' + typed('scene'))
        for kind, *args in sc['steps']:
            if kind == 'note':
                out.append(typed(f'note "{args[0]}"'))
            elif kind == 'show':
                out.append(typed('show ' + ' '.join(f'"{a}"' if (' ' in a or a.startswith('/')) else a for a in args)))
            elif kind == 'live':
                out.append(typed(f'run_live "{args[0]}"'))
                out.append(f'Show\nWait+Screen@300s /{args[1]}/\nSleep {args[2]}\nHide\n')
            elif kind == 'callout':
                out.append(typed(f'callout "{args[0]}"'))
        out.append(f'Show\nSleep {sc["sleep"]}\n')
    return ''.join(out)

if __name__ == '__main__':
    import json
    spec = json.load(open(sys.argv[1]))
    open(sys.argv[2], 'w').write(tape(spec['id'], spec['title'], spec['scenes']))
