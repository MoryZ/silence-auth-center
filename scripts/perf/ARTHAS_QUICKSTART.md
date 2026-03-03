# Auth Center 压测 + Arthas 快速上手

## 1. 先跑压测脚本（k6）

> 脚本文件：`scripts/perf/k6-auth-center.js`

### 安装 k6（macOS）
```bash
brew install k6
```

### 基础压测（只读 + 登录）
```bash
cd /Users/moryzang/IdeaProjects/silence-auth-center
BASE_URL=http://127.0.0.1:8096 USERNAME=admin PASSWORD='你的密码' k6 run scripts/perf/k6-auth-center.js
```

### 开启写压测（会写入测试数据）
```bash
BASE_URL=http://127.0.0.1:8096 USERNAME=admin PASSWORD='你的密码' ENABLE_WRITE=true k6 run scripts/perf/k6-auth-center.js
```

### 常用并发调参
```bash
BASE_URL=http://127.0.0.1:8096 USERNAME=admin PASSWORD='你的密码' \
LOGIN_VUS=20 STAGE_1_VUS=20 STAGE_2_VUS=80 \
k6 run scripts/perf/k6-auth-center.js
```

## 2. 建议优先压测接口（按价值排序）

1. `POST /api/v1/auth/login`（认证瓶颈、密码校验、权限组装）
2. `GET /api/v1/users?pageNo=1&pageSize=20`（用户分页）
3. `GET /api/v1/roles?pageNo=1&pageSize=20`（角色分页）
4. `GET /api/v1/menus/tree`（树形构建）
5. `GET /api/v1/notices?pageNo=1&pageSize=20`（通知分页）
6. `GET /api/v1/captcha/image`（CPU + Redis + 图像编码）

## 3. Arthas 启动

### 下载并启动
```bash
curl -O https://arthas.aliyun.com/arthas-boot.jar
java -jar arthas-boot.jar
```

选择你的 Spring Boot 进程后，先执行：
```bash
dashboard
thread -n 5
```

## 4. Arthas 必会命令（定位性能 gap）

### 4.1 看谁最慢：`trace`
```bash
trace com.old.silence.auth.center.domain.service.AuthService login '#cost > 50' -n 5
trace com.old.silence.auth.center.domain.service.MenuService getMenuTree '#cost > 30' -n 5
trace com.old.silence.auth.center.domain.service.UserService query '#cost > 30' -n 5
```
- 用法：看一个方法调用链内每一层耗时，快速找慢点。
- 建议：压测在跑时执行，`#cost` 阈值按你机器调整。

### 4.2 看热点方法：`monitor`
```bash
monitor -c 5 com.old.silence.auth.center.domain.service.AuthService login
monitor -c 5 com.old.silence.auth.center.api.CaptchaResource getCode
```
- 每 5 秒输出 QPS/RT/失败数。

### 4.3 看参数和返回：`watch`
```bash
watch com.old.silence.auth.center.domain.service.AuthService login '{params,returnObj}' -x 2 -n 3
watch com.old.silence.auth.center.domain.service.UserService create '{params,throwExp}' -e -x 2 -n 3
```
- `-e` 只在异常时触发，适合排查失败请求。

### 4.4 看是否阻塞：`thread`
```bash
thread -n 10
thread <id>
```
- 看最忙线程栈，识别锁竞争/慢 SQL 等阻塞。

### 4.5 采样火焰图：`profiler`
```bash
profiler start
# 保持压测 30~60 秒
profiler stop --format html
```
- 生成 HTML 火焰图，定位 CPU 热点最直观。

## 5. 你这套场景的推荐排查顺序

1. 先 `monitor AuthService login` 看登录 RT/QPS 走势。
2. RT 抖动时立刻 `trace AuthService login '#cost > 50'`。
3. 若 CPU 高：`profiler` 采样。
4. 若 RT 高但 CPU 不高：`thread -n 10` 看阻塞线程。
5. 对树形菜单慢链路补 `trace MenuService getMenuTree`。

## 6. 小贴士

- 你的接口大量走权限校验，压测账号建议使用完整权限管理员，避免 403 干扰结果。
- `ENABLE_WRITE=true` 会产生测试数据，建议只在测试库开启。
- 脚本默认把 401/403 计入 `auth_fail_rate`，方便快速发现 token/权限配置问题。
