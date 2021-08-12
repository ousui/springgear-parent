# SpringGear 使用手册

## maven 坐标

```xml
    <dependencyManagement>
        <dependencies>
            ...
            <!-- 在 maven 依赖管理中，添加 -->
            <dependency>
                <groupId>com.jd.springgear</groupId>
                <artifactId>springgear-support</artifactId>
                <version>${ver.springgear}</version>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- 在需要的地方，添加相关依赖，注意所有扩展依赖都依赖于 springgear-engine，不必重复引用 -->
    <dependencies>
        <dependency>
            <groupId>com.jd.springgear</groupId>
            <artifactId>springgear-extend-jsf</artifactId>
            <version>${ver.springgear}</version>
        </dependency>
    </dependencies>

```

---

## 入口代码

---

## 接口定义

---

## Handler 编写