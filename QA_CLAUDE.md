# QA_CLAUDE.md（测试与业务验证规范）

> 角色视角：高级测试开发工程师（QA/SDET）
> 
> 适用项目：`silence-auth-center`
> 
> 目标：把测试团队从“按接口点测”升级为“按业务模型测”，明确 **测什么**、**怎么测**、**失败时断言什么**。

---

## 文档使用约定

- 本文强制输出 6 个结构产物：
  1. 场景清单库
  2. 状态模型（状态机）
  3. 实体模型（领域建模）
  4. 规则模型
  5. 风险点模型
  6. 跨域交互模型
- 术语说明：
  - **规则模型**：把业务约束抽象为“可验证规则项（Rule Item）”，每条规则都能直接映射到测试断言。
  - **风险点模型**：把缺陷高发区域抽象为“触发条件 + 观测信号 + 预防性测试策略”的风险清单。

---

## 0. 代码事实基线（用于追溯）

核心服务与接口：
- `domain/service`: `AuthService`, `UserService`, `RoleService`, `MenuService`, `PermissionService`
- `api`: `AuthResource`, `UserResource`, `RoleResource`, `MenuResource`, `NoticeResource`
- `domain/model`: `User`, `Role`, `Menu`, `Notice`, `UserRole`, `RoleMenu`
- 异常消息：`infrastructure/message/AuthCenterMessages`

当前测试现状（重要）：
- 仓库未发现 `src/test/java` 测试代码。
- `silence-auth-center-console/pom.xml` 已具备测试依赖：
  - `spring-boot-starter-test`
  - `h2`

---

## 1) 场景清单库（Scenario Library）

> 目的：形成团队统一的“测试资产目录”，每次需求变更先定位场景，再设计用例。

### 1.1 认证与登录场景

| 场景ID | 场景名称 | 入口 | 关键前置 | 关键断言 | 优先级 |
|---|---|---|---|---|---|
| AUTH-001 | 正常登录 | `AuthService.login` | 用户存在且启用，密码正确 | 返回 token、用户信息、菜单树、权限集合 | P0 |
| AUTH-002 | 用户不存在登录 | `AuthService.login` | 用户名不存在 | 抛 `USER_NOT_EXIST`（错误码 51） | P0 |
| AUTH-003 | 密码错误登录 | `AuthService.login` | 用户存在，密码错误 | 抛 `PASSWORD_NOT_CORRECT`（错误码 52） | P0 |
| AUTH-004 | 菜单元数据异常 | `AuthService.flattenMenu` | 按钮菜单缺失 `meta.permission` | 权限聚合不应 NPE（应有防御性断言） | P1 |

### 1.2 用户管理场景

| 场景ID | 场景名称 | 入口 | 关键前置 | 关键断言 | 优先级 |
|---|---|---|---|---|---|
| USER-001 | 创建用户成功 | `UserService.create` | 用户名未占用，密码满足复杂度 | 密码已加密、用户写入、角色关联写入 | P0 |
| USER-002 | 重复用户名创建 | `UserService.create` | 用户名已存在 | 抛 `USERNAME_ALREADY_EXIST`（59） | P0 |
| USER-003 | 密码复杂度不达标 | `UserService.create/update/resetPassword` | 密码缺字母/数字/特殊字符 | 抛 `PASSWORD_COMPLEXITY_NOT_MATCHED`（53） | P0 |
| USER-004 | 更新用户不改密码 | `UserService.update` | 请求 password 为空 | 保持原密码不变 | P0 |
| USER-005 | 重置密码用户不存在 | `UserService.resetPassword` | id 不存在 | 抛 `USER_NOT_EXIST`（51） | P0 |
| USER-006 | 删除用户幂等性 | `UserService.delete` | 用户已删除或不存在 | 不得产生脏数据；用户角色关系一致 | P1 |

### 1.3 角色管理场景

| 场景ID | 场景名称 | 入口 | 关键前置 | 关键断言 | 优先级 |
|---|---|---|---|---|---|
| ROLE-001 | 创建角色成功 | `RoleService.create` | role code 唯一 | 角色写入成功 | P0 |
| ROLE-002 | 角色编码重复 | `RoleService.create/update` | code 已存在 | 抛 `ROLE_CODE_ALREADY_EXIST`（58） | P0 |
| ROLE-003 | 更新不存在角色 | `RoleService.update` | id 不存在 | 抛 `ROLE_NOT_EXIST`（57） | P0 |
| ROLE-004 | 分配角色菜单 | `RoleService.assignRoleMenus` | 角色存在 | 先清空后重建，结果与输入一致 | P0 |

