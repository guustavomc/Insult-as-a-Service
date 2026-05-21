import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  duration: '10s',
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const res = http.post(
    `${BASE_URL}/api/iaas/insult`,
    JSON.stringify({ name: 'John', characteristics: ['slow'] }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(res, {
    'status is 200':    (r) => r.status === 200,
    'insult returned':  (r) => JSON.parse(r.body).insult !== undefined,
  });
}