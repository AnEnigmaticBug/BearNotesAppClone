package com.example.nishant.bear;

import java.util.ArrayList;
import java.util.List;

public final class DummyData {


    public static List<NoteMetaData> getNoteMetaData() {
        List<NoteMetaData> metaDataList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for(int i = 0; i < 3; ++i) {
            tags.add("#Tag" + i);
        }
        for(int i = 0; i < 10; ++i) {
            metaDataList.add(new NoteMetaData(tags, "Title" + i));
        }
        return metaDataList;
    }
}