### 1.4 菜单管理场景

| 场景ID | 场景名称 | 入口 | 关键前置 | 关键断言 | 优先级 |
|---|---|---|---|---|---|
| MENU-001 | 创建菜单成功 | `MenuService.create` | 合法菜单数据 | 菜单写入成功 | P0 |
| MENU-002 | 更新不存在菜单 | `MenuService.update` | id 不存在 | 抛 `MENU_NOT_EXIST`（54） | P0 |
| MENU-003 | 删除有子菜单节点 | `MenuService.delete` | 存在子菜单 | 抛 `SUB_MENU_EXIST`（55）且不删除 | P0 |
| MENU-004 | 获取当前用户菜单树 | `MenuService.getCurrentUserMenuTree` | 用户已绑定角色菜单 | 树结构正确、去重正确 | P1 |

### 1.5 通知管理场景

| 场景ID | 场景名称 | 入口 | 关键前置 | 关键断言 | 优先级 |
|---|---|---|---|---|---|
| NOTICE-001 | 标记单条已读 | `NoticeRepository.markAsRead` | 通知存在且未删除 | 状态从 UN_READ 到 READ | P1 |
| NOTICE-002 | 全部标已读 | `NoticeRepository.markAllAsRead` | 当前用户存在 | 仅修改当前用户通知 | P1 |
| NOTICE-003 | 清空通知 | `NoticeRepository.clearAllNotices` | 当前用户名非空 | 逻辑删除（is_deleted=1） | P0 |
| NOTICE-004 | 清空通知参数异常 | `clearAllNotices` | username 空白 | 抛 `IllegalArgumentException` | P0 |

---

## 2) 状态模型（State Model / 状态机）

> 目的：用状态转移约束指导“允许做什么/禁止做什么”的测试设计。

### 2.1 User 状态机

状态维度：
- 生命周期：`NOT_DELETED` / `DELETED`
- 账号状态：`ENABLED(status=true)` / `DISABLED(status=false)`
- 密码状态：`FIRST_LOGIN`、`FORCE_CHANGE_PASSWORD`

允许转移：
- `create`：初始化到 `NOT_DELETED + ENABLED`
- `updateUserStatus`：`ENABLED <-> DISABLED`
- `delete`：`NOT_DELETED -> DELETED`
- `resetPassword`：重置后 `firstLogin=false`、`forceChangePassword=false`

禁止转移（应加测试）：
- `DELETED` 用户继续更新/分配角色
- 用户不存在时执行 `resetPassword` / `modifyPassword`

### 2.2 Role 状态机

状态维度：`NOT_DELETED/DELETED` + `status(true/false)`

允许转移：
- 创建、更新、状态切换、逻辑删除

约束：
- `code` 在有效数据集中唯一
- 不存在角色不可更新/分配菜单

### 2.3 Menu 状态机

状态维度：`NOT_DELETED/DELETED` + `status(true/false)` + 树结构位置

关键约束：
- 有子菜单时禁止删除父菜单
- 删除前必须先验证存在性
- 获取树时应自动过滤已删除数据

### 2.4 Notice 状态机

状态：`UN_READ(0)` -> `READ(1)`

允许转移：
- 单条已读、全部已读、逻辑删除

约束：
- `readAll` 与 `clearAll` 必须限定在当前认证用户上下文

---

## 3) 实体模型（Entity / Domain Model）

> 目的：统一“数据结构 + 约束 + 关联关系”的测试数据设计语言。

### 3.1 核心实体与聚合关系

- **User**（聚合根）
  - 字段：username、password、status、firstLogin、forceChangePassword
  - 关联：`UserRole`（User:N-M:Role）
- **Role**（聚合根）
  - 字段：code、name、status、appCode
  - 关联：`RoleMenu`（Role:N-M:Menu）
- **Menu**（聚合根）
  - 字段：parentId、type、meta(JSON)、status
  - 关联：树结构（parent-child）
- **Notice**（聚合根）
  - 字段：senderId、senderName、title、content、status

### 3.2 基类与隐性约束

所有关键实体继承逻辑删除审计基类：
- `id`
- 审计字段（创建/修改时间与人）
- `deleted` / `is_deleted`

测试数据红线：
- 查询类测试必须覆盖 `is_deleted=0` 与 `is_deleted=1` 双分支。
- 所有关联测试（`sys_user_role` / `sys_role_menu`）必须断言删除后关联一致性。

