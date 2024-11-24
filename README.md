正常的一个连接服务 aidl

//骨架 自动
implementation ('com.github.rasoulmiri:Skeleton:v1.0.9'){
exclude group: 'com.android.support'
}

在xml文件里用SkeletonGroup和SkeletonView包裹一层
然后在代码中调用SkeletonGroup的id进行finishAnimation方法调用进行停止
