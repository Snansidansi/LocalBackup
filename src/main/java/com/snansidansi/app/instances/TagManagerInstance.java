package com.snansidansi.app.instances;

import com.snansidansi.tag.TagManager;

import java.io.IOException;
import java.nio.file.Path;

public class TagManagerInstance {
    public static final TagManager tagManager;
    public static final IOException loadingException;

    static {
        TagManager tempTagManager;
        IOException tempLoadingException;
        try {
            tempTagManager = new TagManager(Path.of("data/tagData.csv"));
            tempLoadingException = null;
        } catch (IOException e) {
            tempTagManager = null;
            tempLoadingException = e;
        }
        tagManager = tempTagManager;
        loadingException = tempLoadingException;
    }

    private TagManagerInstance() {
    }
}
