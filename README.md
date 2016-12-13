### 多渠道打包Gradle插件

配置渠道

```
apply plugin: 'com.lizhangqu.multichannel'
multiChannel {
    channels = ["baidu", "qihu", "google", "huawei", "xiaomi", "wandoujia", "anzhi", "uc", "tencent", "wangyi", "youmeng"]
}
```

之后会往META-INF/channel.data下写入渠道信息

开启v2签名后会扔异常！！禁用即可

生成渠道包

```
gradle clean multiChannelDebug
gradle clean multiChannelRelease
```

产物位于
${project}/build/outputs/apk/channel下