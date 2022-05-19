package com.ulyp.ui.elements.recording.objects

import com.ulyp.core.recorders.CollectionRecord
import com.ulyp.core.recorders.ObjectRecord
import com.ulyp.ui.RenderSettings
import com.ulyp.ui.util.CssClass
import com.ulyp.ui.util.StyledText.of
import javafx.scene.Node
import java.util.stream.Collectors

class RecordedCollection(record: CollectionRecord, renderSettings: RenderSettings) : RecordedObject(record.type) {

    init {
        val recordedObjects = record.recordedItems
            .stream()
            .map { record: ObjectRecord -> of(record, renderSettings) }
            .collect(Collectors.toList())
        val texts: MutableList<Node> = ArrayList()
        if (renderSettings.showTypes()) {
            texts.add(of(record.type.name, CssClass.CALL_TREE_TYPE_NAME_CSS))
            texts.add(of(": ", CssClass.CALL_TREE_NODE_SEPARATOR_CSS))
        }
        texts.add(of("{", CssClass.CALL_TREE_COLLECTION_BRACKET_CSS))
        for (i in recordedObjects.indices) {
            texts.add(recordedObjects[i])
            if (i != recordedObjects.size - 1 || recordedObjects.size < record.length) {
                texts.add(of(", ", CssClass.CALL_TREE_NODE_SEPARATOR_CSS))
            }
        }
        if (recordedObjects.size < record.length) {
            texts.add(
                of(
                    (record.length - recordedObjects.size).toString() + " more...",
                    CssClass.CALL_TREE_NODE_SEPARATOR_CSS
                )
            )
        }
        texts.add(of("}", CssClass.CALL_TREE_COLLECTION_BRACKET_CSS))
        super.getChildren().addAll(texts)
    }
}