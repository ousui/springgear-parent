
# 举例说明 01

例如：有一个司机开车接口，其有一个方法 drive，其包含的流程为：
1. 校验司机是否有驾照
2. 司机上车
3. 司机点火
4. 司机开车

首先定义一个接口：
```java
interface Api {
    void drive(Driver driver);
}
```

传统模式下，可能直接实现即可：

```java
class FlashDrive implements Api {
    void driver(Driver driver) {
        verifyLicense();
        getOn();
        turnOn();
        drive();
    }
}
```

如果有一天，前置条件为 **车辆类型为手动挡**，则需要添加更多的校验规则和开车流程：
1. 校验司机是否有驾照
2. *记录驾照类型*
3. 司机上车
4. 司机点火
5. *如果开手动挡*
   1. *挂档*
   2. *踩离合*
6. *如果开自动挡*
7. *松手刹*
8. 司机开车

在传统模式下，一个类就包含了这个方法的所有工作，在冗长的流程里面，突然加入了更多的流程，那修改就更加多更加的频繁。

```java
class FlashDrive implements Api {
    void driver(Driver driver) {
        verifyLicense();
        logLicenseType();
        getOn();
        turnOn();
        if (MT) {
            shift();
            clutch();
        } else if (AT) {
            // do AT some thing
        }
        handBrake();
        drive();
    }
}
```

如果是在多人协作的模式下，就更有可能发生代码冲突，要知道，解决代码冲突，从始至终都是一件让人头疼的事情。

SpringGear 就是为了解决这个问题，将应用解耦，例如：

```java
class VerifyLicenseHandler implements SpringGearHandler {
    void handle(Context ctx) {}
}
class LogLicenseHandler implements SpringGearHandler {}
class GetOnHandler implements SpringGearHandler {}
class TurnOnHandler implements SpringGearHandler {}
class MtShiftHandler implements SpringGearHandler { 
    boolean support() {
        return type == MT;
    }
    void handle(Context ctx) {}
}
class MtClutchHandler implements SpringGearHandler { }
class AtHandler implements SpringGearHandler {
    boolean support() {
        return type == AT;
    }
    void handle(Context ctx) {}
}
class HandBrakeHandler implements SpringGearHandler {}
class DriveHandler implements SpringGearHandler {}
```

可以看见，虽然 Class 变多了，但是每个 Class 都专注做一个逻辑，逻辑变动只需要对某一块就行改动，不会造成代码冲突。