package org.mozilla.javascript;

import dev.ngspace.hudder.api.compilers.TextPos;

// Don't ask questions
public final class RhinoTextPosGetter {

    private RhinoTextPosGetter() {}

    public static TextPos getPosition() {
        int[] line = { 0 };
        Context.getSourcePositionFromStack(line);
        return new TextPos(line[0], 0);
    }
}