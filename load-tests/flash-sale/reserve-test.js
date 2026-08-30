import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        flashsale: {
            executor: 'shared-iterations',
            vus: 1000,
            iterations: 10000,
            maxDuration: '1m'
        }
    }
};

export default function () {

    const body = JSON.stringify({
        quantity: 1
    });

    const params = {
        headers: {
            'Authorization': 'Bearer eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiIyOCIsImlzcyI6ImF1dGgtc2VydmljZSIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3ODIzMDkwNDksImV4cCI6MTc4MjMwOTkxMywicm9sZXMiOlsiQURNSU4iXX0.R2-MqjlUGvswSxk0fjMVmawst97wujUvif4zh5KJ14Jg7hpz30Ja_EWPZDiwTPaaXaqBF2VRI3p17IjcAl763Q',
            'Content-Type': 'application/json'
        }
    };

    const response = http.post(
        'http://localhost:8080/api/flash-sales/2/reserve',
        body,
        params
    );

    check(response, {
        'status is 200 or 409': r =>
            r.status === 200 || r.status === 409
    });
}