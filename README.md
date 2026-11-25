# Offshore Lift Plan

Two tools for offshore lifting operations. They work on a phone or a computer.

## Lift plan (`index.html`)
- All lift plan sections (1.0 Details to 13.0 Sign-off) as a fill-in form
- Automatic calculations: max lift weight with contingency, crane utilisation and safety margin, sling leg tension from angle or sling length/height, pass/fail against sling, lug and equipment SWL
- Colour code (Yellow, Blue, Green, White, Purple) that changes all equipment at once
- Sketches: insert a photo, convert it to a pencil sketch or line drawing, draw and write on it with your finger, add notes/steps
- Risk assessment rating fills in from severity and likelihood
- Print / save as PDF in a layout like the paper form
- Work saves on your device as you type; Save file / Open file to keep each lift plan

## Crane lift sketch (`sketch.html`)
- Elevation drawing of a work barge with an offshore pedestal lattice-boom crane lifting onto a platform, drawn from the numbers you type
- Crane controls: hold Boom ▲▼, Hoist ▲▼ and Slew ⟲⟳ to move the crane, or use the sliders
- Load moment indicator: radius, boom angle, slew, load, rated capacity, utilisation, hook height, line pull per fall, sling tension
- Load charts for aux and main block (radius → capacity), plan view with slew angle, wind and sea-state limits
- Aux block (default SWL 10 t) and main block (default SWL 80 t); every crane value is editable
- Save a crane profile for each vessel and pick it next time
- Set the boom by radius or boom angle, or drag the load to change the radius
- Load picture as original, line drawing or pencil sketch; drag labels; lifting gear labels with arrows
- Live checks: crane utilisation, sling angle, tension per leg, sling SWL, two-blocking
- Save PNG, or Add to lift plan (goes into section 9 Sketches)

## Open it
Turn on GitHub Pages (Settings → Pages → Deploy from a branch → main / root), then open
https://hafizibrahim11.github.io/offshore-lift-plan/
