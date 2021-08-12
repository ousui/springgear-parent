# SpringGear Framework

## 简介

SpringGear Framework 是一个依赖 Spring Framework 而构建的辅助型框架。

SpringGear 旨在为开发者提供一个高效、可控、流程清晰、逻辑明了的编写环境；同时 SpringGear 也提供了一系列丰富的扩展，用以避免开发者进行重复劳动，最大限度的节省开发者的时间，将主要精力放在业务逻辑的实现上。

---

## 核心

SpringGear 的核心为 Spring，初始开发版本为 Spring 4.x；当前使用的最新版本为 5.x；

SpringGear 最大限度的使用尽量少的依赖，来实现丰富的功能。内部核心逻辑实现，主要使用 Spring 的核心功能 AOP，IOC。

SpringGear 的核心功能即：将本应该直接实现的接口，使用 **动态代理** 模式，将工作任务分配到每个流程步骤，从而将业务逻辑进行解耦，分布进行，并将结果传递给下一步。

[查看举例说明](./doc-example-01.md)

---

## SpringGear 核心架构

SpringGear 使用的工作流模式(Workflow)，脱胎于管道模式(Pipeline)，正所谓青出于蓝而胜于蓝，去除了管道模式繁杂的流程，保留其线性执行和聚合上下文的精华，逐渐演变为一套简单可用的独特框架。

下图为 SpringGear 的核心流程

![](./springgear-flow.png)

---

## SpringGear 模块说明

```bash
springgear-parent                   // 父模块
    |- documents                    // 文档存储，不做编译使用
    |- springgear-bom               // 框架的清单，里面包含了所有 springgear 模块的依赖版本，一般情况下在依赖管理中 import 这个包，即可管理所有其包含的版本
    |- springgear-context           // 框架上下文，主要包含处理上下文流程的实体类和注解
    |- springgear-engine            // 框架核心引擎，核心处理流程
    |- springgear-extend-ducc       // 扩展 - 框架对 ducc 的支持 
    |- springgear-extend-jsf        // 扩展 - 框架对 jsf 的支持，支持注解形式管理 jsf provider 和 consumer。
    |- springgear-extend-orika      // 扩展 - 框架对 orika 映射框架的支持
    |- springgear-extend-ump        // 扩展 - 框架对 ump 监控的内建支持
    |- springgear-support           // 框架的支持类，非核心的工具类等
    |- springgear-webmvc            // 框架对 springmvc 的支持
```

SpringGear 框架的包命名方式和核心思路，主要参考 Spring 框架。

---

## SpringGear 习惯约定

---

## SpringGear 历史演进

[查看](./doc-history.md)

---

## SpringGear 版本历史

[查看](./doc-change.md)

---

## SpringGear 使用手册

[查看](./doc-guide.md)

---
