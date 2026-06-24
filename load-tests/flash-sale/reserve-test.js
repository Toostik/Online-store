import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        flashsale: {
            executor: 'shared-iterations',
            vus: 10000,
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
            'Authorization': 'Bearer eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiIyOCIsImlzcyI6ImF1dGgtc2VydmljZSIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3ODIyOTQ2NjAsImV4cCI6MTc4MjI5NTUyNCwicm9sZXMiOlsiQURNSU4iXX0.A5_isrEVvw82gnqU94QYOOw-xB47fB5qCqKV817Lf7h3Qa5CpvYh8oZ0WSu6tJ_o4F1fPJONwsMves4nwqyvhA',
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