### 3.3 JSON 字段测试模型（Menu.meta）

推荐覆盖：
- `meta = null`
- `meta = {}`
- `meta.permission` 缺失
- 深层 JSON（嵌套对象/数组）
- 非法类型（permission 为 number/boolean）

---

## 4) 规则模型（Rule Model）

> 回答“规则模型是什么”：
> 
> **规则模型 = 可被自动化测试执行和断言的业务规则目录**，每条规则都包含：
> `规则ID + 触发条件 + 约束表达式 + 失败错误码 + 覆盖级别`。

### 4.1 规则清单（示例）

| 规则ID | 触发条件 | 约束表达式 | 失败断言 | 覆盖级别 |
|---|---|---|---|---|
| R-USER-UNIQUE-001 | 创建用户 | username 必须唯一 | `USERNAME_ALREADY_EXIST(59)` | 单测+集成 |
| R-PWD-COMPLEX-001 | 创建/改密 | 密码必须含字母+数字+特殊字符 | `PASSWORD_COMPLEXITY_NOT_MATCHED(53)` | 单测 |
| R-ROLE-CODE-001 | 创建/更新角色 | role.code 唯一 | `ROLE_CODE_ALREADY_EXIST(58)` | 单测+集成 |
| R-MENU-DELETE-001 | 删除菜单 | 存在子菜单时禁止删除 | `SUB_MENU_EXIST(55)` | 单测+集成 |
| R-NOTICE-CLEAR-001 | 清空通知 | username 非空白 | `IllegalArgumentException` | 单测 |
| R-AUTH-LOGIN-001 | 登录 | 用户存在且密码匹配 | `USER_NOT_EXIST(51)` / `PASSWORD_NOT_CORRECT(52)` | 单测 |

### 4.2 规则驱动测试写法建议

每条规则至少 3 条用例：
1. 满足规则（正向）
2. 违反规则（反向）
3. 边界输入（null/空白/极值）

---

## 5) 风险点模型（Risk Model）

> 回答“风险点模型是什么”：
> 
> **风险点模型 = 缺陷概率 × 业务影响 的优先级模型**，用于决定先测哪里、测到多深。

### 5.1 风险条目格式

每个风险条目统一描述：
- 风险ID
- 风险说明
- 触发条件
- 可观测信号（日志/错误码/脏数据）
- 检测策略（单测/集成/并发）
- 缓解动作（新增断言、回归集）

### 5.2 当前项目高风险清单

| 风险ID | 风险说明 | 触发条件 | 可观测信号 | 检测策略 |
|---|---|---|---|---|
| RK-001 | 菜单删除顺序错误导致误删 | 校验子菜单前执行删除 | 数据已删但仍抛错/树结构异常 | `MenuService.delete` 分支+顺序测试（InOrder） |
| RK-002 | 用户改密路径未统一加密 | `modifyPassword` 直接写入新密码 | DB 密码非 hash | 单测断言 `passwordUtil.encodePassword` 调用 |
| RK-003 | 关联表物理删导致审计断裂 | `deleteByUserId/deleteByRoleId` | 历史关联不可追溯 | Repository 集成测试校验逻辑删除策略 |
| RK-004 | 权限聚合依赖 JSON 元数据 | `meta.permission` 缺失/类型错 | NPE 或权限丢失 | `flattenMenu` 边界数据测试 |
| RK-005 | 缺少现有测试资产 | 无 `src/test/java` | 回归仅靠手测 | 先落地 P0 场景自动化冒烟套件 |

---

## 6) 跨域交互模型（Cross-Domain Interaction Model）

> 回答“跨域交互模型是什么”：
> 
> 即使不是微服务拆分，也存在模块/聚合之间的跨域协作链路。该模型用于识别 **一个动作影响多个域对象** 的验证点。

### 6.1 关键交互链路

#### 链路 A：登录鉴权链路
`AuthService.login`
-> `UserRepository.findByUsernameAndStatus`
-> `PasswordUtil.matches`
-> `MenuService.getCurrentUserMenuTree`
-> `RoleRepository.findRoleByUserId`
-> `SilenceAuthCenterServerTokenAuthority.issueToken`

测试焦点：
- 任何一个环节失败都必须返回可解释业务异常。
- token 中权限集合与菜单树按钮权限保持一致。

