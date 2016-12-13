package com.lizhangqu.multichannel

import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.builder.core.AndroidBuilder
import com.android.sdklib.BuildToolInfo
import org.apache.commons.io.FileUtils
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.process.ExecSpec

/**
 * 多渠道打包插件
 * author:lizhangqu(区长)
 *
 */
class MultiChannelPlugin implements Plugin<Project> {
    HashSet<String> channels = []
    private Logger logger

    @Override
    void apply(Project project) {
        project.gradle.addListener(new TimeListener())
        logger = project.logger;
        project.extensions.create(MultiChannelExtension.PLUGIN_EXTENTION, MultiChannelExtension, project)
        project.afterEvaluate {
            def extension = project.extensions.findByName(MultiChannelExtension.PLUGIN_EXTENTION) as MultiChannelExtension
            extension.channels.each {
                channel ->
                    channels.add(channel)
            }
            if (channels == null || channels.size() <= 0) {
                return
            }
            project.android.applicationVariants.each { variant ->
                //got variantName
                def variantName = variant.name.capitalize()

                AndroidBuilder androidBuilder = variant.androidBuilder
                def mTargetInfo = androidBuilder.mTargetInfo
                def mBuildToolInfo = mTargetInfo.mBuildToolInfo
                Map<BuildToolInfo.PathId, String> mPaths = mBuildToolInfo.mPaths
                def aaptPath = mPaths.get(BuildToolInfo.PathId.AAPT)
                //got aapt file
                def aaptFile = new File(aaptPath);

                //got assemble Task
                def assembleTask = variant.getAssemble();
//                def assembleTask = project.tasks.findByName("assemble${variantName}")

                //got package Task
                def packageTask = project.tasks.findByName("package${variantName}")
                //got signed apk
                File outApk = packageTask.outputFile
                //got sign config
                SigningConfig signingConfig = packageTask.signingConfig;

                //got v2 sign .....
                def v2Enabled = false
                try {
                    v2Enabled = signingConfig.isV2SigningEnabled();
                } catch (Throwable e) {
                    e.printStackTrace()
                    v2Enabled = false
                }
                if (v2Enabled) {
                    throw new RuntimeException("please disable v2SigningEnabled.\n" + "signingConfigs {\n" +
                            "    ${variant.name} {\n" +
                            "        v2SigningEnabled false\n" +
                            "    }\n" +
                            "}" + "\n see:https://developer.android.com/about/versions/nougat/android-7.0.html#apk_signature_v2")
                }
                String multiChannelName = "multiChannel${variantName}"
                project.task(multiChannelName) {
                    doLast {
                        def channelDataFileName = "META-INF/channel.data"
                        //1. generate an empty channel.data
                        File operateFile = new File(outApk.getParentFile(), "channelOrignal.apk");
                        FileUtils.copyFile(outApk, operateFile)

                        File channelDataFile = new File(outApk.parentFile, channelDataFileName)
                        FileUtils.deleteQuietly(channelDataFile)
                        FileUtils.writeStringToFile(channelDataFile, "");
                        try {
                            //2. add an empty channel.data to apk
                            project.exec(new Action<ExecSpec>() {
                                @Override
                                public void execute(ExecSpec execSpec) {
                                    execSpec.workingDir(operateFile.getParent())
                                    execSpec.executable(aaptFile);
                                    execSpec.args("add", operateFile.getName(), channelDataFileName);
                                }
                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace()
                            //ignore,maybe META-INF/channel.data exist
                        }

                        //iterate all channels, and exec aapt remove and add
                        channels.each {
                            channel ->
                                if (channel == null || channel.length() <= 0) {
                                    return
                                }
                                logger.warn("current channel:${channel}")
                                //write channel
                                FileUtils.deleteQuietly(channelDataFile)
                                FileUtils.writeStringToFile(channelDataFile, channel);

                                //remove
                                project.exec(new Action<ExecSpec>() {
                                    @Override
                                    public void execute(ExecSpec execSpec) {
                                        execSpec.workingDir(operateFile.getParent())
                                        execSpec.executable(aaptFile);
                                        execSpec.args("remove", operateFile.getName(), channelDataFileName);
                                    }
                                });
                                //add
                                project.exec(new Action<ExecSpec>() {
                                    @Override
                                    public void execute(ExecSpec execSpec) {
                                        execSpec.workingDir(operateFile.getParent())
                                        execSpec.executable(aaptFile);
                                        execSpec.args("add", operateFile.getName(), channelDataFileName);
                                    }
                                });

                                //sign
//                                File channelResignFile = new File("${operateFile.parent}/channel", "resign_${channel}.apk")
//                                FileUtils.forceMkdir(channelResignFile.getParentFile())
//                                androidBuilder.signApk(operateFile, signingConfig, channelResignFile)
                                //copy
                                File channelFile = new File("${operateFile.parent}/channel", "${project.name}-${variant.name}-${channel}.apk")
                                FileUtils.copyFile(operateFile, channelFile)
                        }

                        //delete
                        FileUtils.deleteQuietly(operateFile)
                        FileUtils.deleteQuietly(channelDataFile)
                        FileUtils.deleteDirectory(channelDataFile.getParentFile())

                    }
                }

                //task
                def multiChannelTask = project.tasks[multiChannelName]
                multiChannelTask.setGroup("multiChannel")
                multiChannelTask.setDescription("多渠道打包task")

                //depend on package Task
                if (packageTask) {
                    multiChannelTask.dependsOn(packageTask)
                }


            }

        }
    }
}


