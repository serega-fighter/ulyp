package com.ulyp.ui.elements.recording.objects

import com.ulyp.core.Type
import com.ulyp.ui.RenderSettings
import com.ulyp.ui.util.CssClass
import com.ulyp.ui.util.StyledText

class RecordedFile(filePath: String, type: Type, renderSettings: RenderSettings) : RecordedObject(type) {

    init {
        if (renderSettings.showTypes()) {
            super.getChildren().add(StyledText.of(type.name, CssClass.CALL_TREE_TYPE_NAME_CSS))
            super.getChildren().add(StyledText.of(": ", CssClass.CALL_TREE_NODE_SEPARATOR_CSS))
        }
        super.getChildren().add(StyledText.of(filePath, CssClass.CALL_TREE_NUMBER_CSS))
    }
}