#### 链路 B：用户角色分配链路
`UserService.assignUserRoles`
-> `UserRoleRepository.deleteByUserId`
-> `UserRoleRepository.bulkCreate`

测试焦点：
- 事务一致性（删除旧关联与写入新关联必须原子化）。
- 空角色集输入时应只清理不重建。

#### 链路 C：角色菜单分配链路
`RoleService.assignRoleMenus`
-> 角色存在性校验
-> `RoleMenuRepository.deleteByRoleId`
-> `RoleMenuRepository.bulkInsert`

测试焦点：
- 角色不存在应直接失败且不触发后续写入。
- 重复调用结果保持一致（幂等视角）。

#### 链路 D：通知用户上下文链路
`NoticeMyBatisRepository.markAllAsRead/clearAllNotices`
-> `SilenceAuthCenterContextHolder.getAuthenticatedUserName`
-> DAO 批量更新/删除

测试焦点：
- 仅作用于当前用户数据。
- username 空白必须失败，防止全量误处理。

---

## 7) 分层测试策略与 Mock 边界（可执行）

### 7.1 Service 单元测试（优先）

- 工具：JUnit 5 + Mockito
- 原则：只测业务逻辑，不连真实 DB
- 断言：
  - 错误码（`AuthCenterMessages`）
  - 方法调用次数与顺序
  - 异常时后续 DAO 不应被调用

### 7.2 Repository 集成测试（第二层）

- 工具：`@SpringBootTest` + H2（或优先 Testcontainers MySQL）
- 覆盖：逻辑删除、查询过滤、批量更新、JSON 字段映射

### 7.3 API 接口测试（第三层）

- 工具：`@WebMvcTest` + MockMvc
- 覆盖：权限注解、参数绑定、HTTP 状态码、序列化契约

### 7.4 异步/锁/外部依赖策略（扩展规范）

当前代码未发现线程池异步与 Redisson 锁；统一预置规范如下：
- 异步：用 `Awaitility` / `CountDownLatch` 验证最终状态，禁用 `Thread.sleep`
- 分布式锁：单测 Mock `RLock`，并发集成测互斥与超时
- 外部 API：Adapter 层用 WireMock 打桩，业务层只测超时/重试/降级规则

---

## 8) 测试红线与异常断言规范（必须遵守）

1. 严禁只测 Happy Path，所有 P0 规则必须有失败分支。
2. 严禁只断言“抛了异常”，必须断言错误码（51~59）与错误语义。
3. 严禁忽略事务回滚测试：任何“先删后增”链路都要验证原子性。
4. 严禁在测试中写死中文硬编码异常消息，统一按消息枚举断言。
5. 严禁忽略逻辑删除分支：`is_deleted` 双分支必须覆盖。

---

## 9) 测试栈与模板建议（Testing Stack）

### 9.1 已具备测试栈

- JUnit（来自 `spring-boot-starter-test`）
- Mockito（starter 内置）
- SpringBootTest / MockMvc（starter 内置）
- H2（已声明 test scope）

### 9.2 当前“最佳模板类”说明

- 现状：仓库暂无可复用测试类，无法客观选择“现有最佳单测类”。
- 建议模板起点：`UserService`（分支最多、事务与关联写入最复杂）
  - 先沉淀 `UserServiceTest` 作为团队模板
  - 再平移到 `RoleServiceTest`、`MenuServiceTest`、`AuthServiceTest`

---

## 10) 落地计划（两周）

- W1：交付 P0 自动化（AUTH-001~003, USER-001~005, MENU-003, ROLE-002）
- W2：补齐通知与跨域链路回归（NOTICE-001~004 + 链路 A/B/C）
- 门禁：P0 规则覆盖率 100%，Service 分支覆盖率目标 ≥ 85%

---

## 附录 A：最小测试骨架建议

```
silence-auth-center-console/
  src/test/java/com/old/silence/auth/center/
    domain/service/
      UserServiceTest.java
      RoleServiceTest.java
      MenuServiceTest.java
      AuthServiceTest.java
    api/
      UserResourceTest.java
      RoleResourceTest.java
      NoticeResourceTest.java
    infrastructure/persistence/
      NoticeMyBatisRepositoryIT.java
```

---

## 附录 B：团队执行口令（TL;DR）

- 先按 **规则模型** 设计用例，再按 **风险点模型** 排执行优先级。
- 一个需求至少落一条“状态转移验证”。
- 一个跨域链路至少有一条“事务一致性验证”。
- 每条异常分支必须有“错误码断言 + 数据副作用断言”。
