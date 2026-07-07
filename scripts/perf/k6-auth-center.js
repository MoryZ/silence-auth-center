import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://127.0.0.1:8096';
const USERNAME = __ENV.USERNAME || 'admin';
const PASSWORD = __ENV.PASSWORD || '123456';
const ENABLE_WRITE = (__ENV.ENABLE_WRITE || 'false').toLowerCase() === 'true';

const loginFailRate = new Rate('login_fail_rate');
const authFailRate = new Rate('auth_fail_rate');

export const options = {
  discardResponseBodies: true,
  scenarios: {
    public_endpoints: {
      executor: 'constant-vus',
      exec: 'publicScenario',
      vus: Number(__ENV.PUBLIC_VUS || 5),
      duration: __ENV.PUBLIC_DURATION || '1m',
    },
    auth_login: {
      executor: 'constant-vus',
      exec: 'loginScenario',
      vus: Number(__ENV.LOGIN_VUS || 10),
      duration: __ENV.LOGIN_DURATION || '2m',
    },
    auth_read_core: {
      executor: 'ramping-vus',
      exec: 'authReadScenario',
      startVUs: 0,
      stages: [
        { duration: __ENV.STAGE_1_DURATION || '30s', target: Number(__ENV.STAGE_1_VUS || 10) },
        { duration: __ENV.STAGE_2_DURATION || '1m', target: Number(__ENV.STAGE_2_VUS || 30) },
        { duration: __ENV.STAGE_3_DURATION || '30s', target: 0 },
      ],
      gracefulRampDown: '10s',
    },
    auth_write_optional: {
      executor: 'constant-vus',
      exec: 'authWriteScenario',
      vus: Number(__ENV.WRITE_VUS || 1),
      duration: __ENV.WRITE_DURATION || '1m',
      startTime: '10s',
      gracefulStop: '5s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<800', 'p(99)<1500'],
    login_fail_rate: ['rate<0.05'],
    auth_fail_rate: ['rate<0.05'],
  },
};

function jsonHeaders(token) {
  const headers = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

function login() {
  const payload = JSON.stringify({
    username: USERNAME,
    password: PASSWORD,
  });

  const res = http.post(`${BASE_URL}/api/v1/auth/login`, payload, {
    headers: jsonHeaders(),
    tags: { name: 'POST /api/v1/auth/login' },
  });

  const ok = check(res, {
    'login status is 200': (r) => r.status === 200,
    'login has token': (r) => {
      if (!r.body) return false;
      try {
        const body = JSON.parse(r.body);
        return !!body.token;
      } catch (e) {
        return false;
      }
    },
  });

  loginFailRate.add(!ok);

  if (!ok) {
    return null;
  }

  const body = JSON.parse(res.body);
  return body.token;
}

function authGet(path, token, tagName) {
  const res = http.get(`${BASE_URL}${path}`, {
    headers: jsonHeaders(token),
    tags: { name: tagName },
  });
  const ok = check(res, {
    [`${tagName} status < 500`]: (r) => r.status < 500,
    [`${tagName} is not auth-failed`]: (r) => r.status !== 401 && r.status !== 403,
  });
  authFailRate.add(!ok);
  return res;
}

function authPost(path, token, payload, tagName) {
  const res = http.post(`${BASE_URL}${path}`, JSON.stringify(payload), {
    headers: jsonHeaders(token),
    tags: { name: tagName },
  });
  const ok = check(res, {
    [`${tagName} status < 500`]: (r) => r.status < 500,
    [`${tagName} is not auth-failed`]: (r) => r.status !== 401 && r.status !== 403,
  });
  authFailRate.add(!ok);
  return res;
}

export function publicScenario() {
  const health = http.get(`${BASE_URL}/api/v1/health`, {
    tags: { name: 'GET /api/v1/health' },
  });
  check(health, {
    'health status is 200': (r) => r.status === 200,
  });

  const captcha = http.get(`${BASE_URL}/api/v1/captcha/image`, {
    tags: { name: 'GET /api/v1/captcha/image' },
  });
  check(captcha, {
    'captcha status is 200': (r) => r.status === 200,
  });
  sleep(1);
}

export function loginScenario() {
  const token = login();
  check({ token }, { 'token not null': (t) => !!t.token });
  sleep(1);
}

export function authReadScenario() {
  const token = login();
  if (!token) {
    sleep(1);
    return;
  }

  authGet('/api/v1/users?pageNo=1&pageSize=20', token, 'GET /api/v1/users');
  authGet('/api/v1/roles?pageNo=1&pageSize=20', token, 'GET /api/v1/roles');
  authGet('/api/v1/menus/tree', token, 'GET /api/v1/menus/tree');
  authGet('/api/v1/menus/list', token, 'GET /api/v1/menus/list');
  authGet('/api/v1/notices?pageNo=1&pageSize=20', token, 'GET /api/v1/notices');

  sleep(1);
}

export function authWriteScenario() {
  if (!ENABLE_WRITE) {
    sleep(1);
    return;
  }

  const token = login();
  if (!token) {
    sleep(1);
    return;
  }

  const now = Date.now();
  const uname = `k6_u_${__VU}_${now}`;

  authPost('/api/v1/users/register', token, {
    username: uname,
    password: 'Aa12345678',
    nickname: 'k6-user',
    status: true,
  }, 'POST /api/v1/users/register');

  authPost('/api/v1/notices', token, {
    title: `k6-title-${now}`,
    content: 'k6 write test',
    status: 'UNREAD',
  }, 'POST /api/v1/notices');

  sleep(1);
}
