import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '30s', target: 10  },  // ramp up to 10 users
    { duration: '1m',  target: 50  },  // ramp up to 50 users
    { duration: '2m',  target: 100 },  // hold at 100 users
    { duration: '30s', target: 0   },  // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests under 500ms
    errors: ['rate<0.01'],             // less than 1% error rate
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const NAMES = ['John', 'Jane', 'Bob', 'Alice', 'Charlie'];
const TRAITS = ['slow', 'arrogant', 'clumsy', 'loud', 'boring'];

function randomItem(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

export default function () {
  const payload = JSON.stringify({
    name: randomItem(NAMES),
    characteristics: [randomItem(TRAITS), randomItem(TRAITS)],
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  const res = http.post(`${BASE_URL}/api/iaas/insult`, payload, params);

  const success = check(res, {
    'status is 200':          (r) => r.status === 200,
    'has insult field':       (r) => JSON.parse(r.body).insult !== undefined,
    'response time < 500ms':  (r) => r.timings.duration < 500,
  });

  errorRate.add(!success);
  sleep(0.5);
}
