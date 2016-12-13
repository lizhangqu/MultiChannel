package com.lizhangqu.multichannel

import org.gradle.api.Project

class MultiChannelExtension {
    public static final PLUGIN_EXTENTION = "multiChannel"
    HashSet<String> channels = []
    boolean resign = true

    MultiChannelExtension(Project project) {

    }
}
