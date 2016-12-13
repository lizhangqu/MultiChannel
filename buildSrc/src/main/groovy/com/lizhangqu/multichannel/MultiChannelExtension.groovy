package com.lizhangqu.multichannel

import org.gradle.api.Project

class MultiChannelExtension {
    public static final PLUGIN_EXTENTION = "multiChannel"
    HashSet<String> channels = []
    MultiChannelExtension(Project project) {

    }
}
